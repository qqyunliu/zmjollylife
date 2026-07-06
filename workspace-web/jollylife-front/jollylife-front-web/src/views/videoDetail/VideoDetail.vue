<template>
  <div class="video-detail" v-loading="loading">
    <div v-if="!exists" class="video-not-found">
      <el-empty description="该视频不存在或已被删除" />
    </div>
    <template v-else>
    <div class="video-header">
      <div class="video-title">
        <h1 class="title">{{ videoInfo.videoName }}</h1>
        <div class="video-info">
          <span class="info-item play-count">
            <svg class="eye-icon" viewBox="0 0 24 24" aria-hidden="true">
              <path d="M12 5c5.5 0 10 4.5 10 7s-4.5 7-10 7S2 14.5 2 12s4.5-7 10-7zm0 2C7.6 7 4.1 10.2 4.1 12S7.6 17 12 17s7.9-3.2 7.9-5S16.4 7 12 7zm0 2.2A2.8 2.8 0 1 1 12 14.8a2.8 2.8 0 0 1 0-5.6zm0 1.6a1.2 1.2 0 1 0 0 2.4 1.2 1.2 0 0 0 0-2.4z" />
            </svg>
            <span>{{ videoInfo.playCount || 0 }}</span>
          </span>
          <span class="info-item time">{{ videoInfo.createTime }}</span>
        </div>
      </div>
      
      <div class="video-user-info">
        <div class="avatar-wrap">
          <Avatar :userId="userInfo.userInfo?.userId" :avatar="userInfo.userInfo?.avatar" :nickId="userInfo.userInfo?.nickId" :linkable="false"></Avatar>
        </div>
        <div class="user-info">
          <div class="user-name-wrap">
            <span class="nick-name">{{ userInfo.userInfo?.nickId }}</span>
          </div>
          <div class="introduction" :title="userInfo.userInfo?.personIntroduction">
            {{ userInfo.userInfo?.personIntroduction || "这个人很懒，什么都没有留下~" }}
          </div>
          <div class="op-btns">
            <div class="focus-btn">
              <el-dropdown v-if="userInfo.haveFocus && !isAuthor" trigger="click">
                <el-button class="btn followed-btn" type="info">
                  <span class="iconfont icon-list"></span>已关注 {{ userInfo.fansCount }}
                </el-button>
                <template #dropdown-menu>
                  <el-dropdown-item @click="focusUser(-1)">取消关注</el-dropdown-item>
                </template>
              </el-dropdown>
              <el-button class="btn follow-btn"
                         type="primary"
                         @click="focusUser(1)"
                         v-else-if="!isAuthor">
                <span class="plus">+</span> 关注 {{ userInfo.fansCount }}
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="video-body">
      <div class="video-left">
        <div class="video-panel"
             :style="{position:wideScreen? 'absolute':'relative'}">
          <Player ref="playerRef"
                  @changeWideScreen="changeWideScreenHandler"></Player>
          <DanmuPlayer
            ref="danmuPlayerRef"
            v-if="videoInfo.videoId"
            :videoId="videoInfo.videoId"
            :fileId="videoInfo.fileId"
            :danmuShow="danmuShow"
          ></DanmuPlayer>
        </div>
        <div class="danmu-input-bar" v-if="danmuShow">
          <el-input
            v-model="danmuContent"
            placeholder="发送弹幕..."
            maxlength="50"
            @keyup.enter="sendDanmu"
          >
            <template #append>
              <el-button @click="sendDanmu" :disabled="!danmuContent">发送</el-button>
            </template>
          </el-input>
        </div>
        <div class="action-bar">
          <div class="action-item" :class="{ active: videoInfo.haveLike }" @click="handleLike">
            <svg class="action-icon" viewBox="0 0 24 24" aria-hidden="true">
              <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 6 4 4 6.5 4c1.74 0 3.41.81 4.5 2.09C12.09 4.81 13.76 4 15.5 4 18 4 20 6 20 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z" />
            </svg>
            <span class="text">{{ videoInfo.likeCount > 0 ? videoInfo.likeCount : '点赞' }}</span>
          </div>
          <div class="action-item" @click="handleShare">
            <svg class="action-icon" viewBox="0 0 24 24" aria-hidden="true">
              <path d="M18 16.08c-.76 0-1.44.3-1.96.77L8.91 12.7a2.7 2.7 0 0 0 0-1.39l7.02-4.11A2.99 2.99 0 1 0 15 5a3 3 0 0 0 .07.65L8.05 9.76A3 3 0 1 0 8 14.24l7.02 4.11c-.04.2-.07.42-.07.65a3 3 0 1 0 3-2.92z" />
            </svg>
            <span class="text">分享</span>
          </div>
        </div>
        <CommentList v-if="videoInfo.videoId" :videoId="videoInfo.videoId"></CommentList>
      </div>

      <div class="video-right">
        <VideoPList></VideoPList>
      </div>
    </div>
    </template>
  </div>
</template>

<style scoped>
.video-not-found {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
}
</style>

<script setup>
import VideoPList from "./VideoPList.vue"
import Player from "@/components/Player.vue"
import DanmuPlayer from "@/components/DanmuPlayer.vue"
import CommentList from "@/components/CommentList.vue"
import { ref, reactive, computed, getCurrentInstance, nextTick, onMounted, onUnmounted } from "vue";
const { proxy } = getCurrentInstance();
import { useRoute, useRouter } from 'vue-router';
const route = useRoute();
const router = useRouter();
import { useNavAction } from "@/stores/navActionStore";
const navActionStore = useNavAction();

import { useLoginStore } from "../../stores/loginStore";
const loginStore = useLoginStore();
const userInfo = ref({});

const isAuthor = computed(() => {
  return loginStore.userInfo?.userId && userInfo.value?.userInfo?.userId &&
         loginStore.userInfo.userId === userInfo.value.userInfo.userId;
});

const getUserInfo = async (userId) => {
  let result = await proxy.request({
    url: proxy.Api.uHomeGetUserInfo,
    params: {
      userId,
    }
  })
  if (!result) {
    return;
  }
  userInfo.value = result.data;
}

const focusUser = async (changeCount) => {
  if (Object.keys(loginStore.userInfo).length === 0) {
    loginStore.setLogin(true);
    return;
  }
  // 修正了原代码中 Request 大写的问题，保持规范
  let result = await proxy.request({
    url: changeCount === 1 ? proxy.Api.uHomeFocus : proxy.Api.uHomeCancelFocus,
    params: {
      focusUserId: userInfo.value.userInfo?.userId,
    }
  });
  if (!result) {
    return;
  }
  if (changeCount === 1) {
    userInfo.value.haveFocus = true;
    userInfo.value.fansCount++;
  } else {
    userInfo.value.haveFocus = false;
    userInfo.value.fansCount--;
  }
}

const videoInfo = ref({})
const loading = ref(true)
const exists = ref(true)
const getVideoInfo = async () => {
  loading.value = true
  let result = await proxy.request({
    url: proxy.Api.getVideoInfo,
    params: {
      videoId: route.params.videoId
    }
  })
  loading.value = false
  if (!result || !result.data || !result.data.videoInfo) {
    exists.value = false
    return;
  }
  const resultData = result.data.videoInfo;
  getUserInfo(resultData.userId);
  videoInfo.value = resultData;
}

getVideoInfo();

const handleLike = async () => {
  if (Object.keys(loginStore.userInfo).length === 0) {
    loginStore.setLogin(true);
    return;
  }
  const actionType = videoInfo.value.haveLike ? 2 : 0;
  const result = await proxy.request({
    url: proxy.Api.userAction,
    params: {
      videoId: videoInfo.value.videoId,
      actionType
    }
  });
  if (result) {
    const newCount = videoInfo.value.haveLike ? videoInfo.value.likeCount - 1 : videoInfo.value.likeCount + 1;
    videoInfo.value.haveLike = !videoInfo.value.haveLike;
    videoInfo.value.likeCount = newCount;
    proxy.Message.success(videoInfo.value.haveLike ? '点赞成功' : '已取消点赞');
  }
}

const handleShare = () => {
  const url = window.location.href;
  if (navigator.clipboard) {
    navigator.clipboard.writeText(url).then(() => {
      proxy.Message.success('链接已复制到剪贴板');
    });
  } else {
    const input = document.createElement('input');
    input.value = url;
    document.body.appendChild(input);
    input.select();
    document.execCommand('copy');
    document.body.removeChild(input);
    proxy.Message.success('链接已复制到剪贴板');
  }
}

onMounted(() => {
  nextTick(() => {
    navActionStore.setShowHeader(false);
    navActionStore.setFixedHeader(true);
    navActionStore.setFixedCategory(false);
    navActionStore.setShowCategory(false);
    navActionStore.setForceFixedHeader(true);
  })
})

onUnmounted(() => {
  navActionStore.setShowHeader(true);
  navActionStore.setFixedHeader(true);
  navActionStore.setFixedCategory(true);
  navActionStore.setShowCategory(true);
  navActionStore.setForceFixedHeader(false);
})

const wideScreen = ref(false);
const danmuShow = ref(true);
const danmuContent = ref('');
const danmuPlayerRef = ref(null);

const sendDanmu = async () => {
  if (!danmuContent.value) {
    proxy.Message.warning('请输入弹幕内容')
    return
  }
  let result = await proxy.request({
    url: proxy.Api.postDanmu,
    params: {
      videoId: videoInfo.value.videoId,
      fileId: videoInfo.value.fileId,
      content: danmuContent.value,
      color: '#ffffff',
      time: 0
    }
  })
  if (result) {
    proxy.Message.success('弹幕发送成功')
    danmuContent.value = ''
    danmuPlayerRef.value?.loadDanmu()
  }
}

const changeWideScreenHandler = (result) => {
  wideScreen.value = result;
}
</script>

<style lang="scss" scoped>
.video-detail {
  margin-top: 64px;
  min-height: calc(100vh - 64px);
  max-width: 1400px; /* B站通常有最大宽度限制 */
  margin-left: auto;
  margin-right: auto;
  padding: 0 20px;

  .video-header {
    padding-top: 24px;
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 20px;

    .video-title {
      flex: 1;
      padding-right: 30px;

      .title {
        font-size: 22px;
        font-weight: 500;
        color: #18191c;
        line-height: 1.4;
        margin: 0 0 10px 0;
        word-break: break-all;
      }

      .video-info {
        display: flex;
        align-items: center;
        color: #9499a0;
        font-size: 13px;

        .info-item {
          display: flex;
          align-items: center;
          margin-right: 16px;

          .iconfont {
            font-size: 16px;
            margin-right: 4px;
          }

          .eye-icon {
            width: 16px;
            height: 16px;
            fill: currentColor;
            margin-right: 4px;
          }
        }
      }
    }

    .video-user-info {
      width: 410px;
      display: flex;
      align-items: stretch;
      
      .avatar-wrap {
        margin-right: 12px;
        flex-shrink: 0;
      }

      .user-info {
        flex: 1;
        display: flex;
        flex-direction: column;
        justify-content: space-between;

        .user-name-wrap {
          display: flex;
          align-items: center;
          
          .nick-name {
            font-size: 15px;
            font-weight: 500;
            color: #fb7299; /* B站UP主名字常用粉色 */
            text-decoration: none;
            margin-right: 10px;
          }

        }

        .introduction {
          color: #9499a0;
          font-size: 13px;
          text-overflow: ellipsis;
          white-space: nowrap;
          overflow: hidden;
          width: 280px; /* 防止过长挤压布局 */
          margin: 4px 0;
        }

        .op-btns {
          margin-top: 4px;

          .focus-btn {
            width: 100%;

            .el-dropdown {
              width: 100%;
            }

            .btn {
              width: 100%;
              height: 32px;
              border-radius: 6px;
              font-size: 14px;
              transition: all 0.3s;
              border: none;
            }

            .follow-btn {
              background-color: #00aeec; /* B站经典蓝 */
              color: #fff;
              &:hover {
                background-color: #00b5e5;
              }
              .plus {
                font-weight: bold;
                margin-right: 4px;
              }
            }

            .followed-btn {
              background-color: #e3e5e7; /* 已关注灰色 */
              color: #9499a0;
              &:hover {
                background-color: #f1f2f3;
              }
            }
          }
        }
      }
    }
  }

  .video-body {
    position: relative;
    display: flex;
    gap: 30px; /* 使用现代的 gap 控制列间距 */

    .video-left {
      flex: 1;
      min-width: 0;
      position: relative;
    }

    .video-panel {
      position: relative;
      width: 100%;
      border-radius: 6px;
      overflow: hidden;
      background-color: #000;
    }

    .danmu-input-bar {
      margin-top: 10px;
      padding: 10px;
      background: #f1f2f3;
      border-radius: 6px;
    }

    .danmu-input-bar :deep(.el-input-group__append) {
      background-color: #00aeec;
      border-color: #00aeec;
    }

    .danmu-input-bar :deep(.el-input-group__append .el-button) {
      color: #fff;
    }

    .danmu-input-bar :deep(.el-input-group__append .el-button:disabled) {
      background-color: #ccc;
      border-color: #ccc;
    }

    .video-right {
      width: 410px;
      flex-shrink: 0;
      padding-bottom: 20px;
    }
  }

  .action-bar {
    display: flex;
    align-items: center;
    gap: 15px;
    margin-top: 15px;

    .action-item {
      display: flex;
      align-items: center;
      gap: 6px;
      padding: 8px 16px;
      background: #f1f2f3;
      border-radius: 6px;
      cursor: pointer;
      transition: all 0.2s;
      color: #9499a0;

      .action-icon {
        width: 18px;
        height: 18px;
        fill: currentColor;
      }

      .text {
        font-size: 14px;
        color: inherit;
      }

      &:hover {
        background: #e3e5e7;
        color: #00aeec;
      }

      &.active {
        color: #ff85a9;
      }
    }
  }
}
</style>
