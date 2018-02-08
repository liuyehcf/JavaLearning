package org.liuyehcf.algorithm.hihocoder.competition.microsoft20170408;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * AC
 * <p>
 * Created by liuye on 2017/4/9 0009.
 */
public class Item1 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int N = scanner.nextInt();

        Map<Integer, Integer> rows = new HashMap<Integer, Integer>();
        Map<Integer, Integer> cols = new HashMap<Integer, Integer>();
        Map<Integer, Integer> obliques1 = new HashMap<Integer, Integer>();
        Map<Integer, Integer> obliques2 = new HashMap<Integer, Integer>();

        long res = 0;

        for (int i = 0; i < N; i++) {
            int row = scanner.nextInt();
            int col = scanner.nextInt();
            int oblique1 = row - col;
            int oblique2 = row + col;
            if (rows.containsKey(row)) {
                res += rows.get(row);
                rows.put(row, rows.get(row) + 1);
            } else {
                rows.put(row, 1);
            }

            if (cols.containsKey(col)) {
                res += cols.get(col);
                cols.put(col, cols.get(col) + 1);
            } else {
                cols.put(col, 1);
            }

            if (obliques1.containsKey(oblique1)) {
                res += obliques1.get(oblique1);
                obliques1.put(oblique1, obliques1.get(oblique1) + 1);
            } else {
                obliques1.put(oblique1, 1);
            }

            if (obliques2.containsKey(oblique2)) {
                res += obliques2.get(oblique2);
                obliques2.put(oblique2, obliques2.get(oblique2) + 1);
            } else {
                obliques2.put(oblique2, 1);
            }

        }
        System.out.println(res);
    }
}
