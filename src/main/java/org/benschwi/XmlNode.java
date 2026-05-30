package org.benschwi;

import java.util.function.Function;

public sealed interface XmlNode<A> permits XmlField, XmlCollectionField {

    String name();

    boolean hasChildFields();

    Function<A, ?> valueExtractor();

}
