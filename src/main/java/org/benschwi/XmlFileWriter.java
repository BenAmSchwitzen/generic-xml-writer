package org.benschwi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Struct;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;

import static org.benschwi.XmlFileConstants.*;

/**
 * An XML file writer that works with generic values
 * @param <T> the type of object from which any attribute can be chosen to appear as an entry in the generated XML file
 */
public class XmlFileWriter<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(XmlFileWriter.class);

    private final XmlField<T>[] xmlFields;

    /**
     * The instance that represents an XML file writer with a predefined set of attributes from instances of type {@code T}
     *
     * @param xmlFields the fields whose values appear in each entry of the XML FILE
     */
    @SafeVarargs
    public XmlFileWriter(final XmlField<T>...xmlFields) {
        this.xmlFields = getValidatedXmlFields(xmlFields);
        LOGGER.debug("Initialized XmlFileWriter instance");
        LOGGER.debug("The predefined values are {}", Arrays.toString(xmlFields));
    }

    /**
     * Create a new XML with name {@code fileName} in {@code destinationPath}
     *
     * @param destinationPath the path where the new XML file will be stored
     * @param fileName the name of the file to be created
     * @param rootElementName the name of the root element which appears at the top and bottom of the XML file
     * @param xmlElements the entries of the XML file to be created
     *
     * @throws XMLFileWriterException if one of the values is null or empty
     */
    public void writeAndCreateXMLFile(final String destinationPath, final String fileName, final String rootElementName, final Collection<T> xmlElements) {
        writeAndCreateXMLFile(destinationPath, fileName, rootElementName, null, 8192, xmlElements);
    }

    /**
     * Create a new XML with name {@code fileName} in {@code destinationPath}
     *
     * @param destinationPath the path where the new XML file will be stored
     * @param fileName the name of the file to be created
     * @param rootElementName the name of the root element which appears at the top and bottom of the XML file
     * @param comment an optional comment that appears at the top of the XML file
     * @param bufferSize the amount of bytes that the writer stores before writing to a file
     * @param xmlElements the entries of the XML file to be created
     *
     * @throws XMLFileWriterException if one of the values is null or empty
     */
    public void writeAndCreateXMLFile(final String destinationPath, final String fileName, final String rootElementName, final String comment, final int bufferSize, final Collection<T> xmlElements) {
        LOGGER.debug("Start the process of creating and writing a new xml file");
        validateCreationValues(rootElementName, xmlElements);
        validateBufferSize(bufferSize);
        Path filePath = getValidatedFilePath(destinationPath, fileName);
        LOGGER.debug("The path of the new file : {}", destinationPath);
        LOGGER.debug("The name of the new file : {}", fileName);
        LOGGER.debug("The name of the root element : {}", rootElementName);
        LOGGER.debug("The comment of the file : {}", comment == null ? "No comment given" : comment);
        LOGGER.debug("The amount of instances to be converted into xml elements : {}", xmlElements.size());

        try(Writer writer = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(filePath), StandardCharsets.UTF_8), bufferSize)) {
            writer.write(getXMLStartContent(comment, rootElementName));

            for(T value : xmlElements) {
                if(value != null) {
                    writer.write(getXmlElement(value));
                    LOGGER.debug("Conversion of instance from type {} into an XML element was successful", value.getClass().getSimpleName());
                } else {
                    LOGGER.debug("Conversion of instance into an XML element was skipped. The instance was null");
                }

            }
            writer.write(getXMLEndContent(rootElementName));
            LOGGER.debug("the creation of a new XML file was successful");
        } catch(Exception e) {
            LOGGER.error("Writing process failed.");
            LOGGER.error("Start the process of deleting a corrupted file if there is any");

            try {
                Files.deleteIfExists(filePath);
                LOGGER.debug("Removal of file {} was successful", filePath);
            } catch(IOException deleteEx) {
                LOGGER.error("Could not delete corrupted file.", deleteEx);
            }
            throw new XMLFileWriterException("Could not write the xml file with predefined values", e);
        }

    }

    /**
     * Get the text representation of {@code xmlElement} with all its child elements and properties being included
     *
     * @param xmlElement the XML element to represent as text
     * @return a text representation of {@code xmlElement}
     */
    private String getXmlElement(T xmlElement)  {
        String xmlElementName = xmlElement.getClass().getSimpleName();

        return INDENTATION_LEVEL_1 + getStartTag(xmlElementName) + "\n"
                + buildXmlElement(xmlElement) +
                INDENTATION_LEVEL_1 + getEndTag(xmlElementName) + "\n";
    }

    /**
     * Build the text representation of the body of {@code xmlElement}
     *
     * @param xmlElement the element to be represented
     * @return the text body of {@code xmlElement}
     */
    private String buildXmlElement(T xmlElement) {
        var stb = new StringBuilder();

        for(XmlField<T> xmlField : xmlFields) {
            String elementTag = xmlField.name();
            Object rawValue = xmlField.valueExtractor().apply(xmlElement);

            stb.append(INDENTATION_LEVEL_2).append(getStartTag(elementTag));

            if(rawValue == null && xmlField.nullBehavior() == XmlField.NullBehavior.THROW_EXCEPTION) {
                throw new XMLFileWriterException("The writing process has failed. The extractor function for the tag with name " + xmlField.name() + " has generated a null value");
            }
            stb.append(getFullElementConstruct(rawValue));

            stb.append(getEndTag(elementTag)).append("\n");
        }
        return stb.toString();
    }

    private String getFullElementConstruct(Object rawValue) {
        return rawValue instanceof Collection<?> e ? "\n" + getXmlListElementContent(e, INDENTATION_LEVEL_2) : getXmlElementContent(rawValue);
    }

    private String getXmlListElementContent(Collection<?> collection, String indentationLevel) {
        StringBuilder stb = new StringBuilder();

        for(Object instance : collection) {
            // hier wieder if instace instanceofe collection und dann rekursiv
            stb.append(indentationLevel).append(INDENTATION_LEVEL_1).append(getStartTag(LIST_ITEM_TAG_NAME));
            stb.append(getXmlElementContent(instance));
            stb.append(getEndTag(LIST_ITEM_TAG_NAME)).append("\n");
        }
        stb.append(indentationLevel);
        return stb.toString();
    }

    private String getXmlElementContent(Object rawValue) {
        return rawValue != null ? rawValue.toString() : "";
    }

    private String getStartTag(String startTagName) {
        return "<" + startTagName + ">";
    }

    private String getEndTag(String endTagName) {
        return "</" + endTagName + ">";
    }

    private String getXMLStartContent(String comment, String rootElementName)  {
        var stb = new StringBuilder();
        stb.append(XML_DECLARATION_TEXT).append("\n");
        if(comment != null && !comment.isBlank()) {
            stb.append(comment).append("\n");
        }
        return stb.append("<").append(rootElementName).append(">\n")
                .toString();
    }

    private String getXMLEndContent(String rootElementName) {
        return "</" +
                rootElementName +
                ">";
    }

    @SafeVarargs
    private XmlField<T>[] getValidatedXmlFields(final XmlField<T>...xmlFields) {
        if(xmlFields == null || xmlFields.length < 1) {
            throw new XMLFileWriterException("xmlFields must not be null nor empty. The xml fields determine which values under which name of the given type T appear in the generated file");
        }
        return xmlFields;
    }

    private void validateCreationValues(String rootElementName, Collection<T> xmlElements)  {
        if(rootElementName == null || rootElementName.isBlank() || xmlElements == null || xmlElements.isEmpty()) {
            throw new XMLFileWriterException("Validation of creation values failed. Each value must not be null and empty");
        }
    }

    private Path getValidatedFilePath(String destinationPath, String fileName) {
        try {
            if(destinationPath == null || destinationPath.isBlank() || fileName == null || fileName.isBlank()) {
                throw new XMLFileWriterException("Validation of file values failed. The destination path and the name of the file must not be null");
            }
            return Path.of(destinationPath).resolve(fileName + "." + FILE_EXTENSION_NAME);
        } catch (Exception e) {
            throw new XMLFileWriterException("Validation of file values failed. Could not set up the file path", e);
        }

    }

    private void validateBufferSize(int bufferSize) {
        if(bufferSize < 1) {
            throw new XMLFileWriterException("The size of the buffer must be greater than 1");
        }
    }

    public static void main(String[] args) {
        //TODO rekurion und dann vllt noch setLevel methpde anbieten die standardmössi2 zwei ist dann wird heit einfach so liste gerpintted
        //TODO alle intellij Problems anschauen
        // TODO : coverage tests
        // TODO : Check mit AI, ob noch Felder fehlen
        //TODO : Add second method das statt file datei schriebt einfach nur XML Strign returned klönnte irgenwie flush deaktievren in BufferedfWriter, aber dann ist dtr noch systme clals glaube, will ja keine nsystme cll
        // TODO : Schaue was Files.newBufferedWriter(filePath) unter der Haube mmacht. Wahrscheinlich dass gleiche, was ich jketyrt machen werde oder im NOW gemacht  habe
        // TODO : Fix build warnings
    }

}
