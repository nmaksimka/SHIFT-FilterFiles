package io.github.nmaksimka.filterfiles.core;

import java.util.regex.Pattern;

public final class LineClassifier {

    private static final Pattern INTEGER = Pattern.compile("[+-]?\\d+");
    private static final Pattern FLOAT = Pattern.compile("[+-]?(\\d+\\.\\d*|\\.\\d+|\\d+)([eE][+-]?\\d+)?");

    private LineClassifier() {
    }

    public static LineType classify(String value) {
        if (INTEGER.matcher(value).matches()) {
            return LineType.INTEGER;
        }
        if (FLOAT.matcher(value).matches()) {
            return LineType.FLOAT;
        }
        return LineType.STRING;
    }
}
