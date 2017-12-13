package org.liuyehcf.compile.grammar.regex.dfa;

/**
 * Created by Liuye on 2017/10/24.
 */
public class DfaStateDescription {
    private final String description;

    public DfaStateDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }

    @Override
    public int hashCode() {
        return this.description.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this.description.equals(((DfaStateDescription)obj).description);
    }
}
