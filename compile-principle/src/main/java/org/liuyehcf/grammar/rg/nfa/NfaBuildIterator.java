package org.liuyehcf.grammar.rg.nfa;


import org.liuyehcf.grammar.core.definition.Symbol;
import org.liuyehcf.grammar.rg.utils.EscapedUtil;
import org.liuyehcf.grammar.rg.utils.SymbolUtils;

import java.util.*;

import static org.liuyehcf.grammar.utils.AssertUtils.*;

/**
 * Created by Liuye on 2017/10/23.
 */
class NfaBuildIterator {

    // 所有正则表达式语法中的符号
    private List<Symbol> symbols;

    // 当前解析的位置
    private int index;

    // 辅助栈
    private LinkedList<StackUnion> unions;

    // 当前NfaClosure
    private NfaClosure curNfaClosure;

    // 最终NfaClosure
    private NfaClosure nfaClosure;

    // group辅助工具
    private GroupUtil groupUtil;

    private NfaBuildIterator(List<Symbol> symbols) {
        this.symbols = symbols;
        index = 0;
        unions = new LinkedList<>();
        curNfaClosure = null;
        groupUtil = new GroupUtil();
    }

    static NfaClosure createNfaClosure(List<Symbol> symbols) {
        NfaBuildIterator buildIterator = new NfaBuildIterator(symbols);

        while (buildIterator.hasNext()) {
            buildIterator.processEachSymbol();
        }

        buildIterator.finishWork();

        return buildIterator.nfaClosure;
    }

    private StackUnion createStackUnitWithNfaClosure(NfaClosure nfaClosure) {
        return this.new StackUnion(nfaClosure);
    }

    private StackUnion createStackUnitWithParallelGroup() {
        return this.new StackUnion(null);
    }

    private void moveForward() {
        index += 1;
    }

    private Symbol getCurSymbol() {
        return symbols.get(index);
    }

    private Symbol getNextSymbol() {
        if (index + 1 < symbols.size()) {
            return symbols.get(index + 1);
        }
        return null;
    }

    private boolean hasNext() {
        return index < symbols.size();
    }

    private int getCurGroup() {
        return groupUtil.getCurGroup();
    }

    private void enterGroup() {
        groupUtil.enterGroup();
    }

    private void exitGroup() {
        groupUtil.exitGroup();
    }

    private void pushNfaClosure(NfaClosure nfaClosure) {
        unions.push(createStackUnitWithNfaClosure(nfaClosure));
    }

    private void pushCurNfaClosure() {
        if (curNfaClosure != null) {
            pushNfaClosure(curNfaClosure);
            curNfaClosure = null;
        }
    }

    private StackUnion popStackUnion() {
        if (unions.isEmpty()) return null;
        return unions.pop();
    }

    private void pushParallel() {
        unions.push(createStackUnitWithParallelGroup());
    }

    private void finishWork() {
        combineNfaClosuresOfCurGroup();

        assertTrue(unions.isEmpty());

        if (curNfaClosure == null) {
            //todo assertTrue(groupNfaClosures.isEmpty());
            curNfaClosure = NfaClosure.getEmptyClosureForGroup(0);
        }

        setStartAndReceiveOfCurNfaClosure();

        nfaClosure = curNfaClosure;
    }

    private void processEachSymbol() {
        assertTrue(getCurSymbol().isTerminator());
        Symbol curSymbol = getCurSymbol();

        if (curSymbol.equals(SymbolUtils._any)) {
            processWhenEncounteredAny();
        } else if (curSymbol.equals(SymbolUtils._or)) {
            processWhenEncounteredOr();
        } else if (curSymbol.equals(SymbolUtils._star)) {
            processWhenEncounteredStar();
        } else if (curSymbol.equals(SymbolUtils._add)) {
            processWhenEncounteredAdd();
        } else if (curSymbol.equals(SymbolUtils._escaped)) {
            processWhenEncounteredEscaped();
        } else if (curSymbol.equals(SymbolUtils._leftMiddleParenthesis)) {
            processWhenEncounteredLeftMiddleParenthesis();
        } else if (curSymbol.equals(SymbolUtils._leftSmallParenthesis)) {
            processWhenEncounteredLeftSmallParenthesis();
        } else if (curSymbol.equals(SymbolUtils._rightSmallParenthesis)) {
            processWhenEncounteredRightSmallParenthesis();
        } else {
            processWhenEncounteredNormalSymbol();
        }
    }

    private void processWhenEncounteredAny() {
        pushCurNfaClosure();

        buildNfaClosureForAnyAsCurNfaClosure();

        moveForward();
    }

    private void buildNfaClosureForAnyAsCurNfaClosure() {
        curNfaClosure = buildNfaClosureWithSymbols(SymbolUtils.getAlphabetSymbols());
    }

    private void processWhenEncounteredOr() {
        combineNfaClosuresOfCurGroup();

        pushCurNfaClosure();

        pushParallel();

        moveForward();
    }

    private void processWhenEncounteredStar() {
        buildNormalCircleForCurNfaClosure();

        buildEpsilonCircleOfAllEndNfaStateForCurNfaClosure();

        removeAllConnectionsOfStartNfaStateOfCurNfaClosure();

        buildEpsilonConnectionFromStartNfaStateToEachEndNfaStateForCurNfaClosure();

        pushCurNfaClosure();

        moveForward();
    }

    private void buildNormalCircleForCurNfaClosure() {
        NfaState startNfaState = curNfaClosure.getStartNfaState();
        List<NfaState> endNfaStates = curNfaClosure.getEndNfaStates();
        for (NfaState endNfaState : endNfaStates) {
            for (Symbol inputSymbol : startNfaState.getAllInputSymbol()) {
                for (NfaState nextNfaStateOfStartNfaState : startNfaState.getNextNfaStatesWithInputSymbol(inputSymbol)) {
                    endNfaState.addInputSymbolAndNextNfaState(inputSymbol, nextNfaStateOfStartNfaState);
                }
            }
        }
    }

    private void buildEpsilonCircleOfAllEndNfaStateForCurNfaClosure() {
        List<NfaState> endNfaStates = curNfaClosure.getEndNfaStates();
        for (int i = 0; i < endNfaStates.size(); i++) {
            endNfaStates.get(i).addInputSymbolAndNextNfaState(
                    Symbol.EPSILON,
                    endNfaStates.get((i + 1) % endNfaStates.size()));
        }
    }

    private void removeAllConnectionsOfStartNfaStateOfCurNfaClosure() {
        NfaState startNfaState = curNfaClosure.getStartNfaState();
        startNfaState.cleanConnections();
    }

    private void buildEpsilonConnectionFromStartNfaStateToEachEndNfaStateForCurNfaClosure() {
        for (NfaState endNfaState : curNfaClosure.getEndNfaStates()) {
            curNfaClosure.getStartNfaState().
                    addInputSymbolAndNextNfaState(Symbol.EPSILON, endNfaState);
        }
    }

    private void processWhenEncounteredAdd() {
        buildEpsilonConnectionFromEachEndNfaStateToStartNfaState();

        pushCurNfaClosure();

        moveForward();
    }

    private void buildEpsilonConnectionFromEachEndNfaStateToStartNfaState() {
        for (NfaState endNfaState : curNfaClosure.getEndNfaStates()) {
            endNfaState.addInputSymbolAndNextNfaState(Symbol.EPSILON, curNfaClosure.getStartNfaState());
        }
    }

    private void processWhenEncounteredEscaped() {
        pushCurNfaClosure();

        buildNfaClosureForEscapedAsCurNfaClosure();

        moveForward();
    }

    private void buildNfaClosureForEscapedAsCurNfaClosure() {
        moveForward();

        curNfaClosure = buildNfaClosureWithSymbols(
                EscapedUtil.getSymbolsOfEscapedChar(
                        SymbolUtils.getChar(getCurSymbol())));
    }

    private void processWhenEncounteredLeftMiddleParenthesis() {
        pushCurNfaClosure();

        buildNfaClosureForMiddleParenthesisAsCurNfaClosure();
    }

    private void buildNfaClosureForMiddleParenthesisAsCurNfaClosure() {
        curNfaClosure = buildNfaClosureWithSymbols(getOptionalSymbols());
    }

    private Set<Symbol> getOptionalSymbols() {
        moveForward();
        boolean isNot = getCurSymbol().equals(SymbolUtils._middleParenthesisNot);

        Set<Symbol> optionalSymbols = new HashSet<>();
        if (isNot) moveForward();

        do {
            if (getCurSymbol().equals(SymbolUtils._escaped)) {
                moveForward();
                optionalSymbols.addAll(
                        EscapedUtil.getSymbolsOfEscapedCharInMiddleParenthesis(
                                SymbolUtils.getChar(getCurSymbol())));
            } else {
                optionalSymbols.add(getCurSymbol());
            }
            moveForward();
        } while (!getCurSymbol().equals(SymbolUtils._rightMiddleParenthesis));

        moveForward();

        if (isNot) {
            return SymbolUtils.getOppositeSymbols(optionalSymbols);
        } else {
            return optionalSymbols;
        }
    }

    private void processWhenEncounteredLeftSmallParenthesis() {
        enterGroup();

        // 如果出现了()这种情况，那么才特殊处理一下。不加这个条件将会出现多余的ε边
        if (SymbolUtils._rightSmallParenthesis.equals(getNextSymbol())) {
            pushCurNfaClosure();
            buildNonOrdinaryNfaClosure();
        }

        moveForward();
    }

    private void processWhenEncounteredRightSmallParenthesis() {
        combineNfaClosuresOfCurGroup();

        setStartAndReceiveOfCurNfaClosure();

        exitGroup();

        changeGroupOfCurNfaClosure();

        moveForward();
    }

    private void combineNfaClosuresOfCurGroup() {
        pushCurNfaClosure();

        StackUnion topStackUnion;
        StackUnion secondTopStackUnion;
        StackUnion thirdTopStackUnion;

        while ((topStackUnion = popStackUnion()) != null
                && (secondTopStackUnion = popStackUnion()) != null) {
            assertTrue(topStackUnion.isNfaClosure());

            if (secondTopStackUnion.isNfaClosure()) {
                if (secondTopStackUnion.getNfaClosure().getGroup()
                        != topStackUnion.getNfaClosure().getGroup()) {
                    // case "a(b)"
                    unions.push(secondTopStackUnion);
                    break;
                }
                combineTwoClosure(
                        secondTopStackUnion.getNfaClosure(),
                        topStackUnion.getNfaClosure());
                unions.push(secondTopStackUnion);
            } else {
                assertFalse(unions.isEmpty());
                thirdTopStackUnion = unions.pop();
                assertTrue(thirdTopStackUnion.isNfaClosure());
                if (thirdTopStackUnion.getNfaClosure().getGroup()
                        != topStackUnion.getNfaClosure().getGroup()) {
                    // case "((a)|(b))"，将parallel操作滞后到group变更后
                    unions.push(thirdTopStackUnion);
                    unions.push(secondTopStackUnion);
                    break;
                }
                parallel(
                        thirdTopStackUnion.getNfaClosure(),
                        topStackUnion.getNfaClosure());
                unions.push(thirdTopStackUnion);
            }
        }

        if (topStackUnion == null) {
            // when "()"
            topStackUnion = createStackUnitWithNfaClosure(
                    NfaClosure.getEmptyClosureForGroup(getCurGroup()));
        }

        curNfaClosure = topStackUnion.getNfaClosure();
    }

    private void setStartAndReceiveOfCurNfaClosure() {
        assertNotNull(curNfaClosure);
        curNfaClosure.setStartAndReceive(getCurGroup());
    }

    private void changeGroupOfCurNfaClosure() {
        assertNotNull(curNfaClosure);
        curNfaClosure.setGroup(getCurGroup());
    }

    private void processWhenEncounteredNormalSymbol() {
        pushCurNfaClosure();

        buildNfaClosureForNormalSymbol();

        moveForward();
    }

    private void buildNfaClosureForNormalSymbol() {
        curNfaClosure = buildNfaClosureWithSymbols(Arrays.asList(getCurSymbol()));
    }

    private void buildNonOrdinaryNfaClosure() {
        NfaState startNfaState = new NfaState();
        List<NfaState> endNfaStates = new ArrayList<>();

        // todo 这里创建了一个多余的EPSILON边
        startNfaState.addInputSymbolAndNextNfaState(Symbol.EPSILON, startNfaState);
        endNfaStates.add(startNfaState);

        curNfaClosure = new NfaClosure(startNfaState, endNfaStates, getCurGroup());
    }

    private NfaClosure buildNfaClosureWithSymbols(Collection<Symbol> symbols) {
        assertFalse(symbols.isEmpty());

        NfaState startNfaState = new NfaState();
        List<NfaState> endNfaStates = new ArrayList<>();

        for (Symbol symbol : symbols) {
            NfaState curNfaState = new NfaState();
            startNfaState.addInputSymbolAndNextNfaState(symbol, curNfaState);
            endNfaStates.add(curNfaState);
        }

        return new NfaClosure(startNfaState, endNfaStates, getCurGroup());
    }

    private void combineTwoClosure(NfaClosure preNfaClosure, NfaClosure nextNfaClosure) {
        assertTrue(preNfaClosure.getGroup() == nextNfaClosure.getGroup());

        // 符号说明:
        //      S: 开始节点
        //      E(i): 第i个终止节点
        //      S.N(i): 开始节点的第i个后继节点
        //      --*>: 经过多步跳转
        //      -->: 经过一步跳转
        //
        // 连接前:
        //      pre.S --*> Pre.E(1)              next.S --> next.N(1)
        //      pre.S --*> Pre.E(2)              next.S --> next.N(2)
        //            ...                               ...
        //      Pre.S --*> Pre.E(n)              next.S --> next.N(m)
        //
        //
        // 连接后: 将前一个NfaClosure的每一个终止节点，连接到下一个NfaClosure的起始节点的所有后继节点
        //      pre.S --*> Pre.E(1) --> next.N(1)   pre.S --*> Pre.E(2) --> next.N(1)            pre.S --*> Pre.E(n) --> next.N(1)
        //      pre.S --*> Pre.E(1) --> next.N(2)   pre.S --*> Pre.E(2) --> next.N(2)     ...    pre.S --*> Pre.E(n) --> next.N(2)
        //                   ...                                 ...                                          ...
        //      pre.S --*> Pre.E(1) --> next.N(m)   pre.S --*> Pre.E(2) --> next.N(m)            pre.S --*> Pre.E(n) --> next.N(m)
        //
        // next.S节点，在连接后被移除了

        for (NfaState endNfaStateOfPreNfaClosure : preNfaClosure.getEndNfaStates()) {
            NfaState startNfaStateOfNextNfaClosure = nextNfaClosure.getStartNfaState();

            // 以下两行循环需要保留被移除的startNfaStateOfNextNfaClosure节点的状态信息，保留在每个endNfaStateOfPreNfaClosure中
            for (int group : startNfaStateOfNextNfaClosure.getGroupStart()) {
                endNfaStateOfPreNfaClosure.setStart(group);
            }

            for (int group : startNfaStateOfNextNfaClosure.getGroupReceive()) {
                endNfaStateOfPreNfaClosure.setReceive(group);
            }

            // 以下循环用于连接两个NfaClosure
            for (Symbol inputSymbol : startNfaStateOfNextNfaClosure.getAllInputSymbol()) {
                for (NfaState nextNfaState : startNfaStateOfNextNfaClosure.getNextNfaStatesWithInputSymbol(inputSymbol)) {
                    endNfaStateOfPreNfaClosure.addInputSymbolAndNextNfaState(inputSymbol, nextNfaState);
                }
            }
        }

        preNfaClosure.setEndNfaStates(nextNfaClosure.getEndNfaStates());
    }

    private void parallel(NfaClosure preNfaClosure, NfaClosure nextNfaClosure) {

        // 符号说明:
        //      S: 开始节点
        //      S.N(i): 开始节点的第i个后继节点
        //      -->: 经过一步跳转
        //
        // 连接前:
        //      pre.S --> Pre.N(1)              next.S --> next.N(1)
        //      pre.S --> Pre.N(2)              next.S --> next.N(2)
        //            ...                              ...
        //      Pre.S --> Pre.N(n)              next.S --> next.N(m)
        //
        //
        // 连接后: 将前一个NfaClosure的起始节点，连接到后一个NfaClosure的起始节点的所有后继节点
        //      pre.S --> Pre.N(1)
        //      pre.S --> Pre.N(2)
        //            ...
        //      Pre.S --> Pre.N(n)
        //
        //      pre.S --> next.N(1)
        //      pre.S --> next.N(2)
        //            ...
        //      pre.S --> next.N(m)
        //
        // next.S节点，在连接后被移除了

        NfaState startNfaStateOfPreNfaClosure = preNfaClosure.getStartNfaState();
        NfaState startNfaStateOfNextNfaClosure = nextNfaClosure.getStartNfaState();

        for (Symbol inputSymbol : startNfaStateOfNextNfaClosure.getAllInputSymbol()) {
            for (NfaState nextNfaState : startNfaStateOfNextNfaClosure.getNextNfaStatesWithInputSymbol(inputSymbol)) {
                startNfaStateOfPreNfaClosure.addInputSymbolAndNextNfaState(inputSymbol, nextNfaState);
            }
        }

        preNfaClosure.getEndNfaStates().addAll(nextNfaClosure.getEndNfaStates());
    }

    private static class GroupUtil {
        private int groupCount = 0;
        private LinkedList<Integer> groupStack;

        private GroupUtil() {
            groupStack = new LinkedList<>();
            groupStack.push(0);
        }

        private int getCurGroup() {
            return groupStack.peek();
        }

        private void enterGroup() {
            groupStack.push(++groupCount);
        }

        private void exitGroup() {
            groupStack.pop();
        }
    }

    /**
     * 栈元素，元素为NfaClosure，或者一个占位符
     * 当nfaClosure不为空时，就持有了一个NfaClosure
     * 当nfaClosure为空时，即遇到'|'符号，压入了一个占位符
     */
    private class StackUnion {
        private NfaClosure nfaClosure;

        private StackUnion(NfaClosure nfaClosure) {
            this.nfaClosure = nfaClosure;
        }

        private boolean isNfaClosure() {
            return nfaClosure != null;
        }

        private boolean isParallel() {
            return !isNfaClosure();
        }

        private NfaClosure getNfaClosure() {
            assertTrue(isNfaClosure());
            return nfaClosure;
        }

        @Override
        public String toString() {
            return isNfaClosure() ?
                    getNfaClosure().toString() + " group[" + getNfaClosure().getGroup() + "]"
                    : "Parallel";
        }
    }
}
