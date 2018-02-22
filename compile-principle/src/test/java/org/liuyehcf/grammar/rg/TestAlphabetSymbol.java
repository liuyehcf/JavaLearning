package org.liuyehcf.grammar.rg;

import org.junit.Test;
import org.liuyehcf.grammar.rg.utils.SymbolUtils;

/**
 * Created by Liuye on 2017/10/26.
 */
public class TestAlphabetSymbol {
    @Test
    public void testAlphabetSymbols() {
        assert SymbolUtils.getAlphabetSymbols().size() == 256;
    }
}
