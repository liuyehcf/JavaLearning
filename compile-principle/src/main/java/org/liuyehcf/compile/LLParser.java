package org.liuyehcf.compile;

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
