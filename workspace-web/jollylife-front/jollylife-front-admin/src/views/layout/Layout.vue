<template>
  <div class="layout-body">
    <el-container>
      <el-aside class="aside">
        <div class="logo">
          <span>jollylife管理后台</span>
        </div>
        <div class="menu-panel">
          <el-menu :default-openeds="defaultOpeneds"
                   :collapse-transition="false"
                   class="el-menu-vertical-demo"
                   background-color="#3d3c4a"
                   text-color="#fff"
                   router
                   :defaultActive="route.path">
            <template v-for="item in menuList">
              <el-sub-menu :index="item.path"
                           v-if="item.children"
                           :key="item.path">
                <template #title>
                  <i :class="['iconfont', item.icon]"></i>
                  <span class="menu-name">{{ item.menuName }}</span>
                </template>
                <el-menu-item :index="subItem.path"
                              v-for="subItem in item.children"
                              :key="subItem.path">
                  <span class="menu-name">{{ subItem.menuName }}</span>
                </el-menu-item>
              </el-sub-menu>
              <el-menu-item :index="item.path"
                            v-else
                            :key="item.path">
                <i :class="['iconfont', item.icon]"></i>
                <template #title>
                  <span class="menu-name">{{ item.menuName }}</span>
                </template>
              </el-menu-item>
            </template>
          </el-menu>
        </div>
      </el-aside>

      <el-container>
        <el-header class="header">{{userInfo}}</el-header>
        <el-main class="main-content">
          <div class="content-body">
            <router-view />
          </div>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, getCurrentInstance, reactive, nextTick } from "vue";
const { proxy } = getCurrentInstance();
import { useRoute, useRouter } from "vue-router";
const route = useRoute();
const router = useRouter();

const userInfo = ref(proxy.VueCookies.get("account"));
const menuList = [
  {
    menuName: "首页",
    path: "/home",
    icon: "icon-home",
  },
  {
    menuName: "内容管理",
    path: "/content",
    icon: "icon-content",
    children: [
      {
        menuName: "稿件管理",
        path: "/content/video",
      },
      {
        menuName: "视频审核",
        path: "/content/video-audit",
      },
    ],
  },
  {
    menuName: "互动管理",
    path: "/interact",
    icon: "icon-hudong",
    children: [
      {
        menuName: "评论管理",
        path: "/interact/comment",
      },
      {
        menuName: "弹幕管理",
        path: "/interact/delDanmu",
      },
    ],
  },
  {
    menuName: "用户管理",
    path: "/user/userList",
    icon: "icon-fans",
  },
];

const defaultActive = ref();
const defaultOpeneds = ref([]);

const init = () => {
  menuList.forEach((item) => {
    defaultOpeneds.value.push(item.path)
  });
};
init();

</script>

<style scoped>
/* 整体布局样式 */
.layout-body {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

/* 侧边栏样式 */
.aside {
  width: 220px !important;
  background-color: #3d3c4a;
  transition: all 0.3s;
  height: 100vh;
  overflow-y: auto;
}

/* 侧边栏logo样式 */
.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 16px;
  font-weight: bold;
  border-bottom: 1px solid #4a4a5a;
}

/* 菜单面板样式 */
.menu-panel {
  padding: 10px 0;
}

/* 菜单项通用样式 */
.el-menu {
  border-right: none;
}

.el-menu-item,
.el-sub-menu__title {
  height: 50px;
  line-height: 50px;
  color: #bfcbd9;
}

.el-menu-item:hover,
.el-sub-menu__title:hover {
  background-color: #4a4a5a !important;
  color: white !important;
}

.el-menu-item.is-active {
  background-color: #4a4a5a !important;
  color: white !important;
  border-left: 3px solid #409eff;
}

/* 菜单图标样式 */
.iconfont {
  margin-right: 10px;
  width: 24px;
  text-align: center;
  font-size: 18px;
  vertical-align: middle;
}

/* 菜单名称样式 */
.menu-name {
  font-size: 14px;
  vertical-align: middle;
}

/* 顶部导航栏样式 */
.header {
  height: 60px !important;
  background-color: white;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  padding: 0 20px;
  display: flex;
  align-items: center;
  text-align: right;
}

/* 主内容区域样式 */
.main-content {
  padding: 20px;
  background-color: #f0f2f5;
  height: calc(100vh - 60px);
  overflow-y: auto;
}

.content-body {
  background-color: white;
  border-radius: 4px;
  padding: 20px;
  min-height: calc(100% - 40px);
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
}

/* 响应式设计 - 小屏幕时侧边栏可折叠 */
@media (max-width: 768px) {
  .aside {
    width: 64px !important;
  }

  .menu-name {
    display: none;
  }

  .el-sub-menu__title {
    padding: 0 20px !important;
  }
}
</style>
