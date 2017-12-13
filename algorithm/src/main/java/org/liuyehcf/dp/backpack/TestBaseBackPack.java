package org.liuyehcf.dp.backpack;

import java.util.Random;

/**
 * Created by liuye on 2017/4/10 0010.
 */
public class TestBaseBackPack {
    public static void main(String[] args) {
        Random random = new Random(0);
        for (int time = 0; time < 100; time++) {

            int N = random.nextInt(100) + 1, V = random.nextInt(1000) + 1;
            int W = random.nextInt(50) + 1, C = random.nextInt(50) + 1;
            int[] weights = new int[N];
            int[] values = new int[N];
            for (int i = 0; i < N; i++) {
                weights[i] = random.nextInt(W) + 1;
                values[i] = random.nextInt(C) + 1;
            }

            int res1, res2;
            if ((res1 = maxValue1(weights, values, V)) != (res2 = maxValue2(weights, values, V))) {
                System.err.println(time + ": error { res1: " + res1 + ", res2: " + res2);
            }
        }

    }

    private static int maxValue1(int[] weights, int[] values, int capacity) {
        int[][] dp = new int[weights.length + 1][capacity + 1];
        for (int i = 1; i <= weights.length; i++) {
            for (int v = 1; v <= capacity; v++) {
                if (v < weights[i - 1]) {
                    dp[i][v] = dp[i - 1][v];
                } else {
                    dp[i][v] = Math.max(dp[i - 1][v], dp[i - 1][v - weights[i - 1]] + values[i - 1]);
                }
            }
        }

        return dp[weights.length][capacity];
    }

    private static int maxValue2(int[] weights, int[] values, int capacity) {
        int[] dp = new int[capacity + 1];

        for (int i = 1; i <= weights.length; i++) {
            for (int v = capacity; v >= 1; v--) {
                if (v < weights[i - 1]) break;
                dp[v] = Math.max(dp[v], dp[v - weights[i - 1]] + values[i - 1]);
            }
        }
        return dp[capacity];
    }
}
