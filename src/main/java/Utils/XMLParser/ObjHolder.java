package Utils.XMLParser;

public class ObjHolder {
    Object object;
    Class<?> aClass;

    public ObjHolder(Object object, Class<?> aClass) {
        this.object = object;
        this.aClass = aClass;
    }

    public Object getObject() {
        return object;
    }

    public Class<?> getaClass() {
        return aClass;
    }
}
