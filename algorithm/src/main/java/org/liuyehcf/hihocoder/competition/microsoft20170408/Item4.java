package org.liuyehcf.hihocoder.competition.microsoft20170408;

import java.util.*;

/**
 * Created by liuye on 2017/4/9 0009.
 */
public class Item4 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int N = scanner.nextInt();
        List<TreeNode> list = new ArrayList<TreeNode>();
        for (int i = 0; i < N; i++) {
            list.add(new TreeNode(
                    scanner.nextInt(),
                    scanner.nextInt(),
                    scanner.nextInt(),
                    scanner.nextInt()
            ));
        }

        //建立一棵树
        TreeNode root = null;
        for (TreeNode node : list) {
            if (node.F == 0) {
                root = node;
            } else {
                TreeNode father = list.get(node.F - 1);
                father.children.add(node);
                node.parent = father;
            }
        }

        Map<TreeNode, Long> map = new HashMap<TreeNode, Long>();

        long res = INCostX86(root, map);
        if (res >= Integer.MAX_VALUE) {
            System.out.println(-1);
        } else {
            System.out.println(res + root.C);
        }
    }

    private static long INCost(TreeNode root, Map<TreeNode, Long> map) {
        if (map.containsKey(root)) return map.get(root);
        long[][] dp = new long[root.children.size() + 1][root.IN + 1];

        for (int i = 0; i < dp.length; i++) {
            Arrays.fill(dp[i], Integer.MAX_VALUE);
            dp[i][0] = 0;//获取0点信息需要的花费
        }


        for (int i = 1; i <= root.children.size(); i++) {
            TreeNode node = root.children.get(i - 1);
            long cost = INCost(node, map);
            for (int j = 1; j <= root.IN; j++) {
                dp[i][j] = Math.min(dp[i - 1][j], cost + node.C + dp[i - 1][Math.max(0, j - node.IP)]);
            }
        }

        map.put(root, dp[root.children.size()][root.IN]);
        return map.get(root);
    }

    /**
     * 为什么要这样
     *
     * @param root
     * @param map
     * @return
     */
    private static long INCostX86(TreeNode root, Map<TreeNode, Long> map) {
        if (map.containsKey(root)) return map.get(root);
        long[] dp = new long[root.IN + 1];
        Arrays.fill(dp, Integer.MAX_VALUE);
        dp[0] = 0;//获取0点信息需要的花费

        for (TreeNode child : root.children) {
            for (int j = root.IN; j > 0; j--) {
                long cost = INCostX86(child, map);
                dp[j] = Math.min(dp[Math.max(0, j - child.IP)] + cost + child.C, dp[j]);
            }
        }
        map.put(root, dp[root.IN]);
        return map.get(root);
    }

    private static class TreeNode {
        int F;//父亲节点
        int IN;//刺杀该节点需要的信息量
        int IP;//刺杀该节点能获取的信息量
        int C;//刺杀该节点的消费(最终是要最小化这个,而信息量只是一个限制条件)
        List<TreeNode> children;
        TreeNode parent;

        public TreeNode(int F, int IN, int IP, int C) {
            this.F = F;
            this.IN = IN;
            this.IP = IP;
            this.C = C;
            children = new ArrayList<TreeNode>();
            parent = null;
        }
    }

}
