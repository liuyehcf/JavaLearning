package org.liuyehcf.grammar.cfg.lr;

import org.liuyehcf.grammar.cfg.CfgParser;

public interface LRParser extends CfgParser {
    /**
     * 获取Closure的JSON串
     */
    String getClosureJSONString();

    /**
     * 获取状态转移表JSON串
     */
    String getClosureTransferTableJSONString();
}
