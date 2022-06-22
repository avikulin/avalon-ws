package Utils.XMLTransform;

import DAL.Contracts.ReadOnlyRepository;
import DAL.DataEntities.Enums.DeviceType;
import DAL.DataEntities.Enums.OsiLayer;
import Utils.XMLTransform.Contracts.XTransformer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.ejb.EJB;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class XRepo<T> implements XTransformer<T> {
    private static final String PKEY_ATTR_NAME = "pkey";
    private static final String ROOT_CONTAINER_TAG = "data";
    private static final String REF_CONTAINER_TAG = "ref";
    private static final String REF_FIELD_TAG = "field";
    private static final String REF_COLLECTION = "collection";
    private static final String REF_TYPE_TAG = "type";

    private final Set<Class<?>> scalarTypes;
    private final Document xDocument;

    @EJB
    private ReadOnlyRepository<T> sourceRepo;

    public XRepo() {
        this.scalarTypes = new HashSet<>();
        this.scalarTypes.add(int.class);
        this.scalarTypes.add(long.class);
        this.scalarTypes.add(Integer.class);
        this.scalarTypes.add(Long.class);
        this.scalarTypes.add(String.class);
        this.scalarTypes.add(OsiLayer.class);
        this.scalarTypes.add(DeviceType.class);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
            this.xDocument = builder.newDocument();
            this.xDocument.setXmlStandalone(true);
        } catch (ParserConfigurationException e) {
            String msg = "Ошибка инициализации: " + e.getMessage();
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, msg, e);
            throw new IllegalStateException();
        }
    }

    public void serializeObject(Element xParent, Class<?> objClass, Object object) {
        if (object == null) return;

        // поле класса (объект сериализации) является коллекцией
        if (object instanceof Collection) {
            serializeCollectionReference(xParent, null, objClass, object);
            return;
        }

        // поле класса (объект сериализации) является скалярным типом
        Element xCurrent = this.xDocument.createElement(objClass.getName());
        Field[] fields = objClass.getDeclaredFields();
        for (Field f : fields) {
            String fieldName = f.getName();
            boolean accessibleState = f.isAccessible();
            f.setAccessible(true);
            Class<?> fieldType = f.getType();
            Object fieldValue = null;

            XmlTransient[] notSerializeFlag = f.getAnnotationsByType(XmlTransient.class);
            boolean isTransient = notSerializeFlag.length > 0;
            if (isTransient) continue;

            Id[] pkeyFlag = f.getAnnotationsByType(Id.class);
            boolean isPkey = pkeyFlag.length > 0;
            try {
                fieldValue = f.get(object);
            } catch (IllegalAccessException ise) {
                String msg = "Ошибка доступа к полям объекта сериализации: " + ise.getMessage();
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, msg, ise);
                throw new IllegalStateException(msg);
            }

            if (this.scalarTypes.contains(fieldType)) {
                String strValue = "";
                if (fieldValue != null) {
                    strValue = fieldValue.toString();
                }
                if (isPkey){
                    xCurrent.setAttribute(PKEY_ATTR_NAME, strValue);
                }
                xCurrent.setAttribute(f.getName(), strValue);
            } else { // поле (объект сериализации) является ссылочным типом
                boolean isCollection = fieldValue instanceof List;
                if (isCollection){
                   serializeCollectionReference(xCurrent, fieldName, fieldType, fieldValue);
                } else {
                    serializeScalarReference(xCurrent, fieldName, fieldType, fieldValue);
                }
            }
            f.setAccessible(accessibleState);
            xParent.appendChild(xCurrent);
        }
    }

    private void serializeScalarReference(Element xParent, String fieldName, Class<?> objClass, Object object){
        Element xRef = this.xDocument.createElement(REF_CONTAINER_TAG);
        xRef.setAttribute(REF_FIELD_TAG, fieldName);
        serializeObject(xRef, objClass, object);
        xParent.appendChild(xRef);
    }

    private void serializeCollectionReference(Element xParent,  String fieldName, Class<?> objClass, Object object){
        List objCollection = null;
        try {
            objCollection = (List) object;
        } catch (ClassCastException cce) {
            String msg = "Неподдерживаемый для сериализации в XML тип поля: " + objClass.getName();
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, msg, cce);
            throw new IllegalStateException(msg);
        }
        if (objCollection.size() == 0) return;
        Class<?> elementClass = objCollection.get(0).getClass();

        Element xHeader = this.xDocument.createElement(REF_COLLECTION);
        xHeader.setAttribute(REF_TYPE_TAG, objClass.getName());
        if (fieldName != null && !fieldName.isEmpty()){
            xHeader.setAttribute(REF_FIELD_TAG, fieldName);
        }

        objCollection.forEach(e -> {
            serializeObject(xHeader, elementClass, e);
        });

        xParent.appendChild(xHeader);
    }

    @Override
    public void setSource(ReadOnlyRepository<T> source) {
        this.sourceRepo = source;
    }

    @Override
    public void writeXML(OutputStream out) {
        try {
            Element xRoot = this.xDocument.createElement(ROOT_CONTAINER_TAG);
            List<T> data = this.sourceRepo.getAll();
            serializeObject(xRoot, sourceRepo.getEntityClass(), data);
            this.xDocument.appendChild(xRoot);
            this.xDocument.normalizeDocument();
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            DOMSource doms = new DOMSource(this.xDocument);
            StreamResult result = new StreamResult(out);
            transformer.transform(doms, result);
        } catch (TransformerException tfe) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Ошибка вывода XML-данных в поток", tfe);
        } finally {
            if (this.xDocument != null && this.xDocument.hasChildNodes()) {
                this.xDocument.removeChild(this.xDocument.getFirstChild());
            }
        }
    }

    @Override
    public List<T> readXML(InputStream in) {

        return null;
    }
}
