import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.util.HashSet;
import java.util.Set;

public class NationalGroups {
    public static void main(String[] args) {
        try {
            String xmlFilePath = "Popular_Baby_Names_NY.xml";
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            NationalGroupsHandler handler = new NationalGroupsHandler();
            saxParser.parse(xmlFilePath, handler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    static class NationalGroupsHandler extends DefaultHandler {
        private Set<String> nationalGroups = new HashSet<>();
        private boolean isEthnicityTag = false;
        private StringBuilder currentTagContent = new StringBuilder();
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if ("ethcty".equals(qName)) {
                isEthnicityTag = true;
                currentTagContent.setLength(0);
            }
        }
        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            if (isEthnicityTag) {
                currentTagContent.append(ch, start, length);
            }
        }
        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if ("ethcty".equals(qName)) {
                isEthnicityTag = false;
                String ethnicGroup = currentTagContent.toString().trim();
                if (!ethnicGroup.isEmpty()) {
                    nationalGroups.add(ethnicGroup);
                }
            }
        }
        @Override
        public void endDocument() throws SAXException {
            System.out.println("Національні групи, представлені в документі:");
            for (String group : nationalGroups) {
                System.out.println(group);
            }
        }
    }
}