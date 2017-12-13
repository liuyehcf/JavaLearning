package org.liuyehcf.graph;

import java.util.Arrays;
import java.util.Scanner;

/**
 * Created by t-chehe on 8/5/2017.
 */
public class FloydWarshall {
    private static final int CANNOT_REACH = Integer.MAX_VALUE >> 2;

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

        System.out.println(minPath2(graph, vertexNum, source, dest));
    }

    public static int minPath1(int[][] graph, int vertexNum, int source, int dest) {
        int dp[][][] = new int[vertexNum + 1][vertexNum + 1][vertexNum + 1];

        for (int i = 1; i <= vertexNum; i++) {
            dp[0][i] = graph[i].clone();
        }

        for (int k = 1; k <= vertexNum; k++) {
            for (int i = 1; i <= vertexNum; i++) {
                for (int j = 1; j <= vertexNum; j++) {

                    dp[k][i][j] = Math.min(dp[k - 1][i][j], dp[k - 1][i][k] + dp[k - 1][k][j]);
                }
            }
        }

        return dp[vertexNum][source][dest];
    }


    public static int minPath2(int[][] graph, int vertexNum, int source, int dest) {
        int dp[][] = new int[vertexNum + 1][vertexNum + 1];

        for (int i = 1; i <= vertexNum; i++) {
            dp[i] = graph[i].clone();
        }

        // 表示DP过程的迭代k必须置于最外层
        for (int k = 1; k <= vertexNum; k++) {
            // 必须逆序
            for (int i = vertexNum; i >= 1; i--) {
                for (int j = vertexNum; j >= 1; j--) {

                    dp[i][j] = Math.min(dp[i][j], dp[i][k] + dp[k][j]);

                }
            }
        }

        return dp[source][dest];
    }
}
