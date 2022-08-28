<template>
  <div>
    <div v-for="hero in test.heros" :key="hero.name">
      <div>名字: {{ hero.name }}</div>
      <div>战力: {{ hero.rating }}</div>
    </div>
  </div>
</template>

<script lang="ts">
import { ref, reactive, onBeforeMount } from "vue";
import { queryTest } from "@/service/kob";

interface HeroType {
  name: string;
  rating: string;
}

export default {
  setup() {
    const res = ref<string>("123");
    const test = reactive<{
      heros: HeroType[];
    }>({ heros: [] });

    onBeforeMount(async () => {
      const res = await queryTest("");
      test.heros = res as HeroType[];
    });

    console.log(test, "<---test--->");

    return {
      res,
      test,
    };
  },
};
</script>

<style scoped lang="less"></style>
