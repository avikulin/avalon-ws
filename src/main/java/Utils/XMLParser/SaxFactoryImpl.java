package Utils.XMLParser;

import DAL.DataEntities.Enums.DeviceType;
import DAL.DataEntities.Enums.OsiLayer;
import Utils.XMLParser.Contracts.GenericListFactory;
import Utils.XMLParser.Contracts.ObjectFactory;
import org.w3c.dom.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class SaxFactoryImpl implements ObjectFactory {
    private static final String PKEY_ATTR_NAME = "pkey";
    private static final String REF_CONTAINER_TAG = "ref";
    private static final String REF_FIELD_TAG = "field";
    private static final String REF_COLLECTION = "collection";
    private static final String REF_TYPE_TAG = "type";

    private final Map<Class<?>, Map<String, Object>> objStore;

    private final Deque<Field> refFieldStack;
    private final Deque<ReferenceType> refTypeStack;
    private final Deque<Object> objStack;
    private final Deque<Class<?>> objClassStack;

    public SaxFactoryImpl(){
        this.objStore = new HashMap<>();
        this.refFieldStack = new ArrayDeque<>();
        this.refTypeStack = new ArrayDeque<>();
        this.objStack = new ArrayDeque<>();
        this.objClassStack = new ArrayDeque<>();
    }

    private void initType(Class<?> typeName){
        this.objStore.computeIfAbsent(typeName, t-> new HashMap<>());
    }

    // ok
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

    private void processRefElement(Field ownerField, Object object, Element refNode) throws IllegalStateException {
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



    private void registerReference(String fieldName, String fieldType, ReferenceType refType){
        Class<?> clazz = null;
        Field ownerField = null;
        try {
            clazz = this.objClassStack.peek();
            ownerField = clazz.getDeclaredField(fieldName);
            this.refFieldStack.push(ownerField);
            this.refTypeStack.push(refType);
        } catch (NoSuchFieldException e) {
            String msg = String.format("В классе \"%s\" не определено поле \"%s\"",
                    clazz.getName(),
                    fieldName);
            throw new IllegalStateException(msg);
        }
    }

    private void closeReference(ReferenceType refType){
        switch (refType){
            case COLLECTION:{
                                this.objStack.pop();
                                this.objClassStack.pop();
                                this.refFieldStack.pop();
                                this.refTypeStack.pop();
                                break;
                            }
            case SCALAR:{
                                this.refFieldStack.pop();
                                this.refTypeStack.pop();
                                break;
            }
        }
    }

    private Object getReferencedObject(){
        Object obj = this.objStack.peek();
        Class<?> objClass = this.objClassStack.peek();
        Field f = this.refFieldStack.peek();
        ReferenceType refType = this.refTypeStack.peek();

        Object res = null;
        try{
            res = f.get(obj);
            if (res == null && refType == ReferenceType.COLLECTION){
                GenericListFactory listFactory = new ListFactoryImpl();
                res = listFactory.create(objClass);
            }
        } catch (IllegalAccessException e) {
            String msg = String.format("Ошибка получение ссылки на значениеполя \"%s\": %s",
                    f.getName(),
                    e.getMessage());
        }
        return res;
    }

    //ok
    private Object createItem(String tag, Map<String, String> attrs) throws IllegalStateException, NullPointerException {
        Objects.requireNonNull(tag, "В имени тэг передано пустое значение");
        Objects.requireNonNull(attrs, "В коллекции атрибутов передано пустое значение");

        String className = tag;
        Class<?> clazz = null;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            String msg = String.format("Для типа \"%s\" не определен конструктор по умолчанию", className);
            throw new IllegalStateException(msg);
        }
        initType(clazz);
        String pkey = attrs.get(PKEY_ATTR_NAME);
        Object item;
        if (pkey != null && !pkey.isEmpty()) {
            Map<String, Object> idxObjById = this.objStore.get(clazz);
            item = idxObjById.get(pkey);
            if (item == null) {
                Constructor<?> ctor = null;
                try {
                    ctor = clazz.getConstructor();
                    Objects.requireNonNull(ctor,
                            "Не найден конструктор по умолчанию для типа: " + clazz.getName());

                    item = ctor.newInstance();
                    for (Field f : clazz.getDeclaredFields()) {
                        String value = attrs.get(f.getName());
                        if (value != null && !value.isEmpty()) {
                            setFieldValue(f, item, getScalarValue(value, f.getType()));
                        }
                    }
                } catch (NoSuchMethodException | InstantiationException
                        | InvocationTargetException | IllegalAccessException e) {
                    String msg = String.format("Ошибка создание элемента класса \"%s\"", className);
                    throw new IllegalStateException(msg);
                }

            }
        } else {
            String msg = String.format("В данных XML не задан обязательный параметр \"%s\"", PKEY_ATTR_NAME);
            throw new IllegalStateException(msg);
        }
        ReferenceType refType = this.refTypeStack.peek();

        if (refType == ReferenceType.COLLECTION){
            Object currentObj = this.objStack.peek();
            if (!(currentObj instanceof List)) {
                this.objStack.pop(); //этот объект уже полностью обработан и находится в контейнере
                this.objClassStack.pop(); // поэтому просто удаляем его из стека
            }

            try {
                List container = (List) getReferencedObject();
                container.add(item);

            } catch (Exception e) {
                String msg = String.format("В исходном файла задана неправильная " +
                                           "последовательность элементов. " +
                                           "Поле \"%s\" объекта \"%s\" не является коллекцией",
                                            this.refFieldStack.peek().getName(),
                                            this.objClassStack.peek().getName());
                throw new IllegalStateException(msg);
            }

        }
        if (refType == ReferenceType.SCALAR){
            Field f = this.refFieldStack.peek();
            Object obj = this.objStack.peek();
            setFieldValue(f, obj, item);
        }

        this.objStack.push(item);
        this.objClassStack.push(clazz);
        return item;
    }

    @Override
    public Object createItem(Element xmlData) throws IllegalStateException, NullPointerException {
        Objects.requireNonNull(xmlData, "Передано пустое значение в параметре XML-данных");
        String tagName = xmlData.getTagName();
        NamedNodeMap attrsRaw = xmlData.getAttributes();
        Map<String, String> attrsClean = new HashMap<>();

        for (int i=0; i<attrsRaw.getLength();i++) {
            Attr attr = (Attr)attrsRaw.item(i);
            attrsClean.put(attr.getName(), attr.getValue());
        }
        Object item = createItem(tagName, attrsClean);

        NodeList childNodes = xmlData.getChildNodes();
        for (int i=0; i < childNodes.getLength(); i++){ // если дочерних узлов нет - цикл не выполнится ни разу.
            Node refNode = childNodes.item(i);
            if (refNode.getNodeType() == Node.ELEMENT_NODE) {
                Element refElement = (Element) refNode;
                String ownerFieldName = refElement.getAttribute(REF_FIELD_TAG);
                Objects.requireNonNull(ownerFieldName,
                        "Нарушение XML-разметки. Название ссылочного поля не задано в тэге: " +
                                refNode.getTextContent());

                if (refElement.getTagName().equals(REF_CONTAINER_TAG)){
                    processRefElement(ownerField, item, refElement);
                }
                if (refElement.getTagName().equals(REF_COLLECTION)){
                    processRefCollection(ownerField, item, refElement);
                }
            }
        }
        return null;
    }

    // ok
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
