package org.liuyehcf.grammar.cfg.lr;

public class Operation {

    private final int closureId;

    private final OperationCode operator;

    public Operation(int closureId, OperationCode operator) {
        this.closureId = closureId;
        this.operator = operator;
    }

    public enum OperationCode {
        MOVE_IN,
        REDUCTION,
        ACCEPT,
    }
}
