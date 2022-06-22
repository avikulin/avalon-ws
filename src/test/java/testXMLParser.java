import DAL.DataEntities.Registers.Equipment;
import Utils.XMLTransform.ObjectFactory;
import Utils.XMLTransform.ObjectFactoryImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class testXMLParser {
    public static final File fileXml = new File("equipment_units_data.xml");
    public static void main(String[] args) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        try {
            ObjectFactory objectFactory = new ObjectFactoryImpl();
            Document document = builder.parse(fileXml);

            Element root = document.getDocumentElement();

            Element mainCollection = (Element) root.getFirstChild();
            NodeList listItems = mainCollection.getChildNodes();

            for (int i=0; i < listItems.getLength(); i++){
                Equipment equipment = (Equipment) objectFactory.createItem((Element)listItems.item(i));
                System.out.println(equipment);
                System.out.println("------------------------");
                System.out.println();
            }
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
