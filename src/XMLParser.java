import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.XMLConstants;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.Set;

public class XMLParser {
    public static void main(String[] args) {
        try {
            String xmlFilePath = "Popular_Baby_Names_NY.xml";
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            BabyNamesHandler handler = new BabyNamesHandler();
            saxParser.parse(xmlFilePath, handler);
            Set<String> tags = handler.getTags();
            System.out.println("Отримані теги:");
            for (String tag : tags) {
                System.out.println(tag);
            }
            System.out.println();
            String xsdSchema = generateXSDSchema(tags);
            String xsdFilePath = "Popular_Baby_Names_NY.xsd";
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(xsdFilePath))) {
                writer.write(xsdSchema);
            }
            System.out.println("Згенерована XSD схема збережена у файлі: " + xsdFilePath);
            validateXML(xmlFilePath, xsdFilePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    static class BabyNamesHandler extends DefaultHandler {
        Set<String> tags = new HashSet<>();
        private StringBuilder currentTagContent = new StringBuilder();
        private int entryCount = 0;
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if (!"row".equals(qName)) {
                tags.add(qName);
                currentTagContent.setLength(0);
            }
        }
        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            currentTagContent.append(ch, start, length);
        }
        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (entryCount < 2) {
                if ("row".equals(qName)) {
                    entryCount++;
                    if (entryCount > 0) {
                        System.out.println();
                    }

                } else if (tags.contains(qName)) {
                    String translatedTagName = translateTagName(qName);
                    System.out.println(translatedTagName + ": " + currentTagContent);
                }
            }
        }
        private String translateTagName(String tagName) {
            switch (tagName) {
                case "brth_yr":
                    return "Рік народження";
                case "gndr":
                    return "Стать";
                case "ethcty":
                    return "Етнічна приналежність";
                case "nm":
                    return "Ім'я дитини";
                case "cnt":
                    return "Кількість дітей з цим іменем";
                case "rnk":
                    return "Рейтинг імені";
                default:
                    return tagName;
            }
        }
        public Set<String> getTags() {
            return tags;
        }
    }
    public static String generateXSDSchema(Set<String> tags) {
        StringBuilder xsdSchema = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xsdSchema.append("<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">\n");
        xsdSchema.append("<xs:element name=\"response\">\n");
        xsdSchema.append("<xs:complexType>\n");
        xsdSchema.append("<xs:sequence>\n");
        xsdSchema.append("<xs:element name=\"row\" maxOccurs=\"unbounded\">\n");
        xsdSchema.append("<xs:complexType>\n");
        xsdSchema.append("<xs:sequence>\n");
        for (String tag : tags) {
            xsdSchema.append("<xs:element name=\"").append(tag).append("\" type=\"xs:string\" />\n");
        }
        xsdSchema.append("</xs:sequence>\n");
        xsdSchema.append("</xs:complexType>\n");
        xsdSchema.append("</xs:element>\n");
        xsdSchema.append("</xs:sequence>\n");
        xsdSchema.append("</xs:complexType>\n");
        xsdSchema.append("</xs:element>\n");
        xsdSchema.append("</xs:schema>");

        return xsdSchema.toString();
    }
    public static void validateXML(String xmlFilePath, String xsdFilePath) throws Exception {
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = factory.newSchema(new StreamSource(xsdFilePath));
        Validator validator = schema.newValidator();
        Source source = new StreamSource(xmlFilePath);
        try {
            validator.validate(source);
            System.out.println("XSD файл валідний.");
        } catch (SAXException e) {
            System.out.println("XSD файл не валідний:");
            System.out.println(e.getMessage());
        }
    }
}
