package org.liuyehcf.grammar.utils;

public class Pair<FIRST, SECOND> {
    private final FIRST first;
    private final SECOND second;

    public Pair(FIRST first, SECOND second) {
        this.first = first;
        this.second = second;
    }

    public FIRST getFirst() {
        return first;
    }

    public SECOND getSecond() {
        return second;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        if (first != null) {
            hash += first.hashCode();
        }

        if (second != null) {
            hash += second.hashCode();
        }

        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Pair) {
            Pair other = (Pair) obj;

            if (other.first != null && other.second != null) {
                return other.first.equals(this.first)
                        && other.second.equals(this.second);
            } else if (other.first != null) {
                return other.first.equals(this.first);
            } else if (other.second != null) {
                return other.second.equals(this.second);
            } else {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "Pair{" +
                "first=" + first +
                ", second=" + second +
                '}';
    }
}
