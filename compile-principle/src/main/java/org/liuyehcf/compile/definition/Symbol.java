package org.liuyehcf.compile.definition;

/**
 * 文法符号，包括终结符和非终结符
 */
public class Symbol {
    private static final String EMPTY_SUB_VALUE = "";

    public static final Symbol _Epsilon = new Symbol(true, "", "Epsilon");

    private final Boolean isTerminator;
    private final String value;

    // 该字段，仅用于内部处理，比如想要构造一个A'，把'写到subValue中
    private final String subValue;

    public Symbol(Boolean isTerminator, String value) {
        this(isTerminator, value, EMPTY_SUB_VALUE);
    }

    public Symbol(Boolean isTerminator, String value, String subValue) {
        check(isTerminator, value, subValue);
        this.isTerminator = isTerminator;
        this.value = value;
        this.subValue = subValue;
    }

    private void check(Boolean isTerminator, String value, String subValue) {
        if (isTerminator == null || value == null || subValue == null) {
            throw new NullPointerException();
        }
    }

    public Boolean getTerminator() {
        return isTerminator;
    }

    public String getValue() {
        return value;
    }

    public String getSubValue() {
        return subValue;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Symbol) {
            Symbol symbol = (Symbol) obj;
            return symbol.value.equals(this.value)
                    && symbol.subValue.equals(this.subValue)
                    && symbol.isTerminator.equals(this.isTerminator);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.isTerminator.hashCode() + this.value.hashCode() + this.subValue.hashCode();
    }

    @Override
    public String toString() {
        return "{" +
                "\"isTerminator\":" + "\"" + isTerminator + "\"" +
                ", \"value\":" + "\"" + value + "\"" +
                ", \"subValue\":" + "\"" + subValue + "\"" +
                '}';
    }
}
