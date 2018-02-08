package org.liuyehcf.algorithm.sort.quicksort;

import java.util.LinkedList;

import static org.liuyehcf.algorithm.sort.SortUtils.exchange;

/**
 * Created by HCF on 2017/7/25.
 */
public class DualPivotQuickSort {
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
            sortRecursive(nums, range[1] + 1, range[2] - 1);
            sortRecursive(nums, range[3], hi);
        }
    }

    private static void sortStack(int[] nums, int lo, int hi) {
        LinkedList<int[]> stack = new LinkedList<int[]>();

        if (lo < hi) {
            stack.push(new int[]{lo, hi});
        }

        while (!stack.isEmpty()) {
            int[] peek = stack.pop();

            int peekLo = peek[0];
            int peekHi = peek[1];

            int[] range = partition(nums, peekLo, peekHi);

            if (peekLo < range[0]) {
                stack.push(new int[]{peekLo, range[0]});
            }

            if (range[1] + 1 < range[2] - 1) {
                stack.push(new int[]{range[1] + 1, range[2] - 1});
            }

            if (range[3] < peekHi) {
                stack.push(new int[]{range[3], peekHi});
            }
        }
    }

    private static int[] partition(int[] nums, int lo, int hi) {
        int lt = lo - 1;
        int le = lt;
        int gt = hi + 1;
        int ge = gt;

        int pivot1 = nums[lo];
        int pivot2 = nums[hi];

        if (pivot1 > pivot2) {
            exchange(nums, lo, hi);
            pivot1 = nums[lo];
            pivot2 = nums[hi];
        }

        int j = lo;

        while (j < ge) {
            if (nums[j] == pivot1) {
                exchange(nums, ++le, j++);
            } else if (nums[j] < pivot1) {
                if (le == lt) {
                    ++lt;
                    ++le;
                    exchange(nums, lt, j++);
                } else {
                    exchange(nums, ++lt, j);
                    exchange(nums, ++le, j++);
                }
            } else if (nums[j] == pivot2) {
                exchange(nums, --ge, j);
            } else if (nums[j] > pivot2) {
                if (ge == gt) {
                    --gt;
                    --ge;
                    exchange(nums, gt, j);
                } else {
                    exchange(nums, --gt, j);
                    exchange(nums, --ge, j);
                }
            } else {
                j++;
            }
        }

        return new int[]{lt, le, ge, gt};
    }
}
