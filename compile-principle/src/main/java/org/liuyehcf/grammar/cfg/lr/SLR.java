package org.liuyehcf.grammar.cfg.lr;

import org.liuyehcf.grammar.LexicalAnalyzer;
import org.liuyehcf.grammar.core.definition.Grammar;
import org.liuyehcf.grammar.core.definition.PrimaryProduction;
import org.liuyehcf.grammar.core.definition.Symbol;

public class SLR extends LR0 implements LRParser {

    private SLR(LexicalAnalyzer lexicalAnalyzer, Grammar originalGrammar) {
        super(lexicalAnalyzer, originalGrammar);
    }

    public static LRParser create(LexicalAnalyzer lexicalAnalyzer, Grammar originalGrammar) {
        LRParser parser = new SLR(lexicalAnalyzer, originalGrammar);

        parser.init();

        return parser;
    }

    @Override
    protected void initAnalysisTableWithReduction(Closure closure, PrimaryProduction _PP) {
        PrimaryProduction _PPRaw = removeDot(_PP);

        if ((Symbol.START.equals(_PP.getLeft()))) {
            analysisTable.get(closure.getId())
                    .get(Symbol.DOLLAR)
                    .add(new Operation(
                            -1,
                            _PPRaw,
                            Operation.OperationCode.ACCEPT));
        } else {

            for (Symbol terminator : analysisTerminators) {
                if (getFollows().get(_PPRaw.getLeft()).contains(terminator)) {

                    analysisTable.get(closure.getId())
                            .get(terminator)
                            .add(new Operation(
                                    -1,
                                    _PPRaw,
                                    Operation.OperationCode.REDUCTION));
                }
            }
            if (getFollows().get(_PPRaw.getLeft()).contains(Symbol.DOLLAR)) {
                analysisTable.get(closure.getId())
                        .get(Symbol.DOLLAR)
                        .add(new Operation(
                                -1,
                                _PPRaw,
                                Operation.OperationCode.REDUCTION));
            }
        }
    }

    @Override
    public boolean matches(String input) {
        return false;
    }
}
