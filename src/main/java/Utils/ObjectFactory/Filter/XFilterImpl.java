package Utils.ObjectFactory.Filter;

import Utils.ObjectFactory.Enums.PredicateType;

import java.util.Map;

public class XFilterImpl implements XFilter {
    private static final char EXPR_PREFIX = '@';
    private static final String PATH_SEPARATOR = "/";
    private static final String PREDICATE_PREFIX = "[";
    private static final String PREDICATE_SUFFIX = "]";

    String[] path;
    String field;
    PredicateType predicate;
    String value;

    public XFilterImpl(String s){
        if (s == null || s.isEmpty()){
            throw new IllegalStateException("Ошибка выражения. Передано пустое значение");
        }
        if (s.charAt(0) != EXPR_PREFIX){
            throw new IllegalStateException("Переданная строка не является предикатом");
        }
        s = s.substring(1);
        this.path = s.split(PATH_SEPARATOR);
        String lastToken = this.path[this.path.length-1];
        String[] predicateTokens = lastToken.split("\\" + PREDICATE_PREFIX);
        if (predicateTokens.length!=2){
            throw new IllegalStateException("Ощибочное форматирование области предиката");
        }

        this.path[this.path.length-1] = predicateTokens[0];
        String expressionStr = predicateTokens[1].substring(0, predicateTokens[1].length()-1); //отрезаем "]"
        String[] expressionTokens = expressionStr.split(PredicateType.getSplitExpr());

        if (expressionTokens.length!=2){
            throw new IllegalStateException("Ощибочное форматирование теста предиката");
        }

        this.field = expressionTokens[0];
        this.predicate = PredicateType.fromString(expressionStr);
        this.value = expressionTokens[1].substring(1, expressionTokens[1].length() - 1); //отрезаем кавычки
    }

    @Override
    public int getPathLength(){
        return this.path.length;
    }

    @Override
    public boolean checkPath(int partIdx, String pathToken){
        if (partIdx < 0 || partIdx > this.path.length - 1){
            String msg = String.format("Передано неправильное значение индекса: %s. " +
                                       "Корректный диапазон значений: от %d до %d", partIdx, 0, this.path.length - 1);
            throw new IllegalStateException(msg);
        }

        return this.path[partIdx].equals(pathToken.trim());
    }

    @Override
    public boolean testCondition(Map<String, String> attributes){
        String v = attributes.get(this.field);
        if (v == null) return true;
        return this.predicate.apply(v, this.value);
    }

    @Override
    public String toString() {
        return  EXPR_PREFIX +
                String.join(PATH_SEPARATOR, this.path) +
                PREDICATE_PREFIX + this.field + ' ' + predicate + " \"" + value + "\"" + PREDICATE_SUFFIX;
    }
}
