package org.liuyehcf.grammar.rg;

public interface Matcher {
    /**
     * 正则表达式是否匹配整个字符串
     */
    boolean matches();

    /**
     * 查询'下一个'匹配的子串
     */
    boolean find();

    /**
     * 返回第i组匹配的内容
     */
    String group(int group);
}
