package org.liuyehcf.compile.definition;

/**
 * 文法符号，包括终结符和非终结符
 * 特殊符号用"__"作为前后缀，且全部字母大写，同时禁止普通Symbol带有"__"前后缀
 */
public class Symbol {

    public static final Symbol START = new Symbol(false, "__START__", 0);
    public static final Symbol EPSILON = new Symbol(true, "__EPSILON__", 0);

    // 是否为终结符
    private final boolean isTerminator;

    // 符号的字符串
    private final String value;

    // 一撇"′"的数量（value本身包含的"′"不算）
    private final int primeCount;

    /**
     * 对外暴露的构造方法
     *
     * @param isTerminator
     * @param value
     */
    public Symbol(boolean isTerminator, String value) {
        if (value == null) {
            throw new NullPointerException();
        }
        if (value.startsWith("__") || value.endsWith("__")) {
            throw new IllegalArgumentException();
        }
        this.isTerminator = isTerminator;
        this.value = value;
        this.primeCount = 0;
    }

    /**
     * 内部用的构造方法，仅用于创建一些特殊Symbol，以及异变符号
     *
     * @param isTerminator
     * @param value
     * @param primeCount
     */
    private Symbol(boolean isTerminator, String value, int primeCount) {
        if (value == null) {
            throw new NullPointerException();
        }
        this.isTerminator = isTerminator;
        this.value = value;
        this.primeCount = primeCount;
    }

    public boolean isTerminator() {
        return isTerminator;
    }

    /**
     * 带上异变后缀的符号字符串
     *
     * @return
     */
    public String getPrimedValue() {
        return value + toPrimeString();
    }

    /**
     * 是否为异变符号
     *
     * @return
     */
    public boolean isPrimedSymbol() {
        return primeCount != 0;
    }

    /**
     * 根据异变次数生成后缀符号，例如1次异变就是"′"
     *
     * @return
     */
    private String toPrimeString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < primeCount; i++) {
            sb.append("′");
        }
        return sb.toString();
    }

    /**
     * 产生一个异变符号
     *
     * @return
     */
    public Symbol getPrimedSymbol() {
        return new Symbol(this.isTerminator, this.value, this.primeCount + 1);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Symbol) {
            Symbol symbol = (Symbol) obj;
            return symbol.value.equals(this.value)
                    && symbol.primeCount == this.primeCount
                    && symbol.isTerminator == this.isTerminator;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Boolean.valueOf(this.isTerminator).hashCode() +
                this.value.hashCode() +
                Integer.valueOf(this.primeCount).hashCode();
    }

    public String toJSONString() {
        return '{' +
                "\"isTerminator\":" + '\"' + isTerminator + '\"' +
                ", \"value\":" + '\"' + getPrimedValue() + '\"' +
                '}';
    }

    public String toReadableJSONString() {
        return getPrimedValue();
    }

    @Override
    public String toString() {
        return toReadableJSONString();
    }
}
