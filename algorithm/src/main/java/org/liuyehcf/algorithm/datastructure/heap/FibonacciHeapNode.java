package org.liuyehcf.algorithm.datastructure.heap;

public class FibonacciHeapNode {
    int key;//节点
    int degree;//度,记录该节点有几个孩子
    FibonacciHeapNode left;//左兄弟
    FibonacciHeapNode right;//右兄弟
    FibonacciHeapNode parent;//父节点
    FibonacciHeapNode child;//第一个孩子节点
    boolean marked;//是否被删除第一个孩子

    public FibonacciHeapNode(int key) {
        this.key = key;
        this.degree = 0;
        this.left = null;
        this.right = null;
        this.parent = null;
        this.child = null;
        this.marked = false;
    }
}
