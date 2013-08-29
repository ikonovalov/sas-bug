package com.luxoft.sas.bug;

import java.io.*;

public class FileLineIterator {

    public static final char LINE_TERMINATOR = '\n';
    private final File file;

    private BufferedReader bReader;

    private boolean closed = false;

    private String currentLine = "";

    private String lastFetched = null;

    public FileLineIterator(final File file) throws FileNotFoundException {
        this.file = file;
        this.bReader = new BufferedReader(new FileReader(this.file));
    }

     public String next() {
        try {
            currentLine = bReader.readLine();
        } catch (IOException e) {
           throw new RuntimeException("FileLineIterator error", e);
        }
        return currentLine;
    }


    public final void close() {
        if (!closed) {
            try {
                bReader.close();
            } catch (IOException e) { }
            closed = true;
        }
    }

    public static StringBuilder asStringBuilder(final File file) throws FileNotFoundException {
        final FileLineIterator iter = new FileLineIterator(file);
        final StringBuilder sb = new StringBuilder(Long.valueOf(file.length()).intValue());
        String line;
        while((line = iter.next()) != null) {
            sb.append(line).append(LINE_TERMINATOR);
        }
        return sb;
    }

    @Override
    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }
}
