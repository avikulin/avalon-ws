package Utils.XMLTransformer;

import DAL.Contracts.CrudRepository;

import javax.ejb.Local;
import java.io.File;

@Local
public interface XTransformer<T, K> {
    void setSource(CrudRepository<T, K> source);

    void writeXML(File outputFile);
}
