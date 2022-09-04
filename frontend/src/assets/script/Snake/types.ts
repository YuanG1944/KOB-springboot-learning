export interface ISnakeInfo {
  id: string;
  color: string;
  r: number;
  c: number;
}

export enum EForwardX {
  left = 1001,
  right,
}
export enum EForwardY {
  up = 1003,
  down,
}

export enum EStatus {
  idle = "IDLE", // 静止不动
  move = "MOVE", // 正在移动
  die = "DIE", // 死亡
}

export type NoCommad = -1;
