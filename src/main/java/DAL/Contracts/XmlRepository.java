package DAL.Contracts;

import Utils.ObjectFactory.Filter.XFilter;
import Utils.ObjectFactory.ObjectFactory;
import Utils.XMLParser.XParser;
import Utils.XMLTransformer.XTransformer;

import javax.ejb.Local;
import java.io.File;
import java.util.List;

@Local
public interface XmlRepository<T, K> {
    void setSourceRepo(CrudRepository<T, K> repo);

    void setParser(XParser<T> parser, ObjectFactory<T> objectFactory);

    void setTransformer(XTransformer<T, K> transformer);

    void setXmlFile(File xmlFile);

    void writeXml();

    List<T> readXml(String filterExpr);

    List<T> readXml();
}
