package io.github.nmaksimka.filterfiles.stats;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Optional;

public final class NumberStatistics {

    private long count;
    private BigDecimal min;
    private BigDecimal max;
    private BigDecimal sum = BigDecimal.ZERO;

    public void accept(BigDecimal value) {
        count++;
        min = min == null || value.compareTo(min) < 0 ? value : min;
        max = max == null || value.compareTo(max) > 0 ? value : max;
        sum = sum.add(value);
    }

    public long count() {
        return count;
    }

    public Optional<BigDecimal> min() {
        return Optional.ofNullable(min);
    }

    public Optional<BigDecimal> max() {
        return Optional.ofNullable(max);
    }

    public BigDecimal sum() {
        return sum;
    }

    public Optional<BigDecimal> average() {
        if (count == 0) {
            return Optional.empty();
        }
        return Optional.of(sum.divide(BigDecimal.valueOf(count), MathContext.DECIMAL128));
    }
}
