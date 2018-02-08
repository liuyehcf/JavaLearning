package org.liuyehcf.algorithm.graph;

import java.util.Arrays;
import java.util.Scanner;

/**
 * Created by t-chehe on 8/5/2017.
 */
public class Dijkstra {
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

        System.out.println(minPath(graph, vertexNum, source, dest));
    }

    public static int minPath(int[][] graph, int vertexNum, int source, int dest) {
        // visited将节点集合分为两部分，true代表已访问（记为集合S），false代表未访问(记为集合U)
        boolean[] visited = new boolean[vertexNum + 1];

        int[] distance = new int[vertexNum + 1];

        for (int i = 1; i <= vertexNum; i++) {
            distance[i] = graph[source][i];
        }

        distance[source] = 0;

        for (int i = 2; i <= vertexNum; i++) {
            int minLength = CANNOT_REACH;
            int nextVertex = -1;

            // 在所有未访问的节点中找到距离source最近的节点
            for (int j = 1; j <= vertexNum; j++) {
                if (!visited[j]
                        && distance[j] < minLength) {
                    nextVertex = j;
                    minLength = distance[j];
                }
            }

            visited[nextVertex] = true;

            // 如果source与j(j在集合U中)可以通过节点nextNode相连，那么更新source与j的距离
            for (int j = 1; j <= vertexNum; j++) {
                if (!visited[j]
                        && distance[nextVertex] + graph[nextVertex][j] < distance[j]) {
                    distance[j] = distance[nextVertex] + graph[nextVertex][j];
                }
            }
        }

        return distance[dest];
    }
}
