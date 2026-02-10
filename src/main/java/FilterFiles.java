//package main.java;
//
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//import java.nio.file.*;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//import java.io.BufferedReader;
//
//public class FilterFiles {
//    private static final Pattern INTEGER_PTRN = Pattern.compile("^[-+]?\\d+$");
//    private static final Pattern FLOAT_PTRN = Pattern.compile("^[-+]?\\d*\\.\\d+([eE][-+]?\\d+)?$|^[-+]?\\d+(\\.\\d*)?([eE][-+]?\\d+)?$"
//    );
//
//    private static boolean appendMode = false;
//    private static String viewModeStats = "shortViewMode";
//    private static String prefix = "";
//    private static String pathToFiles = System.getProperty("user.dir");
//    private static final List<String> inputFiles = new ArrayList<>();
//
//    private static int integerCount = 0;
//    private static int floatCount = 0;
//    private static int stringCount = 0;
//
//    private static Long maxInt = null, minInt = null, sumInt = 0L;
//    private static Double maxFloat = null, minFloat = null, sumFloat = 0.0;
//    private static Integer maxStringLength = null, minStringLength = null;
//
//    private static final List<String> integers = new ArrayList<>();
//    private static final List<String> floats = new ArrayList<>();
//    private static final List<String> strings = new ArrayList<>();
//
//    public static void main(String[] args) {
//        if(args.length == 0 || args[0].equals("-h")) {
//            printHelp();
//            return;
//        }
//
//        parseArguments(args);
//
//        if(inputFiles.isEmpty()) {
//            System.err.println("Ошибка не указаны входные файлы");
//            printHelp();
//            return;
//        }
//
//        for(String file : inputFiles) {
//            processFile(file);
//        }
//
//        printStatistics();
//
//        writeResultsToFiles();
//    }
//
//    public static void parseArguments(String[] args) {
//        for (int i = 0; i < args.length; i++) {
//            String arg = args[i];
//            switch (arg) {
//                case "-h" -> {
//                    printHelp();
//                    return;
//                }
//                case "-a" -> {
//                    appendMode = !appendMode;
//                }
//                case "-s" -> {
//                    viewModeStats = "shortViewMode";
//                }
//                case "-f" -> {
//                    viewModeStats = "fullViewMode";
//                }
//                case "-o" -> {
//                    try {
//                        if (i + 1 >= args.length) throw new IllegalArgumentException("Опция -o требует пути(-h для помощи)");
//                        String nextArg = args[i + 1];
//                        if (nextArg.startsWith("-")) throw new IllegalArgumentException("Опция требует пути, а не другой опции(-h для помощи)");
//
//                        pathToFiles = nextArg;
//                        i++;
//                    } catch (IllegalArgumentException err) {
//                        System.err.println("Error" + err.getMessage());
//                        System.exit(220);
//                    }
//
//                }
//                case "-p" -> {
//                    try {
//                        if (i + 1 >= args.length) throw new IllegalArgumentException("Опция -p требует пути(-h для помощи)");
//                        String nextArg = args[i + 1];
//                        if (nextArg.startsWith("-")) throw new IllegalArgumentException("Опция требует пути, а не другой опции(-h для помощи)");
//
//                        prefix = nextArg;
//                        i++;
//                    } catch (IllegalArgumentException err) {
//                        System.err.println("Error" + err.getMessage());
//                        System.exit(220);
//                    }
//                }
//                default -> {
//                    if (arg.startsWith("-")) {
//                        System.err.println("Ошибка: неизвестная опция " + arg + " (-h для помощи)");
//                        System.exit(220);
//                    } else {
//                        inputFiles.add(arg);
//                    }
//                }
//            }
//        }
//    }
//
//    private static void processFile(String file) {
//        System.out.println("Обработка файла " + file);
//        Path filePath = Paths.get(file);
//        System.out.println("Полный путь " + filePath.toAbsolutePath());
//        if(!Files.exists(filePath)) {
//            System.err.println("Файл " + file + " не существует, пропускаем");
//            System.err.println("  Ищется по пути " + filePath.toAbsolutePath());
//            System.err.println("  Текущая директория " + Paths.get(".").toAbsolutePath());
//            return;
//        }
//
//        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
//            String line;
//            int lineNumber = 0;
//
//            while((line = reader.readLine()) != null) {
//                lineNumber++;
//                line = line.trim();
//
//                if(line.isEmpty()) {
//                    continue;
//                }
//
//                classifyAndStore(line);
//            }
//        } catch (IOException e) {
//            System.err.println("Ошибка чтения файла " + e.getMessage());
//        }
//    }
//
//    private static void classifyAndStore(String line) {
//        if(isInteger(line)) {
//            integerCount++;
//            integers.add(line);
//
//            if(viewModeStats.equals("fullViewMode")) {
//                long value = Long.parseLong(line);
//                if(minInt == null || value < minInt) minInt = value;
//                if(maxInt == null || value > maxInt) maxInt = value;
//                sumInt += value;
//            }
//
//        } else if (isFloat(line)) {
//            floatCount++;
//            floats.add(line);
//
//            if(viewModeStats.equals("fullViewMode")) {
//                double value = Double.parseDouble(line);
//                if(minFloat == null || value < minFloat) minFloat = value;
//                if(maxFloat == null || value > maxFloat) maxFloat = value;
//                sumFloat += value;
//            }
//        } else {
//            stringCount++;
//            strings.add(line);
//
//            if(viewModeStats.equals("fullViewMode")) {
//                int stringLength = line.length();
//                if(minStringLength == null || stringLength < minStringLength) minStringLength = stringLength;
//                if(maxStringLength == null || stringLength > maxStringLength) maxStringLength = stringLength;
//            }
//        }
//    }
//
//    public static void writeResultsToFiles() {
//        Path outputDir = Paths.get(pathToFiles);
//        try {
//            Files.createDirectories(outputDir);
//        } catch (IOException err) {
//            System.err.println("Ошибка создания директории " + err.getMessage());
//            return;
//        }
//
//        String[] filenames = {
//                prefix + "integers.txt",
//                prefix + "floats.txt",
//                prefix + "strings.txt"
//        };
//
//        List<List<String>> dataLists = Arrays.asList(integers, floats, strings);
//
//        for (int i = 0; i < filenames.length; i++) {
//            if(dataLists.get(i).isEmpty()) {
//                System.out.println("Нет файлов для " + filenames[i] + ", файл не создается");
//                continue;
//            }
//
//            Path filePath = outputDir.resolve(filenames[i]);
//            try {
//                OpenOption[] options = appendMode ?
//                        new OpenOption[]{StandardOpenOption.CREATE, StandardOpenOption.APPEND} :
//                        new OpenOption[]{StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING};
//
//                Files.write(filePath, dataLists.get(i), StandardCharsets.UTF_8, options);
//
//                System.out.println("Создан файл: " + filePath);
//            } catch (IOException err) {
//                System.err.println("Ошибка при записи данных в файл " + filenames[i] + ": " + err.getMessage());
//            }
//        }
//    }
//
//    public static boolean isInteger(String line) {
//        Matcher matcher = INTEGER_PTRN.matcher(line);
//        if(!matcher.matches()) return false;
//
//        try {
//            Long.parseLong(line);
//            return true;
//        } catch (NumberFormatException e) {
//            return false;
//        }
//    }
//
//    public static boolean isFloat(String line) {
//        Matcher matcher = FLOAT_PTRN.matcher(line);
//        if(!matcher.matches()) return false;
//
//        try {
//            Double.parseDouble(line);
//            return true;
//        } catch (NumberFormatException e) {
//            return false;
//        }
//    }
//
//    public static void printStatistics() {
//        System.out.println("=== СТАТИСТИКА ===");
//
//        if (viewModeStats.equals("shortViewMode")) {
//            System.out.println("Краткая статистика:");
//            System.out.println("  Целые числа: " + integerCount);
//            System.out.println("  Дробные числа: " + floatCount);
//            System.out.println("  Строки: " + stringCount);
//        } else {
//            System.out.println("Полная статистика:");
//
//            System.out.println("Целые числа (" + integerCount + "):");
//            if (integerCount > 0) {
//                System.out.println("  Минимальное: " + minInt);
//                System.out.println("  Максимальное: " + maxInt);
//                System.out.println("  Сумма: " + sumInt);
//                System.out.println("  Среднее: " + (sumInt / (double)integerCount));
//            }
//
//            System.out.println("Дробные числа (" + floatCount + "):");
//            if (floatCount > 0) {
//                System.out.println("  Минимальное: " + minFloat);
//                System.out.println("  Максимальное: " + maxFloat);
//                System.out.println("  Сумма: " + sumFloat);
//                System.out.println("  Среднее: " + (sumFloat / floatCount));
//            }
//
//            System.out.println("Строки (" + stringCount + "):");
//            if (stringCount > 0) {
//                System.out.println("  Минимальная длина: " + minStringLength);
//                System.out.println("  Максимальная длина: " + maxStringLength);
//            }
//        }
//    }
//
//    public static void printHelp() {
//        System.out.println("Использование: java -jar program.jar [опции] файлы...");
//        System.out.println();
//        System.out.println("Опции:");
//        System.out.println("  -a             Добавлять в конец существующих файлов");
//        System.out.println("  -s             Краткая статистика (по умолчанию 'default' обязательно указывать!!!)");
//        System.out.println("  -f             Полная статистика");
//        System.out.println("  -o <путь>      Путь для выходных файлов");
//        System.out.println("  -p <префикс>   Префикс имен выходных файлов");
//        System.out.println("  -h     Показать эту справку");
//        System.out.println();
//        System.out.println("Примеры:");
//        System.out.println("  java -jar program.jar -s file1.txt file2.txt");
//        System.out.println("  java -jar program.jar -o /tmp -p result_ -f data.txt");
//    }
//}
