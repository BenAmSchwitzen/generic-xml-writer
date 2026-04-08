package org.benschwi;

import java.util.Objects;
import java.util.function.Function;

/**
 * This field represents a tag name in combination with a corresponding function that generates a proper value to the given name
 *
 * @param name the name of an XML tag
 * @param valueExtractor the function
 * @param <T>
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
