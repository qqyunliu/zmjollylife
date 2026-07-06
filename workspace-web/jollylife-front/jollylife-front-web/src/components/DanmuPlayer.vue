<template>
  <div class="danmu-panel" v-if="danmuShow">
    <div class="danmu-layer" ref="danmuLayerRef">
      <div
        v-for="item in showDanmuList"
        :key="item.danmuId"
        class="danmu-item"
        :style="{
          top: item.top + 'px',
          color: item.color || '#ffffff'
        }"
      >
        {{ item.content }}
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue'
import { getCurrentInstance } from 'vue'
const { proxy } = getCurrentInstance()

const props = defineProps({
  videoId: {
    type: String,
    required: true
  },
  fileId: {
    type: String,
    default: ''
  },
  danmuShow: {
    type: Boolean,
    default: true
  }
})

const emit = defineEmits(['sendDanmu'])

const danmuLayerRef = ref(null)
const danmuList = ref([])
const showDanmuList = ref([])

const danmuTimer = ref(null)

const loadDanmu = async () => {
  let result = await proxy.request({
    url: proxy.Api.loadDanmu,
    params: {
      videoId: props.videoId,
      fileId: props.fileId
    }
  })
  if (result) {
    danmuList.value = result.data || []
  }
}

const showDanmu = () => {
  if (danmuList.value.length === 0) return
  
  const layerHeight = danmuLayerRef.value?.clientHeight || 500
  const item = danmuList.value[Math.floor(Math.random() * danmuList.value.length)]
  if (item) {
    item.top = Math.random() * (layerHeight - 30)
    item.danmuId = item.danmuId || Date.now()
    showDanmuList.value.push(item)
    
    setTimeout(() => {
      const index = showDanmuList.value.findIndex(d => d.danmuId === item.danmuId)
      if (index > -1) {
        showDanmuList.value.splice(index, 1)
      }
    }, 8000)
  }
}

onMounted(() => {
  loadDanmu()
  danmuTimer.value = setInterval(showDanmu, 3000)
})

onUnmounted(() => {
  if (danmuTimer.value) {
    clearInterval(danmuTimer.value)
  }
})

watch(() => props.videoId, () => {
  loadDanmu()
})

defineExpose({
  loadDanmu
})
</script>

<style scoped>
/* 弹幕面板铺满父级（即 video-panel） */
.danmu-panel {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none; /* 让鼠标事件穿透到下层的视频播放器 */
  z-index: 10;
  overflow: hidden; /* 确保弹幕不会飘出视频框 */
}

.danmu-layer {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
  pointer-events: none;
}

.danmu-item {
  position: absolute;
  white-space: nowrap;
  font-size: 20px;
  font-weight: 500;
  /* 增加经典的 B 站弹幕黑色描边阴影，防止在白色画面背景看不清 */
  text-shadow: 
    1px 1px 2px rgba(0, 0, 0, 0.8),
    -1px -1px 2px rgba(0, 0, 0, 0.8),
    1px -1px 2px rgba(0, 0, 0, 0.8),
    -1px 1px 2px rgba(0, 0, 0, 0.8);
  /* 开启动画，默认 8 秒划过 */
  animation: danmuMove 8s linear forwards;
  /* 开启 GPU 加速，让弹幕丝滑不卡顿 */
  will-change: transform, left;
}

/* 完美的全屏划过动画公式 */
@keyframes danmuMove {
  0% {
    left: 100%;
    transform: translateX(0);
  }
  100% {
    left: 0;
    transform: translateX(-100%);
  }
}
</style>