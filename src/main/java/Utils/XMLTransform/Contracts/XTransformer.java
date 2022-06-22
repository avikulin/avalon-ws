package Utils.XMLTransform.Contracts;

import DAL.Contracts.ReadOnlyRepository;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public interface XTransformer<T> {
    void setSource(ReadOnlyRepository<T> source);
    void writeXML(OutputStream out);
    List<T> readXML(InputStream in);
}
