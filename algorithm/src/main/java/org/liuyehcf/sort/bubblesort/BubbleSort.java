package org.liuyehcf.sort.bubblesort;

import static org.liuyehcf.sort.SortUtils.*;

/**
 * Created by HCF on 2017/7/26.
 */
public class BubbleSort {

    public static void sort(int[] nums) {
        for (int i = 0; i < nums.length; i++) {
            for (int j = nums.length - 1; j > i; j--) {
                compareAndExchange(nums, j - 1, j);
            }
        }
    }

    private static void compareAndExchange(int[] nums, int left, int right) {
        if (nums[left] > nums[right]) {
            exchange(nums, left, right);
        }
    }
}
