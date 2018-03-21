package org.liuyehcf.grammar.core.definition;

import java.util.Objects;

/**
 * 文法产生式
 * 只研究二型文法（包括三型文法）
 * 等式左边是非终结符，等式右边是文法符号串
 */
public class PrimaryProduction {
    // 产生式左侧非终结符
    private final Symbol left;

    // 产生式右部的文法符号串
    private final SymbolString right;

    private PrimaryProduction(Symbol left, SymbolString right) {
        this.left = left;
        this.right = right;
    }

    public static PrimaryProduction create(Symbol left, SymbolString right) {
        return new PrimaryProduction(left, right);
    }

    public Symbol getLeft() {
        return left;
    }

    public SymbolString getRight() {
        return right;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrimaryProduction that = (PrimaryProduction) o;
        return Objects.equals(left, that.left) &&
                Objects.equals(right, that.right);
    }

    @Override
    public int hashCode() {

        return Objects.hash(left, right);
    }

    public String toJSONString() {
        return left.toJSONString() + " → " + right.toJSONString();
    }

    @Override
    public String toString() {
        return "PrimaryProduction{" +
                "left=" + left +
                ", right=" + right +
                '}';
    }
}
