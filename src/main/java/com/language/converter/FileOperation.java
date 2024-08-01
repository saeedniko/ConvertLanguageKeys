package com.language.converter;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileOperation {

    public static String readFile(Path path) throws IOException {
        return Files.readString(path);
    }

    public static void writeFile(Path path, String content) throws IOException {
        try (FileWriter writer = new FileWriter(path.toFile())) {
            writer.write(content);
        }
    }

    public static FileReader getFileReader(Path path) throws IOException {
        return new FileReader(path.toFile());
    }
}