package main.java.io;

import main.java.core.DataClassifier;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileProcessor {
    private final DataClassifier classifier;

    public FileProcessor(boolean collectFullStats) {
        this.classifier = new DataClassifier(collectFullStats);
    }

    public void processFile(String filePath) {
        Path path = Paths.get(filePath);

        if (!Files.exists(path)) {
            System.err.println("Предупреждение: файл " + filePath + " не существует, пропускаем");
            return;
        }

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();

                if (line.isEmpty()) {
                    continue;
                }

                classifier.classifyAndStore(line);
            }

            System.out.printf("Обработан файл: %s (%d строк)%n", filePath, lineNumber);

        } catch (IOException e) {
            System.err.println("Ошибка чтения файла " + filePath + ": " + e.getMessage());
        }
    }

    public DataClassifier getClassifier() {
        return classifier;
    }
}