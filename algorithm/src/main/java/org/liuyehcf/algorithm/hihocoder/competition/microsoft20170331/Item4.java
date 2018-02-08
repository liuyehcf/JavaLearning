package org.liuyehcf.algorithm.hihocoder.competition.microsoft20170331;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by liuye on 2017/4/12 0012.
 */
public class Item4 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String S = scanner.next();

        List<String> res = new ArrayList<String>();
        helper(S, 0, 0, res);
        System.out.println(res);
        System.out.println(res.get(0).length() - S.length() + " " + res.size());
    }

    /**
     * 必须要给定start，避免重复
     * &1()&2()))->对于这个括号对，标记&的地方是待填的括号,如果第一个括号填在&2处,第二个括号填在&1处,会与第一个括号填在&1处，第二个括号填在&2处重合
     * !!!因此后面增加的括号必须在前一次加括号的位置之后!!!
     *
     * @param s
     * @param pos
     * @param start
     * @param res
     */
    private static void helper(String s, int pos, int start, List<String> res) {
        int count = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '(') {
                count++;
            } else {
                count--;
            }
            if (count >= 0) continue;

            for (int j = start; j <= i; j++) {
                if (j == start || s.charAt(j - 1) != '(') {
                    helper(s.substring(0, j) + '(' + s.substring(j), i + 1, j + 1, res);
                }
            }
            return;
        }
        helperReverse(s, s.length() - 1, s.length() - 1, res);
    }

    private static void helperReverse(String s, int pos, int start, List<String> res) {
        int count = 0;
        for (int i = pos; i >= 0; i--) {
            if (s.charAt(i) == ')') {
                count++;
            } else {
                count--;
            }
            if (count >= 0) continue;

            for (int j = start; j >= i; j--) {
                if (j == start || s.charAt(j + 1) != ')') {
                    helperReverse(s.substring(0, j + 1) + ')' + s.substring(j + 1), i - 1, j - 1, res);
                }
            }

            return;
        }
        res.add(s);
    }
}


/**
 * C++
 */
//#include<cstdio>
//#include<cstring>
//#include<cstdlib>
//#include<cmath>
//#include<iostream>
//#include<algorithm>
//#include<queue>
//using namespace std;
//        const int MAXN=1005;
//        const int INF=0x3f3f3f3f;
//        const int Mod=1000000007;
//        char s[MAXN];
//        int dis[MAXN][MAXN],cnt[MAXN][MAXN];
//        int main()
//        {
//        scanf("%s",s+1);
//        int n=strlen(s+1);
//        memset(dis,INF,sizeof(dis));
//        dis[0][0]=0;
//        cnt[0][0]=1;
//        queue<pair<int,int> >q;
//        q.push(make_pair(0,0));
//        while(!q.empty())
//        {
//        int i=q.front().first;
//        int j=q.front().second;
//        q.pop();
//        for(int t='(',k=-1;t<=')' && k<=1;t+=')'-'(',k+=2)
//        {
//        int ti=min(n,i+(s[i+1]==t)),tj=j-k;
//        if(tj>=0 && tj<=n)
//        {
//        if(dis[ti][tj]>dis[i][j]+1)
//        {
//        dis[ti][tj]=dis[i][j]+1;
//        cnt[ti][tj]=0;
//        q.push(make_pair(ti,tj));
//        }
//        if(dis[ti][tj]==dis[i][j]+1)
//        cnt[ti][tj]=(cnt[ti][tj]+cnt[i][j])%Mod;
//        }
//        }
//        }
//        return 0*printf("%d %d\n",dis[n][0]-n,cnt[n][0]);
//        }


class Item4_1 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String S = scanner.next();

        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (int i = 0; i < S.length(); i++) {
            char c = S.charAt(i);
            if (c == '(') count++;
            else count--;
            if (count < 0) {
                count = 0;
                sb.append(c);
            }
        }
        while (count > 0) {
            sb.append('(');
            count--;
        }
        int res1 = sb.length();
        int n = (S.length() + res1) / 2;
        List<String> res = new ArrayList<String>();
        sb.setLength(0);
        allParenthess(n, 0, 0, sb, res);

        count = 0;
        for (String s : res) {
            if (isSubString(s, S)) {
                count++;
            }
        }

        System.out.println(res1 + " " + count);
    }


    private static void allParenthess(int n, int leftNum, int rightNum, StringBuilder sb, List<String> res) {
        if (rightNum == n) {
            res.add(sb.toString());
            return;
        }
        if (leftNum == rightNum) {
            sb.append('(');
            allParenthess(n, leftNum + 1, rightNum, sb, res);
            sb.setLength(sb.length() - 1);
        } else if (leftNum == n) {
            sb.append(')');
            allParenthess(n, leftNum, rightNum + 1, sb, res);
            sb.setLength(sb.length() - 1);

        } else {
            sb.append('(');
            allParenthess(n, leftNum + 1, rightNum, sb, res);
            sb.setLength(sb.length() - 1);

            sb.append(')');
            allParenthess(n, leftNum, rightNum + 1, sb, res);
            sb.setLength(sb.length() - 1);
        }
    }

    private static boolean isSubString(String s, String sub) {
        boolean[][] dp = new boolean[s.length() + 1][sub.length() + 1];
        dp[0][0] = true;
        for (int i = 1; i <= s.length(); i++) {
            for (int j = 1; j <= sub.length(); j++) {
                if (s.charAt(i - 1) == sub.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] || dp[i - 1][j];
                } else {
                    dp[i][j] = dp[i - 1][j];
                }
            }
        }
        return dp[s.length()][sub.length()];
    }
}