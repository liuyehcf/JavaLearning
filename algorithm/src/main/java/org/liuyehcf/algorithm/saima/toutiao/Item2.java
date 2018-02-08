package org.liuyehcf.algorithm.saima.toutiao;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by HCF on 2017/4/18.
 */
public class Item2 {
    public static void main(String[] args) {
        Map<String, Integer> map = new HashMap<String, Integer>();
        Scanner scanner = new Scanner(System.in);

        String temp = null;
        while (!(temp = scanner.next()).equals("0")) {
            if (map.containsKey(temp)) {
                map.put(temp, map.get(temp) + 1);
            } else {
                map.put(temp, 1);
            }
        }

        System.out.println(map.size());
    }
}
