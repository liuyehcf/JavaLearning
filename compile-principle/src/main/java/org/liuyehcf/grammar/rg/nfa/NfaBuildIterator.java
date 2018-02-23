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

    private void parallel(NfaClosure preNfaClosure, NfaClosure nextNfaClosure) {

        NfaState startNfaStateOfPreNfaClosure = preNfaClosure.getStartNfaState();
        NfaState startNfaStateOfNextNfaClosure = nextNfaClosure.getStartNfaState();

        for (Symbol inputSymbol : startNfaStateOfNextNfaClosure.getAllInputSymbol()) {
            for (NfaState nextNfaState : startNfaStateOfNextNfaClosure.getNextNfaStatesWithInputSymbol(inputSymbol)) {
                startNfaStateOfPreNfaClosure.addInputSymbolAndNextNfaState(inputSymbol, nextNfaState);
            }
        }

        preNfaClosure.getEndNfaStates().addAll(nextNfaClosure.getEndNfaStates());
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
        nfaClosure = curNfaClosure;

        for (NfaState endNfaState : curNfaClosure.getEndNfaStates()) {
            assertTrue(getCurGroup() == 0);
            endNfaState.setReceive(getCurGroup());
        }
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
                    // "[ab](([ab])|([01]))*"
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

    private NfaClosure buildNfaClosureWithSymbols(Collection<Symbol> symbols) {
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

        for (NfaState endNfaStateOfPreNfaClosure : preNfaClosure.getEndNfaStates()) {
            NfaState startNfaStateOfNextNfaClosure = nextNfaClosure.getStartNfaState();
            for (Symbol inputSymbol : startNfaStateOfNextNfaClosure.getAllInputSymbol()) {
                for (NfaState nextNfaState : startNfaStateOfNextNfaClosure.getNextNfaStatesWithInputSymbol(inputSymbol)) {
                    endNfaStateOfPreNfaClosure.addInputSymbolAndNextNfaState(inputSymbol, nextNfaState);
                }
            }

        }

        preNfaClosure.setEndNfaStates(nextNfaClosure.getEndNfaStates());
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
