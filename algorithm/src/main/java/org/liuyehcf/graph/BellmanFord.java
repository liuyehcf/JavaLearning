package org.liuyehcf.graph;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by HCF on 2017/8/6.
 */
public class BellmanFord {
    private static final int CANNOT_REACH = Integer.MAX_VALUE >> 2;

    private static class Edge {
        final int source;

        final int dest;

        int length;

        public Edge(int source, int dest, int length) {
            this.source = source;
            this.dest = dest;
            this.length = length;
        }
    }

    public static void main(String[] args) {
        // edgeNum并不代表真正有效边的数量，因为相同的源节点和目的节点之间可能有多条道路，我们只需要保留最小的即可
        int vertexNum, edgeNum, source, dest;

        Scanner scanner = new Scanner(System.in);

        vertexNum = scanner.nextInt();
        edgeNum = scanner.nextInt();
        source = scanner.nextInt();
        dest = scanner.nextInt();

        int[][] graph = new int[vertexNum + 1][vertexNum + 1];

        for (int i = 1; i <= vertexNum; i++) {
            Arrays.fill(graph[i], CANNOT_REACH);
            graph[i][i] = 0;
        }

        while (--edgeNum >= 0) {
            int s = scanner.nextInt();
            int d = scanner.nextInt();
            int l = scanner.nextInt();

            // 题意中的道路是双向都可通的，因此对于有向图来说两个方向都需要存
            if (l < graph[s][d]) {
                graph[s][d] = l;
                graph[d][s] = l;
            }
        }

        List<Edge> edges = new LinkedList<Edge>();

        for (int i = 1; i <= vertexNum; i++) {
            for (int j = 1; j <= vertexNum; j++) {
                if (graph[i][j] < CANNOT_REACH) {
                    edges.add(new Edge(i, j, graph[i][j]));
                }
            }
        }

        System.out.println(minPath(edges, vertexNum, source, dest));
    }

    public static int minPath(List<Edge> edges, int num, int source, int dest) {
        int[] dp = new int[num + 1];

        Arrays.fill(dp, CANNOT_REACH);

        dp[source] = 0;

        for (int k = 1; k < num; k++) {
            for (Edge e : edges) {
                if (e == null) continue;
                dp[e.dest] = Math.min(dp[e.dest], dp[e.source] + e.length);
            }
        }

        return dp[dest];
    }
}
