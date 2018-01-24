package org.liuyehcf.hihocoder.competition.microsoft20170331;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by liuye on 2017/4/12 0012.
 */
public class Item1 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int P = scanner.nextInt();
        int Q = scanner.nextInt();
        int N = scanner.nextInt();

        List<State> pre = new ArrayList<State>();
        pre.add(new State(percentage(P), 0, 0, 1D));

        double res = 0;

        while (!pre.isEmpty()) {
            List<State> cur = new ArrayList<State>();
            System.out.println(pre);
            for (State s : pre) {
                if (s.p < 0.0001) {//由于概率的百分数始终为整数，因此小于此数就是0
                    double temP = percentage(Q);
                    int temCnt = s.cnt;
                    int temStep = s.step + 1;
                    double temAccumulative = s.accumulative * 1D;
                    if (temCnt == N) {
                        res += temStep * temAccumulative;
                    } else {
                        cur.add(new State(temP, temCnt, temStep, temAccumulative));
                    }
                } else if (s.p > 0.9999) {
                    double temP = percentage(P / (1 << s.cnt));
                    int temCnt = s.cnt + 1;
                    int temStep = s.step + 1;
                    double temAccumulative = s.accumulative * 1D;
                    if (temCnt == N) {
                        res += temStep * temAccumulative;
                    } else {
                        cur.add(new State(temP, temCnt, temStep, temAccumulative));
                    }
                } else {
                    double temP1 = percentage(P / (1 << s.cnt));
                    int temCnt1 = s.cnt + 1;
                    int temStep1 = s.step + 1;
                    double temAccumulative1 = s.accumulative * s.p;
                    if (temCnt1 == N) {
                        res += temStep1 * temAccumulative1;
                    } else {
                        cur.add(new State(temP1, temCnt1, temStep1, temAccumulative1));
                    }


                    double temP2 = percentage(((int) (s.p * 100) + Q) >= 100 ? 100 : ((int) (s.p * 100) + Q));
                    int temCnt2 = s.cnt;
                    int temStep2 = s.step + 1;
                    double temAccumulative2 = s.accumulative * (1 - s.p);

                    if (temCnt2 == N) {
                        res += temStep2 * temAccumulative2;
                    } else {
                        cur.add(new State(temP2, temCnt2, temStep2, temAccumulative2));
                    }
                }
            }
            pre = cur;
        }
        System.out.println(res);
    }

    private static double percentage(int p) {
        return (double) p / 100D;
    }

    private static final class State {
        public double p;
        public int cnt;
        public int step;
        public double accumulative;

        public State(double p, int cnt, int step, double accumulative) {
            this.p = p;
            this.cnt = cnt;
            this.step = step;
            this.accumulative = accumulative;
        }

        public String toString() {
            return p + ", " + cnt + ", " + step + ", " + accumulative + "\n";
        }
    }

}
