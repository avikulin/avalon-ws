package Utils.GenericListFactory;

import Utils.GenericListFactory.GenericListFactory;

import java.util.ArrayList;
import java.util.List;

public class ArrayListFactoryImpl implements GenericListFactory {
    @Override
    public <Type> List<Type> create(Class<Type> type) {
        List<Type> res = new ArrayList<>();
        return res;
    }
}
