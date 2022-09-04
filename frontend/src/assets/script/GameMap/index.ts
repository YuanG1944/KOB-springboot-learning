import GameObject from "@/assets/script/GameObject";
import Wall from "@/assets/script/Wall";
import * as _ from "lodash";
import Cell from "../Cell";
import Snake from "../Snake";
import { EForwardX, EForwardY, EStatus } from "../Snake/types";

export default class GameMap extends GameObject {
  ctx: CanvasRenderingContext2D | null = null;
  parent: HTMLElement | null = null;

  L = 0; // 单位长度
  rows = 13;
  cols = 14;

  walls: Wall[] = [];
  innerWallsCount = 20;
  putWallsNumber = Number((this.innerWallsCount / 2).toFixed(0));

  snakes: Snake[];

  constructor(ctx: CanvasRenderingContext2D, parent: HTMLElement) {
    super();

    this.ctx = ctx;
    this.parent = parent;
    this.L = 0;

    this.snakes = [
      new Snake(
        {
          id: "1",
          color: "#4876ec",
          r: this.rows - 2,
          c: 1,
        },
        this
      ),
      new Snake(
        {
          id: "2",
          color: "#f94848",
          r: 1,
          c: this.cols - 2,
        },
        this
      ),
    ];
  }

  /**
   * 判断地图是否联通
   * @param g 地图
   * @param sx 起点x轴坐标
   * @param sy 起点y轴坐标
   * @param tx 终点x轴坐标
   * @param ty 终点y轴坐标
   * @returns
   */
  checkConnectivity(g: boolean[][], sx: number, sy: number, tx: number, ty: number): boolean {
    if (sx === tx && sy === ty) return true;
    g[sx][sy] = true;

    const dx = [-1, 0, 1, 0];
    const dy = [0, 1, 0, -1];

    for (let i = 0; i < 4; i++) {
      let x = sx + dx[i];
      let y = sy + dy[i];
      if (!g[x][y] && this.checkConnectivity(g, x, y, tx, ty)) {
        return true;
      }
    }

    return false;
  }

  /**
   *
   * 判断每条蛇是否都准备好了下一回合
   * @returns
   */
  checkReady(): boolean {
    for (const snake of this.snakes) {
      if (snake.status !== EStatus.idle) {
        return false;
      }
      if (snake.direction === -1) {
        return false;
      }
    }
    return true;
  }

  createWalls(): boolean {
    const g = [];
    for (let r = 0; r < this.rows; r++) {
      g[r] = [];
      for (let c = 0; c < this.cols; c++) {
        g[r][c] = false;
      }
    }

    //创建上下边框障碍物
    for (let c = 0; c < this.cols; c++) {
      g[0][c] = g[this.rows - 1][c] = true;
    }

    //创建左右边框障碍物
    for (let r = 0; r < this.rows; r++) {
      g[r][0] = g[r][this.cols - 1] = true;
    }

    //创建随机障碍物
    while (this.putWallsNumber) {
      const r = Number((Math.random() * (this.rows - 1)).toFixed(0));
      const c = Number((Math.random() * (this.cols - 1)).toFixed(0));

      if (r < 0 || r > this.rows - 1 || c > this.cols - 1 || c < 0) {
        continue;
      }

      if ((r === this.rows - 2 && c === 1) || (r === 1 && c === this.cols - 2)) {
        continue;
      }

      if (!g[r][c] && !g[c][r]) {
        g[r][c] = g[this.rows - 1 - r][this.cols - 1 - c] = true;
        this.putWallsNumber--;
      }
    }

    //图联通
    if (!this.checkConnectivity(_.cloneDeep(g), this.rows - 2, 1, 1, this.cols - 2)) {
      return false;
    }

    // 创建障碍物
    for (let r = 0; r < this.rows; r++) {
      for (let c = 0; c < this.cols; c++) {
        if (g[r][c]) {
          this.walls.push(new Wall(r, c, this));
        }
      }
    }

    return true;
  }

  addListeningEvents() {
    this.ctx.canvas.focus();

    const [snake0, snake1] = this.snakes;
    this.ctx.canvas.addEventListener("keydown", (ev: KeyboardEvent) => {
      console.log("ev--->", ev.key);

      ev.key === "w" && snake0.setDirection(EForwardY.up);
      ev.key === "s" && snake0.setDirection(EForwardY.down);
      ev.key === "a" && snake0.setDirection(EForwardX.left);
      ev.key === "d" && snake0.setDirection(EForwardX.right);

      ev.key === "ArrowUp" && snake1.setDirection(EForwardY.up);
      ev.key === "ArrowDown" && snake1.setDirection(EForwardY.down);
      ev.key === "ArrowLeft" && snake1.setDirection(EForwardX.left);
      ev.key === "ArrowRight" && snake1.setDirection(EForwardX.right);
    });
  }

  start(): void {
    for (let i = 0; i < 1000; i++) {
      if (this.createWalls()) break;
    }

    this.addListeningEvents();
  }

  updateSize() {
    this.L = Math.min(this.parent!.clientWidth / this.cols, this.parent!.clientWidth / this.cols);
    this.L = Number(this.L.toFixed(0));
    this.ctx!.canvas.width = this.L * this.cols;
    this.ctx!.canvas.height = this.L * this.rows;
  }

  /**
   * 让两条蛇进入下一回合
   */
  nextStep() {
    for (const snake of this.snakes) {
      snake.nextStep();
    }
  }

  /**
   * 检测蛇的位置是否合法: 没有撞到蛇的身体和墙
   * @param cell
   */
  checkValid(cell: Cell): boolean {
    if (cell) {
      for (const wall of this.walls) {
        if (wall.r === cell.r && wall.c === cell.c) {
          return false;
        }
      }

      for (const snake of this.snakes) {
        let k = snake.cells.length;
        if (!snake.checkTailIncreasing()) {
          k--;
        }
        for (let i = 0; i < k; i++) {
          if (snake.cells[i].r === cell.r && snake.cells[i].c == cell.c) {
            return false;
          }
        }
      }
    }
    return true;
  }

  update(): void {
    this.updateSize();
    if (this.checkReady()) {
      this.nextStep();
    }
    this.render();
  }

  render(): void {
    const color_even = "#2ecc71";
    const color_odd = "#27ae60";
    for (let r = 0; r < this.rows; r++) {
      for (let c = 0; c < this.cols; c++) {
        if ((r + c) % 2 === 0) {
          this.ctx.fillStyle = color_even;
        } else {
          this.ctx.fillStyle = color_odd;
        }
        this.ctx.fillRect(c * this.L, r * this.L, this.L, this.L);
      }
    }
  }
}
