package org.benschwi;

/**
 * This class stores values that revolve about an XML file and its content
 */
public final class XmlFileConstants {

    // Why not protected ? protected ist nur im selberm package und unterklassen
    // durrch final calss gibt es keine unterklassenm
    // und kein keyword heisst nur im selben package (package private)

    static final String INDENTATION_LEVEL_0 = "";
    static final String INDENTATION_LEVEL_1 = "    ";
    static final String INDENTATION_LEVEL_2 = "        ";

    static final String FILE_EXTENSION_NAME = "xml";
    static final String XML_DECLARATION_TEXT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

    private XmlFileConstants() {}


}
