package org.liuyehcf.algorithm.compile.grammar.regex;

import org.junit.Test;
import org.liuyehcf.algorithm.compile.grammar.regex.symbol.Symbol;

/**
 * Created by Liuye on 2017/10/26.
 */
public class TestSymbol {
    @Test
    public void testAlphabetSymbols() {
        assert Symbol.getAlphabetSymbols().size() == 256;
    }
}
