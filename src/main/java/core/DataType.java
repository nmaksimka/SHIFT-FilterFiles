package main.java.core;

public enum DataType {
    INTEGER("целое число"),
    FLOAT("вещественное число"),
    STRING("строка");

    private final String description;

    DataType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}