package Utils.ObjectFactory;

import Utils.ObjectFactory.Filter.XFilter;
import Utils.GenericListFactory.GenericListFactory;
import Utils.GenericListFactory.ArrayListFactoryImpl;
import Utils.ObjectFactory.Enums.ReferenceType;
import Utils.ObjectFactory.Holders.ObjHolder;
import Utils.ObjectFactory.Holders.RefHolder;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static Utils.ObjectFactory.Constants.Constants.*;
import Utils.ObjectFactory.Helpers.Reflector;

import javax.ejb.Stateless;
import javax.xml.bind.annotation.XmlAttribute;

@Stateless
public class ObjectFactoryImpl<T> implements ObjectFactory<T> {
    private static final String DATA_STORE_FIELD_NAME = "dataStore";
    private final Map<Class<?>, Map<String, Object>> objCache;
    private final Deque<ObjHolder> objStack;
    private final Deque<RefHolder> refStack;
    private final List<T> dataStore;

    private int depthLevel;
    private boolean matchStatus;
    private XFilter filter;

    public ObjectFactoryImpl(){
        this.objCache = new HashMap<>();
        this.objStack = new ArrayDeque<>();
        this.refStack = new ArrayDeque<>();
        this.dataStore = new ArrayList<>();
        this.depthLevel = 0;
        this.matchStatus = true;
    }

    private void initTypeInCache(Class<?> typeName){
        this.objCache.computeIfAbsent(typeName, t-> new HashMap<>());
    }

    private Field getMainDataStoreField(){
        Field field = null;
        try {
            field = this.getClass().getDeclaredField(DATA_STORE_FIELD_NAME);
        } catch (NoSuchFieldException ignore){}
        return field;
    }

    private void checkContainerFieldMetadata(String fieldName, String fieldType){
        if (fieldName == null || fieldName.isEmpty()){
            String msg = String.format("Ошибка в структуре XML-документа. " +
                            "Имя поля-контейнера должно быть задано в теге <%s> в атрибуте \"%s\"",
                    REF_CONTAINER_TAG,
                    REF_FIELD_TAG);
            throw new IllegalStateException(msg);
        }
        if (fieldType == null || fieldType.isEmpty()){
            String msg = String.format("Ошибка в структуре XML-документа. " +
                            "Имя поля-контейнера должно быть задано в теге <%s> в атрибуте \"%s\"",
                    REF_CONTAINER_TAG,
                    REF_TYPE_TAG);
            throw new IllegalStateException(msg);
        }
    }

    @Override
    public void initDocument() {
        this.objCache.clear();
        this.objStack.clear();
        this.refStack.clear();
        this.dataStore.clear();
        this.depthLevel = 0;
        this.matchStatus = true;
    }

    @Override
    public void setFilter(XFilter filter){
        this.filter = filter;
    }

    @Override
    public void processRefElement() throws IllegalStateException {
        RefHolder currentRef = null;
        try {
            currentRef = this.refStack.peek();
            if (currentRef == null || currentRef.getReferenceType() != ReferenceType.SCALAR){
                throw new IllegalStateException();
            }

            //присваиваем ссылку на последний элемент стэка полю предпоследнего.
            ObjHolder value = this.objStack.pop();
            RefHolder refHolder = this.refStack.pop();
            Reflector.setFieldValue(refHolder, value.getRefObject());

            this.depthLevel--; //уменьшаем глубину иерархии на -1
        } catch (IllegalStateException | NullPointerException | NoSuchElementException e) {
            String msg = null;
            if (currentRef != null) {
                msg = String.format("Ошибка в структуре XML-документа. " +
                                    "Ожидается ссылка на элемент <%s>в поле \"%s\" объекта \"%s\" (%s): ",
                                    currentRef.getReferencedObjectClass().getName(),
                                    currentRef.getContainerFieldRef().getName(),
                                    currentRef.getContainerObject(),
                                    currentRef.getContainerClass().getName());
            } else {
                msg = String.format("Ошибка в структуре XML-документа. " +
                                    "Найдет закрывающий тэг </%s>, которому не соответвует открывающий тэг <%s>.",
                                    REF_CONTAINER_TAG,REF_CONTAINER_TAG);;
            }
            throw new IllegalStateException(msg);
        }

    }

    @Override
    public void processRefCollection() throws IllegalStateException {
        try{
            RefHolder ref = this.refStack.peek();
            Object container = ref.getContainerObject();

            while (true) {
                ObjHolder item = this.objStack.peek();
                if (item.getRefObject() == container) { //сравнение по ссылке - все ОК
                    break; // все дочерние элементы выше по стэку уже обработаны.
                }
                try {
                    List collection = (List) ref.getReferencedObject();
                    collection.add(item.getRefObject());
                } catch (Exception e) {
                    String msg = String.format("Ошибка в структуре XML-документа. Задана неправильная " +
                                               "последовательность элементов. " +
                                               "Поле \"%s\" объекта \"%s\" не является коллекцией",
                                                ref.getContainerFieldRef().getName(),
                                                ref.getContainerObject());
                    throw new IllegalStateException(msg);
                }
                this.objStack.pop(); //удаляем из стэка обработанный элемент.
            }
            this.refStack.pop(); //удаляем из стека обработанную ссылку на коллекцию.

            this.depthLevel--; //уменьшаем глубину иерархии на -1
        } catch (NullPointerException | NoSuchElementException e) {
            String msg = String.format("Ошибка в структуре XML-документа. " +
                                       "Найдет закрывающий тэг </%s>, которому не соответвует открывающий тэг <%s>.",
                                       REF_COLLECTION,REF_COLLECTION);
            throw new IllegalStateException(msg);
        }
    }

    @Override
    public void registerCollectionRef(String fieldName, String fieldType) throws IllegalStateException {
        checkContainerFieldMetadata(fieldName, fieldType);
        if (fieldName.equals(REF_ROOT_NODE)){
            RefHolder ref = new RefHolder(this,
                                          this.getClass(),
                                          getMainDataStoreField(),
                                          ReferenceType.COLLECTION);
            this.refStack.push(ref);
            this.objStack.push(new ObjHolder(this, this.getClass()));
            return;
        }

        Class<?> clazz = null;
        try {
            ObjHolder objReference = this.objStack.peek();
            Object object = objReference.getRefObject();
            clazz = objReference.getRefClass();
            Field ownerField = clazz.getDeclaredField(fieldName);
            RefHolder ref = new RefHolder(object, clazz, ownerField, ReferenceType.COLLECTION);
            this.refStack.push(ref);

            Class<?> referencedType = Class.forName(fieldType);
            List collectionRef = (List) ownerField.get(object);
            if (collectionRef == null){
                GenericListFactory listFactory = new ArrayListFactoryImpl();
                collectionRef = listFactory.create(referencedType);
            }
            // обработка фильтрации
            this.depthLevel++; //увеличиваем глубину иерархии на +1
        } catch (NoSuchFieldException | NullPointerException | IllegalAccessException e) {
            String msg = String.format("В классе \"%s\" не определено поле \"%s\"", clazz.getName(), fieldName);
            throw new IllegalStateException(msg);
        } catch (NoSuchElementException e) {
            String msg = String.format("Ошибка в структуре XML-документа. " +
                         "Найдет открывающий тэг <%s>, перед которым нет тэга элемента-владельца.", REF_CONTAINER_TAG);
            throw new IllegalStateException(msg);
        } catch (ClassNotFoundException e) {
            String msg = String.format("Ошибка в структуре XML-элемента. " +
                                        "Указанный в аттрибуте \"%s\" тэга <%s> тип \"%s\" не зарегистрирован в системе.",
                                        REF_TYPE_TAG, REF_COLLECTION, fieldName);
            throw new IllegalStateException(msg);
        }

    }

    @Override
    public void registerElementRef(String fieldName, String fieldType) throws IllegalStateException {
        checkContainerFieldMetadata(fieldName, fieldType);
        Class<?> clazz = null;
        try {
            ObjHolder objReference = this.objStack.peek();
            Object object = objReference.getRefObject();
            clazz = objReference.getRefClass();
            Field ownerField = clazz.getDeclaredField(fieldName);
            RefHolder ref = new RefHolder(object, clazz, ownerField, ReferenceType.SCALAR);
            this.refStack.push(ref);

            // обработка фильтрации
            this.depthLevel++; //увеличиваем глубину иерархии на +1
        } catch (NoSuchFieldException | NullPointerException e) {
            String msg = String.format("В классе \"%s\" не определено поле \"%s\"", clazz.getName(), fieldName);
            throw new IllegalStateException(msg);
        } catch (NoSuchElementException e) {
            String msg = String.format("Ошибка в структуре XML-документа. " +
                                       "Найдет открывающий тэг <%s>, перед которым нет тэга элемента-владельца.",
                                        REF_CONTAINER_TAG);
            throw new IllegalStateException(msg);
        }
    }

    @Override
    public void registerItem(String tag, Map<String, String> attrs) throws IllegalStateException, NullPointerException {
        Objects.requireNonNull(tag, "В имени тэга передано пустое значение");
        Objects.requireNonNull(attrs, "В коллекции атрибутов передано пустое значение");

        String className = tag;
        Class<?> clazz = null;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            String msg = String.format("Для типа \"%s\" не определен конструктор по умолчанию", className);
            throw new IllegalStateException(msg);
        }
        initTypeInCache(clazz);
        String pkey = attrs.get(PKEY_ATTR_NAME);
        Object item;
        if (pkey != null && !pkey.isEmpty()) {
            Map<String, Object> idxObjById = this.objCache.get(clazz);
            item = idxObjById.get(pkey);
            if (item == null) {
                Constructor<?> ctor = null;
                try {
                    ctor = clazz.getConstructor();
                    Objects.requireNonNull(ctor,
                            "Не найден конструктор по умолчанию для типа: " + clazz.getName());

                    item = ctor.newInstance();
                    for (Field f : clazz.getDeclaredFields()) {
                        XmlAttribute[] xmlAttribute = f.getAnnotationsByType(XmlAttribute.class);
                        if (xmlAttribute.length == 0) continue;
                        String value = attrs.get(f.getName());
                        if (value != null && !value.isEmpty()) {
                            Reflector.setFieldValue(f, item, Reflector.getScalarValue(value, f.getType()));
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
        this.objStack.push(new ObjHolder(item, clazz));

        // обрабатываем фильтрацию
        if (this.filter == null){
            return;
        }
        if (this.depthLevel >= 0 && this.depthLevel < this.filter.getPathLength()){
            if (this.depthLevel == this.filter.getPathLength() - 1 && this.filter.checkPath(this.depthLevel, tag)){
                this.matchStatus = this.filter.testCondition(attrs);
            }
        }

    }

    @Override
    public void finalizeItem(){
        // обрабатываем состояние this.matchStatus
        if (this.depthLevel == 0){ //мы на самом верхнем уровне
            if (!this.matchStatus){
                // если было невовпадение фильтра на любом уровне в пределах маршрута - удаляем объект из стэка.
                this.objStack.pop();
            }
            this.matchStatus = true;
        }
    }

    @Override
    public List<T> getData() {
        return new ArrayList<>(dataStore);
    }
}
