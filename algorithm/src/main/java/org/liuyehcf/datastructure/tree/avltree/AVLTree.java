package org.liuyehcf.datastructure.tree.avltree;

import java.util.*;

/**
 * Created by liuye on 2017/4/24 0024.
 */


public class AVLTree {
    private AVLTreeNode root;

    private AVLTreeNode nil;
    private Map<AVLTreeNode, Integer> highMap;


    public AVLTree() {
        nil = new AVLTreeNode(0);
        nil.left = nil;
        nil.right = nil;
        nil.parent = nil;
        root = nil;
    }

    public void insert(int val) {
        AVLTreeNode x = root;
        AVLTreeNode y = nil;
        AVLTreeNode z = new AVLTreeNode(val);
        while (x != nil) {
            y = x;
            if (val < x.val) {
                x = x.left;
            } else {
                x = x.right;
            }
        }
        z.parent = y;
        if (y == nil) {
            root = z;
        } else if (z.val < y.val) {
            y.left = z;
        } else {
            y.right = z;
        }
        z.left = nil;
        z.right = nil;
        balanceFix(z);
        if (!check())
            throw new RuntimeException();
    }

    private void balanceFix(AVLTreeNode z) {
        //当前节点的初始高度
        int originHigh = z.h;

        updateHigh(z);

        //经过调整后的子树根节点(调整之前子树根节点为z)
        AVLTreeNode r = z;

        if (z.left.h == z.right.h + 2) {
            //todo 这里的等号非常重要(插入过程时不可能取等号，删除过程可能取等号)
            if (z.left.left.h >= z.left.right.h) {
                r = rightRotate(z);
            } else if (z.left.left.h < z.left.right.h) {
                leftRotate(z.left);
                r = rightRotate(z);
            }

        } else if (z.right.h == z.left.h + 2) {
            //todo 这里的等号非常重要(插入过程时不可能取等号，删除过程可能取等号)
            if (z.right.right.h >= z.right.left.h) {
                r = leftRotate(z);
            } else if (z.right.right.h < z.right.left.h) {
                rightRotate(z.right);
                r = leftRotate(z);
            }
        }

        //递归其父节点
        if (r.h != originHigh && r != root)
            balanceFix(r.parent);
    }

    private void updateHigh(AVLTreeNode z) {
        z.h = Math.max(z.left.h, z.right.h) + 1;
    }

    /**
     * 左旋
     *
     * @param x
     * @return 返回旋转后的根节点
     */
    private AVLTreeNode leftRotate(AVLTreeNode x) {
        AVLTreeNode y = x.right;
        x.right = y.left;
        if (y.left != nil) {
            y.left.parent = x;
        }
        y.parent = x.parent;
        if (x.parent == nil) {
            root = y;
        } else if (x == x.parent.left) {
            x.parent.left = y;
        } else {
            x.parent.right = y;
        }
        y.left = x;
        x.parent = y;

        //更新高度
        updateHigh(x);
        updateHigh(y);
        return y;
    }

    /**
     * 右旋
     *
     * @param y
     * @return 返回旋转后的根节点
     */
    private AVLTreeNode rightRotate(AVLTreeNode y) {
        AVLTreeNode x = y.left;
        y.left = x.right;
        if (x.right != nil) {
            x.right.parent = y;
        }
        x.parent = y.parent;
        if (y.parent == nil) {
            root = x;
        } else if (y == y.parent.left) {
            y.parent.left = x;
        } else {
            y.parent.right = x;
        }
        x.right = y;
        y.parent = x;

        //更新高度
        updateHigh(y);
        updateHigh(x);
        return x;
    }

    private boolean check() {
        highMap = new HashMap<AVLTreeNode, Integer>();
        return checkHigh(root) && checkBalance(root);
    }

    private boolean checkHigh(AVLTreeNode root) {
        if (root == nil) return true;
        return checkHigh(root.left) && checkHigh(root.right) && root.h == high(root);
    }

    private int high(AVLTreeNode root) {
        if (root == nil) {
            return 0;
        }
        if (highMap.containsKey(root)) return highMap.get(root);
        int leftHigh = high(root.left);
        int rightHigh = high(root.right);
        highMap.put(root, Math.max(leftHigh, rightHigh) + 1);
        return highMap.get(root);
    }

    private boolean checkBalance(AVLTreeNode root) {
        if (root == nil) {
            return true;
        }
        int leftHigh = root.left.h;
        int rightHigh = root.right.h;
        if (Math.abs(leftHigh - rightHigh) == 2) return false;
        return checkBalance(root.left) && checkBalance(root.right);
    }

    public boolean search(int val) {
        return search(root, val) != nil;
    }

    private AVLTreeNode search(AVLTreeNode x, int val) {
        while (x != nil) {
            if (x.val == val) return x;
            else if (val < x.val) {
                x = x.left;
            } else {
                x = x.right;
            }
        }
        return nil;
    }


    public void delete(int val) {
        AVLTreeNode z = search(root, val);
        if (z == nil) {
            throw new RuntimeException();
        }
        //y代表真正被删除的节点
        AVLTreeNode y = z;
        //x为被删除节点的父节点，如果平衡被破坏，从该节点开始
        AVLTreeNode p = y.parent;
        if (z.left == nil) {
            transplant(z, z.right);
        } else if (z.right == nil) {
            transplant(z, z.left);
        } else {
            y = min(z.right);
            //todo 这里的分类讨论非常重要,否则将会定位到错误的父节点
            if (y == z.right) {
                p = y;
            } else {
                p = y.parent;
            }
            transplant(y, y.right);

            //todo 下面六句可以用z.val=y.val来代替,效果一样
            y.right = z.right;
            y.right.parent = y;

            y.left = z.left;
            y.left.parent = y;

            transplant(z, y);
            y.h = z.h;//todo 这里高度必须维护
            //todo 这里不需要更新p的高度,因为p的子树的高度此时并不知道是否正确,因此更新也没有意义,这也是deleteFixBalance必须遍历到root的原因
        }
        if (p != nil)
            balanceFix(p);
        if (!check())
            throw new RuntimeException();
    }

    private void transplant(AVLTreeNode u, AVLTreeNode v) {
        v.parent = u.parent;
        if (u.parent == nil) {
            root = v;
        } else if (u == u.parent.left) {
            u.parent.left = v;
        } else {
            u.parent.right = v;
        }
    }


    private AVLTreeNode min(AVLTreeNode x) {
        while (x.left != nil) {
            x = x.left;
        }
        return x;
    }


    public void inOrderTraverse() {
        inOrderTraverse(root);
        System.out.println();
    }

    public void levelOrderTraversal() {
        Queue<AVLTreeNode> queue = new LinkedList<AVLTreeNode>();
        queue.offer(root);
        while (!queue.isEmpty()) {
            int len = queue.size();
            for (int i = 0; i < len; i++) {
                AVLTreeNode peek = queue.poll();
                System.out.print("[" + peek.val + "," + peek.h + "], ");
                if (peek.left != nil) queue.offer(peek.left);
                if (peek.right != nil) queue.offer(peek.right);
            }
        }
        System.out.println();
    }

    private void inOrderTraverse(AVLTreeNode root) {
        if (root != nil) {
            inOrderTraverse(root.left);
            System.out.print(root.val + ", ");
            inOrderTraverse(root.right);
        }
    }
}


class TestAVLTree {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        Random random = new Random();

        int TIMES = 10;

        while (--TIMES > 0) {
            System.out.println("剩余测试次数: " + TIMES);
            AVLTree avlTree = new AVLTree();

            int N = 10000;
            int M = N / 2;

            List<Integer> list = new ArrayList<Integer>();
            for (int i = 0; i < N; i++) {
                list.add(random.nextInt());
            }

            Collections.shuffle(list, random);
            //插入N个数据
            for (int i : list) {
                avlTree.insert(i);
            }

            //删除M个数据
            Collections.shuffle(list, random);

            for (int i = 0; i < M; i++) {
                int k = list.get(list.size() - 1);
                list.remove(list.size() - 1);
                avlTree.delete(k);
            }

            //再插入M个数据
            for (int i = 0; i < M; i++) {
                int k = random.nextInt();
                list.add(k);
                avlTree.insert(k);
            }
            Collections.shuffle(list, random);

            //再删除所有元素
            for (int i : list) {
                avlTree.delete(i);
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("Run time: " + (end - start) / 1000 + "s");
    }
}
