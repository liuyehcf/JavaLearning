package org.liuyehcf.algorithm.mianshi.alibaba;

import java.util.Scanner;

/**
 * Created by HCF on 2017/4/26.
 */
public class Item1 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String s = scanner.nextLine();
        String p = scanner.nextLine();

        boolean[][] dp = new boolean[s.length() + 1][p.length() + 1];

        dp[0][0] = true;

        int i = 0;
        while (i < p.length() && p.charAt(i) == '*') {
            dp[0][i + 1] = true;
            i++;
        }

        for (i = 1; i <= s.length(); i++) {
            for (int j = 1; j <= p.length(); j++) {
                if (p.charAt(j - 1) == '*') {
                    dp[i][j] = dp[i][j - 1] || dp[i - 1][j];
                } else if (p.charAt(j - 1) == '?' || s.charAt(i - 1) == p.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = false;
                }
            }
        }

        System.out.println(dp[s.length()][p.length()] ? "1" : "0");
    }

}


class Item1_2 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String s = scanner.nextLine();
        String p = scanner.nextLine();

        boolean[][] dp = new boolean[s.length() + 1][p.length() + 1];

        dp[0][0] = true;
        for (int i = 1; i <= p.length(); i++) {
            if (get(p, i) != '*') break;
            dp[0][i] = true;
        }

        for (int i = 1; i <= s.length(); i++) {
            for (int j = 1; j <= p.length(); j++) {
                if (get(p, j) == '*') {
                    dp[i][j] = dp[i - 1][j] || dp[i][j - 1];
                } else if (get(p, j) == '?' || get(p, j) == get(s, i)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = false;
                }
            }
        }
        System.out.println(dp[s.length()][p.length()] ? "1" : "0");
    }

    private static char get(String s, int pos) {
        if (pos < 1 || pos > s.length()) return '\0';
        return s.charAt(pos - 1);
    }
}
