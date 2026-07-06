<template>
  <div
    class="image-panel"
    ref="coverRef"
    :style="{
      'border-radius': borderRadius,
      width: width ? width + 'px' : '100%',
      height: width ? width * scale + 'px' : '100%',
    }"
  >
    <!-- 调试信息 - 找到问题后请删除 -->
    <div v-if="false" style="position: fixed; top: 20px; left: 20px; background: #fff; border: 3px solid #28a745; font-size: 16px; padding: 20px; z-index: 9999; width: 500px; box-shadow: 0 4px 12px rgba(0,0,0,0.5); border-radius: 8px; font-family: monospace; line-height: 1.6;">
      <div style="color: #28a745; font-size: 18px; font-weight: bold; margin-bottom: 15px;">✅ Cover组件调试 (已修复)</div>
      
      <div><strong>source:</strong> <span style="color: #333;">{{ source || '❌空' }}</span></div>
      <div><strong>defaultImg:</strong> <span style="color: #333;">{{ defaultImg || '❌空' }}</span></div>
      <div><strong>fileSource:</strong> <span style="color: #28a745; font-weight: bold;">{{ fileSource || '❌空' }}</span></div>
      <div><strong>fileImage:</strong> <span style="color: #333;">{{ fileImage || '❌空' }}</span></div>
      
      <div style="margin: 15px 0; padding: 10px; background: #d4edda; border: 1px solid #c3e6cb; border-radius: 4px;">
        <strong>🔧 修复说明:</strong><br>
        <span style="color: #155724;">绕过有问题的getLocalImage，直接使用正确路径</span>
      </div>
      
      <div style="margin: 10px 0; padding: 8px; background: {{ (fileSource || fileImage) ? '#d4edda' : '#f8d7da' }}; border-radius: 4px; font-weight: bold; font-size: 17px;">
        🎯 最终结果: {{ (fileSource || fileImage) ? '✅会显示图片' : '❌显示"请选择图片"' }}
      </div>
      
      <div style="margin-top: 10px; font-size: 14px; color: #6c757d;">
        如果显示正确，请将此调试信息删除
      </div>
    </div>

    <el-image
      :lazy="lazy"
      :src="fileSource || fileImage"
      :fit="fit"
      v-if="fileSource || fileImage"
      @click="showViewerHandler"
    >
      <template #placeholder>
        <div class="loading" :style="{ height: loadingHeight + 'px' }">
          <img :src="proxy.Utils.getLocalImage('playing.gif')" />
        </div>
      </template>
      <template #error>
        <img
          :src="proxy.Utils.getLocalImage(img404)"
          class="el-image__inner"
          :style="{ 'object-fit': fit}"
        />
      </template>
    </el-image>
    <div v-else class="no-image">请选择图片</div>
    <el-image-viewer
      :hide-on-click-modal="true"
      @close="
        () => {
          showViewer = false;
        }
      "
      v-if="showViewer"
      :url-list="imageList"
      :teleported="true"
    ></el-image-viewer>
  </div>
</template>

<script setup>
import { ref, getCurrentInstance, nextTick, onMounted, onUpdated, computed } from "vue";
const { proxy } = getCurrentInstance();
import { useRoute, useRouter } from "vue-router";
const route = useRoute();
const router = useRouter();

const props = defineProps({
  source: {
    type: [String, File],
  },
  width: {
    type: Number,
  },
  fit: {
    type: String,
    default: "scale-down",
  },
  preview: {
    type: Boolean,
    default: false,
  },
  defaultImg: {
    type: String,
  },
  img404: {
    type: String,
    default: "404_cover.png",
  },
  borderRadius: {
    type: String,
    default: "5px",
  },
  lazy: {
    type: Boolean,
    default: true,
  },
  scale: {
    type: Number,
    default: 0.6,
  },
});

const fileImage = ref();

const fileSource = computed(() => {
  console.log('🔍=== Cover组件调试开始 ===');
  console.log('📋 props.source:', props.source);
  console.log('📋 props.defaultImg:', props.defaultImg);
  
  // 如果有source，优先使用source
  if (props.source) {
    console.log('✅ 使用source路径');
    if (props.source instanceof File) {
      console.log('📁 source是File对象');
      let img = new FileReader();
      img.readAsDataURL(props.source);
      img.onload = ({ target }) => {
        fileImage.value = target.result;
        console.log('📁 File读取完成:', target.result?.substring(0, 50) + '...');
      };
      return null; // 返回null，等待fileImage.value被设置
    } else if (typeof props.source === "string") {
      const sourcePath = `${proxy.Api.sourcePath}${props.source}`;
      console.log('🔗 source字符串路径:', sourcePath);
      return sourcePath;
    }
  }
  
  // 如果没有source但有defaultImg，使用默认图片
  if (props.defaultImg) {
    console.log('🎯 使用defaultImg:', props.defaultImg);
    
    // 检查是否已经是完整的URL（通过import导入的或以/开头的）
    if (props.defaultImg.startsWith('data:') || props.defaultImg.startsWith('http') || props.defaultImg.startsWith('/')) {
      console.log('🌐 defaultImg是完整URL，直接使用:', props.defaultImg);
      console.log('🔍=== Cover组件调试结束 - 返回完整URL ===');
      return props.defaultImg;
    }
    
    // 对于相对路径，直接添加/前缀，不使用有问题的getLocalImage
    const directPath = '/' + props.defaultImg.replace(/^\/+/, ''); // 移除开头的斜杠然后添加一个
    console.log('🔄 直接使用路径（绕过getLocalImage）:', directPath);
    console.log('🔍=== Cover组件调试结束 - 返回直接路径 ===');
    return directPath;
  }
  
  console.log('❌ 没有找到图片源，返回null');
  console.log('🔍=== Cover组件调试结束 - 返回null ===');
  return null;
});

const imageList = computed(() => {
  if (!props.preview || !props.source) {
    return [];
  }
  const sourceImg = props.Api.sourcePath + props.source.replace(proxy.imageThumbnailSuffix, "");
  return [sourceImg];
});

const showViewer = ref(false);
const showViewerHandler = () => {
  if (!props.preview) {
    return;
  }
  showViewer.value = true;
};

const coverRef = ref();
const loadingHeight = ref();
onMounted(() => {
  loadingHeight.value = coverRef.value.clientWidth * props.scale;
});
</script>

<style lang="scss" scoped>
.image-panel {
  position: relative;
  overflow: hidden;
  cursor: pointer;
  background: #f8f8f8;
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  width: 100%;
  
  :deep(.el-image) {
    width: 100%;
    height: 100%;
  }
  
  :deep(.is-loading) {
    display: none;
  }
  
  :deep(.el-image__wrapper) {
    position: relative;
    vertical-align: top;
    width: 100%;
    height: 100%;
    display: flex;
  }
  
  .icon-image-error {
    margin: 0 auto;
    font-size: 20px;
    color: #838383;
    height: 100%;
  }
  
  .loading {
    width: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    
    img {
      width: 20px;
    }
  }
  
  .no-image {
    text-align: center;
    color: #9f9f9f;
  }
}
</style>