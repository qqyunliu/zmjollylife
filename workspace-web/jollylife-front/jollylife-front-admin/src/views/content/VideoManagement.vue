<template>
  <div class="video-management-container">
    <div class="management-header">
      <h2>视频管理</h2>
      <div class="management-stats">
        <div class="stat-item">
          <span class="stat-label">视频总数</span>
          <span class="stat-value">{{ stats.total }}</span>
        </div>
        <div class="stat-item">
          <span class="stat-label">推荐视频</span>
          <span class="stat-value">{{ stats.recommended }}</span>
        </div>
      </div>
    </div>

    <div class="management-content">
      <div class="filter-section">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索视频名称或用户昵称"
          style="width: 300px; margin-right: 10px"
          @keyup.enter="getVideoList"
        >
          <template #append>
            <el-button @click="getVideoList">搜索</el-button>
          </template>
        </el-input>
      </div>

      <el-table :data="videoList" style="width: 100%" v-loading="loading">
        <el-table-column prop="videoId" label="视频ID" min-width="12%" />
        <el-table-column prop="videoName" label="视频名称" min-width="20%" show-overflow-tooltip />
        <el-table-column prop="nickName" label="用户昵称" min-width="12%">
          <template #default="scope">
            <span>{{ scope.row.nickName || scope.row.userId || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="playCount" label="播放量" min-width="8%">
          <template #default="scope">
            <span>{{ scope.row.playCount || 0 }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="likeCount" label="点赞数" min-width="8%">
          <template #default="scope">
            <span>{{ scope.row.likeCount || 0 }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="上传时间" min-width="15%">
          <template #default="scope">
            <span>{{ scope.row.createTime || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="推荐状态" min-width="10%">
          <template #default="scope">
            <el-tag :type="scope.row.recommendType === 1 ? 'success' : 'info'">
              {{ scope.row.recommendType === 1 ? '已推荐' : '未推荐' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="15%">
          <template #default="scope">
            <el-button
              :type="scope.row.recommendType === 1 ? 'warning' : 'success'"
              size="small"
              @click="toggleRecommend(scope.row)"
            >
              {{ scope.row.recommendType === 1 ? '取消推荐' : '设为推荐' }}
            </el-button>
            <el-button
              type="danger"
              size="small"
              @click="deleteVideo(scope.row)"
            >
              删除
            </el-button>
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
  </div>
</template>

<script>
import { ref, onMounted, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Api, videoManagementListApi, videoManagementRecommendApi, videoManagementDeleteApi, videoManagementStatsApi } from '@/utils/Api'

export default {
  name: 'VideoManagement',
  setup() {
    const videoList = ref([])
    const total = ref(0)
    const searchKeyword = ref('')
    const loading = ref(false)

    const queryParams = reactive({
      pageNo: 1,
      pageSize: 10
    })

    const stats = reactive({
      total: 0,
      recommended: 0
    })

    const getVideoList = async () => {
      loading.value = true
      try {
        const params = {
          pageNo: queryParams.pageNo,
          pageSize: queryParams.pageSize,
        }
        if (searchKeyword.value && searchKeyword.value.trim()) {
          params.videoNameFuzzy = searchKeyword.value.trim()
          params.nickNameFuzzy = searchKeyword.value.trim()
        }
        const response = await videoManagementListApi(params)
        if (response.code === 200) {
          videoList.value = response.data.list || []
          total.value = response.data.totalCount || 0
        } else {
          ElMessage.error(response.message || '获取列表失败')
        }
      } catch (error) {
        ElMessage.error('网络错误，请稍后重试')
        console.error('获取视频列表失败:', error)
      } finally {
        loading.value = false
      }
    }

    const getStats = async () => {
      try {
        const response = await videoManagementStatsApi()
        if (response.code === 200) {
          stats.total = response.data.total || 0
          stats.recommended = response.data.recommended || 0
        }
      } catch (error) {
        console.error('获取统计数据失败:', error)
      }
    }

    const toggleRecommend = async (video) => {
      const action = video.recommendType === 1 ? 0 : 1
      const actionText = video.recommendType === 1 ? '取消推荐' : '设为推荐'
      try {
        await ElMessageBox.confirm(
          `确定要${actionText}该视频吗？`,
          '提示',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
          }
        )
        const response = await videoManagementRecommendApi({
          videoId: video.videoId,
          recommendType: action
        })
        if (response.code === 200) {
          ElMessage.success(actionText + '成功')
          video.recommendType = action
          getStats()
        } else {
          ElMessage.error(response.message || actionText + '失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error(actionText + '失败:', error)
        }
      }
    }

    const deleteVideo = async (video) => {
      try {
        await ElMessageBox.confirm(
          `确定要删除视频"${video.videoName}"吗？删除后用户将无法看到该视频，且不会收到任何通知。`,
          '删除确认',
          {
            confirmButtonText: '确定删除',
            cancelButtonText: '取消',
            type: 'warning',
          }
        )
        const response = await videoManagementDeleteApi({
          videoId: video.videoId
        })
        if (response.code === 200) {
          ElMessage.success('删除成功')
          getVideoList()
          getStats()
        } else {
          ElMessage.error(response.message || '删除失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除视频失败:', error)
        }
      }
    }

    const handleSizeChange = (size) => {
      queryParams.pageSize = size
      getVideoList()
    }

    const handleCurrentChange = (current) => {
      queryParams.pageNo = current
      getVideoList()
    }

    onMounted(() => {
      getVideoList()
      getStats()
    })

    return {
      Api,
      videoList,
      total,
      searchKeyword,
      queryParams,
      stats,
      loading,
      getVideoList,
      toggleRecommend,
      deleteVideo,
      handleSizeChange,
      handleCurrentChange
    }
  }
}
</script>

<style scoped>
.video-management-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 64px);
}

.management-header {
  margin-bottom: 20px;
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.management-header h2 {
  margin: 0 0 20px 0;
  color: #303133;
  font-size: 18px;
}

.management-stats {
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

.management-content {
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
</style>
