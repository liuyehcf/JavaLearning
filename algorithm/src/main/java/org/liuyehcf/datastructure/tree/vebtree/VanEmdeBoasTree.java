package org.liuyehcf.datastructure.tree.vebtree;


/**
 * Created by HCF on 2017/4/29.
 */
public class VanEmdeBoasTree {
    private static final int NIL = Integer.MIN_VALUE;

    private VanEmdeBoasTreeNode root;

    public VanEmdeBoasTree() {
        root = new VanEmdeBoasTreeNode();
    }

    /**
     * 计算出在当前节点下,索引x位于哪个子节点中
     * 即商
     *
     * @param v
     * @param x
     * @return
     */
    private static int high(VanEmdeBoasTreeNode v, int x) {
        return (int) Math.floor(x / floorSqrt(v.u));
    }

    /**
     * 计算出在当前节点下,索引x在子节点中的索引
     * 即余数
     *
     * @param v
     * @param x
     * @return
     */
    private static int low(VanEmdeBoasTreeNode v, int x) {
        return x % floorSqrt(v.u);
    }

    private static int index(VanEmdeBoasTreeNode v, int x, int y) {
        return x * floorSqrt(v.u) + y;
    }

    /**
     * 开方后向上取整
     *
     * @param x
     * @return
     */
    private static int ceilSqrt(int x) {
        return (int) Math.ceil(Math.sqrt(x));
    }

    /**
     * 开方后向下取整
     *
     * @param x
     * @return
     */
    private static int floorSqrt(int x) {
        return (int) Math.floor(Math.sqrt(x));
    }

    public int minimum(VanEmdeBoasTreeNode v) {
        return v.min;
    }

    public int maximum(VanEmdeBoasTreeNode v) {
        return v.max;
    }

    /**
     * 判断值x是否在以v为根的子树中
     *
     * @param v
     * @param x
     * @return
     */
    public boolean isMember(VanEmdeBoasTreeNode v, int x) {
        if (x == v.min || x == v.max) {
            return true;
        } else if (v.u == 2) {
            return false;
        } else {
            return isMember(v.cluster[high(v, x)], low(v, x));
        }
    }

    public int successor(VanEmdeBoasTreeNode v, int x) {
        if (v.u == 2) {
            if (x == 0 && v.max == 1) {
                return 1;
            } else {
                return NIL;
            }
        } else if (v.min != NIL && x < v.min) {
            return v.min;
        } else {
            int maxLow = maximum(v.cluster[high(v, x)]);
            if (maxLow != NIL && low(v, x) < maxLow) {
                int offset = successor(v.cluster[high(v, x)], low(v, x));
                return index(v, high(v, x), offset);
            } else {
                int successorCluster = successor(v.summary, high(v, x));
                if (successorCluster == NIL) {
                    return NIL;
                } else {
                    int offset = minimum(v.cluster[successorCluster]);
                    return index(v, successorCluster, offset);
                }
            }
        }
    }

    public int predecessor(VanEmdeBoasTreeNode v, int x) {
        if (v.u == 2) {
            if (x == 1 && v.min == 0) {
                return 0;
            } else {
                return NIL;
            }
        } else if (v.max != NIL && x > v.max) {
            return v.max;
        } else {
            int minLow = minimum(v.cluster[high(v, x)]);
            if (minLow != NIL && low(v, x) > minLow) {
                int offset = predecessor(v.cluster[high(v, x)], low(v, x));
                return index(v, high(v, x), offset);
            } else {
                int predecessorCluster = predecessor(v.summary, high(v, x));
                if (predecessorCluster == NIL) {
                    if (v.min != NIL && x > v.min) {
                        return v.min;
                    } else {
                        return NIL;
                    }

                } else {
                    int offset = maximum(v.cluster[predecessorCluster]);
                    return index(v, predecessorCluster, offset);
                }
            }
        }
    }

    private void insertEmpty(VanEmdeBoasTreeNode v, int x) {
        v.min = x;
        v.max = x;
    }

    public void insert(VanEmdeBoasTreeNode v, int x) {
        if (v.min == NIL) {
            insertEmpty(v, x);
        } else {
            if (x < v.min) {
                int temp = x;
                x = v.min;
                v.min = temp;
            }
            if (v.u > 2) {
                if (minimum(v.cluster[high(v, x)]) == NIL) {
                    insert(v.summary, high(v, x));
                    insertEmpty(v.cluster[high(v, x)], low(v, x));
                } else {
                    insert(v.cluster[high(v, x)], low(v, x));
                }
            }
            if (x > v.max) {
                v.max = x;
            }
        }
    }
}
