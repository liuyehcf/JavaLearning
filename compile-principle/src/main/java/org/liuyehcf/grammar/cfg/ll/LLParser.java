package org.liuyehcf.grammar.cfg.ll;

import org.liuyehcf.grammar.Parser;

public interface LLParser extends Parser {
    /**
     * 获取First、Follow、Select的JSON串
     */
    String getStatus();

    /**
     * 获取预测分析表的markdown格式的字符串
     */
    String getForecastAnalysisTable();
}
