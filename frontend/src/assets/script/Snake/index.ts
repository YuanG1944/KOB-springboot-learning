import GameObject from "../GameObject";
import Cell from "../Cell";
import GameMap from "../GameMap";
import { ISnakeInfo, EForwardX, EForwardY, EStatus, NoCommad } from "./types";
import * as _ from "lodash";

export default class Snake extends GameObject {
  id: string;
  color: string;
  gamemap: GameMap;
  speed: number = 5; // 蛇每秒钟走5个格子

  // cells存放蛇的身体, cells[0]存放蛇头
  cells: Cell[];

  //下一步的指令
  direction: EForwardX | EForwardY | NoCommad = -1;

  //蛇的状态
  status: EStatus = EStatus.idle;

  //下一步的目标位置
  nextCell: Cell | null = null;

  //回合数
  step = 0;

  //点误差
  eps = 1e-2;

  //蛇头朝向
  eyeDirection: EForwardX | EForwardY;

  //眼睛偏移量
  eyeDx = {
    [EForwardY.up]: [-1, 1],
    [EForwardX.right]: [1, 1],
    [EForwardY.down]: [1, -1],
    [EForwardX.left]: [-1, -1],
  };

  //眼睛偏移量
  eyeDy = {
    [EForwardY.up]: [-1, -1],
    [EForwardX.right]: [-1, 1],
    [EForwardY.down]: [1, 1],
    [EForwardX.left]: [1, -1],
  };

  dirOffset = {
    [EForwardX.left]: [0, -1],
    [EForwardX.right]: [0, 1],
    [EForwardY.up]: [-1, 0],
    [EForwardY.down]: [1, 0],
  };

  constructor(info: ISnakeInfo, gamemap: GameMap) {
    super();

    this.id = info.id;
    this.color = info.color;
    this.gamemap = gamemap;

    this.cells = [new Cell(info.r, info.c)];

    this.id === "1" && (this.eyeDirection = EForwardY.up);
    this.id === "2" && (this.eyeDirection = EForwardY.down);
  }

  start(): void {}

  clearDirection() {
    this.direction = -1;
  }

  setDirection(d: EForwardX | EForwardY | -1) {
    this.direction = d;
  }

  /**
   * 检测当前回合蛇的长度是否增加
   */
  checkTailIncreasing() {
    if (this.step <= 10) return true;
    if (this.step % 3 === 1) return true;

    return false;
  }

  /**
   * 将蛇的状态变为走下一步
   */
  nextStep() {
    if (this.direction !== -1) {
      this.nextCell = new Cell(
        this.cells[0].r + this.dirOffset[this.direction][0],
        this.cells[0].c + this.dirOffset[this.direction][1]
      );
    }
    this.eyeDirection = this.direction;
    this.clearDirection();
    this.status = EStatus.move;
    this.step++;

    const k = this.cells.length;
    for (let i = k; i > 0; i--) {
      this.cells[i] = _.cloneDeep(this.cells[i - 1]);
    }

    if (!this.gamemap.checkValid(this.nextCell)) {
      // 下一步操作非法, 🐍去世
      this.status = EStatus.die;
    }
  }

  updateMove(): void {
    const dx = this.nextCell.x - this.cells[0].x;
    const dy = this.nextCell.y - this.cells[0].y;
    const distance = Math.sqrt(dx * dx + dy * dy);
    if (distance < this.eps) {
      this.cells[0] = this.nextCell; // 添加一个新蛇头
      this.nextCell = null;
      // 小于误差认定为重合
      this.status = EStatus.idle; // 走完了, 停下来了
      !this.checkTailIncreasing() && this.cells.pop();
    } else {
      //每两帧走过的距离
      const moveDistance = (this.speed * this.timedelta) / 1000;
      this.cells[0].x += (moveDistance * dx) / distance;
      this.cells[0].y += (moveDistance * dy) / distance;

      if (!this.checkTailIncreasing()) {
        const len = this.cells.length;
        const tail = this.cells[len - 1];
        const tailTarget = this.cells[len - 2];
        const tailDx = tailTarget.x - tail.x;
        const tailDy = tailTarget.y - tail.y;
        tail.x += (moveDistance * tailDx) / distance;
        tail.y += (moveDistance * tailDy) / distance;
      }
    }
  }

  update(): void {
    // 每一帧执行一次
    if (this.status === EStatus.move) {
      this.updateMove();
    }

    this.render();
  }

  render(): void {
    const { L, ctx } = this.gamemap;

    if (this.status === EStatus.die) {
      this.color = "#f1f2f6";
    }

    ctx.fillStyle = this.color;
    for (const cell of this.cells) {
      ctx.beginPath();
      ctx.arc(cell.x * L, cell.y * L, (L / 2) * 0.8, 0, Math.PI * 2);
      ctx.fill();
    }

    for (let i = 1; i < this.cells.length; i++) {
      const a = this.cells[i - 1];
      const b = this.cells[i];
      if (Math.abs(a.x - b.x) < this.eps && Math.abs(a.y - b.y) < this.eps) {
        continue;
      }
      if (Math.abs(a.x - b.x) < this.eps) {
        ctx.fillRect((a.x - 0.4) * L, Math.min(a.y, b.y) * L, L * 0.8, Math.abs(a.y - b.y) * L);
      } else {
        ctx.fillRect(Math.min(a.x, b.x) * L, (a.y - 0.4) * L, Math.abs(a.x - b.x) * L, L * 0.8);
      }
    }

    ctx.fillStyle = "#000";
    for (let i = 0; i < 2; i++) {
      const x = (this.cells[0].x + this.eyeDx[this.eyeDirection][i] * 0.15) * L;
      const y = (this.cells[0].y + this.eyeDy[this.eyeDirection][i] * 0.15) * L;
      ctx.beginPath();
      ctx.arc(x, y, L * 0.07, 0, Math.PI * 2);
      ctx.fill();
    }
  }
}
