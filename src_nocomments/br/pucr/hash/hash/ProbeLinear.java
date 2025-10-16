package br.pucr.hash;

public final class ProbeLinear implements ProbeStrategy {
    @Override
    public int probe(int h1, int h2, int i, int m) {
        int pos = h1 + i;
        if (pos >= m) pos %= m;
        return pos;
    }
    @Override public String name() { return "LINEAR"; }
}
