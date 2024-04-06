import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.*;

public class PopularNames {
    public static void main(String[] args) {
        try {
            sortAndSaveNames();
            readAndPrintNames();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void sortAndSaveNames() {
        try {
            File inputFile = new File("Popular_Baby_Names_NY.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getElementsByTagName("row");
            List<BabyName> babyNames = new ArrayList<>();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String ethnicGroup = element.getElementsByTagName("ethcty").item(0).getTextContent();
                    if (ethnicGroup.equals("WHITE NON HISP")) {
                        String name = element.getElementsByTagName("nm").item(0).getTextContent();
                        String gender = element.getElementsByTagName("gndr").item(0).getTextContent();
                        int count = Integer.parseInt(element.getElementsByTagName("cnt").item(0).getTextContent());
                        int rank = Integer.parseInt(element.getElementsByTagName("rnk").item(0).getTextContent());
                        babyNames.add(new BabyName(name, gender, count, rank));
                    }
                }
            }
            Collections.sort(babyNames, Comparator.comparingInt(BabyName::getRank));
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = dbf.newDocumentBuilder();
            Document newDoc = docBuilder.newDocument();
            Element rootElement = newDoc.createElement("BabyNames");
            newDoc.appendChild(rootElement);
            for (BabyName babyName : babyNames) {
                Element nameElement = newDoc.createElement("Name");
                nameElement.setAttribute("gender", babyName.getGender());
                nameElement.setAttribute("count", String.valueOf(babyName.getCount()));
                nameElement.setAttribute("rank", String.valueOf(babyName.getRank()));
                nameElement.setTextContent(babyName.getName());
                rootElement.appendChild(nameElement);
            }
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(newDoc);
            StreamResult result = new StreamResult(new File("Sorted_Baby_Names.xml"));
            transformer.transform(source, result);
            System.out.println("Сортування та збереження виконано успішно!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void readAndPrintNames() {
        try {
            File inputFile = new File("Sorted_Baby_Names.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();

            System.out.println("Список відсортованих імен:");

            NodeList nodeList = doc.getElementsByTagName("Name");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String name = element.getTextContent();
                    String gender = element.getAttribute("gender");
                    int count = Integer.parseInt(element.getAttribute("count"));
                    int rank = Integer.parseInt(element.getAttribute("rank"));

                    System.out.println("Ім'я: " + name);
                    System.out.println("Гендер: " + gender);
                    System.out.println("Кількість: " + count);
                    System.out.println("Рейтинг: " + rank);
                    System.out.println();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
class BabyName {
    private String name;
    private String gender;
    private int count;
    private int rank;
    public BabyName(String name, String gender, int count, int rank) {
        this.name = name;
        this.gender = gender;
        this.count = count;
        this.rank = rank;
    }
    public String getName() {
        return name;
    }
    public String getGender() {
        return gender;
    }
    public int getCount() {
        return count;
    }
    public int getRank() {
        return rank;
    }
}