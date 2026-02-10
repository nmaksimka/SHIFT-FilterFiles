package main.java.cli;

public class CommandLineOptions {
    private boolean appendMode = false;
    private boolean fullStatistics = false;
    private String outputPath = "files"; //System.getProperty("user.dir");
    private String filePrefix = "";

    public boolean isAppendMode() {
        return appendMode;
    }

    public void setAppendMode(boolean appendMode) {
        this.appendMode = appendMode;
    }

    public boolean isFullStatistics() {
        return fullStatistics;
    }

    public void setFullStatistics(boolean fullStatistics) {
        this.fullStatistics = fullStatistics;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    public String getFilePrefix() {
        return filePrefix;
    }

    public void setFilePrefix(String filePrefix) {
        this.filePrefix = filePrefix;
    }

    @Override
    public String toString() {
        return String.format(
                "CommandLineOptions{appendMode=%s, fullStatistics=%s, outputPath='%s', filePrefix='%s'}",
                appendMode, fullStatistics, outputPath, filePrefix
        );
    }
}