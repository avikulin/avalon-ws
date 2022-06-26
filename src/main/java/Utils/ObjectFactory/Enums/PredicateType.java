package Utils.ObjectFactory.Enums;

import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public enum PredicateType {
    EQ(String::equals),
    LIKE(String::contains);

    private BiFunction<String, String, Boolean> fun;
    PredicateType(BiFunction<String, String, Boolean> fun) {
        this.fun = fun;
    }

    public boolean apply(String lValue, String rValue){
        return this.fun.apply(lValue, rValue);
    }

    public static PredicateType fromString(String s){
        for (PredicateType p: PredicateType.values()){
            String pattern = " " + p + " ";
            if (s.contains(pattern)){
                return p;
            }
        }
        throw new IllegalArgumentException("Неподдерживаемый тип предиката");
    }

    public static String getSplitExpr(){
        return "\\s" + Arrays
                .stream(PredicateType.values())
                .map(Enum::toString)
                .collect(Collectors.joining(" | "))
                + "\\s";
    }
}
