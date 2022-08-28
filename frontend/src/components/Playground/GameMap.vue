<template>
  <div ref="parent" class="gamemap">
    <canvas ref="canvas"> </canvas>
  </div>
</template>

<script lang="ts" setup>
import GameMap from "@/assets/script/GameMap";
import { ref, defineExpose, onMounted } from "vue";

const parent = ref<HTMLElement | null>(null);
const canvas = ref<HTMLCanvasElement | null>(null);

onMounted(() => {
  if (parent.value && canvas.value) {
    new GameMap(canvas.value?.getContext("2d"), parent.value);
  }
});

defineExpose({
  parent,
  canvas,
});
</script>

<style lang="less" scoped></style>
