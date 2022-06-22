package Utils.XMLParser;

import DAL.DataEntities.Enums.DeviceType;
import DAL.DataEntities.Enums.OsiLayer;
import Utils.XMLParser.Contracts.GenericListFactory;
import Utils.XMLParser.Contracts.ObjectFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DomFactoryImpl implements ObjectFactory {
    private static final String PKEY_ATTR_NAME = "pkey";
    private static final String REF_CONTAINER_TAG = "ref";
    private static final String REF_FIELD_TAG = "field";
    private static final String REF_COLLECTION = "collection";
    private static final String REF_TYPE_TAG = "type";

    private final Map<Class<?>, Map<String, Object>> objStore;

    public DomFactoryImpl(){
        this.objStore = new HashMap<>();
    }

    private void initType(Class<?> typeName){
        this.objStore.computeIfAbsent(typeName, t-> new HashMap<>());
    }

    private void setFieldValue(Field ownerField, Object object, Object value){
        Objects.requireNonNull(ownerField, "Не задано поле-владелец значения");
        Objects.requireNonNull(object, "Не задан объект-контейнер");
        Objects.requireNonNull(value, "Не задано присваиваемое значение");
        try {
            boolean accessMode = ownerField.isAccessible();
            ownerField.setAccessible(true);
            ownerField.set(object, value);
            ownerField.setAccessible(accessMode);
        } catch (IllegalAccessException e) {
            String msg = String.format("Ошибка задания значения для поля \"%s\": %s",
                    ownerField.getName(),
                    e.getMessage());
            throw new IllegalStateException(msg);
        }
    }

    private void processRefNode(Field ownerField, Object object, Element refNode) throws IllegalStateException {
        Objects.requireNonNull(refNode, "Не заданы XML-данные");
        Object value = null;
        try {
            value = createItem((Element)refNode.getFirstChild());
        } catch (IllegalStateException | NullPointerException e) {
            String msg = String.format("Нарушение XML-разметки. " +
                                       "Ошибка обработки ссылки на объект \"%s\" для поля \"%s\" в тэге \"%s\": %s",
                                        object,
                                        ownerField.getName(),
                                        refNode.getTextContent(),
                                        e.getMessage());
            throw new IllegalStateException(msg);
        }
        setFieldValue(ownerField, object, value);
    }

    private void processRefCollection(Field ownerField, Object object, Element refNode) throws IllegalStateException {
        Objects.requireNonNull(refNode, "Не заданы XML-данные");
        String typeName = refNode.getAttribute(REF_TYPE_TAG);
        Objects.requireNonNull(typeName,
                "Нарушение XML-разметки. Тип ссылочной коллекции не задан в тэге: " +
                        refNode.getTextContent());
        NodeList itemNodes = refNode.getChildNodes();
        if (itemNodes.getLength() == 0) return;

        try {
            Class<?> clazz = Class.forName(typeName);
            GenericListFactory listFactory = new ListFactoryImpl();
            List container = listFactory.create(clazz);
            for (int i=0; i<itemNodes.getLength(); i++){
                container.add(createItem((Element)itemNodes.item(i)));
            }
            setFieldValue(ownerField, object, container);
        } catch (ClassNotFoundException | IllegalStateException | NullPointerException e) {
            String msg = String.format("Ошибка созданые ссылочной коллекции для поля \"%s\": %s",
                                        ownerField.getName(),
                                        e.getMessage());
            throw new IllegalStateException(msg);
        }
    }

    @Override
    public Object createItem(Element xmlData) throws IllegalStateException, NullPointerException {
        Objects.requireNonNull(xmlData, "В данные XML-элемента передано пустое значение");
        String className = xmlData.getTagName();
        Class<?> clazz = null;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        initType(clazz);
        String pkey = xmlData.getAttribute(PKEY_ATTR_NAME);
        Object item;
        if (pkey!=null && !pkey.isEmpty()){
            Map<String, Object> idxObjById = this.objStore.get(clazz);
            item = idxObjById.get(pkey);
            if (item == null){
                Constructor<?> ctor = null;
                try {
                    ctor = clazz.getConstructor();
                    Objects.requireNonNull(ctor,
                                   "Не найден конструктор по умолчанию для типа: "+clazz.getName());

                    item = ctor.newInstance();
                    for (Field f: clazz.getDeclaredFields()){
                        String value = xmlData.getAttribute(f.getName());
                        if (value != null && !value.isEmpty()){
                            setFieldValue(f, item, getScalarValue(value, f.getType()));
                        }
                    }
                } catch (NoSuchMethodException | InstantiationException
                        | InvocationTargetException | IllegalAccessException e) {
                    String msg = String.format("Ошибка создание элемента класса \"%s\"", className);
                    throw new IllegalStateException(msg);
                }

                NodeList childNodes = xmlData.getChildNodes();
                for (int i=0; i < childNodes.getLength(); i++){ // если дочерних узлов нет - цикл не выполнится ни разу.
                    Node refNode = childNodes.item(i);
                    if (refNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element refElement = (Element) refNode;
                        String ownerFieldName = refElement.getAttribute(REF_FIELD_TAG);
                        Objects.requireNonNull(ownerFieldName,
                                "Нарушение XML-разметки. Название ссылочного поля не задано в тэге: " +
                                        refNode.getTextContent());
                        Field ownerField = null;
                        try {
                            ownerField = clazz.getDeclaredField(ownerFieldName);
                        } catch (NoSuchFieldException e) {
                            String msg = String.format("В классе \"%s\" не определено поле \"%s\"",
                                                       clazz.getName(),
                                                       ownerFieldName);
                            throw new IllegalStateException(msg);
                        }
                        if (refElement.getTagName().equals(REF_CONTAINER_TAG)){
                            processRefNode(ownerField, item, refElement);
                        }
                        if (refElement.getTagName().equals(REF_COLLECTION)){
                            processRefCollection(ownerField, item, refElement);
                        }
                    }
                }
            }
        } else {
            String msg = String.format("В данных XML не задан обязательный параметр \"%s\"", PKEY_ATTR_NAME);
            throw new IllegalStateException(msg);
        }
        return item;
    }

    Object getScalarValue(String s, Class<?> clazz){
        if (clazz.equals(String.class)) return s; // идентичное отображение

        if (clazz.equals(int.class) || clazz.equals(Integer.class)){
            return Integer.parseInt(s);
        }

        if (clazz.equals(long.class) || clazz.equals(Long.class)){
            return Long.parseLong(s);
        }

        if (clazz.equals(double.class) || clazz.equals(Double.class)){
            return Double.parseDouble(s);
        }

        if (clazz.equals(OsiLayer.class)){
            return OsiLayer.valueOf(s);
        }

        if (clazz.equals(DeviceType.class)){
            return DeviceType.valueOf(s);
        }

        String msg = String.format("Неподдерживаемый скалярный тип \"%s\" в значении \"%s\"", clazz.getName(), s);
        throw new IllegalStateException(msg);
    }
}
