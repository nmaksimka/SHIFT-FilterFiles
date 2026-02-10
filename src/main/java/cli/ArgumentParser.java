package main.java.cli;

import java.util.ArrayList;
import java.util.List;

public class ArgumentParser {
    private final CommandLineOptions options = new CommandLineOptions();
    private final List<String> inputFiles = new ArrayList<>();

    public void parse(String[] args) throws IllegalArgumentException {
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            switch (arg) {
                case "-a":
                    options.setAppendMode(true);
                    break;

                case "-s":
                    options.setFullStatistics(false);
                    break;

                case "-f":
                    options.setFullStatistics(true);
                    break;

                case "-o":
                    if (i + 1 >= args.length) {
                        throw new IllegalArgumentException("Опция -o требует указания пути");
                    }
                    options.setOutputPath(args[++i]);
                    break;

                case "-p":
                    if (i + 1 >= args.length) {
                        throw new IllegalArgumentException("Опция -p требует указания префикса");
                    }
                    options.setFilePrefix(args[++i]);
                    break;

                case "-h":
                    printHelp();
                    System.exit(0);
                    break;

                default:
                    if (arg.startsWith("-")) {
                        throw new IllegalArgumentException("Неизвестная опция: " + arg);
                    } else {
                        inputFiles.add(arg);
                    }
                    break;
            }
        }

        if (inputFiles.isEmpty()) {
            throw new IllegalArgumentException("Не указаны входные файлы");
        }
    }

    public CommandLineOptions getOptions() {
        return options;
    }

    public List<String> getInputFiles() {
        return inputFiles;
    }

    public static void printHelp() {
        System.out.println("FilterFiles - утилита для фильтрации содержимого файлов");
        System.out.println("Использование: java FilterFiles [опции] файлы...");
        System.out.println();
        System.out.println("Опции:");
        System.out.println("  -a             Добавлять в конец существующих файлов (по умолчанию: перезапись)");
        System.out.println("  -s             Краткая статистика (по умолчанию)");
        System.out.println("  -f             Полная статистика");
        System.out.println("  -o <путь>      Путь для выходных файлов (по умолчанию: текущая директория)");
        System.out.println("  -p <префикс>   Префикс имен выходных файлов");
        System.out.println("  -h             Показать эту справку");
        System.out.println();
        System.out.println("Примеры:");
        System.out.println("  java FilterFiles -s file1.txt file2.txt");
        System.out.println("  java FilterFiles -o output -p result_ -f data.txt");
        System.out.println("  java FilterFiles -a -p data_ file.txt");
        System.out.println();
        System.out.println("Файлы результатов:");
        System.out.println("  [префикс]integers.txt - целые числа");
        System.out.println("  [префикс]floats.txt   - числа с плавающей точкой");
        System.out.println("  [префикс]strings.txt  - строки");
    }

    public static void printUsage() {
        System.out.println("Использование: java FilterFiles [опции] файлы...");
        System.out.println("Для получения справки используйте: java FilterFiles -h");
    }
}