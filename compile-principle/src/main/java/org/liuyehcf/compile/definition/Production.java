package org.liuyehcf.compile.definition;

import org.liuyehcf.compile.utils.ListUtils;

import java.util.List;

import static org.liuyehcf.compile.utils.AssertUtils.assertFalse;

/**
 * 文法产生式
 * 只研究二型文法（包括三型文法）
 * 等式左边是非终结符，等式右边是文法符号串
 */
public class Production {
    private static final String OR = "|";

    // 产生式左侧非终结符
    private final Symbol left;

    // 并列关系的多个产生式右部
    private final List<PrimaryProduction> right;

    private Production(Symbol left, List<PrimaryProduction> right) {
        this.left = left;
        this.right = right;
    }

    public static Production create(Symbol left, PrimaryProduction... right) {
        return create(left, ListUtils.of(right));
    }

    public static Production create(Symbol left, List<PrimaryProduction> right) {
        return new Production(left, right);
    }

    public Symbol getLeft() {
        return left;
    }

    public List<PrimaryProduction> getRight() {
        return right;
    }

    public String toJSONString() {
        return '{' +
                "\"left\":" + left +
                ", \"right\":" + right +
                '}';
    }

    public String toReadableJSONString() {
        StringBuilder sb = new StringBuilder();

        sb.append('\"');
        sb.append(left.toReadableJSONString())
                .append(" → ");

        for (PrimaryProduction pp : right) {
            sb.append(pp.toReadableJSONString())
                    .append(' ')
                    .append(OR)
                    .append(' ');
        }

        assertFalse(right.isEmpty());
        sb.setLength(sb.length() - 1);

        sb.append('\"');

        return sb.toString();
    }

    @Override
    public String toString() {
        return toReadableJSONString();
    }
}
