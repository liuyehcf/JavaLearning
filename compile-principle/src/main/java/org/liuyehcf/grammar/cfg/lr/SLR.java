package org.liuyehcf.grammar.cfg.lr;

import org.liuyehcf.grammar.LexicalAnalyzer;
import org.liuyehcf.grammar.core.definition.Grammar;
import org.liuyehcf.grammar.core.definition.PrimaryProduction;
import org.liuyehcf.grammar.core.definition.Symbol;

public class SLR extends LR0 {

    private SLR(LexicalAnalyzer lexicalAnalyzer, Grammar originalGrammar) {
        super(lexicalAnalyzer, originalGrammar);
    }

    public static LRParser create(LexicalAnalyzer lexicalAnalyzer, Grammar originalGrammar) {
        SLR parser = new SLR(lexicalAnalyzer, originalGrammar);

        parser.init();

        return parser;
    }

    @Override
    protected void initAnalysisTableWithReduction(Closure closure, Item item) {
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
                if (getFollows().get(_PPRaw.getLeft()).contains(terminator)) {
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
}
