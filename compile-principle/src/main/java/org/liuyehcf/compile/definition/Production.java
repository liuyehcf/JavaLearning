package org.liuyehcf.compile.definition;

import java.util.List;
import java.util.Map;

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

    // 产生式依赖的其他产生式
    private final Map<Symbol, Production> extraProductions;

    public Production(Map<Symbol, Production> extraProductions, Symbol left, List<SymbolSequence> right) {
        this.extraProductions = extraProductions;
        this.left = left;
        this.right = right;
    }

    public Symbol getLeft() {
        return left;
    }

    public List<SymbolSequence> getRight() {
        return right;
    }

    public Map<Symbol, Production> getExtraProductions() {
        return extraProductions;
    }

    @Override
    public String toString() {
        return "{" +
                "\"left\":" + left +
                ", \"right\":" + right +
                ", \"extraProductions\":" + toExtraProductionsString() +
                '}';
    }

    private String toExtraProductionsString() {
        StringBuilder sb = new StringBuilder();

        sb.append('{');

        for (Map.Entry<Symbol, Production> entry : extraProductions.entrySet()) {
            sb.append("\"")
                    .append(entry.getKey().getMutatedValue())
                    .append("\"")
                    .append(":")
                    .append(entry.getValue().toString())
                    .append(",");
        }

        if (sb.length() > 1) {
            sb.setLength(sb.length() - 1);
        }

        sb.append('}');

        return sb.toString();
    }
}
