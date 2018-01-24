package org.liuyehcf.datastructure.tree.bplustree;

import java.util.*;

/**
 * Created by liuye on 2017/5/3 0003.
 * 该版本的B+树同样不支持重复关键字
 */
public class BPlusTree {
    private int t;

    private BPlusTreeNode root;
    private BPlusTreeNode data;

    public BPlusTree(int t) {
        this.t = t;
        root = new BPlusTreeNode(t);
        root.n = 0;
        root.isLeaf = true;

        data = root;
    }

    public void insert(int k) {
        if (root.n == 2 * t) {
            BPlusTreeNode newRoot = new BPlusTreeNode(t);
            newRoot.n = 1;
            newRoot.keys[0] = root.keys[2 * t - 1];
            newRoot.children[0] = root;
            newRoot.isLeaf = false;
            root = newRoot;
            split(root, 0);
        }
        insertNotFull(root, k);

        if (!check()) {
            throw new RuntimeException();
        }
    }

    private void split(BPlusTreeNode x, int i) {
        BPlusTreeNode y = x.children[i];
        BPlusTreeNode z = new BPlusTreeNode(t);

        y.n = z.n = t;
        for (int j = 0; j < t; j++) {
            z.keys[j] = y.keys[j + t];
        }
        if (!y.isLeaf) {
            for (int j = 0; j < t; j++) {
                z.children[j] = y.children[j + t];
            }
        } else {
            z.next = y.next;
            y.next = z;
        }
        z.isLeaf = y.isLeaf;

        for (int j = x.n; j > i + 1; j--) {
            x.keys[j] = x.keys[j - 1];
            x.children[j] = x.children[j - 1];
        }

        x.keys[i + 1] = x.keys[i];
        x.keys[i] = y.keys[y.n - 1];

        x.children[i + 1] = z;
        x.n++;
    }

    private void insertNotFull(BPlusTreeNode x, int k) {
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
            //todo 这个等号非常关键，执行过删除操作后，遗留下来的元素可能并不存在于叶节点中
            while (i >= 0 && x.keys[i] >= k) {
                i--;
            }
            i++;
            //todo 关键，自上而下寻找插入点时，即维护了索引的正确性
            if (i == x.n) {
                //此时说明新插入的值k比当前节点中所有关键字都要大，因此当前节点的最后一个索引需要改变
                x.keys[x.n - 1] = k;
                i--;
            }

            BPlusTreeNode y = x.children[i];
            if (y.n == 2 * t) {
                split(x, i);
                if (k > y.keys[y.n - 1])
                    i++;
            }
            insertNotFull(x.children[i], k);
        }
    }

    private boolean check() {
        return checkIndex(root)
                && checkN(root)
                && checkOrder();
    }

    private boolean checkIndex(BPlusTreeNode x) {
        if (x == null) return true;
        for (int i = 1; i < x.n; i++) {
            if (x.keys[i] <= x.keys[i - 1]) {
                return false;
            }
        }
        if (!x.isLeaf) {
            for (int i = 0; i < x.n; i++) {
                BPlusTreeNode child = x.children[i];
                if (x.keys[i] < child.keys[child.n - 1]) return false;
                if (i > 0 && child.keys[0] <= x.keys[i - 1]) return false;
                if (!checkIndex(child)) return false;
            }
        }
        return true;
    }

    private boolean checkN(BPlusTreeNode x) {
        if (x.isLeaf) {
            return (x == root) || (x.n >= t && x.n <= 2 * t);
        } else {
            boolean flag = (x == root) || (x.n >= t && x.n <= 2 * t);
            for (int i = 0; i < x.n; i++) {
                flag = flag && checkN(x.children[i]);
            }
            return flag;
        }
    }

    private boolean checkOrder() {
        BPlusTreeNode x = data;
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

    private BPlusTreeNode search(BPlusTreeNode x, int k) {
        while (!x.isLeaf) {
            int i = 0;
            while (i < x.n && k > x.keys[i]) {
                i++;
            }
            x = x.children[i];
        }
        for (int i = 0; i < x.n; i++) {
            if (x.keys[i] == k) return x;
        }
        return null;
    }

    public void delete(int k) {
        if (!root.isLeaf && root.n == 1) {
            root = root.children[0];
        }
        if (root.n == 2) {
            if (!root.isLeaf && root.children[0].n == t && root.children[1].n == t) {
                merge(root, 0);
            }
        }
        deleteNotNone(root, k);
        if (!check()) {
            throw new RuntimeException();
        }
    }

    private void deleteNotNone(BPlusTreeNode x, int k) {
        int i = 0;
        if (x.isLeaf) {
            while (i < x.n && x.keys[i] < k) {
                i++;
            }
            if (k != x.keys[i]) {
                throw new RuntimeException();
            }
            while (i < x.n - 1) {
                x.keys[i] = x.keys[i + 1];
                i++;
            }
            x.n--;
        } else {
            while (i < x.n && x.keys[i] < k) {
                i++;
            }
            BPlusTreeNode y = x.children[i];
            BPlusTreeNode p = null, z = null;
            if (i > 0) {
                p = x.children[i - 1];
            }
            if (i < x.n - 1) {
                z = x.children[i + 1];
            }
            if (y.n == t) {
                if (p != null && p.n > t) {
                    shiftToRight(x, i - 1);
                } else if (z != null && z.n > t) {
                    shiftToLeft(x, i);
                } else if (p != null) {
                    merge(x, i - 1);
                    y = p;
                } else {
                    merge(x, i);
                }
            }
            deleteNotNone(y, k);
        }
    }

    private void merge(BPlusTreeNode x, int i) {
        BPlusTreeNode y = x.children[i];
        BPlusTreeNode z = x.children[i + 1];

        y.n = 2 * t;
        for (int j = 0; j < t; j++) {
            y.keys[j + t] = z.keys[j];
        }
        if (!y.isLeaf) {
            for (int j = 0; j < t; j++) {
                y.children[j + t] = z.children[j];
            }
        } else {
            y.next = z.next;
        }

        for (int j = i + 1; j < x.n - 1; j++) {
            x.keys[j] = x.keys[j + 1];
            x.children[j] = x.children[j + 1];
        }
        x.keys[i] = y.keys[y.n - 1];
        x.n--;
    }

    private void shiftToLeft(BPlusTreeNode x, int i) {
        BPlusTreeNode y = x.children[i];
        BPlusTreeNode z = x.children[i + 1];

        y.keys[y.n] = z.keys[0];
        for (int j = 0; j < z.n - 1; j++) {
            z.keys[j] = z.keys[j + 1];
        }
        if (!y.isLeaf) {
            y.children[y.n] = z.children[0];
            for (int j = 0; j < z.n - 1; j++) {
                z.children[j] = z.children[j + 1];
            }
        }
        y.n++;
        z.n--;

        x.keys[i] = y.keys[y.n - 1];
    }

    private void shiftToRight(BPlusTreeNode x, int i) {
        BPlusTreeNode p = x.children[i];
        BPlusTreeNode y = x.children[i + 1];

        for (int j = y.n; j > 0; j--) {
            y.keys[j] = y.keys[j - 1];
        }
        y.keys[0] = p.keys[p.n - 1];

        if (!y.isLeaf) {
            for (int j = y.n; j > 0; j--) {
                y.children[j] = y.children[j - 1];
            }
            y.children[0] = p.children[p.n - 1];
        }
        y.n++;
        p.n--;

        x.keys[i] = p.keys[p.n - 1];
    }

    public void levelOrderTraverse() {
        Queue<BPlusTreeNode> queue = new LinkedList<BPlusTreeNode>();
        queue.offer(root);
        while (!queue.isEmpty()) {
            int len = queue.size();
            while (len-- > 0) {
                BPlusTreeNode peek = queue.poll();
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

    public List<Integer> getOrderedList() {
        List<Integer> list = new ArrayList<Integer>();
        BPlusTreeNode x = data;
        while (x != null) {
            for (int i = 0; i < x.n; i++) {
                list.add(x.keys[i]);
            }
            x = x.next;
        }
        return list;
    }
}


class TestBPlusTree {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        Random random = new Random();

        int TIMES = 500;

        while (--TIMES > 0) {
            System.out.println("剩余测试次数: " + TIMES);
            BPlusTree bPlusTree = new BPlusTree(random.nextInt(20) + 2);

            int N = 10000;

            Set<Integer> set = new HashSet<Integer>();
            for (int i = 0; i < N; i++) {
                set.add(random.nextInt());
            }

            List<Integer> list = new ArrayList<Integer>(set);
            Collections.shuffle(list, random);
            //插入N个数据
            for (int i : list) {
                bPlusTree.insert(i);
            }

            int M = list.size() / 2;

            //删除M个数据
            Collections.shuffle(list, random);

            for (int i = 0; i < M; i++) {
                set.remove(list.get(i));
                bPlusTree.delete(list.get(i));
            }

            //再插入M个数据
            for (int i = 0; i < M; i++) {
                int k = random.nextInt();
                if (set.add(k)) {
                    bPlusTree.insert(k);
                }
            }
            list.clear();
            list.addAll(set);
            Collections.shuffle(list, random);

            //再删除所有元素
            for (int i : list) {
                bPlusTree.delete(i);
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("Run time: " + (end - start) / 1000 + "s");
    }
}
