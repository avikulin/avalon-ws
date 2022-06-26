import DAL.DataEntities.Registers.Equipment;
import Utils.ObjectFactory.Filter.XFilterImpl;
import Utils.ObjectFactory.ObjectFactory;
import Utils.ObjectFactory.ObjectFactoryImpl;
import Utils.XMLParser.XParser;
import Utils.XMLParser.Dom.DomXmlParserSeqImpl;

import java.io.File;
import java.util.List;

public class testDomXmlParser {
    public static void main(String[] args) {
        XParser<Equipment> parser = new DomXmlParserSeqImpl<>();
        parser.setSource(new File("equipment_units_data.xml"));
        ObjectFactory<Equipment> factory = new ObjectFactoryImpl<>();
        XFilterImpl filter = new XFilterImpl("@DAL.DataEntities.Registers.Equipment/DAL.DataEntities.Dictionaries.Model/DAL.DataEntities.Dictionaries.Vendor[id EQ \"1002\"]");
        factory.setFilter(filter);
        parser.setObjectFactory(factory);
        List<Equipment> res = parser.getData();
        for (Equipment eq: res){
            System.out.println(eq);
            System.out.println("------------------------");
            System.out.println();
        }
    }
}
