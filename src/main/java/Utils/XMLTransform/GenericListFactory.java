package Utils.XMLTransform;

import java.util.List;

public interface GenericListFactory {
    <Type> List<Type> create(Class<Type> type);
}
