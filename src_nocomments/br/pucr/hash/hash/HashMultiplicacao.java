package br.pucr.hash;

public final class HashMultiplicacao implements HashFunction {
    private static final double A = 0.6180339887498949; 
    @Override
    public int hash(int key, int m) {
        long k = key & 0x00000000FFFFFFFFL;
        double prod = k * A;
        double frac = prod - Math.floor(prod);
        int h = (int) Math.floor(m * frac);
        if (h < 0) h = -h;
        return h % m;
    }
    @Override public String name() { return "MULTIPLICACAO"; }
}
