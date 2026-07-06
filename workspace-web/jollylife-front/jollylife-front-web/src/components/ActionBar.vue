<template>
  <div class="action-bar">
    <div class="action-item" :class="{ active: hasLiked }" @click="handleLike">
      <span class="iconfont icon-like"></span>
      <span class="text">{{ likeCount > 0 ? likeCount : '点赞' }}</span>
    </div>
    <div class="action-item" :class="{ active: hasCoin }" @click="handleCoin">
      <span class="iconfont icon-coin"></span>
      <span class="text">{{ coinCount > 0 ? coinCount : '投币' }}</span>
    </div>
    <div class="action-item" :class="{ active: hasCollected }" @click="handleCollect">
      <span class="iconfont icon-collect"></span>
      <span class="text">{{ collectCount > 0 ? collectCount : '收藏' }}</span>
    </div>
    <div class="action-item" @click="handleShare">
      <span class="iconfont icon-share"></span>
      <span class="text">分享</span>
    </div>

    <el-dialog v-model="coinDialogVisible" title="投币" width="400px">
      <div class="coin-dialog-content">
        <div class="coin-info">
          <span class="label">选择投币数量：</span>
          <el-radio-group v-model="coinNum">
            <el-radio :label="1">1 币</el-radio>
            <el-radio :label="2">2 币</el-radio>
          </el-radio-group>
        </div>
        <div class="coin-tip">投币后无法退回</div>
      </div>
      <template #footer>
        <el-button @click="coinDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmCoin">确认投币</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, getCurrentInstance } from 'vue'
const { proxy } = getCurrentInstance()

const props = defineProps({
  videoId: {
    type: String,
    required: true
  },
  likeCount: {
    type: Number,
    default: 0
  },
  coinCount: {
    type: Number,
    default: 0
  },
  collectCount: {
    type: Number,
    default: 0
  },
  hasLiked: {
    type: Boolean,
    default: false
  },
  hasCoin: {
    type: Boolean,
    default: false
  },
  hasCollected: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:likeCount', 'update:coinCount', 'update:collectCount', 'update:hasLiked', 'update:hasCoin', 'update:hasCollected'])

const coinDialogVisible = ref(false)
const coinNum = ref(1)

const handleLike = async () => {
  const actionType = props.hasLiked ? 2 : 0
  const result = await proxy.request({
    url: proxy.Api.userAction,
    params: {
      videoId: props.videoId,
      actionType
    }
  })
  if (result) {
    const newCount = props.hasLiked ? props.likeCount - 1 : props.likeCount + 1
    emit('update:hasLiked', !props.hasLiked)
    emit('update:likeCount', newCount)
    proxy.Message.success(props.hasLiked ? '已取消点赞' : '点赞成功')
  }
}

const handleCoin = () => {
  coinDialogVisible.value = true
}

const confirmCoin = async () => {
  const result = await proxy.request({
    url: proxy.Api.userAction,
    params: {
      videoId: props.videoId,
      actionType: 4,
      actionCount: coinNum.value
    }
  })
  if (result) {
    emit('update:hasCoin', true)
    emit('update:coinCount', props.coinCount + coinNum.value)
    proxy.Message.success('投币成功')
    coinDialogVisible.value = false
  }
}

const handleCollect = async () => {
  const actionType = props.hasCollected ? 3 : 1
  const result = await proxy.request({
    url: proxy.Api.userAction,
    params: {
      videoId: props.videoId,
      actionType
    }
  })
  if (result) {
    emit('update:hasCollected', !props.hasCollected)
    emit('update:collectCount', props.hasCollected ? props.collectCount - 1 : props.collectCount + 1)
    proxy.Message.success(props.hasCollected ? '已取消收藏' : '收藏成功')
  }
}

const handleShare = () => {
  const url = window.location.href
  if (navigator.clipboard) {
    navigator.clipboard.writeText(url).then(() => {
      proxy.Message.success('链接已复制到剪贴板')
    })
  } else {
    const input = document.createElement('input')
    input.value = url
    document.body.appendChild(input)
    input.select()
    document.execCommand('copy')
    document.body.removeChild(input)
    proxy.Message.success('链接已复制到剪贴板')
  }
}
</script>

<style lang="scss" scoped>
.action-bar {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-top: 15px;

  .action-item {
    display: flex;
    align-items: center;
    gap: 5px;
    padding: 8px 15px;
    background: #f1f2f3;
    border-radius: 8px;
    cursor: pointer;
    transition: all 0.3s;
    color: #61666d;

    .iconfont {
      font-size: 18px;
    }

    .text {
      font-size: 14px;
    }

    &:hover {
      background: #e3e4e5;
    }

    &.active {
      color: #fb7299;
      background: #fae1e8;
    }
  }

  .coin-dialog-content {
    .coin-info {
      display: flex;
      align-items: center;
      gap: 15px;
      margin-bottom: 15px;

      .label {
        font-size: 14px;
        color: var(--text);
      }
    }

    .coin-tip {
      font-size: 12px;
      color: #9499a0;
    }
  }
}
</style>