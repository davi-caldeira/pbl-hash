package br.pucr.hash;

public final class OpenAddressingHashTable implements HashTable {
    
    private static final int EMPTY = Integer.MIN_VALUE;   
    private static final int DELETED = Integer.MAX_VALUE; 

    private final HashFunction baseHash; 
    private final ProbeStrategy probe;
    private int[] table; 
    private int n;       
    private int used;    

    
    private final double maxLoadFactor; 

    public OpenAddressingHashTable(int m, HashFunction baseHash, ProbeStrategy probe, double maxLoad) {
        this.baseHash = baseHash;
        this.probe = probe;
        this.table = new int[m];
        this.n = 0; this.used = 0;
        this.maxLoadFactor = maxLoad;
        for (int i = 0; i < m; i++) table[i] = EMPTY;
    }

    @Override
    public void clear() {
        for (int i = 0; i < table.length; i++) table[i] = EMPTY;
        n = 0; used = 0;
    }

    private int secondHash(int key, int m) {
        int h2 = 1 + (key % (m - 1));
        if (h2 < 0) h2 = -h2;
        if (h2 == 0) h2 = 1;
        return h2;
    }

    private void rehashGrow(Stats stats) {
        int newCap = nextCapacity(table.length);
        int[] old = table;
        table = new int[newCap];
        for (int i = 0; i < newCap; i++) table[i] = EMPTY;
        n = 0; used = 0;

        for (int v : old) {
            if (v != EMPTY && v != DELETED) {
                
                insertInternal(v, null);
            }
        }
        if (stats != null) stats.rehashCount++;
    }

    private static int nextCapacity(int current) {
        long candidate = (long)current * 2L + 1L; 
        if (candidate > Integer.MAX_VALUE) candidate = Integer.MAX_VALUE;
        
        return (int)candidate;
    }

    private boolean insertInternal(int codigo, Stats stats) {
        final int m = table.length;
        final int h1 = baseHash.hash(codigo, m);
        final int h2 = (probe instanceof ProbeDoubleHash) ? secondHash(codigo, m) : 1;

        int firstDeleted = -1;
        for (int i = 0; i < m; i++) {
            int pos = probe.probe(h1, h2, i, m);
            int slot = table[pos];

            if (slot == EMPTY) {
                int target = (firstDeleted >= 0) ? firstDeleted : pos;
                table[target] = codigo;
                n++;
                used += (firstDeleted >= 0) ? 0 : 1;
                return true;
            } else if (slot == DELETED) {
                if (firstDeleted < 0) firstDeleted = pos;
                if (stats != null) stats.collisionsInsert++; 
            } else if (slot == codigo) {
                return false; 
            } else {
                if (stats != null) stats.collisionsInsert++;
            }
        }
        return false; 
    }

    @Override
    public boolean insert(Registro r, Stats stats) {
        if (used >= (int)(table.length * maxLoadFactor)) {
            rehashGrow(stats);
        }
        return insertInternal(r.getCodigo(), stats);
    }

    @Override
    public boolean contains(int codigo, Stats stats) {
        final int m = table.length;
        final int h1 = baseHash.hash(codigo, m);
        final int h2 = (probe instanceof ProbeDoubleHash) ? secondHash(codigo, m) : 1;

        for (int i = 0; i < m; i++) {
            int pos = probe.probe(h1, h2, i, m);
            int slot = table[pos];
            if (slot == EMPTY) {
                
                return false;
            } else if (slot == DELETED) {
                if (stats != null) stats.collisionsSearch++;
            } else if (slot == codigo) {
                
                return true;
            } else {
                if (stats != null) stats.collisionsSearch++;
            }
        }
        return false;
    }

    @Override public int size() { return n; }
    @Override public int capacity() { return table.length; }

    @Override
    public int[] chainLengthsSnapshot() { return null; }

    @Override
    public boolean[] bucketOccupancySnapshot() {
        boolean[] occ = new boolean[table.length];
        for (int i = 0; i < table.length; i++) occ[i] = (table[i] != EMPTY && table[i] != DELETED);
        return occ;
    }

    @Override public String name() { return "OA(" + baseHash.name() + "," + probe.name() + ")"; }
}
