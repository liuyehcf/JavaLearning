package org.liuyehcf.saima.toutiao;

import java.util.*;

/**
 * Created by HCF on 2017/4/18.
 */
public class Item4 {
    private static int max;

    private static int cur;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int k = scanner.nextInt();
        int[][] jobs = new int[n][n];


        for (int i = 0; i < n; i++) {
            for (int j = 0; j <= i; j++) {
                jobs[i][j] = scanner.nextInt();
            }
        }

        max = 0;
        cur = 0;
        Set<String> set = new HashSet<String>();
        if (k <= 0) {
            System.out.println(max);
            return;
        }

        helper(jobs, n, k, n - 1, n - 1, set);
        System.out.println(max);

    }

    private static void helper(int[][] jobs, int n, int k, int row, int col, Set<String> set) {

        int nextRow = row;
        int nextCol = col - 1;
        if (nextCol == 0) {
            nextRow--;
            nextCol = nextRow;
        }
        //不做jobs[row][col]这个job
        if (nextRow >= 0 && nextCol >= 0) {
            max = Math.max(max, cur);
            helper(jobs, n, k, nextRow, nextCol, set);
        } else {
            max = Math.max(max, cur);
        }

        //做jobs[row][col]这个job
        set.add(row + "," + col);
        cur += jobs[row][col];
        Set<String> temp = new HashSet<String>();
        for (int i = row + 1; i < n; i++) {
            for (int j = col; j <= col + (i - row); j++) {
                if (set.add(i + "," + j)) {
                    cur += jobs[i][j];
                    temp.add(i + "," + j);
                }
            }
        }
        if (nextRow >= 0 && nextCol >= 0 && set.size() <= k) {
            max = Math.max(max, cur);
            helper(jobs, n, k, nextRow, nextCol, set);
        }

        cur -= jobs[row][col];
        for (String s : temp) {
            String[] strs = s.split(",");
            int i = strs[0].charAt(0) - '0';
            int j = strs[1].charAt(0) - '0';
            cur -= jobs[i][j];
            set.remove(s);
        }

        max = Math.max(max, cur);

    }
}


/**
 * C
 */

//#include <algorithm>
//#include <cassert>
//#include <cstring>
//#include <cstdio>
//
//const int N = 60;
//        const int M = 500 + 10;
//
//        int dp[N][N][M], sum[N][N], a[N][N], n, m;
//
//        int main() {
//        assert(scanf("%d%d", &n, &m) == 2);
//        assert(1 <= n && n <= 50);
//        assert(1 <= m && m <= 500);
//        for (int i = 1; i <= n; ++ i) {
//        for (int j = 1; j <= i; ++ j) {
//        assert(scanf("%d", &a[i][j]) == 1);
//        assert(0 <= a[i][j] && a[i][j] <= 1000);
//        }
//        }
//
//        for (int i = 1; i <= n; ++ i) {
//        for (int j = 1; j <= i; ++ j) {
//        sum[i][j] = sum[i][j - 1] + a[n - j + 1][i - j + 1];
//        }
//        }
//
//        memset(dp, 200, sizeof(dp));
//        for (int i = 0; i <= n; ++ i) {
//        dp[i][0][0] = 0;
//        }
//        for (int i = 1; i <= n; ++ i) {
//        for (int j = i; j >= 0; -- j) {
//        for (int k = j; k <= m; ++ k) {
//        dp[i][j][k] = std::max(dp[i][j + 1][k],
//        dp[i - 1][std::max(0, j - 1)][k - j] + sum[i][j]);
//        }
//        }
//        }
//        printf("%d\n", dp[n][0][m]);
//        return 0;
//        }
