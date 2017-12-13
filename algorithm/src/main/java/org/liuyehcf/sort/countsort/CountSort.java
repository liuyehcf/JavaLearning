package org.liuyehcf.sort.countsort;

import java.lang.reflect.Method;
import java.util.Random;

/**
 * Created by HCF on 2017/7/27.
 */
public class CountSort {
    public static void sort(int[] nums) {
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;

        for (int num : nums) {
            if (num < min) {
                min = num;
            }

            if (num > max) {
                max = num;
            }
        }

        int len = max - min + 1;

        int[] count = new int[len];

        for (int num : nums) {
            count[num - min]++;
        }

        for (int i = 1; i < count.length; i++) {
            count[i] += count[i - 1];
        }

        int[] tmp = nums.clone();

        for (int num : tmp) {
            nums[--count[num - min]] = num;
        }
    }

    public static void main(String[] args) {
        final int LEN = 500000;

        final int TIMES = 100;

        final Random random = new Random();

        long duration = 0;
        for (int t = 0; t < TIMES; t++) {


            int[] nums = new int[LEN];

            for (int i = 0; i < LEN; i++) {
                nums[i] = random.nextInt(100000);
            }

            long start = System.currentTimeMillis();

            sort(nums);

            duration += System.currentTimeMillis() - start;

            for (int j = 1; j < nums.length; j++) {
                if (nums[j] < nums[j - 1]) throw new RuntimeException();
            }
        }
        System.out.format("%-20s : %d ms\n", "CountSort", duration);


        System.out.println("\n------------------------------------------\n");
    }
}
