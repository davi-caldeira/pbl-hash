package br.pucr.hash;

public final class OpenAddressingHashTable implements HashTable {
    // Marcadores de estado
    private static final int EMPTY = Integer.MIN_VALUE;   // posição nunca usada
    private static final int DELETED = Integer.MAX_VALUE; // posição que já foi ocupada (não usamos remoção aqui, mas deixei pronto)

    private final HashFunction baseHash; // h1
    private final ProbeStrategy probe;
    private int[] table; // armazena códigos
    private int n;       // itens válidos
    private int used;    // posições usadas (inclui DELETED)

    // Parâmetros de rehash/ocupação
    private final double maxLoadFactor; // ex.: 0.80

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
                // reinserir sem contar como colisões do experimento principal
                insertInternal(v, null);
            }
        }
        if (stats != null) stats.rehashCount++;
    }

    private static int nextCapacity(int current) {
        long candidate = (long)current * 2L + 1L; // dobrar +1
        if (candidate > Integer.MAX_VALUE) candidate = Integer.MAX_VALUE;
        // (poderia escolher próximo primo; mantemos simples aqui)
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
                if (stats != null) stats.collisionsInsert++; // DELETED também conta como colisão
            } else if (slot == codigo) {
                return false; // duplicata
            } else {
                if (stats != null) stats.collisionsInsert++;
            }
        }
        return false; // tabela saturada (deveria ter crescido antes)
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
                // não encontrado
                return false;
            } else if (slot == DELETED) {
                if (stats != null) stats.collisionsSearch++;
            } else if (slot == codigo) {
                // encontrado
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
