package org.liuyehcf.compile;

import org.liuyehcf.compile.definition.Grammar;

public interface LLParser {
    /**
     * 获取Grammar
     **/
    Grammar getGrammar();

    /**
     * 给定字符串是否为当前文法的句子
     */
    boolean isMatch(String expression);

    /**
     * 获取First、Follow、Select的JSON串
     */
    String getStatus();

    /**
     * 获取预测分析表的markdown格式的字符串
     */
    String getForecastAnalysisTable();
}
