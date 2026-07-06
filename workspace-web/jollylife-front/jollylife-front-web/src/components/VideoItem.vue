<template>
  <div :class="['video-item',layoutType==1? 'video-item2' : '']"
       :style="{'margin-top':marginTop+'px'}">
    <router-link :to="`/video/${data.videoId}`"
                 target="_blank">
      <div class="cover">
        <Cover :source="data.videoCover"></Cover>
        <div class="shade">
          <div class="play-count"
               v-show="false">
            <div class="iconfont icon-danmu">{{data.danmuCount || 0}}</div>
          </div>
          <div class="play-time">{{data.playTime}}</div>
        </div>
      </div>
    </router-link>
    <div class="video-info">
      <div class="title-row">
        <router-link class="title"
                     :to="`/video/${data.videoId}`"
                     v-html="data.videoName"
                     target="_blank">
        </router-link>
        <div class="play-count">
          <svg class="eye-icon" viewBox="0 0 24 24" aria-hidden="true">
            <path d="M12 5c5.5 0 10 4.5 10 7s-4.5 7-10 7S2 14.5 2 12s4.5-7 10-7zm0 2C7.6 7 4.1 10.2 4.1 12S7.6 17 12 17s7.9-3.2 7.9-5S16.4 7 12 7zm0 2.2A2.8 2.8 0 1 1 12 14.8a2.8 2.8 0 0 1 0-5.6zm0 1.6a1.2 1.2 0 1 0 0 2.4 1.2 1.2 0 0 0 0-2.4z" />
          </svg>
          <span>{{ data.playCount || 0 }}</span>
        </div>
      </div>
      <router-link class="user-name"
                   :to="`/user/${data.userId}`"
                   target="_blank">
        <span class="iconfont icon-upzhu">{{data.nickName}} · </span>
        <span>{{proxy.Utils.formatDate(data.createTime)}}</span>
      </router-link>
      <div class="play-count"
           v-show="false">
        <div class="iconfont icon-danmu">{{data.dankuCount || 0}}</div>
      </div>
    </div>
  </div>
</template>    

<script setup>

import { ref, reactive, getCurrentInstance, nextTick, onMounted } from "vue";
const { proxy } = getCurrentInstance();
import { useRoute, useRouter } from "vue-router";
const route = useRoute();
const router = useRouter();

const props = defineProps({
  data: {
    type: Object,
    default: {},
  },
  layoutType: {
    type: Number,
    default: 0,
  },
  marginTop: {
    type: Number,
    default: 0,
  }
});

</script>

<style  lang="scss" scoped>
.video-item {
  width: 100%;
  overflow: hidden;
  .cover {
    cursor: pointer;
    position: relative;
    overflow: hidden;
    .image-style {
      width: 100%;
      height: 100%;
      overflow: hidden;
      border-radius: 5px;
    }
    .shade {
      position: absolute;
      bottom: 0;
      left: 0;
      z-index: 1;
      box-sizing: border-box;
      padding: 8px 8px 6px;
      width: 100%;
      height: 38px;
      border-bottom-right-radius: 6px;
      border-bottom-left-radius: 6px;
      background-image: linear-gradient(
        180deg,
        rgba(0, 0, 0, 0) 0%,
        rgba(0, 0, 0, 0) 100%
      );
      color: #fff;
      opacity: 1;
      display: -webkit-flex;
      display: flex;
      align-items: center;
      justify-content: space-between;
      .play-count {
        display: flex;
        .iconfont {
          font-size: 13px;
          &::before {
            font-size: 16px;
            margin-right: 2px;
          }
        }
        .icon-danmu {
          margin-left: 15px;
        }
      }
    }
  }
  .video-info {
    cursor: pointer;
    .title-row {
      display: flex;
      align-items: flex-start;
      gap: 8px;
    }
    .title {
      height: 40px;
      color: var(--text2);
      font-size: 14px;
      margin-top: 10px;
      display: -webkit-box;
      overflow: hidden;
      text-decoration: none;
      -webkit-box-orient: vertical;
      text-overflow: -o-ellipsis-lastline;
      text-overflow: ellipsis;
      word-break: break-word !important;
      word-break: break-all;
      line-break: anywhere;
      -webkit-line-clamp: 2;
      cursor: pointer;
      flex: 1;
      &:hover {
        color: var(--blue);
      }
      :deep(.hightlight) {
        color: red !important;
      }
    }
    .play-count {
      margin-top: 10px;
      display: inline-flex;
      align-items: center;
      gap: 4px;
      font-size: 12px;
      color: #9499a0;
      white-space: nowrap;
      flex-shrink: 0;
      .eye-icon {
        width: 14px;
        height: 14px;
        fill: currentColor;
      }
    }
    .user-name {
      margin-top: 5px;
      color: #d8d2d2;
      font-size: 13px;
      cursor: pointer;
      text-decoration: none;
      &:hover {
        color: var(--blue);
      }
      .iconfont {
        &::before {
          font-size: 18px;
          margin-right: 3px;
          float: left;
        }
        font-size: 13px;
      }
    }
  }
}
.video-item2 {
  display: flex;
  .cover {
    width: 190px;
    .shade {
      justify-content: end;
    }
  }
  .video-info {
    flex: 1;
    margin-left: 15px;
    .title {
      margin-top: 0px;
    }
    .play-count {
      display: flex;
      width: 100%;
      margin-top: 5px;
      color: #d8d2d2;
      .iconfont {
        font-size: 14px;
        &::before {
          font-size: 16px;
          margin-right: 2px;
        }
      }
      .icon-danmu {
        margin-top: 15px;
      }
    }
  }
}
</style>
