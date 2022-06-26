import Utils.ObjectFactory.Filter.XFilterImpl;

import java.util.HashMap;
import java.util.Map;

public class testFilter {
    public static void main(String[] args) {
        XFilterImpl filter = new XFilterImpl("@DAL.DataEntities.Registers.Equipment/DAL.DataEntities.Dictionaries.Model/DAL.DataEntities.Dictionaries.Vendor[name EQ \"Zyxel Corp.\"]");
        System.out.println(filter);
        System.out.println(filter.getPathLength());
        System.out.println(filter.checkPath(0,"DAL.DataEntities.Registers.Equipment"));
        System.out.println(filter.checkPath(1,"DAL.DataEntities.Dictionaries.Model"));
        System.out.println(filter.checkPath(2,"DAL.DataEntities.Dictionaries.Vendor"));

        Map<String, String> expr = new HashMap<>();
        expr.put("name", "Zyxel Corp.");
        System.out.println(filter.testCondition(expr));
    }
}
