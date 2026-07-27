<template>
  <div :class="['header-bar', 'header-bar-' + theme, { 'header-bar-transparent': transparent }]">
    <div class="menu">
      <router-link class="logo-link" to="/">
        <span class="iconfont icon-logo"></span>
        <span class="logo-text">首页</span>
      </router-link>
    </div>

    <div class="search-body">
      <div class="search-panel">
        <div class="input-panel">
          <input 
            v-model="searchKeyword"
            placeholder="搜索视频、UP主或动态..." 
            @keyup.enter="doSearch"
          />
          <div class="search-btn" @click="doSearch">
            <span class="iconfont icon-search"></span>
          </div>
        </div>
      </div>
    </div>

    <div class="user-panel">
      <div class="user-avatar-wrap"
           @mouseenter="handleMouseEnter"
           @mouseleave="handleMouseLeave">
        
        <template v-if="Object.keys(loginStore.userInfo).length > 0">
          <div class="custom-avatar"
               :class="{ 'custom-avatar-hover': showUserPanel }"
               :style="getAvatarStyle()">
            <img v-if="loginStore.userInfo.avatar"
                 :src="getAvatarUrl()"
                 :alt="loginStore.userInfo.nickName" />
            <span v-else class="avatar-text">
              {{ getAvatarText() }}
            </span>
          </div>

          <div class="custom-user-panel"
               :class="{ 'custom-panel-visible': showUserPanel }">
            <div class="custom-nick-name">
              {{ loginStore.userInfo.nickName || loginStore.userInfo.nickId || '神秘UP主' }}
            </div>

            <div class="custom-count-info">
              <div class="custom-count-item">
                <div class="custom-count-value">{{ userCountInfo.focusCount || 0 }}</div>
                <div class="custom-count-title">关注</div>
              </div>
              <div class="custom-count-item">
                <div class="custom-count-value">{{ userCountInfo.fansCount || 0 }}</div>
                <div class="custom-count-title">粉丝</div>
              </div>
            </div>

            <div class="menu-list">
              <router-link :to="`/ucenter/video`" class="custom-menu-item">
                <div class="menu-left"><span class="iconfont icon-play"></span>投稿管理</div>
                <span class="iconfont icon-right"></span>
              </router-link>
            </div>

            <div class="custom-logout" @click="logout">
              <span class="iconfont icon-logout"></span>退出登录
            </div>
          </div>
        </template>

        <div v-else class="login-avatar" @click="login">
          <span class="login-text">登录</span>
        </div>
      </div>

      <div class="user-panel-item" @click="navJump('/ucenter/home')">
        <div class="iconfont icon-light"></div>
        <div class="item-text">个人中心</div>
      </div>

      <div class="btn-upload" @click="navJump('/ucenter/postVideo')">
        <el-button class="upload-btn">
          <span class="iconfont icon-upload"></span>
          <span>投稿</span>
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, getCurrentInstance } from "vue";
import { ElMessageBox } from "element-plus";
import { useRoute, useRouter } from "vue-router";
const { proxy } = getCurrentInstance();
const route = useRoute();
const router = useRouter();

import { useLoginStore } from "../../stores/loginStore";
const loginStore = useLoginStore();

const props = defineProps({
  theme: {
    type: String,
    default: "light",
  },
  transparent: {
    type: Boolean,
    default: false,
  },
});

const searchKeyword = ref('');

const doSearch = () => {
  if (!searchKeyword.value.trim()) {
    return;
  }
  router.push({
    path: '/search',
    query: { keyword: searchKeyword.value }
  });
};

const showUserPanel = ref(false);
let hideTimer = null;

const userCountInfo = ref({});
const loadUserCountInfo = async () => {
  const userId = loginStore.userInfo?.userId;
  if (!userId) {
    userCountInfo.value = {};
    return;
  }
  let result = await proxy.request({
    url: proxy.Api.uHomeGetUserInfo,
    params: { userId }
  });
  if (!result) {
    return;
  }
  userCountInfo.value = {
    focusCount: result.data?.focusCount || 0,
    fansCount: result.data?.fansCount || 0
  };
};

// 头像基础样式
const getAvatarStyle = () => {
  return {
    width: '38px',
    height: '38px',
    borderRadius: '50%',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    cursor: 'pointer',
    transition: 'transform 0.3s cubic-bezier(0.2, 0, 0, 1)',
    position: 'relative',
    zIndex: 10003, // 确保悬浮时在最顶层
  };
};

const getAvatarUrl = () => {
  const avatar = loginStore.userInfo.avatar;
  if (!avatar) return '';
  if (avatar.startsWith('http')) {
    return avatar;
  }
  return `/api/files/${avatar}`; 
};

const getAvatarText = () => {
  const nickname = loginStore.userInfo.nickName || loginStore.userInfo.nickId;
  if (!nickname) return '用';
  return nickname.charAt(0).toUpperCase();
};

const handleMouseEnter = () => {
  if (hideTimer) {
    clearTimeout(hideTimer);
    hideTimer = null;
  }
  showUserPanel.value = true;
  if (Object.keys(loginStore.userInfo).length > 0) {
    loadUserCountInfo();
  }
};

const handleMouseLeave = () => {
  hideTimer = setTimeout(() => {
    showUserPanel.value = false;
  }, 300);
};

const login = () => {
  loginStore.setLogin(true);
};

const logout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', { type: 'warning' });
  } catch {
    return;
  }
  await proxy.request({
    url: proxy.Api.logout,
  });
  loginStore.clearUserInfo();
  showUserPanel.value = false;
  router.push('/');
};

const navJump = (url) => {
  if (Object.keys(loginStore.userInfo).length == 0) {
    loginStore.setLogin(true);
    return;
  }
  window.open(url, "_blank");
};
</script>

<style lang="scss" scoped>
.header-bar {
  width: 100%;
  height: 64px;
  padding: 0px 24px;
  display: flex; /* 改用 Flex 布局 */
  justify-content: space-between;
  align-items: center;
  position: fixed;
  top: 0;
  left: 0;
  z-index: 9999;
  box-sizing: border-box;
  transition: all 0.3s;

  .menu {
    flex: 1;
    display: flex;
    align-items: center;
    .logo-link {
      display: flex;
      align-items: center;
      text-decoration: none;
      font-size: 16px;
      font-weight: 500;
      
      .icon-logo {
        font-size: 28px;
        margin-right: 8px;
        color: #00aeec; /* B站蓝 */
      }
    }
  }

  .search-body {
    flex: 1;
    display: flex;
    justify-content: center;
    
    .search-panel {
      width: 100%;
      max-width: 500px; /* 限制搜索框最大宽度 */
      
      .input-panel {
        display: flex;
        align-items: center;
        background: #f1f2f3; /* B站搜索框底色 */
        border: 1px solid #e3e5e7;
        border-radius: 8px;
        overflow: hidden;
        transition: background-color 0.3s, border-color 0.3s;

        &:hover {
          background: #ffffff;
        }
        &:focus-within {
          background: #ffffff;
          border-color: #c9ccd0;
        }

        input {
          flex: 1;
          border: none;
          background: transparent;
          padding: 0 16px;
          height: 38px;
          font-size: 14px;
          color: #18191c;
          
          &:focus {
            outline: none;
          }
          &::placeholder {
            color: #9499a0;
          }
        }

        .search-btn {
          width: 40px;
          height: 32px;
          margin-right: 4px;
          display: flex;
          align-items: center;
          justify-content: center;
          cursor: pointer;
          border-radius: 6px;
          transition: background-color 0.3s;

          .iconfont {
            font-size: 18px;
            color: #18191c;
          }

          &:hover {
            background: #e3e5e7;
          }
        }
      }
    }
  }

  .user-panel {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: flex-end;
    gap: 16px; /* 统一间距 */

    .user-avatar-wrap {
      position: relative;
      height: 64px; /* 撑满头部高度，防止鼠标移出丢失 hover */
      display: flex;
      align-items: center;

      .custom-avatar {
        background: #e3e5e7;
        border: 2px solid transparent; /* 预留边框位置，防止抖动 */
        
        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
          border-radius: 50%;
        }

        .avatar-text {
          font-size: 16px;
          font-weight: 500;
          color: #e74c3c;
        }

        /* 🔥 B站精髓：头像悬停放大并平移到底层面板上方 */
        &.custom-avatar-hover {
          transform: scale(2) translateY(10px) !important;
          border-color: #ffffff; /* 放大后加上白边与背景区分 */
        }
      }

      .login-avatar {
        width: 38px;
        height: 38px;
        border-radius: 50%;
        background: #00aeec;
        display: flex;
        align-items: center;
        justify-content: center;
        cursor: pointer;
        transition: background 0.3s;
        
        .login-text {
          color: white;
          font-size: 13px;
          font-weight: 500;
        }

        &:hover {
          background: #00b5e5;
        }
      }

      .custom-user-panel {
        position: absolute;
        top: 64px; /* 紧贴头部下方 */
        right: -80px; /* 居中对齐头像的视觉修正 */
        width: 280px;
        background: #ffffff;
        border-radius: 8px;
        padding: 40px 20px 20px; /* 顶部留出空间给下沉的头像 */
        box-shadow: 0 3px 12px rgba(0, 0, 0, 0.1);
        border: 1px solid #e3e5e7;
        z-index: 10001;

        /* B站平滑显示动画 */
        opacity: 0;
        visibility: hidden;
        transform: translateY(-10px);
        transition: opacity 0.3s, transform 0.3s, visibility 0.3s;

        &.custom-panel-visible {
          opacity: 1;
          visibility: visible;
          transform: translateY(0);
        }

        .custom-nick-name {
          font-size: 16px;
          font-weight: 500;
          text-align: center;
          color: #fb7299; /* 昵称用 B站粉 */
          margin-bottom: 16px;
        }

        .custom-count-info {
          display: flex;
          justify-content: space-between;
          margin-bottom: 16px;
          padding: 0 10px;

          .custom-count-item {
            text-align: center;
            cursor: pointer;

            &:hover .custom-count-value {
              color: #00aeec;
            }

            .custom-count-value {
              font-size: 16px;
              font-weight: bold;
              color: #18191c;
              margin-bottom: 4px;
              transition: color 0.3s;
            }

            .custom-count-title {
              font-size: 12px;
              color: #9499a0;
            }
          }
        }

        .menu-list {
          border-top: 1px solid #e3e5e7;
          border-bottom: 1px solid #e3e5e7;
          padding: 10px 0;
          margin-bottom: 10px;

          .custom-menu-item {
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 10px;
            color: #61666d;
            text-decoration: none;
            border-radius: 6px;
            font-size: 14px;
            transition: background-color 0.3s;

            .menu-left {
              display: flex;
              align-items: center;
              .iconfont {
                margin-right: 12px;
                font-size: 16px;
                color: #9499a0;
              }
            }

            &:hover {
              background: #f1f2f3;
              color: #18191c;
            }
          }
        }

        .custom-logout {
          display: flex;
          align-items: center;
          padding: 10px;
          color: #61666d;
          font-size: 14px;
          cursor: pointer;
          border-radius: 6px;
          transition: background-color 0.3s;

          .iconfont {
            margin-right: 12px;
            font-size: 16px;
          }

          &:hover {
            background: #f1f2f3;
          }
        }
      }
    }

    .user-panel-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      cursor: pointer;
      color: #61666d;
      transition: color 0.3s;

      .iconfont {
        font-size: 20px;
        margin-bottom: 2px;
      }

      .item-text {
        font-size: 12px;
      }

      &:hover {
        color: #00aeec; /* 悬停变蓝 */
      }
    }

    .btn-upload {
      margin-left: 10px;
      
      .upload-btn {
        background-color: #fb7299; /* B站主题粉 */
        color: white;
        border: none;
        border-radius: 8px;
        padding: 0 20px;
        height: 38px;
        font-size: 14px;
        display: flex;
        align-items: center;
        transition: background-color 0.3s;

        .iconfont {
          margin-right: 6px;
          font-size: 16px;
        }

        &:hover {
          background-color: #ff85a9;
        }
      }
    }
  }
}

/* 主题颜色控制 */
.header-bar-light {
  background-color: #ffffff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);

  .logo-link {
    color: #18191c;
  }
}

.header-bar-transparent {
  background-color: transparent !important;
  box-shadow: none !important;
}

.header-bar-dark {
  background-color: #18191c;
  border-bottom: 1px solid #2f3134;

  .logo-link, .user-panel-item, .item-text {
    color: #9499a0;
  }
  
  .search-panel .input-panel {
    background-color: #2f3134;
    border-color: #2f3134;
    
    input {
      color: #e3e5e7;
    }
    .search-btn .iconfont {
      color: #9499a0;
    }
  }

  .custom-user-panel {
    background-color: #2f3134 !important;
    border-color: #404040 !important;

    .custom-count-item .custom-count-value {
      color: #e3e5e7 !important;
    }
    .custom-menu-item, .custom-logout {
      color: #e3e5e7 !important;
      &:hover {
        background-color: #404040 !important;
      }
    }
    .menu-list {
      border-color: #404040 !important;
    }
  }
}
</style>
