package main.java;

import main.java.cli.ArgumentParser;
import main.java.cli.CommandLineOptions;
import main.java.core.DataStatistics;
import main.java.io.FileProcessor;
import main.java.io.FileWriter;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    private static void createDefaultDirectory() throws Exception {
        Path filesDir = Paths.get("src", "main", "files");
        if (!Files.exists(filesDir)) {
            Files.createDirectories(filesDir);
            System.out.println("Создана директория " + filesDir.toAbsolutePath());
        } else {
            System.out.println("Директория для результатов уже существует " + filesDir.toAbsolutePath());
        }

        Path input1 = filesDir.resolve("input1.txt");
        if (!Files.exists(input1)) {
            List<String> input1Content = List.of(
                    "Lorem ipsum dolor sit amet",
                    "45",
                    "Пример",
                    "3.1415",
                    "consectetur adipiscing",
                    "-0.001",
                    "тестовое задание",
                    "100500"
            );
            Files.write(input1, input1Content, StandardCharsets.UTF_8);
            System.out.println("Создан тестовый файл " + input1.toAbsolutePath());
        }

        Path input2 = filesDir.resolve("input2.txt");
        if (!Files.exists(input2)) {
            List<String> input2Content = List.of(
                    "Нормальная форма числа с плавающей запятой",
                    "1.528535047E-25",
                    "Long",
                    "1234567890123456789"
            );
            Files.write(input2, input2Content, StandardCharsets.UTF_8);
            System.out.println("Создан тестовый файл " + input2.toAbsolutePath());
        }
    }

    private static void printStatistics(DataStatistics stats, boolean fullStatistics) {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("СТАТИСТИКА ОБРАБОТКИ ДАННЫХ");
        System.out.println("=".repeat(40));

        if (!fullStatistics) {
            System.out.println("Краткая статистика:");
            System.out.printf("  Целые числа: %d%n", stats.getIntegerCount());
            System.out.printf("  Дробные числа: %d%n", stats.getFloatCount());
            System.out.printf("  Строки: %d%n", stats.getStringCount());
        } else {
            System.out.println("Полная статистика:");

            System.out.printf("%nЦелые числа (%d):%n", stats.getIntegerCount());
            if (stats.getIntegerCount() > 0) {
                System.out.printf("  Минимальное: %d%n", stats.getMinInteger());
                System.out.printf("  Максимальное: %d%n", stats.getMaxInteger());
                System.out.printf("  Сумма: %d%n", stats.getSumInteger());
                System.out.printf("  Среднее: %.2f%n", stats.getIntegerAverage());
            }

            System.out.printf("%nДробные числа (%d):%n", stats.getFloatCount());
            if (stats.getFloatCount() > 0) {
                System.out.printf("  Минимальное: %g%n", stats.getMinFloat());
                System.out.printf("  Максимальное: %g%n", stats.getMaxFloat());
                System.out.printf("  Сумма: %g%n", stats.getSumFloat());
                System.out.printf("  Среднее: %g%n", stats.getFloatAverage());
            }

            System.out.printf("%nСтроки (%d):%n", stats.getStringCount());
            if (stats.getStringCount() > 0) {
                System.out.printf("  Минимальная длина: %d%n", stats.getMinStringLength());
                System.out.printf("  Максимальная длина: %d%n", stats.getMaxStringLength());
            }
        }
        System.out.println("=".repeat(40) + "\n");
    }

    public static void main(String[] args) {
        try {
            System.out.println("=".repeat(50));
            System.out.println("  ЗАПУСК FILTERFILES УТИЛИТЫ");
            System.out.println("=".repeat(50));

            createDefaultDirectory();

            if (args.length == 0) {
                System.out.println("Аргументы не указаны, используем файлы по умолчанию");
                System.out.println("Для справки используйте: java -cp out main.java.Main -h\n");
                args = new String[]{"src/main/files/input1.txt", "src/main/files/input2.txt"};
            }

            ArgumentParser parser = new ArgumentParser();
            parser.parse(args);

            CommandLineOptions options = parser.getOptions();
            List<String> inputFiles = parser.getInputFiles();

            System.out.println("Настройки: " + options);
            System.out.printf("Файлов для обработки: %d%n%n", inputFiles.size());

            FileProcessor fileProcessor = new FileProcessor(options.isFullStatistics());

            for (String file : inputFiles) {
                fileProcessor.processFile(file);
            }

            DataStatistics stats = fileProcessor.getClassifier().getStatistics();
            printStatistics(stats, options.isFullStatistics());

            FileWriter fileWriter = new FileWriter(
                    options.getOutputPath(),
                    options.getFilePrefix(),
                    options.isAppendMode()
            );

            fileWriter.writeResults(
                    stats.getIntegers(),
                    stats.getFloats(),
                    stats.getStrings()
            );

            System.out.println("\n" + "=".repeat(50));
            System.out.println("  ОБРАБОТКА УСПЕШНО ЗАВЕРШЕНА");
            System.out.println("=".repeat(50));

        } catch (IllegalArgumentException e) {
            System.err.println("Ошибка: " + e.getMessage());
            System.err.println();
            ArgumentParser.printUsage();
            System.exit(220);
        } catch (Exception e) {
            System.err.println("Неожиданная ошибка: " + e.getMessage());
            e.printStackTrace();
            System.exit(400);
        }
    }
}