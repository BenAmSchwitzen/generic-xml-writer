package org.benschwi;

import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.*;

public class XmlFieldTest {

    @Test
    void testCreateXmlField() {
        assertThatNoException().isThrownBy(() -> new XmlField<Dummy>("name", Dummy::name));
    }

    @Test
    void testCreateXmlField_nameIsNull() {
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> new XmlField<Dummy>(null, Dummy::name));
    }

    @Test
    void testCreateXmlField_converterFunctionIsNull() {
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> new XmlField<Dummy>("name", null));
    }

    private static record Dummy(String name, Integer age, boolean isHealthy) {}
}
