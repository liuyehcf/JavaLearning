package org.liuyehcf.grammar.cfg.lr;

import org.liuyehcf.grammar.core.definition.PrimaryProduction;
import org.liuyehcf.grammar.core.definition.Symbol;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

public class Item {
    /**
     * 产生式
     */
    private final PrimaryProduction primaryProduction;

    /**
     * 展望符
     */
    private final Set<Symbol> lookAHeads;

    Item(PrimaryProduction primaryProduction, Set<Symbol> lookAHeads) {
        this.primaryProduction = primaryProduction;
        if (lookAHeads == null) {
            this.lookAHeads = null;
        } else {
            this.lookAHeads = Collections.unmodifiableSet(lookAHeads);
        }
    }

    public PrimaryProduction getPrimaryProduction() {
        return primaryProduction;
    }

    public Set<Symbol> getLookAHeads() {
        return lookAHeads;
    }

    public boolean isOfSamePrimaryProduction(PrimaryProduction _PP) {
        return primaryProduction.equals(_PP);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(primaryProduction, item.primaryProduction) &&
                Objects.equals(lookAHeads, item.lookAHeads);
    }

    @Override
    public int hashCode() {

        return Objects.hash(primaryProduction, lookAHeads);
    }

    @Override
    public String toString() {
        return "Item{" +
                "primaryProduction=" + primaryProduction +
                ", lookAHeads=" + lookAHeads +
                '}';
    }
}
