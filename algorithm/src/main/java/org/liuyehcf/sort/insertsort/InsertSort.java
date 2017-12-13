package org.liuyehcf.sort.insertsort;

/**
 * Created by HCF on 2017/7/26.
 */
public class InsertSort {
    public static void sort(int[] nums) {
        for (int i = 1; i < nums.length; i++) {
            int pivot = nums[i];
            int j = i - 1;
            while (j >= 0 && nums[j] > pivot) {
                nums[j + 1] = nums[j];
                j--;
            }
            nums[j + 1] = pivot;
        }
    }
}
