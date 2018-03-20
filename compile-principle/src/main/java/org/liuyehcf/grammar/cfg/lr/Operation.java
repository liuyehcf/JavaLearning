package org.liuyehcf.grammar.cfg.lr;

import org.liuyehcf.grammar.core.definition.PrimaryProduction;

public class Operation {
    private final int nextClosureId;

    private final PrimaryProduction primaryProduction;

    private final OperationCode operator;

    public Operation(int nextClosureId, PrimaryProduction primaryProduction, OperationCode operator) {
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

    public enum OperationCode {
        MOVE_IN,
        REDUCTION,
        JUMP,
        ACCEPT,

    }
}
