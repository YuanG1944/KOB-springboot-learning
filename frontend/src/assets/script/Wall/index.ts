import GameObject from "@/assets/script/GameObject";
import GameMap from "@/assets/script/GameMap";

export default class Wall extends GameObject {
  r = 0;
  c = 0;
  gamemap: GameMap;
  color = "#cd6133";
  constructor(r: number, c: number, gamemap: GameMap) {
    super();
    this.r = r;
    this.c = c;
    this.gamemap = gamemap;
  }

  update(): void {
    this.render();
  }

  render(): void {
    const L = this.gamemap.L;
    const ctx = this.gamemap.ctx;

    ctx.fillStyle = this.color;
    ctx.fillRect(this.c * L, this.r * L, L, L);
  }
}
