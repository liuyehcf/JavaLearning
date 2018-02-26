package org.liuyehcf.grammar.utils;

public class Tuple<FIRST, SECOND, THIRD> {
    private final FIRST first;

    private final SECOND second;

    private final THIRD third;

    public Tuple(FIRST first, SECOND second, THIRD third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public FIRST getFirst() {
        return first;
    }

    public SECOND getSecond() {
        return second;
    }

    public THIRD getThird() {
        return third;
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

        if (third != null) {
            hash += third.hashCode();
        }
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Tuple) {
            Tuple other = (Tuple) obj;

            if (other.first != null && other.second != null && other.third != null) {
                return other.first.equals(this.first)
                        && other.second.equals(this.second)
                        && other.third.equals(this.third);
            } else if (other.first != null && other.second != null) {
                return this.third == null
                        && other.first.equals(this.first)
                        && other.second.equals(this.second);
            } else if (other.first != null && other.third != null) {
                return this.second == null
                        && other.first.equals(this.first)
                        && other.third.equals(this.third);
            } else if (other.second != null && other.third != null) {
                return this.first == null
                        && other.second.equals(this.second)
                        && other.third.equals(this.third);
            } else if (other.first != null) {
                return this.second == null
                        && this.third == null
                        && other.first.equals(this.first);
            } else if (other.second != null) {
                return this.first == null
                        && this.third == null
                        && other.second.equals(this.second);
            } else if (other.third != null) {
                return this.first == null
                        && this.second == null
                        && other.third.equals(this.third);
            } else {
                return this.first == null && this.second == null && this.third == null;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "Tuple{" +
                "first=" + first +
                ", second=" + second +
                ", third=" + third +
                '}';
    }
}
