package org.benschwi;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;

public class XmlNestedCollectionFieldTest {

    private record CollectionDummy(String name, List<List<Integer>> numberLists) {}

    @Test
    void testCreateXmlCollectionField() {
        assertThatNoException().isThrownBy(() -> new XmlNestedCollectionField<CollectionDummy, List<Integer>>("DummyName", CollectionDummy::numberLists));
    }

    @Test
    void testCreateXmlCollectionField_nameIsNull() {
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() ->new XmlNestedCollectionField<CollectionDummy, List<Integer>>(null, CollectionDummy::numberLists));
    }

    @Test
    void testCreateXmlCollectionField_extractorFunctionIsNull() {
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> new XmlNestedCollectionField<CollectionDummy, List<Integer>>("DummyName", null));
    }

    @Test
    void testCreateXmlCollectionField_childFieldsIsNull() {
        assertThatNoException().isThrownBy(() -> new XmlNestedCollectionField<CollectionDummy, List<Integer>>("DummyName", CollectionDummy::numberLists, (XmlNode<List<Integer>> []) null));
    }

    @Test
    void testCreateXmlCollectionField_childFieldsArrayContainsAtLeastOneNullElement() {
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() ->new XmlNestedCollectionField<CollectionDummy, List<Integer>>("DummyName", CollectionDummy::numberLists, (XmlNode<List<Integer>>) null));
    }

}
