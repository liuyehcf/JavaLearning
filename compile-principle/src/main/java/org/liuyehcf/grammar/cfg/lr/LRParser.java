package org.liuyehcf.grammar.cfg.lr;

import org.liuyehcf.grammar.Parser;

public interface LRParser extends Parser {
    /**
     * 获取预测分析表的markdown格式的字符串
     */
    String getForecastAnalysisTable();
}
