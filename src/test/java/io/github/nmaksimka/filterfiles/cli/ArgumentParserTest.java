package io.github.nmaksimka.filterfiles.cli;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ArgumentParserTest {

    @Test
    void defaults() throws UsageException {
        var options = ArgumentParser.parse("in1.txt");

        assertThat(options.outputDir()).isEqualTo(Path.of(""));
        assertThat(options.prefix()).isEmpty();
        assertThat(options.append()).isFalse();
        assertThat(options.statistics()).isEqualTo(StatisticsMode.NONE);
        assertThat(options.inputs()).containsExactly(Path.of("in1.txt"));
    }

    @Test
    void allOptions() throws UsageException {
        var options = ArgumentParser.parse("-s", "-a", "-p", "sample-", "-o", "out", "in1.txt", "in2.txt");

        assertThat(options.outputDir()).isEqualTo(Path.of("out"));
        assertThat(options.prefix()).isEqualTo("sample-");
        assertThat(options.append()).isTrue();
        assertThat(options.statistics()).isEqualTo(StatisticsMode.SHORT);
        assertThat(options.inputs()).containsExactly(Path.of("in1.txt"), Path.of("in2.txt"));
    }

    @Test
    void optionsMayFollowFiles() throws UsageException {
        var options = ArgumentParser.parse("in1.txt", "-f", "in2.txt");

        assertThat(options.statistics()).isEqualTo(StatisticsMode.FULL);
        assertThat(options.inputs()).hasSize(2);
    }

    @Test
    void fullStatisticsWinsOverShort() throws UsageException {
        assertThat(ArgumentParser.parse("-f", "-s", "in.txt").statistics()).isEqualTo(StatisticsMode.FULL);
        assertThat(ArgumentParser.parse("-s", "-f", "in.txt").statistics()).isEqualTo(StatisticsMode.FULL);
    }

    @Test
    void doubleDashTreatsRestAsFiles() throws UsageException {
        var options = ArgumentParser.parse("--", "-s");

        assertThat(options.inputs()).containsExactly(Path.of("-s"));
        assertThat(options.statistics()).isEqualTo(StatisticsMode.NONE);
    }

    @Test
    void helpStopsParsing() throws UsageException {
        assertThat(ArgumentParser.parse("-h").help()).isTrue();
        assertThat(ArgumentParser.parse("in.txt", "--help", "-x").help()).isTrue();
    }

    @Test
    void missingFiles() {
        assertThatThrownBy(() -> ArgumentParser.parse("-s"))
                .isInstanceOf(UsageException.class)
                .hasMessageContaining("входные файлы");
    }

    @Test
    void unknownOption() {
        assertThatThrownBy(() -> ArgumentParser.parse("-x", "in.txt"))
                .isInstanceOf(UsageException.class)
                .hasMessageContaining("-x");
    }

    @ParameterizedTest
    @ValueSource(strings = {"-o", "-p"})
    void optionWithoutValue(String option) {
        assertThatThrownBy(() -> ArgumentParser.parse("in.txt", option))
                .isInstanceOf(UsageException.class)
                .hasMessageContaining(option);
    }

    @Test
    void optionValueCannotBeAnotherOption() {
        assertThatThrownBy(() -> ArgumentParser.parse("-o", "-f", "in.txt"))
                .isInstanceOf(UsageException.class)
                .hasMessageContaining("-o");
    }

    @ParameterizedTest
    @ValueSource(strings = {"../evil_", "dir/name_", "dir\\name_"})
    void prefixCannotLeaveOutputDirectory(String prefix) {
        assertThatThrownBy(() -> ArgumentParser.parse("-p", prefix, "in.txt"))
                .isInstanceOf(UsageException.class)
                .hasMessageContaining("Префикс");
    }
}
