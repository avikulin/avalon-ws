package Utils.XMLParser;

import Utils.XMLParser.Contracts.GenericListFactory;

import java.lang.reflect.Field;

public class RefHolder {
    Object containerObject;
    Class<?> containerClass;
    Field containerField;
    ReferenceType referenceType;

    public RefHolder(Object containerObject, Class<?> containerClass, Field containerField, ReferenceType referenceType) {
        this.containerObject = containerObject;
        this.containerClass = containerClass;
        this.containerField = containerField;
        this.referenceType = referenceType;
    }

    public Object getContainerObject() {
        return containerObject;
    }

    public Class<?> getContainerClass() {
        return containerClass;
    }

    public Field getContainerField() {
        return containerField;
    }

    public ReferenceType getReferenceType() {
        return referenceType;
    }

    private Object getReferencedObject(){
        Object res = null;
        try{
            res = this.containerField.get(this.containerObject);
        } catch (IllegalAccessException e) {
            String msg = String.format("Ошибка получение ссылки на значение поля \"%s\": %s",
                    f.getName(),
                    e.getMessage());
        }
        return res;
    }
}
