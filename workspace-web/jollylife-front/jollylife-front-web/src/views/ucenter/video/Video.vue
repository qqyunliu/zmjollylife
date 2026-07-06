<template>
  <div class="video-management">
    <div class="page-header">
      <div class="title">稿件管理</div>
    </div>

    <div class="filter-bar">
      <el-select v-model="status" placeholder="稿件状态" clearable @change="loadVideoList">
        <el-option label="全部" value="" />
        <el-option label="待审核" :value="2" />
        <el-option label="审核成功" :value="3" />
        <el-option label="审核不通过" :value="4" />
      </el-select>
    </div>

    <div class="video-list" v-loading="loading">
      <template v-if="videoList.length > 0">
        <div class="video-item" v-for="item in videoList" :key="item.videoId">
          <div class="video-cover">
            <img :src="proxy.Api.sourcePath + item.videoCover" alt="">
          </div>
          <div class="video-info">
            <div class="video-name">{{ item.videoName }}</div>
            <div class="video-meta">
              <span class="status" :class="'status-' + Number(item.status)">{{ item.statusName }}</span>
              <span class="date">创建于：{{ item.createTime }}</span>
            </div>
            <div class="reject-reason" v-if="Number(item.status) === 4 && item.reviewReason">
              <span class="reason-label">拒绝原因：</span>{{ item.reviewReason }}
            </div>
            <div class="video-stats"></div>
          </div>
          <div class="video-actions">
            <el-button type="danger" size="small" @click="deleteVideo(item.videoId)">删除</el-button>
            <el-button size="small" @click="viewVideo(item.videoId)">查看</el-button>
          </div>
        </div>
      </template>
      <div v-else class="empty-tip">
        <div>暂无稿件</div>
      </div>
    </div>

    <div class="pagination" v-if="total > 0">
      <el-pagination
        background
        layout="total, prev, pager, next"
        :total="total"
        :page-size="pageSize"
        v-model:current-page="pageNo"
        @current-change="loadVideoList"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, getCurrentInstance } from 'vue'
import { useRouter } from 'vue-router'

const { proxy } = getCurrentInstance()
const router = useRouter()

const loading = ref(false)
const videoList = ref([])
const pageNo = ref(1)
const pageSize = ref(10)
const total = ref(0)
const status = ref('')

const loadVideoList = async () => {
  loading.value = true
  let result = await proxy.request({
    url: proxy.Api.loadUcenterVideoList,
    params: {
      pageNo: pageNo.value,
      pageSize: pageSize.value,
      status: status.value
    }
  })
  loading.value = false
  if (result) {
    videoList.value = result.data?.list || []
    total.value = result.data?.totalCount || 0
  }
}

const viewVideo = (videoId) => {
  window.open('/video/' + videoId, '_blank')
}

const deleteVideo = async (videoId) => {
  try {
    await proxy.Confirm.warning('确定要删除这个视频吗？删除后无法恢复。')
    let result = await proxy.request({
      url: proxy.Api.ucDeleteVideo,
      params: { videoId }
    })
    if (result) {
      proxy.Message.success('删除成功')
      if (videoList.value.length === 1 && pageNo.value > 1) {
        pageNo.value--
      }
      loadVideoList()
    }
  } catch (e) {}
}

onMounted(() => {
  loadVideoList()
})
</script>

<style scoped>
.video-management {
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

.video-list {
  min-height: 400px;
}

.video-item {
  display: flex;
  align-items: center;
  padding: 15px;
  background: #fff;
  border-radius: 8px;
  margin-bottom: 15px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
}

.video-cover {
  width: 160px;
  height: 90px;
  border-radius: 4px;
  overflow: hidden;
  flex-shrink: 0;
}

.video-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.video-info {
  flex: 1;
  padding: 0 20px;
}

.video-name {
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.video-meta {
  font-size: 13px;
  color: #9499a0;
  margin-bottom: 8px;
}

.status {
  padding: 2px 8px;
  border-radius: 4px;
  margin-right: 10px;
}

.status-1, .status-0 {
  background: #e6a23c;
  color: #fff;
}

.status-2, .status-5 {
  background: #409eff;
  color: #fff;
}

.status-3 {
  background: #67c23a;
  color: #fff;
}

.status-4 {
  background: #f56c6c;
  color: #fff;
}

.reject-reason {
  font-size: 13px;
  color: #f56c6c;
  margin-top: 8px;
  padding: 8px 12px;
  background: #fef0f0;
  border-radius: 4px;
}

.reason-label {
  font-weight: 500;
}

.video-stats {
  font-size: 13px;
  color: #9499a0;
}

.video-stats span {
  margin-right: 15px;
}

.video-actions {
  display: flex;
  gap: 10px;
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
