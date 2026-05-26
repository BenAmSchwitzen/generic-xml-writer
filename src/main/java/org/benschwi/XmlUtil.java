package org.benschwi;

import static org.benschwi.XmlFileConstants.XML_DECLARATION_TEXT;

public final class XmlUtil {

    private XmlUtil() {}

    static String getStartTag(String startTagName) {
        return "<" + startTagName + ">";
    }

    static String getEndTag(String endTagName) {
        return "</" + endTagName + ">" + "\n";
    }

    static String getElementWithValue(String elementName, Object value) {
        return getStartTag(elementName) + value.toString() + getEndTag(elementName);
    }

    static String getXMLEndContent(String rootElementName) {
        return "</" +
                rootElementName +
                ">";
    }

    static String getXMLStartContent(String comment, String rootElementName)  {
        var stb = new StringBuilder();
        stb.append(XML_DECLARATION_TEXT).append("\n");
        if(comment != null && !comment.isBlank()) {
            stb.append(formatAsXmlComment(comment));
        }
        return stb.append("<").append(rootElementName).append(">\n")
                .toString();
    }

    static String formatAsXmlComment(String comment) {
        StringBuilder stb = new StringBuilder();
        stb.append("<!--");

        for (int i = 0; i < comment.length() - 1; i++) {
            char currentChar = comment.charAt(i);
            if(currentChar == '-' && stb.charAt(stb.length() - 1) == '-') {
                stb.append(' ');
            }
            stb.append(currentChar);
        }
        char lastChar = comment.charAt(comment.length() - 1);

        if(lastChar == '-') {
            stb.append(' ');
        }
        stb.append(lastChar).append("-->").append("\n");
        return stb.toString();
    }

}
