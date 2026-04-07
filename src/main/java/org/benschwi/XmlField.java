package org.benschwi;

import java.util.Objects;
import java.util.function.Function;

/**
 *
 * @param name
 * @param valueExtractor
 * @param <T>
 */
public record XmlField<T>(String name, Function<T, Object> valueExtractor) {

    public XmlField {
        Objects.requireNonNull(name, "The name must not be null");
        Objects.requireNonNull(valueExtractor, "The extractor function must not be null.");
    }

}
