package org.liuyehcf.algorithm.other;

import java.util.Arrays;
import java.util.Random;

/**
 * Created by HCF on 2017/8/16.
 */
public class BankAlgorithm {
    /**
     * 可用资源数目
     */
    private static final int RESOURCE_NUM = 10;

    /**
     * 进程数量
     */
    private static final int PROCESS_NUM = 100;

    /**
     * 初始化的系统资源总量
     */
    private int[] total;

    /**
     * 目前可用资源
     * available[i]代表第i类资源的可用数目
     * available[i]的初始值就是系统配置的该类资源的全部数目
     */
    private int[] available;

    /**
     * max[i][j]代表进程i需要j类资源的最大数目
     */
    private int[][] max;

    /**
     * allocation[i][j]代表进程i分得j类资源的数目
     */
    private int[][] allocation;

    /**
     * need[i][j]代表进程i还需要j类资源的数目
     */
    private int[][] need;

    /**
     * state[i]表示进程i是否处于安全状态，true是，false否
     * isSafeState的临时变量，避免重复分配内存，因此设为字段
     */
    private boolean[] state;

    /**
     * finished[i]==true说明进程i执行完毕了
     */
    private boolean[] finished;

    /**
     * 执行完毕的进程数量
     */
    private int finishedCnt;

    /**
     * available数组的拷贝，临时数组
     */
    private int[] pause;

    /**
     * 随机数，用于随机产生需要分配资源的进程id以及资源数量
     */
    private Random random = new Random();

    public static void main(String[] args) {
        BankAlgorithm bankAlgorithm = new BankAlgorithm();

        bankAlgorithm.serve();
    }

    public void serve() {
        initialize();

        while (finishedCnt != PROCESS_NUM) {
            int processId = getRandomProcessId();

            int[] request = getRandomRequest(processId);

            check();
            requestResource(processId, request);
            check();
        }
    }

    /**
     * 初始化系统
     */
    private void initialize() {
        total = new int[RESOURCE_NUM];
        available = new int[RESOURCE_NUM];
        max = new int[PROCESS_NUM][RESOURCE_NUM];
        allocation = new int[PROCESS_NUM][RESOURCE_NUM];
        need = new int[PROCESS_NUM][RESOURCE_NUM];
        state = new boolean[PROCESS_NUM];
        finished = new boolean[PROCESS_NUM];
        pause = new int[RESOURCE_NUM];

        finishedCnt = 0;

        Arrays.fill(total, 10);
        Arrays.fill(available, 10);
        for (int i = 0; i < PROCESS_NUM; i++) {
            for (int j = 0; j < RESOURCE_NUM; j++) {
                max[i][j] = random.nextInt(5) + 1;
                need[i][j] = max[i][j];
            }
        }
    }

    private int getRandomProcessId() {
        int remainProcessNum = PROCESS_NUM - finishedCnt;

        int index = random.nextInt(remainProcessNum);

        for (int i = 0; i < PROCESS_NUM; i++) {
            if (finished[i]) continue;
            if (index-- == 0) {
                return i;
            }
        }
        throw new RuntimeException();
    }

    private int[] getRandomRequest(int processId) {
        int[] request = new int[RESOURCE_NUM];

        for (int j = 0; j < RESOURCE_NUM; j++) {
            request[j] = random.nextInt(Math.min(available[j], need[processId][j]) + 1);
        }

        return request;
    }

    /**
     * 为进程processId申请指定的资源
     *
     * @param processId
     * @param request
     */
    private void requestResource(int processId, int[] request) {
        // 首先检查一下输入的合法性
        for (int j = 0; j < RESOURCE_NUM; j++) {
            if (request[j] > need[processId][j]) {
                throw new RuntimeException();
            }
            if (request[j] > available[j]) {
                throw new RuntimeException();
            }
        }

        // 试探性分配
        for (int j = 0; j < RESOURCE_NUM; j++) {
            available[j] -= request[j];
            allocation[processId][j] += request[j];
            need[processId][j] -= request[j];
        }


        if (isSafeState()) {
            System.err.println("Safe!");
            // 对于进程processId，已经分配完毕的资源种类的计数值
            int count = 0;

            for (int j = 0; j < RESOURCE_NUM; j++) {
                if (need[processId][j] == 0) {
                    count++;
                }
            }

            if (count == RESOURCE_NUM) {
                for (int j = 0; j < RESOURCE_NUM; j++) {
                    available[j] += allocation[processId][j];
                    allocation[processId][j] = 0;
                    need[processId][j] = max[processId][j];
                }
                finishedCnt++;
                finished[processId] = true;
                System.out.println("Process " + processId + " is finished!");
            }
        } else {
            System.err.println("Not safe!");
            // 撤销试探性分配
            for (int j = 0; j < RESOURCE_NUM; j++) {
                available[j] += request[j];
                allocation[processId][j] -= request[j];
                need[processId][j] += request[j];
            }
        }
    }

    /**
     * 判断当前系统是否满足安全性
     *
     * @return
     */
    private boolean isSafeState() {

        System.arraycopy(available, 0, pause, 0, RESOURCE_NUM);
        Arrays.fill(state, false);

        boolean canBreak = false;
        while (!canBreak) {
            canBreak = true;
            for (int i = 0; i < PROCESS_NUM; i++) {
                if (finished[i]) {
                    state[i] = true;
                    continue;
                }
                int count = 0;
                for (int j = 0; j < RESOURCE_NUM; j++) {
                    // 资源j的现有量能够满足进程i对资源j的需求
                    if (need[i][j] <= pause[j]) {
                        count++;
                    }
                }

                // 现有的所有资源均能满足进程i的需求，也就是说进程i不依赖其他进程所占有的资源也能够完成执行
                if (!state[i] && count == RESOURCE_NUM) {
                    state[i] = true;
                    // 由于进程i是安全的，因此当进程i结束并释放所有占用的资源后，可能还会有别的进程被判定为安全，因此while循环不能结束
                    canBreak = false;
                    // 现在假定进程i执行完毕了，现在退还i占用的所有资源
                    for (int j = 0; j < RESOURCE_NUM; j++) {
                        pause[j] += allocation[i][j];
                    }
                }
            }
        }

        int safeProcessNum = 0;

        for (boolean b : state) {
            safeProcessNum += (b ? 1 : 0);
        }

        return safeProcessNum == PROCESS_NUM;
    }

    private void check() {
        for (int i = 0; i < PROCESS_NUM; i++) {
            for (int j = 0; j < RESOURCE_NUM; j++) {
                if (max[i][j] != need[i][j] + allocation[i][j]) throw new RuntimeException();
                if (max[i][j] > total[j]) throw new RuntimeException();
            }
        }

        for (int j = 0; j < RESOURCE_NUM; j++) {
            if (available[j] < 0) throw new RuntimeException();
        }

        if (!isSafeState()) throw new RuntimeException();
    }
}
