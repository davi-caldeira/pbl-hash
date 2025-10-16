# 🚀 Análise de Desempenho de Tabelas Hash em Java

Este repositório apresenta um estudo prático e a implementação de diferentes estratégias de **Tabelas Hash (Hash Tables)** em Java. O objetivo principal é analisar e comparar o desempenho de duas abordagens fundamentais para o tratamento de colisões: **Encadeamento Separado** e **Endereçamento Aberto** (com Sondagem Linear e Hash Duplo).

O projeto executa testes de inserção e busca em grandes volumes de dados para responder a uma pergunta central: *Qual estratégia oferece o melhor desempenho e quais são os trade-offs envolvidos?*

-----

## 🧪 Metodologia

Para garantir uma análise robusta e reprodutível, a seguinte metodologia foi adotada:

### 📦 Conjuntos de Dados

Foram gerados três conjuntos de dados com diferentes volumes, utilizando *seeds* fixas para consistência entre os testes:

  * **100.000** registros
  * **1.000.000** de registros
  * **10.000.000** de registros

### ⚙️ Estratégias de Resolução de Colisão

Foram implementadas e comparadas as seguintes técnicas:

  * **Encadeamento Separado:** Cada posição da tabela aponta para uma lista ligada que armazena todos os elementos com o mesmo índice de hash.
  * **Endereçamento Aberto (Sondagem Linear):** Em caso de colisão, o algoritmo sonda a tabela sequencialmente (`indice + 1`, `indice + 2`, ...) até encontrar um espaço livre.
  * **Endereçamento Aberto (Hash Duplo):** Utiliza uma segunda função de hash para calcular um "passo" de sondagem, mitigando o problema de agrupamento primário (*primary clustering*).

### 🧮 Funções de Hash Testadas

Para cada estratégia de colisão, foram avaliadas três funções de hash distintas para mapear as chaves aos índices da tabela:

1.  **Método da Divisão (Resto):** A função de hash mais simples e rápida.
2.  **Método da Multiplicação:** Uma abordagem que tende a espalhar melhor as chaves.
3.  **Hashing Universal:** Uma técnica randomizada que garante um bom desempenho médio, independentemente do conjunto de dados.

-----

## 📊 Resultados & Desempenho

Os resultados a seguir representam a **média dos testes executados** com as três *seeds* diferentes. Para manter a clareza, as tabelas utilizam a função de hash de **RESTO** como base comparativa.

#### Tabela 1 - Desempenho de **Inserção** (Tempo em `ms`)

| Estratégia | Tamanho Tabela (M) | Conjunto de Dados (N) | Tempo Médio Inserção (ms) | Nº Médio de Colisões |
| :--- | :--- | :--- | :--- | :--- |
| **Encadeamento** | 1.000.000 | 1.000.000 | **67,59** | 499.849 |
| **Encadeamento** | 10.000.000 | 10.000.000 | **562,20** | 4.949.127 |
| | | | | |
| **Rehash Linear** | 2.000.001 | 1.000.000 | **30,14** | 936.991 |
| **Rehash Linear** | 20.000.001 | 10.000.000 | **475,21** | 9.307.622 |
| | | | | |
| **Rehash Duplo** | 2.000.001 | 1.000.000 | **39,43** | 591.221 |
| **Rehash Duplo** | 20.000.001 | 10.000.000 | **846,06** | 19.771.537 |

#### Tabela 2 - Desempenho de **Busca** (Tempo em `ms`)

| Estratégia | Tamanho Tabela (M) | Conjunto de Dados (N) | Tempo Médio Busca (ms) |
| :--- | :--- | :--- | :--- |
| **Encadeamento** | 1.000.000 | 1.000.000 | **31,48** |
| **Encadeamento** | 10.000.000 | 10.000.000 | **457,65** |
| | | | |
| **Rehash Linear** | 2.000.001 | 1.000.000 | **15,33** |
| **Rehash Linear** | 20.000.001 | 10.000.000 | **325,65** |
| | | | |
| **Rehash Duplo** | 2.000.001 | 1.000.000 | **26,89** |
| **Rehash Duplo** | 20.000.001 | 10.000.000 | **582,68** |

-----

## 📈 Análise e Discussão dos Resultados

A análise dos dados empíricos revelou um claro *trade-off* entre velocidade de execução e qualidade da distribuição das chaves.

  * **🔗 Encadeamento Separado:** Mostrou-se uma técnica **previsível e robusta**. Seu desempenho escala de forma linear com o fator de carga (`α = N/M`). É uma excelente escolha quando o número de elementos pode exceder o tamanho da tabela.

  * **⚡ Rehashing Linear: O Campeão de Velocidade:** Contrariando a expectativa teórica, a sondagem linear foi **consistentemente mais rápida que o Hash Duplo**, tanto na inserção quanto na busca. A simplicidade do cálculo do próximo índice (`+1`) supera a sobrecarga de calcular um segundo hash, além de potencialmente se beneficiar do cache da CPU.

  * **🎯 Hash Duplo: O Mestre da Distribuição:** Esta técnica cumpriu sua promessa de espalhar melhor os dados, **reduzindo significativamente o número de colisões** em comparação com a sondagem linear e evitando o efeito de *clustering*. No entanto, essa melhor distribuição teve um custo em tempo de processamento.

> O *trade-off* é claro: **Rehashing Linear** para máxima velocidade e **Hash Duplo** para máxima qualidade de distribuição e prevenção de piores casos.

-----

## 💡 Conclusão

Este estudo prático demonstrou que a escolha da estratégia de hashing ideal depende dos requisitos específicos da aplicação.

1.  **Rehashing Linear** provou ser a estratégia **mais rápida em tempo de execução** para as cargas de trabalho testadas, sendo a escolha ideal para cenários onde a performance bruta é o fator mais crítico.

2.  **Rehashing Duplo** é a melhor opção para garantir uma **distribuição uniforme das chaves**, sendo ideal para aplicações que precisam evitar os piores casos de desempenho causados pelo agrupamento.

3.  **Encadeamento Separado** permanece uma alternativa **sólida e flexível**, com desempenho previsível e a capacidade única de suportar fatores de carga maiores que 1.
