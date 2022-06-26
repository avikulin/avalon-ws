package Utils.XMLParser.Dom;

import Utils.ObjectFactory.ObjectFactory;
import Utils.XMLParser.XParser;
import org.w3c.dom.*;

import javax.ejb.EJB;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.util.*;

import static Utils.ObjectFactory.Constants.Constants.*;

public class DomXmlParserSeqImpl<T> implements XParser<T> {
    private File fileXml;

    @EJB
    private ObjectFactory<T> objectFactory;

    @Override
    public void setSource(File inputFile){
        if (inputFile == null){
            throw new IllegalStateException("Передана пустая строка с именем файла");
        }
        this.fileXml = inputFile;
    }

    @Override
    public void setObjectFactory(ObjectFactory<T> objectFactory){
        Objects.requireNonNull(objectFactory, "Передана пустая ссылка (null) на фабрику объектов");
        this.objectFactory = objectFactory;
    }

    private void checkElementNode(Node xmlNode) throws IllegalStateException {
        if (xmlNode == null && xmlNode.getNodeType() != Node.ELEMENT_NODE){
            throw new IllegalStateException("Нарушение XML-разметки. " +
                    "В ссылке на элемент передан некорректный тип тэга: " +
                    xmlNode.getTextContent());
        }
    }

    private void createItem(Element xmlData) throws IllegalStateException, NullPointerException {
        Objects.requireNonNull(xmlData, "Передано пустое значение в параметре XML-данных");
        String tagName = xmlData.getTagName();
        NamedNodeMap attrsRaw = xmlData.getAttributes();
        Map<String, String> attrsClean = new HashMap<>();

        for (int i=0; i<attrsRaw.getLength();i++) {
            Attr attr = (Attr)attrsRaw.item(i);
            attrsClean.put(attr.getName(), attr.getValue());
        }
        this.objectFactory.registerItem(tagName, attrsClean);
        processReferences(xmlData);
        this.objectFactory.finalizeItem();
    }

    private void processReferences(Element parentNode){
        Objects.requireNonNull(parentNode, "Ссылка на головной узел должна быть задана");
        NodeList childNodes = parentNode.getChildNodes();
        for (int i=0; i < childNodes.getLength(); i++){ // если дочерних узлов нет - цикл не выполнится ни разу.
            Node refNode = childNodes.item(i);
            checkElementNode(refNode);
            if (refNode.getNodeType() == Node.ELEMENT_NODE) {
                Element refElement = (Element) refNode;
                String ownerFieldName = refElement.getAttribute(REF_FIELD_TAG);
                if(ownerFieldName == null && ownerFieldName.isEmpty()) {
                    throw new IllegalStateException("Нарушение XML-разметки. " +
                            "Название ссылочного поля не задано в тэге: " +
                            refNode.getTextContent());
                }
                String ownerFieldType = refElement.getAttribute(REF_TYPE_TAG);
                if (ownerFieldType == null && ownerFieldType.isEmpty()) {
                    throw new IllegalStateException("Нарушение XML-разметки. " +
                            "Тип ссылочного поля не задан в тэге: " +
                            refNode.getTextContent());
                }
                if (refElement.getTagName().equals(REF_CONTAINER_TAG)){
                    this.objectFactory.registerElementRef(ownerFieldName, ownerFieldType);
                    Node refItemNode = refNode.getFirstChild();
                    checkElementNode(refItemNode);
                    createItem((Element) refItemNode);
                    this.objectFactory.processRefElement();
                }
                if (refElement.getTagName().equals(REF_COLLECTION)){
                    NodeList itemNodes = refNode.getChildNodes();
                    if (itemNodes.getLength() == 0) return;
                    this.objectFactory.registerCollectionRef(ownerFieldName, ownerFieldType);
                    for (int j=0; j<itemNodes.getLength(); j++){
                        Node collectionItemNode = itemNodes.item(j);
                        checkElementNode(collectionItemNode);
                        createItem((Element)collectionItemNode);
                    }
                    this.objectFactory.processRefCollection();
                }
            }
        }
    }

    @Override
    public List<T> getData() throws IllegalStateException {
        this.objectFactory.initDocument();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new IllegalStateException("Ошибка создания \"DocumentBuilder\": "+e.getMessage());
        }

        try {
            Document document = builder.parse(this.fileXml);
            Element root = document.getDocumentElement();
            processReferences(root);
        } catch (Exception e) {
            throw new IllegalStateException("Ошибка в обработке XML-данных: " + e.getMessage());
        }
        return this.objectFactory.getData();
    }
}
