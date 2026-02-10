package main.java.io;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class FileWriter {
    private final String outputPath;
    private final String filePrefix;
    private final boolean appendMode;

    public FileWriter(String outputPath, String filePrefix, boolean appendMode) {
        this.outputPath = outputPath;
        this.filePrefix = filePrefix;
        this.appendMode = appendMode;
    }

    public void writeResults(List<String> integers, List<String> floats, List<String> strings) {
        try {
            Path outputDir = Paths.get(outputPath);
            Files.createDirectories(outputDir);

            writeFile(integers, "integers.txt");
            writeFile(floats, "floats.txt");
            writeFile(strings, "strings.txt");

        } catch (IOException e) {
            System.err.println("Ошибка создания директории: " + e.getMessage());
        }
    }

    private void writeFile(List<String> data, String filename) throws IOException {
        if (data.isEmpty()) {
            return;
        }

        Path filePath = Paths.get(outputPath, filePrefix + filename);

        StandardOpenOption[] options = appendMode ?
                new StandardOpenOption[]{StandardOpenOption.CREATE, StandardOpenOption.APPEND} :
                new StandardOpenOption[]{StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING};

        Files.write(filePath, data, StandardCharsets.UTF_8, options);
        System.out.println("Создан файл: " + filePath.toAbsolutePath());
    }
}