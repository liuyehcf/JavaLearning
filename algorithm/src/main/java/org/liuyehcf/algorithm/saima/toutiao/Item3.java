package org.liuyehcf.algorithm.saima.toutiao;

import java.util.Scanner;

/**
 * Created by HCF on 2017/4/18.
 */
public class Item3 {
    public static void main(String[] args) {
        StringBuilder[] ary = new StringBuilder[5];
        for (int i = 0; i < 5; i++) {
            ary[i] = new StringBuilder();
        }
        Scanner scanner = new Scanner(System.in);
        String exp = scanner.nextLine();
        for (int i = 0; i < exp.length(); i++) {
            if (exp.charAt(i) == ' ') continue;
            addString(ary, exp.charAt(i), true);
        }

        String res = calculate(exp);
        addString(ary, '=', true);
        for (int i = 0; i < res.length(); i++) {
            if (res.charAt(i) == ' ') continue;
            if (i != res.length() - 1)
                addString(ary, res.charAt(i), true);
            else
                addString(ary, res.charAt(i), false);

        }

        for (int i = 0; i < 5; i++) {

            System.out.println(ary[i].toString());
        }

    }

    private static void addString(StringBuilder[] ary, char c, boolean needBlank) {
        if (c == '1') {
            ary[0].append("*");
            ary[1].append("*");
            ary[2].append("*");
            ary[3].append("*");
            ary[4].append("*");
        } else if (c == '2') {
            ary[0].append("***");
            ary[1].append("  *");
            ary[2].append("***");
            ary[3].append("*  ");
            ary[4].append("***");
        } else if (c == '3') {
            ary[0].append("***");
            ary[1].append("  *");
            ary[2].append("***");
            ary[3].append("  *");
            ary[4].append("***");
        } else if (c == '4') {
            ary[0].append("* *");
            ary[1].append("* *");
            ary[2].append("***");
            ary[3].append("  *");
            ary[4].append("  *");
        } else if (c == '5') {
            ary[0].append("***");
            ary[1].append("*  ");
            ary[2].append("***");
            ary[3].append("  *");
            ary[4].append("***");
        } else if (c == '6') {
            ary[0].append("***");
            ary[1].append("*  ");
            ary[2].append("***");
            ary[3].append("* *");
            ary[4].append("***");
        } else if (c == '7') {
            ary[0].append("***");
            ary[1].append("  *");
            ary[2].append("  *");
            ary[3].append("  *");
            ary[4].append("  *");
        } else if (c == '8') {
            ary[0].append("***");
            ary[1].append("* *");
            ary[2].append("***");
            ary[3].append("* *");
            ary[4].append("***");
        } else if (c == '9') {
            ary[0].append("***");
            ary[1].append("* *");
            ary[2].append("***");
            ary[3].append("  *");
            ary[4].append("***");
        } else if (c == '0') {
            ary[0].append("***");
            ary[1].append("* *");
            ary[2].append("* *");
            ary[3].append("* *");
            ary[4].append("***");
        } else if (c == '+') {
            ary[0].append("   ");
            ary[1].append(" * ");
            ary[2].append("***");
            ary[3].append(" * ");
            ary[4].append("   ");
        } else if (c == '-') {
            ary[0].append("   ");
            ary[1].append("   ");
            ary[2].append("***");
            ary[3].append("   ");
            ary[4].append("   ");
        } else if (c == '*') {
            ary[0].append("   ");
            ary[1].append("* *");
            ary[2].append(" * ");
            ary[3].append("* *");
            ary[4].append("   ");
        } else if (c == '/') {
            ary[0].append("   ");
            ary[1].append("  *");
            ary[2].append(" * ");
            ary[3].append("*  ");
            ary[4].append("   ");
        } else if (c == '=') {
            ary[0].append("    ");
            ary[1].append("****");
            ary[2].append("    ");
            ary[3].append("****");
            ary[4].append("    ");
        } else if (c == '.') {
            ary[0].append("  ");
            ary[1].append("  ");
            ary[2].append("  ");
            ary[3].append("**");
            ary[4].append("**");
        }

        if (needBlank) {
            ary[0].append("  ");
            ary[1].append("  ");
            ary[2].append("  ");
            ary[3].append("  ");
            ary[4].append("  ");
        }
    }

    private static String calculate(String exp) {
        double sum = 0;
        double pre = 0;
        char sign = '+';
        for (int i = 0; i < exp.length(); i++) {
            char c = exp.charAt(i);
            if (c == ' ') continue;
            else if (Character.isDigit(c)) {
                int cur = 0;
                while (i < exp.length() && Character.isDigit(exp.charAt(i))) {
                    cur = cur * 10 + (exp.charAt(i++) - '0');
                }
                i--;
                if (sign == '+') {
                    sum += pre;
                    pre = (double) cur;
                } else if (sign == '-') {
                    sum += pre;
                    pre = (double) -cur;
                } else if (sign == '*') {
                    pre = pre * (double) cur;
                } else {
                    pre = pre / (double) cur;
                }
            } else {
                sign = c;
            }
        }
        sum += pre;
        String res = Double.toString(sum);
        if (res.charAt(res.length() - 2) == '.' && res.charAt(res.length() - 1) == '0') {
            res = res.substring(0, res.length() - 2);
        }
        int indexOfPoint = res.indexOf('.');
        if (indexOfPoint != -1 && res.length() - indexOfPoint > 3) {
            int len = res.length() - indexOfPoint - 3;
            res = res.substring(0, res.length() - len);
        }

        if (res.length() > 3 && res.charAt(res.length() - 3) == '.' && res.charAt(res.length() - 2) == '0' && res.charAt(res.length() - 1) == '0') {
            res = res.substring(0, res.length() - 3);
        }
        return res;
    }
}
