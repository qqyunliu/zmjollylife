

<template>
  <el-config-provider :locale="zhCn"
                      :message="config">
    <router-view></router-view>
  </el-config-provider>
</template>

<script setup>
import zhCn from "element-plus/dist/locale/zh-cn.mjs";

import { ref, reactive, getCurrentInstance, nextTick, onBeforeMount } from "vue";
const { proxy } = getCurrentInstance();
import { useRoute, useRouter } from "vue-router";
import CategoryVue from "./views/layout/Category.vue";
import { useCategoryStore } from "./stores/categoryStore";
import { useLoginStore } from "./stores/loginStore";
const categoryStore = useCategoryStore();

const route = useRoute();
const router = useRouter();


import VueCookies from "vue-cookies";
import { Api } from "./utils/Api.js"
import Request from "./utils/Request.js"
const loginStore = useLoginStore();

import { useSysSettingStore } from "./stores/sysSettingStore"
const sysSettingStore = useSysSettingStore();


// 获取系统设置信息
const getSysSetting = async () => {
  let result = await proxy.request({
    url: Api.getSysSetting, // Api 需提前引入，定义接口地址常量
  });
  if (!result) {
    return;
  }
  sysSettingStore.saveSetting(result.data); // 调用 Pinia Store 的方法存数据
};


const autoLogin = async () => {
  const token = VueCookies.get('token');
  if (!token) {
    return;
  }
  let result = await proxy.request({
    url: proxy.Api.autoLogin,
  });
  if (!result) {
    return;
  }
  saveLoginInfo(result.data);
};
const saveLoginInfo = (loginInfo) => {
  if (!loginInfo) {
    loginStore.saveUserInfo({});

  } else {
    loginStore.saveUserInfo(loginInfo);
  }


};



const config = ref({
  max: 1,
});



let categoryList = [];
let categoryMap = {};

const loadCategory = async () => {
  let result = await Request({
    url: Api.loadCategory,
  });
  if (!result) {
    return;
  }

  categoryList = result.data;

  result.data.forEach((element) => {
    categoryMap[element.categoryCode] = element;

    // 确保children始终是数组
    if (!element.children || !Array.isArray(element.children)) {
      element.children = [];
    }

    element.children.forEach((sub) => {
      categoryMap[sub.categoryCode] = sub;
    });
  });

  categoryStore.saveCategoryMap(categoryMap);
  categoryStore.saveCategoryList(categoryList);
};

onBeforeMount(() => {
  loadCategory();
  autoLogin();
  getSysSetting();
});
</script>

<style scoped>
</style>
