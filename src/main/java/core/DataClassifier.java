package main.java.core;

import main.java.utils.NumberValidator;

public class DataClassifier {
    private final DataStatistics statistics;
    private final boolean collectFullStats;

    public DataClassifier(boolean collectFullStats) {
        this.statistics = new DataStatistics();
        this.collectFullStats = collectFullStats;
    }

    public void classifyAndStore(String line) {
        DataType type = NumberValidator.classify(line);

        switch (type) {
            case INTEGER:
                statistics.addInteger(line);
                if (collectFullStats) {
                    statistics.updateIntegerStats(Long.parseLong(line));
                }
                break;

            case FLOAT:
                statistics.addFloat(line);
                if (collectFullStats) {
                    statistics.updateFloatStats(Double.parseDouble(line));
                }
                break;

            case STRING:
                statistics.addString(line);
                if (collectFullStats) {
                    statistics.updateStringStats(line.length());
                }
                break;
        }
    }

    public DataStatistics getStatistics() {
        return statistics;
    }
}