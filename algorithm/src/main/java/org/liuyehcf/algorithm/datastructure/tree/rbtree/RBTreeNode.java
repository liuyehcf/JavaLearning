package org.liuyehcf.algorithm.datastructure.tree.rbtree;

/**
 * Created by HCF on 2017/4/29.
 */
public class RBTreeNode {
    int val;
    RBTreeNode left;
    RBTreeNode right;
    RBTreeNode parent;
    Color color;

    RBTreeNode(int val) {
        this.val = val;
    }
}
