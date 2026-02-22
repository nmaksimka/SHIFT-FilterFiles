package io.github.nmaksimka.filterfiles;

import io.github.nmaksimka.filterfiles.cli.ArgumentParser;
import io.github.nmaksimka.filterfiles.cli.Options;
import io.github.nmaksimka.filterfiles.cli.UsageException;
import io.github.nmaksimka.filterfiles.core.LineClassifier;
import io.github.nmaksimka.filterfiles.core.LineType;
import io.github.nmaksimka.filterfiles.files.ResultWriter;
import io.github.nmaksimka.filterfiles.stats.Statistics;
import io.github.nmaksimka.filterfiles.stats.StatisticsReport;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class FilterFiles {

    public static final int OK = 0;
    public static final int PARTIAL_FAILURE = 1;
    public static final int USAGE_ERROR = 2;

    private static final char BOM = '﻿';

    private final PrintStream out;
    private final PrintStream err;

    public FilterFiles(PrintStream out, PrintStream err) {
        this.out = out;
        this.err = err;
    }

    public int run(String... args) {
        Options options;
        try {
            options = ArgumentParser.parse(args);
        } catch (UsageException e) {
            err.println(e.getMessage());
            err.println("Справка: java -jar filterfiles.jar -h");
            return USAGE_ERROR;
        }
        if (options.help()) {
            out.print(ArgumentParser.USAGE);
            return OK;
        }
        return process(options);
    }

    private int process(Options options) {
        var statistics = new Statistics();
        var failedInputs = 0;
        try (var writer = new ResultWriter(options.outputDir(), options.prefix(), options.append(), err)) {
            for (var input : options.inputs()) {
                if (!readFile(input, statistics, writer)) {
                    failedInputs++;
                }
            }
            writer.close();
            out.print(StatisticsReport.format(statistics, options.statistics()));
            if (failedInputs > 0) {
                err.printf("Не обработано файлов: %d из %d%n", failedInputs, options.inputs().size());
            }
            return failedInputs > 0 || writer.hasErrors() ? PARTIAL_FAILURE : OK;
        }
    }

    private boolean readFile(Path input, Statistics statistics, ResultWriter writer) {
        if (!Files.isRegularFile(input)) {
            err.println("Файл не найден: " + input);
            return false;
        }
        var lineNumber = 0;
        try (var reader = Files.newBufferedReader(input, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (lineNumber == 1 && !line.isEmpty() && line.charAt(0) == BOM) {
                    line = line.substring(1);
                }
                accept(line, statistics, writer);
            }
            return true;
        } catch (MalformedInputException e) {
            err.printf("Файл %s не в кодировке UTF-8, прочитано строк: %d%n", input, lineNumber);
        } catch (IOException e) {
            err.printf("Ошибка чтения %s после строки %d: %s%n", input, lineNumber, e.getMessage());
        }
        return false;
    }

    private static void accept(String line, Statistics statistics, ResultWriter writer) {
        var value = line.strip();
        if (value.isEmpty()) {
            return;
        }
        var type = LineClassifier.classify(value);
        var stored = type == LineType.STRING ? line : value;
        statistics.accept(type, stored);
        writer.write(type, stored);
    }
}
