package org.liuyehcf.algorithm.hihocoder.competition.microsoft20170408;

/**
 * TLE
 * <p>
 * Created by liuye on 2017/4/9 0009.
 */


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;


class Status {
    int finishedJobNum;
    int workRobotNum;
    int[] copyRobot;

    public Status(int finishedJobNum, int workRobot, int Q, int copyRobot) {
        this.finishedJobNum = finishedJobNum;
        this.workRobotNum = workRobot;
        this.copyRobot = new int[Q];
        this.copyRobot[Q - 1] = copyRobot * 2;
    }

    public Status(Status status, int copyRobotNum) {
        this.finishedJobNum = status.finishedJobNum + status.workRobotNum - copyRobotNum;
        this.workRobotNum = status.workRobotNum - copyRobotNum;
        this.copyRobot = status.copyRobot.clone();
        this.copyRobot[this.copyRobot.length - 1] = copyRobotNum * 2;
    }

    public void update() {
        workRobotNum += copyRobot[0];
        for (int i = 0; i < copyRobot.length - 1; i++) {
            copyRobot[i] = copyRobot[i + 1];
        }
    }

    public String toString() {
        return "finishedJobNum: " + finishedJobNum + ", workRobotNum" + workRobotNum + ", copyRobot" + Arrays.toString(copyRobot);
    }
}

public class Item2 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int N = scanner.nextInt();
        int Q = scanner.nextInt();
        //int N=10;
        //int Q=1;

        int res = 0;
        if (N <= 0) {
            System.out.println(res);
            return;
        }

        //dp[i]:i时刻，有几种状态
        List<List<Status>> dp = new ArrayList<List<Status>>();
        dp.add(Arrays.asList(new Status(0, 1, Q, 0)));

        for (int i = 1; ; i++) {
            List<Status> cur = new ArrayList<Status>();
            for (Status status : dp.get(i - 1)) {
                status.update();
                for (int copyRobotNum = 0; copyRobotNum <= status.workRobotNum; copyRobotNum++) {

                    Status tmp = new Status(status, copyRobotNum);
                    //System.out.println("time: "+i+", "+tmp);

                    if (tmp.finishedJobNum >= N) {
                        System.out.println(i);
                        return;
                    }
                    cur.add(tmp);
                }
            }
            dp.add(cur);
        }
    }
}

/**
 * AC
 */
class Item2_1 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        long N = scanner.nextLong();
        int Q = scanner.nextInt();

        for (int time = 1; ; time++) {
            for (int copyNum = 0; copyNum * Q < time; copyNum++) {
                int workTime = time - copyNum * Q;
                long robotNum = 1L << copyNum;
                long jobNum = robotNum * workTime;
                if (jobNum >= N) {
                    System.out.println(time);
                    return;
                }
            }
        }
    }
}

