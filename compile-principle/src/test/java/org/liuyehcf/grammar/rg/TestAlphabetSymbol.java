package org.liuyehcf.grammar.rg;

import org.junit.Test;
import org.liuyehcf.grammar.rg.utils.SymbolUtils;

import static org.liuyehcf.grammar.utils.AssertUtils.assertTrue;

/**
 * Created by Liuye on 2017/10/26.
 */
public class TestAlphabetSymbol {
    @Test
    public void testAlphabetSymbols() {
        assertTrue(SymbolUtils.getAlphabetSymbols().size() == 256);
    }
}
