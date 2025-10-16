package br.pucr.hash;

import java.io.IOException;

public final class ExperimentRunner {
    public static void main(String[] args) throws Exception {
        final int[] MS = {100_000, 1_000_000, 10_000_000};  // tamanhos base
        final int[] NS = {100_000, 1_000_000, 10_000_000};  // datasets
        final long[] SEEDS = {42L, 4242L, 424242L};         // 3 datasets diferentes

        // funções hash
        HashFunction[] hashes = new HashFunction[] {
                new HashResto(),
                new HashMultiplicacao(),
                new HashUniversal(123456789L) // seed fixa para universal
        };

        // estratégias
        ProbeStrategy linear = new ProbeLinear();
        ProbeStrategy dHash  = new ProbeDoubleHash();

        try (CsvWriter exp = new CsvWriter("out/experimentos.csv",
                "cenario;tabela;hash;probe;M;N;seed;tempoIns(ns);tempoBus(ns);colisIns;colisBus;rehashCount;top1;top2;top3;gapMin;gapMax;gapMean;gapCount")) {

            for (int M0 : MS) {
                // Tabelas de encadeamento para cada função hash
                for (HashFunction hf : hashes) {
                    // 1) Encadeamento
                    runScenarioChaining(exp, M0, NS, SEEDS, hf);
                }
                // 2) OA + Linear (para cada função hash)
                for (HashFunction hf : hashes) {
                    runScenarioOA(exp, M0, NS, SEEDS, hf, linear);
                }
                // 3) OA + Duplo-Hash (para cada função hash)
                for (HashFunction hf : hashes) {
                    runScenarioOA(exp, M0, NS, SEEDS, hf, dHash);
                }
            }
        }

        // BÔNUS: exportar versão sem comentários
        NoCommentsExporter.export("src/br/pucr/hash", "src_nocomments/br/pucr/hash");
    }

    private static void runScenarioChaining(CsvWriter exp, int M0, int[] NS, long[] SEEDS, HashFunction hf) throws IOException {
        ChainingHashTable ht = new ChainingHashTable(M0, hf);
        for (int N : NS) {
            for (long sd : SEEDS) {
                Stats st = new Stats();
                int[] dados = DataGenerator.gerarCodigos(sd, N);
                Registro[] items = DataGenerator.envolver(dados);

                long memBefore = MemoryEstimator.measureHeapUsedBytes();
                long t0 = System.nanoTime();
                for (Registro r : items) ht.insert(r, st);
                long t1 = System.nanoTime();

                long t2 = System.nanoTime();
                for (int c : dados) ht.contains(c, st);
                long t3 = System.nanoTime();

                boolean[] occ = ht.bucketOccupancySnapshot();
                GapUtils.GapStats gaps = GapUtils.compute(occ);
                int[] lens = ht.chainLengthsSnapshot();
                int[] top3 = GapUtils.top3(lens);

                long memAfter = MemoryEstimator.measureHeapUsedBytes();
                long memDelta = Math.max(0L, memAfter - memBefore);
                // (opcional) escrever memória por cenário
                try (CsvWriter memcsv = new CsvWriter("out/memoria.csv",
                    "cenario;tabela;hash;probe;M;N;seed;heapDelta(bytes);estimado(bytes)")) {
                    long est = MemoryEstimator.estimateChainingBytes(M0, ht.size());
                    memcsv.writeLine("INSERCAO;ENCAD;" + hf.name() + ";-;" + M0 + ";" + N + ";" + sd + ";" + memDelta + ";" + est);
                } catch (IOException ignore) {}

                exp.writeLine(String.join(";",
                    "INSER+BUSCA",
                    ht.name(), hf.name(), "-", String.valueOf(M0), String.valueOf(N), String.valueOf(sd),
                    String.valueOf(t1 - t0), String.valueOf(t3 - t2),
                    String.valueOf(st.collisionsInsert), String.valueOf(st.collisionsSearch),
                    String.valueOf(st.rehashCount),
                    String.valueOf(top3[0]), String.valueOf(top3[1]), String.valueOf(top3[2]),
                    String.valueOf(gaps.minGap), String.valueOf(gaps.maxGap),
                    String.format(java.util.Locale.US,"%.4f", gaps.meanGap),
                    String.valueOf(gaps.countGaps)
                ));
                ht.clear();
            }
        }
    }

    private static void runScenarioOA(CsvWriter exp, int M0, int[] NS, long[] SEEDS, HashFunction hf, ProbeStrategy probe) throws IOException {
        // OA com fator de carga máximo 0.80 e rehash de crescimento automático
        OpenAddressingHashTable ht = new OpenAddressingHashTable(M0, hf, probe, 0.80);

        for (int N : NS) {
            for (long sd : SEEDS) {
                Stats st = new Stats();
                int[] dados = DataGenerator.gerarCodigos(sd, N);
                Registro[] items = DataGenerator.envolver(dados);

                long memBefore = MemoryEstimator.measureHeapUsedBytes();
                long t0 = System.nanoTime();
                for (Registro r : items) ht.insert(r, st);
                long t1 = System.nanoTime();

                long t2 = System.nanoTime();
                for (int c : dados) ht.contains(c, st);
                long t3 = System.nanoTime();

                boolean[] occ = ht.bucketOccupancySnapshot();
                GapUtils.GapStats gaps = GapUtils.compute(occ);

                long memAfter = MemoryEstimator.measureHeapUsedBytes();
                long memDelta = Math.max(0L, memAfter - memBefore);
                try (CsvWriter memcsv = new CsvWriter("out/memoria.csv",
                        null)) {
                    long est = MemoryEstimator.estimateOpenAddressingBytes(ht.capacity());
                    memcsv.writeLine("INSERCAO;" + ht.name() + ";" + hf.name() + ";" + probe.name() + ";" + ht.capacity() + ";" + N + ";" + sd + ";" + memDelta + ";" + est);
                } catch (IOException ignore) {}

                exp.writeLine(String.join(";",
                    "INSER+BUSCA",
                    ht.name(), hf.name(), probe.name(), String.valueOf(ht.capacity()), String.valueOf(N), String.valueOf(sd),
                    String.valueOf(t1 - t0), String.valueOf(t3 - t2),
                    String.valueOf(st.collisionsInsert), String.valueOf(st.collisionsSearch),
                    String.valueOf(st.rehashCount),
                    "0","0","0", // top3 não se aplica
                    String.valueOf(gaps.minGap), String.valueOf(gaps.maxGap),
                    String.format(java.util.Locale.US,"%.4f", gaps.meanGap),
                    String.valueOf(gaps.countGaps)
                ));
                ht.clear();
            }
        }
    }
}
