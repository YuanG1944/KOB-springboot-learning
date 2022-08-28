import GameObject from "@/assets/script/GameObject";

export default class GameMap extends GameObject {
  ctx: CanvasRenderingContext2D | null = null;
  parent: HTMLElement | null = null;

  L = 0; // 单位长度
  rows = 13;
  cols = 13;

  constructor(ctx: CanvasRenderingContext2D, parent: HTMLElement) {
    super();

    this.ctx = ctx;
    this.parent = parent;
    this.L = 0;
  }

  start(): void {
    console.log("<--start-->");
  }

  updateSize() {
    this.L = Math.min(this.parent!.clientWidth / this.cols, this.parent!.clientWidth / this.cols);
    this.ctx!.canvas.width = this.L * this.cols;
    this.ctx!.canvas.height = this.L * this.rows;
  }
  update(): void {
    this.updateSize();
    this.render();
    // console.log("<--update-->");
  }

  render(): void {
    // console.log("<---render-->");
    this.ctx!.fillStyle = "#27ae60";
    this.ctx!.fillRect(0, 0, this.ctx!.canvas.width, this.ctx!.canvas.height);
  }
}
