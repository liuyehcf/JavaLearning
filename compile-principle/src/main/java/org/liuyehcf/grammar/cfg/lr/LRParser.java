package org.liuyehcf.grammar.cfg.lr;

import org.liuyehcf.grammar.cfg.CfgParser;

public interface LRParser extends CfgParser {
    /**
     * 获取Closure的JSON串
     */
    String getClosureJSONString();
}
