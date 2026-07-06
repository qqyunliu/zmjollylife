<template>
  <div class="collection-page">
    <div class="page-header">
      <div class="title">我的收藏</div>
    </div>
    
    <div class="collection-content" v-loading="loading">
      <div class="video-grid">
        <div 
          v-for="item in collectionList" 
          :key="item.videoId" 
          class="video-item"
        >
          <VideoItem :data="item" />
          <div class="collect-time">
            收藏于 {{ formatDate(item.createTime) }}
          </div>
          <div class="delete-btn" @click="handleDelete(item.videoId)">
            <span class="iconfont icon-delete"></span> 取消收藏
          </div>
        </div>
      </div>
      
      <div v-if="collectionList.length === 0 && !loading" class="empty-tip">
        <div class="empty-icon">❤️</div>
        <div>还没有收藏任何视频</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, getCurrentInstance } from 'vue'
import VideoItem from '@/components/VideoItem.vue'

const { proxy } = getCurrentInstance()

const collectionList = ref([])
const loading = ref(false)

const loadCollection = async () => {
  loading.value = true
  let result = await proxy.request({
    url: proxy.Api.uHomeLoadUserCollection,
    params: {
      pageNo: 1,
      pageSize: 50
    }
  })
  loading.value = false
  
  if (result) {
    collectionList.value = result.data?.list || []
  }
}

const handleDelete = async (videoId) => {
  try {
    await proxy.Confirm.warning('确定取消收藏吗？')
    let result = await proxy.request({
      url: proxy.Api.userAction,
      params: {
        videoId,
        actionType: 3
      }
    })
    if (result) {
      const index = collectionList.value.findIndex(c => c.videoId === videoId)
      if (index > -1) {
        collectionList.value.splice(index, 1)
      }
      proxy.Message.success('已取消收藏')
    }
  } catch (e) {}
}

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleDateString()
}

onMounted(() => {
  loadCollection()
})
</script>

<style scoped>
.collection-page {
  padding: 20px;
  background: #ffffff;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.03);
  min-height: 800px;
}

.page-header {
  margin-bottom: 20px;
}

.title {
  font-size: 20px;
  font-weight: 500;
}

.collection-content {
  min-height: 300px;
}

.video-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 20px;
}

.video-item {
  position: relative;
}

.collect-time {
  font-size: 12px;
  color: #9499a0;
  margin-top: 5px;
}

.delete-btn {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  cursor: pointer;
  transition: opacity 0.3s;
  color: #fff;
}

.video-item:hover .delete-btn {
  opacity: 1;
}

.empty-tip {
  text-align: center;
  padding: 60px;
  color: #9499a0;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 15px;
}
</style>