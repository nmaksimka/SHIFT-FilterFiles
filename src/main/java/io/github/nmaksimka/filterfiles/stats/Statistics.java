package io.github.nmaksimka.filterfiles.stats;

import io.github.nmaksimka.filterfiles.core.LineType;
import java.math.BigDecimal;

public final class Statistics {

    private final NumberStatistics integers = new NumberStatistics();
    private final NumberStatistics floats = new NumberStatistics();
    private final StringStatistics strings = new StringStatistics();

    public void accept(LineType type, String value) {
        switch (type) {
            case INTEGER -> integers.accept(new BigDecimal(value));
            case FLOAT -> floats.accept(new BigDecimal(value));
            case STRING -> strings.accept(value);
        }
    }

    public NumberStatistics integers() {
        return integers;
    }

    public NumberStatistics floats() {
        return floats;
    }

    public StringStatistics strings() {
        return strings;
    }
}
