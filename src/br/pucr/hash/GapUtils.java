package br.pucr.hash;

public final class GapUtils {
    public static final class GapStats {
        public int minGap;
        public int maxGap;
        public double meanGap;
        public int countGaps;

        public String toCsv() {
            return minGap + "," + maxGap + "," + String.format(java.util.Locale.US,"%.4f", meanGap) + "," + countGaps;
        }
    }

    /**
     * Gaps lineares entre índices ocupados (não circular).
     * Ex.: ocupados em [3, 10, 11] => gaps: (10-3-1)=6, (11-10-1)=0 => min=0, max=6, mean=3.0
     */
    public static GapStats compute(boolean[] occupied) {
        GapStats g = new GapStats();
        g.minGap = Integer.MAX_VALUE; g.maxGap = 0; g.meanGap = 0.0; g.countGaps = 0;

        int last = -1;
        for (int i = 0; i < occupied.length; i++) {
            if (occupied[i]) {
                if (last >= 0) {
                    int gap = i - last - 1;
                    if (gap < g.minGap) g.minGap = gap;
                    if (gap > g.maxGap) g.maxGap = gap;
                    g.meanGap += gap;
                    g.countGaps++;
                }
                last = i;
            }
        }
        if (g.countGaps > 0) g.meanGap /= g.countGaps;
        if (g.minGap == Integer.MAX_VALUE) g.minGap = 0;
        return g;
    }

    /**
     * Retorna os 3 maiores tamanhos de lista (apenas para encadeamento).
     */
    public static int[] top3(int[] lens) {
        int a=0,b=0,c=0;
        for (int v : lens) {
            if (v >= a) { c=b; b=a; a=v; }
            else if (v >= b) { c=b; b=v; }
            else if (v > c) { c=v; }
        }
        return new int[]{a,b,c};
    }
}
