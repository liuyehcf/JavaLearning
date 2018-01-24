package org.liuyehcf.mianshi.alibaba;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * Created by HCF on 2017/4/26.
 */
public class Item2 {
    public static void main(String[] args) {
        ArrayList<Integer> inputs = new ArrayList<Integer>();
        Scanner in = new Scanner(System.in);
        String line = in.nextLine();
        if (line != null && !line.isEmpty()) {
            int res = resolve(line.trim());
            System.out.println(String.valueOf(res));
        }
    }

    public static int resolve(String expr) {
        LinkedList<Integer> stack = new LinkedList<Integer>();
        for (int i = 0; i < expr.length(); i++) {
            if (expr.charAt(i) == ' ') continue;
            else if (Character.isDigit(expr.charAt(i))) {
                int value = 0;
                while (i < expr.length() && Character.isDigit(expr.charAt(i))) {
                    value = value * 10 + expr.charAt(i) - '0';
                    i++;
                }
                i--;
                if (stack.size() >= 16) return -2;
                stack.push(value);
            } else if (expr.charAt(i) == '*') {
                if (stack.size() < 2) return -1;
                int leftValue = stack.pop();
                int rightValue = stack.pop();
                stack.push(leftValue * rightValue);
            } else if (expr.charAt(i) == '+') {
                if (stack.size() < 2) return -1;
                int leftValue = stack.pop();
                int rightValue = stack.pop();
                stack.push(leftValue + rightValue);
            } else if (expr.charAt(i) == '^') {
                if (stack.size() < 1) return -1;
                int value = stack.pop();
                stack.push(value + 1);
            }
        }
        return stack.pop();
    }
}
