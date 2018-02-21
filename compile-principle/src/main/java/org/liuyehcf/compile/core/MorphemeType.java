package org.liuyehcf.compile.core;

public enum MorphemeType {
    /**
     * 内部使用的词素，例如ε，$等符号
     */
    INTERNAL(1),

    /**
     * 关键字词素
     */
    KEY(2),

    /**
     * 普通词素
     */
    NORMAL(4),

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
