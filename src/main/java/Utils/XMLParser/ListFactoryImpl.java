package Utils.XMLParser;

import Utils.XMLParser.Contracts.GenericListFactory;

import java.util.ArrayList;
import java.util.List;

public class ListFactoryImpl implements GenericListFactory {
    @Override
    public <Type> List<Type> create(Class<Type> type) {
        List<Type> res = new ArrayList<>();
        return res;
    }
}
