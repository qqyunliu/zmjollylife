<template>
  <div class="management-container">
    <div class="management-header">
      <h2>用户管理</h2>
    </div>

    <div class="management-content">
      <div class="filter-section">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索昵称或邮箱"
          style="width: 300px; margin-right: 10px"
          @keyup.enter="getList"
        >
          <template #append>
            <el-button @click="getList">搜索</el-button>
          </template>
        </el-input>
      </div>

      <el-table :data="list" style="width: 100%" v-loading="loading">
        <el-table-column prop="userId" label="用户ID" min-width="10%" />
        <el-table-column prop="nickId" label="昵称" min-width="12%" show-overflow-tooltip />
        <el-table-column prop="email" label="邮箱" min-width="18%" show-overflow-tooltip />
        <el-table-column prop="joinTime" label="注册时间" min-width="14%" />
        <el-table-column prop="lastLoginTime" label="最后登录" min-width="14%" />
        <el-table-column label="状态" min-width="8%">
          <template #default="scope">
            <el-tag v-if="scope.row.status === 1" type="success">正常</el-tag>
            <el-tag v-else-if="scope.row.status === 0" type="warning">已禁用</el-tag>
            <el-tag v-else-if="scope.row.status === -1" type="danger">已注销</el-tag>
            <el-tag v-else type="info">{{ scope.row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="24%">
          <template #default="scope">
            <el-button
              v-if="scope.row.status === 1"
              type="warning"
              size="small"
              @click="changeStatus(scope.row, 0, '禁用该用户')"
            >
              禁止登录
            </el-button>
            <el-button
              v-if="scope.row.status === 0"
              type="success"
              size="small"
              @click="changeStatus(scope.row, 1, '启用该用户')"
            >
              启用
            </el-button>
            <el-button
              v-if="scope.row.status !== -1"
              type="danger"
              size="small"
              @click="changeStatus(scope.row, -1, '注销该用户')"
            >
              注销
            </el-button>
            <span v-if="scope.row.status === -1" style="color: #909399;">-</span>
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
import { loadUserApi, changeUserStatusApi } from '@/utils/Api'

export default {
  name: 'UserManagement',
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
          params.nickIdFuzzy = searchKeyword.value.trim()
          params.emailFuzzy = searchKeyword.value.trim()
        }
        const response = await loadUserApi(params)
        if (response.code === 200) {
          list.value = response.data.list || []
          total.value = response.data.totalCount || 0
        } else {
          ElMessage.error(response.info || '获取列表失败')
        }
      } catch (error) {
        ElMessage.error('网络错误，请稍后重试')
        console.error('获取用户列表失败:', error)
      } finally {
        loading.value = false
      }
    }

    const changeStatus = async (row, status, actionText) => {
      try {
        await ElMessageBox.confirm(
          `确定要${actionText}吗？`,
          '操作确认',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
          }
        )
        const response = await changeUserStatusApi({
          userId: row.userId,
          status: status
        })
        if (response.code === 200) {
          ElMessage.success('操作成功')
          getList()
        } else {
          ElMessage.error(response.info || '操作失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('修改用户状态失败:', error)
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
      changeStatus,
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
