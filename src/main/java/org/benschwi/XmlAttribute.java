package org.benschwi;

import java.util.Objects;
import java.util.function.Function;

/**
 * This class represents an XML attribute being inside a tag.
 *
 * @param name the name of the attribute that appears in the tag
 * @param valueExtractor the function that extracts a value from a source object of type {@code A} and returns it as the value of the attribute name
 * @param <A> the type of the source object
 */
record XmlAttribute<A>(String name, Function<A, ?> valueExtractor) {
    public XmlAttribute {
        Objects.requireNonNull(name, "The name of the attribute must not be null");
        Objects.requireNonNull(valueExtractor, "The valueExtractor function must not be null");
    }
}
