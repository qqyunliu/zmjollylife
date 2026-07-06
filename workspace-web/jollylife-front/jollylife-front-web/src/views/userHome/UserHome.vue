<template>
  <div class="user-home">
    <div class="user-header">
      <div class="user-cover" :style="{ backgroundImage: `url(${userInfo.cover || defaultCover})` }"></div>
      <div class="user-info">
        <Avatar :userId="userInfo.userId" :avatar="userInfo.avatar" :nickId="userInfo.userInfo?.nickId || userInfo.nickId" :size="100" />
        <div class="info-content">
          <div class="nick-name">{{ userInfo.nickName || '未设置昵称' }}</div>
          <div class="user-id">UID: {{ userInfo.userId }}</div>
          <div class="user-desc">{{ userInfo.personIntroduction || '这个人很懒，什么都没留下' }}</div>
          <div class="user-stats">
            <div class="stat-item">
              <span class="count">{{ userInfo.followCount || 0 }}</span>
              <span class="label">关注</span>
            </div>
            <div class="stat-item">
              <span class="count">{{ userInfo.fansCount || 0 }}</span>
              <span class="label">粉丝</span>
            </div>
            <div class="stat-item">
              <span class="count">{{ userInfo.playCount || 0 }}</span>
              <span class="label">播放</span>
            </div>
          </div>
          <div class="user-actions" v-if="!isOwner">
            <el-button v-if="!userInfo.haveFocus" type="primary" @click="handleFocus">
              关注
            </el-button>
            <el-button v-else @click="handleCancelFocus">取消关注</el-button>
            <el-button @click="handleSendMessage">发消息</el-button>
          </div>
          <div class="user-actions" v-else>
            <el-button @click="$router.push('/ucenter/home')">编辑资料</el-button>
          </div>
        </div>
      </div>
    </div>
    
    <div class="user-content">
      <div class="tab-bar">
        <div 
          class="tab-item" 
          :class="{ active: currentTab === 'video' }"
          @click="currentTab = 'video'"
        >
          视频 {{ userInfo.videoCount || 0 }}
        </div>
        <div 
          class="tab-item" 
          :class="{ active: currentTab === 'collection' }"
          @click="currentTab = 'collection'"
        >
          收藏 {{ userInfo.collectCount || 0 }}
        </div>
      </div>
      
      <div class="tab-content" v-loading="loading">
        <div v-if="currentTab === 'video'" class="video-grid">
          <VideoItem
            v-for="item in videoList"
            :key="item.videoId"
            :data="item"
          />
          <div v-if="videoList.length === 0" class="empty-tip">
            还没有投稿视频
          </div>
        </div>
        
        <div v-if="currentTab === 'collection'" class="video-grid">
          <VideoItem
            v-for="item in collectionList"
            :key="item.videoId"
            :data="item"
          />
          <div v-if="collectionList.length === 0" class="empty-tip">
            还没有收藏视频
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { useLoginStore } from '@/stores/loginStore'
import Avatar from '@/components/Avatar.vue'
import VideoItem from '@/components/VideoItem.vue'

const route = useRoute()
const loginStore = useLoginStore()

const userInfo = ref({})
const videoList = ref([])
const collectionList = ref([])
const currentTab = ref('video')
const loading = ref(false)
const defaultCover = 'https://pic1.zhimg.com/80/v2-0c00e1160c3c2b37a41bc3c0c2d3e6e1_720w.jpg'

const isOwner = computed(() => {
  return loginStore.userInfo?.userId === userInfo.value.userId
})

const loadUserInfo = async () => {
  let result = await proxy.request({
    url: proxy.Api.uHomeGetUserInfo,
    params: {
      userId: route.params.userId
    }
  })
  if (result) {
    userInfo.value = result.data || {}
  }
}

const loadUserVideo = async () => {
  loading.value = true
  let result = await proxy.request({
    url: proxy.Api.uHomeLoadVideoList,
    params: {
      userId: route.params.userId,
      pageNo: 1,
      pageSize: 20
    }
  })
  loading.value = false
  
  if (result) {
    videoList.value = result.data?.list || []
  }
}

const loadUserCollection = async () => {
  loading.value = true
  let result = await proxy.request({
    url: proxy.Api.uHomeLoadUserCollection,
    params: {
      userId: route.params.userId,
      pageNo: 1,
      pageSize: 20
    }
  })
  loading.value = false
  
  if (result) {
    collectionList.value = result.data?.list || []
  }
}

const handleFocus = async () => {
  let result = await proxy.request({
    url: proxy.Api.uHomeFocus,
    params: {
      focusUserId: route.params.userId,
      operationType: 0
    }
  })
  if (result) {
    userInfo.value.haveFocus = true
    userInfo.value.fansCount = (userInfo.value.fansCount || 0) + 1
    proxy.Message.success('关注成功')
  }
}

const handleCancelFocus = async () => {
  let result = await proxy.request({
    url: proxy.Api.uHomeCancelFocus,
    params: {
      focusUserId: route.params.userId
    }
  })
  if (result) {
    userInfo.value.haveFocus = false
    userInfo.value.fansCount = Math.max(0, (userInfo.value.fansCount || 1) - 1)
    proxy.Message.success('取消关注成功')
  }
}

const handleSendMessage = () => {
  proxy.Message.info('私信功能开发中')
}

onMounted(() => {
  loadUserInfo()
  loadUserVideo()
})
</script>

<script>
import { getCurrentInstance } from 'vue'
const { proxy } = getCurrentInstance()
</script>

<style scoped>
.user-home {
  min-height: 500px;
}

.user-header {
  position: relative;
  margin-bottom: 20px;
}

.user-cover {
  height: 200px;
  background-size: cover;
  background-position: center;
  background-color: #e3e5e7;
}

.user-info {
  display: flex;
  gap: 20px;
  padding: 0 40px;
  margin-top: -50px;
  position: relative;
}

.info-content {
  flex: 1;
  padding-top: 60px;
}

.nick-name {
  font-size: 24px;
  font-weight: 500;
  margin-bottom: 5px;
}

.user-id {
  color: #9499a0;
  font-size: 13px;
  margin-bottom: 10px;
}

.user-desc {
  color: #18191c;
  margin-bottom: 15px;
}

.user-stats {
  display: flex;
  gap: 30px;
  margin-bottom: 15px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-item .count {
  font-size: 18px;
  font-weight: 500;
}

.stat-item .label {
  color: #9499a0;
  font-size: 13px;
}

.user-actions {
  display: flex;
  gap: 10px;
}

.user-content {
  padding: 0 40px;
}

.tab-bar {
  display: flex;
  border-bottom: 1px solid #e3e5e7;
  margin-bottom: 20px;
}

.tab-item {
  padding: 15px 20px;
  cursor: pointer;
  color: #9499a0;
  border-bottom: 2px solid transparent;
}

.tab-item.active {
  color: #18191c;
  border-bottom-color: #00a1d6;
}

.video-grid {
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