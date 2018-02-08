package org.liuyehcf.algorithm.sort.insertsort;

/**
 * Created by HCF on 2017/7/26.
 */
public class BinaryInsertSort {
    public static void sort(int[] nums) {
        for (int i = 1; i < nums.length; i++) {
            int pivot = nums[i];

            int left = 0, right = i - 1;

            if (nums[right] <= pivot) {
                continue;
            } else if (nums[0] > pivot) {
                System.arraycopy(nums, 0, nums, 1, i);
                nums[0] = pivot;
                continue;
            }

            while (left < right) {
                int mid = left + (right - left >> 1);

                if (nums[mid] <= pivot) {
                    left = mid + 1;
                } else {
                    right = mid;
                }
            }

            System.arraycopy(nums, left, nums, left + 1, i - left);
            nums[left] = pivot;
        }
    }
}
