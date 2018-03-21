package org.liuyehcf.grammar.cfg.ll;

import org.liuyehcf.grammar.cfg.CfgParser;

public interface LLParser extends CfgParser {
    /**
     * 获取First、Follow、Select的JSON串
     */
    String getSelectJSONString();
}
