package org.liuyehcf.grammar.rg.nfa;


import org.liuyehcf.grammar.core.definition.Symbol;
import org.liuyehcf.grammar.rg.utils.EscapedUtil;
import org.liuyehcf.grammar.rg.utils.SymbolUtils;
import org.liuyehcf.grammar.utils.ListUtils;
import org.liuyehcf.grammar.utils.Pair;

import java.util.*;

import static org.liuyehcf.grammar.utils.AssertUtils.*;

/**
 * Created by Liuye on 2017/10/23.
 * todo 目前还不支持：1. 匹配模式的选择
 * ┐ ┘ └ ┌   ┴ ├ ┬ ┤  ┼   ─  │
 * <p>
 * ┓ ┛ ┗ ┏   ┻ ┣ ┳ ┫  ╋   ━  ┃
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

    static Pair<NfaClosure, Integer> createNfaClosure(List<Symbol> symbols) {
        NfaBuildIterator buildIterator = new NfaBuildIterator(symbols);

        while (buildIterator.hasNext()) {
            buildIterator.processEachSymbol();
        }

        buildIterator.finishWork();

        return new Pair<>(buildIterator.nfaClosure, buildIterator.groupUtil.getMaxGroup());
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
        } else if (curSymbol.equals(SymbolUtils._unKnow)) {
            processWhenEncounteredUnKnow();
        } else if (curSymbol.equals(SymbolUtils._star)) {
            processWhenEncounteredStar();
        } else if (curSymbol.equals(SymbolUtils._add)) {
            processWhenEncounteredAdd();
        } else if (curSymbol.equals(SymbolUtils._leftBigParenthesis)) {
            processWhenEncounteredLeftBigParenthesis();
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

        // 创建一个新的NfaClosure
        buildNfaClosureForAnyAsCurNfaClosure();

        moveForward();
    }

    private void buildNfaClosureForAnyAsCurNfaClosure() {
        curNfaClosure = buildNfaClosureWithSymbols(SymbolUtils.getAlphabetSymbols());
    }

    private void processWhenEncounteredOr() {
        // 合并左侧的NfaClosure
        combineNfaClosuresOfCurGroup();

        pushCurNfaClosure();

        // 插入一个parallel操作的占位符
        pushParallel();

        moveForward();
    }

    private void processWhenEncounteredUnKnow() {
        // 用一个新的NfaClosure封装当前NfaClosure
        wrapCurNfaClosureForUnKnow();

        pushCurNfaClosure();

        moveForward();
    }

    private void wrapCurNfaClosureForUnKnow() {
        curNfaClosure = createUnKnowWrappedNfaClosureFor(curNfaClosure);
    }

    private NfaClosure createUnKnowWrappedNfaClosureFor(NfaClosure __INNER) {
        /*
         * Inner: 内层NfaClosure
         * Outer: 外层NfaClosure
         * S: 开始节点
         * E(i): 第i个终止节点
         * P,Q: 外层NfaClosure的特殊节点
         * --*>: 经过多步跳转
         * -->: 经过一步跳转
         *
         *                                  Inner.S ───────*> Inner.E(1)
         *                                     ├───────────*> Inner.E(2)
         *                                     │       ...
         *                                     └───────────*> Inner.E(n)
         *
         *                                             ||
         *                                             ||
         *                                             ||
         *                                            \  /
         *                                             \/
         *
         *
         *                   请注意，步骤1，2的相对顺序不可乱，步骤的次序即匹配的策略（贪婪or勉强or占有），这里的连线都是ε边
         *
         *
         *                ┌────────────────────────────────────────── 2 ───────────────────────────────────────────────┐
         *                │                                                                                            │
         *                │                                                                                            V
         *             Outer.S ──── 1 ───> Inner.S ───────*> Inner.E(1) ──────── 4 ─────> Outer.Q ──────── 3 ─────> Outer.E
         *                                    │                                             Λ
         *                                    │                                             │
         *                                    ├───────────*> Inner.E(2) ──────── 4 ─────────┤
         *                                    │                 ...                         │
         *                                    └───────────*> Inner.E(n) ──────── 4 ─────────┘
         *
         */

        NfaClosure wrapNfaClosure = buildWrapNfaClosure();

        // 必须保证wrapNfaClosure单入单出，否则group匹配会出现边界问题（ "(a)?" 与 "(a?)" ）
        NfaState _OUTER_S = wrapNfaClosure.getStartNfaState();
        NfaState _OUTER_Q = new NfaState();
        NfaState _OUTER_E = wrapNfaClosure.getEndNfaStates().get(0);

        assertNotNull(__INNER);
        NfaState _INNER_S = __INNER.getStartNfaState();

        // (1)
        _OUTER_S.addInputSymbolAndNextNfaState(Symbol.EPSILON, _INNER_S);

        // (2)
        _OUTER_S.addInputSymbolAndNextNfaState(Symbol.EPSILON, _OUTER_E);

        // (3)
        _OUTER_Q.addInputSymbolAndNextNfaState(Symbol.EPSILON, _OUTER_E);

        for (NfaState _INNER_E : __INNER.getEndNfaStates()) {
            // (4)
            _INNER_E.addInputSymbolAndNextNfaState(Symbol.EPSILON, _OUTER_Q);
        }

        return wrapNfaClosure;
    }

    private NfaClosure buildWrapNfaClosure() {
        NfaState startNfaState = new NfaState();
        NfaState endNfaState = new NfaState();

        return new NfaClosure(startNfaState, ListUtils.of(endNfaState), getCurGroup());
    }

    private void processWhenEncounteredStar() {
        // 用一个新的NfaClosure封装当前NfaClosure
        wrapCurNfaClosureForStar();

        pushCurNfaClosure();

        moveForward();
    }

    private void wrapCurNfaClosureForStar() {
        curNfaClosure = createStarWrappedNfaClosureFor(curNfaClosure);
    }

    private NfaClosure createStarWrappedNfaClosureFor(NfaClosure _INNER) {
        /*
         * Inner: 内层NfaClosure
         * Outer: 外层NfaClosure
         * S: 开始节点
         * E(i): 第i个终止节点
         * P,Q: 外层NfaClosure的特殊节点
         * --*>: 经过多步跳转
         * -->: 经过一步跳转
         *
         *                                  Inner.S ───────*> Inner.E(1)
         *                                     ├───────────*> Inner.E(2)
         *                                     │       ...
         *                                     └───────────*> Inner.E(n)
         *
         *                                             ||
         *                                             ||
         *                                             ||
         *                                            \  /
         *                                             \/
         *
         *                   请注意，步骤2，3的相对顺序不可乱，步骤的次序即匹配的策略（贪婪or勉强or占有），这里的连线都是ε边
         *
         *                           ┌───────────────────────────────── 3 ──────────────────────────────┐
         *                           │                                                                  │
         *                           │                                                                  V
         *    Outer.S ─── 1 ───> Outer.P ───── 2 ───> Inner.S ───────*> Inner.E(1) ─────────┐        Outer.E
         *                           Λ                   ├───────────*> Inner.E(2) ─────┐   │
         *                           │                   │       ...                    │   │
         *                           │                   └───────────*> Inner.E(n) ──┐  │   │
         *                           │                                               │  │   │
         *                           │                                               │  │   │
         *                           ├───────────────────── 4 ───────────────────────┘  │   │
         *                           │                                                  │   │
         *                           ├───────────────────── 4 ──────────────────────────┘   │
         *                           │                                                      │
         *                           └───────────────────── 4 ──────────────────────────────┘
         *
         */


        NfaClosure _OUTER = buildWrapNfaClosure();

        // 必须保证wrapNfaClosure单入单出，否则group匹配会出现边界问题（ "(a)*" 与 "(a*)" ）
        NfaState _OUTER_S = _OUTER.getStartNfaState();
        NfaState _OUTER_P = new NfaState();
        NfaState _OUTER_E = _OUTER.getEndNfaStates().get(0);

        assertNotNull(_INNER);
        NfaState _INNER_S = _INNER.getStartNfaState();

        // (1)
        _OUTER_S.addInputSymbolAndNextNfaState(Symbol.EPSILON, _OUTER_P);

        // (2)
        _OUTER_P.addInputSymbolAndNextNfaState(Symbol.EPSILON, _INNER_S);

        // (3)
        _OUTER_P.addInputSymbolAndNextNfaState(Symbol.EPSILON, _OUTER_E);

        for (NfaState _INNER_E : _INNER.getEndNfaStates()) {
            // (4)
            _INNER_E.addInputSymbolAndNextNfaState(Symbol.EPSILON, _OUTER_P);
        }

        return _OUTER;
    }

    private void processWhenEncounteredAdd() {
        // 用一个新的NfaClosure封装当前NfaClosure
        wrapCurNfaClosureForAdd();

        pushCurNfaClosure();

        moveForward();
    }

    private void wrapCurNfaClosureForAdd() {
        curNfaClosure = createAddWrappedNfaClosureFor(curNfaClosure);
    }

    private NfaClosure createAddWrappedNfaClosureFor(NfaClosure _INNER) {
        /*
         * Inner: 内层NfaClosure
         * Outer: 外层NfaClosure
         * S: 开始节点
         * E(i): 第i个终止节点
         * P,Q: 外层NfaClosure的特殊节点
         * --*>: 经过多步跳转
         * -->: 经过一步跳转
         *
         *                                  Inner.S ───────*> Inner.E(1)
         *                                     ├───────────*> Inner.E(2)
         *                                     │       ...
         *                                     └───────────*> Inner.E(n)
         *
         *                                             ||
         *                                             ||
         *                                             ||
         *                                            \  /
         *                                             \/
         *
         *
         *                   请注意，步骤4，5的相对顺序不可乱，步骤的次序即匹配的策略（贪婪or勉强or占有），这里的连线都是ε边
         *
         *
         *    Outer.S ─── 1 ───> Outer.P ──── 2 ───> Inner.S ───────*> Inner.E(1) ─────────┬──────── 5 ──────> Outer.Q ───── 3 ───> Outer.E
         *                           Λ                  │                                  │                     Λ
         *                           │                  │                                  │                     │
         *                           │                  ├───────────*> Inner.E(2) ─────┬───┼──────── 5 ──────────┤
         *                           │                  │       ...                    │   │                     │
         *                           │                  └───────────*> Inner.E(n) ──┬──┼───┼──────── 5 ──────────┘
         *                           │                                              │  │   │
         *                           │                                              │  │   │
         *                           ├───────────────────── 4 ──────────────────────┘  │   │
         *                           │                                                 │   │
         *                           ├───────────────────── 4 ─────────────────────────┘   │
         *                           │                                                     │
         *                           └───────────────────── 4 ─────────────────────────────┘
         *
         *
         */

        NfaClosure wrapNfaClosure = buildWrapNfaClosure();

        // 必须保证wrapNfaClosure单入单出，否则group匹配会出现边界问题（ "(a)+" 与 "(a+)" ）
        NfaState _OUTER_S = wrapNfaClosure.getStartNfaState();
        NfaState _OUTER_P = new NfaState();
        NfaState _OUTER_Q = new NfaState();
        NfaState _OUTER_E = wrapNfaClosure.getEndNfaStates().get(0);

        assertNotNull(_INNER);
        NfaState _INNER_S = _INNER.getStartNfaState();

        // (1)
        _OUTER_S.addInputSymbolAndNextNfaState(Symbol.EPSILON, _OUTER_P);

        // (2)
        _OUTER_P.addInputSymbolAndNextNfaState(Symbol.EPSILON, _INNER_S);

        // (3)
        _OUTER_Q.addInputSymbolAndNextNfaState(Symbol.EPSILON, _OUTER_E);

        for (NfaState _INNER_E : _INNER.getEndNfaStates()) {
            // (4)
            _INNER_E.addInputSymbolAndNextNfaState(Symbol.EPSILON, _OUTER_P);

            // (5)
            _INNER_E.addInputSymbolAndNextNfaState(Symbol.EPSILON, _OUTER_Q);
        }

        return wrapNfaClosure;
    }

    private void processWhenEncounteredLeftBigParenthesis() {
        // 用一个新的NfaClosure封装当前NfaClosure
        wrapCurNfaClosureForLeftBigParenthesis();

        pushCurNfaClosure();
    }

    private void wrapCurNfaClosureForLeftBigParenthesis() {
        curNfaClosure = createLeftBigParenthesisWrappedNfaClosureFor(getRepeatInterval());
    }

    private NfaClosure createLeftBigParenthesisWrappedNfaClosureFor(Pair<Integer, Integer> repeatInterval) {
        // 依据指定的重复区间，构建NfaClosure
        return buildNfaClosureForRepeatInterval(repeatInterval);
    }

    private NfaClosure buildNfaClosureForRepeatInterval(Pair<Integer, Integer> repeatInterval) {
        assertNotNull(repeatInterval.getFirst());

        NfaClosure newNfaClosure;

        // a{1,}
        if (repeatInterval.getSecond() == null) {
            newNfaClosure = buildRepeatedNfaClosureFor(curNfaClosure, repeatInterval.getFirst());

            combineTwoClosure(
                    newNfaClosure,
                    createStarWrappedNfaClosureFor(curNfaClosure)
            );
            return newNfaClosure;
        } else {
            newNfaClosure = buildRepeatedNfaClosureFor(curNfaClosure, repeatInterval.getFirst());
            for (int repeatTime = repeatInterval.getFirst() + 1; repeatTime <= repeatInterval.getSecond(); repeatTime++) {
                parallel(
                        newNfaClosure,
                        buildRepeatedNfaClosureFor(curNfaClosure, repeatTime)
                );
            }
        }

        return newNfaClosure;
    }

    private NfaClosure buildRepeatedNfaClosureFor(NfaClosure originalNfaClosure, int repeatTime) {
        int count = 1;

        NfaClosure repeatedNfaClosure = originalNfaClosure.clone();

        while (count < repeatTime) {
            NfaClosure newClosure = originalNfaClosure.clone();
            combineTwoClosure(repeatedNfaClosure, newClosure);
            count++;
        }

        return repeatedNfaClosure;
    }

    private Pair<Integer, Integer> getRepeatInterval() {
        moveForward();

        Integer leftNumber = null, rightNumber = null;

        StringBuilder sb = new StringBuilder();
        while (!getCurSymbol().equals(SymbolUtils._rightBigParenthesis)) {

            if (SymbolUtils.getChar(getCurSymbol()) == ',') {
                assertNull(leftNumber);
                leftNumber = Integer.parseInt(sb.toString());
                sb = new StringBuilder();
            } else {
                sb.append(SymbolUtils.getChar(getCurSymbol()));
            }

            moveForward();
        }


        if (leftNumber == null) {
            leftNumber = Integer.parseInt(sb.toString());
            rightNumber = leftNumber;
        } else {
            if (sb.length() > 0) {
                rightNumber = Integer.parseInt(sb.toString());
            }
        }

        if (rightNumber != null) {
            assertTrue(rightNumber >= leftNumber);
        }

        moveForward();

        return new Pair<>(leftNumber, rightNumber);
    }

    private void processWhenEncounteredEscaped() {
        pushCurNfaClosure();

        // 创建一个新的NfaClosure
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

        // 创建一个新的NfaClosure
        buildNfaClosureForMiddleParenthesisAsCurNfaClosure();
    }

    private void buildNfaClosureForMiddleParenthesisAsCurNfaClosure() {
        curNfaClosure = buildNfaClosureWithSymbols(getOptionalSymbols());
    }

    private Set<Symbol> getOptionalSymbols() {
        moveForward();
        boolean isNot = getCurSymbol().equals(SymbolUtils._middleParenthesisNot);
        if (isNot) {
            moveForward();
        }

        Set<Symbol> optionalSymbols = new HashSet<>();

        int pre = -1;
        boolean hasTo = false;

        do {
            if (getCurSymbol().equals(SymbolUtils._escaped)) {
                moveForward();
                optionalSymbols.addAll(
                        EscapedUtil.getSymbolsOfEscapedCharInMiddleParenthesis(
                                SymbolUtils.getChar(getCurSymbol())));
                pre = -1;
            }
            // '-'前面存在有效字符时
            else if (pre != -1 && getCurSymbol().equals(SymbolUtils._to)) {
                assertFalse(hasTo);
                hasTo = true;
            } else {
                if (hasTo) {
                    assertTrue(pre != -1);
                    assertTrue(pre <= SymbolUtils.getChar(getCurSymbol()));
                    // pre在上一次已经添加过了，本次从pre+1开始
                    for (char c = (char) (pre + 1); c <= SymbolUtils.getChar(getCurSymbol()); c++) {
                        optionalSymbols.add(SymbolUtils.getAlphabetSymbolWithChar(c));
                    }
                    pre = -1;
                    hasTo = false;
                } else {
                    pre = SymbolUtils.getChar(getCurSymbol());
                    optionalSymbols.add(getCurSymbol());
                }
            }
            moveForward();
        } while (!getCurSymbol().equals(SymbolUtils._rightMiddleParenthesis));

        // 最后一个'-'当做普通字符
        if (hasTo) {
            optionalSymbols.add(SymbolUtils._to);
        }

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
        // 合并NfaClosure
        combineNfaClosuresOfCurGroup();

        // 为当前组设置接受以及起始状态标记
        setStartAndReceiveOfCurNfaClosure();

        exitGroup();

        // 修改当前NfaClosure的组
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

        // 创建一个新的NfaClosure
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

    private void combineTwoClosure(NfaClosure _PRE, NfaClosure _NEXT) {
        assertTrue(_PRE.getGroup() == _NEXT.getGroup());

        /*
         *
         * Pre: 左侧NfaClosure
         * Next: 右侧NfaClosure
         * S: 开始节点
         * E(i): 第i个终止节点
         * S.N(i): 开始节点的第i个后继节点
         * --*>: 经过多步跳转
         * -->: 经过一步跳转
         *
         *            ┌──────────*> Pre.E(1)                            ┌───────────*> Next.E(1)
         *            │                                                 │
         *          Pre.S────────*> Pre.E(2)                        Next.S ─────────*> Next.E(2)
         *            │       ...                                       │       ...
         *            └──────────*> Pre.E(n)                            └───────────*> Next.E(m)
         *
         *                                             ||
         *                                             ||
         *                                             ||
         *                                            \  /
         *                                             \/
         *
         *            ┌──────────*> Pre.E(1) ────────── 1 ──────────┐   ┌───────────*> Next.E(1)
         *            │                                             V   │
         *          Pre.S────────*> Pre.E(2) ────────── 1 ────────> Next.S ─────────*> Next.E(2)
         *            │               ...                           Λ   │       ...
         *            └──────────*> Pre.E(n) ────────── 1 ──────────┘   └───────────*> Next.E(m)
         *
         */

        NfaState _NEXT_S = _NEXT.getStartNfaState();

        for (NfaState _PRE_E : _PRE.getEndNfaStates()) {
            // (1)
            _PRE_E.addInputSymbolAndNextNfaState(Symbol.EPSILON, _NEXT_S);
        }

        _PRE.setEndNfaStates(_NEXT.getEndNfaStates());
    }

    private void parallel(NfaClosure _PRE, NfaClosure _NEXT) {
        /*
         *
         * Pre: 左侧NfaClosure
         * Next: 右侧NfaClosure
         * S: 开始节点
         * E(i): 第i个终止节点
         * S.N(i): 开始节点的第i个后继节点
         * --*>: 经过多步跳转
         * -->: 经过一步跳转
         *
         *            ┌──────────*> Pre.E(1)                            ┌───────────*> Next.E(1)
         *            │                                                 │
         *          Pre.S────────*> Pre.E(2)                        Next.S ─────────*> Next.E(2)
         *            │       ...                                       │       ...
         *            └──────────*> Pre.E(n)                            └───────────*> Next.E(m)
         *
         *                                             ||
         *                                             ||
         *                                             ||
         *                                            \  /
         *                                             \/
         *
         *            ┌──────────*> Pre.E(1)                            ┌───────────*> Next.E(1)
         *            │                                                 │
         *          Pre.S────────*> Pre.E(2)            ┌─────────> Next.S ─────────*> Next.E(2)
         *            │       ...                       │               │       ...
         *            ├──────────*> Pre.E(n)            │               └───────────*> Next.E(m)
         *            │                                 │
         *            └──────────────────────── 1 ──────┘
         *
         *   ******************************************************************************************
         *   **                                                                                      **
         *   **   parallel不能用一个新的单入单出的NfaClosure包裹起来，这样会导致"(a)|(b)|(ab)"只有一个出口   **
         *   **       必须保证多出的特性("NfaMatcher.initMatchIntervals"方法会利用该特性实现贪婪find)      **
         *   **                                                                                      **
         *   ******************************************************************************************
         */

        NfaState _PRE_S = _PRE.getStartNfaState();
        NfaState _NEXT_S = _NEXT.getStartNfaState();

        /*
         * (1)
         * NfaState中的邻接节点用的是LinkedHashMap，且对于同一个输入符号的不同后继邻接节点用的是LinkedHashSet，保证了邻接节点的相对顺序
         * 结合"NfaMatcher.isMatchDfs"方法的实现，就可以保证 "(a)|(a*)" 匹配"a"时，group(1)="a", group(2)=null
         */
        _PRE_S.addInputSymbolAndNextNfaState(Symbol.EPSILON, _NEXT_S);

        // 更新Pre的终止节点
        _PRE.getEndNfaStates().addAll(_NEXT.getEndNfaStates());
    }

    private static class GroupUtil {
        private int groupCount = 0;
        private int maxGroup = 0;
        private LinkedList<Integer> groupStack;

        private GroupUtil() {
            groupStack = new LinkedList<>();
            groupStack.push(0);
        }

        private int getCurGroup() {
            return groupStack.peek();
        }

        private int getMaxGroup() {
            return maxGroup;
        }

        private void enterGroup() {
            groupCount++;
            maxGroup = Math.max(maxGroup, groupCount);
            groupStack.push(groupCount);
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
