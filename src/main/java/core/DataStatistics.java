package main.java.core;

import java.util.ArrayList;
import java.util.List;

public class DataStatistics {
    private int integerCount = 0;
    private int floatCount = 0;
    private int stringCount = 0;

    private Long minInteger = null;
    private Long maxInteger = null;
    private Long sumInteger = 0L;

    private Double minFloat = null;
    private Double maxFloat = null;
    private Double sumFloat = 0.0;

    private Integer minStringLength = null;
    private Integer maxStringLength = null;

    private final List<String> integers = new ArrayList<>();
    private final List<String> floats = new ArrayList<>();
    private final List<String> strings = new ArrayList<>();

    public void addInteger(String value) {
        integerCount++;
        integers.add(value);
    }

    public void addFloat(String value) {
        floatCount++;
        floats.add(value);
    }

    public void addString(String value) {
        stringCount++;
        strings.add(value);
    }

    public void updateIntegerStats(long value) {
        if (minInteger == null || value < minInteger) {
            minInteger = value;
        }
        if (maxInteger == null || value > maxInteger) {
            maxInteger = value;
        }
        sumInteger += value;
    }

    public void updateFloatStats(double value) {
        if (minFloat == null || value < minFloat) {
            minFloat = value;
        }
        if (maxFloat == null || value > maxFloat) {
            maxFloat = value;
        }
        sumFloat += value;
    }

    public void updateStringStats(int length) {
        if (minStringLength == null || length < minStringLength) {
            minStringLength = length;
        }
        if (maxStringLength == null || length > maxStringLength) {
            maxStringLength = length;
        }
    }

    public int getIntegerCount() { return integerCount; }
    public int getFloatCount() { return floatCount; }
    public int getStringCount() { return stringCount; }

    public Long getMinInteger() { return minInteger; }
    public Long getMaxInteger() { return maxInteger; }
    public Long getSumInteger() { return sumInteger; }

    public Double getMinFloat() { return minFloat; }
    public Double getMaxFloat() { return maxFloat; }
    public Double getSumFloat() { return sumFloat; }

    public Integer getMinStringLength() { return minStringLength; }
    public Integer getMaxStringLength() { return maxStringLength; }

    public List<String> getIntegers() { return integers; }
    public List<String> getFloats() { return floats; }
    public List<String> getStrings() { return strings; }

    public double getIntegerAverage() {
        return integerCount > 0 ? (double) sumInteger / integerCount : 0.0;
    }

    public double getFloatAverage() {
        return floatCount > 0 ? sumFloat / floatCount : 0.0;
    }
}