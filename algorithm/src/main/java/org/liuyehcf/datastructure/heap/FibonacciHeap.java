package org.liuyehcf.datastructure.heap;

import java.util.*;

/**
 * Created by liuye on 2017/4/15 0015.
 */
class FibonacciHeapNode {
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

/**
 * 概念说明
 * 1.由于链表都是首位相接,我将min,与node.child成为root链表的表头以及节点孩子链表的表头
 * 2.相对应的min.left与node.child.left成为链表尾
 */
public class FibonacciHeap {
    private int n;//堆节点的总数
    private FibonacciHeapNode min;//最小节点(某个最小堆的根节点)

    /**
     * 合并两个斐波那契堆
     *
     * @param heap1
     * @param heap2
     * @return
     */
    private static FibonacciHeap union(FibonacciHeap heap1, FibonacciHeap heap2) {
        FibonacciHeap heap;
        if (heap1.min == null) {
            heap = heap2;
        } else if (heap2.min == null) {
            heap = heap1;
        } else {
            FibonacciHeapNode temp = heap2.min.left;
            heap2.min.left = heap1.min.left;
            heap1.min.left.right = heap2.min;
            heap1.min.left = temp;
            temp.right = heap1.min;
            if (heap1.min.key <= heap2.min.key) {
                heap = heap1;
            } else {
                heap = heap2;
            }
            heap.n = heap1.n + heap2.n;
        }
        return heap;
    }

    /**
     * 移除指定节点,仅需要维护left,right字段即可
     */
    private static void removeNodeFromRootChain(FibonacciHeapNode z) {
        if (z.right != z) {
            z.left.right = z.right;
            z.right.left = z.left;
        }
    }

    /**
     * 将child插入到parent节点的孩子列表中,仅需要维护parent,left,right,child即可
     *
     * @param parent
     * @param child
     */
    private static void insertAsChild(FibonacciHeapNode parent, FibonacciHeapNode child) {
        FibonacciHeapNode firstChild = parent.child;
        if (firstChild == null) {//parent节点没有孩子节点
            //此时child将成为第一个孩子
            parent.child = child;
            child.left = child;
            child.right = child;
            child.parent = parent;
        } else {//否则将child插入到孩子链表尾部
            FibonacciHeapNode tail = firstChild.left;
            //将其插入到链表尾
            tail.right = child;
            child.left = tail;

            //维护环状链表
            child.right = firstChild;
            firstChild.left = child;

            child.parent = parent;
        }
    }

    public static void main(String[] args) {
        FibonacciHeap heap1 = new FibonacciHeap();
        Random random = new Random();
        int N = 1000;
        for (int i = 0; i < N; i++) {
            heap1.insert(random.nextInt());
            //System.out.println(heap1.min.key);
        }

        System.out.println("--------------------------------------------");

        for (int i = 0; i < N; i++) {
            FibonacciHeapNode extract = heap1.extractMin();
            System.out.println(extract.key + ", " + heap1.n);
        }
    }

    public void insert(int key) {
        FibonacciHeapNode x = new FibonacciHeapNode(key);

        insertNodeIntoRootChain(x);
        if (x.key < min.key) {
            min = x;
        }
        this.n++;

        if (!checkMinHeap(this.min)) throw new RuntimeException();
    }

    /**
     * 在min节点之前插入新节点,仅需要维护left,right字段即可
     *
     * @param node
     */
    private void insertNodeIntoRootChain(FibonacciHeapNode node) {
        if (this.min == null) {
            this.min = node;
            node.left = node;
            node.right = node;
        } else {
            FibonacciHeapNode tail = this.min.left;
            tail.right = node;
            node.left = tail;
            node.right = this.min;
            this.min.left = node;
        }
    }

    /**
     * 抽取最小节点
     *
     * @return
     */
    public FibonacciHeapNode extractMin() {
        FibonacciHeapNode z = min;
        if (z != null) {
            FibonacciHeapNode firstChild = z.child;
            if (firstChild != null) {
                FibonacciHeapNode cur = firstChild;
                do {
                    FibonacciHeapNode next = cur.right;
                    insertNodeIntoRootChain(cur);
                    cur.parent = null;
                    cur = next;
                } while (cur != firstChild);
            }
            removeNodeFromRootChain(z);
            if (z == z.right)
                this.min = null;
            else {
                this.min = z.right;//随便指向一个节点
                consolidate();
            }
            this.n--;
        }
        if (!checkMinHeap(this.min) || !uniqueDegreeAfterExtract()) throw new RuntimeException();
        return z;
    }

    /**
     * 合并所有度相同的根链表下的节点
     */
    private void consolidate() {
        Map<Integer, FibonacciHeapNode> degreeMap = new HashMap<Integer, FibonacciHeapNode>();

        //首先,将根链表中的节点都存到set中,因为在下面的处理流程中,这个链表会发生改变的
        Set<FibonacciHeapNode> set = new HashSet<FibonacciHeapNode>();
        FibonacciHeapNode ite = this.min;
        if (ite == null) throw new RuntimeException();
        do {
            set.add(ite);
            ite = ite.right;
        } while (ite != this.min);

        //遍历每个节点
        while (!set.isEmpty()) {
            FibonacciHeapNode x = set.iterator().next();//任意取一个节点

            int d = x.degree;
            while (degreeMap.containsKey(d)) {
                FibonacciHeapNode y = degreeMap.get(d);
                if (x.key > y.key) {
                    FibonacciHeapNode temp = x;
                    x = y;
                    y = temp;
                    set.remove(y);//todo 此时删的是从set中取的元素
                }
                heapLink(y, x);
                degreeMap.remove(d);
                d++;
            }

            //将x节点填入到map中
            degreeMap.put(d, x);
            set.remove(x);//todo 尝试着删吧,只有x还是之前从set取的元素时才会成功
        }
        this.min = null;


        for (Map.Entry<Integer, FibonacciHeapNode> m : degreeMap.entrySet()) {
            if (this.min == null) {
                insertNodeIntoRootChain(m.getValue());
                this.min = m.getValue();
            } else {
                insertNodeIntoRootChain(m.getValue());
                if (m.getValue().key < min.key) {
                    this.min = m.getValue();
                }
            }
        }
    }

    /**
     * 将节点y从根链表中移出,并且插入到x的孩子链表中
     */
    private void heapLink(FibonacciHeapNode y, FibonacciHeapNode x) {
        removeNodeFromRootChain(y);
        insertAsChild(x, y);
        x.degree++;
        y.marked = false;
    }

    public void decreaseKey(FibonacciHeapNode node, int key) {
        if (key > node.key) throw new RuntimeException();
        node.key = key;
        FibonacciHeapNode parent = node.parent;
        if (parent != null && node.key < parent.key) {
            //破坏了最小堆的性质
            cut(node, parent);
            cascadingCut(parent);
        }
        if (node.key < this.min.key) {
            this.min = node;
        }
    }

    /**
     * 切断child与parent的链接,并插入根链表
     *
     * @param child
     * @param parent
     */
    private void cut(FibonacciHeapNode child, FibonacciHeapNode parent) {
        removeChildFromNode(parent, child);
        insertNodeIntoRootChain(child);
        child.parent = null;
        child.marked = false;
    }

    /**
     * 将child从parent节点上切断,并插入根链表，只需要维护节点的left,right,parent,child字段即可
     *
     * @param parent
     * @param child
     */
    private void removeChildFromNode(FibonacciHeapNode parent, FibonacciHeapNode child) {
        FibonacciHeapNode firstChild = parent.child;
        FibonacciHeapNode ite = firstChild;
        do {
            if (ite == child)
                break;
            ite = ite.right;
        } while (ite != firstChild);

        if (ite == firstChild) {
            //todo 是否需要维护mark节点
            FibonacciHeapNode left = ite.left;
            FibonacciHeapNode right = ite.right;
            if (left == ite) {
                parent.child = null;
            } else {
                parent.child = right;
                right.left = left;
                left.right = right;
            }
        } else {
            FibonacciHeapNode left = ite.left;
            FibonacciHeapNode right = ite.right;
            left.right = right;
            right.left = left;
        }
    }

    private void cascadingCut(FibonacciHeapNode node) {
        FibonacciHeapNode parent = node.parent;
        if (parent != null) {
            if (!node.marked) {
                node.marked = true;
            } else {
                cut(node, parent);
                cascadingCut(parent);
            }
        }
    }

    private boolean checkMinHeap(FibonacciHeapNode node) {
        if (node == null || node.child == null) return true;
        FibonacciHeapNode firstChild = node.child;
        FibonacciHeapNode ite = firstChild;
        do {
            if (ite.key < node.key) return false;
            if (!checkMinHeap(ite)) return false;
            ite = ite.right;
        } while (ite != firstChild);
        return true;
    }

    private boolean uniqueDegreeAfterExtract() {
        Set<Integer> set = new HashSet<Integer>();
        FibonacciHeapNode node = this.min;
        if (node == null) return true;
        do {
            if (!set.add(node.degree)) return false;
            node = node.right;
        } while (node != this.min);
        return true;
    }
}




























