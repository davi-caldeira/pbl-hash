package br.pucr.hash;

import java.io.*;

public final class CsvWriter implements Closeable {
    private final BufferedWriter bw;
    public CsvWriter(String path, String header) throws IOException {
        File out = new File(path);
        out.getParentFile().mkdirs();
        this.bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(out), "UTF-8"));
        if (header != null && !header.isEmpty()) {
            bw.write(header); bw.newLine();
        }
    }
    public void writeLine(String line) throws IOException {
        bw.write(line); bw.newLine();
    }
    @Override public void close() throws IOException { bw.flush(); bw.close(); }
}
