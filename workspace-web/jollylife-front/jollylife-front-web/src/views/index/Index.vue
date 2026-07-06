<template>
  <div class="commend-panel">
    <div class="top-panel">
      <div class="carousel-panel"
           :style="{
             width: carouselWidth + 'px',
             height: carouselWidth * 0.6 + 'px',
           }">

        <div v-if="carouselVideoList.length === 0"
             class="empty-state">
          <span class="loading-text">正在加载推荐视频...</span>
        </div>

        <el-carousel v-else
                     :height="carouselWidth * 0.6 + 'px'"
                     indicator-position="none"
                     arrow="never"
                     @change="carouselChange"
                     ref="carouselRef">
          <el-carousel-item v-for="(item, index) in carouselVideoList"
                            :key="item?.videoId || index"
                            :name="index + ''">
            <div class="roll-image">
              <router-link v-if="item?.videoId"
                           :to="`/video/${item.videoId}`"
                           target="_blank">
                <img :src="`${proxy.Api.sourcePath}${item.videoCover}`"
                     :alt="item.videoName || '视频封面'" />
              </router-link>
            </div>
          </el-carousel-item>
        </el-carousel>

        <div v-if="carouselVideoList.length > 0 && carouselVideoList[carouselIndex]"
             class="carousel-bottom">
          <div class="name-op">
            <router-link class="video-name"
                         :to="'/video/' + carouselVideoList[carouselIndex].videoId"
                         target="_blank"
                         :title="carouselVideoList[carouselIndex].videoName">
              {{ carouselVideoList[carouselIndex].videoName }}
            </router-link>

            <div class="change-btn">
              <div class="btn-item" @click="preCarousel">
                <svg class="nav-icon" viewBox="0 0 24 24" aria-hidden="true">
                  <path d="M15.5 4.5a1 1 0 0 1 0 1.4L9.4 12l6.1 6.1a1 1 0 1 1-1.4 1.4l-6.8-6.8a1 1 0 0 1 0-1.4l6.8-6.8a1 1 0 0 1 1.4 0z" />
                </svg>
              </div>
              <div class="btn-item" @click="nextCarousel">
                <svg class="nav-icon" viewBox="0 0 24 24" aria-hidden="true">
                  <path d="M8.5 4.5a1 1 0 0 0 0 1.4L14.6 12l-6.1 6.1a1 1 0 1 0 1.4 1.4l6.8-6.8a1 1 0 0 0 0-1.4L9.9 4.5a1 1 0 0 0-1.4 0z" />
                </svg>
              </div>
            </div>
          </div>

          <div class="dtos">
            <div v-for="(item, idx) in carouselVideoList.length"
                 :key="idx"
                 :class="['dto-item', carouselIndex === idx ? 'active' : '']"
                 @click="setCarousel(idx)"></div>
          </div>
        </div>
      </div>

      <div class="side-video-list">
        <div v-for="item in sideVideoList"
             :key="item.videoId"
             class="video-item-wrapper">
          <videoItem :data="item"></videoItem>
        </div>
      </div>
    </div>

    <div class="video-list" ref="videoListRef">
      <div v-for="item in bottomVideoList"
           :key="item.videoId"
           class="video-item-wrapper">
        <videoItem :data="item"></videoItem>
      </div>
      <div v-if="loading" class="loading-tip">加载中...</div>
      <div v-if="noMore" class="no-more-tip">没有更多视频了</div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, getCurrentInstance, nextTick, onMounted, onUnmounted } from "vue";
const { proxy } = getCurrentInstance();
import { useRoute, useRouter } from "vue-router";
const route = useRoute();
const router = useRouter();

const carouselMaxCount = proxy.carouselMaxCount;
const carouselVideoList = ref([]);
const commendVideoList = ref([]);
const sideMaxCount = 6;
const sideVideoList = computed(() => commendVideoList.value.slice(0, sideMaxCount));
const bottomVideoList = computed(() => commendVideoList.value.slice(sideMaxCount));

const pageNo = ref(1);
const pageSize = ref(12);
const loading = ref(false);
const noMore = ref(false);
const videoListRef = ref(null);

const loadRecommendVideo = async (append = false) => {
  if (loading.value) return;
  loading.value = true;

  let result = await proxy.request({
    url: proxy.Api.loadRecommendVideo,
    params: {
      pageNo: pageNo.value,
      pageSize: pageSize.value
    }
  });

  loading.value = false;

  if (!result) {
    console.error("API返回为空", result);
    return;
  }

  const data = result.data;
  console.log("API返回数据:", result, "data:", data);

  if (data) {
    const list = data.list || [];
    if (list.length === 0) {
      console.warn("视频列表为空");
      noMore.value = true;
      return;
    }
    const carouselList = list.slice(0, carouselMaxCount);
    const recommendList = list.slice(carouselMaxCount);

    if (carouselList.length > 0) {
      carouselVideoList.value = carouselList;
    }

    if (append) {
      commendVideoList.value = [...commendVideoList.value, ...recommendList];
    } else {
      commendVideoList.value = recommendList;
    }

    if (data.pageNo >= data.pageTotal) {
      noMore.value = true;
    }
  }
};

const loadMore = () => {
  if (noMore.value || loading.value) return;
  pageNo.value++;
  loadRecommendVideo(true);
};

const handleScroll = () => {
  const scrollTop = window.scrollY;
  const clientHeight = window.innerHeight;
  const scrollHeight = document.documentElement.scrollHeight;

  if (scrollHeight - scrollTop - clientHeight < 100) {
    loadMore();
  }
};

loadRecommendVideo();

const carouselWidth = ref();
const carouselIndex = ref(0);

const carouselChange = (e) => {
  carouselIndex.value = e;
}

const resetCarouselWidth = () => {
  let width = (document.documentElement.clientWidth - proxy.bodyPadding * 2) * 0.4218;
  if (width < 400) {
    width = 400;
  }
  carouselWidth.value = width;
};

const carouselRef = ref();

const preCarousel = () => {
  carouselRef.value.prev();
};

const nextCarousel = () => {
  carouselRef.value.next();
};

const setCarousel = (index) => {
  carouselRef.value.setActiveItem(index - 1 + '');
};

onMounted(() => {
  resetCarouselWidth();
  window.addEventListener('scroll', handleScroll);
});

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll);
});
</script>

<style lang="scss" scoped>
.commend-panel {
  display: flex;
  flex-direction: column;
  margin-top: 20px;
  gap: 20px;

  .top-panel {
    display: flex;
    gap: 20px;
    align-items: flex-start;
  }

  .video-item-wrapper {
    width: 100%;
    height: 100%;
    overflow: hidden;
  }

  .carousel-panel {
    border-radius: 8px;
    overflow: hidden;
    position: relative;
    background-color: #f1f2f3;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
    transition: transform 0.3s ease;

    .empty-state {
      width: 100%;
      height: 100%;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #999;
      font-size: 14px;
    }

    .roll-image {
      position: relative;
      width: 100%;
      height: 100%;
      a {
        display: block;
        width: 100%;
        height: 100%;
      }
      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
    }

    &:hover .change-btn {
      opacity: 1 !important;
    }

    .carousel-bottom {
      position: absolute;
      bottom: 0px;
      left: 0;
      width: 100%;
      height: 80px;
      background: linear-gradient(to top, rgba(0, 0, 0, 0.85) 0%, rgba(0, 0, 0, 0.4) 50%, transparent 100%);
      padding: 30px 15px 10px 15px;
      display: flex;
      flex-direction: column;
      justify-content: flex-end;
      pointer-events: none;

      .name-op {
        display: flex;
        justify-content: space-between;
        align-items: center;
        pointer-events: auto;

        .video-name {
          flex: 1;
          color: #ffffff;
          text-overflow: ellipsis;
          overflow: hidden;
          white-space: nowrap;
          text-decoration: none;
          display: inline-block;
          font-size: 18px;
          font-weight: 500;
          text-shadow: 0 1px 2px rgba(0,0,0,0.5);
          transition: color 0.2s;

          &:hover {
            color: #00aeec;
          }
        }

        .change-btn {
          margin-left: 15px;
          display: flex;
          gap: 10px;
          opacity: 0;
          transition: opacity 0.3s ease;

          .btn-item {
            width: 28px;
            height: 28px;
            display: flex;
            align-items: center;
            justify-content: center;
            background-color: rgba(255, 255, 255, 0.2);
            backdrop-filter: blur(4px);
            border-radius: 8px;
            cursor: pointer;
            transition: all 0.2s;
            color: #ffffff;

            .nav-icon {
              width: 16px;
              height: 16px;
              fill: currentColor;
              filter: drop-shadow(0 1px 2px rgba(0, 0, 0, 0.5));
            }

            &:hover {
              background-color: rgba(255, 255, 255, 0.4);
              transform: scale(1.05);
              color: #00aeec;
            }
          }
        }
      }

      .dtos {
        display: flex;
        margin-top: 8px;
        align-items: center;
        pointer-events: auto;

        .dto-item {
          width: 8px;
          height: 8px;
          border-radius: 50%;
          background: rgba(255, 255, 255, 0.4);
          cursor: pointer;
          margin-right: 8px;
          transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);

          &:hover {
            background: rgba(255, 255, 255, 0.8);
          }
        }

        .active {
          width: 24px;
          border-radius: 4px;
          background: #ffffff;
        }
      }
    }
  }

  .side-video-list {
    flex: 1;
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 20px;
    align-content: start;
  }

  .video-list {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 20px;

    .loading-tip, .no-more-tip {
      grid-column: 1 / -1;
      text-align: center;
      padding: 20px;
      color: #9499a0;
    }
  }
}
</style>
