package org.liuyehcf.hihocoder.routine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * TLE
 * <p>
 * Created by liuye on 2017/4/9 0009.
 */

class Status145 {
    int rightAnswer;
    int remainNum;

    public Status145(int rightAnswer, int remainNum) {
        this.rightAnswer = rightAnswer;
        this.remainNum = remainNum;
    }
}

public class Week145 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int group = scanner.nextInt();
        for (; group > 0; group--) {

            int N = scanner.nextInt();
            int M = scanner.nextInt();
            int S = scanner.nextInt();
            int T = scanner.nextInt();

            int[] A = new int[N];

            for (int i = 0; i < N; i++) {
                A[i] = scanner.nextInt();
            }

            List<List<Status145>> list = new ArrayList<List<Status145>>();

            list.add(Arrays.asList(new Status145(0, M)));


            for (int level = 1; level <= N; level++) {
                List<Status145> curLevel = new ArrayList<Status145>();
                //遍历上一关留下的所有状态
                for (Status145 s : list.get(level - 1)) {
                    //本关选择答题m道
                    for (int m = 1; m <= s.remainNum; m++) {
                        //m道题目有right道回答正确
                        for (int right = 0; right <= m; right++) {
                            int wrong = m - right;
                            int curScore = right * S + wrong * T;
                            if (curScore >= A[level - 1]) {
                                curLevel.add(new Status145(s.rightAnswer + right, s.remainNum - m));
                            }
                        }
                    }
                }
                list.add(curLevel);
            }

            int minAnswer = Integer.MAX_VALUE;
            for (Status145 s : list.get(N)) {
                if (s.rightAnswer < minAnswer) {
                    minAnswer = s.rightAnswer;
                }
            }

            if (minAnswer == Integer.MAX_VALUE) {
                System.out.println("No");
            } else {
                System.out.println(minAnswer);
            }
        }
    }
}


class Week145_1 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int group = scanner.nextInt();
        for (; group > 0; group--) {

            int N = scanner.nextInt();
            int M = scanner.nextInt();
            int S = scanner.nextInt();
            int T = scanner.nextInt();

            int[] A = new int[N];

            for (int i = 0; i < N; i++) {
                A[i] = scanner.nextInt();
            }

            //dp[i][j]:表示前i关,总共答题j次的最少答对次数
            int[][] dp = new int[N + 1][M + 1];
            for(int i=1;i<=N;i++){
                Arrays.fill(dp[i], 2000);
            }

            dp[0][0]=0;

            for (int i = 1; i <= N; i++) {//i为关卡号
                int maxRight=(A[i-1]+S-1)/S;
                for(int right=0;right<=maxRight;right++){
                    int remainScore=A[i-1]-right*S;
                    int wrong=0;
                    if(remainScore>0)
                        wrong=(remainScore+T-1)/T;
                    for(int k=0;right+wrong+k<=M;k++){
                        dp[i][right+wrong+k]=Math.min(dp[i][right+wrong+k],dp[i-1][k]+right);
                    }
                }
            }

            int res = 2000;
            for (int i : dp[N]) {
                res = Math.min(res, i);
            }

            System.out.println(res == 2000 ? "No" : res);
        }
    }
}


/*
#include<iostream>

using namespace std;


int min(int a, int b) {
	return a <= b ? a : b;
}

int main() {
	int Time;
	cin >> Time;
	while (--Time >= 0) {
		int N, M, S, T;
		cin >> N >> M >> S >> T;
		int* A = new int[N];
		for (int i = 0; i<N; i++) {
			cin >> A[i];
		}

		int ** dp = new int*[N + 1];
		for (int i = 0; i <= N; i++) {
			dp[i] = new int[M + 1];
		}

		for (int i = 0; i <= N; i++) {
			for (int j = 0; j <= M; j++) {
				dp[i][j] = 2000;
			}
		}

		dp[0][0] = 0;

		int res = 2000;

		for (int i = 1; i <= N; i++) {
			for (int m = 0; m <= M; m++) {
				for (int wrong = 0; wrong <= m&&wrong <= (A[i - 1] + T - 1) / T; wrong++) {
					int right = (A[i - 1] - wrong*T + S - 1) / S;
					if (dp[i - 1][m - wrong] + right + wrong > M) continue;
					if (dp[i - 1][m - wrong] + right > M - m)continue;
					dp[i][m] = min(dp[i][m], dp[i - 1][m - wrong] + right);
				}
				if (i == N) {
					res = min(res, dp[i][m]);
				}
			}
		}

		if (res == 2000) {
			cout << "No" << endl;
		}
		else {
			cout << res << endl;
		}

		for (int i = 0; i <= N; i++) {
			delete[] dp[i];
		}
		delete[] dp;
	}
	system("pause");
}
 */


class Week145_2 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int group = scanner.nextInt();
        for (; group > 0; group--) {

            int N = scanner.nextInt();
            int M = scanner.nextInt();
            int S = scanner.nextInt();
            int T = scanner.nextInt();

            int[] A = new int[N];

            for (int i = 0; i < N; i++) {
                A[i] = scanner.nextInt();
            }

            //dp[i][j]表示答对i题，答错j题能达到的状态，其中dp[i][j][0]代表关卡号，dp[i][j][1]代表分数
            int[][][] dp = new int[M + 1][M + 1][2];
            dp[0][0][0] = 0;
            dp[0][0][1] = 0;
            int res=Integer.MAX_VALUE;

            outer:
            for (int i = 0; i <= M; i++) {
                for (int j = 0; j + i <= M; j++) {
                    if (i == 0 && j == 0) continue;
                    if (i == 0) {
                        int level = dp[i][j - 1][0];
                        int score = dp[i][j - 1][1];
                        score += T;
                        if (score >= A[level]) {
                            dp[i][j][0] = level + 1;
                            dp[i][j][1] = 0;
                        } else {
                            dp[i][j][0] = level;
                            dp[i][j][1] = score;
                        }
                    } else if (j == 0) {
                        int level = dp[i - 1][j][0];
                        int score = dp[i - 1][j][1];
                        score += S;
                        if (score >= A[level]) {
                            dp[i][j][0] = level + 1;
                            dp[i][j][1] = 0;
                        } else {
                            dp[i][j][0] = level;
                            dp[i][j][1] = score;
                        }
                    }
                    else{
                        int level1=dp[i][j-1][0];
                        int level2=dp[i-1][j][0];
                        int score1=dp[i][j-1][1];
                        int score2=dp[i-1][j][1];

                        score1+=T;
                        score2+=S;

                        if(score1>=A[level1]){
                            level1++;
                            score1=0;
                        }

                        if(score2>=A[level2]){
                            level2++;
                            score2=0;
                        }

                        if(level1>level2){
                            dp[i][j][0]=level1;
                            dp[i][j][1]=score1;
                        }
                        else if(level1<level2){
                            dp[i][j][0]=level2;
                            dp[i][j][1]=score2;
                        }
                        else{
                            if(score1>=score2){
                                dp[i][j][0]=level1;
                                dp[i][j][1]=score1;
                            }
                            else{
                                dp[i][j][0]=level2;
                                dp[i][j][1]=score2;
                            }
                        }
                    }
                    if(dp[i][j][0]==N){
                        res=i;
                        break outer;
                    }
                }
            }
            System.out.println(res == Integer.MAX_VALUE ? "No" : res);
        }
    }

    private static int compare(int[] obj1, int[] obj2) {
        if (obj1[0] != obj2[0]) return obj2[0] - obj1[0];
        return obj2[1] - obj1[1];
    }
}