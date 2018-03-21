package org.liuyehcf.grammar.core.definition;

import org.liuyehcf.grammar.utils.AssertUtils;
import org.liuyehcf.grammar.utils.ListUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.liuyehcf.grammar.utils.AssertUtils.assertTrue;

/**
 * 具有相同左部的产生式集合
 */
public class Production {
    private static final String OR = "|";

    private final Symbol left;

    private final List<PrimaryProduction> primaryProductions;

    private Production(List<PrimaryProduction> primaryProductions) {
        this.primaryProductions = Collections.unmodifiableList(primaryProductions);

        Symbol left = null;
        for (PrimaryProduction _PP : primaryProductions) {
            if (left == null) {
                left = _PP.getLeft();
            } else {
                assertTrue(left.equals(_PP.getLeft()));
            }
        }
        this.left = left;
    }

    public static Production create(PrimaryProduction... primaryProductions) {
        return create(ListUtils.of(primaryProductions));
    }

    public static Production create(List<PrimaryProduction> primaryProductions) {
        return new Production(primaryProductions);
    }

    public Symbol getLeft() {
        return left;
    }

    public List<PrimaryProduction> getPrimaryProductions() {
        return primaryProductions;
    }

    public String toJSONString() {
        StringBuilder sb = new StringBuilder();

        sb.append('\"');
        sb.append(left.toJSONString())
                .append(" → ");

        for (PrimaryProduction _PP : primaryProductions) {
            sb.append(_PP.getRight().toJSONString())
                    .append(' ')
                    .append(OR)
                    .append(' ');
        }

        AssertUtils.assertFalse(primaryProductions.isEmpty());
        sb.setLength(sb.length() - 3);

        sb.append('\"');

        return sb.toString();
    }

    @Override
    public String toString() {
        return "Production{" +
                "left=" + left +
                ", primaryProductions=" + primaryProductions +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Production that = (Production) o;
        return Objects.equals(left, that.left) &&
                Objects.equals(primaryProductions, that.primaryProductions); // todo 依赖PrimaryProduction的equals
    }

    @Override
    public int hashCode() {

        return Objects.hash(left, primaryProductions);
    }
}
