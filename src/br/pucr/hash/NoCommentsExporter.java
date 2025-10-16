package br.pucr.hash;

import java.io.*;

public final class NoCommentsExporter {
    public static void export(String srcDir, String dstDir) throws IOException {
        File src = new File(srcDir);
        File dst = new File(dstDir);
        if (!dst.exists()) dst.mkdirs();
        copyRec(src, dst);
    }
    private static void copyRec(File f, File dstRoot) throws IOException {
        if (f.isDirectory()) {
            File d = new File(dstRoot, f.getName());
            if (!d.exists()) d.mkdir();
            for (File c : f.listFiles()) copyRec(c, d);
            return;
        }
        if (!f.getName().endsWith(".java")) return;
        String content = readAll(f);
        String noCom = stripComments(content);
        File out = new File(dstRoot, f.getName());
        writeAll(out, noCom);
    }
    private static String readAll(File f) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) { sb.append(line).append('\n'); }
        br.close();
        return sb.toString();
    }
    private static void writeAll(File f, String s) throws IOException {
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), "UTF-8"));
        bw.write(s); bw.close();
    }
    private static String stripComments(String s) {
        // remove /* ... */ e // ...
        StringBuilder out = new StringBuilder(s.length());
        boolean inBlock = false;
        boolean inLine = false;
        for (int i = 0; i < s.length(); i++) {
            if (inBlock) {
                if (i+1 < s.length() && s.charAt(i) == '*' && s.charAt(i+1) == '/') {
                    inBlock = false; i++;
                }
                continue;
            }
            if (inLine) {
                if (s.charAt(i) == '\n') { inLine = false; out.append('\n'); }
                continue;
            }
            if (i+1 < s.length() && s.charAt(i) == '/' && s.charAt(i+1) == '*') { inBlock = true; i++; continue; }
            if (i+1 < s.length() && s.charAt(i) == '/' && s.charAt(i+1) == '/') { inLine = true; i++; continue; }
            out.append(s.charAt(i));
        }
        return out.toString();
    }
}
