package org.liuyehcf.algorithm.datastructure.tree.avltree;

import java.util.*;

/**
 * Created by Liuye on 2017/4/27.
 */
enum RotateOrientation {
    INVALID,
    LEFT,
    RIGHT
}

public class AVLTree2 {
    private AVLTreeNode root;

    private AVLTreeNode nil;
    private Map<AVLTreeNode, Integer> highMap;


    public AVLTree2() {
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
        fixUp(z);
        if (!check())
            throw new RuntimeException();
    }

    private void fixUp(AVLTreeNode y) {
        if (y == nil) {
            y = y.parent;
        }
        while (y != nil) {
            updateHigh(y);
            if (y.left.h == y.right.h + 2)
                y = holdRotate(y, RotateOrientation.RIGHT);
            else if (y.right.h == y.left.h + 2)
                y = holdRotate(y, RotateOrientation.LEFT);
            y = y.parent;
        }
    }

    private AVLTreeNode holdRotate(AVLTreeNode x, RotateOrientation orientation) {
        LinkedList<AVLTreeNode> stack1 = new LinkedList<AVLTreeNode>();
        LinkedList<RotateOrientation> stack2 = new LinkedList<RotateOrientation>();
        stack1.push(x);
        stack2.push(orientation);
        AVLTreeNode cur = nil;
        AVLTreeNode rotateRoot = nil;
        RotateOrientation curOrientation = RotateOrientation.INVALID;
        while (!stack1.isEmpty()) {
            cur = stack1.peek();
            curOrientation = stack2.peek();
            if (curOrientation == RotateOrientation.LEFT) {
                if (cur.right.right.h >= cur.right.left.h) {
                    stack1.pop();
                    stack2.pop();
                    rotateRoot = leftRotate(cur);
                } else {
                    stack1.push(cur.right);
                    stack2.push(RotateOrientation.RIGHT);
                }
            } else if (curOrientation == RotateOrientation.RIGHT) {
                if (cur.left.left.h >= cur.left.right.h) {
                    stack1.pop();
                    stack2.pop();
                    rotateRoot = rightRotate(cur);
                } else {
                    stack1.push(cur.left);
                    stack2.push(RotateOrientation.LEFT);
                }
            }
        }
        return rotateRoot;
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
        AVLTreeNode y = z;
        AVLTreeNode x = nil;
        if (z.left == nil) {
            x = y.right;
            transplant(z, z.right);
        } else if (z.right == nil) {
            x = y.left;
            transplant(z, z.left);
        } else {
            y = min(z.right);
            x = y.right;
            if (y.parent == z) {
                x.parent = y;
            } else {
                transplant(y, y.right);
                y.right = z.right;
                y.right.parent = y;
            }
            transplant(z, y);

            y.left = z.left;
            y.left.parent = y;

            //todo 这里不需要更新p的高度,因为p的子树的高度此时并不知道是否正确,因此更新也没有意义,这也是deleteFixBalance必须遍历到root的原因
        }
        fixUp(x);
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


class TestAVLTree2 {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        Random random = new Random();

        int TIMES = 10;

        while (--TIMES > 0) {
            System.out.println("剩余测试次数: " + TIMES);
            AVLTree2 avlTree2 = new AVLTree2();

            int N = 1000;
            int M = N / 2;

            Set<Integer> set = new HashSet<Integer>();
            for (int i = 0; i < N; i++) {
                set.add(random.nextInt());
            }

            List<Integer> list = new ArrayList<Integer>(set);
            Collections.shuffle(list, random);
            //插入N个数据
            for (int i : list) {
                avlTree2.insert(i);
            }

            //删除M个数据
            Collections.shuffle(list, random);

            for (int i = 0; i < M; i++) {
                set.remove(list.get(i));
                avlTree2.delete(list.get(i));
            }

            //再插入M个数据
            for (int i = 0; i < M; i++) {
                int k = random.nextInt();
                set.add(k);
                avlTree2.insert(k);
            }
            list.clear();
            list.addAll(set);
            Collections.shuffle(list, random);

            //再删除所有元素
            for (int i : list) {
                avlTree2.delete(i);
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("Run time: " + (end - start) / 1000 + "s");
    }
}
