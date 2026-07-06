<template>
  <div class="history-page">
    <div class="page-header">
      <div class="title">播放历史</div>
      <el-button v-if="historyList.length > 0" type="danger" size="small" @click="handleCleanAll">
        清空历史
      </el-button>
    </div>
    
    <div class="history-content" v-loading="loading">
      <div 
        v-for="item in historyList" 
        :key="item.videoId" 
        class="history-item"
      >
        <div class="video-cover" @click="$router.push('/video/' + item.videoId)">
          <img :src="item.cover" alt="" />
          <div class="progress-bar">
            <div class="progress" :style="{ width: (item.progress / item.duration * 100) + '%' }"></div>
          </div>
        </div>
        <div class="video-info">
          <div class="video-title">{{ item.videoName }}</div>
          <div class="video-meta">
            <span class="play-time">看到 {{ formatTime(item.progress) }}</span>
            <span class="watch-time">{{ formatDate(item.createTime) }}</span>
          </div>
        </div>
        <div class="delete-btn" @click="handleDelete(item.videoId)">
          <span class="iconfont icon-delete"></span>
        </div>
      </div>
      
      <div v-if="historyList.length === 0 && !loading" class="empty-tip">
        <div class="empty-icon">📺</div>
        <div>还没有播放记录</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, getCurrentInstance } from 'vue'

const { proxy } = getCurrentInstance()

const historyList = ref([])
const loading = ref(false)

const loadHistory = async () => {
  loading.value = true
  let result = await proxy.request({
    url: proxy.Api.playHisotry,
    params: {
      pageNo: 1,
      pageSize: 50
    }
  })
  loading.value = false
  
  if (result) {
    historyList.value = result.data?.list || []
  }
}

const handleDelete = async (videoId) => {
  try {
    await proxy.Confirm.warning('确定删除这条播放记录吗？')
    let result = await proxy.request({
      url: proxy.Api.delHistory,
      params: {
        videoId
      }
    })
    if (result) {
      const index = historyList.value.findIndex(h => h.videoId === videoId)
      if (index > -1) {
        historyList.value.splice(index, 1)
      }
      proxy.Message.success('删除成功')
    }
  } catch (e) {}
}

const handleCleanAll = async () => {
  try {
    await proxy.Confirm.warning('确定清空所有播放记录吗？此操作不可恢复！')
    let result = await proxy.request({
      url: proxy.Api.cleanHistory
    })
    if (result) {
      historyList.value = []
      proxy.Message.success('已清空')
    }
  } catch (e) {}
}

const formatTime = (seconds) => {
  if (!seconds) return '00:00'
  const m = Math.floor(seconds / 60)
  const s = Math.floor(seconds % 60)
  return `${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`
}

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleDateString()
}

onMounted(() => {
  loadHistory()
})
</script>

<style scoped>
.history-page {
  padding: 20px;
  background: #ffffff;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.03);
  min-height: 800px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.title {
  font-size: 20px;
  font-weight: 500;
}

.history-content {
  min-height: 300px;
}

.history-item {
  display: flex;
  gap: 15px;
  padding: 15px;
  border-bottom: 1px solid #e3e5e7;
}

.history-item:hover {
  background: #f6f7f8;
}

.video-cover {
  width: 160px;
  height: 90px;
  border-radius: 4px;
  overflow: hidden;
  position: relative;
  cursor: pointer;
}

.video-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.progress-bar {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: rgba(0, 0, 0, 0.3);
}

.progress {
  height: 100%;
  background: #00a1d6;
}

.video-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.video-title {
  font-size: 15px;
  margin-bottom: 8px;
  cursor: pointer;
}

.video-title:hover {
  color: #00a1d6;
}

.video-meta {
  font-size: 13px;
  color: #9499a0;
}

.play-time {
  margin-right: 15px;
}

.delete-btn {
  display: flex;
  align-items: center;
  cursor: pointer;
  color: #9499a0;
  padding: 10px;
}

.delete-btn:hover {
  color: #ff6699;
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