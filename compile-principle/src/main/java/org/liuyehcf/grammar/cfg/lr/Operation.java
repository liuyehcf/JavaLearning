package org.liuyehcf.grammar.cfg.lr;

import org.liuyehcf.grammar.core.definition.PrimaryProduction;

import java.util.Objects;

public class Operation {
    private final int nextClosureId;

    private final PrimaryProduction primaryProduction;

    private final OperationCode operator;

    Operation(int nextClosureId, PrimaryProduction primaryProduction, OperationCode operator) {
        this.nextClosureId = nextClosureId;
        this.primaryProduction = primaryProduction;
        this.operator = operator;
    }

    public int getNextClosureId() {
        return nextClosureId;
    }

    public PrimaryProduction getPrimaryProduction() {
        return primaryProduction;
    }

    public OperationCode getOperator() {
        return operator;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Operation operation = (Operation) o;
        return nextClosureId == operation.nextClosureId &&
                Objects.equals(primaryProduction, operation.primaryProduction) &&
                operator == operation.operator;
    }

    @Override
    public int hashCode() {

        return Objects.hash(nextClosureId, primaryProduction, operator);
    }

    public enum OperationCode {
        MOVE_IN,
        REDUCTION,
        JUMP,
        ACCEPT,

    }
}
