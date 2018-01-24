package org.liuyehcf.hihocoder.practice.practice13;

import java.util.Scanner;

/**
 * Created by liuye on 2017/4/9 0009.
 */
public class Item2 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int N = scanner.nextInt();
        int M = scanner.nextInt();
        int K = scanner.nextInt();

        int[][] A = new int[N][M];
        for (int row = 0; row < N; row++) {
            for (int col = 0; col < M; col++) {
                A[row][col] = scanner.nextInt();
            }
        }

        int[][] dpNum = new int[N + 1][M + 1];
        int[][] dpRow = new int[N + 1][M + 1];
        int[][] dpCol = new int[N + 1][M + 1];
        int[][] dpSum = new int[N + 1][M + 1];

        int res = -1;

        for (int row = 1; row <= N; row++) {
            for (int col = 1; col <= M; col++) {
                dpNum[row][col] = -1;
                int temp1 = dpSum[row - 1][col];
                for (int i = col - dpCol[row - 1][col] + 1; i <= col; i++) {
                    temp1 += A[row - 1][i - 1];
                }
                if (temp1 <= K) {
                    if (dpNum[row - 1][col] + dpCol[row - 1][col] > dpNum[row][col]) {
                        dpNum[row][col] = dpNum[row - 1][col] + dpCol[row - 1][col];
                        dpRow[row][col] = dpRow[row - 1][col] + 1;
                        dpCol[row][col] = dpCol[row - 1][col];
                        dpSum[row][col] = temp1;
                    }
                }

                int temp2 = dpSum[row][col - 1];
                for (int i = row - dpRow[row][col - 1] + 1; i <= row; i++) {
                    temp2 += A[i - 1][col - 1];
                }
                if (temp2 <= K) {
                    if (dpNum[row][col - 1] + dpRow[row][col - 1] > dpNum[row][col]) {
                        dpNum[row][col] = dpNum[row][col - 1] + dpRow[row][col - 1];
                        dpRow[row][col] = dpRow[row][col - 1];
                        dpCol[row][col] = dpCol[row][col - 1] + 1;
                        dpSum[row][col] = temp2;
                    }
                }

                int temp3 = dpSum[row - 1][col - 1];
                temp3 += A[row - 1][col - 1];
                for (int i = row - 1 - dpRow[row - 1][col - 1] + 1; i <= row - 1; i++) {
                    temp3 += A[i - 1][col - 1];
                }
                for (int i = col - 1 - dpCol[row - 1][col - 1] + 1; i <= col - 1; i++) {
                    temp3 += A[row - 1][i - 1];
                }
                if (temp3 <= K) {
                    if (dpNum[row - 1][col - 1] + dpRow[row - 1][col - 1] + dpCol[row - 1][col - 1] + 1 > dpNum[row][col]) {
                        dpNum[row][col] = dpNum[row - 1][col - 1] + dpRow[row - 1][col - 1] + dpCol[row - 1][col - 1] + 1;
                        dpRow[row][col] = dpRow[row - 1][col - 1] + 1;
                        dpCol[row][col] = dpCol[row - 1][col - 1] + 1;
                        dpSum[row][col] = temp3;
                    }
                }

                int temp4 = A[row - 1][col - 1];
                if (temp4 <= K) {
                    if (1 > dpNum[row][col]) {
                        dpNum[row][col] = 1;
                        dpRow[row][col] = 1;
                        dpCol[row][col] = 1;
                        dpSum[row][col] = temp4;
                    }
                }

                res = Math.max(res, dpNum[row][col]);
            }
        }
        System.out.println(res);
    }
}


class Item2_1 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int N = scanner.nextInt();
        int M = scanner.nextInt();
        int K = scanner.nextInt();

        int[][] A = new int[N][M];
        for (int row = 0; row < N; row++) {
            for (int col = 0; col < M; col++) {
                A[row][col] = scanner.nextInt();
            }
        }

        int[][] sum = new int[N + 1][M + 1];
        int res = -1;
        for (int row = 1; row <= N; row++) {
            for (int col = 1; col <= M; col++) {
                sum[row][col] = sum[row - 1][col] + sum[row][col - 1] - sum[row - 1][col - 1] + A[row - 1][col - 1];

                for (int i = 1; i <= row; i++) {
                    for (int j = 1; j <= col; j++) {
                        int temp = sum[row][col] - sum[row][j - 1] - sum[i - 1][col] + sum[i - 1][j - 1];
                        if (temp <= K) {
                            res = Math.max(res, (row - i + 1) * (col - j + 1));
                        }
                    }
                }
            }
        }

        System.out.println(res);
    }
}
