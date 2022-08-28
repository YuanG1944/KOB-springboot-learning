import { createRouter, createWebHistory, RouteRecordRaw } from "vue-router";
import Pk from "../views/Pk/index.vue";

const routes: Array<RouteRecordRaw> = [
  {
    path: "/",
    name: "Default",
    redirect: "/pk",
  },
  {
    path: "/pk",
    name: "Pk",
    component: Pk,
  },
  {
    path: "/rank-list",
    name: "Rank",
    component: () => import("../views/Rank/index.vue"),
  },
  {
    path: "/round-list",
    name: "Round",
    component: () => import("../views/Round/index.vue"),
  },
  {
    path: "/personal-file/bot",
    name: "Bot",
    component: () => import("../views/PersonalFile/Bot.vue"),
  },
  {
    path: "/404",
    name: "not fount",
    component: () => import("../views/Error/index.vue"),
  },
  {
    path: "/:catchAll(.*)",
    redirect: "/404",
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
