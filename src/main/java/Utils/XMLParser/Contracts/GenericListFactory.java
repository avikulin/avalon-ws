package Utils.XMLParser.Contracts;

import java.util.List;

public interface GenericListFactory {
    <Type> List<Type> create(Class<Type> type);
}
