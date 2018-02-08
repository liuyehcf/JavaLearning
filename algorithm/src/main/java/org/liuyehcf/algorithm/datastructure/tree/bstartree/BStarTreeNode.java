package org.liuyehcf.algorithm.datastructure.tree.bstartree;

/**
 * Created by liuye on 2017/5/8 0008.
 */
public class BStarTreeNode {
    /**
     * 关键字数量
     */
    int n;

    /**
     * 关键字
     */
    int[] keys;

    /**
     * 孩子
     */
    BStarTreeNode[] children;

    /**
     * 是否为叶子节点
     */
    boolean isLeaf;

    /**
     * 右兄弟
     * 只有叶节点的该字段有效，与网上的定义不一致，因为我的实现中不需要
     */
    BStarTreeNode next;

    public BStarTreeNode(int t) {
        n = 0;
        keys = new int[6 * t];
        children = new BStarTreeNode[6 * t];
        isLeaf = false;
        next = null;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{ size: " + n + ", keys: [");
        for (int i = 0; i < n; i++) {
            sb.append(keys[i] + ", ");
        }
        sb.append("] }");
        return sb.toString();
    }
}
