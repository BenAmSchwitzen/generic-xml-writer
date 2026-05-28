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
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;

import static org.benschwi.XmlFileConstants.*;
import static org.benschwi.XmlUtil.*;

/**
 * An XML file writer that works with generic values
 * @param <A> the type of object from which any attribute can be chosen to appear as an entry in the generated XML file
 */
public class XmlFileWriter<A> {

    private static final Logger LOGGER = LoggerFactory.getLogger(XmlFileWriter.class);

    private final XmlNode<A>[] xmlNodes;

    /**
     * The instance that represents an XML file writer with a predefined set of attributes from instances of type {@code T}
     *
     * @param xmlNodes the fields whose values appear in each entry of the XML FILE
     */
    @SafeVarargs
    public XmlFileWriter(final XmlNode<A>...xmlNodes) {
        this.xmlNodes = getValidatedXmlFields(xmlNodes);
        LOGGER.debug("Initialized XmlFileWriter instance");
        LOGGER.debug("The predefined values are {}", Arrays.toString(xmlNodes));
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
    public void writeAndCreateXMLFile(final String destinationPath, final String fileName, final String rootElementName, final Collection<A> xmlElements) {
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
     * @param xmlEntries the entries of the XML file to be created
     *
     * @throws XMLFileWriterException if one of the values is null or empty
     */
    public void writeAndCreateXMLFile(final String destinationPath, final String fileName, final String rootElementName, final String comment, final int bufferSize, final Collection<A> xmlEntries) {
        LOGGER.debug("Start the process of creating and writing a new xml file");
        validateCreationValues(rootElementName, xmlEntries);
        validateBufferSize(bufferSize);
        Path filePath = getValidatedFilePath(destinationPath, fileName);
        LOGGER.debug("The path of the new file : {}", destinationPath);
        LOGGER.debug("The name of the new file : {}", fileName);
        LOGGER.debug("The name of the root element : {}", rootElementName);
        LOGGER.debug("The comment of the file : {}", comment == null ? "No comment given" : comment);
        LOGGER.debug("The amount of instances to be converted into xml elements : {}", xmlEntries.size());

        try(Writer writer = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(filePath), StandardCharsets.UTF_8), bufferSize)) {
            writer.write(getXMLStartContent(comment, rootElementName));

            for(A value : xmlEntries) {
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
            deleteCorruptedFile(filePath);

            throw new XMLFileWriterException("Could not write the xml file with predefined values", e);
        }

    }

    private static void deleteCorruptedFile(Path filePath) {
        try {
            Files.deleteIfExists(filePath);
            LOGGER.debug("Removal of file {} was successful", filePath);
        } catch(IOException deleteEx) {
            LOGGER.error("Could not delete corrupted file.", deleteEx);
        }
    }

    /**
     * Get the text representation of {@code xmlEntry} with all its child elements and properties being included
     *
     * @param xmlEntry the XML element to represent as text
     * @return a text representation of {@code xmlElement}
     */
    private String getXmlElement(A xmlEntry)  {
        String xmlElementName = xmlEntry.getClass().getSimpleName();
        var stb = new StringBuilder();

        stb.append(INDENTATION_LEVEL_1).append(getStartTag(xmlElementName)).append("\n");

        for(XmlNode<A> node : xmlNodes) {
            stb.append(constructElement(xmlEntry, node, INDENTATION_LEVEL_1));
        }

        stb.append(INDENTATION_LEVEL_1).append(getEndTag(xmlElementName));
        return stb.toString();
    }

    private String constructElement(Object xmlEntry, XmlNode<?> xmlNode, String indentationLevel) {
        var stb = new StringBuilder();
        String tagName = xmlNode.name();
        switch(xmlNode) {
            case XmlField<?, ?> xmlField -> {
                @SuppressWarnings("unchecked")
                Function<Object, Object> extractor = (Function<Object, Object>) xmlField.valueExtractor();
                Object rawValue = extractor.apply(xmlEntry);

                if(rawValue == null && xmlField.nullBehavior() == XmlField.NullBehavior.THROW_EXCEPTION) {
                    throw new XMLFileWriterException("The value of the field " + tagName + " is null. The null behavior of this field is set to THROW_EXCEPTION. Therefore, the writing process was stopped.");
                }

                stb.append(indentationLevel).append(INDENTATION_LEVEL_1).append(getStartTag(tagName));
                if(xmlField.hasChildFields()) {
                    stb.append("\n");
                    for(XmlNode<?> childNode : xmlField.childFields()) {
                        stb.append(constructElement(rawValue, childNode, indentationLevel + INDENTATION_LEVEL_1));
                    }
                        stb.append(indentationLevel).append(INDENTATION_LEVEL_1);
                    } else {
                        stb.append(rawValue != null ? rawValue.toString() : "");
                    }
                    stb.append(getEndTag(tagName));
                }
                case XmlCollectionField<?, ?> xmlCollectionField -> {
                    @SuppressWarnings({"rawtypes"})
                    Function extractor = (Function) xmlCollectionField.valueExtractor();
                    Collection<?> rawCollection = (Collection<?>) extractor.apply(xmlEntry);

                    stb.append(indentationLevel).append(INDENTATION_LEVEL_1).append(getStartTag(tagName)).append("\n");

                    for(Object value : rawCollection) {
                        if(xmlCollectionField.hasChildFields()) {
                            stb.append(indentationLevel).append(INDENTATION_LEVEL_2).append(getStartTag(xmlCollectionField.elementName())).append("\n");
                            for(XmlNode<?> childNode : xmlCollectionField.childFields()) {
                                stb.append(constructElement(value, childNode, indentationLevel + INDENTATION_LEVEL_2));
                            }
                            stb.append(indentationLevel).append(INDENTATION_LEVEL_2).append(getEndTag(xmlCollectionField.elementName()));
                        } else  {
                            stb.append(indentationLevel).append(INDENTATION_LEVEL_2).append(getElementWithValue(xmlCollectionField.elementName(), value));
                        }
                    }
                    stb.append(indentationLevel).append(INDENTATION_LEVEL_1).append(getEndTag(tagName));
                }
            }
        return stb.toString();
    }

    @SafeVarargs
    private XmlNode<A>[] getValidatedXmlFields(final XmlNode<A>...xmlNodes) {
        if(xmlNodes == null || xmlNodes.length < 1) {
            throw new XMLFileWriterException("xmlFields must not be null nor empty. The xml fields determine which values under which name of the given type T appear in the generated file");
        }
        return xmlNodes;
    }

    private void validateCreationValues(String rootElementName, Collection<A> xmlElements)  {
        if(rootElementName == null || rootElementName.isBlank() || xmlElements == null || xmlElements.isEmpty()) {
            throw new XMLFileWriterException("Validation of creation values failed. Each value must not be null and empty");
        }
    }

    private static Path getValidatedFilePath(String destinationPath, String fileName) {
        try {
            if(destinationPath == null || destinationPath.isBlank() || fileName == null || fileName.isBlank()) {
                throw new XMLFileWriterException("Validation of file values failed. The destination path and the name of the file must not be null");
            }
            return Path.of(destinationPath).resolve(fileName + "." + FILE_EXTENSION_NAME);
        } catch (Exception e) {
            throw new XMLFileWriterException("Validation of file values failed. Could not set up the file path", e);
        }

    }

    private static void validateBufferSize(int bufferSize) {
        if(bufferSize < 1) {
            throw new XMLFileWriterException("The size of the buffer must be greater than 1");
        }
    }

}
