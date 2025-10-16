package br.pucr.hash;


public final class DataGenerator {
    public static int[] gerarCodigos(long seed, int n) {
        int[] v = new int[n];
        
        long mod = 1_000_000_007L; 
        long a = 48271L;
        long c = 11L;
        long x = (seed % mod + mod) % mod;
        for (int i = 0; i < n; i++) {
            x = (a * x + c) % mod;
            int codigo = (int)(x % 1_000_000_000L); 
            v[i] = codigo;
        }
        return v;
    }

    public static Registro[] envolver(int[] codigos) {
        Registro[] rs = new Registro[codigos.length];
        for (int i = 0; i < codigos.length; i++) rs[i] = new Registro(codigos[i]);
        return rs;
    }
}
