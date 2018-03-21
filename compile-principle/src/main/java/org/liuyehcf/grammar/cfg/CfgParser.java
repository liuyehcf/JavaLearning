package org.liuyehcf.grammar.cfg;

import org.liuyehcf.grammar.Parser;

public interface CfgParser extends Parser {
    /**
     * 初始化
     */
    void init();

    /**
     * 获取First集的JSON串
     */
    String getFirstJSONString();

    /**
     * 获取Follow集的JSON串
     */
    String getFollowJSONString();

    /**
     * 获取预测分析表的的Markdown格式的字符串
     */
    String getAnalysisTableMarkdownString();
}
