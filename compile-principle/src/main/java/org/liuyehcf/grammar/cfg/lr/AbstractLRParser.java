package org.liuyehcf.grammar.cfg.lr;

import org.liuyehcf.grammar.LexicalAnalyzer;
import org.liuyehcf.grammar.cfg.AbstractCfgParser;
import org.liuyehcf.grammar.core.definition.*;
import org.liuyehcf.grammar.core.definition.converter.AugmentedGrammarConverter;
import org.liuyehcf.grammar.core.definition.converter.GrammarConverterPipelineImpl;
import org.liuyehcf.grammar.core.definition.converter.MergeGrammarConverter;
import org.liuyehcf.grammar.core.definition.converter.StatusExpandGrammarConverter;
import org.liuyehcf.grammar.utils.ListUtils;
import org.liuyehcf.grammar.utils.SetUtils;

import java.util.*;
import java.util.stream.Collectors;

import static org.liuyehcf.grammar.utils.AssertUtils.*;

abstract class AbstractLRParser extends AbstractCfgParser implements LRParser {
    // 项目集闭包
    List<Closure> closures = new ArrayList<>();

    // 状态转移表 [ClosureId, Symbol] -> ClosureId
    Map<Integer, Map<Symbol, Integer>> closureTransferTable = new HashMap<>();

    // 预测分析表正常情况下，表项只有一个，若出现多个，则说明有冲突
    // [ClosureId, Symbol] -> Operation
    Map<Integer, Map<Symbol, LinkedHashSet<Operation>>> analysisTable = new HashMap<>();

    // 预测分析表中的所有输入符号，与Grammar中的符号有些不一样
    List<Symbol> analysisSymbols = new ArrayList<>();
    List<Symbol> analysisTerminators = new ArrayList<>();
    int closureCnt = 0;

    AbstractLRParser(LexicalAnalyzer lexicalAnalyzer, Grammar originalGrammar) {
        super(lexicalAnalyzer, originalGrammar, GrammarConverterPipelineImpl
                .builder()
                .registerGrammarConverter(AugmentedGrammarConverter.class)
                .registerGrammarConverter(StatusExpandGrammarConverter.class)
                .registerGrammarConverter(MergeGrammarConverter.class)
                .build());
    }

    static Item successor(Item preItem) {
        PrimaryProduction _PP = preItem.getPrimaryProduction();

        assertTrue(_PP.getRight().getIndexOfDot() != -1);

        if (_PP.getRight().getIndexOfDot() == _PP.getRight().getSymbols().size()) {
            return null;
        }

        return new Item(
                PrimaryProduction.create(
                        _PP.getLeft(),
                        SymbolString.create(
                                _PP.getRight().getSymbols(),
                                _PP.getRight().getIndexOfDot() + 1
                        )

                ),
                preItem.getLookAHeads()
        );
    }

    static Symbol nextSymbol(Item preItem) {
        PrimaryProduction _PP = preItem.getPrimaryProduction();

        assertTrue(_PP.getRight().getIndexOfDot() != -1);

        if (_PP.getRight().getIndexOfDot() == _PP.getRight().getSymbols().size()) {
            return null;
        }

        return _PP.getRight().getSymbols().get(_PP.getRight().getIndexOfDot());
    }

    static PrimaryProduction removeDot(PrimaryProduction _PP) {
        assertTrue(_PP.getRight().getIndexOfDot() != -1);

        return PrimaryProduction.create(
                _PP.getLeft(),
                SymbolString.create(
                        _PP.getRight().getSymbols()
                )
        );
    }

    @Override
    public final boolean matches(String input) {
        return new Matcher().matches(input);
    }

    @Override
    public final String getClosureJSONString() {
        StringBuilder sb = new StringBuilder();

        sb.append('{');

        for (int i = 0; i < closures.size(); i++) {
            sb.append('\"')
                    .append(i)
                    .append('\"')
                    .append(':')
                    .append('[');

            for (int j = 0; j < closures.get(i).getItems().size(); j++) {
                if (closures.get(i).getItems().get(j).getLookAHeads() == null) {
                    sb.append('\"')
                            .append(closures.get(i).getItems().get(j).getPrimaryProduction().toJSONString())
                            .append('\"');
                } else {
                    assertFalse(closures.get(i).getItems().get(j).getLookAHeads().isEmpty());
                    sb.append('\"')
                            .append(closures.get(i).getItems().get(j).getPrimaryProduction().toJSONString())
                            .append(", ")
                            .append('[');

                    for (Symbol symbol : closures.get(i).getItems().get(j).getLookAHeads()) {
                        sb.append(symbol.toJSONString())
                                .append(", ");
                    }
                    sb.setLength(sb.length() - 2);

                    sb.append(']')
                            .append('\"');

                }

                sb.append(',');
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
    public final String getAnalysisTableMarkdownString() {
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
                                    .append(operation.getPrimaryProduction().toJSONString())
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
    public final String getClosureTransferTableJSONString() {
        StringBuilder sb = new StringBuilder();
        int cnt = 1;

        sb.append('{');

        for (Closure closure : closures) {

            for (Symbol symbol : analysisSymbols) {
                if (closureTransferTable.get(closure.getId()) != null
                        && closureTransferTable.get(closure.getId()).get(symbol) != null) {
                    sb.append('\"')
                            .append(cnt++)
                            .append("\"")
                            .append(":")
                            .append('\"')
                            .append('[')
                            .append(closure.getId())
                            .append(", ")
                            .append(symbol.toJSONString())
                            .append(']')
                            .append(" → ")
                            .append(closureTransferTable.get(closure.getId()).get(symbol))
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

    @Override
    protected final void postInit() {
        // 初始化项目集闭包
        initClosure();

        // 初始化分析表
        initAnalysisTable();
    }

    private void initClosure() {
        // 初始化，添加闭包0
        closures.add(closure(ListUtils.of(createFirstItem())));

        boolean canBreak = false;

        while (!canBreak) {
            canBreak = true;

            int preSize = closures.size();

            // 避免遍历时修改容器，因此不用foreach
            for (int i = 0; i < preSize; i++) {
                Closure preClosure = closures.get(i);

                // 同一个闭包下的不同项目，如果下一个符号相同，那么这些项目的后继项目作为下一个闭包的核心项目集合
                // 这个Map就是用于保存: 输入符号 -> 后继闭包的核心项目集合 的映射关系
                Map<Symbol, List<Item>> successorMap = new LinkedHashMap<>();

                // 遍历闭包中的产生式，初始化successorMap
                for (Item preItem : preClosure.getItems()) {
                    Item nextItem = successor(preItem);

                    // 有后继
                    if (nextItem != null) {
                        Symbol nextSymbol = nextSymbol(preItem);
                        assertNotNull(nextSymbol);

                        if (!successorMap.containsKey(nextSymbol)) {
                            successorMap.put(nextSymbol, new ArrayList<>());
                        }

                        successorMap.get(nextSymbol).add(nextItem);
                    }
                }

                // 创建Closure，维护状态转移表
                for (Map.Entry<Symbol, List<Item>> entry : successorMap.entrySet()) {
                    Symbol nextSymbol = entry.getKey();
                    List<Item> coreItemsOfNextClosure = entry.getValue();


                    Closure nextClosure;
                    int existsClosureId;

                    if ((existsClosureId = indexOf(coreItemsOfNextClosure)) == -1) {
                        closures.add(closure(coreItemsOfNextClosure));
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

    abstract Item createFirstItem();

    private Closure closure(List<Item> coreItems) {
        Set<Item> items = new LinkedHashSet<>(coreItems);

        boolean canBreak = false;

        while (!canBreak) {
            int preSize = items.size();

            // 遍历Set的时候不能进行写操作
            List<Item> newAddedItems = new ArrayList<>();
            for (Item item : items) {
                Symbol nextSymbol = nextSymbol(item);

                // '·'后面跟的是非终结符
                if (nextSymbol != null
                        && !nextSymbol.isTerminator()) {

                    newAddedItems.addAll(findEqualItems(item));
                }
            }

            items.addAll(newAddedItems);

            if (preSize == items.size()) {
                canBreak = true;
            }
        }

        // 合并具有相同产生式的Item，即合并展望符。同时保持Item的顺序，因此用LinkedHashMap
        Map<PrimaryProduction, Item> helpMap = new LinkedHashMap<>();

        for (Item item : items) {
            PrimaryProduction _PP = item.getPrimaryProduction();
            if (!helpMap.containsKey(_PP)) {
                helpMap.put(_PP, item);
            } else {
                helpMap.put(
                        _PP,
                        new Item(
                                _PP,
                                SetUtils.of(helpMap.get(_PP).getLookAHeads(), item.getLookAHeads())
                        )
                );
            }
        }

        return new Closure(closureCnt++, coreItems, new ArrayList<>(helpMap.values()));
    }

    /**
     * 找到形如 A → ·α 的产生式
     */
    abstract List<Item> findEqualItems(Item item);

    private int indexOf(List<Item> coreItems) {
        for (int i = 0; i < closures.size(); i++) {
            if (closures.get(i).isSame(coreItems)) {
                return i;
            }
        }
        return -1;
    }


    private void initAnalysisTable() {
        analysisTerminators.addAll(this.grammar.getTerminators().stream().filter(symbol -> !Symbol.EPSILON.equals(symbol)).collect(Collectors.toList()));
        analysisSymbols.addAll(analysisTerminators);
        analysisSymbols.add(Symbol.DOLLAR);
        analysisSymbols.addAll(this.grammar.getNonTerminators().stream().filter((symbol -> !Symbol.START.equals(symbol))).collect(Collectors.toList()));

        // 初始化
        for (Closure closure : closures) {
            analysisTable.put(closure.getId(), new HashMap<>());
            for (Symbol symbol : analysisSymbols) {
                analysisTable.get(closure.getId()).put(symbol, new LinkedHashSet<>());
            }
        }

        // 遍历每个产生式
        for (Production _P : getProductionMap().values()) {
            for (PrimaryProduction _PP : _P.getPrimaryProductions()) {

                // 遍历每个Closure
                for (Closure closure : closures) {

                    for (Item item : closure.getItems()) {
                        // 若某个产生式属于当前Closure
                        if (item.isOfSamePrimaryProduction(_PP)) {
                            Symbol nextSymbol = nextSymbol(item);

                            if (nextSymbol == null) {
                                initAnalysisTableWithReduction(closure, item);
                            } else if (nextSymbol.isTerminator()) {
                                initAnalysisTableWithMoveIn(closure, nextSymbol);
                            } else {
                                initAnalysisTableWithJump(closure, nextSymbol);
                            }
                        }
                    }
                }
            }
        }

        // 检查合法性，即检查表项动作是否唯一
        for (Closure closure : closures) {
            for (Symbol symbol : analysisSymbols) {
                if (analysisTable.get(closure.getId()).get(symbol).size() > 1) {
                    System.err.println(this.getClass().getSimpleName() + ": Conflict");
                }
            }
        }
    }

    abstract void initAnalysisTableWithReduction(Closure closure, Item item);

    void initAnalysisTableWithMoveIn(Closure closure, Symbol nextSymbol) {
        analysisTable.get(closure.getId())
                .get(nextSymbol)
                .add(new Operation(
                        closureTransferTable.get(closure.getId()).get(nextSymbol),
                        null,
                        Operation.OperationCode.MOVE_IN));
    }

    void initAnalysisTableWithJump(Closure closure, Symbol nextSymbol) {
        analysisTable.get(closure.getId())
                .get(nextSymbol)
                .add(new Operation(
                        closureTransferTable.get(closure.getId()).get(nextSymbol),
                        null,
                        Operation.OperationCode.JUMP));
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
            assertNotNull(nextOperation);
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
