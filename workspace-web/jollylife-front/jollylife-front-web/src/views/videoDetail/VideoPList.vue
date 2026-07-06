<template>
  <div class="video-panel" v-if="videoList.length > 1">
    <div class="video-panel-title">
      <div class="title">
        视频选集
        <span class="video-count">({{ currentP }}/{{ videoList.length }})</span>
      </div>
      <el-switch v-model="autoPlay"
                 active-color="#00aeec" 
                 inactive-text="自动连播" />
    </div>
    
    <el-scrollbar :max-height="450"
                  class="video-list">
      <div :class="['video-item', index === currentP - 1 ? 'active' : '']"
           v-for="(item, index) in videoList"
           :key="index"
           @click="selectVideo(index + 1)">
           
        <div class="item-left">
          <div class="playing" v-if="index === currentP - 1"></div>
          <div class="part-name" :title="item.fileName">
            <span class="part-index">P{{ index + 1 }}</span>
            <span class="text">{{ item.fileName }}</span>
          </div>
        </div>
        
        <div class="duration">
          {{ proxy.Utils.convertSecondsToHMS(item.duration) }}
        </div>
      </div>
    </el-scrollbar>
  </div>
</template>

<script setup>
import { ref, reactive, getCurrentInstance, nextTick } from "vue";
const { proxy } = getCurrentInstance();
import { useRoute, useRouter } from "vue-router";
import mitter from "../../eventbus/eventBus";
const route = useRoute();
const router = useRouter();

const autoPlay = ref(true);
const currentP = ref(route.query.p ? Number.parseInt(route.query.p) : 1);
const videoList = ref([]);

const loadVideoPlist = async () => {
  let result = await proxy.request({
    url: proxy.Api.loadVideoPList,
    params: {
      videoId: route.params.videoId
    }
  });
  if (!result) {
    return;
  }
  videoList.value = result.data;
  selectVideoFile();
};

loadVideoPlist();

const selectVideo = (index) => {
  currentP.value = index;
  router.push({
    path: route.path,
    query: {
      p: index
    }
  })
  selectVideoFile();
}

const selectVideoFile = () => {
  mitter.emit("changeF", videoList.value[currentP.value - 1].fileId)
}
</script>

<style lang="scss" scoped>
.video-panel {
  background: #f1f2f3;
  border-radius: 6px;
  padding: 16px;
  color: #18191c;

  .video-panel-title {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;

    .title {
      font-size: 16px;
      font-weight: 500;

      .video-count {
        font-size: 13px;
        color: #9499a0;
        margin-left: 5px;
        font-weight: normal;
      }
    }
    
    /* 覆盖 el-switch 的部分样式以贴合 B 站 */
    :deep(.el-switch__label) {
      color: #61666d;
      font-size: 13px;
    }
  }

  .video-list {
    margin-right: -8px; /* 给滚动条留出空间 */
    padding-right: 8px;

    .video-item {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 0 10px;
      height: 40px;
      cursor: pointer;
      margin-bottom: 6px;
      border-radius: 4px;
      transition: background-color 0.2s, color 0.2s;

      .item-left {
        display: flex;
        align-items: center;
        flex: 1;
        overflow: hidden;

        .playing {
          width: 14px;
          height: 14px;
          margin-right: 8px;
          flex-shrink: 0;
          background-position: center center;
          background-size: cover;
          background-repeat: no-repeat;
          background-image: url('@/assets/playing.gif'); /* 确保你的动态图路径正确 */
        }

        .part-name {
          flex: 1;
          display: flex;
          align-items: center;
          overflow: hidden;
          white-space: nowrap;
          text-overflow: ellipsis;
          font-size: 14px;

          .part-index {
            margin-right: 8px;
            color: #9499a0;
            font-size: 13px;
            flex-shrink: 0;
          }

          .text {
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
          }
        }
      }

      .duration {
        margin-left: 15px;
        font-size: 13px;
        color: #9499a0;
        flex-shrink: 0;
      }

      &:hover {
        background: #e3e5e7;
        .part-name .text {
          color: #00aeec;
        }
      }
    }

    /* B 站当前播放项的高亮样式 */
    .active {
      background: #fff;
      box-shadow: 0 0 0 1px #00aeec inset; /* 内发光代替边框，不导致抖动 */
      
      .part-name .text, .part-index {
        color: #00aeec !important;
      }
      .duration {
        color: #00aeec;
      }
      &:hover {
        background: #fff; /* active 状态下 hover 不改变底色 */
      }
    }
  }
}
</style>