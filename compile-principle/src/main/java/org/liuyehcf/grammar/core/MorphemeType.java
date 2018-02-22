package org.liuyehcf.grammar.core;

public enum MorphemeType {
    /**
     * 普通词素
     */
    NORMAL(1),

    /**
     * 正则表达式词素
     */
    REGEX(8);

    private final int order;

    MorphemeType(int order) {
        this.order = order;
    }

    public int getOrder() {
        return order;
    }
}
