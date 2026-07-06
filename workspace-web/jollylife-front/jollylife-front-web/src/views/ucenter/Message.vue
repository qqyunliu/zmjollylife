<template>
  <div class="message-page">
    <div class="page-header">
      <div class="title">消息中心</div>
      <div class="header-actions">
        <el-badge :value="noReadCount" :hidden="noReadCount === 0">
          <el-button type="primary" size="small" @click="handleReadAll">全部已读</el-button>
        </el-badge>
      </div>
    </div>
    
    <div class="message-tabs">
      <div 
        class="tab-item" 
        :class="{ active: currentTab === 'all' }"
        @click="currentTab = 'all'; loadMessage()"
      >
        全部
      </div>
      <div 
        class="tab-item" 
        :class="{ active: currentTab === 'like' }"
        @click="currentTab = 'like'; loadMessage()"
      >
        点赞
      </div>
      <div 
        class="tab-item" 
        :class="{ active: currentTab === 'coin' }"
        @click="currentTab = 'coin'; loadMessage()"
      >
        投币
      </div>
      <div 
        class="tab-item" 
        :class="{ active: currentTab === 'collect' }"
        @click="currentTab = 'collect'; loadMessage()"
      >
        收藏
      </div>
      <div 
        class="tab-item" 
        :class="{ active: currentTab === 'comment' }"
        @click="currentTab = 'comment'; loadMessage()"
      >
        评论
      </div>
      <div 
        class="tab-item" 
        :class="{ active: currentTab === 'follow' }"
        @click="currentTab = 'follow'; loadMessage()"
      >
        关注
      </div>
      <div 
        class="tab-item" 
        :class="{ active: currentTab === 'system' }"
        @click="currentTab = 'system'; loadMessage()"
      >
        系统
      </div>
    </div>
    
    <div class="message-content" v-loading="loading">
      <div 
        v-for="item in messageList" 
        :key="item.messageId" 
        class="message-item"
        :class="{ unread: item.readStatus === 0 }"
        @click="handleClick(item)"
      >
        <div class="message-icon">
          <span class="iconfont" :class="getIconClass(item.messageType)"></span>
        </div>
        <div class="message-body">
          <div class="message-title">{{ item.messageTitle }}</div>
          <div class="message-content">{{ item.messageContent }}</div>
          <div class="message-time">{{ formatTime(item.createTime) }}</div>
        </div>
        <div class="message-action" @click.stop="handleDelete(item.messageId)">
          <span class="iconfont icon-delete"></span>
        </div>
      </div>
      
      <div v-if="messageList.length === 0 && !loading" class="empty-tip">
        <div class="empty-icon">📭</div>
        <div>暂无消息</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, getCurrentInstance } from 'vue'

const { proxy } = getCurrentInstance()

const messageList = ref([])
const loading = ref(false)
const currentTab = ref('all')
const noReadCount = ref(0)

const messageTypeMap = {
  'like': 0,
  'coin': 1,
  'collect': 2,
  'comment': 3,
  'follow': 4,
  'system': 5
}

const loadMessage = async () => {
  loading.value = true
  const params = {
    pageNo: 1,
    pageSize: 50
  }
  
  if (currentTab.value !== 'all' && messageTypeMap[currentTab.value] !== undefined) {
    params.messageType = messageTypeMap[currentTab.value]
  }
  
  let result = await proxy.request({
    url: proxy.Api.loadUserMessage,
    params
  })
  loading.value = false
  
  if (result) {
    messageList.value = result.data?.list || []
  }
}

const loadNoReadCount = async () => {
  let result = await proxy.request({
    url: proxy.Api.getNoReadCount
  })
  if (result) {
    noReadCount.value = result.data || 0
  }
}

const handleClick = async (item) => {
  if (item.readStatus === 0) {
    await proxy.request({
      url: proxy.Api.readAll
    })
    item.readStatus = 1
    noReadCount.value = Math.max(0, noReadCount.value - 1)
  }
  
  if (item.videoId) {
    proxy.$router.push('/video/' + item.videoId)
  } else if (item.linkUrl) {
    window.location.href = item.linkUrl
  }
}

const handleReadAll = async () => {
  let result = await proxy.request({
    url: proxy.Api.readAll
  })
  if (result) {
    messageList.value.forEach(item => {
      item.readStatus = 1
    })
    noReadCount.value = 0
    proxy.Message.success('已全部标记为已读')
  }
}

const handleDelete = async (messageId) => {
  try {
    await proxy.Confirm.warning('确定删除这条消息吗？')
    let result = await proxy.request({
      url: proxy.Api.delMessage,
      params: { messageId }
    })
    if (result) {
      const index = messageList.value.findIndex(m => m.messageId === messageId)
      if (index > -1) {
        messageList.value.splice(index, 1)
      }
      proxy.Message.success('删除成功')
    }
  } catch (e) {}
}

const getIconClass = (type) => {
  const map = {
    0: 'icon-like',
    1: 'icon-coin',
    2: 'icon-collect',
    3: 'icon-comment',
    4: 'icon-addfriend',
    5: 'icon-tongzhi'
  }
  return map[type] || 'icon-tongzhi'
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
  if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
  if (diff < 604800000) return Math.floor(diff / 86400000) + '天前'
  return date.toLocaleDateString()
}

onMounted(() => {
  loadMessage()
  loadNoReadCount()
})
</script>

<style scoped>
.message-page {
  padding: 20px;
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

.message-tabs {
  display: flex;
  gap: 10px;
  border-bottom: 1px solid #e3e5e7;
  margin-bottom: 20px;
}

.tab-item {
  padding: 10px 15px;
  cursor: pointer;
  color: #9499a0;
  border-bottom: 2px solid transparent;
}

.tab-item.active {
  color: #18191c;
  border-bottom-color: #00a1d6;
}

.message-content {
  min-height: 300px;
}

.message-item {
  display: flex;
  gap: 15px;
  padding: 15px;
  border-bottom: 1px solid #e3e5e7;
  cursor: pointer;
  transition: background 0.3s;
}

.message-item:hover {
  background: #f6f7f8;
}

.message-item.unread {
  background: #e6f7ff;
}

.message-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #f1f2f3;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  color: #00a1d6;
}

.message-body {
  flex: 1;
}

.message-title {
  font-weight: 500;
  margin-bottom: 5px;
}

.message-content {
  color: #9499a0;
  font-size: 13px;
  margin-bottom: 5px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.message-time {
  color: #9499a0;
  font-size: 12px;
}

.message-action {
  color: #9499a0;
  cursor: pointer;
  padding: 10px;
}

.message-action:hover {
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