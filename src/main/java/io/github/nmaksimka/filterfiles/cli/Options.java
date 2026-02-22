package io.github.nmaksimka.filterfiles.cli;

import java.nio.file.Path;
import java.util.List;

public record Options(Path outputDir, String prefix, boolean append, StatisticsMode statistics, List<Path> inputs,
                      boolean help) {

    public Options {
        inputs = List.copyOf(inputs);
    }

    public static Options helpRequested() {
        return new Options(Path.of(""), "", false, StatisticsMode.NONE, List.of(), true);
    }
}
