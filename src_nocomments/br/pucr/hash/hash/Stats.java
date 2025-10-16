package br.pucr.hash;

public final class Stats {
    public long collisionsInsert = 0L;
    public long collisionsSearch = 0L;
    public int rehashCount = 0;

    public void reset() {
        collisionsInsert = 0;
        collisionsSearch = 0;
        rehashCount = 0;
    }
}
