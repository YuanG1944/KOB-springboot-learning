package com.kob.backend.consumer.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Player {
    private Integer id;
    private Integer sx;  // 行数
    private Integer sy;  // 列数

    private List<Integer> steps;


    // 检验当前回合，蛇的长度是否增加
    private boolean check_tail_increasing(int steps) {
        if (steps <= 10) return true;
        return steps % 3 == 1;
    }

    public List<Cell> getCells() {
        List<Cell> res = new ArrayList<>();

        int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};

        int x = this.sx, y = this.sy;
        int step = 0;
        res.add(new Cell(x, y));
        for (int d : steps) {
            x += dx[d];
            y += dy[d];
            res.add(new Cell(x, y));
            if (this.check_tail_increasing(++step)) {
                res.remove(0);
            }
        }

        return res;
    }

    public String getStepsString() {
        StringBuilder res = new StringBuilder();
        for (int d : this.steps) {
            res.append(d);
        }
        return res.toString();
    }
}
