package com.kob.backend.consumer.utils;

import com.alibaba.fastjson.JSONObject;
import com.kob.backend.consumer.WebSocketServer;
import com.kob.backend.pojo.Record;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class Game extends Thread {
    private final Integer rows, cols, inner_walls_count;

    @Getter
    private final int[][] g;

    private final static int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};

    @Getter
    private final Player playerA, playerB;

    // null 表示未获取到，0,1,2,3 表明上下左右四个方向
    @Getter
    private Integer nextStepA = null, nextStepB = null;

    private final ReentrantLock lock = new ReentrantLock();
    private String status = "playing"; // playing -> finished
    private String loser = ""; // all: 平局, A: A输， B: B输

    public void setNextStepA(Integer nextStepA) {
        this.lock.lock();
        try {
            this.nextStepA = nextStepA;
        } finally {
            this.lock.unlock();
        }
    }

    public void setNextStepB(Integer nextStepB) {
        this.lock.lock();
        try {
            this.nextStepB = nextStepB;
        } finally {
            this.lock.unlock();
        }
    }


    public Game(Integer rows, Integer cols, Integer inner_walls_count, Integer idA, Integer idB) {
        this.rows = rows;
        this.cols = cols;
        this.inner_walls_count = inner_walls_count;
        this.g = new int[rows][cols];
        this.playerA = new Player(idA, this.rows - 2, 1, new ArrayList<>());
        this.playerB = new Player(idB, 1, this.cols - 2, new ArrayList<>());
    }

    private boolean checkConnectivity(int sx, int sy, int tx, int ty) {
        if (sx == tx && sy == ty) return true;
        this.g[sx][sy] = 1;

        for (int i = 0; i < 4; i++) {
            int x = sx + Game.dx[i], y = sy + Game.dy[i];
            if (x >= 0 && x < this.rows && y >= 0 && y < this.cols && this.g[x][y] == 0) {
                if (checkConnectivity(x, y, tx, ty)) {
                    this.g[sx][sy] = 0;
                    return true;
                }
            }
        }
        this.g[sx][sy] = 0;

        return false;
    }

    private boolean draw() {
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.cols; j++) {
                g[i][j] = 0;
            }
        }

        for (int r = 0; r < this.rows; r++) {
            g[r][0] = g[r][this.cols - 1] = 1;
        }

        for (int c = 0; c < this.cols; c++) {
            g[0][c] = g[this.rows - 1][c] = 1;
        }

        Random rand = new Random();
        for (int i = 0; i < this.inner_walls_count / 2; i++) {
            for (int j = 0; j < 1000; j++) {
                int r = rand.nextInt(this.rows);
                int c = rand.nextInt(this.cols);

                if (g[r][c] == 1 | g[this.rows - 1 - r][this.cols - 1 - c] == 1) continue;

                if (r == this.rows - 2 && c == 1 || r == 1 && c == this.cols - 2) continue;

                g[r][c] = g[this.rows - 1 - r][this.cols - 1 - c] = 1;
                break;
            }
        }

        return checkConnectivity(this.rows - 2, 1, 1, this.cols - 2);
    }

    public void createMap() {
        for (int i = 0; i < 1000; i++) {
            if (draw()) break;
        }
    }

    // 等待两名玩家的下一步操作
    private boolean nextStep() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        for (int i = 0; i < 50; i++) {
            try {
                Thread.sleep(100);
                this.lock.lock();
                try {
                    if (this.nextStepA != null && this.nextStepB != null) {
                        this.playerA.getSteps().add(this.nextStepA);
                        this.playerB.getSteps().add(this.nextStepB);
                        return true;
                    }
                } finally {
                    this.lock.unlock();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private boolean check_valid(List<Cell> cellsA, List<Cell> cellsB) {
        int n = cellsA.size();
        Cell cell = cellsA.get(n - 1);

        if (this.g[cell.x][cell.y] == 1) return false;

        // 撞自己身上
        for (int i = 0; i < n - 1; i++) {
            if (cellsA.get(i).x == cell.x && cellsA.get(i).y == cell.y) return false;
        }
        // 撞对方身上
        for (int i = 0; i < n - 1; i++) {
            if (cellsB.get(i).x == cell.x && cellsB.get(i).y == cell.y) return false;
        }

        return true;
    }


    // 判断两名玩家下步操作是否合法
    private void judge() {
        List<Cell> cellsA = this.playerA.getCells();
        List<Cell> cellsB = this.playerB.getCells();

        boolean validA = this.check_valid(cellsA, cellsB);
        boolean validB = this.check_valid(cellsB, cellsA);
        if (!validA || !validB) {
            this.status = "finished";

            if (!validA && !validB) {
                this.loser = "all";
            } else if (!validA) {
                this.loser = "A";
            } else {
                this.loser = "B";
            }
        }


    }

    private void sendAllMessage(String message) {
        if (WebSocketServer.users.get(playerA.getId()) != null) {
            WebSocketServer.users.get(playerA.getId()).sendMessage(message);
        }
        if (WebSocketServer.users.get(playerB.getId()) != null) {
            WebSocketServer.users.get(playerB.getId()).sendMessage(message);
        }
    }

    // 向两个client传递移动信息
    private void sendMove() {
        this.lock.lock();
        try {
            JSONObject resp = new JSONObject();
            System.out.println("move resp: " + resp);

            resp.put("event", "move");
            resp.put("a_direction", this.nextStepA);
            resp.put("b_direction", this.nextStepB);

            this.sendAllMessage(resp.toJSONString());
            this.nextStepA = this.nextStepB = null;
        } finally {
            this.lock.unlock();
        }

    }

    private String getMapString() {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.cols; j++) {
                res.append(this.g[i][j]);
            }
        }
        return res.toString();
    }

    private void saveToDB() {
        Record record = new Record(null, this.playerA.getId(), this.playerA.getSx(), this.playerA.getSy(),
                this.playerB.getId(), this.playerB.getSx(), this.playerB.getSy(), this.playerA.getStepsString(),
                this.playerB.getStepsString(), this.getMapString(), this.loser, new Date());

        WebSocketServer.recordMapper.insert(record);
    }


    // 向两个玩家公布结果
    private void sendResult() {
        JSONObject resp = new JSONObject();
        resp.put("event", "result");
        resp.put("loser", this.loser);
        this.saveToDB();
        this.sendAllMessage(resp.toJSONString());
    }

    @Override
    public void run() {
        for (int i = 0; i < 1000; i++) {
            if (this.nextStep()) {
                this.judge();
                if ("playing".equals(this.status)) {
                    this.sendMove();
                } else {
                    this.sendResult();
                    break;
                }
            } else {
                this.status = "finished";
                this.lock.lock();
                try {
                    if (this.nextStepA == null && this.nextStepB == null) {
                        this.loser = "all";
                    } else if (this.nextStepA == null) {
                        loser = "A";
                    } else {
                        loser = "B";
                    }
                } finally {
                    this.lock.unlock();
                }
                break;
            }

        }
        System.out.println("end");
    }


}
