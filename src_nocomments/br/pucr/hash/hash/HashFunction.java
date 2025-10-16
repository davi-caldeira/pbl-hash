package br.pucr.hash;

public interface HashFunction {
    int hash(int key, int m);
    String name();
}
