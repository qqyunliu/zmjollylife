<template>
  <div class="video-list">
    <div v-for="item in videoList" :key="item.videoId" class="video-item-wrapper">
      <videoItem :data="item"></videoItem>
    </div>
    <div v-if="videoList.length === 0 && !loading" class="empty-tip">
      <div>暂无视频</div>
    </div>
    <div v-if="loading" class="loading-tip">
      <span>加载中...</span>
    </div>
  </div>
</template>

<script setup>
import { ref, getCurrentInstance, onMounted, watch, computed } from "vue";
const { proxy } = getCurrentInstance();
import { useRoute } from "vue-router";
const route = useRoute();

import { useCategoryStore } from "../../stores/categoryStore";
const categoryStore = useCategoryStore();

const videoList = ref([]);
const loading = ref(false);
const pageNo = ref(1);
const categoryIdInfo = ref({});

const hasCategoryData = computed(() => {
  return categoryStore.categoryMap && Object.keys(categoryStore.categoryMap).length > 0;
});

const convertCode2Id = (pCategoryCode, categoryCode) => {
  let pCategoryId = null;
  let categoryId = null;
  if (pCategoryCode && hasCategoryData.value) {
    const cate = categoryStore.categoryMap[pCategoryCode];
    if (cate && cate.categoryId) {
      pCategoryId = cate.categoryId;
    }
  }
  if (categoryCode && hasCategoryData.value) {
    const cate = categoryStore.categoryMap[categoryCode];
    if (cate && cate.categoryId) {
      categoryId = cate.categoryId;
    }
  }
  categoryIdInfo.value = {
    pCategoryId,
    categoryId,
  };
};

const loadVideoList = async () => {
  loading.value = true;
  let params = {
    pageNo: pageNo.value,
  };
  if (categoryIdInfo.value.pCategoryId) {
    params.pCategoryId = categoryIdInfo.value.pCategoryId;
  }
  if (categoryIdInfo.value.categoryId) {
    params.categoryId = categoryIdInfo.value.categoryId;
  }
  let result = await proxy.request({
    url: proxy.Api.loadVideo,
    params,
  });
  loading.value = false;
  if (!result) {
    return;
  }
  if (result.data) {
    if (pageNo.value > 1) {
      videoList.value = videoList.value.concat(result.data.list || []);
    } else {
      videoList.value = result.data.list || [];
    }
  }
};

const initData = () => {
  if (!hasCategoryData.value) {
    setTimeout(initData, 100);
    return;
  }
  convertCode2Id(route.params.pCategoryCode, route.params.categoryCode);
  pageNo.value = 1;
  loadVideoList();
};

watch(() => route.params.pCategoryCode, () => {
  initData();
});

watch(() => route.params.categoryCode, () => {
  initData();
});

onMounted(() => {
  initData();
});
</script>

<style lang="scss" scoped>
.video-list {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 20px;
  padding: 20px;
}

.video-item-wrapper {
  width: 100%;
}

.empty-tip, .loading-tip {
  grid-column: 1 / -1;
  text-align: center;
  padding: 40px;
  color: #9499a0;
}
</style>