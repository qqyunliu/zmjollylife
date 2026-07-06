<template>
  <div class="main-container"
       :style="{
      'max-width': proxy.bodyMaxWidth + 'px',
      'min-width': proxy.bodyMinWidth + 'px',
    }">
    <div class="header"
         v-show="navActionStore.showHeader">
      <LayoutHeader :transparent="isHomePage && headerTransparent"></LayoutHeader>
    </div>
    <div class="header-fixed"
         v-if="navActionStore.fixedHeader&&showFixedHeader||navActionStore.forceFixedHeader">
      <LayoutHeader theme="dark"></LayoutHeader>
    </div>
    <div class="category-fiexd"
         v-show="navActionStore.showCategory">
      <div class="category-fiexd-inner">
        <Category></Category>
      </div>
    </div>
    <div class="body-main">
      <router-view></router-view>
    </div>
    <Account></Account>
  </div>
</template>

<script setup>
import LayoutHeader from "../layout/LayoutHeader.vue";
import Category from '../layout/Category.vue';
import Account from "../account/Account.vue";
import { ref, computed, getCurrentInstance, nextTick, onMounted } from "vue";
const { proxy } = getCurrentInstance();
import { useRoute, useRouter } from "vue-router";
const route = useRoute();
const router = useRouter();

import { useNavAction } from "../../stores/navActionStore";
const navActionStore = useNavAction();

const showFixedHeader = ref(false);
const headerTransparent = ref(true);

const isHomePage = computed(() => {
  const name = route.name;
  return name === 'index' || name === 'message' || name === 'history' || name === 'userCollection' || name === 'categoryVideo' || name === 'subCategoryVideo';
});

const windowScrollHandler = () => {
  var curScrollTop = window.scrollY;
  if (isHomePage.value) {
    if (curScrollTop <= 20) {
      showFixedHeader.value = false;
    } else {
      showFixedHeader.value = true;
    }
    headerTransparent.value = true;
  } else {
    if (curScrollTop <= 20) {
      showFixedHeader.value = false;
      headerTransparent.value = true;
    } else {
      showFixedHeader.value = true;
      headerTransparent.value = false;
    }
  }
};

onMounted(() => {
  window.addEventListener("scroll", windowScrollHandler);
  headerTransparent.value = isHomePage.value;
});
</script>

<style >
body {
  background: #c2bfbf !important;
}
</style>

<style lang="scss" scoped>
.main-container {
  background: #fff;
  margin: 0px auto;
  min-height: calc(100vh);

  .header {
    margin: 0px auto;
    background-color: #7c9ce1;
    height: 180px;
    background-position: center;
    background-repeat: no-repeat;
    width: 100%;
    position: relative;
    z-index: 9999;
    background-image: url('../../assets/banner_bg.png');
  }

  .header-fixed {
    position: fixed;
    width: 100%;
    background: rgb(255, 255, 255);
    top: 0;
    z-index: 9998;
  }

  .category-fiexd {
    background: #fff;
    padding-top: 10px;
  }

  .category-fiexd-inner {
    padding: 0 30px;
  }

  .body-main {
    padding-top: 10px;
  }
}
</style>
