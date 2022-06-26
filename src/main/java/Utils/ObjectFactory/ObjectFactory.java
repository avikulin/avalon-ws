package Utils.ObjectFactory;

import Utils.ObjectFactory.Filter.XFilter;

import javax.ejb.Local;
import java.util.List;
import java.util.Map;

@Local
public interface ObjectFactory<T> {
    void initDocument();

    void setFilter(XFilter filter);

    void processRefElement();

    void processRefCollection();

    void registerCollectionRef(String fieldName, String fieldType);

    void registerElementRef(String fieldName, String fieldType);

    void registerItem(String tag, Map<String, String> attrs);

    void finalizeItem();

    List<T> getData();
}
