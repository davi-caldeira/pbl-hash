package br.pucr.hash;

public interface HashTable {
    void clear();
    boolean insert(Registro r, Stats stats);
    boolean contains(int codigo, Stats stats);
    int size();
    int capacity();

    // Apenas para encadeamento: devolve vetor com tamanhos das listas
    int[] chainLengthsSnapshot(); // pode retornar null em endereçamento aberto

    // Para métricas de gaps: retorna vetor booleano de ocupação de buckets
    boolean[] bucketOccupancySnapshot();
    String name();
}
