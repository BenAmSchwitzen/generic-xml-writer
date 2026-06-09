package org.benschwi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public record XmlNestedCollectionField<A, B>(String name, Function<A, Collection<B>> valueExtractor, List<XmlAttribute<A>> attributes, XmlNode<B>...childFields) implements XmlNode<A> {

    @SafeVarargs
    public XmlNestedCollectionField {
        Objects.requireNonNull(name, "The name must not be null");
        Objects.requireNonNull(valueExtractor, "The extractor function must not be null.");

        if(childFields != null) {
            for(var childField : childFields) {
                Objects.requireNonNull(childField, "The childFields array that revolves around the element value being inside the tag with name " + name + " must not contain null values");
            }
        }
    }

    @SafeVarargs
    public XmlNestedCollectionField(String name, Function<A, Collection<B>> valueExtractor, XmlNode<B>...childFields) {
        this(name, valueExtractor, new ArrayList<>(), childFields);
    }

    public boolean hasChildFields() {
        return childFields != null && childFields.length > 0;
    }

    @Override
    public XmlNestedCollectionField<A, B> setAttribute(String name, Function<A, ?> valueExtractor) {
        attributes.add(new XmlAttribute<>(name, valueExtractor));
        return this;
    }

}
