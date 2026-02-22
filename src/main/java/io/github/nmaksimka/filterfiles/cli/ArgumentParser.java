package io.github.nmaksimka.filterfiles.cli;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Set;

public final class ArgumentParser {

    public static final String USAGE = """
            Использование: java -jar filterfiles.jar [опции] файл...

            Опции:
              -o <путь>      папка для результатов (по умолчанию текущая)
              -p <префикс>   префикс имён выходных файлов, например result_
              -a             дописывать в существующие файлы вместо перезаписи
              -s             краткая статистика: количество элементов
              -f             полная статистика: минимум, максимум, сумма, среднее, длины строк
              -h, --help     показать эту справку
              --             всё дальше считать именами файлов

            Результаты: [префикс]integers.txt, [префикс]floats.txt, [префикс]strings.txt.
            Файл создаётся, только если для него есть данные.
            """;

    private static final Set<String> KNOWN_OPTIONS = Set.of("-o", "-p", "-a", "-s", "-f", "-h", "--help", "--");

    private ArgumentParser() {
    }

    public static Options parse(String... args) throws UsageException {
        var outputDir = Path.of("");
        var prefix = "";
        var append = false;
        var statistics = StatisticsMode.NONE;
        var inputs = new ArrayList<Path>();
        var onlyFiles = false;

        for (var i = 0; i < args.length; i++) {
            var arg = args[i];
            if (onlyFiles || !arg.startsWith("-")) {
                inputs.add(path(arg));
                continue;
            }
            switch (arg) {
                case "--" -> onlyFiles = true;
                case "-h", "--help" -> {
                    return Options.helpRequested();
                }
                case "-a" -> append = true;
                case "-s" -> statistics = statistics == StatisticsMode.FULL ? StatisticsMode.FULL : StatisticsMode.SHORT;
                case "-f" -> statistics = StatisticsMode.FULL;
                case "-o" -> outputDir = path(value(args, ++i, arg));
                case "-p" -> prefix = prefix(value(args, ++i, arg));
                default -> throw new UsageException("Неизвестная опция: " + arg);
            }
        }

        if (inputs.isEmpty()) {
            throw new UsageException("Не указаны входные файлы");
        }
        return new Options(outputDir, prefix, append, statistics, inputs, false);
    }

    private static String value(String[] args, int index, String option) throws UsageException {
        if (index >= args.length || KNOWN_OPTIONS.contains(args[index]) || args[index].isBlank()) {
            throw new UsageException("После " + option + " нужно указать значение");
        }
        return args[index];
    }

    private static String prefix(String value) throws UsageException {
        if (value.contains("/") || value.contains("\\") || value.contains("..")) {
            throw new UsageException("Префикс не может содержать разделители пути: " + value);
        }
        path(value + "integers.txt");
        return value;
    }

    private static Path path(String value) throws UsageException {
        try {
            return Path.of(value);
        } catch (InvalidPathException e) {
            throw new UsageException("Некорректный путь: " + value);
        }
    }
}
