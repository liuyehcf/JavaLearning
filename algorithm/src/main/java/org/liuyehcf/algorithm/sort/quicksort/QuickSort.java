package org.liuyehcf.algorithm.sort.quicksort;

import java.util.LinkedList;

import static org.liuyehcf.algorithm.sort.SortUtils.exchange;

/**
 * Created by HCF on 2017/4/23.
 */

public class QuickSort {
    public static void sortRecursive(int[] nums) {
        sortRecursive(nums, 0, nums.length - 1);
    }

    public static void sort(int[] nums) {
        sortStack(nums, 0, nums.length - 1);
    }

    private static void sortRecursive(int[] nums, int lo, int hi) {
        if (lo < hi) {
            int mid = partition(nums, lo, hi);
            sortRecursive(nums, lo, mid - 1);
            sortRecursive(nums, mid + 1, hi);
        }
    }

    private static void sortStack(int[] nums, int lo, int hi) {
        LinkedList<int[]> stack = new LinkedList<int[]>();

        if (lo < hi) {
            stack.push(new int[]{lo, hi});
        }

        while (!stack.isEmpty()) {
            int[] peek = stack.pop();

            int peekLo = peek[0], peekHi = peek[1];

            int mid = partition(nums, peekLo, peekHi);

            if (peekLo < mid - 1) {
                stack.push(new int[]{peekLo, mid - 1});
            }
            if (mid + 1 < peekHi) {
                stack.push(new int[]{mid + 1, peekHi});
            }
        }
    }

    private static int partition(int[] nums, int lo, int hi) {
        int i = lo - 1;
        int pivot = nums[hi];

        for (int j = lo; j < hi; j++) {
            if (nums[j] < pivot) {
                exchange(nums, ++i, j);
            }
        }

        exchange(nums, ++i, hi);

        return i;
    }
}

