package ru.job4j.concurrent;

import java.io.*;
import java.util.function.Predicate;

public class ParseFile {
    private final File file;

    public ParseFile(File file) {
        this.file = file;
    }

    public String getContentWithoutUnicode() throws IOException {
        return getContent(it -> true);
    }

    public String getContentWithUnicode() throws IOException {
        return getContent(it -> it < 0x80);
    }

    public String getContent(Predicate<Integer> pred) throws IOException {
        try (BufferedInputStream input = new BufferedInputStream(new FileInputStream(file))) {
            StringBuilder output = new StringBuilder();
            int data;
            while ((data = input.read()) != 0) {
                if (pred.test(data)) {
                    output.append((char) data);
                }
            }
            return output.toString();
        }
    }
}
