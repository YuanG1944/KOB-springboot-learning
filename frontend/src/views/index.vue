<template>
  <el-menu
    :default-active="activeIndex"
    class="el-menu-demo"
    mode="horizontal"
    background-color="#545c64"
    text-color="#fff"
    :ellipsis="false"
    active-text-color="#ffd04b"
    @select="handleSelect"
    router
  >
    <div class="page-logo">King Of Bots</div>
    <el-menu-item index="/pk">对战</el-menu-item>
    <el-menu-item index="/round-list">对局列表</el-menu-item>
    <el-menu-item index="/rank-list">排行榜</el-menu-item>
    <div class="flex-grow" />
    <el-sub-menu index="pesonal">
      <template #title>Yuan Gao</template>
      <el-menu-item index="/personal-file/bot">机器对战</el-menu-item>
    </el-sub-menu>
  </el-menu>

  <div class="main-container">
    <div class="scroll-card">
      <el-card class="main-card">
        <router-view />
      </el-card>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, defineExpose, onMounted } from "vue";

const activeIndex = ref(window.location.pathname || "/pk");

const handleSelect = (key: string, keyPath: string[]) => {
  console.log(key, keyPath);
};

defineExpose({});
</script>

<style scoped lang="less">
.el-menu {
  width: 100%;
}
.page-logo {
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 0 20px;
  margin-right: 48px;
  font-size: 20px;
  color: #f1c40f;
}

.flex-grow {
  flex-grow: 1;
}

.main-container {
  width: 100%;
  height: calc(100vh - 58px);
  overflow: auto;
  background-color: #dcdde1;
  .scroll-card {
    margin: 20px;
    display: flex;
    justify-content: center;
    .main-card {
      width: 78%;
      min-height: calc(100vh - 98px);
    }
  }
}
</style>
