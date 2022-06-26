package Utils.XMLParser;

import Utils.ObjectFactory.ObjectFactory;

import javax.ejb.Local;
import java.io.File;
import java.util.List;

@Local
public interface XParser<T> {
    void setSource(File inputFile);

    void setObjectFactory(ObjectFactory<T> objectFactory);

    List<T> getData();
}
