package org.liuyehcf.grammar.cfg.lr;

import org.liuyehcf.grammar.LexicalAnalyzer;
import org.liuyehcf.grammar.cfg.AbstractCfgParser;
import org.liuyehcf.grammar.core.definition.*;
import org.liuyehcf.grammar.core.definition.converter.AugmentedGrammarConverter;
import org.liuyehcf.grammar.core.definition.converter.GrammarConverterPipelineImpl;
import org.liuyehcf.grammar.core.definition.converter.MergeGrammarConverter;
import org.liuyehcf.grammar.core.definition.converter.StatusExpandGrammarConverter;
import org.liuyehcf.grammar.utils.ListUtils;

import java.util.*;
import java.util.stream.Collectors;

import static org.liuyehcf.grammar.utils.AssertUtils.*;

public class LR0 extends AbstractCfgParser implements LRParser {
    // 项目集闭包
    protected List<Closure> closures = new ArrayList<>();
    // 状态转移表 [ClosureId, Symbol] -> ClosureId
    protected Map<Integer, Map<Symbol, Integer>> closureTransferTable = new HashMap<>();
    // 预测分析表正常情况下，表项只有一个，若出现多个，则说明有冲突
    // [ClosureId, Symbol] -> Operation
    protected Map<Integer, Map<Symbol, LinkedHashSet<Operation>>> analysisTable = new HashMap<>();
    // 预测分析表中的所有输入符号，与Grammar中的符号有些不一样
    protected List<Symbol> analysisSymbols = new ArrayList<>();
    protected List<Symbol> analysisTerminators = new ArrayList<>();
    private int closureCnt = 0;

    protected LR0(LexicalAnalyzer lexicalAnalyzer, Grammar originalGrammar) {
        super(lexicalAnalyzer, originalGrammar, GrammarConverterPipelineImpl
                .builder()
                .registerGrammarConverter(AugmentedGrammarConverter.class)
                .registerGrammarConverter(StatusExpandGrammarConverter.class)
                .registerGrammarConverter(MergeGrammarConverter.class)
                .build());
    }

    public static LRParser create(LexicalAnalyzer lexicalAnalyzer, Grammar originalGrammar) {
        LRParser parser = new LR0(lexicalAnalyzer, originalGrammar);

        parser.init();

        return parser;
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

    protected static Symbol nextSymbol(PrimaryProduction _PP) {

        assertTrue(_PP.getRight().getIndexOfDot() != -1);

        if (_PP.getRight().getIndexOfDot() == _PP.getRight().getSymbols().size()) {
            return null;
        }

        return _PP.getRight().getSymbols().get(_PP.getRight().getIndexOfDot());
    }

    protected static PrimaryProduction removeDot(PrimaryProduction _PP) {
        assertTrue(_PP.getRight().getIndexOfDot() != -1);

        return PrimaryProduction.create(
                _PP.getLeft(),
                SymbolString.create(
                        _PP.getRight().getSymbols()
                )
        );
    }

    @Override
    protected void postInit() {
        // 初始化项目集闭包
        initClosure();

        // 初始化分析表
        initAnalysisTable();
    }

    @Override
    public boolean matches(String input) {
        return new Matcher().matches(input);
    }

    @Override
    public String getClosureJSONString() {
        StringBuilder sb = new StringBuilder();

        sb.append('{');

        for (int i = 0; i < closures.size(); i++) {
            sb.append('\"')
                    .append(i)
                    .append('\"')
                    .append(':')
                    .append('[');

            for (int j = 0; j < closures.get(i).getItems().size(); j++) {
                assertNull(closures.get(i).getItems().get(j).getLookAHeads());
                sb.append('\"')
                        .append(closures.get(i).getItems().get(j).getPrimaryProduction().toJSONString())
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
    public String getAnalysisTableMarkdownString() {
        StringBuilder sb = new StringBuilder();

        String separator = "|";

        // 第一行：表头，各个终结符符号以及非终结符号

        sb.append(separator)
                .append(' ')
                .append("状态\\文法符号")
                .append(' ');

        for (Symbol symbol : analysisTerminators) {
            sb.append(separator)
                    .append(' ')
                    .append(symbol.toJSONString())
                    .append(' ');
        }

        sb.append(separator)
                .append(' ')
                .append(Symbol.DOLLAR.toJSONString())
                .append(' ');


        for (Symbol symbol : this.grammar.getNonTerminators()) {
            if (Symbol.START.equals(symbol)) {
                continue;
            }
            sb.append(separator)
                    .append(' ')
                    .append(symbol.toJSONString())
                    .append(' ');
        }

        sb.append(separator).append('\n');

        // 第二行：对齐格式
        sb.append(separator);

        for (int i = 0; i < analysisSymbols.size(); i++) {
            sb.append(":--")
                    .append(separator);
        }
        sb.append(":--")
                .append(separator);

        sb.append('\n');

        // 其余行：转义表
        for (int i = 0; i < closures.size(); i++) {
            int closureId = closures.get(i).getId();

            sb.append(separator)
                    .append(' ')
                    .append(i)
                    .append(' ');

            for (Symbol symbol : analysisSymbols) {
                LinkedHashSet<Operation> operations = analysisTable.get(closureId).get(symbol);
                if (operations.isEmpty()) {
                    sb.append(separator)
                            .append(' ')
                            .append("\\")
                            .append(' ');
                } else {
                    sb.append(separator);
                    for (Operation operation : operations) {
                        if (operation.getOperator() == Operation.OperationCode.ACCEPT
                                || operation.getOperator() == Operation.OperationCode.REDUCTION) {
                            sb.append(' ')
                                    .append(operation.getOperator())
                                    .append(" \"")
                                    .append(operation.getPrimaryProduction())
                                    .append('\"')
                                    .append(" /");
                        } else {
                            sb.append(' ')
                                    .append(operation.getOperator())
                                    .append(" \"")
                                    .append(operation.getNextClosureId())
                                    .append('\"')
                                    .append(" /");
                        }
                    }
                    assertTrue(sb.charAt(sb.length() - 1) == '/');
                    sb.setLength(sb.length() - 1);
                }
            }

            sb.append(separator).append('\n');
        }


        return sb.toString();
    }

    @Override
    public String getClosureTransferTableJSONString() {
        StringBuilder sb = new StringBuilder();
        int cnt = 1;

        sb.append('{');

        for (int i = 0; i < closures.size(); i++) {
            int closureId = closures.get(i).getId();

            for (Symbol symbol : analysisSymbols) {
                if (closureTransferTable.get(closureId) != null
                        && closureTransferTable.get(closureId).get(symbol) != null) {
                    sb.append('\"')
                            .append(cnt++)
                            .append("\"")
                            .append(":")
                            .append('\"')
                            .append('[')
                            .append(closureId)
                            .append(", ")
                            .append(symbol.toJSONString())
                            .append(']')
                            .append(" → ")
                            .append(closureTransferTable.get(closureId).get(symbol))
                            .append('\"')
                            .append(',');
                }
            }

            sb.setLength(sb.length() - 1);

            sb.append(',');
        }

        sb.setLength(sb.length() - 1);

        sb.append('}');

        return sb.toString();
    }

    private void initClosure() {
        PrimaryProduction _PPStart; // origin production

        assertTrue(getProductionMap().get(Symbol.START).getPrimaryProductions().size() == 2);

        if (getProductionMap().get(Symbol.START).getPrimaryProductions().get(0) // 第一个子产生式
                .getRight().getIndexOfDot() == 0) {
            _PPStart = getProductionMap().get(Symbol.START).getPrimaryProductions().get(0);
        } else {
            _PPStart = getProductionMap().get(Symbol.START).getPrimaryProductions().get(1);
        }

        boolean canBreak = false;

        // 初始化，添加闭包0
        closures.add(closure(ListUtils.of(_PPStart)));

        while (!canBreak) {
            canBreak = true;

            int preSize = closures.size();

            // 避免遍历时修改容器，因此不用foreach
            for (int i = 0; i < preSize; i++) {
                Closure preClosure = closures.get(i);

                // 同一个闭包下的不同项目，如果下一个符号相同，那么这些项目的后继项目作为下一个闭包的核心项目集合
                // 这个Map就是用于保存: 输入符号 -> 后继闭包的核心项目集合 的映射关系
                Map<Symbol, List<PrimaryProduction>> successorMap = new LinkedHashMap<>();

                // 遍历闭包中的产生式，初始化successorMap
                for (Item preItem : preClosure.getItems()) {
                    PrimaryProduction _PPre = preItem.getPrimaryProduction();
                    PrimaryProduction _PPNext = successor(_PPre);

                    // 有后继
                    if (_PPNext != null) {
                        Symbol nextSymbol = nextSymbol(_PPre);
                        assertNotNull(nextSymbol);

                        if (!successorMap.containsKey(nextSymbol)) {
                            successorMap.put(nextSymbol, new ArrayList<>());
                        }

                        successorMap.get(nextSymbol).add(_PPNext);
                    }
                }

                // 创建Closure，维护状态转移表
                for (Map.Entry<Symbol, List<PrimaryProduction>> entry : successorMap.entrySet()) {
                    Symbol nextSymbol = entry.getKey();
                    List<PrimaryProduction> _PPCores = entry.getValue();

                    List<Item> coreItemsOfNextClosure = new ArrayList<>();

                    for (PrimaryProduction _PP : _PPCores) {
                        coreItemsOfNextClosure.add(new Item(_PP, null));
                    }

                    Closure nextClosure;
                    int existsClosureId;

                    if ((existsClosureId = indexOf(coreItemsOfNextClosure)) == -1) {
                        closures.add(closure(_PPCores));
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

    private Closure closure(List<PrimaryProduction> _PPs) {
        List<Item> coreItems = new ArrayList<>();

        for (PrimaryProduction _PP : _PPs) {
            coreItems.add(new Item(_PP, null));
        }

        Set<Item> items = new LinkedHashSet<>(coreItems);

        boolean canBreak = false;

        while (!canBreak) {
            int preSize = items.size();

            // 遍历Set的时候不能进行写操作
            List<Item> newAddedItems = new ArrayList<>();
            for (Item item : items) {
                PrimaryProduction _PP = item.getPrimaryProduction();
                Symbol nextSymbol = nextSymbol(_PP);

                // '·'后面跟的是非终结符
                if (nextSymbol != null
                        && !nextSymbol.isTerminator()) {

                    newAddedItems.addAll(findBeginItems(nextSymbol));
                }
            }

            items.addAll(newAddedItems);

            if (preSize == items.size()) {
                canBreak = true;
            }
        }

        return new Closure(closureCnt++, coreItems, new ArrayList<>(items));
    }

    private int indexOf(List<Item> coreItems) {
        for (int i = 0; i < closures.size(); i++) {
            if (closures.get(i).isSame(coreItems)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 找到形如 A → ·α 的产生式
     */
    private List<Item> findBeginItems(Symbol symbol) {
        List<Item> result = new ArrayList<>();

        Production _P = getProductionMap().get(symbol);

        for (PrimaryProduction _PP : _P.getPrimaryProductions()) {
            if (_PP.getRight().getIndexOfDot() == 0
                    || SymbolString.EPSILON_END.equals(_PP.getRight())) {
                result.add(new Item(_PP, null));
            }
        }

        return result;
    }

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
                                    analysisTable.get(closure.getId())
                                            .get(terminator)
                                            .add(new Operation(
                                                    -1,
                                                    _PPRaw,
                                                    Operation.OperationCode.REDUCTION));
                                }
                                analysisTable.get(closure.getId())
                                        .get(Symbol.DOLLAR)
                                        .add(new Operation(
                                                -1,
                                                _PPRaw,
                                                Operation.OperationCode.REDUCTION));
                            }
                        } else if (nextSymbol.isTerminator()) {
                            analysisTable.get(closure.getId())
                                    .get(nextSymbol)
                                    .add(new Operation(
                                            closureTransferTable.get(closure.getId()).get(nextSymbol),
                                            null,
                                            Operation.OperationCode.MOVE_IN));
                        } else {
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

        // 检查合法性，即检查表项动作是否唯一
        for (int i = 0; i < closures.size(); i++) {
            int closureId = closures.get(i).getId();
            for (Symbol symbol : analysisSymbols) {
                if (analysisTable.get(closureId).get(symbol).size() > 1) {
                    System.err.println("Conflict");
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
            if (analysisTable.get(closureId).get(symbol).isEmpty()) return null;
            assertTrue(analysisTable.get(closureId).get(symbol).size() == 1);
            return analysisTable.get(closureId).get(symbol).iterator().next();
        }

    }
}
