package io.github.nmaksimka.filterfiles.core;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class LineClassifierTest {

    @ParameterizedTest
    @ValueSource(strings = {"0", "45", "-0", "+5", "-100500", "1234567890123456789", "99999999999999999999999999"})
    void integers(String value) {
        assertThat(LineClassifier.classify(value)).isEqualTo(LineType.INTEGER);
    }

    @ParameterizedTest
    @ValueSource(strings = {"3.1415", "-0.001", "1.528535047E-25", ".5", "1.", "1e10", "-2E+3", "+.5e-1", "1e999"})
    void floats(String value) {
        assertThat(LineClassifier.classify(value)).isEqualTo(LineType.FLOAT);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Lorem ipsum", "1,5", "0x10", "1e", "e5", ".", "-", "+", "1.2.3", "12abc", "NaN",
            "Infinity", "١٢٣", "1 000"})
    void strings(String value) {
        assertThat(LineClassifier.classify(value)).isEqualTo(LineType.STRING);
    }
}
