package org.liuyehcf.grammar.cfg.lr;

import org.liuyehcf.grammar.LexicalAnalyzer;
import org.liuyehcf.grammar.core.definition.Grammar;
import org.liuyehcf.grammar.core.definition.PrimaryProduction;
import org.liuyehcf.grammar.core.definition.Production;
import org.liuyehcf.grammar.core.definition.Symbol;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

import static org.liuyehcf.grammar.utils.AssertUtils.assertTrue;

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
    protected void initAnalysisTable() {
        analysisTerminators.addAll(this.grammar.getTerminators().stream().filter(symbol -> !Symbol.EPSILON.equals(symbol)).collect(Collectors.toList()));
        analysisSymbols.addAll(analysisTerminators);
        analysisSymbols.add(Symbol.DOLLAR);
        analysisSymbols.addAll(this.grammar.getNonTerminators().stream().filter((symbol -> !Symbol.START.equals(symbol))).collect(Collectors.toList()));

        // 初始化
        for (int i = 0; i < closures.size(); i++) {
            int closureId = closures.get(i).getId();
            analysisTable.put(closureId, new HashMap<>());
            for (Symbol symbol : analysisSymbols) {
                analysisTable.get(closureId).put(symbol, new LinkedHashSet<>());
            }
        }

        for (Production _P : getProductionMap().values()) {
            for (PrimaryProduction _PP : _P.getPrimaryProductions()) {

                Item item = new Item(_PP, null);

                for (Closure closure : closures) {
                    if (closure.getItems().contains(item)) {
                        Symbol nextSymbol = nextSymbol(_PP);
                        PrimaryProduction _PPRaw = removeDot(_PP);

                        if (nextSymbol == null) {

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
                        } else if (nextSymbol.isTerminator()) {
                            analysisTable.get(closure.getId())
                                    .get(nextSymbol)
                                    .add(new Operation(
                                            closureTransferTable.get(closure.getId()).get(nextSymbol),
                                            null,
                                            Operation.OperationCode.MOVE_IN));
                        } else {
                            assertTrue(analysisTable.containsKey(closure.getId()));
                            analysisTable.get(closure.getId())
                                    .get(nextSymbol)
                                    .add(new Operation(
                                            closureTransferTable.get(closure.getId()).get(nextSymbol),
                                            null,
                                            Operation.OperationCode.JUMP));
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean matches(String input) {
        return false;
    }
}
