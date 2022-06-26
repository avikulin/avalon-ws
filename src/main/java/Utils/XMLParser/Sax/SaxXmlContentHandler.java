package Utils.XMLParser.Sax;

import Utils.ObjectFactory.ObjectFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static Utils.ObjectFactory.Constants.Constants.*;

public class SaxXmlContentHandler<T> extends DefaultHandler {
    private ObjectFactory<T> objectFactory;

    public SaxXmlContentHandler(ObjectFactory<T> objectFactory) {
        Objects.requireNonNull(objectFactory, "Передана пустая ссылка (null) на фабрику объектов");
        this.objectFactory = objectFactory;
    }

    public void setObjectFactory(ObjectFactory<T> objectFactory){

        this.objectFactory = objectFactory;
    }

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
        objectFactory.initDocument();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        switch (qName){
            case REF_ROOT_NODE: return;
            case REF_CONTAINER_TAG: {
                String fieldName = attributes.getValue(REF_FIELD_TAG);
                String fieldType = attributes.getValue(REF_TYPE_TAG);
                objectFactory.registerElementRef(fieldName, fieldType);
                return;
            }
            case REF_COLLECTION: {
                String fieldName = attributes.getValue(REF_FIELD_TAG);
                String fieldType = attributes.getValue(REF_TYPE_TAG);
                objectFactory.registerCollectionRef(fieldName, fieldType);
                return;
            }
            default:{
                Map<String, String> attributesMap = new HashMap<>();
                for (int i=0; i<attributes.getLength(); i++){
                    String key = attributes.getLocalName(i);
                    String value = attributes.getValue(i);
                    attributesMap.put(key, value);
                }
                objectFactory.registerItem(qName, attributesMap);
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        switch (qName){
            case REF_ROOT_NODE: return;
            case REF_CONTAINER_TAG: {
                objectFactory.processRefElement();
                return;
            }
            case REF_COLLECTION: {
                objectFactory.processRefCollection();
                return;
            }
            default: objectFactory.finalizeItem();
        }
    }
}
