package io.github.nmaksimka.filterfiles.stats;

import java.util.OptionalInt;

public final class StringStatistics {

    private long count;
    private int shortest = Integer.MAX_VALUE;
    private int longest;

    public void accept(String value) {
        var length = value.codePointCount(0, value.length());
        count++;
        shortest = Math.min(shortest, length);
        longest = Math.max(longest, length);
    }

    public long count() {
        return count;
    }

    public OptionalInt shortest() {
        return count == 0 ? OptionalInt.empty() : OptionalInt.of(shortest);
    }

    public OptionalInt longest() {
        return count == 0 ? OptionalInt.empty() : OptionalInt.of(longest);
    }
}
