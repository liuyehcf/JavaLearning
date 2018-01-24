package org.liuyehcf.hihocoder.competition.microsoft20170408;

import java.util.Scanner;

/**
 * WA
 * 经过验证,那两个函数有问题ATakeFromRight/BTakeFromRight
 * 有问题的例子 3 1 2 2 1 15 15
 * <p>
 * Created by liuye on 2017/4/9 0009.
 */
public class Item3 {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int N = scanner.nextInt();
        long[] A = new long[N];
        long[] B = new long[N];
        long total = 0;
        for (int i = 0; i < N; i++) {
            A[i] = scanner.nextLong();
            B[i] = scanner.nextLong();
            total += A[i];
            total += B[i];
        }

        long average = total / 2 / N;

        long res = 0;
        for (int i = 0; i < N; i++) {
            if (A[i] >= average && B[i] >= average) {
                long overA = A[i] - average;
                long overB = B[i] - average;
                if (A[i] > average) A[i + 1] += overA;
                if (B[i] > average) B[i + 1] += overB;
                A[i] = average;
                B[i] = average;
                res += overA + overB;
            } else if (A[i] >= average) {
                long overA = A[i] - average;
                long lackB = average - B[i];
                if (lackB <= overA) {
                    A[i] = average;
                    B[i] = average;
                    if (lackB < overA) A[i + 1] += overA - lackB;
                    res += overA;
                } else {
                    A[i] = average;
                    B[i] += overA;
                    res += overA;
                    res += BTakeFromRight(A, B, i, lackB - overA);
//                    A[i] = average;
//                    B[i] = average;
//                    B[i+1]-=lackB-overA;
//                    res+=lackB;
                }
            } else if (B[i] >= average) {
                long overB = B[i] - average;
                long lackA = average - A[i];
                if (lackA <= overB) {
                    B[i] = average;
                    A[i] = average;
                    if (lackA < overB) B[i + 1] += overB - lackA;
                    res += overB;
                } else {
                    B[i] = average;
                    A[i] += overB;
                    res += overB;
                    res += ATakeFromRight(A, B, i, lackA - overB);
//                    A[i] = average;
//                    B[i] = average;
//                    A[i+1]-=lackA-overB;
//                    res+=lackA;
                }
            } else {
                long lackA = average - A[i];
                long lackB = average - B[i];
                long temp1 = ATakeFromRight(A, B, i, lackA);
                long temp2 = BTakeFromRight(A, B, i, lackB);
                res += temp1;
                res += temp2;
//                long lackA = average - A[i];
//                long lackB = average - B[i];
//                A[i+1]-=lackA;
//                B[i+1]-=lackB;
//                res+=lackA+lackB;
            }
        }

        System.out.println(res);
    }

    private static long ATakeFromRight(long[] A, long[] B, int pos, long lack) {
        int horizonOver = 1;
        long res = 0;
        A[pos] += lack;
        for (int i = pos + 1; ; i++, horizonOver++) {
            if (A[i] >= lack) {
                res += lack * horizonOver;
                A[i] -= lack;
                return res;
            } else {
                lack -= A[i];
                res += A[i] * horizonOver;
                A[i] = 0;
            }

            if (B[i] >= lack) {
                res += lack * (horizonOver + 1);
                B[i] -= lack;
                return res;
            } else {
                lack -= B[i];
                res += B[i] * (horizonOver + 1);
                B[i] = 0;
            }
        }
    }

    private static long BTakeFromRight(long[] A, long[] B, int pos, long lack) {
        int horizonOver = 1;
        long res = 0;
        B[pos] += lack;
        for (int i = pos + 1; ; i++, horizonOver++) {
            if (B[i] >= lack) {
                res += lack * horizonOver;
                B[i] -= lack;
                return res;
            } else {
                lack -= B[i];
                res += B[i] * horizonOver;
                B[i] = 0;
            }

            if (A[i] >= lack) {
                res += lack * (horizonOver + 1);
                A[i] -= lack;
                return res;
            } else {
                lack -= A[i];
                res += A[i] * (horizonOver + 1);
                A[i] = 0;
            }
        }
    }


}

/**
 * 贪心算法,有多的先给垂直方向的(必须要这样),在有多的再给水平方向上右边
 * 不足时先从垂直方向拿，再不够再从右边拿
 * AC
 */
class Item3_1 {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int N = scanner.nextInt();
        long[] A = new long[N];
        long[] B = new long[N];
        long total = 0;
        for (int i = 0; i < N; i++) {
            A[i] = scanner.nextLong();
            B[i] = scanner.nextLong();
            total += A[i];
            total += B[i];
        }

        long average = total / 2 / N;
        long res = 0;

        for (int i = 0; i < N; i++) {
            if (A[i] >= average && B[i] >= average) {
                long overA = A[i] - average;
                long overB = B[i] - average;
                A[i] = average;
                B[i] = average;
                if (overA > 0) A[i + 1] += overA;
                if (overB > 0) B[i + 1] += overB;
                res += overA + overB;
            } else if (A[i] >= average) {
                long overA = A[i] - average;
                long lackB = average - B[i];
                if (overA >= lackB) {
                    A[i] = average;
                    B[i] = average;
                    if (overA > lackB) A[i + 1] += overA - lackB;
                    res += overA;
                } else {
                    A[i] = average;
                    B[i] = average;
                    B[i + 1] -= lackB - overA;
                    res += lackB;
                }
            } else if (B[i] >= average) {
                long overB = B[i] - average;
                long lackA = average - A[i];
                if (overB >= lackA) {
                    A[i] = average;
                    B[i] = average;
                    if (overB > lackA) B[i + 1] += overB - lackA;
                    res += overB;
                } else {
                    A[i] = average;
                    B[i] = average;
                    A[i + 1] -= lackA - overB;
                    res += lackA;
                }
            } else {
                long lackA = average - A[i];
                long lackB = average - B[i];
                A[i] = average;
                B[i] = average;
                A[i + 1] -= lackA;
                B[i + 1] -= lackB;
                res += lackA + lackB;
            }
        }
        System.out.println(res);
    }
}