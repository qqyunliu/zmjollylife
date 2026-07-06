<template>
  <div class="search-page">
    <div class="search-header">
      <div class="search-input-wrap">
        <el-input
          v-model="keyword"
          placeholder="搜索视频"
          @keyup.enter="doSearch"
          class="search-input"
        >
          <template #append>
            <el-button :icon="Search" @click="doSearch" />
          </template>
        </el-input>
      </div>
    </div>
    
    <div class="search-content">
      <div class="hot-keywords">
        <div class="section-title">热门搜索</div>
        <div class="keyword-list">
          <div
            v-for="item in hotKeywordList"
            :key="item.keyword"
            class="keyword-item"
            @click="keyword = item.keyword; doSearch()"
          >
            {{ item.keyword }}
          </div>
        </div>
      </div>
      
      <div class="search-result" v-if="searched">
        <div class="result-title">
          搜索结果 <span class="count">({{ totalCount }} 个)</span>
        </div>
        <div class="video-list" v-loading="loading">
          <VideoItem
            v-for="item in videoList"
            :key="item.videoId"
            :data="item"
          />
          <div v-if="videoList.length === 0 && !loading" class="empty-tip">
            未找到相关视频
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
// 1. 将所有导入统一放在 script setup 中
import { ref, onMounted, watch, getCurrentInstance } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Search } from '@element-plus/icons-vue'
import VideoItem from '@/components/VideoItem.vue'

// 2. 正确获取 proxy 和路由实例
const { proxy } = getCurrentInstance()
const route = useRoute()
const router = useRouter() // 引入 router 用于页面内搜索跳转

const keyword = ref('')
const searched = ref(false)
const loading = ref(false)
const videoList = ref([])
const totalCount = ref(0)
const hotKeywordList = ref([])

// 3. 点击搜索按钮或回车时，统一推送到路由，让 watch 来接管数据请求
const doSearch = () => {
  if (!keyword.value.trim()) return
  router.push({
    path: '/search',
    query: { keyword: keyword.value }
  })
}

// 4. 真实的发送请求拉取数据的逻辑
const fetchSearchData = async () => {
  searched.value = true
  loading.value = true
  
  let result = await proxy.request({
    url: proxy.Api.search,
    params: {
      keyword: keyword.value,
      // 如果后端暂时不支持分页，传了 pageNo 和 pageSize 也没关系，会被忽略
      pageNo: 1, 
      pageSize: 20
    }
  })
  loading.value = false
  
  // 核心修改在这里 👇
  if (result && result.data) {
    // 兼容处理：判断 data 是直接的数组，还是包裹了 list 的分页对象
    if (Array.isArray(result.data)) {
      // 1. 如果后端直接返回数组 (当前情况)
      videoList.value = result.data
      totalCount.value = result.data.length
    } else {
      // 2. 如果后端以后改成了分页结构
      videoList.value = result.data.list || []
      totalCount.value = result.data.total || 0
    }
  } else {
    // 如果没有数据或者请求失败
    videoList.value = []
    totalCount.value = 0
  }
}

const loadHotKeywords = async () => {
  let result = await proxy.request({
    url: proxy.Api.getSearchKeywordTop
  })
  if (result) {
    hotKeywordList.value = result.data || []
  }
}

// 5. 核心修复：监听路由参数变化。加上 immediate: true 后，连 onMounted 里的判断都可以省了
watch(
  () => route.query.keyword,
  (newVal) => {
    if (newVal) {
      keyword.value = newVal
      fetchSearchData()
    }
  },
  { immediate: true }
)

onMounted(() => {
  loadHotKeywords()
})
</script>

<style scoped>
.search-page {
  min-height: 500px;
  padding: 20px;
}

.search-header {
  margin-bottom: 30px;
}

.search-input {
  max-width: 600px;
}

.search-content {
  padding: 0 40px;
}

.hot-keywords {
  margin-bottom: 30px;
}

.section-title {
  font-size: 18px;
  font-weight: 500;
  margin-bottom: 15px;
}

.keyword-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.keyword-item {
  background: #f1f2f3;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.keyword-item:hover {
  background: #e3e5e7;
}

.result-title {
  font-size: 18px;
  font-weight: 500;
  margin-bottom: 20px;
}

.count {
  color: #9499a0;
  font-weight: normal;
}

.video-list {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 20px;
  min-height: 200px;
}

.empty-tip {
  grid-column: 1 / -1;
  text-align: center;
  padding: 60px;
  color: #9499a0;
}
</style>