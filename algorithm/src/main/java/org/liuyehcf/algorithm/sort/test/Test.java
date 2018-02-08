package org.liuyehcf.algorithm.sort.test;

import org.liuyehcf.algorithm.sort.bubblesort.BubbleSort;
import org.liuyehcf.algorithm.sort.heapsort.HeapSort;
import org.liuyehcf.algorithm.sort.insertsort.BinaryInsertSort;
import org.liuyehcf.algorithm.sort.insertsort.InsertSort;
import org.liuyehcf.algorithm.sort.mergesort.MergeSort;
import org.liuyehcf.algorithm.sort.quicksort.DualPivotQuickSort;
import org.liuyehcf.algorithm.sort.quicksort.QuickSort;
import org.liuyehcf.algorithm.sort.quicksort.ThreeWayQuickSort;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by HCF on 2017/7/25.
 */
public class Test {
    private static final int LEN = 5000;
    private static final int TIMES = 100;
    private static final int BOUND = 10;
    private static final Random random = new Random();
    private static Class<int[]> CLASS = int[].class;
    private static List<Class<?>> classList = new ArrayList<Class<?>>();

    private static void add(Class<?> clazz) {
        classList.add(clazz);
    }

    public static void main(String[] args) throws Exception {

        add(QuickSort.class);
        add(ThreeWayQuickSort.class);
        add(DualPivotQuickSort.class);
        add(BubbleSort.class);
        add(InsertSort.class);
        add(BinaryInsertSort.class);
        add(HeapSort.class);
        add(MergeSort.class);
        add(Arrays.class);

        test();

        TimeUnit.SECONDS.sleep(1);
    }

    private static void test() throws Exception {
        testRandomArray();

        testRepeatArray();

        testPartialSortedArray();
    }


    private static void testRandomArray() throws Exception {
        for (Class<?> clazz : classList) {
            long duration = 0;
            for (int t = 0; t < TIMES; t++) {
                Method method = clazz.getMethod("sort", CLASS);


                int[] nums = new int[LEN];

                for (int i = 0; i < LEN; i++) {
                    nums[i] = random.nextInt();
                }

                long start = System.currentTimeMillis();

                method.invoke(null, nums);

                duration += System.currentTimeMillis() - start;

                if (!check(nums)) {
                    throw new RuntimeException();
                }
            }
            System.out.format("%-20s %-20s : %d ms\n", "[RandomArray]", clazz.getSimpleName(), duration);

        }

        System.out.println("\n------------------------------------------\n");
    }

    private static void testRepeatArray() throws Exception {
        for (Class<?> clazz : classList) {
            long duration = 0;
            for (int t = 0; t < TIMES; t++) {
                Method method = clazz.getMethod("sort", CLASS);

                int[] nums = new int[LEN];

                for (int i = 0; i < LEN; i++) {
                    nums[i] = random.nextInt(BOUND);
                }

                long start = System.currentTimeMillis();

                method.invoke(null, nums);

                duration += System.currentTimeMillis() - start;

                if (!check(nums)) {
                    throw new RuntimeException();
                }
            }
            System.out.format("%-20s %-20s : %d ms\n", "[RepeatArray]", clazz.getSimpleName(), duration);

        }

        System.out.println("\n------------------------------------------\n");
    }

    private static void testPartialSortedArray() throws Exception {
        for (Class<?> clazz : classList) {
            long duration = 0;
            for (int t = 0; t < TIMES; t++) {
                Method method = clazz.getMethod("sort", CLASS);

                int[] nums = new int[LEN];

                int i = 0;

                while (i < LEN) {
                    int runLen = random.nextInt(100);
                    if (i + runLen > LEN) {
                        runLen = LEN - i;
                    }

                    boolean ascend = random.nextBoolean();

                    int val = random.nextInt(100000) + 50000;

                    while (--runLen >= 0) {
                        if (ascend) {
                            nums[i++] = val++;
                        } else {
                            nums[i++] = val--;
                        }
                    }
                }

                if (i != LEN) throw new RuntimeException();

                long start = System.currentTimeMillis();

                method.invoke(null, nums);

                duration += System.currentTimeMillis() - start;

                if (!check(nums)) {
                    throw new RuntimeException();
                }
            }
            System.out.format("%-20s %-20s : %d ms\n", "[PartialSortedArray]", clazz.getSimpleName(), duration);

        }

        System.out.println("\n------------------------------------------\n");
    }

    private static boolean check(int[] nums) {
        for (int j = 1; j < nums.length; j++) {
            if (nums[j] < nums[j - 1]) return false;
        }
        return true;
    }

}
