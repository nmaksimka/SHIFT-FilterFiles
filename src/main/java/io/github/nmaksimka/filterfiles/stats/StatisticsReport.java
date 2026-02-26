package io.github.nmaksimka.filterfiles.stats;

import io.github.nmaksimka.filterfiles.cli.StatisticsMode;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.OptionalInt;

public final class StatisticsReport {

    private static final int READABLE_DIGITS = 20;

    private StatisticsReport() {
    }

    public static String format(Statistics statistics, StatisticsMode mode) {
        return switch (mode) {
            case NONE -> "";
            case SHORT -> shortReport(statistics);
            case FULL -> fullReport(statistics);
        };
    }

    private static String shortReport(Statistics statistics) {
        return """
                Целые числа: %d
                Вещественные числа: %d
                Строки: %d
                """.formatted(
                statistics.integers().count(),
                statistics.floats().count(),
                statistics.strings().count());
    }

    private static String fullReport(Statistics statistics) {
        var report = new StringBuilder();
        appendNumbers(report, "Целые числа", statistics.integers(), true);
        appendNumbers(report, "Вещественные числа", statistics.floats(), false);
        var strings = statistics.strings();
        report.append("Строки: ").append(strings.count()).append('\n');
        line(report, "самая короткая", strings.shortest());
        line(report, "самая длинная", strings.longest());
        return report.toString();
    }

    private static void appendNumbers(StringBuilder report, String title, NumberStatistics numbers,
                                      boolean integral) {
        report.append(title).append(": ").append(numbers.count()).append('\n');
        if (numbers.count() == 0) {
            return;
        }
        line(report, "минимум", numbers.min(), integral);
        line(report, "максимум", numbers.max(), integral);
        line(report, "сумма", Optional.of(numbers.sum()), integral);
        line(report, "среднее", numbers.average(), false);
    }

    private static void line(StringBuilder report, String name, Optional<BigDecimal> value, boolean integral) {
        value.ifPresent(number -> append(report, name, format(number, integral)));
    }

    private static void line(StringBuilder report, String name, OptionalInt value) {
        value.ifPresent(length -> append(report, name, String.valueOf(length)));
    }

    private static void append(StringBuilder report, String name, String value) {
        report.append("  ").append(name).append(": ").append(value).append('\n');
    }

    static String format(BigDecimal number, boolean integral) {
        if (integral) {
            return number.toBigInteger().toString();
        }
        var stripped = number.stripTrailingZeros();
        var magnitude = stripped.precision() - stripped.scale();
        var readable = magnitude <= READABLE_DIGITS && stripped.scale() <= READABLE_DIGITS;
        return readable ? stripped.toPlainString() : stripped.toString();
    }
}
