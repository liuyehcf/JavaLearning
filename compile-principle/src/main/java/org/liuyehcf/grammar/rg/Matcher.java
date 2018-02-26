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

    /**
     * 返回捕获组的数量
     */
    int groupCount();

    /**
     * 指定捕获组的起始索引，闭
     */
    int start(int group);

    /**
     * 指定不获取的终止索引，开
     */
    int end(int group);
}
