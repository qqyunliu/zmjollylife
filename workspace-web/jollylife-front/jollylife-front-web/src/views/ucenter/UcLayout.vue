<template>
  <div class="header">
    <router-link to="/" class="logo">
      <span class="iconfont icon-logo"></span>主站
    </router-link>
    <div class="user-info">
      <Avatar class="avatar"
              :avatar="loginStore.userInfo.avatar"
              :userId="loginStore.userInfo.userId"
              :nickId="loginStore.userInfo.nickId || loginStore.userInfo.nickName"
              :linkable="false"
              :width="35"></Avatar>
    </div>
  </div>
  <div class="ucenter-body">
    <div class="left-side">
      <router-link class="upload-btn" to="/ucenter/postVideo">
        <span class="iconfont icon-upload"></span> 投稿
      </router-link>
      <div class="menu-list">
        <el-menu :router="true"
                 :default-active="defaultActive"
                 :default-openeds="defaultOpeneds"
                 class="custom-menu">

          <template v-for="item in menuList" :key="item.path">
            <el-menu-item :index="item.path" v-if="!item.children">
              <span :class="['iconfont', 'icon-' + item.icon]"></span>
              <span class="title">{{ item.name }}</span>
            </el-menu-item>
            <el-sub-menu v-else :index="item.path">
              <template #title>
                <span :class="['iconfont', 'icon-' + item.icon]"></span>
                <span class="title">{{ item.name }}</span>
              </template>
              <el-menu-item :index="sub.path" v-for="sub in item.children" :key="sub.path">
                <span class="sub-menu">{{ sub.name }}</span>
              </el-menu-item>
            </el-sub-menu>
          </template>
        </el-menu>
      </div>
    </div>
    <div class="right-content">
      <div class="right-content-inner">
        <router-view></router-view>
      </div>
    </div>
  </div>
  <Account></Account>
</template>

<script setup>
import Account from '../../views/account/Account.vue';
import { ref, watch, getCurrentInstance } from "vue";
import { useRoute } from "vue-router";
import { useLoginStore } from "@/stores/loginStore.js";

const { proxy } = getCurrentInstance();
const route = useRoute();
const loginStore = useLoginStore();

const menuList = [
  // 保持你原有的菜单配置完全不变
  { name: "首页", path: "/ucenter/home", icon: "home" },
  { name: "内容管理", path: "/ucenter/content", icon: "content", children: [{ name: "稿件管理", path: "/ucenter/video" }] },
  { name: "互动管理", path: "/ucenter/hudong", icon: "hudong", children: [{ name: "评论管理", path: "/ucenter/comment" }, { name: "弹幕管理", path: "/ucenter/danmu" }] },
];

const defaultActive = ref();
const defaultOpeneds = ref([]);

const init = () => {
  menuList.forEach((item) => {
    defaultOpeneds.value.push(item.path);
  });
};
init();

watch(
  () => route.currentRoute,
  () => { defaultActive.value = route.path; },
  { immediate: true, deep: true }
);
</script>

<style lang="scss" scoped>
.header {
  width: 100%;
  background: #fff;
  height: 60px;
  box-shadow: 0 2px 10px 0 rgba(0, 0, 0, 0.05);
  position: fixed;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0px 24px;
  z-index: 100;

  .logo {
    text-decoration: none;
    color: #00aeec; /* B站蓝 */
    font-size: 18px;
    font-weight: bold;
    display: flex;
    align-items: center;
    .iconfont { font-size: 24px; margin-right: 8px; }
  }
}

.ucenter-body {
  padding-top: 60px;
  display: flex;
  min-height: 100vh;
  background: #f4f5f7; /* B站后台经典的浅灰底色，能衬托出白色卡片 */

  .left-side {
    width: 220px; /* 稍微加宽一点，显得更大气 */
    height: calc(100vh - 60px);
    background: #fff;
    border-right: 1px solid #f1f2f3;
    overflow: auto;
    display: flex;
    flex-direction: column;
    align-items: center;

    .upload-btn {
      background: #fb7299; /* B站主题粉 */
      color: #fff;
      width: 150px;
      height: 40px;
      display: flex;
      justify-content: center;
      align-items: center;
      border-radius: 4px;
      margin: 20px 0;
      text-decoration: none;
      font-size: 15px;
      transition: background 0.3s;
      
      .iconfont { margin-right: 6px; }
      &:hover { background: #ff85a9; }
    }

    .menu-list {
      width: 100%;
      
      .custom-menu {
        border-right: none;

        .iconfont {
          font-size: 18px;
          margin-right: 12px;
          color: #9499a0;
        }

        :deep(.el-menu-item), :deep(.el-sub-menu__title) {
          height: 50px;
          line-height: 50px;
          color: #61666d;
          border-radius: 4px;
          margin: 0 10px; /* 给菜单项加一点边距，让它像个按钮 */
          padding-left: 20px !important;
          transition: all 0.3s;

          &:hover { background-color: #f1f2f3; }
        }

        /* 选中状态：浅蓝色底纹 + 主题蓝文字 */
        :deep(.el-menu-item.is-active) {
          background-color: #e6f7ff;
          color: #00aeec;
          font-weight: 500;
          
          .iconfont { color: #00aeec; }
        }

        .sub-menu { padding-left: 12px; }
      }
    }
  }

  .right-content {
    flex: 1;
    height: calc(100vh - 60px);
    overflow: auto;

    .right-content-inner {
      width: 1200px;
      margin: 20px auto;
      min-height: calc(100vh - 100px);
      /* 这里去掉了原本生硬的背景白，把白色背景交给内部组件去实现卡片化 */
    }
  }
}
</style>
