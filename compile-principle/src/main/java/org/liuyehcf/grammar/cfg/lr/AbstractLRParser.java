package org.liuyehcf.grammar.cfg.lr;

import org.liuyehcf.grammar.LexicalAnalyzer;
import org.liuyehcf.grammar.cfg.AbstractCfgParser;
import org.liuyehcf.grammar.core.ParserException;
import org.liuyehcf.grammar.core.definition.Grammar;
import org.liuyehcf.grammar.core.definition.PrimaryProduction;
import org.liuyehcf.grammar.core.definition.Symbol;
import org.liuyehcf.grammar.core.definition.SymbolString;
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
    // 是否合并同心闭包（只有LALR才是true）
    private final boolean needMerge;
    // 项目集闭包 closureId -> Closure
    private Map<Integer, Closure> closures;
    // 状态转移表 [ClosureId, Symbol] -> ClosureId
    private Map<Integer, Map<Symbol, Integer>> closureTransferTable;

    // 预测分析表正常情况下，表项只有一个，若出现多个，则说明有冲突
    // [ClosureId, Symbol] -> Operation
    private Map<Integer, Map<Symbol, LinkedHashSet<Operation>>> analysisTable;

    // 预测分析表中的所有输入符号，与Grammar中的符号有些不一样
    private List<Symbol> analysisTerminators;
    private List<Symbol> analysisSymbols;
    private int closureCnt;

    AbstractLRParser(LexicalAnalyzer lexicalAnalyzer, Grammar originalGrammar, boolean needMerge) {
        super(lexicalAnalyzer, originalGrammar, GrammarConverterPipelineImpl
                .builder()
                .registerGrammarConverter(AugmentedGrammarConverter.class)
                .registerGrammarConverter(StatusExpandGrammarConverter.class)
                .registerGrammarConverter(MergeGrammarConverter.class)
                .build());

        this.closures = new LinkedHashMap<>();
        this.needMerge = needMerge;
        this.closureTransferTable = new HashMap<>();
        this.analysisTable = new HashMap<>();
        this.analysisTerminators = new ArrayList<>();
        this.analysisSymbols = new ArrayList<>();
        this.closureCnt = 0;
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

    List<Symbol> getAnalysisTerminators() {
        return analysisTerminators;
    }

    @Override
    protected final boolean doMatches(String input) {
        return new Matcher().matches(input);
    }

    @Override
    public final String getClosureJSONString() {
        StringBuilder sb = new StringBuilder();

        sb.append('{');

        sb.append('\"')
                .append("closures:")
                .append('\"')
                .append(':');

        sb.append(closures.values());

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
                    .append(symbol)
                    .append(' ');
        }

        for (Symbol symbol : this.grammar.getNonTerminators()) {
            if (Symbol.START.equals(symbol)) {
                continue;
            }
            sb.append(separator)
                    .append(' ')
                    .append(symbol)
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
        for (Closure closure : closures.values()) {
            sb.append(separator)
                    .append(' ')
                    .append(closure.getId())
                    .append(' ');

            for (Symbol symbol : analysisSymbols) {
                LinkedHashSet<Operation> operations = analysisTable.get(closure.getId()).get(symbol);
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
    public final String getClosureTransferTableJSONString() {
        StringBuilder sb = new StringBuilder();
        int cnt = 1;

        sb.append('{');

        for (Closure closure : closures.values()) {

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
                            .append(symbol)
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

        // 合并同心闭包
        mergeConcentricClosure();

        // 初始化分析表
        initAnalysisTable();
    }

    private void initClosure() {
        // 初始化，添加闭包0
        Closure firstClosure = closure(ListUtils.of(createFirstItem()));
        closures.put(firstClosure.getId(), firstClosure);

        boolean canBreak = false;

        while (!canBreak) {
            canBreak = true;

            Map<Integer, Closure> newClosures = new LinkedHashMap<>(closures);

            for (Closure preClosure : closures.values()) {

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

                    if ((existsClosureId = closureIdOf(coreItemsOfNextClosure)) == -1) {
                        nextClosure = closure(coreItemsOfNextClosure);
                        newClosures.put(nextClosure.getId(), nextClosure);
                    } else {
                        nextClosure = newClosures.get(existsClosureId);
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

            closures = newClosures;
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
        // 形如 "[B → · γ, b]"与"[B → · γ, c]" 合并成 "[B → · γ, b/c]"
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

        return new Closure(
                closureCnt++,
                coreItems,
                new ArrayList<>(
                        helpMap.values().stream().filter(item -> !coreItems.contains(item)).collect(Collectors.toList())
                )
        );
    }

    /**
     * 找到形如 A → ·α 的产生式
     */
    abstract List<Item> findEqualItems(Item item);

    private int closureIdOf(List<Item> coreItems) {
        for (Closure closure : closures.values()) {
            if (closure.isSame(coreItems)) {
                return closure.getId();
            }
        }
        return -1;
    }

    private void mergeConcentricClosure() {
        if (!needMerge) {
            return;
        }

        // removedClosure --> savedClosure
        Map<Closure, Closure> mergePairs = new HashMap<>();

        List<Integer> closureIds = ListUtils.sort(new ArrayList<>(closures.keySet()));

        for (int i = 0; i < closureIds.size(); i++) {
            int closureIdI = closureIds.get(i);
            for (int j = i + 1; j < closures.size(); j++) {
                int closureIdJ = closureIds.get(j);

                Closure savedClosure = closures.get(closureIdI);
                Closure removedClosure = closures.get(closureIdJ);

                if (Closure.isConcentric(savedClosure, removedClosure)) {
                    // 现在假设不可能有3个以上的冲突项
                    assertFalse(mergePairs.containsKey(removedClosure));
                    mergePairs.put(removedClosure, savedClosure);
                }
            }
        }

        Map<Integer, Map<Symbol, Integer>> newClosureTransferTable = new HashMap<>();

        for (Map.Entry<Integer, Map<Symbol, Integer>> outerEntry : closureTransferTable.entrySet()) {
            int fromClosureId = outerEntry.getKey();
            Closure fromClosure = closures.get(fromClosureId);

            // 若这个Closure需要被移除，那么每一条连线都得改变
            if (mergePairs.containsKey(fromClosure)) {
                newClosureTransferTable.putIfAbsent(mergePairs.get(fromClosure).getId(), new HashMap<>());

                for (Map.Entry<Symbol, Integer> innerEntry : outerEntry.getValue().entrySet()) {
                    Symbol nextSymbol = innerEntry.getKey();
                    int toClosureId = innerEntry.getValue();
                    Closure toClosure = closures.get(toClosureId);

                    // toClosure 仍然是需要被移除的Closure
                    if (mergePairs.containsKey(toClosure)) {
                        assertTrue(!newClosureTransferTable.get(mergePairs.get(fromClosure).getId()).containsKey(nextSymbol)
                                || newClosureTransferTable.get(mergePairs.get(fromClosure).getId()).get(nextSymbol).equals(mergePairs.get(toClosure).getId()));

                        newClosureTransferTable.get(mergePairs.get(fromClosure).getId())
                                .put(nextSymbol, mergePairs.get(toClosure).getId());
                    } else {
                        assertTrue(!newClosureTransferTable.get(mergePairs.get(fromClosure).getId()).containsKey(nextSymbol)
                                || newClosureTransferTable.get(mergePairs.get(fromClosure).getId()).get(nextSymbol).equals(toClosureId));

                        newClosureTransferTable.get(mergePairs.get(fromClosure).getId())
                                .put(nextSymbol, toClosureId);
                    }
                }
            } else {
                newClosureTransferTable.putIfAbsent(fromClosureId, new HashMap<>());

                for (Map.Entry<Symbol, Integer> innerEntry : outerEntry.getValue().entrySet()) {
                    Symbol nextSymbol = innerEntry.getKey();
                    int toClosureId = innerEntry.getValue();
                    Closure toClosure = closures.get(toClosureId);

                    // toClosure 是需要被移除的Closure
                    if (mergePairs.containsKey(toClosure)) {
                        assertTrue(!newClosureTransferTable.get(fromClosureId).containsKey(nextSymbol)
                                || newClosureTransferTable.get(mergePairs.get(fromClosure).getId()).get(nextSymbol).equals(mergePairs.get(toClosure).getId()));


                        newClosureTransferTable.get(fromClosureId)
                                .put(nextSymbol, mergePairs.get(toClosure).getId());
                    } else {
                        assertTrue(!newClosureTransferTable.get(fromClosureId).containsKey(nextSymbol)
                                || newClosureTransferTable.get(mergePairs.get(fromClosure).getId()).get(nextSymbol).equals(toClosureId));


                        newClosureTransferTable.get(fromClosureId)
                                .put(nextSymbol, toClosureId);
                    }
                }
            }
        }

        // 合并同心闭包的展望符集合
        for (Map.Entry<Closure, Closure> entry : mergePairs.entrySet()) {
            Closure savedClosure = entry.getValue();
            Closure removedClosure = entry.getKey();

            List<Item> coreItems = new ArrayList<>();
            List<Item> equalItems = new ArrayList<>();

            assertTrue(savedClosure.getCoreItems().size() == removedClosure.getCoreItems().size());
            assertTrue(savedClosure.getEqualItems().size() == removedClosure.getEqualItems().size());
            assertTrue(savedClosure.getItems().size() == removedClosure.getItems().size());

            for (int i = 0; i < savedClosure.getCoreItems().size(); i++) {
                assertTrue(savedClosure.getCoreItems().get(i).getPrimaryProduction().equals(
                        removedClosure.getCoreItems().get(i).getPrimaryProduction()
                ));
                coreItems.add(
                        new Item(
                                savedClosure.getCoreItems().get(i).getPrimaryProduction(),
                                SetUtils.of(
                                        savedClosure.getCoreItems().get(i).getLookAHeads(),
                                        removedClosure.getCoreItems().get(i).getLookAHeads()
                                )
                        )
                );
            }

            for (int i = 0; i < savedClosure.getEqualItems().size(); i++) {
                assertTrue(savedClosure.getEqualItems().get(i).getPrimaryProduction().equals(
                        removedClosure.getEqualItems().get(i).getPrimaryProduction()
                ));
                equalItems.add(
                        new Item(
                                savedClosure.getEqualItems().get(i).getPrimaryProduction(),
                                SetUtils.of(
                                        savedClosure.getEqualItems().get(i).getLookAHeads(),
                                        removedClosure.getEqualItems().get(i).getLookAHeads()
                                )
                        )
                );
            }

            closures.put(savedClosure.getId(), new Closure(
                    savedClosure.getId(),
                    coreItems,
                    equalItems
            ));
        }

        this.closureTransferTable = newClosureTransferTable;
        for (int id : mergePairs.keySet().stream().map(Closure::getId).collect(Collectors.toList())) {
            closures.remove(id);
        }
    }

    private void initAnalysisTable() {
        analysisTerminators.addAll(
                ListUtils.sort(
                        ListUtils.of(
                                // 除Symbol.EPSILON之外的所有终结符
                                this.grammar.getTerminators().stream().filter(symbol -> !Symbol.EPSILON.equals(symbol)).collect(Collectors.toList()),
                                Symbol.DOLLAR
                        )
                )
        );
        analysisSymbols.addAll(
                ListUtils.sort(
                        ListUtils.of(
                                analysisTerminators,
                                // 除Symbol.START之外的所有非终结符
                                (List<Symbol>) this.grammar.getNonTerminators().stream().filter((symbol -> !Symbol.START.equals(symbol))).collect(Collectors.toList())
                        )
                )
        );

        // 初始化
        for (Closure closure : closures.values()) {
            analysisTable.put(closure.getId(), new HashMap<>());
            for (Symbol symbol : analysisSymbols) {
                analysisTable.get(closure.getId()).put(symbol, new LinkedHashSet<>());
            }
        }

        // 遍历每个Closure
        for (Closure closure : closures.values()) {
            // 遍历Closure中的每个项目
            for (Item item : closure.getItems()) {
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

    @Override
    protected void checkIsLegal() {
        // 检查合法性，即检查表项动作是否唯一
        for (Closure closure : closures.values()) {
            for (Symbol symbol : analysisSymbols) {
                if (analysisTable.get(closure.getId()).get(symbol).size() > 1) {
                    setLegal(false);
                    return;
                }
            }
        }
        setLegal(true);
    }

    abstract void initAnalysisTableWithReduction(Closure closure, Item item);

    private void initAnalysisTableWithMoveIn(Closure closure, Symbol nextSymbol) {
        assertNotNull(closureTransferTable.get(closure.getId()));
        analysisTable.get(closure.getId())
                .get(nextSymbol)
                .add(new Operation(
                        closureTransferTable.get(closure.getId()).get(nextSymbol),
                        null,
                        Operation.OperationCode.MOVE_IN));
    }

    private void initAnalysisTableWithJump(Closure closure, Symbol nextSymbol) {
        analysisTable.get(closure.getId())
                .get(nextSymbol)
                .add(new Operation(
                        closureTransferTable.get(closure.getId()).get(nextSymbol),
                        null,
                        Operation.OperationCode.JUMP));
    }

    private Operation getOperationFromAnalysisTable(int closureId, Symbol symbol) {
        if (analysisTable.get(closureId).get(symbol).isEmpty()) return null;
        assertTrue(analysisTable.get(closureId).get(symbol).size() == 1);
        return analysisTable.get(closureId).get(symbol).iterator().next();
    }

    void addOperationToAnalysisTable(int closureId, Symbol symbol, Operation operation) {
        analysisTable.get(closureId).get(symbol).add(operation);
    }

    private class Matcher {
        private LinkedList<Integer> statusStack;
        private LinkedList<Symbol> symbolStack;
        private Queue<Symbol> remainSymbols;
        private boolean canReceive;
        private Operation operation;

        public boolean matches(String input) {
            LexicalAnalyzer.TokenIterator tokenIterator;
            try {
                tokenIterator = lexicalAnalyzer.iterator(input);
            } catch (ParserException e) {
                // 词法分析阶段出现了错误
                return false;
            }

            statusStack = new LinkedList<>();
            symbolStack = new LinkedList<>();
            remainSymbols = new LinkedList<>();

            statusStack.push(0);
            symbolStack.push(Symbol.DOLLAR);

            while (tokenIterator.hasNext()) {
                remainSymbols.offer(tokenIterator.next().getId());
            }

            boolean canBreak = false;
            while (!canBreak) {
                operation = getOperationFromAnalysisTable(statusStack.peek(), remainSymbols.peek());

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

            // 如果是形如 "A → ε"这样的产生式，那么特殊处理一下（不进行出栈操作）
            if (!_PPReduction.getRight().equals(SymbolString.EPSILON_RAW)) {
                for (int i = 0; i < _PPReduction.getRight().getSymbols().size(); i++) {
                    statusStack.pop();
                    symbolStack.pop();
                }
            }

            symbolStack.push(_PPReduction.getLeft());
        }

        private void jump() {
            assertTrue(statusStack.size() + 1 == symbolStack.size());
            assertTrue(!statusStack.isEmpty() && !symbolStack.isEmpty());

            Operation nextOperation = getOperationFromAnalysisTable(statusStack.peek(), symbolStack.peek());
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

    }
}
