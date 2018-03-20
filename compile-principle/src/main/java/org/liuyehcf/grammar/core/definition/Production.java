package org.liuyehcf.grammar.core.definition;

import org.liuyehcf.grammar.utils.AssertUtils;
import org.liuyehcf.grammar.utils.ListUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

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
        this.right = Collections.unmodifiableList(right);
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

        for (PrimaryProduction _PP : right) {
            sb.append(_PP.toReadableJSONString())
                    .append(' ')
                    .append(OR)
                    .append(' ');
        }

        AssertUtils.assertFalse(right.isEmpty());
        sb.setLength(sb.length() - 3);

        sb.append('\"');

        return sb.toString();
    }

    @Override
    public String toString() {
        return toReadableJSONString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Production that = (Production) o;
        return Objects.equals(left, that.left) &&
                Objects.equals(right, that.right);
    }

    @Override
    public int hashCode() {

        return Objects.hash(left, right);
    }
}
