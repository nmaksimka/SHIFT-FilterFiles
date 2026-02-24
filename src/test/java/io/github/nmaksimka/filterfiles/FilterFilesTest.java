package io.github.nmaksimka.filterfiles;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FilterFilesTest {

    @TempDir
    Path dir;

    private final ByteArrayOutputStream out = new ByteArrayOutputStream();
    private final ByteArrayOutputStream err = new ByteArrayOutputStream();
    private Path in1;
    private Path in2;

    @BeforeEach
    void inputs() throws IOException {
        in1 = write("in1.txt", "Lorem ipsum dolor sit amet", "45", "Пример", "3.1415", "consectetur adipiscing",
                "-0.001", "тестовое задание", "100500");
        in2 = write("in2.txt", "Нормальная форма числа с плавающей запятой", "1.528535047E-25", "Long",
                "1234567890123456789");
    }

    @Test
    void exampleFromTask() throws IOException {
        var code = run("-o", dir.resolve("out").toString(), "-p", "sample-", in1.toString(), in2.toString());

        assertThat(code).isEqualTo(FilterFiles.OK);
        assertThat(read("out/sample-integers.txt")).containsExactly("45", "100500", "1234567890123456789");
        assertThat(read("out/sample-floats.txt")).containsExactly("3.1415", "-0.001", "1.528535047E-25");
        assertThat(read("out/sample-strings.txt")).containsExactly("Lorem ipsum dolor sit amet", "Пример",
                "consectetur adipiscing", "тестовое задание", "Нормальная форма числа с плавающей запятой", "Long");
        assertThat(out()).isEmpty();
        assertThat(err()).isEmpty();
    }

    @Test
    void fileIsCreatedOnlyWhenThereIsData() throws IOException {
        var onlyText = write("text.txt", "hello", "world");

        run("-o", dir.resolve("out").toString(), onlyText.toString());

        assertThat(dir.resolve("out/strings.txt")).exists();
        assertThat(dir.resolve("out/integers.txt")).doesNotExist();
        assertThat(dir.resolve("out/floats.txt")).doesNotExist();
    }

    @Test
    void overwriteByDefaultAndAppendWithOption() throws IOException {
        var output = dir.resolve("out").toString();
        run("-o", output, in1.toString());
        run("-o", output, in1.toString());
        assertThat(read("out/integers.txt")).containsExactly("45", "100500");

        run("-a", "-o", output, in2.toString());
        assertThat(read("out/integers.txt")).containsExactly("45", "100500", "1234567890123456789");
    }

    @Test
    void appendDoesNotGlueLinesWhenFileHasNoTrailingLineBreak() throws IOException {
        var output = dir.resolve("out");
        Files.createDirectories(output);
        Files.writeString(output.resolve("integers.txt"), "1");

        run("-a", "-o", output.toString(), in1.toString());

        assertThat(read("out/integers.txt")).containsExactly("1", "45", "100500");
    }

    @Test
    void missingFileIsReportedAndOthersAreProcessed() throws IOException {
        var code = run("-o", dir.resolve("out").toString(), "nope.txt", in1.toString());

        assertThat(code).isEqualTo(FilterFiles.PARTIAL_FAILURE);
        assertThat(err()).contains("nope.txt");
        assertThat(read("out/integers.txt")).containsExactly("45", "100500");
    }

    @Test
    void bomAndSurroundingSpaces() throws IOException {
        var file = dir.resolve("bom.txt");
        Files.writeString(file, "﻿12\n  7  \n  hi  \n\n   \n", StandardCharsets.UTF_8);

        run("-o", dir.resolve("out").toString(), file.toString());

        assertThat(read("out/integers.txt")).containsExactly("12", "7");
        assertThat(read("out/strings.txt")).containsExactly("  hi  ");
    }

    @Test
    void nonUtf8FileIsReported() throws IOException {
        var file = dir.resolve("cp1251.txt");
        Files.write(file, new byte[]{'4', '2', '\n', (byte) 0xCF, (byte) 0xF0, (byte) 0xE8, '\n'});

        var code = run("-o", dir.resolve("out").toString(), file.toString(), in2.toString());

        assertThat(code).isEqualTo(FilterFiles.PARTIAL_FAILURE);
        assertThat(err()).contains("UTF-8");
        assertThat(read("out/integers.txt")).contains("1234567890123456789");
    }

    @Test
    void fullStatisticsIsPrinted() {
        run("-f", "-o", dir.resolve("out").toString(), in1.toString(), in2.toString());

        assertThat(out()).contains("Целые числа: 3", "максимум: 1234567890123456789", "Вещественные числа: 3",
                "Строки: 6", "самая длинная: 42");
    }

    @Test
    void shortStatisticsIsPrinted() {
        run("-s", "-o", dir.resolve("out").toString(), in1.toString());

        assertThat(out()).isEqualToNormalizingNewlines("""
                Целые числа: 2
                Вещественные числа: 2
                Строки: 4
                """);
    }

    @Test
    void usageErrorReturnsTwo() {
        assertThat(run()).isEqualTo(FilterFiles.USAGE_ERROR);
        assertThat(err()).contains("Не указаны входные файлы", "-h");
    }

    @Test
    void helpPrintsUsage() {
        assertThat(run("-h")).isEqualTo(FilterFiles.OK);
        assertThat(out()).contains("Использование", "-o <путь>");
    }

    @Test
    void outputDirectoryThatIsAFileIsReported() throws IOException {
        var blocker = Files.writeString(dir.resolve("blocker"), "x");

        var code = run("-o", blocker.toString(), in1.toString());

        assertThat(code).isEqualTo(FilterFiles.PARTIAL_FAILURE);
        assertThat(err()).contains("Не удалось записать");
    }

    private int run(String... args) {
        var app = new FilterFiles(print(out), print(err));
        return app.run(args);
    }

    private Path write(String name, String... lines) throws IOException {
        return Files.write(dir.resolve(name), List.of(lines), StandardCharsets.UTF_8);
    }

    private List<String> read(String name) throws IOException {
        return new ArrayList<>(Files.readAllLines(dir.resolve(name), StandardCharsets.UTF_8));
    }

    private String out() {
        return out.toString(StandardCharsets.UTF_8);
    }

    private String err() {
        return err.toString(StandardCharsets.UTF_8);
    }

    private static PrintStream print(ByteArrayOutputStream target) {
        return new PrintStream(target, true, StandardCharsets.UTF_8);
    }
}
