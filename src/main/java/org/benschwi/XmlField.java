package org.benschwi;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * This class represents a mapping between an XML tag name and a function that extracts its corresponding value from a source object.
 * The mapping forms an XML element.
 *
 * @param name the tag name of the XML element
 * @param valueExtractor a function that extracts content for this element from a source object of type {@code A}
 * @param nullBehavior a constant that defines behavior when the extraction of a value returns null
 * @param childFields array of XML fields. It represents child element mappings. If the array is empty, the XML element is a leaf element. If the array contains at least one field, the XML element is a parent element.
 * @param <A> the type of the source object
 * @param <B> the type of the value extracted by the valueExtractor function
 */
public record XmlField<A, B>(String name, Function<A, B> valueExtractor, NullBehavior nullBehavior, List<XmlAttribute<A>> attributes, XmlNode<B>...childFields) implements XmlNode<A> {

    @SafeVarargs
    public XmlField {
        Objects.requireNonNull(name, "The name must not be null");
        Objects.requireNonNull(valueExtractor, "The extractor function must not be null.");
        Objects.requireNonNull(nullBehavior,"nullBehavior must not be null. Use the other constructor where the default value for nullBehavior is EMPTY_ELEMENT_VALUE");

        if(childFields != null) {
            for(var childField : childFields) {
                Objects.requireNonNull(childField, "The childFields array that revolves around the element value being inside the tag with name " + name + " must not contain null values");
            }
        }
    }

    @SafeVarargs
    public XmlField(String name, Function<A, B> valueExtractor, XmlField<B, ?>...childFields) {
        this(name, valueExtractor, NullBehavior.EMPTY_ELEMENT_VALUE, new ArrayList<>(), childFields);
    }

    public boolean hasChildFields() {
        return childFields != null && childFields.length > 0;
    }

    @Override
    public XmlField<A, B> setAttribute(String name, Function<A, ?> valueExtractor) {
        attributes.add(new XmlAttribute<>(name, valueExtractor));
        return this;
    }

    /**
     * The constant describes what should happen if the generated value from the valueExtractor function returns null
     */
    public enum NullBehavior {
        EMPTY_ELEMENT_VALUE,
        THROW_EXCEPTION
    }

}
