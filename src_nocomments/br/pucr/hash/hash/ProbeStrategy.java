package br.pucr.hash;

public interface ProbeStrategy {
    
    int probe(int h1, int h2, int i, int m);
    String name();
}
