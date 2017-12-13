package org.liuyehcf.datastructure.tree.bstartree;

import java.util.*;

/**
 * Created by liuye on 2017/5/8 0008.
 */
public class BStarTree {
    /**
     * 根节点
     */
    private BStarTreeNode root;

    /**
     * 叶节点链表头
     */
    private BStarTreeNode data;

    /**
     * B*树的度，非根节点且非叶节点最少含有4t个关键字，最多含有6t个关键字
     * 根节点关键字数量[0,6t]
     * 叶节点关键字数量[3t,6t]
     * 叶节点最少关键字数量取一半是想要减少叶节点的数量，否则将会导致叶节点增多
     */
    private int t;

    private final int MIN_KEY_NUM;

    private final int MAX_KEY_NUM;

    private final int HALF_KEY_NUM;

    public BStarTree(int t) {
        this.t = t;
        root = new BStarTreeNode(t);
        root.n = 0;
        root.isLeaf = true;
        root.next = null;
        MIN_KEY_NUM = 4 * t;
        MAX_KEY_NUM = 6 * t;
        HALF_KEY_NUM = 3 * t;

        data = root;
    }

    public void insert(int k) {
        if (root.n == MAX_KEY_NUM) {
            BStarTreeNode newRoot = new BStarTreeNode(t);
            newRoot.n = 1;
            newRoot.keys[0] = root.keys[root.n - 1];
            newRoot.children[0] = root;
            newRoot.isLeaf = false;
            newRoot.next = null;
            root = newRoot;
            splitOuter(root, 0);
        }
        insertNotFull(root, k);
        if (!checkIndex(root)) {
            levelOrderTraverse();
            throw new RuntimeException("checkIndex");
        }
//        if (!checkN(root)) {
//            levelOrderTraverse();
//            throw new RuntimeException("checkN");
//        }
        if (!checkOrder()) {
            levelOrderTraverse();
            throw new RuntimeException("checkOrder");
        }
    }

    private void insertNotFull(BStarTreeNode x, int k) {
        int i = x.n - 1;
        if (x.isLeaf) {
            while (i >= 0 && x.keys[i] > k) {
                x.keys[i + 1] = x.keys[i];
                i--;
            }
            if (i >= 0 && x.keys[i] == k) {
                throw new RuntimeException();
            }
            i++;
            x.keys[i] = k;
            x.n++;
        } else {
            //todo 这里的等号很关键
            while (i >= 0 && x.keys[i] >= k) {
                i--;
            }
            i++;

            //todo 关键，自上而下寻找插入点时，即维护了索引的正确性
            if (i == x.n) {
                x.keys[x.n - 1] = k;
                i--;
            }

            BStarTreeNode y = x.children[i];
            BStarTreeNode p = null, z = null;
            if (i > 0) {
                p = x.children[i - 1];
            }
            if (i < x.n - 1) {
                z = x.children[i + 1];
            }
            if (y.n == MAX_KEY_NUM) {
                if (p != null && p.n < MAX_KEY_NUM) {
                    //todo 这里当p的大小为MAX_KEY_NUM-1时，并且下面的条件成立，会导致仍然进入一个满的节点，但是如果采用分裂，那么会有一个节点的关键字数量小于MIN_KEY_NUM
                    shiftToLeft(x, i - 1);
                    if (k <= p.keys[p.n - 1]) {
                        i--;
                    }
                } else if (z != null && z.n < MAX_KEY_NUM) {
                    //todo 这里当z的大小为MAX_KEY_NUM-1时，并且下面的条件成立，会导致仍然进入一个满的节点，但是如果采用分裂，那么会有一个节点的关键字数量小于MIN_KEY_NUM
                    shiftToRight(x, i);
                    if (k > y.keys[y.n - 1]) {
                        i++;
                    }
                } else if (p != null) {
                    splitWithLeft(x, i);
                    if (k > y.keys[y.n - 1]) {
                        i++;
                    }
                } else if (z != null) {
                    splitWithRight(x, i);
                    if (k > y.keys[y.n - 1]) {
                        i++;
                    }
                } else {
                    splitOuter(x, i);
                    if (k > y.keys[y.n - 1]) {
                        i++;
                    }
                }
            }
            insertNotFull(x.children[i], k);
        }
    }

    /**
     * 将一个含有6t个关键字的节点分裂成两个含有3t个关键字的节点
     * 仅根节点以及叶节点能采用此种分裂方法
     *
     * @param x
     * @param i
     */
    private void splitOuter(BStarTreeNode x, int i) {
        BStarTreeNode y = x.children[i];
        BStarTreeNode z = new BStarTreeNode(t);

        for (int j = 0; j < HALF_KEY_NUM; j++) {
            z.keys[j] = y.keys[HALF_KEY_NUM + j];
        }

        if (!y.isLeaf) {
            for (int j = 0; j < HALF_KEY_NUM; j++) {
                z.children[j] = y.children[HALF_KEY_NUM + j];
            }
        } else {
            z.next = y.next;
            y.next = z;
        }
        y.n = z.n = HALF_KEY_NUM;
        z.isLeaf = y.isLeaf;


        for (int j = x.n; j >= i + 2; j--) {
            x.keys[j] = x.keys[j - 1];
            x.children[j] = x.children[j - 1];
        }
        x.keys[i] = y.keys[y.n - 1];
        x.keys[i + 1] = z.keys[z.n - 1];
        x.children[i + 1] = z;
        x.n++;
    }

    /**
     * 将两个满节点(6t个关键字)分裂成3个节点(4t个关键字)
     *
     * @param x
     * @param i
     */
    private void splitWithRight(BStarTreeNode x, int i) {
        BStarTreeNode y = x.children[i];
        BStarTreeNode z = x.children[i + 1];
        BStarTreeNode a = new BStarTreeNode(t);

        for (int j = 0; j < MIN_KEY_NUM; j++) {
            a.keys[j] = z.keys[MIN_KEY_NUM / 2 + j];
        }
        for (int j = MIN_KEY_NUM / 2; j < MIN_KEY_NUM; j++) {
            z.keys[j] = z.keys[j - MIN_KEY_NUM / 2];
        }
        for (int j = 0; j < MIN_KEY_NUM / 2; j++) {
            z.keys[j] = y.keys[MIN_KEY_NUM + j];
        }

        if (!y.isLeaf) {
            for (int j = 0; j < MIN_KEY_NUM; j++) {
                a.children[j] = z.children[MIN_KEY_NUM / 2 + j];
            }
            for (int j = MIN_KEY_NUM / 2; j < MIN_KEY_NUM; j++) {
                z.children[j] = z.children[j - MIN_KEY_NUM / 2];
            }
            for (int j = 0; j < MIN_KEY_NUM / 2; j++) {
                z.children[j] = y.children[MIN_KEY_NUM + j];
            }
        } else {
            a.next = z.next;
            z.next = a;
        }

        y.n = z.n = a.n = MIN_KEY_NUM;
        a.isLeaf = y.isLeaf;


        for (int j = x.n; j >= i + 3; j--) {
            x.keys[j] = x.keys[j - 1];
            x.children[j] = x.children[j - 1];
        }

        x.keys[i] = y.keys[y.n - 1];
        x.keys[i + 1] = z.keys[z.n - 1];
        x.keys[i + 2] = a.keys[a.n - 1];
        x.children[i + 2] = a;
        x.n++;
    }

    private void splitWithLeft(BStarTreeNode x, int i) {

        BStarTreeNode p = x.children[i - 1];
        BStarTreeNode y = x.children[i];
        BStarTreeNode a = new BStarTreeNode(t);

        for (int j = 0; j < MIN_KEY_NUM; j++) {
            a.keys[j] = y.keys[MIN_KEY_NUM / 2 + j];
        }
        for (int j = MIN_KEY_NUM / 2; j < MIN_KEY_NUM; j++) {
            y.keys[j] = y.keys[j - MIN_KEY_NUM / 2];
        }
        for (int j = 0; j < MIN_KEY_NUM / 2; j++) {
            y.keys[j] = p.keys[MIN_KEY_NUM + j];
        }

        if (!p.isLeaf) {
            for (int j = 0; j < MIN_KEY_NUM; j++) {
                a.children[j] = y.children[MIN_KEY_NUM / 2 + j];
            }
            for (int j = MIN_KEY_NUM / 2; j < MIN_KEY_NUM; j++) {
                y.children[j] = y.children[j - MIN_KEY_NUM / 2];
            }
            for (int j = 0; j < MIN_KEY_NUM / 2; j++) {
                y.children[j] = p.children[MIN_KEY_NUM + j];
            }
        } else {
            a.next = y.next;
            y.next = a;
        }

        p.n = y.n = a.n = MIN_KEY_NUM;
        a.isLeaf = p.isLeaf;


        for (int j = x.n; j >= i + 2; j--) {
            x.keys[j] = x.keys[j - 1];
            x.children[j] = x.children[j - 1];
        }

        //todo 下面这句非常重要，x.key[i]可能是经过维护的，即等于插入时的k值
        x.keys[i + 1] = x.keys[i];
        x.keys[i - 1] = p.keys[p.n - 1];
        x.keys[i] = y.keys[y.n - 1];
        x.children[i + 1] = a;
        x.n++;
    }


    private void shiftToLeft(BStarTreeNode x, int i) {
        BStarTreeNode p = x.children[i];
        BStarTreeNode y = x.children[i + 1];

        p.keys[p.n] = y.keys[0];
        for (int j = 0; j < y.n - 1; j++) {
            y.keys[j] = y.keys[j + 1];
        }
        if (!y.isLeaf) {
            p.children[p.n] = y.children[0];
            for (int j = 0; j < y.n - 1; j++) {
                y.children[j] = y.children[j + 1];
            }
        }
        p.n++;
        y.n--;

        x.keys[i] = p.keys[p.n - 1];
    }

    private void shiftToRight(BStarTreeNode x, int i) {
        BStarTreeNode y = x.children[i];
        BStarTreeNode z = x.children[i + 1];

        for (int j = z.n; j >= 1; j--) {
            z.keys[j] = z.keys[j - 1];
        }
        z.keys[0] = y.keys[y.n - 1];
        if (!y.isLeaf) {
            for (int j = z.n; j >= 1; j--) {
                z.children[j] = z.children[j - 1];
            }
            z.children[0] = y.children[y.n - 1];
        }
        y.n--;
        z.n++;

        x.keys[i] = y.keys[y.n - 1];
    }

    private boolean check() {
        return checkIndex(root)
                && checkN(root)
                && checkOrder();
    }

    private boolean checkIndex(BStarTreeNode x) {
        if (x == null) return true;
        for (int i = 1; i < x.n; i++) {
            if (x.keys[i] <= x.keys[i - 1]) {
                return false;
            }
        }
        if (!x.isLeaf) {
            for (int i = 0; i < x.n; i++) {
                BStarTreeNode child = x.children[i];
                if (x.keys[i] < child.keys[child.n - 1]) return false;
                if (i > 0 && child.keys[0] <= x.keys[i - 1]) return false;
                if (!checkIndex(child)) return false;
            }
        }
        return true;
    }

    private boolean checkN(BStarTreeNode x) {
        if (x.isLeaf) {
            return (x == root) || (x.n >= HALF_KEY_NUM && x.n <= MAX_KEY_NUM);
        } else {
            boolean flag = (x == root) || (x.n >= MIN_KEY_NUM && x.n <= MAX_KEY_NUM);
            for (int i = 0; i < x.n; i++) {
                flag = flag && checkN(x.children[i]);
            }
            return flag;
        }
    }

    private boolean checkOrder() {
        BStarTreeNode x = data;
        Integer pre = null;
        int i = 0;
        while (x != null && x.n > 0) {
            if (pre == null) {
                pre = x.keys[i++];
            } else {
                if (pre >= x.keys[i]) return false;
                pre = x.keys[i++];
            }
            if (i == x.n) {
                x = x.next;
                i = 0;
            }
        }
        return true;
    }

    public void levelOrderTraverse() {
        Queue<BStarTreeNode> queue = new LinkedList<BStarTreeNode>();
        queue.offer(root);
        while (!queue.isEmpty()) {
            int len = queue.size();
            while (len-- > 0) {
                BStarTreeNode peek = queue.poll();
                System.out.print(peek + ", ");
                if (!peek.isLeaf) {
                    for (int i = 0; i < peek.n; i++) {
                        queue.offer(peek.children[i]);
                    }
                }
            }
            System.out.println();
        }
    }
}


class TestBStarTree {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        Random random = new Random(0);

        int TIMES = 2;

        while (--TIMES > 0) {
            System.out.println("剩余测试次数: " + TIMES);
            BStarTree bStarTree = new BStarTree(1);

            int N = 10000;

            Set<Integer> set = new HashSet<Integer>();
            for (int i = 0; i < N; i++) {
                //set.add(random.nextInt());
                set.add(i);
            }

            List<Integer> list = new ArrayList<Integer>(set);
            Collections.shuffle(list, random);
            //插入N个数据
            int cnt = 0;
            for (int i : list) {
                System.out.println("(" + cnt++ + ") insert :" + i);
                bStarTree.insert(i);
                bStarTree.levelOrderTraverse();
            }

            int M = list.size() / 2;

//            //删除M个数据
//            Collections.shuffle(list, random);
//
//            for (int i = 0; i < M; i++) {
//                set.remove(list.get(i));
//                bStarTree.delete(list.get(i));
//            }
//
//            //再插入M个数据
//            for (int i = 0; i < M; i++) {
//                int k = random.nextInt();
//                if (set.add(k)) {
//                    bStarTree.insert(k);
//                }
//            }
//            list.clear();
//            list.addAll(set);
//            Collections.shuffle(list, random);
//
//            //再删除所有元素
//            for (int i : list) {
//                bStarTree.delete(i);
//            }
        }
        long end = System.currentTimeMillis();
        System.out.println("Run time: " + (end - start) / 1000 + "s");
    }
}
