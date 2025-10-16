package br.pucr.hash;

public final class Registro {
    private final int codigo; // 9 dígitos (0..999_999_999)

    public Registro(int codigo) {
        if (codigo < 0 || codigo > 999_999_999) {
            throw new IllegalArgumentException("Código deve ter 9 dígitos (0..999999999).");
        }
        this.codigo = codigo;
    }

    public int getCodigo() { return codigo; }

    public String codigoFormatado() {
        String s = Integer.toString(codigo);
        int faltam = 9 - s.length();
        if (faltam <= 0) return s;
        StringBuilder sb = new StringBuilder(9);
        for (int i = 0; i < faltam; i++) sb.append('0');
        sb.append(s);
        return sb.toString();
    }
}
