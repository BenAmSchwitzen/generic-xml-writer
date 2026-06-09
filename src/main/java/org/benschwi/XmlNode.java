package org.benschwi;

import java.util.function.Function;

public sealed interface XmlNode<A> permits XmlField, XmlCollectionField, XmlNestedCollectionField {

    String name();

    boolean hasChildFields();

    XmlNode<A> setAttribute(String name, Function<A, ?> valueExtractor);

    Function<A, ?> valueExtractor();

}
