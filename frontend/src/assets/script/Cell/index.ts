export default class Cell {
  r = 0;
  c = 0;
  x = 0;
  y = 0;
  constructor(r: number, c: number) {
    this.r = r;
    this.c = c;
    this.x = c + 0.5;
    this.y = r + 0.5;
  }
}
