package org.liuyehcf.datastructure.tree.btree;

/**
 * Created by HCF on 2017/4/29.
 */
public class BTreeNode {
    int n;
    int[] keys;
    BTreeNode[] children;
    boolean isLeaf;

    BTreeNode(int t) {
        n = 0;
        keys = new int[2 * t - 1];
        children = new BTreeNode[2 * t];
        isLeaf = false;
    }
}
