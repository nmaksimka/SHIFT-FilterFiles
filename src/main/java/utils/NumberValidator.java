package main.java.utils;

import main.java.core.DataType;

import java.util.regex.Pattern;

public class NumberValidator {
    private static final Pattern INTEGER_PATTERN = Pattern.compile("^[-+]?\\d+$");
    private static final Pattern FLOAT_PATTERN = Pattern.compile(
            "^[-+]?\\d*\\.\\d+([eE][-+]?\\d+)?$|^[-+]?\\d+(\\.\\d*)?([eE][-+]?\\d+)?$"
    );

    public static boolean isInteger(String value) {
        if (!INTEGER_PATTERN.matcher(value).matches()) {
            return false;
        }

        try {
            Long.parseLong(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isFloat(String value) {
        if (!FLOAT_PATTERN.matcher(value).matches()) {
            return false;
        }

        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static DataType classify(String value) {
        if (isInteger(value)) {
            return DataType.INTEGER;
        } else if (isFloat(value)) {
            return DataType.FLOAT;
        } else {
            return DataType.STRING;
        }
    }
}