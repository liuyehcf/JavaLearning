package org.liuyehcf.sort;

/**
 * Created by HCF on 2017/7/25.
 */
public class SortUtils {
    public static void exchange(int[] nums, int i, int j) {
        int temp = nums[i];
        nums[i] = nums[j];
        nums[j] = temp;
    }
}
