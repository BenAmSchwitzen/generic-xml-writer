package org.benschwi;

import java.util.Objects;
import java.util.function.Function;

/**
 * This class represents a mapping between an XML tag name and a function that extracts its corresponding value from a source object.
 * The mapping forms an XML element.
 *
 * @param name the tag name of the XML element
 * @param valueExtractor a function that extracts content for this element from a source object of type {@code T}
 * @param nullBehavior a constant that defines behavior when the extraction of a value returns null
 * @param childFields array of XML fields. It represents child element mappings. If the array is empty, the XML element is a leaf element. If the array contains at least one field, the XML element is a parent element.
 * @param <T> the type of the source object
 */
public record XmlField<T>(String name, Function<T, Object> valueExtractor, NullBehavior nullBehavior, XmlField<?>...childFields) {

    public XmlField {
        Objects.requireNonNull(name, "The name must not be null");
        Objects.requireNonNull(valueExtractor, "The extractor function must not be null.");
        Objects.requireNonNull(nullBehavior,"nullBehavior must not be null. Use the other constructor where the default value for nullBehavior is EMPTY_ELEMENT_VALUE");
    }
    
    public XmlField(String name, Function<T, Object> valueExtractor, XmlField<?>...childFields) {
        this(name, valueExtractor, NullBehavior.EMPTY_ELEMENT_VALUE, childFields);
    }

    public boolean hasChildFields() {
        return childFields != null && childFields.length > 0;
    }

    /**
     * The constant describes what should happen if the generated value from the valueExtractor function returns null
     */
    public enum NullBehavior {
        EMPTY_ELEMENT_VALUE,
        THROW_EXCEPTION
    }

}
