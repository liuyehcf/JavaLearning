package org.liuyehcf.sort.quicksort;

import java.util.LinkedList;

import static org.liuyehcf.sort.SortUtils.exchange;

/**
 * Created by HCF on 2017/7/25.
 */
public class ThreeWayQuickSort {

    public static void sortRecursive(int[] nums) {
        sortRecursive(nums, 0, nums.length - 1);
    }


    public static void sort(int[] nums) {
        sortStack(nums, 0, nums.length - 1);
    }


    private static void sortRecursive(int[] nums, int lo, int hi) {
        if (lo < hi) {
            int[] range = partition(nums, lo, hi);

            sortRecursive(nums, lo, range[0]);

            sortRecursive(nums, range[1], hi);
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

            int[] range = partition(nums, peekLo, peekHi);

            if (peekLo < range[0]) {
                stack.push(new int[]{peekLo, range[0]});
            }
            if (range[1] < peekHi) {
                stack.push(new int[]{range[1], peekHi});
            }
        }
    }

    private static int[] partition(int[] nums, int lo, int hi) {
        int lt = lo - 1;
        int gt = hi + 1;

        int pivot = nums[hi];

        int j = lo;
        while (j < gt) {
            if (nums[j] < pivot) {
                exchange(nums, ++lt, j++);
            } else if (nums[j] > pivot) {
                exchange(nums, --gt, j);
            } else {
                j++;
            }
        }

        return new int[]{lt, gt};
    }
}
