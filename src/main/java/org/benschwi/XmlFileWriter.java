package org.benschwi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;

import static org.benschwi.XmlFileConstants.*;

/**
 * An XML file writer that works with generic values
 * @param <T> the type of object from which any attribute can be chosen to appear as an entry in the generated XML file
 */
public class XmlFileWriter<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(XmlFileWriter.class);

    private final String[] xmlFieldNames;
    private final Function<T, Object>[] xmlFields;

    /**
     * The instance that represents an XML file writer with a predefined set of attributes from instances of type {@code T}
     *
     * @param xmlFields the fields whose values appear in each entry of the XML FILE
     * @param xmlFieldNames the names of the fields defined in {@code xmlFields}
     */
    @SafeVarargs
    public XmlFileWriter(final String[] xmlFieldNames, final Function<T, Object>...xmlFields) {
        validateXmlFields(xmlFieldNames, xmlFields);
        this.xmlFieldNames = xmlFieldNames;
        this.xmlFields = xmlFields;
        LOGGER.debug("Initialized XmlFileWriter instance");
        LOGGER.debug("The predefined values are {}", Arrays.toString(xmlFieldNames));
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
        writeAndCreateXMLFile(destinationPath, fileName, rootElementName, null, xmlElements);
    }

    /**
     * Create a new XML with name {@code fileName} in {@code destinationPath}
     *
     * @param destinationPath the path where the new XML file will be stored
     * @param fileName the name of the file to be created
     * @param rootElementName the name of the root element which appears at the top and bottom of the XML file
     * @param comment an optional comment that appears at the top of the XML file
     * @param xmlElements the entries of the XML file to be created
     *
     * @throws XMLFileWriterException if one of the values is null or empty
     */
    public void writeAndCreateXMLFile(final String destinationPath, final String fileName, final String rootElementName, final String comment, final Collection<T> xmlElements) {
        LOGGER.debug("Start the process of creating and writing a new xml file");
        validateCreationValues(rootElementName, xmlElements);
        Path filePath = getValidatedFilePath(destinationPath, fileName);
        LOGGER.debug("The path of the new file : {}", destinationPath);
        LOGGER.debug("The name of the new file : {}", fileName);
        LOGGER.debug("The name of the root element : {}", rootElementName);
        LOGGER.debug("The comment of the file : {}", comment == null ? "No comment given" : comment);
        LOGGER.debug("The amount of instances to be converted into xml elements : {}", xmlElements.size());

        try(Writer writer = Files.newBufferedWriter(filePath)) {
            writer.write(getXMLStartContent(comment, rootElementName));

            for(T value : xmlElements) {
                writer.write(getXMLElement(value));
            }

            writer.write(getXMLEndContent(rootElementName));
        } catch (IOException e) {
            throw new XMLFileWriterException("Could not write the xml file with predefined values", e);
        }

    }

    private String getXMLElement(T xmlElement)  {
        String xmlElementName = xmlElement.getClass().getSimpleName();
        var stb = new StringBuilder();

        stb.append(INDENTATION_LEVEL_1).append(getStartTag(xmlElementName)).append("\n");
        stb.append(writeXmlElementContent(xmlElement));
        stb.append(INDENTATION_LEVEL_1).append(getEndTag(xmlElementName)).append("\n");

        return stb.toString();

    }

    private String writeXmlElementContent(T xmlElement) {
        var stb = new StringBuilder();

        for(int i = 0; i < xmlFields.length; i++) {
            String elementTag = xmlFieldNames[i];

            stb.append(INDENTATION_LEVEL_2).append(getStartTag(elementTag));

            Function<T, Object> converterFunction = xmlFields[i];
            Object elementInstance = converterFunction.apply(xmlElement);
            if (elementInstance instanceof Collection<?> e) {
                stb.append(getRecursiveCollectionEntryContent(e, 2, stb));
            } else {
                stb.append(String.valueOf(elementInstance.toString()));
            }
            stb.append(getEndTag(elementTag)).append("\n");
        }
        return stb.toString();
    }

    @SafeVarargs
    private void validateXmlFields(final String[] xmlFieldNames, final Function<T, Object>... xmlFields) {
        if(xmlFieldNames == null || xmlFieldNames.length < 1) {
            throw new XMLFileWriterException("xmlFieldNames must not be null. The field names determine the text that appears in the element tags");
        }
        if(xmlFields == null || xmlFields.length < 1) {
            throw new XMLFileWriterException("xmlFields must not be null. The xml fields determine which values of the given type T appear in the generated file");
        }
        if(xmlFieldNames.length != xmlFields.length) {
            throw new XMLFileWriterException("The size of xmlFieldNames and xmlFields must match to ensure a properly generated xml file");
        }
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
        return new StringBuilder()
                .append("</")
                .append(rootElementName)
                .append(">")
                .toString();
    }












    private StringBuilder getRecursiveCollectionEntryContent(Collection<?> collection, int indentationLevel, StringBuilder stb) {
        // rekurion und dann vllt noch setLevel methpde anbieten die standardmössi2 zwei ist dann wird heit einfach so liste gerpintted
        // mit isinstanceofCollection
        // logge wenn nullk und dann wird einfach übersprungen
        // zu viele StringBuilder Instanzen?
        return null;
    }

    public static void main(String[] args) {
        XmlFileWriter<Object> xmlFileWriter = new XmlFileWriter<>(new String[]{"1"}, Object::toString);
        xmlFileWriter.writeAndCreateXMLFile(null, null, null, null);
    }

}
