package org.liuyehcf.sort.heapsort;

import static org.liuyehcf.sort.SortUtils.*;

/**
 * Created by HCF on 2017/7/26.
 */
public class HeapSort {
    public static void sort(int[] nums) {
        buildMaxHeap(nums);

        for (int len = nums.length; len >= 2; len--) {
            exchange(nums, 0, len - 1);
            maxHeapFix(nums, len - 1, 0);
        }
    }

    private static void maxHeapFix(int[] nums, int heapSize, int i) {
        int left = i * 2 + 1, right = i * 2 + 2;

        if (left >= heapSize) {
            left = -1;
        }
        if (right >= heapSize) {
            right = -1;
        }

        int max = i;

        if (left != -1 && nums[left] > nums[i]) {
            max = left;
        }

        if (right != -1 && nums[right] > nums[max]) {
            max = right;
        }

        if (max != i) {
            exchange(nums, i, max);
            maxHeapFix(nums, heapSize, max);
        }
    }

    private static void buildMaxHeap(int[] nums) {
        for (int i = nums.length >> 1; i >= 0; i--) {
            maxHeapFix(nums, nums.length, i);
        }
    }
}
