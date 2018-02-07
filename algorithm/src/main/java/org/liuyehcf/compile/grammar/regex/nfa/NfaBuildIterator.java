package org.liuyehcf.compile.grammar.regex.nfa;

import org.liuyehcf.compile.grammar.regex.symbol.EscapedUtil;
import org.liuyehcf.compile.grammar.regex.symbol.Symbol;

import java.util.*;

/**
 * Created by Liuye on 2017/10/23.
 */
class NfaBuildIterator {

    private List<Symbol> symbols;
    private int index;
    private LinkedList<StackUnion> unions;
    private NfaClosure curNfaClosure;
    private List<NfaClosure> groupNfaClosures;
    private GroupUtil groupUtil;

    private NfaBuildIterator(List<Symbol> symbols) {
        this.symbols = symbols;
        index = 0;
        unions = new LinkedList<>();
        curNfaClosure = null;
        groupNfaClosures = new ArrayList<>();
        groupUtil = new GroupUtil();
    }

    static List<NfaClosure> createNfaClosuresMap(List<Symbol> symbols) {
        NfaBuildIterator buildIterator = new NfaBuildIterator(symbols);

        while (buildIterator.hasNext()) {
            buildIterator.processEachSymbol();
        }

        buildIterator.finishWork();

        return buildIterator.groupNfaClosures;
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

        assert unions.isEmpty();

        if (curNfaClosure == null) {
            assert groupNfaClosures.isEmpty();
            curNfaClosure = NfaClosure.getEmptyClosureForGroup(0);
        }
        groupNfaClosures.add(curNfaClosure);

        for (NfaState endNfaState : curNfaClosure.getEndNfaStates()) {
            endNfaState.setCanReceive();
        }

        Collections.sort(groupNfaClosures, new Comparator<NfaClosure>() {
            @Override
            public int compare(NfaClosure o1, NfaClosure o2) {
                return o1.getGroup() - o2.getGroup();
            }
        });
    }

    private void processEachSymbol() {
        assert getCurSymbol().isOfAlphabet();
        Symbol curSymbol = getCurSymbol();

        if (curSymbol.equals(Symbol._any)) {
            processWhenEncounteredAny();
        } else if (curSymbol.equals(Symbol._or)) {
            processWhenEncounteredOr();
        } else if (curSymbol.equals(Symbol._star)) {
            processWhenEncounteredStar();
        } else if (curSymbol.equals(Symbol._add)) {
            processWhenEncounteredAdd();
        } else if (curSymbol.equals(Symbol._escaped)) {
            processWhenEncounteredEscaped();
        } else if (curSymbol.equals(Symbol._leftMiddleParenthesis)) {
            processWhenEncounteredLeftMiddleParenthesis();
        } else if (curSymbol.equals(Symbol._leftSmallParenthesis)) {
            processWhenEncounteredLeftSmallParenthesis();
        } else if (curSymbol.equals(Symbol._rightSmallParenthesis)) {
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
        curNfaClosure = buildNfaClosureWithSymbols(Symbol.getAlphabetSymbols());
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
                    Symbol._Epsilon,
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
                    addInputSymbolAndNextNfaState(Symbol._Epsilon, endNfaState);
        }
    }

    private void processWhenEncounteredAdd() {
        buildEpsilonConnectionFromEachEndNfaStateToStartNfaState();

        pushCurNfaClosure();

        moveForward();
    }

    private void buildEpsilonConnectionFromEachEndNfaStateToStartNfaState() {
        for (NfaState endNfaState : curNfaClosure.getEndNfaStates()) {
            endNfaState.addInputSymbolAndNextNfaState(Symbol._Epsilon, curNfaClosure.getStartNfaState());
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
                        getCurSymbol().getChar()));
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
        boolean isNot = getCurSymbol().equals(Symbol._middleParenthesisNot);

        Set<Symbol> optionalSymbols = new HashSet<>();
        if (isNot) moveForward();

        do {
            if (getCurSymbol().equals(Symbol._escaped)) {
                moveForward();
                optionalSymbols.addAll(
                        EscapedUtil.getSymbolsOfEscapedCharInMiddleParenthesis(
                                getCurSymbol().getChar()));
            } else {
                optionalSymbols.add(getCurSymbol());
            }
            moveForward();
        } while (!getCurSymbol().equals(Symbol._rightMiddleParenthesis));

        moveForward();

        if (isNot) {
            return Symbol.getOppositeSymbols(optionalSymbols);
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

        addClonedCurNfaClosureToGroupNfaClosures();

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
            assert topStackUnion.isNfaClosure();

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
                assert !unions.isEmpty();
                thirdTopStackUnion = unions.pop();
                assert thirdTopStackUnion.isNfaClosure();
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

    private void addClonedCurNfaClosureToGroupNfaClosures() {
        assert curNfaClosure != null;
        groupNfaClosures.add(curNfaClosure.clone());
    }

    private void changeGroupOfCurNfaClosure() {
        assert curNfaClosure != null;
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
        assert preNfaClosure.getGroup() == nextNfaClosure.getGroup();

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

        public GroupUtil() {
            groupStack = new LinkedList<>();
            groupStack.push(0);
        }

        public int getCurGroup() {
            return groupStack.peek();
        }

        public void enterGroup() {
            groupStack.push(++groupCount);
        }

        public void exitGroup() {
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
            assert isNfaClosure();
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
