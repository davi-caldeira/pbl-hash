package br.pucr.hash;

public final class CodeUtils {
    public static String format9(int codigo) {
        String s = Integer.toString(codigo);
        int k = 9 - s.length();
        if (k <= 0) return s;
        StringBuilder sb = new StringBuilder(9);
        for (int i = 0; i < k; i++) sb.append('0');
        sb.append(s);
        return sb.toString();
    }
}
