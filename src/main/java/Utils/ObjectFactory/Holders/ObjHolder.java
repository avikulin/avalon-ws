package Utils.ObjectFactory.Holders;

public class ObjHolder {
    private final Object refObject;
    private final Class<?> refClass;

    public ObjHolder(Object refObject, Class<?> refClass) {
        this.refObject = refObject;
        this.refClass = refClass;
    }

    public Object getRefObject() {
        return refObject;
    }

    public Class<?> getRefClass() {
        return refClass;
    }
}
