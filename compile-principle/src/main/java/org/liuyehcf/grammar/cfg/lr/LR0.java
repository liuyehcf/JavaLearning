package org.liuyehcf.grammar.cfg.lr;

import org.liuyehcf.grammar.LexicalAnalyzer;
import org.liuyehcf.grammar.core.definition.*;
import org.liuyehcf.grammar.core.definition.converter.*;

import java.util.*;

import static org.liuyehcf.grammar.utils.AssertUtils.*;

public class LR0 implements LRParser {
    // 词法分析器
    private final LexicalAnalyzer lexicalAnalyzer;

    // 原始文法
    private final Grammar originalGrammar;

    // 文法转换流水线
    private final GrammarConverterPipeline grammarConverterPipeline;

    // 转换后的文法
    private Grammar grammar;

    // 非终结符 -> 产生式的映射
    private Map<Symbol, Production> symbolProductionMap = new HashMap<>();

    // 项目集闭包
    private List<Closure> closures = new ArrayList<>();

    // 状态转移表 [ClosureId, Symbol] -> ClosureId
    private Map<Integer, Map<Symbol, Integer>> closureTransferTable = new HashMap<>();

    // 预测分析表
    private Map<Integer, Map<Symbol, Operation>> analysisTable = new HashMap<>();

    public LR0(Grammar grammar, LexicalAnalyzer lexicalAnalyzer) {
        this.originalGrammar = grammar;
        this.grammarConverterPipeline = GrammarConverterPipelineImpl
                .builder()
                .registerGrammarConverter(AugmentedGrammarConverter.class)
                .registerGrammarConverter(StatusExpandGrammarConverter.class)
                .registerGrammarConverter(MergeGrammarConverter.class)
                .build();
        this.lexicalAnalyzer = lexicalAnalyzer;
        init();
    }

    private static PrimaryProduction successor(PrimaryProduction _PP) {

        assertTrue(_PP.getRight().getIndexOfDot() != -1);

        if (_PP.getRight().getIndexOfDot() == _PP.getRight().getSymbols().size()) {
            return null;
        }

        return PrimaryProduction.create(
                _PP.getLeft(),
                SymbolString.create(
                        _PP.getRight().getSymbols(),
                        _PP.getRight().getIndexOfDot() + 1
                )

        );
    }

    private static Symbol nextSymbol(PrimaryProduction _PP) {

        assertTrue(_PP.getRight().getIndexOfDot() != -1);

        if (_PP.getRight().getIndexOfDot() == _PP.getRight().getSymbols().size()) {
            return null;
        }

        return _PP.getRight().getSymbols().get(_PP.getRight().getIndexOfDot());
    }

    private static PrimaryProduction removeDot(PrimaryProduction _PP) {
        assertTrue(_PP.getRight().getIndexOfDot() != -1);

        return PrimaryProduction.create(
                _PP.getLeft(),
                SymbolString.create(
                        _PP.getRight().getSymbols()
                )
        );
    }

    private void init() {
        // 文法转换
        convertGrammar();

        // 初始化项目集闭包
        initClosure();

        // 初始化分析表
        initAnalysisTable();
    }

    private void convertGrammar() {
        this.grammar = grammarConverterPipeline.convert(originalGrammar);

        // 初始化symbolProductionMap
        for (Production _P : grammar.getProductions()) {
            Symbol left = _P.getLeft();
            assertFalse(symbolProductionMap.containsKey(left));
            symbolProductionMap.put(left, _P);
        }
    }

    @Override
    public boolean matches(String input) {
        return new Matcher().matches(input);
    }

    @Override
    public String getClosureStatus() {
        StringBuilder sb = new StringBuilder();

        sb.append('{');

        for (int i = 0; i < closures.size(); i++) {
            sb.append('\"')
                    .append(i)
                    .append('\"')
                    .append(':')
                    .append('[');

            for (int j = 0; j < closures.get(i).getPrimaryProductions().size(); j++) {
                sb.append('\"')
                        .append(closures.get(i).getPrimaryProductions().get(j).toJSONString())
                        .append('\"')
                        .append(',');
            }

            sb.setLength(sb.length() - 1);

            sb.append(']')
                    .append(',');
        }
        sb.setLength(sb.length() - 1);

        sb.append('}');

        return sb.toString();
    }

    @Override
    public String getForecastAnalysisTable() {
        List<Symbol> symbols = new ArrayList<>();
        StringBuilder sb = new StringBuilder();

        String separator = "|";

        // 第一行：表头，各个终结符符号以及非终结符号

        sb.append(separator)
                .append(' ')
                .append("状态\\文法符号")
                .append(' ');

        for (Symbol symbol : this.grammar.getTerminators()) {
            sb.append(separator)
                    .append(' ')
                    .append(symbol.toJSONString())
                    .append(' ');
            symbols.add(symbol);
        }

        sb.append(separator)
                .append(' ')
                .append(Symbol.DOLLAR.toJSONString())
                .append(' ');
        symbols.add(Symbol.DOLLAR);


        for (Symbol symbol : this.grammar.getNonTerminators()) {
            if (Symbol.START.equals(symbol)) {
                continue;
            }
            sb.append(separator)
                    .append(' ')
                    .append(symbol.toJSONString())
                    .append(' ');
            symbols.add(symbol);
        }

        sb.append(separator).append('\n');

        // 第二行：对齐格式
        sb.append(separator);

        for (int i = 0; i < symbols.size(); i++) {
            sb.append(":--")
                    .append(separator);
        }
        sb.append(":--")
                .append(separator);

        sb.append('\n');

        // 其余行：转义表
        for (int i = 0; i < closures.size(); i++) {
            sb.append(separator)
                    .append(' ')
                    .append(i)
                    .append(' ');

            for (Symbol symbol : symbols) {
                Operation operation = analysisTable.get(i).get(symbol);
                if (operation == null) {
                    sb.append(separator)
                            .append(' ')
                            .append("\\")
                            .append(' ');
                } else {
                    if (operation.getOperator() == Operation.OperationCode.ACCEPT
                            || operation.getOperator() == Operation.OperationCode.REDUCTION) {
                        sb.append(separator)
                                .append(' ')
                                .append(operation.getOperator()).append(" \"").append(operation.getPrimaryProduction()).append('\"')
                                .append(' ');
                    } else {
                        sb.append(separator)
                                .append(' ')
                                .append(operation.getOperator()).append(" \"").append(operation.getNextClosureId()).append('\"')
                                .append(' ');
                    }

                }
            }

            sb.append(separator).append('\n');
        }


        return sb.toString();
    }

    @Override
    public Grammar getGrammar() {
        return grammar;
    }

    private void initClosure() {
        PrimaryProduction _PPOrigin; // origin production

        assertTrue(symbolProductionMap.get(Symbol.START).getPrimaryProductions().size() == 2);

        if (symbolProductionMap.get(Symbol.START).getPrimaryProductions().get(0) // 第一个子产生式
                .getRight().getIndexOfDot() == 0) {
            _PPOrigin = symbolProductionMap.get(Symbol.START).getPrimaryProductions().get(0);
        } else {
            _PPOrigin = symbolProductionMap.get(Symbol.START).getPrimaryProductions().get(1);
        }

        boolean canBreak = false;

        // 初始化，添加闭包0
        closures.add(closure(_PPOrigin));

        while (!canBreak) {
            canBreak = true;

            int preSize = closures.size();
            // 避免遍历时修改容器
            for (int i = 0; i < preSize; i++) {
                Closure preClosure = closures.get(i);

                // 遍历闭包中的产生式
                for (PrimaryProduction _PPre : preClosure.getPrimaryProductions()) {

                    PrimaryProduction _PPNext = successor(_PPre);

                    // 有后继
                    if (_PPNext != null) {
                        Symbol nextSymbol = nextSymbol(_PPre);
                        assertNotNull(nextSymbol);

                        Closure nextClosure;

                        int existsClosureId;

                        if ((existsClosureId = indexOf(_PPNext)) == -1) {
                            closures.add(closure(_PPNext));
                            nextClosure = closures.get(closures.size() - 1);
                        } else {
                            nextClosure = closures.get(existsClosureId);
                        }

                        if (!closureTransferTable.containsKey(preClosure.getId())) {
                            closureTransferTable.put(preClosure.getId(), new HashMap<>());
                        }

                        assertTrue(!closureTransferTable.get(preClosure.getId()).containsKey(nextSymbol)
                                || closureTransferTable.get(preClosure.getId()).get(nextSymbol).equals(nextClosure.getId()));

                        if (!closureTransferTable.get(preClosure.getId()).containsKey(nextSymbol)) {
                            closureTransferTable.get(preClosure.getId()).put(nextSymbol, nextClosure.getId());
                            canBreak = false;
                        }
                    }
                }
            }
        }
    }

    private Closure closure(PrimaryProduction _PPOrigin) {
        Set<PrimaryProduction> primaryProductions = new LinkedHashSet<>();

        primaryProductions.add(_PPOrigin);

        boolean canBreak = false;

        while (!canBreak) {
            int preSize = primaryProductions.size();

            // 遍历Set的时候不能进行写操作
            List<PrimaryProduction> newAddedPrimaryProductions = new ArrayList<>();
            for (PrimaryProduction _PP : primaryProductions) {

                Symbol nextSymbol = nextSymbol(_PP);

                // '·'后面跟的是非终结符
                if (nextSymbol != null
                        && !nextSymbol.isTerminator()) {

                    newAddedPrimaryProductions.addAll(findOriginalStatusPrimaryProductions(nextSymbol));
                }
            }

            primaryProductions.addAll(newAddedPrimaryProductions);

            if (preSize == primaryProductions.size()) {
                canBreak = true;
            }
        }

        return new Closure(_PPOrigin, new ArrayList<>(primaryProductions));
    }

    private int indexOf(PrimaryProduction _PP) {
        for (int i = 0; i < closures.size(); i++) {
            if (closures.get(i).isCorePrimaryProduction(_PP)) {
                return i;
            }
        }
        return -1;
    }

    private List<PrimaryProduction> findOriginalStatusPrimaryProductions(Symbol symbol) {
        List<PrimaryProduction> result = new ArrayList<>();

        Production _P = symbolProductionMap.get(symbol);

        for (PrimaryProduction _PP : _P.getPrimaryProductions()) {
            if (_PP.getRight().getIndexOfDot() == 0) {
                result.add(_PP);
            }
        }

        return result;
    }

    private void initAnalysisTable() {
        // 初始化
        for (int i = 0; i < closures.size(); i++) {
            analysisTable.put(i, new HashMap<>());
        }

        for (Production _P : symbolProductionMap.values()) {
            for (PrimaryProduction _PP : _P.getPrimaryProductions()) {
                for (Closure closure : closures) {
                    if (closure.getPrimaryProductions().contains(_PP)) {
                        Symbol nextSymbol = nextSymbol(_PP);
                        PrimaryProduction _PPRaw = removeDot(_PP);

                        if (nextSymbol == null) {

                            if ((Symbol.START.equals(_PP.getLeft()))) {
                                analysisTable.get(closure.getId()).put(
                                        Symbol.DOLLAR,
                                        new Operation(
                                                -1,
                                                _PPRaw,
                                                Operation.OperationCode.ACCEPT
                                        )
                                );
                            } else {

                                for (Symbol terminator : this.grammar.getTerminators()) {
                                    analysisTable.get(closure.getId()).put(
                                            terminator,
                                            new Operation(
                                                    -1,
                                                    _PPRaw,
                                                    Operation.OperationCode.REDUCTION
                                            )
                                    );
                                }

                                analysisTable.get(closure.getId()).put(
                                        Symbol.DOLLAR,
                                        new Operation(
                                                -1,
                                                _PPRaw,
                                                Operation.OperationCode.REDUCTION
                                        )
                                );
                            }
                        } else if (nextSymbol.isTerminator()) {
                            analysisTable.get(closure.getId()).put(
                                    nextSymbol,
                                    new Operation(
                                            closureTransferTable.get(closure.getId()).get(nextSymbol),
                                            null,
                                            Operation.OperationCode.MOVE_IN
                                    )
                            );
                        } else {
                            analysisTable.get(closure.getId()).put(
                                    nextSymbol,
                                    new Operation(
                                            closureTransferTable.get(closure.getId()).get(nextSymbol),
                                            null,
                                            Operation.OperationCode.JUMP
                                    )
                            );
                        }
                    }
                }
            }
        }
    }

    private class Matcher {
        private LinkedList<Integer> statusStack;
        private LinkedList<Symbol> symbolStack;
        private Queue<Symbol> remainSymbols;
        private boolean canReceive;
        private Operation operation;

        public boolean matches(String input) {
            statusStack = new LinkedList<>();
            symbolStack = new LinkedList<>();
            remainSymbols = new LinkedList<>();

            statusStack.push(0);
            symbolStack.push(Symbol.DOLLAR);

            LexicalAnalyzer.TokenIterator tokenIterator = lexicalAnalyzer.iterator(input);
            while (tokenIterator.hasNext()) {
                remainSymbols.offer(tokenIterator.next().getId());
            }

            boolean canBreak = false;
            while (!canBreak) {
                operation = lookUp(statusStack.peek(), remainSymbols.peek());

                if (operation == null) {
                    error();
                    canBreak = true;
                } else {
                    switch (operation.getOperator()) {
                        case MOVE_IN:
                            moveIn();
                            break;
                        case REDUCTION:
                            reduction();
                            jump();
                            break;
                        case JUMP:
                            error();
                            break;
                        case ACCEPT:
                            accept();
                            canBreak = true;
                            break;
                        default:
                            error();
                            canBreak = true;
                            break;
                    }
                }
            }

            return canReceive;
        }

        private void moveIn() {
            assertTrue(statusStack.size() == symbolStack.size());
            assertFalse(operation.getNextClosureId() == -1);

            statusStack.push(operation.getNextClosureId());
            symbolStack.push(remainSymbols.poll());
        }

        private void reduction() {
            assertTrue(statusStack.size() == symbolStack.size());

            PrimaryProduction _PPReduction = operation.getPrimaryProduction();
            assertNotNull(_PPReduction);

            for (int i = 0; i < _PPReduction.getRight().getSymbols().size(); i++) {
                statusStack.pop();
                symbolStack.pop();
            }

            symbolStack.push(_PPReduction.getLeft());
        }

        private void jump() {
            assertTrue(statusStack.size() + 1 == symbolStack.size());
            assertTrue(!statusStack.isEmpty() && !symbolStack.isEmpty());

            Operation nextOperation = lookUp(statusStack.peek(), symbolStack.peek());

            assertFalse(nextOperation.getNextClosureId() == -1);

            statusStack.push(nextOperation.getNextClosureId());
        }

        private void accept() {
            canReceive = true;
        }

        private void error() {
            canReceive = false;
        }

        private Operation lookUp(int closureId, Symbol symbol) {
            System.out.println("[" + closureId + ", " + symbol.toJSONString() + "]");

            return analysisTable.get(closureId).get(symbol);
        }

    }
}
