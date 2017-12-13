package org.liuyehcf.sort.mergesort;

/**
 * Created by HCF on 2017/7/27.
 */
public class MergeSort {
    public static void sort(int[] nums) {
        sort(nums, 0, nums.length - 1);
    }

    private static void sort(int[] nums, int left, int right) {
        if (left < right) {
            int mid = left + (right - left >> 1);

            sort(nums, left, mid);

            sort(nums, mid + 1, right);

            merge(nums, left, mid, mid + 1, right);
        }
    }

    private static void merge(int[] nums, int left1, int right1, int left2, int right2) {
        int[] tmp = new int[right1 - left1 + 1];

        System.arraycopy(nums, left1, tmp, 0, tmp.length);

        int i1 = 0, i2 = left2, i = left1;

        while (i1 <= tmp.length - 1 && i2 <= right2) {
            if (tmp[i1] <= nums[i2]) {
                nums[i++] = tmp[i1++];
            } else {
                nums[i++] = nums[i2++];
            }
        }

        if (i1 <= tmp.length - 1) {
            System.arraycopy(tmp, i1, nums, i, tmp.length - i1);
        } else if (i2 <= right2) {
            System.arraycopy(nums, i2, nums, i, right2 - i2 + 1);
        }
    }
}
