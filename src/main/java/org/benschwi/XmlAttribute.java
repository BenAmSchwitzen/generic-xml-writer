package org.benschwi;

import java.util.Objects;
import java.util.function.Function;

record XmlAttribute<A>(String name, Function<A, ?> valueExtractor) {
    public XmlAttribute {
        Objects.requireNonNull(name, "The name of the attribute must not be null");
        Objects.requireNonNull(valueExtractor, "The valueExtractor function must not be null");
    }
}
