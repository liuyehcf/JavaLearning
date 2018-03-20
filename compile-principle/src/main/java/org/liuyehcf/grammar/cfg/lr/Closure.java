package org.liuyehcf.grammar.cfg.lr;

import org.liuyehcf.grammar.core.definition.PrimaryProduction;

import java.util.List;

public class Closure {

    private static int count = 0;

    private final int id = count++;

    private final PrimaryProduction corePrimaryProduction;

    private final List<PrimaryProduction> primaryProductions;

    Closure(PrimaryProduction corePrimaryProduction, List<PrimaryProduction> primaryProductions) {
        this.corePrimaryProduction = corePrimaryProduction;
        this.primaryProductions = primaryProductions;
    }

    public int getId() {
        return id;
    }

    public PrimaryProduction getCorePrimaryProduction() {
        return corePrimaryProduction;
    }

    public List<PrimaryProduction> getPrimaryProductions() {
        return primaryProductions;
    }

    public boolean isCorePrimaryProduction(PrimaryProduction _PP) {
        return corePrimaryProduction.equals(_PP);
    }

    @Override
    public String toString() {
        return "Closure{" +
                "id=" + id +
                ", corePrimaryProduction=" + corePrimaryProduction +
                ", primaryProductions=" + primaryProductions +
                '}';
    }
}
