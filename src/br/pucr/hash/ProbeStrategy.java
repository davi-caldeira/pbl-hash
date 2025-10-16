package br.pucr.hash;

public interface ProbeStrategy {
    /**
     * @param h1 hash base
     * @param h2 segundo hash (no caso de duplo-hash; se não usado, pode vir 1)
     * @param i índice da tentativa (0,1,2,...)
     * @param m tamanho da tabela
     * @return posição candidata no vetor
     */
    int probe(int h1, int h2, int i, int m);
    String name();
}
