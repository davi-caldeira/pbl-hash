package br.pucr.hash;

public interface HashTable {
    void clear();
    boolean insert(Registro r, Stats stats);
    boolean contains(int codigo, Stats stats);
    int size();
    int capacity();

    
    int[] chainLengthsSnapshot(); 

    
    boolean[] bucketOccupancySnapshot();
    String name();
}
