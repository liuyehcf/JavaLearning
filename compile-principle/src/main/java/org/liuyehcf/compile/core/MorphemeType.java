package org.liuyehcf.compile.core;

public enum MorphemeType {
    /**
     * 关键字词素
     */
    KEY,

    /**
     * 内部使用的词素，例如ε，$等符号
     */
    INTERNAL,

    /**
     * 普通词素
     */
    NORMAL,

    /**
     * 正则表达式词素
     */
    REGEX,
}
