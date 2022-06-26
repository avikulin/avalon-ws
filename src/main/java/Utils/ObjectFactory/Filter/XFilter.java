package Utils.ObjectFactory.Filter;

import java.util.Map;

public interface XFilter {
    int getPathLength();

    boolean checkPath(int partIdx, String pathToken);

    boolean testCondition(Map<String, String> attributes);
}
