package io.github.nmaksimka.filterfiles.files;

import io.github.nmaksimka.filterfiles.core.LineType;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ResultWriter implements Closeable {

    private final Path outputDir;
    private final String prefix;
    private final boolean append;
    private final PrintStream err;
    private final Map<LineType, BufferedWriter> writers = new EnumMap<>(LineType.class);
    private final Set<LineType> failed = EnumSet.noneOf(LineType.class);
    private final List<Path> written = new ArrayList<>();

    public ResultWriter(Path outputDir, String prefix, boolean append, PrintStream err) {
        this.outputDir = outputDir;
        this.prefix = prefix;
        this.append = append;
        this.err = err;
    }

    public void write(LineType type, String line) {
        if (failed.contains(type)) {
            return;
        }
        try {
            var writer = writers.get(type);
            if (writer == null) {
                writer = open(type);
                writers.put(type, writer);
            }
            writer.write(line);
            writer.newLine();
        } catch (IOException e) {
            fail(type, e);
        }
    }

    public boolean hasErrors() {
        return !failed.isEmpty();
    }

    public List<Path> writtenFiles() {
        return List.copyOf(written);
    }

    @Override
    public void close() {
        writers.forEach((type, writer) -> {
            try {
                writer.close();
            } catch (IOException e) {
                fail(type, e);
            }
        });
        writers.clear();
    }

    private BufferedWriter open(LineType type) throws IOException {
        var file = outputDir.resolve(prefix + type.fileName());
        if (!outputDir.toString().isEmpty()) {
            Files.createDirectories(outputDir);
        }
        var needsLineBreak = append && endsWithoutLineBreak(file);
        OpenOption[] options = append
                ? new OpenOption[]{StandardOpenOption.CREATE, StandardOpenOption.APPEND}
                : new OpenOption[]{StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING};
        var writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8, options);
        if (needsLineBreak) {
            writer.newLine();
        }
        written.add(file);
        return writer;
    }

    private static boolean endsWithoutLineBreak(Path file) throws IOException {
        if (!Files.isRegularFile(file) || Files.size(file) == 0) {
            return false;
        }
        try (SeekableByteChannel channel = Files.newByteChannel(file)) {
            var last = ByteBuffer.allocate(1);
            channel.position(channel.size() - 1).read(last);
            return last.get(0) != '\n';
        }
    }

    private void fail(LineType type, IOException e) {
        if (failed.add(type)) {
            err.println("Не удалось записать " + prefix + type.fileName() + ": " + describe(e));
        }
    }

    static String describe(IOException e) {
        return e.getMessage() == null ? e.getClass().getSimpleName() : e.getClass().getSimpleName() + " " + e.getMessage();
    }
}
