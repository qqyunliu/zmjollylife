<template>
  <div class="comment-management">
    <div class="page-header">
      <div class="title">评论管理</div>
    </div>

    <div class="filter-bar">
      <el-input 
        v-model="keyword" 
        placeholder="搜索评论内容" 
        clearable 
        style="width: 300px"
        @keyup.enter="loadCommentList"
      >
        <template #append>
          <el-button icon="Search" @click="loadCommentList" />
        </template>
      </el-input>
    </div>

    <div class="comment-list" v-loading="loading">
      <template v-if="commentList.length > 0">
        <div class="comment-item" v-for="item in commentList" :key="item.commentId">
          <div class="comment-content">{{ item.content }}</div>
          <div class="comment-video">
            所属视频：<span class="video-name" @click="viewVideo(item.videoId)">{{ item.videoName }}</span>
          </div>
          <div class="comment-time">发布时间：{{ item.postTime }}</div>
          <div class="comment-actions">
            <el-button type="danger" size="small" @click="deleteComment(item.commentId)">删除</el-button>
          </div>
        </div>
      </template>
      <div v-else class="empty-tip">
        <div>暂无评论</div>
      </div>
    </div>

    <div class="pagination" v-if="total > 0">
      <el-pagination
        background
        layout="total, prev, pager, next"
        :total="total"
        :page-size="pageSize"
        v-model:current-page="pageNo"
        @current-change="loadCommentList"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, getCurrentInstance } from 'vue'

const { proxy } = getCurrentInstance()

const loading = ref(false)
const commentList = ref([])
const pageNo = ref(1)
const pageSize = ref(10)
const total = ref(0)
const keyword = ref('')

const loadCommentList = async () => {
  loading.value = true
  let result = await proxy.request({
    url: proxy.Api.ucLoadComment,
    params: {
      pageNo: pageNo.value,
      pageSize: pageSize.value,
      keyword: keyword.value
    }
  })
  loading.value = false
  if (result) {
    commentList.value = result.data?.list || []
    total.value = result.data?.totalCount || 0
  }
}

const deleteComment = async (commentId) => {
  try {
    await proxy.Confirm.warning('确定删除这条评论吗？')
    let result = await proxy.request({
      url: proxy.Api.ucDelComment,
      params: { commentId }
    })
    if (result) {
      proxy.Message.success('删除成功')
      loadCommentList()
    }
  } catch (e) {}
}

const viewVideo = (videoId) => {
  window.open('/video/' + videoId, '_blank')
}

onMounted(() => {
  loadCommentList()
})
</script>

<style scoped>
.comment-management {
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

.comment-list {
  min-height: 400px;
}

.comment-item {
  padding: 15px;
  background: #fff;
  border-radius: 8px;
  margin-bottom: 15px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
}

.comment-user {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  margin-right: 10px;
}

.nickname {
  font-weight: 500;
}

.comment-content {
  margin-bottom: 10px;
  color: #18191c;
}

.comment-video {
  font-size: 13px;
  color: #9499a0;
  margin-bottom: 8px;
}

.video-name {
  color: #00a1d6;
  cursor: pointer;
}

.video-name:hover {
  text-decoration: underline;
}

.comment-time {
  font-size: 13px;
  color: #9499a0;
  margin-bottom: 10px;
}

.comment-actions {
  text-align: right;
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
