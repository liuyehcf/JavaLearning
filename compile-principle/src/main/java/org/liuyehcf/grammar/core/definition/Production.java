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
public class Production implements Comparable<Production> {
    private static final String OR = "|";

    private final Symbol left;

    private final List<PrimaryProduction> primaryProductions;

    private Production(List<PrimaryProduction> primaryProductions) {
        this.primaryProductions = Collections.unmodifiableList(ListUtils.sort(primaryProductions));

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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append('\"');
        sb.append(left)
                .append(" → ");

        for (PrimaryProduction _PP : primaryProductions) {
            sb.append(_PP.getRight())
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Production that = (Production) o;
        return Objects.equals(left, that.left) &&
                Objects.equals(primaryProductions, that.primaryProductions);
    }

    @Override
    public int hashCode() {

        return Objects.hash(left, primaryProductions);
    }

    @Override
    public int compareTo(Production o) {
        int res = this.left.compareTo(o.left);
        if (res == 0) {
            int i = 0;
            while (i < this.primaryProductions.size()
                    && i < o.primaryProductions.size()) {
                res = this.primaryProductions.get(i).compareTo(o.primaryProductions.get(i));
                if (res != 0) {
                    return res;
                }
                i++;
            }
            if (i < this.primaryProductions.size()) {
                return 1;
            } else if (i < o.primaryProductions.size()) {
                return -1;
            } else {
                return 0;
            }
        }
        return res;
    }
}
