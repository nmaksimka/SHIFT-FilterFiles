package io.github.nmaksimka.filterfiles.stats;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.nmaksimka.filterfiles.cli.StatisticsMode;
import io.github.nmaksimka.filterfiles.core.LineType;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class StatisticsTest {

    @Test
    void integerSumDoesNotOverflow() {
        var statistics = new Statistics();
        statistics.accept(LineType.INTEGER, "9223372036854775807");
        statistics.accept(LineType.INTEGER, "1");

        assertThat(statistics.integers().sum()).isEqualByComparingTo("9223372036854775808");
    }

    @Test
    void integersBeyondLongAreSupported() {
        var statistics = new Statistics();
        statistics.accept(LineType.INTEGER, "-99999999999999999999");
        statistics.accept(LineType.INTEGER, "5");

        assertThat(statistics.integers().min()).hasValueSatisfying(min -> assertThat(min)
                .isEqualByComparingTo("-99999999999999999999"));
        assertThat(statistics.integers().max()).hasValueSatisfying(max -> assertThat(max).isEqualByComparingTo("5"));
    }

    @Test
    void floatsKeepPrecision() {
        var statistics = new Statistics();
        statistics.accept(LineType.FLOAT, "0.1");
        statistics.accept(LineType.FLOAT, "0.2");

        assertThat(statistics.floats().sum()).isEqualByComparingTo("0.3");
        assertThat(statistics.floats().average()).hasValueSatisfying(avg -> assertThat(avg)
                .isEqualByComparingTo("0.15"));
    }

    @Test
    void hugeExponentIsNotInfinity() {
        var statistics = new Statistics();
        statistics.accept(LineType.FLOAT, "1e999");

        assertThat(statistics.floats().max()).hasValue(new BigDecimal("1e999"));
    }

    @Test
    void stringLengthCountsCharactersNotBytes() {
        var statistics = new Statistics();
        statistics.accept(LineType.STRING, "Пример");
        statistics.accept(LineType.STRING, "ab");

        assertThat(statistics.strings().shortest()).hasValue(2);
        assertThat(statistics.strings().longest()).hasValue(6);
    }

    @Test
    void emptyStatisticsHaveNoValues() {
        var statistics = new Statistics();

        assertThat(statistics.integers().min()).isEmpty();
        assertThat(statistics.integers().average()).isEmpty();
        assertThat(statistics.strings().shortest()).isEmpty();
    }

    @Test
    void shortReportShowsOnlyCounts() {
        var statistics = new Statistics();
        statistics.accept(LineType.INTEGER, "1");
        statistics.accept(LineType.STRING, "a");

        assertThat(StatisticsReport.format(statistics, StatisticsMode.SHORT)).isEqualTo("""
                Целые числа: 1
                Вещественные числа: 0
                Строки: 1
                """);
    }

    @Test
    void fullReport() {
        var statistics = new Statistics();
        statistics.accept(LineType.INTEGER, "45");
        statistics.accept(LineType.INTEGER, "100500");
        statistics.accept(LineType.FLOAT, "3.1415");
        statistics.accept(LineType.FLOAT, "-0.001");
        statistics.accept(LineType.STRING, "Пример");

        assertThat(StatisticsReport.format(statistics, StatisticsMode.FULL)).isEqualTo("""
                Целые числа: 2
                  минимум: 45
                  максимум: 100500
                  сумма: 100545
                  среднее: 50272.5
                Вещественные числа: 2
                  минимум: -0.001
                  максимум: 3.1415
                  сумма: 3.1405
                  среднее: 1.57025
                Строки: 1
                  самая короткая: 6
                  самая длинная: 6
                """);
    }

    @Test
    void noReportWithoutOption() {
        assertThat(StatisticsReport.format(new Statistics(), StatisticsMode.NONE)).isEmpty();
    }

    @Test
    void formatSwitchesToScientificForExtremeValues() {
        assertThat(StatisticsReport.format(new BigDecimal("1e999"), false)).isEqualTo("1E+999");
        assertThat(StatisticsReport.format(new BigDecimal("1.50"), false)).isEqualTo("1.5");
        assertThat(StatisticsReport.format(new BigDecimal("100"), false)).isEqualTo("100");
        assertThat(StatisticsReport.format(new BigDecimal("1E+2"), true)).isEqualTo("100");
    }
}
