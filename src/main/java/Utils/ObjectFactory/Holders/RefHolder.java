package Utils.ObjectFactory.Holders;

import Utils.ObjectFactory.Enums.ReferenceType;
import Utils.ObjectFactory.Helpers.Reflector;

import java.lang.reflect.Field;

public class RefHolder {
    private final Object containerObject;
    private final Class<?> containerClass;
    private final Field containerFieldRef;
    private final Class<?> containerFieldRefClass;
    private final ReferenceType referenceType;

    public RefHolder(Object containerObject, Class<?> containerClass, Field containerFieldRef, ReferenceType referenceType) {
        this.containerObject = containerObject;
        this.containerClass = containerClass;
        this.containerFieldRef = containerFieldRef;
        this.containerFieldRefClass = containerFieldRef.getType();
        this.referenceType = referenceType;
    }

    public Object getContainerObject() {
        return containerObject;
    }

    public Class<?> getContainerClass() {
        return containerClass;
    }

    public Object getReferencedObject(){
        return Reflector.getFieldValue(this.containerFieldRef, this.containerObject);
    }

    public Class<?> getReferencedObjectClass(){
        return this.containerFieldRefClass;
    }

    public Field getContainerFieldRef() {
        return containerFieldRef;
    }

    public ReferenceType getReferenceType() {
        return referenceType;
    }
}
