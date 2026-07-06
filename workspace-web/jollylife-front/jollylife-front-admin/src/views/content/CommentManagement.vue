<template>
  <div class="management-container">
    <div class="management-header">
      <h2>评论管理</h2>
    </div>

    <div class="management-content">
      <div class="filter-section">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索评论内容"
          style="width: 300px; margin-right: 10px"
          @keyup.enter="getList"
        >
          <template #append>
            <el-button @click="getList">搜索</el-button>
          </template>
        </el-input>
      </div>

      <el-table :data="list" style="width: 100%" v-loading="loading">
        <el-table-column prop="commentId" label="评论ID" min-width="8%" />
        <el-table-column prop="content" label="评论内容" min-width="25%" show-overflow-tooltip />
        <el-table-column prop="nickId" label="发布者" min-width="12%">
          <template #default="scope">
            <span>{{ scope.row.nickId || scope.row.userId || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="videoName" label="所属视频" min-width="15%" show-overflow-tooltip>
          <template #default="scope">
            <span>{{ scope.row.videoName || scope.row.videoId || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="postTime" label="发布时间" min-width="15%">
          <template #default="scope">
            <span>{{ scope.row.postTime || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" min-width="8%">
          <template #default="scope">
            <el-tag :type="scope.row.status === -1 ? 'danger' : 'success'">
              {{ scope.row.status === -1 ? '已删除' : '正常' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="10%">
          <template #default="scope">
            <el-button
              type="danger"
              size="small"
              :disabled="scope.row.status === -1"
              @click="deleteItem(scope.row)"
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
import { loadCommentApi, delCommentApi } from '@/utils/Api'

export default {
  name: 'CommentManagement',
  setup() {
    const list = ref([])
    const total = ref(0)
    const searchKeyword = ref('')
    const loading = ref(false)

    const queryParams = reactive({
      pageNo: 1,
      pageSize: 10
    })

    const getList = async () => {
      loading.value = true
      try {
        const params = {
          pageNo: queryParams.pageNo,
          pageSize: queryParams.pageSize,
        }
        if (searchKeyword.value && searchKeyword.value.trim()) {
          params.content = searchKeyword.value.trim()
        }
        const response = await loadCommentApi(params)
        if (response.code === 200) {
          list.value = response.data.list || []
          total.value = response.data.totalCount || 0
        } else {
          ElMessage.error(response.info || '获取列表失败')
        }
      } catch (error) {
        ElMessage.error('网络错误，请稍后重试')
        console.error('获取评论列表失败:', error)
      } finally {
        loading.value = false
      }
    }

    const deleteItem = async (row) => {
      try {
        await ElMessageBox.confirm(
          `确定要删除该评论吗？删除后用户将无法看到。`,
          '删除确认',
          {
            confirmButtonText: '确定删除',
            cancelButtonText: '取消',
            type: 'warning',
          }
        )
        const response = await delCommentApi({ commentId: row.commentId })
        if (response.code === 200) {
          ElMessage.success('删除成功')
          getList()
        } else {
          ElMessage.error(response.info || '删除失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除评论失败:', error)
        }
      }
    }

    const handleSizeChange = (size) => {
      queryParams.pageSize = size
      getList()
    }

    const handleCurrentChange = (current) => {
      queryParams.pageNo = current
      getList()
    }

    onMounted(() => {
      getList()
    })

    return {
      list,
      total,
      searchKeyword,
      queryParams,
      loading,
      getList,
      deleteItem,
      handleSizeChange,
      handleCurrentChange
    }
  }
}
</script>

<style scoped>
.management-container {
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
  margin: 0;
  color: #303133;
  font-size: 18px;
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
