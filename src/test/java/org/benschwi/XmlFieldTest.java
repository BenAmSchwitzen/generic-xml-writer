package org.benschwi;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class XmlFieldTest {

    @Test
    void testCreateXmlField() {
        assertThatNoException().isThrownBy(() -> new XmlField<>("name", Dummy::name));
    }

    @Test
    void testCreateXmlField_nameIsNull() {
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> new XmlField<>(null, Dummy::name));
    }

    @Test
    void testCreateXmlField_converterFunctionIsNull() {
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> new XmlField<>("name", null));
    }

    @Test
    void testCreateXmlField_NullBehaviorIsNull() {
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> new XmlField<>("name", Dummy::name, (XmlField.NullBehavior)null));
    }

    @Test
    void testCreateXmlField_childFieldsIsNull() {
        assertThatNoException().isThrownBy(() -> new XmlField<>("name", Dummy::name, XmlField.NullBehavior.THROW_EXCEPTION, (XmlField<String, ?>[]) null));
    }

    @Test
    void testCreateXmlField_childFieldsIsEmpty() {
        XmlField[] emptyArray = new XmlField[3];
        assertThatNoException().isThrownBy(() -> new XmlField<Dummy, String>("name", Dummy::name, XmlField.NullBehavior.THROW_EXCEPTION, emptyArray));
    }

    @Test
    void testCreateXmlField_EMPTY_ELEMENT_VALUE_IsTheStandardNullBehavior() {
        XmlField<Dummy, Integer> xmlField = new XmlField<>("age", Dummy::age);
        XmlField.NullBehavior expectedValue = XmlField.NullBehavior.EMPTY_ELEMENT_VALUE;

        assertThat(xmlField.nullBehavior()).isEqualByComparingTo(expectedValue);
    }

    private static record Dummy(String name, Integer age, boolean isHealthy) {}
}
