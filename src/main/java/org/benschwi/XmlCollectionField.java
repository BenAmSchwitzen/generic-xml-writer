package org.benschwi;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;

/**
 * This class represents a mapping between an XML tag name and a function that extracts its corresponding value from a source object.
 * The mapping forms an XML list element.
 *
 * @param name the tag name of the XML element
 * @param elementName the tag name of each element in the collection
 * @param valueExtractor a function that extracts content for this element from a source object of type {@code A}
 * @param childFields array of XML fields. It represents child element mappings. If the array is empty, the XML element is a leaf element. If the array contains at least one field, the XML element is a parent element.
 * @param <A> the type of the source object
 * @param <B> the type of the elements in the collection extracted by the valueExtractor function
 */
public record XmlCollectionField<A, B>(String name, String elementName, Function<A, Collection<B>> valueExtractor, XmlNode<B>...childFields) implements XmlNode<A> {

    @SafeVarargs
    public XmlCollectionField {
        Objects.requireNonNull(name, "The name must not be null");
        Objects.requireNonNull(elementName, "The elementName must not be null");
        Objects.requireNonNull(valueExtractor, "The extractor function must not be null.");

        if(childFields != null) {
            for(var childField : childFields) {
                Objects.requireNonNull(childField, "The childFields array that revolves around the element value being inside the tag with name " + name + " must not contain null values");
            }
        }
    }

    public boolean hasChildFields() {
        return childFields != null && childFields.length > 0;
    }

}
