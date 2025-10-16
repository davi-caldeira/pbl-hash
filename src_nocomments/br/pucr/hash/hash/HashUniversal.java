package br.pucr.hash;

import java.util.Random;


public final class HashUniversal implements HashFunction {
    private static final int P = 1_000_000_007; 
    private final int a;
    private final int b;
    private final long seed;

    public HashUniversal(long seed) {
        this.seed = seed;
        Random rnd = new Random(seed);
        int aa = rnd.nextInt(P - 1) + 1;   
        int bb = rnd.nextInt(P);           
        this.a = aa;
        this.b = bb;
    }

    @Override
    public int hash(int key, int m) {
        long v = ((long)a * (key & 0xFFFFFFFFL) + b) % P;
        int h = (int)(v % m);
        if (h < 0) h = -h;
        return h;
    }

    @Override
    public String name() { return "UNIVERSAL(seed=" + seed + ")"; }
}
