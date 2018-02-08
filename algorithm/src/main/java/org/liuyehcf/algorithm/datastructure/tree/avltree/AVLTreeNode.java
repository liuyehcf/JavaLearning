package org.liuyehcf.algorithm.datastructure.tree.avltree;

/**
 * Created by Liuye on 2017/4/27.
 */
public class AVLTreeNode {
    /**
     * 该节点的高度(从该节点到叶节点的最多边数)
     */
    int h;

    /**
     * 节点的值
     */
    int val;

    /**
     * 该节点的左孩子节点，右孩子节点，父节点
     */
    AVLTreeNode left, right, parent;

    public AVLTreeNode(int val) {
        h = 0;
        this.val = val;
        left = null;
        right = null;
        parent = null;
    }
}
