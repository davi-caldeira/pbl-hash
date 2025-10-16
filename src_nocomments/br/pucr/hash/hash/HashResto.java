package br.pucr.hash;

public final class HashResto implements HashFunction {
    @Override
    public int hash(int key, int m) {
        int x = key ^ (key >>> 16);
        if (x < 0) x = -x;
        return x % m;
    }
    @Override public String name() { return "RESTO"; }
}
