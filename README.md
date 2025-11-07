# Análise de Desempenho de Tabelas Hash em Java

## Introdução

Este trabalho apresenta a implementação e análise de diferentes estratégias de tabelas hash em Java.
O objetivo foi comparar o desempenho de três formas de tratar colisões: **Encadeamento Separado**, **Sondagem Linear** e **Hash Duplo** (ambas com endereçamento aberto).

A ideia foi medir, na prática, qual dessas abordagens é mais eficiente em tempo de inserção e busca, e entender os prós e contras de cada uma.

---

## Metodologia

Para garantir uma comparação justa entre os métodos, foram gerados três conjuntos de dados com tamanhos diferentes:

* 100.000 registros
* 1.000.000 registros
* 10.000.000 registros

Foram usadas **seeds fixas** para que os mesmos dados fossem aplicados em todos os testes, permitindo uma análise mais consistente.

---

## Estratégias de Colisão Testadas

* **Encadeamento Separado:**
  Cada posição da tabela aponta para uma lista ligada, que armazena todos os elementos que tiveram o mesmo índice hash.

* **Endereçamento Aberto (Sondagem Linear):**
  Quando ocorre colisão, o algoritmo procura a próxima posição livre de forma sequencial (índice + 1, índice + 2...).

* **Endereçamento Aberto (Hash Duplo):**
  Utiliza uma segunda função de hash para definir o passo da sondagem, o que ajuda a reduzir o problema de agrupamento de chaves.

---

## Funções de Hash Avaliadas

Foram testadas três funções de hash para mapear as chaves aos índices:

* **Método da Divisão (Resto):** rápido e simples, serve como base de comparação.
* **Método da Multiplicação:** tende a distribuir melhor as chaves na tabela.
* **Hashing Universal:** usa uma função randomizada para garantir bom desempenho médio, independente do conjunto de dados.

---

## Resultados

Os resultados abaixo mostram os tempos médios de inserção e busca com a função de hash baseada no método do **resto da divisão**, que foi usada como referência.

### Tabela 1 – Desempenho de Inserção

| Estratégia    | Tamanho Tabela (M) | Conjunto de Dados (N) | Tempo Médio Inserção (ms) | Nº Médio de Colisões |
| ------------- | ------------------ | --------------------- | ------------------------- | -------------------- |
| Encadeamento  | 1.000.000          | 1.000.000             | 67,59                     | 499.849              |
| Encadeamento  | 10.000.000         | 10.000.000            | 562,20                    | 4.949.127            |
| Rehash Linear | 2.000.001          | 1.000.000             | 30,14                     | 936.991              |
| Rehash Linear | 20.000.001         | 10.000.000            | 475,21                    | 9.307.622            |
| Rehash Duplo  | 2.000.001          | 1.000.000             | 39,43                     | 591.221              |
| Rehash Duplo  | 20.000.001         | 10.000.000            | 846,06                    | 19.771.537           |

### Tabela 2 – Desempenho de Busca

| Estratégia    | Tamanho Tabela (M) | Conjunto de Dados (N) | Tempo Médio Busca (ms) |
| ------------- | ------------------ | --------------------- | ---------------------- |
| Encadeamento  | 1.000.000          | 1.000.000             | 31,48                  |
| Encadeamento  | 10.000.000         | 10.000.000            | 457,65                 |
| Rehash Linear | 2.000.001          | 1.000.000             | 15,33                  |
| Rehash Linear | 20.000.001         | 10.000.000            | 325,65                 |
| Rehash Duplo  | 2.000.001          | 1.000.000             | 26,89                  |
| Rehash Duplo  | 20.000.001         | 10.000.000            | 582,68                 |

---

## Análise dos Resultados

Os testes mostraram que há um equilíbrio entre **velocidade** e **qualidade da distribuição** das chaves.

* **Encadeamento Separado:**
  Apresentou comportamento previsível e estável. O desempenho cresce de forma linear com o aumento do fator de carga.
  É uma boa escolha quando o número de elementos pode ser maior que o tamanho da tabela.

* **Sondagem Linear:**
  Foi o método mais rápido nos testes.
  Apesar de ser simples, o cálculo sequencial de índices (índice + 1) é eficiente e pode aproveitar melhor o cache da CPU.
  Mesmo com mais colisões, o tempo de execução médio foi menor.

* **Hash Duplo:**
  Distribui melhor os elementos na tabela, o que reduz o número de colisões e evita agrupamentos.
  No entanto, essa vantagem vem acompanhada de um custo maior em tempo de processamento, já que é necessário calcular uma segunda função hash.

O resultado final mostra que a **sondagem linear** é a mais rápida, enquanto o **hash duplo** oferece uma distribuição mais uniforme.
O encadeamento continua sendo uma alternativa estável e flexível, principalmente quando o fator de carga pode ultrapassar 1.

---

## Conclusão

A escolha da melhor estratégia de hashing depende do tipo de aplicação.

* Para cenários onde **tempo de execução** é o mais importante, o **Rehashing Linear** é a melhor opção.
* Se o objetivo é **evitar agrupamentos e colisões**, o **Rehashing Duplo** é mais indicado.
* Já o **Encadeamento Separado** é uma solução simples, previsível e eficiente, especialmente quando o número de elementos pode crescer além do tamanho inicial da tabela.
