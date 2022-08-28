<template>
  <div class="playground">
    <div class="playground-contain">
      <GameMap />
    </div>
  </div>
</template>

<script lang="ts" setup>
import GameMap from "./GameMap.vue";
</script>

<style lang="less" scoped>
.playground {
  width: 100%;
  display: flex;
  justify-content: center;
  &-contain {
    width: 60vw;
    height: 60vw;
    background-color: beige;
  }
}
</style>
