package org.benschwi;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;

public class XmlCollectionFieldTest {

    private record CollectionDummy(String name, List<Integer> numberList) {}

    @Test
    void testCreateXmlCollectionField() {
        assertThatNoException().isThrownBy(() -> new XmlCollectionField<CollectionDummy, Integer>("DummyName", CollectionDummy::numberList));
    }

    @Test
    void testCreateXmlCollectionField_nameIsNull() {
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> new XmlCollectionField<CollectionDummy, Integer>(null, CollectionDummy::numberList));
    }

    @Test
    void testCreateXmlCollectionField_extractorFunctionIsNull() {
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> new XmlCollectionField<CollectionDummy, Integer>("DummyName", null));
    }

    @Test
    void testCreateXmlCollectionField_childFieldsIsNull() {
        assertThatNoException().isThrownBy(() -> new XmlCollectionField<CollectionDummy, Integer>("DummyName", CollectionDummy::numberList, (XmlField<Integer, ?>) null));
    }

}
