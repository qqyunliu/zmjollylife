<template>
  <div class="danmu-management">
    <div class="page-header">
      <div class="title">弹幕管理</div>
    </div>

    <div class="filter-bar">
      <el-input 
        v-model="keyword" 
        placeholder="搜索弹幕内容" 
        clearable 
        style="width: 300px"
        @keyup.enter="loadDanmuList"
      >
        <template #append>
          <el-button icon="Search" @click="loadDanmuList" />
        </template>
      </el-input>
    </div>

    <div class="danmu-list" v-loading="loading">
      <template v-if="danmuList.length > 0">
        <div class="danmu-item" v-for="item in danmuList" :key="item.danmuId">
          <div class="danmu-content">{{ item.content }}</div>
          <div class="danmu-video">
            所属视频：<span class="video-name" @click="viewVideo(item.videoId)">{{ item.videoName }}</span>
          </div>
          <div class="danmu-time">
            <span>发布时间：{{ item.createTime }}</span>
            <span class="danmu-timepoint">{{ formatTime(item.timePoint) }}</span>
          </div>
          <div class="danmu-actions">
            <el-button type="danger" size="small" @click="deleteDanmu(item.danmuId)">删除</el-button>
          </div>
        </div>
      </template>
      <div v-else class="empty-tip">
        <div>暂无弹幕</div>
      </div>
    </div>

    <div class="pagination" v-if="total > 0">
      <el-pagination
        background
        layout="total, prev, pager, next"
        :total="total"
        :page-size="pageSize"
        v-model:current-page="pageNo"
        @current-change="loadDanmuList"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, getCurrentInstance } from 'vue'

const { proxy } = getCurrentInstance()

const loading = ref(false)
const danmuList = ref([])
const pageNo = ref(1)
const pageSize = ref(10)
const total = ref(0)
const keyword = ref('')

const formatTime = (seconds) => {
  if (!seconds) return '00:00'
  const m = Math.floor(seconds / 60)
  const s = Math.floor(seconds % 60)
  return `${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`
}

const loadDanmuList = async () => {
  loading.value = true
  let result = await proxy.request({
    url: proxy.Api.ucLoadDanmu,
    params: {
      pageNo: pageNo.value,
      pageSize: pageSize.value,
      keyword: keyword.value
    }
  })
  loading.value = false
  if (result) {
    danmuList.value = result.data?.list || []
    total.value = result.data?.totalCount || 0
  }
}

const deleteDanmu = async (danmuId) => {
  try {
    await proxy.Confirm.warning('确定删除这条弹幕吗？')
    let result = await proxy.request({
      url: proxy.Api.ucDelDanmu,
      params: { danmuId }
    })
    if (result) {
      proxy.Message.success('删除成功')
      loadDanmuList()
    }
  } catch (e) {}
}

const viewVideo = (videoId) => {
  window.open('/video/' + videoId, '_blank')
}

onMounted(() => {
  loadDanmuList()
})
</script>

<style scoped>
.danmu-management {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.title {
  font-size: 20px;
  font-weight: 500;
}

.filter-bar {
  margin-bottom: 20px;
}

.danmu-list {
  min-height: 400px;
}

.danmu-item {
  padding: 15px;
  background: #fff;
  border-radius: 8px;
  margin-bottom: 15px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
}

.danmu-content {
  font-size: 14px;
  color: #18191c;
  margin-bottom: 10px;
  padding: 8px 12px;
  background: #f6f7fb;
  border-radius: 4px;
}

.danmu-video {
  font-size: 13px;
  color: #9499a0;
  margin-bottom: 8px;
}

.video-name {
  color: #00a1d6;
  cursor: pointer;
}

.video-name:hover {
  text-decoration: underline;
}

.danmu-time {
  font-size: 13px;
  color: #9499a0;
  margin-bottom: 10px;
}

.danmu-timepoint {
  margin-left: 15px;
  color: #00a1d6;
}

.danmu-actions {
  text-align: right;
}

.empty-tip {
  text-align: center;
  padding: 60px;
  color: #9499a0;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>
