<template>
  <div class="comment-item">
    <Avatar :userId="comment.userId" :avatar="comment.avatar" :nickId="comment.nickId || comment.nickName" :linkable="false" />
    <div class="comment-content">
      <div class="comment-header">
        <span class="nick-name">{{ comment.nickId || comment.nickName }}</span>
        <span class="post-time">{{ formatTime(comment.postTime) }}</span>
        <span v-if="comment.topType === 1" class="top-tag">置顶</span>
      </div>
      <div class="comment-body">
        {{ comment.content }}
      </div>
      <div class="comment-footer">
        <div class="action-item" @click="showReplyToCommentUser(comment.userId, comment.nickId || comment.nickName)">
          <span class="iconfont icon-comment"></span>
          <span>回复</span>
        </div>
        <div class="action-item" @click="toggleCommentReplies" :class="{ disabled: repliesLoading }">
          <span>{{ repliesExpanded ? '收起' : `展开${replyCountText}` }}</span>
        </div>
        <div class="action-item" v-if="isOwner" @click="handleDelete">
          <span class="iconfont icon-delete"></span>
          <span>删除</span>
        </div>
      </div>
      
      <div v-if="showReply" class="reply-input-wrap">
        <CommentInput
          :videoId="comment.videoId"
          :pCommentId="comment.commentId"
          :replyUserId="replyUserId"
          :replyingTo="replyingTo"
          @success="replySuccessForComment"
        />
      </div>

      <div v-if="repliesExpanded && comment.replies && comment.replies.length > 0" class="reply-list">
        <div
          v-for="reply in comment.replies"
          :key="reply.commentId"
          class="reply-item"
        >
          <Avatar :userId="reply.userId" :avatar="reply.avatar" :nickId="reply.nickId || reply.nickName" :linkable="false" />
          <div class="reply-content">
            <div class="reply-header">
              <span class="nick-name">{{ reply.nickId || reply.nickName }}</span>
              <span class="reply-to" v-if="reply.replyUserId !== comment.userId">
                回复 @{{ reply.replyNickId || reply.replyNickName }}
              </span>
              <span class="post-time">{{ formatTime(reply.postTime) }}</span>
            </div>
            <div class="reply-body">
              {{ reply.content }}
            </div>
            <div class="reply-footer">
              <div class="action-item" @click="showReplyToReply(reply)">
                <span class="iconfont icon-comment"></span>
                <span>回复</span>
              </div>
              <div class="action-item" @click="toggleReplyChildren(reply)">
                <span>{{ replyState[reply.commentId]?.expanded ? '收起' : '展开' }}</span>
              </div>
              <div class="action-item" v-if="reply.userId === currentUserId" @click="handleDeleteReply(reply)">
                <span class="iconfont icon-delete"></span>
                <span>删除</span>
              </div>
            </div>

            <div v-if="replyState[reply.commentId]?.showInput" class="reply-input-wrap">
              <CommentInput
                :videoId="comment.videoId"
                :pCommentId="reply.commentId"
                :replyUserId="replyState[reply.commentId].replyUserId"
                :replyingTo="replyState[reply.commentId].replyingTo"
                @success="() => replySuccessForReply(reply.commentId)"
              />
            </div>

            <div v-if="replyState[reply.commentId]?.expanded && replyState[reply.commentId]?.children?.length" class="reply-children">
              <div v-for="child in replyState[reply.commentId].children"
                   :key="child.commentId"
                   class="reply-child-item">
                <Avatar :userId="child.userId" :avatar="child.avatar" :nickId="child.nickId || child.nickName" :linkable="false" />
                <div class="reply-content">
                  <div class="reply-header">
                    <span class="nick-name">{{ child.nickId || child.nickName }}</span>
                    <span class="reply-to" v-if="child.replyUserId">
                      回复 @{{ child.replyNickId || child.replyNickName }}
                    </span>
                    <span class="post-time">{{ formatTime(child.postTime) }}</span>
                  </div>
                  <div class="reply-body">{{ child.content }}</div>
                  <div class="reply-footer">
                    <div class="action-item" @click="showReplyToReply(reply, child)">
                      <span class="iconfont icon-comment"></span>
                      <span>回复</span>
                    </div>
                    <div class="action-item" v-if="child.userId === currentUserId" @click="handleDeleteReply(child)">
                      <span class="iconfont icon-delete"></span>
                      <span>删除</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div v-if="comment.replyCount > (comment.replies?.length || 0)" class="load-more-reply" @click="loadMoreReplies">
          查看更多回复 ({{ comment.replyCount - (comment.replies?.length || 0) }})
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { getCurrentInstance } from 'vue'
import { useLoginStore } from '@/stores/loginStore'
import CommentInput from './CommentInput.vue'
const { proxy } = getCurrentInstance()
const loginStore = useLoginStore()

const props = defineProps({
  comment: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['delete', 'refresh', 'loadReplies'])

const showReply = ref(false)
const replyUserId = ref('')
const replyingTo = ref('')
const replyState = reactive({})
const repliesExpanded = ref(false)
const repliesLoaded = ref(false)
const repliesLoading = ref(false)
const currentUserId = loginStore.userInfo?.userId
const isOwner = ref(props.comment.userId === currentUserId)

const replyCountText = computed(() => {
  const count = props.comment.replyCount ?? props.comment.replies?.length ?? 0
  return count > 0 ? `(${count})` : '回复'
})

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

const ensureReplyState = (commentId) => {
  if (!replyState[commentId]) {
    replyState[commentId] = {
      expanded: false,
      loaded: false,
      loading: false,
      showInput: false,
      replyUserId: '',
      replyingTo: '',
      children: []
    }
  }
  return replyState[commentId]
}

const loadReplyChildren = async (pCommentId) => {
  const state = ensureReplyState(pCommentId)
  if (state.loading) {
    return
  }
  state.loading = true
  try {
    let result = await proxy.request({
      url: proxy.Api.loadReply,
      params: {
        pCommentId
      }
    })
    if (result) {
      state.children = result.data || []
    }
    state.loaded = true
  } finally {
    state.loading = false
  }
}

const loadCommentReplies = async () => {
  let result = await proxy.request({
    url: proxy.Api.loadReply,
    params: {
      pCommentId: props.comment.commentId
    }
  })
  if (result) {
    props.comment.replies = result.data || []
  }
  repliesLoaded.value = true
}

const toggleCommentReplies = async () => {
  if (repliesLoading.value) {
    return
  }
  if (repliesExpanded.value) {
    repliesExpanded.value = false
    return
  }
  repliesLoading.value = true
  try {
    if (!repliesLoaded.value) {
      await loadCommentReplies()
    }
    const replies = props.comment.replies || []
    await Promise.all(
      replies.map(async (r) => {
        const state = ensureReplyState(r.commentId)
        if (!state.loaded) {
          await loadReplyChildren(r.commentId)
        }
        state.expanded = true
      })
    )
    repliesExpanded.value = true
  } finally {
    repliesLoading.value = false
  }
}

const showReplyToCommentUser = (userId, nick) => {
  const nextUserId = userId || ''
  const nextNick = nick || ''
  const isSameTarget = replyUserId.value === nextUserId && replyingTo.value === nextNick
  replyUserId.value = nextUserId
  replyingTo.value = nextNick
  showReply.value = !showReply.value || !isSameTarget
}

const showReplyToReply = (parentReply, targetReply) => {
  const state = ensureReplyState(parentReply.commentId)
  const target = targetReply || parentReply
  const nextUserId = target.userId || ''
  const nextNick = target.nickId || target.nickName || ''
  const isSameTarget = state.replyUserId === nextUserId && state.replyingTo === nextNick
  state.replyUserId = nextUserId
  state.replyingTo = nextNick
  state.showInput = !state.showInput || !isSameTarget
  loadReplyChildren(parentReply.commentId)
}

const toggleReplyChildren = async (reply) => {
  const state = ensureReplyState(reply.commentId)
  if (state.expanded) {
    state.expanded = false
    return
  }
  if (!state.loaded) {
    await loadReplyChildren(reply.commentId)
  }
  state.expanded = true
}

const replySuccessForComment = () => {
  showReply.value = false
  repliesLoaded.value = false
  toggleCommentReplies()
}

const replySuccessForReply = async (replyCommentId) => {
  const state = ensureReplyState(replyCommentId)
  state.showInput = false
  await loadReplyChildren(replyCommentId)
}

const handleDelete = async () => {
  try {
    await proxy.Confirm.warning('确定要删除这条评论吗？')
    let result = await proxy.request({
      url: proxy.Api.userDelComment,
      params: {
        commentId: props.comment.commentId
      }
    })
    if (result) {
      proxy.Message.success('删除成功')
      emit('delete', props.comment.commentId)
    }
  } catch (e) {}
}

const handleDeleteReply = async (reply) => {
  try {
    await proxy.Confirm.warning('确定要删除这条回复吗？')
    let result = await proxy.request({
      url: proxy.Api.userDelComment,
      params: {
        commentId: reply.commentId
      }
    })
    if (result) {
      proxy.Message.success('删除成功')
      emit('refresh')
    }
  } catch (e) {}
}

const loadMoreReplies = () => {
  emit('loadReplies', props.comment.commentId)
}
</script>

<style scoped>
.comment-item {
  display: flex;
  gap: 12px;
  padding: 15px 0;
  border-bottom: 1px solid #eee;
}

.comment-content {
  flex: 1;
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}

.nick-name {
  font-weight: 500;
  color: #18191c;
}

.post-time {
  color: #9499a0;
  font-size: 12px;
}

.top-tag {
  background: #ff9900;
  color: #fff;
  padding: 2px 6px;
  border-radius: 3px;
  font-size: 12px;
}

.comment-body {
  color: #18191c;
  line-height: 1.6;
  margin-bottom: 10px;
}

.comment-footer {
  display: flex;
  gap: 20px;
}

.action-item {
  display: flex;
  align-items: center;
  gap: 5px;
  color: #9499a0;
  cursor: pointer;
}

.action-item:hover {
  color: #00a1d6;
}

.action-item.disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.action-item.active {
  color: #00a1d6;
}

.reply-input-wrap {
  margin-top: 15px;
}

.reply-list {
  margin-top: 15px;
  padding-left: 10px;
  border-left: 2px solid #e3e5e7;
}

.reply-item {
  display: flex;
  gap: 10px;
  padding: 10px 0;
}

.reply-children {
  margin-top: 10px;
  padding-left: 12px;
  border-left: 2px solid #f0f0f0;
}

.reply-child-item {
  display: flex;
  gap: 10px;
  padding: 8px 0;
}

.reply-content {
  flex: 1;
}

.reply-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 5px;
  font-size: 13px;
}

.reply-to {
  color: #9499a0;
}

.reply-body {
  color: #18191c;
  font-size: 14px;
  line-height: 1.5;
}

.reply-footer {
  display: flex;
  gap: 15px;
  margin-top: 5px;
}

.load-more-reply {
  color: #00a1d6;
  cursor: pointer;
  padding: 10px 0;
  font-size: 13px;
}

.load-more-reply:hover {
  color: #00a1d6;
}
</style>
