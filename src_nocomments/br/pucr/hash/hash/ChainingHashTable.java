package br.pucr.hash;

public final class ChainingHashTable implements HashTable {
    private static final class Node {
        int codigo;
        Node next;
        Node(int c) { this.codigo = c; }
    }

    private final HashFunction hash;
    private Node[] buckets;
    private int n; 

    public ChainingHashTable(int m, HashFunction hash) {
        this.hash = hash;
        this.buckets = new Node[m];
        this.n = 0;
    }

    @Override
    public void clear() {
        for (int i = 0; i < buckets.length; i++) buckets[i] = null;
        n = 0;
    }

    @Override
    public boolean insert(Registro r, Stats stats) {
        int h = hash.hash(r.getCodigo(), buckets.length);
        Node head = buckets[h];
        if (head == null) {
            buckets[h] = new Node(r.getCodigo());
            n++;
            return true;
        }
        
        Node cur = head;
        int traversed = 0;
        while (true) {
            if (cur.codigo == r.getCodigo()) {
                stats.collisionsInsert += traversed; 
                return false; 
            }
            if (cur.next == null) break;
            cur = cur.next;
            traversed++;
        }
        cur.next = new Node(r.getCodigo());
        stats.collisionsInsert += traversed + 1; 
        n++;
        return true;
    }

    @Override
    public boolean contains(int codigo, Stats stats) {
        int h = hash.hash(codigo, buckets.length);
        Node cur = buckets[h];
        int traversed = 0;
        while (cur != null) {
            if (cur.codigo == codigo) {
                stats.collisionsSearch += traversed;
                return true;
            }
            cur = cur.next;
            traversed++;
        }
        stats.collisionsSearch += traversed;
        return false;
    }

    @Override public int size() { return n; }
    @Override public int capacity() { return buckets.length; }

    @Override
    public int[] chainLengthsSnapshot() {
        int[] lens = new int[buckets.length];
        for (int i = 0; i < buckets.length; i++) {
            int len = 0;
            Node cur = buckets[i];
            while (cur != null) { len++; cur = cur.next; }
            lens[i] = len;
        }
        return lens;
    }

    @Override
    public boolean[] bucketOccupancySnapshot() {
        boolean[] occ = new boolean[buckets.length];
        for (int i = 0; i < buckets.length; i++) occ[i] = (buckets[i] != null);
        return occ;
    }

    @Override public String name() { return "ENCAD_SEPARADO(" + hash.name() + ")"; }
}
