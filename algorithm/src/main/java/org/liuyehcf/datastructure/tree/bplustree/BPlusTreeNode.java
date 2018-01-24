package org.liuyehcf.datastructure.tree.bplustree;

/**
 * Created by liuye on 2017/5/3 0003.
 */

/**
 * 对比前两版的实现，此版本采用了2*t的关键字个数
 * 不支持节点包含奇数个关键字，这种自由度没有什么太大作用
 * 偶数个关键字可以很好地支持自顶向下的维护节点数目，类似于B-树
 */
public class BPlusTreeNode {
    /**
     * 关键字个数
     */
    int n;

    /**
     * 关键字
     */
    int[] keys;

    /**
     * 孩子
     */
    BPlusTreeNode[] children;

    /**
     * 叶子节点
     */
    boolean isLeaf;

    /**
     * 兄弟节点
     */
    BPlusTreeNode next;

    public BPlusTreeNode(int t) {
        n = 0;
        keys = new int[2 * t];
        children = new BPlusTreeNode[2 * t];
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
