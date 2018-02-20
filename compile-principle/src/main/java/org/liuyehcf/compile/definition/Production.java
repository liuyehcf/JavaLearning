package org.liuyehcf.compile.definition;

import java.util.List;

/**
 * 文法产生式
 * 只研究二型文法（包括三型文法）
 * 等式左边是非终结符，等式右边是文法符号串
 */
public class Production {
    // 产生式左侧非终结符
    private final Symbol left;

    // 并列关系的多个产生式右部
    private final List<SymbolSequence> right;

    public Production(Symbol left, List<SymbolSequence> right) {
        this.left = left;
        this.right = right;
    }

    public Symbol getLeft() {
        return left;
    }

    public List<SymbolSequence> getRight() {
        return right;
    }

    @Override
    public String toString() {
        return "{" +
                "\"left\":" + left +
                ", \"right\":" + right +
                '}';
    }
}
