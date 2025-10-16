package br.pucr.hash;

public final class ProbeDoubleHash implements ProbeStrategy {
    @Override
    public int probe(int h1, int h2, int i, int m) {
        long pos = (h1 + (long)i * h2) % m;
        if (pos < 0) pos += m;
        return (int) pos;
    }
    @Override public String name() { return "DUPLO_HASH"; }
}
