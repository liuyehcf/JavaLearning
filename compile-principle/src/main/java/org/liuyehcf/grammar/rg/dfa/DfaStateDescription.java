package org.liuyehcf.grammar.rg.dfa;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.liuyehcf.grammar.utils.AssertUtils.assertNotNull;

/**
 * Created by Liuye on 2017/10/24.
 */
public class DfaStateDescription {

    // hash值
    private final int hash;

    // DfaState包含的所有NfaState的id集合
    private final Set<Integer> nfaStateIds;

    public DfaStateDescription(Set<Integer> nfaStateIds) {
        assertNotNull(nfaStateIds);

        this.nfaStateIds = Collections.unmodifiableSet(new HashSet<>(nfaStateIds));

        int shiftOffset = 0;

        int sum = 0;

        for (int id : nfaStateIds) {
            sum += (id << (shiftOffset++));
        }

        this.hash = 0;
    }

    @Override
    public String toString() {
        return nfaStateIds.toString();
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DfaStateDescription) {
            DfaStateDescription other = (DfaStateDescription) obj;
            return other.hash == this.hash
                    && other.nfaStateIds.equals(this.nfaStateIds);

        }
        return false;
    }
}
