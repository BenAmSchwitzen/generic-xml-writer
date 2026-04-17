package org.benschwi;

import java.util.Objects;
import java.util.function.Function;

/**
 * This class represents a mapping between an XML tag name and a function that extracts its corresponding value from a source object.
 * The mapping forms an XML element.
 *
 * @param name the tag name of the XML element
 * @param valueExtractor a function that extracts content for this element from a source object of type {@code T}
 * @param <T> the type of the source object
 */
public record XmlField<T>(String name, Function<T, Object> valueExtractor) {

    public XmlField {
        Objects.requireNonNull(name, "The name must not be null");
        Objects.requireNonNull(valueExtractor, "The extractor function must not be null.");

        // und im writer wird standardmöässig toString() für converision genutzt. als default gut, vlltr kann nutzer aber auch selberr fangeben welche methode verrwdnent wird.
        // sie muss nur allerdrings auch einen String returnen

        // Ob name empty sein darf kann man später per extra Parameter angeben klnnen, eventuell OptionsEnum
    }

}
