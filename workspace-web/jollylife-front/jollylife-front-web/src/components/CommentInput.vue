<template>
  <div class="comment-input">
    <div class="input-area">
      <el-input
        v-model="commentContent"
        type="textarea"
        :rows="3"
        :placeholder="placeholder"
        maxlength="500"
        show-word-limit
      />
    </div>
    <div class="input-footer">
      <el-button type="primary" @click="submitComment" :loading="submitting">
        {{ replyingTo ? '回复' : '发表评论' }}
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { getCurrentInstance } from 'vue'
const { proxy } = getCurrentInstance()

const props = defineProps({
  videoId: {
    type: String,
    required: true
  },
  pCommentId: {
    type: Number,
    default: null
  },
  replyUserId: {
    type: String,
    default: ''
  },
  replyingTo: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['success'])

const commentContent = ref('')
const submitting = ref(false)

const placeholder = computed(() => (props.replyingTo ? `回复 @${props.replyingTo}` : '发表评论'))

const submitComment = async () => {
  if (!commentContent.value.trim()) {
    proxy.Message.warning('请输入评论内容')
    return
  }
  
  submitting.value = true
  let result = await proxy.request({
    url: proxy.Api.postComment,
    params: {
      videoId: props.videoId,
      content: commentContent.value,
      pCommentId: props.pCommentId,
      replyUserId: props.replyUserId
    }
  })
  submitting.value = false
  
  if (result) {
    proxy.Message.success('评论成功')
    commentContent.value = ''
    emit('success')
  }
}
</script>

<style scoped>
.comment-input {
  background: #fff;
  padding: 15px;
  border-radius: 4px;
}

.input-area {
  margin-bottom: 10px;
}

.input-footer {
  display: flex;
  justify-content: flex-end;
}
</style>
