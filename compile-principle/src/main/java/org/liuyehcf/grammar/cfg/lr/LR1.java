package org.liuyehcf.grammar.cfg.lr;

import org.liuyehcf.grammar.LexicalAnalyzer;
import org.liuyehcf.grammar.core.definition.*;
import org.liuyehcf.grammar.utils.SetUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.liuyehcf.grammar.utils.AssertUtils.*;

public class LR1 extends AbstractLRParser {

    private LR1(LexicalAnalyzer lexicalAnalyzer, Grammar originalGrammar) {
        super(lexicalAnalyzer, originalGrammar);
    }

    public static LRParser create(LexicalAnalyzer lexicalAnalyzer, Grammar originalGrammar) {
        LR1 parser = new LR1(lexicalAnalyzer, originalGrammar);

        parser.init();

        return parser;
    }

    @Override
    Item createFirstItem() {
        PrimaryProduction _PPStart; // origin production

        assertTrue(getProductionMap().get(Symbol.START).getPrimaryProductions().size() == 2);

        if (getProductionMap().get(Symbol.START).getPrimaryProductions().get(0) // 第一个子产生式
                .getRight().getIndexOfDot() == 0) {
            _PPStart = getProductionMap().get(Symbol.START).getPrimaryProductions().get(0);
        } else {
            _PPStart = getProductionMap().get(Symbol.START).getPrimaryProductions().get(1);
        }

        return new Item(_PPStart, SetUtils.of(Symbol.DOLLAR));
    }

    @Override
    List<Item> findEqualItems(Item item) {

        /*
         * 对于每个 "[A → α · B β, a]"
         * 找出所有的 "[B → · γ, b]"，其中 "b ∈ FIRST(βa)"
         */

        Symbol nextSymbol = nextSymbol(item);
        Item nextItem = successor(item);
        assertNotNull(nextSymbol);
        assertNotNull(nextItem);

        Symbol secondNextSymbol = nextSymbol(nextItem);

        Set<Symbol> lookAHeadsA = item.getLookAHeads();
        assertFalse(lookAHeadsA.isEmpty());

        Set<Symbol> lookAHeadsB;

        // 此时展望符包含A，"β -*> ε"
        if (secondNextSymbol == null
                || getFirsts().get(secondNextSymbol).contains(Symbol.EPSILON)) {
            // 此时展望符就是 "FIRST(β) + a - ε"
            lookAHeadsB = SetUtils.extract(
                    SetUtils.of(
                            lookAHeadsA,
                            secondNextSymbol == null ? new HashSet<>() : getFirsts().get(secondNextSymbol)
                    )
                    , Symbol.EPSILON
            );
        } else {
            // 此时展望符就是 "FIRST(β)"
            lookAHeadsB = new HashSet<>(getFirsts().get(secondNextSymbol));
        }

        Production _P = getProductionMap().get(nextSymbol);

        List<Item> result = new ArrayList<>();

        for (PrimaryProduction _PP : _P.getPrimaryProductions()) {
            if (_PP.getRight().getIndexOfDot() == 0
                    || SymbolString.EPSILON_END.equals(_PP.getRight())) {
                result.add(new Item(_PP, lookAHeadsB));
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
            for (Symbol terminator : item.getLookAHeads()) {
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
