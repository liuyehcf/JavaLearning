package org.liuyehcf.grammar.rg.nfa;


import org.liuyehcf.grammar.definition.Symbol;

import java.util.*;

/**
 * Created by Liuye on 2017/10/21.
 */
public class NfaState {
    private static int count = 1;
    private final int id = count++;
    private final List<NfaState> NONE = new ArrayList<>();
    private boolean canReceive;
    private Map<Symbol, List<NfaState>> nextNfaStatesMap = new HashMap<>();

    public int getId() {
        return id;
    }

    public void setCanReceive() {
        this.canReceive = true;
    }

    public boolean isCanReceive() {
        return canReceive;
    }

    public Set<Symbol> getAllInputSymbol() {
        return nextNfaStatesMap.keySet();
    }

    public List<NfaState> getNextNfaStatesWithInputSymbol(Symbol symbol) {
        if (nextNfaStatesMap.containsKey(symbol)) {
            return nextNfaStatesMap.get(symbol);
        } else {
            return NONE;
        }
    }

    public void addInputSymbolAndNextNfaState(Symbol symbol, NfaState nextNfaState) {
        if (!nextNfaStatesMap.containsKey(symbol)) {
            nextNfaStatesMap.put(symbol, new ArrayList<>());
        }
        nextNfaStatesMap.get(symbol).add(nextNfaState);
    }

    public void cleanConnections() {
        nextNfaStatesMap.clear();
    }

    @Override
    public String toString() {
        return "NfaState[" + id + "]";
    }
}
