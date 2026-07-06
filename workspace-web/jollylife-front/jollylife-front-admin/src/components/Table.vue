<template>
  <div class="table-container"
       :style="{ height: containerHeight }">
    <!-- 表格主体 -->
    <el-table :data="processedDataSource"
              style="width: 100%"
              border
              stripe
              v-loading="loading"
              :height="tableHeight"
              :highlight-current-row="options.highlightCurrentRow"
              @selection-change="handleSelectionChange"
              @current-change="handleCurrentChange"
              @row-click="handleRowClick"
              @sort-change="handleSortChange"
              ref="tableRef">
      <!-- 多选框列 -->
      <el-table-column v-if="options.showSelection"
                       type="selection"
                       width="55"
                       align="center" />

      <!-- 序号列 -->
      <el-table-column v-if="options.showIndex"
                       label="序号"
                       type="index"
                       width="60"
                       align="center" />

      <!-- 动态列 -->
      <el-table-column v-for="column in processedColumns"
                       :key="column.prop || column.label"
                       :prop="column.prop"
                       :label="column.label"
                       :width="column.width"
                       :min-width="column.minWidth"
                       :fixed="column.fixed"
                       :align="column.align || 'center'"
                       :sortable="column.sortable ? 'custom' : false"
                       :formatter="column.formatter">
        <!-- 自定义列内容 -->
        <template v-if="column.scopedSlots"
                  #default="scope">
          <slot :name="column.scopedSlots"
                :row="scope.row"
                :index="scope.$index"
                :column="column" />
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div v-if="showPagination && pagination.total > 0"
         class="pagination-container">
      <el-pagination v-model:current-page="pagination.currentPage"
                     v-model:page-size="pagination.pageSize"
                     :page-sizes="pageSizes"
                     :total="pagination.total"
                     :layout="paginationLayout"
                     @size-change="handleSizeChange"
                     @current-change="handleCurrentPageChange" />
    </div>
  </div>
</template>
<script setup>
import { ref, reactive, computed, onMounted, watch, nextTick } from 'vue'
import { ElMessage } from 'element-plus'

const props = defineProps({
  columns: {
    type: Array,
    required: true,
    default: () => []
  },
  dataSource: {
    type: [Array, Object],
    default: () => ({ list: [] })
  },
  fetch: {
    type: Function,
    default: null
  },
  options: {
    type: Object,
    default: () => ({
      showSelection: false,
      showIndex: false,
      highlightCurrentRow: true, // 默认开启行高亮
      autoHeight: false,
      rowKey: 'id' // 添加默认行键
    })
  },
  showPagination: {
    type: Boolean,
    default: true
  },
  extHeight: {
    type: Number,
    default: 0
  },
  pageSizes: {
    type: Array,
    default: () => [10, 20, 50, 100]
  },
  paginationLayout: {
    type: String,
    default: 'total, sizes, prev, pager, next, jumper'
  }
})

const emit = defineEmits([
  'rowClick',
  'selectionChange',
  'currentChange',
  'sortChange',
  'sizeChange',
  'currentPageChange',
  'fetchSuccess',
  'fetchError'
])

// 响应式数据
const loading = ref(false)
const tableRef = ref(null)
const pagination = reactive({
  currentPage: 1,
  pageSize: props.pageSizes[0] || 10,
  total: 0
})

// 计算属性
const processedColumns = computed(() => {
  return props.columns.map(col => ({
    ...col,
    align: col.align || 'center',
    minWidth: col.minWidth || (col.width ? null : 100)
  }))
})



const containerHeight = computed(() => {
  return props.options.autoHeight ? 'auto' : `calc(100% - ${props.extHeight}px)`
})

const tableHeight = computed(() => {
  return props.options.autoHeight ? null : '100%'
})

// 方法 - 获取数据
const fetchData = async (params = {}) => {
  if (!props.fetch) return

  loading.value = true
  try {
    const queryParams = {
      pageNo: pagination.currentPage,
      pageSize: pagination.pageSize,
      ...params
    }

    const res = await props.fetch(queryParams)

    if (res) {
      if (res.total !== undefined) {
        pagination.total = res.total
      } else if (Array.isArray(res)) {
        pagination.total = res.length
      } else if (res.list) {
        pagination.total = res.total || res.list.length
      }
    }

    emit('fetchSuccess', res)
    return res
  } catch (error) {
    console.error('获取数据失败:', error)
    ElMessage.error(error.message || '获取数据失败')
    emit('fetchError', error)
    throw error
  } finally {
    loading.value = false
  }
}

// 方法 - 事件处理
const handleSelectionChange = (selection) => {
  emit('selectionChange', selection)
}

const handleCurrentChange = (currentRow, oldCurrentRow) => {
  emit('currentChange', currentRow, oldCurrentRow)
}

const handleRowClick = (row, column, event) => {
  emit('rowClick', {
    row,
    column,
    event,
    setCurrentRow: (key, value) => setCurrentRow(key, value),
    getTableData: () => processedDataSource.value
  })

  // 自动高亮当前行
  if (props.options.highlightCurrentRow) {
    nextTick(() => {
      try {
        tableRef.value?.setCurrentRow(row)
      } catch (e) {
        console.warn('设置当前行高亮失败:', e)
      }
    })
  }
}

const handleSortChange = ({ column, prop, order }) => {
  emit('sortChange', { column, prop, order })
}

const handleSizeChange = (val) => {
  pagination.pageSize = val
  pagination.currentPage = 1
  emit('sizeChange', val)
  fetchData()
}

const handleCurrentPageChange = (val) => {
  pagination.currentPage = val
  emit('currentPageChange', val)
  fetchData()
}

// 重写setCurrentRow方法（解决行高亮不更新问题）
const setCurrentRow = (key, value) => {
  nextTick(() => {
    if (!tableRef.value) return

    const data = processedDataSource.value
    const targetRow = data.find(item => {
      if (!item || typeof item !== 'object') return false
      // 支持嵌套属性查找
      const keys = key.split('.')
      let val = item
      for (const k of keys) {
        val = val?.[k]
        if (val === undefined) break
      }
      return val === value
    })

    if (targetRow) {
      tableRef.value.setCurrentRow(null) // 先清空
      nextTick(() => {
        tableRef.value.setCurrentRow(targetRow)
      })
    }
  })
}


const clearSelection = () => {
  tableRef.value?.clearSelection()
}

const refresh = (params = {}) => {
  if (props.showPagination) {
    pagination.currentPage = 1
  }
  return fetchData(params)
}

const resetPagination = () => {
  pagination.currentPage = 1
  pagination.pageSize = props.pageSizes[0] || 10
  pagination.total = 0
}

// 监听
watch(
  () => props.dataSource,
  (newVal) => {
    if (!newVal) return

    if (typeof newVal === 'object' && newVal.total !== undefined) {
      pagination.total = newVal.total
    } else if (Array.isArray(newVal)) {
      pagination.total = newVal.length
    }
  },
  { immediate: true, deep: true }
)

// 生命周期
onMounted(() => {
  if (props.fetch) {
    fetchData()
  }
})

// 暴露API
defineExpose({
  refresh,
  resetPagination,
  setCurrentRow,
  clearSelection,
  getTableRef: () => tableRef.value,
  getTableData: () => processedDataSource.value,
  getPagination: () => ({ ...pagination }),
  fetchData // 暴露fetch方法
})

// 新增重要修复：深度监听数据变化
const processedDataSource = computed(() => {
  if (!props.dataSource) return []

  // 强制触发响应式更新
  const forceUpdate = JSON.parse(JSON.stringify(props.dataSource))

  if (Array.isArray(forceUpdate)) {
    return forceUpdate
  }

  if (forceUpdate?.list) {
    return Array.isArray(forceUpdate.list) ? forceUpdate.list : []
  }

  return []
})


</script>

<style scoped lang="scss">
.table-container {
  background-color: #fff;
  height: 100%;
  display: flex;
  flex-direction: column;
  border-radius: 4px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);

  .pagination-container {
    margin-top: 16px;
    padding: 0 16px;
    text-align: right;
    flex-shrink: 0;
  }
}

:deep(.el-table) {
  flex: 1;
  overflow: hidden;

  th.el-table__cell {
    background-color: #f5f7fa;
    color: #333;
    font-weight: bold;
  }

  &.el-table--striped
    .el-table__body
    tr.el-table__row--striped
    td.el-table__cell {
    background-color: #fafafa;
  }

  .el-table__body-wrapper {
    overflow-y: auto;
    flex-grow: 1;
  }

  .cell {
    white-space: nowrap;
  }

  .current-row > td {
    background-color: #f0f7ff !important;
  }
}
</style>