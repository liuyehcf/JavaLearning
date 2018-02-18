package org.liuyehcf.compile.definition;

/**
 * 文法符号，包括终结符和非终结符
 */
public class Symbol {
    private final Boolean isTerminator;
    private final String value;

    public Symbol(Boolean isTerminator, String value) {
        check(isTerminator, value);
        this.isTerminator = isTerminator;
        this.value = value;
    }

    private void check(Boolean isTerminator, String value) {
        if (isTerminator == null || value == null) {
            throw new NullPointerException();
        }
    }

    public Boolean getTerminator() {
        return isTerminator;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Symbol) {
            Symbol symbol = (Symbol) obj;
            return symbol.value.equals(this.value)
                    && symbol.isTerminator.equals(this.isTerminator);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.isTerminator.hashCode() + this.value.hashCode();
    }

    @Override
    public String toString() {
        return "{" +
                "\"isTerminator\":" + "\"" + isTerminator + "\"" +
                ", \"value\":" + "\"" + value + "\"" +
                '}';
    }
}
