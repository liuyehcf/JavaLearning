package org.liuyehcf.grammar.cfg.lr;

import org.liuyehcf.grammar.LexicalAnalyzer;
import org.liuyehcf.grammar.core.definition.*;

import java.util.ArrayList;
import java.util.List;

import static org.liuyehcf.grammar.utils.AssertUtils.assertNotNull;
import static org.liuyehcf.grammar.utils.AssertUtils.assertTrue;

public class LR0 extends AbstractLRParser {


    LR0(LexicalAnalyzer lexicalAnalyzer, Grammar originalGrammar) {
        super(lexicalAnalyzer, originalGrammar, false);
    }

    public static LRParser create(LexicalAnalyzer lexicalAnalyzer, Grammar originalGrammar) {
        LR0 parser = new LR0(lexicalAnalyzer, originalGrammar);

        parser.init();

        return parser;
    }

    Item createFirstItem() {
        PrimaryProduction _PPStart; // origin production

        assertTrue(getProductionMap().get(Symbol.START).getPrimaryProductions().size() == 2);

        if (getProductionMap().get(Symbol.START).getPrimaryProductions().get(0) // 第一个子产生式
                .getRight().getIndexOfDot() == 0) {
            _PPStart = getProductionMap().get(Symbol.START).getPrimaryProductions().get(0);
        } else {
            _PPStart = getProductionMap().get(Symbol.START).getPrimaryProductions().get(1);
        }

        return new Item(_PPStart, null);
    }

    @Override
    List<Item> findEqualItems(Item item) {

        /*
         * 对于每个 "A → α · B β"
         * 找出所有的 "B → · γ"
         */

        Symbol nextSymbol = nextSymbol(item);
        assertNotNull(nextSymbol);

        List<Item> result = new ArrayList<>();

        Production _P = getProductionMap().get(nextSymbol);

        for (PrimaryProduction _PP : _P.getPrimaryProductions()) {
            if (_PP.getRight().getIndexOfDot() == 0
                    || SymbolString.EPSILON_END.equals(_PP.getRight())) {
                result.add(new Item(_PP, null));
            }
        }

        return result;
    }


    @Override
    void initAnalysisTableWithReduction(Closure closure, Item item) {
        PrimaryProduction _PP = item.getPrimaryProduction();
        PrimaryProduction _PPRaw = removeDot(_PP);

        if ((Symbol.START.equals(_PP.getLeft()))) {
            addOperationToAnalysisTable(
                    closure.getId(),
                    Symbol.DOLLAR,
                    new Operation(
                            -1,
                            _PPRaw,
                            Operation.OperationCode.ACCEPT)
            );
        } else {
            for (Symbol terminator : getAnalysisTerminators()) {
                addOperationToAnalysisTable(
                        closure.getId(),
                        terminator,
                        new Operation(
                                -1,
                                _PPRaw,
                                Operation.OperationCode.REDUCTION)
                );
            }
        }
    }

}
