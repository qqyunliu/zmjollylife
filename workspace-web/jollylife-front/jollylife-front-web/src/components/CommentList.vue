<template>
  <div class="comment-list">
    <div class="comment-title">
      <span>评论</span>
      <span class="count">{{ totalAllCount }}</span>
    </div>
    
    <CommentInput :videoId="videoId" @success="loadComment" />
    
    <div class="comment-tabs">
      <div 
        class="tab-item" 
        :class="{ active: sortType === 0 }"
        @click="sortType = 0; loadComment()"
      >
        热门
      </div>
      <div 
        class="tab-item" 
        :class="{ active: sortType === 1 }"
        @click="sortType = 1; loadComment()"
      >
        最新
      </div>
    </div>
    
    <div class="comment-items" v-loading="loading">
      <CommentItem
        v-for="item in commentList"
        :key="item.commentId"
        :comment="item"
        @delete="handleDelete"
        @refresh="loadComment"
        @loadReplies="loadReplies"
      />
      
      <div v-if="commentList.length === 0 && !loading" class="empty-tip">
        暂无评论，快来抢沙发吧~
      </div>
    </div>
    
    <div class="load-more" v-if="hasMore" @click="loadMore">
      <span>加载更多</span>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getCurrentInstance } from 'vue'
import CommentInput from './CommentInput.vue'
import CommentItem from './CommentItem.vue'

const { proxy } = getCurrentInstance()

const props = defineProps({
  videoId: {
    type: String,
    required: true
  }
})

const commentList = ref([])
const loading = ref(false)
const pageNo = ref(1)
const pageSize = ref(10)
const totalTopCount = ref(0)
const totalAllCount = ref(0)
const hasMore = ref(false)
const sortType = ref(0)

const loadComment = async () => {
  loading.value = true
  pageNo.value = 1
  let result = await proxy.request({
    url: proxy.Api.loadComment,
    params: {
      videoId: props.videoId,
      pageNo: pageNo.value,
      pageSize: pageSize.value,
      sortType: sortType.value
    }
  })
  loading.value = false
  
  if (result) {
    commentList.value = result.data?.list || []
    totalTopCount.value = result.data?.total || 0
    totalAllCount.value = result.data?.allCount ?? totalTopCount.value
    hasMore.value = commentList.value.length < totalTopCount.value
  }
}

const loadMore = async () => {
  pageNo.value++
  let result = await proxy.request({
    url: proxy.Api.loadComment,
    params: {
      videoId: props.videoId,
      pageNo: pageNo.value,
      pageSize: pageSize.value,
      sortType: sortType.value
    }
  })
  
  if (result) {
    commentList.value.push(...(result.data?.list || []))
    hasMore.value = commentList.value.length < totalTopCount.value
  }
}

const handleDelete = async (commentId) => {
  await loadComment()
}

const loadReplies = async (commentId) => {
  let result = await proxy.request({
    url: proxy.Api.loadReply,
    params: {
      pCommentId: commentId
    }
  })
  
  if (result) {
    const comment = commentList.value.find(c => c.commentId === commentId)
    if (comment) {
      comment.replies = result.data || []
    }
  }
}

onMounted(() => {
  loadComment()
})
</script>

<style scoped>
.comment-list {
  margin-top: 20px;
}

.comment-title {
  font-size: 18px;
  font-weight: 500;
  margin-bottom: 15px;
}

.count {
  color: #9499a0;
  margin-left: 5px;
}

.comment-tabs {
  display: flex;
  gap: 20px;
  margin: 15px 0;
  border-bottom: 1px solid #e3e5e7;
}

.tab-item {
  padding: 10px 0;
  cursor: pointer;
  color: #9499a0;
  border-bottom: 2px solid transparent;
}

.tab-item.active {
  color: #18191c;
  border-bottom-color: #00a1d6;
}

.comment-items {
  min-height: 200px;
}

.empty-tip {
  text-align: center;
  padding: 40px;
  color: #9499a0;
}

.load-more {
  text-align: center;
  padding: 20px;
  color: #00a1d6;
  cursor: pointer;
}

.load-more:hover {
  color: #00a1d6;
}
</style>
