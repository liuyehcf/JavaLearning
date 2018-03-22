package org.liuyehcf.grammar.cfg;

import org.liuyehcf.grammar.Parser;

public interface CfgParser extends Parser {
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

    /**
     * 当前文法是否合法（是否是当前文法分析器支持的文法）
     */
    boolean isLegal();
}
