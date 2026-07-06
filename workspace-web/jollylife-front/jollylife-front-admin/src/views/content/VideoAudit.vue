<template>
  <div class="video-audit-container">
    <div class="audit-header">
      <h2>视频审核管理</h2>
      <div class="audit-stats">
        <div class="stat-item">
          <span class="stat-label">待审核</span>
          <span class="stat-value">{{ stats.pending }}</span>
        </div>
        <div class="stat-item">
          <span class="stat-label">审核通过</span>
          <span class="stat-value">{{ stats.passed }}</span>
        </div>
        <div class="stat-item">
          <span class="stat-label">审核不通过</span>
          <span class="stat-value">{{ stats.rejected }}</span>
        </div>
        <div class="stat-item">
          <span class="stat-label">待人工复核</span>
          <span class="stat-value">{{ stats.review }}</span>
        </div>
      </div>
    </div>

    <div class="audit-content">
      <div class="filter-section">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索视频ID或标题"
          style="width: 300px; margin-right: 10px"
          @keyup.enter="getReviewList"
        >
          <template #append>
            <el-button @click="getReviewList">搜索</el-button>
          </template>
        </el-input>
      </div>

      <el-table :data="videoList" style="width: 100%">
        <el-table-column prop="videoId" label="视频ID" min-width="25%" />
        <el-table-column prop="videoName" label="视频名称" min-width="35%" show-overflow-tooltip>
          <template #default="scope">
            <span>{{ scope.row.videoName }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="20%">
          <template #default="scope">
            <el-button
              type="primary"
              size="small"
              @click="viewDetail(scope.row)"
            >
              详情
            </el-button>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="视频状态" min-width="20%">
          <template #default="scope">
            <el-tag
              :type="getStatusType(scope.row.status)"
            >
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          :current-page="queryParams.pageNo"
          :page-size="queryParams.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <el-dialog
      v-model="detailDialogVisible"
      title="视频详情"
      width="900px"
      destroy-on-close
    >
      <div v-loading="detailLoading" class="detail-body" v-if="detailVideo">
        <div class="detail-main">
          <div class="player-panel">
            <div ref="playerContainer" class="player"></div>
          </div>
          <div class="info-panel">
            <div class="info-row">
              <span class="info-label">标题：</span>
              <span class="info-value">{{ detailVideo.videoName || '-' }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">简介：</span>
              <span class="info-value">{{ detailVideo.introduction || '-' }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">作者：</span>
              <span class="info-value">
                <span v-if="detailVideo.avatar" class="author-avatar">
                  <img :src="Api.sourcePath + detailVideo.avatar" />
                </span>
                {{ detailVideo.nickName || detailVideo.userId || '-' }}
              </span>
            </div>
            <div class="info-row">
              <span class="info-label">视频ID：</span>
              <span class="info-value">{{ detailVideo.videoId }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">封面：</span>
              <span class="info-value">
                <img
                  v-if="detailVideo.videoCover"
                  class="cover-img"
                  :src="Api.sourcePath + detailVideo.videoCover"
                />
                <span v-else>-</span>
              </span>
            </div>
            <div class="info-row">
              <span class="info-label">当前状态：</span>
              <span class="info-value">
                <el-tag :type="getStatusType(detailVideo.status)">
                  {{ getStatusText(detailVideo.status) }}
                </el-tag>
              </span>
            </div>
            <div class="info-row" v-if="detailVideo.reviewReason">
              <span class="info-label">拒绝原因：</span>
              <span class="info-value reject-reason">{{ detailVideo.reviewReason }}</span>
            </div>
          </div>
        </div>

        <div class="audit-action">
          <div class="audit-title">审核操作</div>
          <el-input
            v-model="rejectReason"
            type="textarea"
            :rows="3"
            :maxlength="500"
            show-word-limit
            :disabled="!canAudit"
            placeholder="如选择审核不通过,请填写原因"
          />
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
          <el-button type="danger" :loading="submitting" :disabled="!canAudit" @click="submitAudit('reject')">审核不通过</el-button>
          <el-button type="success" :loading="submitting" :disabled="!canAudit" @click="submitAudit('pass')">审核通过</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, onMounted, reactive, computed, nextTick, onBeforeUnmount, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Api, reviewListApi, reviewVideoApi, auditStatsApi, reviewDetailApi } from '@/utils/Api'
import Artplayer from 'artplayer'
import Hls from 'hls.js'

export default {
  name: 'VideoAudit',
  setup() {
    const videoList = ref([])
    const total = ref(0)
    const searchKeyword = ref('')
    const rejectReason = ref('')
    const submitting = ref(false)
    const detailDialogVisible = ref(false)
    const detailLoading = ref(false)
    const detailVideo = ref(null)
    const playFileId = ref(null)
    const playerContainer = ref(null)
    let art = null
    
    const queryParams = reactive({
      pageNo: 1,
      pageSize: 10
    })
    
    const stats = reactive({
      pending: 0,
      passed: 0,
      rejected: 0,
      review: 0
    })
    
    // 获取复核列表
    const getReviewList = async () => {
      try {
        const params = {
          pageNo: queryParams.pageNo,
          pageSize: queryParams.pageSize,
        }
        if (searchKeyword.value && searchKeyword.value.trim()) {
          params.videoIdFuzzy = searchKeyword.value.trim()
          params.videoNameFuzzy = searchKeyword.value.trim()
        }
        const response = await reviewListApi(params)
        if (response.code === 200) {
          videoList.value = response.data.list
          total.value = response.data.totalCount
        } else {
          ElMessage.error(response.message || '获取列表失败')
        }
      } catch (error) {
        ElMessage.error('网络错误，请稍后重试')
        console.error('获取复核列表失败:', error)
      }
    }
    
    // 获取统计数据
    const getStats = async () => {
      try {
        const response = await auditStatsApi()
        if (response.code === 200) {
          stats.pending = response.data.pending
          stats.passed = response.data.passed
          stats.rejected = response.data.rejected
          stats.review = response.data.review
        } else {
          ElMessage.error(response.message || '获取统计数据失败')
        }
      } catch (error) {
        ElMessage.error('网络错误，请稍后重试')
        console.error('获取统计数据失败:', error)
      }
    }
    
    // 处理分页大小变化
    const handleSizeChange = (size) => {
      queryParams.pageSize = size
      getReviewList()
    }
    
    // 处理页码变化
    const handleCurrentChange = (current) => {
      queryParams.pageNo = current
      getReviewList()
    }
    
    // 是否允许审核:STATUS2(待审核) 或 STATUS5(待人工复核)
    const canAudit = computed(() => {
      const s = Number(detailVideo.value?.status)
      return s === 2 || s === 5
    })

    // 提交审核(pass/reject)
    const submitAudit = async (action) => {
      if (!detailVideo.value) return
      if (action === 'reject' && !rejectReason.value.trim()) {
        ElMessage.warning('请填写审核不通过的原因')
        return
      }

      submitting.value = true
      try {
        const response = await reviewVideoApi({
          videoId: detailVideo.value.videoId,
          action,
          reason: action === 'reject' ? rejectReason.value.trim() : ''
        })
        if (response && response.code === 200) {
          ElMessage.success(action === 'pass' ? '已通过' : '已拒绝')
          detailDialogVisible.value = false
          getReviewList()
          getStats()
        } else {
          ElMessage.error(response?.message || '审核失败')
        }
      } catch (error) {
        ElMessage.error('网络错误，请稍后重试')
        console.error('审核失败:', error)
      } finally {
        submitting.value = false
      }
    }

    // 查看详情
    const destroyPlayer = () => {
      if (art) {
        art.destroy(false)
        art = null
      }
    }

    const initPlayer = async () => {
      destroyPlayer()
      if (!playerContainer.value || !playFileId.value) return
      const url = `/api/file/videoResourcePost/${playFileId.value}/index.m3u8`
      art = new Artplayer({
        container: playerContainer.value,
        url,
        autoplay: false,
        muted: false,
        pip: true,
        fullscreen: true,
        aspectRatio: true,
        type: 'm3u8',
        customType: {
          m3u8: (video, url, art) => {
            if (Hls.isSupported()) {
              const hls = new Hls()
              hls.loadSource(url)
              hls.attachMedia(video)
              art.on('destroy', () => {
                hls.destroy()
              })
            } else {
              video.src = url
            }
          }
        }
      })
    }

    const openDetail = async (videoId) => {
      detailDialogVisible.value = true
      detailLoading.value = true
      detailVideo.value = null
      playFileId.value = null
      rejectReason.value = ''
      try {
        const response = await reviewDetailApi({ videoId })
        if (response && response.code === 200) {
          detailVideo.value = response.data.video
          playFileId.value = response.data.playFileId
          await nextTick()
          await initPlayer()
        } else {
          ElMessage.error(response?.message || '获取详情失败')
        }
      } catch (e) {
        ElMessage.error('网络错误，请稍后重试')
      } finally {
        detailLoading.value = false
      }
    }

    const viewDetail = (video) => {
      openDetail(video.videoId)
    }
    
    // 格式化日期
    const formatDate = (dateString) => {
      if (!dateString) return ''
      const date = new Date(dateString)
      return date.toLocaleString('zh-CN')
    }
    
    // 获取状态文本
    const getStatusText = (status) => {
      const s = Number(status)
      switch (s) {
        case 0: return '转码中'
        case 1: return '转码失败'
        case 2: return '待审核'
        case 3: return '审核通过'
        case 4: return '审核不通过'
        case 5: return '待人工复核'
        default: return '未知(' + status + ')'
      }
    }

    // 获取状态类型
    const getStatusType = (status) => {
      const s = Number(status)
      switch (s) {
        case 0: return 'warning'
        case 1: return 'danger'
        case 2: return 'info'
        case 3: return 'success'
        case 4: return 'danger'
        case 5: return 'warning'
        default: return 'info'
      }
    }
    
    // 初始化
    onMounted(() => {
      getReviewList()
      getStats()
    })

    watch(detailDialogVisible, (visible) => {
      if (!visible) {
        destroyPlayer()
        detailVideo.value = null
        playFileId.value = null
      }
    })

    onBeforeUnmount(() => {
      destroyPlayer()
    })
    
    return {
      Api,
      videoList,
      total,
      searchKeyword,
      queryParams,
      stats,
      rejectReason,
      submitting,
      detailDialogVisible,
      detailLoading,
      detailVideo,
      playFileId,
      playerContainer,
      canAudit,
      getReviewList,
      getStats,
      handleSizeChange,
      handleCurrentChange,
      submitAudit,
      viewDetail,
      formatDate,
      getStatusText,
      getStatusType
    }
  }
}
</script>

<style scoped>
.video-audit-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 64px);
}

.audit-header {
  margin-bottom: 20px;
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.audit-header h2 {
  margin: 0 0 20px 0;
  color: #303133;
  font-size: 18px;
}

.audit-stats {
  display: flex;
  gap: 20px;
}

.stat-item {
  flex: 1;
  text-align: center;
  padding: 15px;
  background-color: #f9f9f9;
  border-radius: 6px;
}

.stat-label {
  display: block;
  font-size: 14px;
  color: #606266;
  margin-bottom: 5px;
}

.stat-value {
  display: block;
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
}

.audit-content {
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.filter-section {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}

.text-gray {
  color: #909399;
}

.review-info {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f9f9f9;
  border-radius: 6px;
}

.review-info p {
  margin: 8px 0;
}

.review-action {
  margin-top: 20px;
}

.review-action h4 {
  margin: 0 0 15px 0;
  color: #303133;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.detail-body {
  min-height: 360px;
}

.detail-main {
  display: flex;
  gap: 16px;
}

.player-panel {
  flex: 1;
  min-width: 520px;
}

.player {
  width: 100%;
  height: 320px;
  background: #000;
  border-radius: 6px;
  overflow: hidden;
}

.info-panel {
  width: 320px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.info-row {
  display: flex;
  gap: 6px;
  line-height: 20px;
}

.info-label {
  width: 70px;
  color: #606266;
}

.info-value {
  flex: 1;
  color: #303133;
  word-break: break-all;
}

.cover-img {
  width: 100%;
  border-radius: 6px;
  border: 1px solid #ebeef5;
}

.author-avatar img {
  width: 18px;
  height: 18px;
  border-radius: 50%;
  vertical-align: -4px;
  margin-right: 6px;
}

.audit-tip {
  text-align: center;
  padding: 20px;
  background: #f9f9f9;
  border-radius: 6px;
  margin-top: 10px;
}
</style>
