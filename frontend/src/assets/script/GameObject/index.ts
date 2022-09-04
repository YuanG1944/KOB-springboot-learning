const AC_GAME_OBJECTS: GameObject[] = [];

export default class GameObject {
  timedelta = 0;
  hasCallStart = false;
  constructor() {
    AC_GAME_OBJECTS.push(this);
  }

  // 开始函数
  start(): void {
    console.log("start");
  }

  // 每一帧执行一次, 除了第一针之外
  update(): void {
    console.log("update");
  }

  beforeDestroy(): void {
    console.log("destroy");
  }

  destroy(): void {
    this.beforeDestroy();
    for (let i in AC_GAME_OBJECTS) {
      const obj = AC_GAME_OBJECTS[i];
      if (obj === this) {
        AC_GAME_OBJECTS.splice(Number(i));
        break;
      }
    }
  }
}

let lastTimestamp: number = 0; //上一次执行的时刻
const step = (timestamp: number) => {
  for (let obj of AC_GAME_OBJECTS) {
    if (!obj.hasCallStart) {
      obj.hasCallStart = true;
      obj.start();
    } else {
      obj.timedelta = timestamp - lastTimestamp;
      obj.update();
    }
  }
  lastTimestamp = timestamp;
  requestAnimationFrame(step);
};

requestAnimationFrame(step);
