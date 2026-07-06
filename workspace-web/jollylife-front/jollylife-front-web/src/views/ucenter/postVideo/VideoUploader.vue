<template>
  <div>
    <div v-show="!startUpload"
         class="uploader-start-panel">
      <VideoUploadStart @addFile="addFile"></VideoUploadStart>
    </div>
    <div v-if="startUpload">
      <div class="file-list"
           v-draggable="[fileList,{animation:150,handle:'.video-p'}]">
        <div class="file-item"
             v-for="(item,index) in fileList"
             :key="index">
          <div class="video-p">
            <div class="iconfont icon-video"> </div>
            <div class="video-p-info">P{{index+1}}</div>
          </div>
          <div class="video-info">
            <div class="video-title">
              <div class="upload-info">
                <div class="title">
                  <!-- 文件名编辑输入框：item.edit 为 true 时显示，失去焦点触发 endEdit -->
                  <el-input v-show="item.edit"
                            v-model="item.fileName"
                            :id="'file-input' + item.uid"
                            size="small"
                            @blur="endEdit(index)"></el-input>
                  <!-- 文件名展示：item.edit 为 false 时显示，点击触发 editFileName 进入编辑 -->
                  <div v-show="!item.edit"
                       class="title-show"
                       @click="editFileName(index)">
                    {{ item.fileName }}
                  </div>
                </div>
                <div class="upload-status">
                  <span v-if="item.status === 'uploading'">
                    已上传：{{ proxy.Utils.size2Str(item.uploadSize) }}/{{ proxy.Utils.size2Str(item.totalSize) }}
                  </span>
                  <span v-else
                        :class="['iconfont', 'icon-' + STATUS[item.status].icon]"
                        :style="{ color: STATUS[item.status].color }">
                    {{ STATUS[item.status].desc }}
                  </span>
                </div>
              </div>
              <div class="op">
                <div class="item percent"
                     v-if="item.status === 'uploading'">
                  {{ item.uploadPercent }}%
                </div>
                <template v-if="item.status === 'uploading'">
                  <div v-if="item.pause"
                       class="item iconfont icon-play3"
                       @click="restartUpload(item.uid)"></div>
                  <div v-else
                       class="item iconfont icon-pause"
                       @click="pauseUpload(item.uid)"></div>
                </template>
                <div class="item iconfont icon-del"
                     @click="delFile(index)"></div>
              </div>
            </div>
            <div class="video-progress"
                 v-if="item.status=='uploading'||item.status=='success'">
              <el-progress :percentage="item.uploadPercent"
                           :show-text="false"
                           :stroke-width="3"
                           :status="item.status=='uploading' ? '':'success'">
              </el-progress>
            </div>
          </div>
        </div>
      </div>
      <div class="add-video-btn"
           v-if="fileList.length<sysSettingStore.sysSetting.videoPCount||10">
        <el-upload multiple
                   :show-file-list="false"
                   :http-request="addFile"
                   :accept="proxy.videoAccept">
          <el-button type="primary">添加分P</el-button>
        </el-upload>
      </div>
    </div>
  </div>
</template>

<script setup>
import VideoUploadStart from "./VideoUploadStart.vue";
import { ref, reactive, getCurrentInstance, nextTick, onUnmounted } from "vue";
import { useRoute, useRouter } from "vue-router";

const { proxy } = getCurrentInstance();
const route = useRoute();
const router = useRouter();

import { useSysSettingStore } from "@/stores/sysSettingStore"
const sysSettingStore = useSysSettingStore();
import { mitter } from '@/eventbus/eventBus.js';
import { vDraggable } from "vue-draggable-plus";

const STATUS = {
  emptyfile: {
    value: "emptyfile",
    desc: "文件为空",
    color: "#F75000",
    icon: "error",
  },
  largefile: {
    value: "largefile",
    desc: "文件超过大小" + sysSettingStore.sysSetting.videoSize + "MB",
    color: "#F75000",
    icon: "error",
  },
  waiting: {
    icon: "waiting", // 需确保对应图标组件可识别该值
    value: "waiting",
    desc: "等待上传",
    color: "#e6a23c" // 橙色系，标识等待状态
  },
  uploading: {
    icon: "upload",
    value: "uploading",
    desc: "上传中",
    color: "#409eff" // 蓝色系，标识进度中状态
  },
  fail: {
    icon: "error",
    value: "fail",
    desc: "上传失败",
    color: "#F75000" // 红色系，标识错误状态
  },
  success: {
    icon: "success",
    value: "success",
    desc: "上传完成",
    color: "#67c23a" // 绿色系，标识成功状态
  }
};


const fileList = ref([]);

const MAX_UPLOADING = proxy.maxUploading;
const CHUNK_SIZE = proxy.chunkSize;
const editFileName = (index) => {
  const currentFile = fileList.value[index];
  currentFile.edit = true;
  nextTick(() => {
    const input = document.querySelector("#file-input" + currentFile.uid);
    input.focus();
  });
};

const endEdit = (index) => {
  const currentFile = fileList.value[index];
  currentFile.edit = false;
};


const startUpload = ref(false);
mitter.on("startUpload", () => {
  startUpload.value = true;
  fileList.value = [];
});

const getFileByUid = (uid) => {
  const currentFile = fileList.value.find((item) => {
    return item.uid === uid
  })
  return currentFile
}


const addFile = (file) => {
  file = file.file;
  if (fileList.value.length >= sysSettingStore.sysSetting.videoPCount) {
    proxy.Message.warning(
      `最多可以添加${sysSettingStore.sysSetting.videoPCount}个视频`
    );
    return;
  }
  let fileName = proxy.Utils.getFileName(file.name);
  const fileItem = {
    file: file,
    uid: file.uid,
    fileName: fileName,
    status: STATUS.waiting.value,
    uploadSize: 0,
    totalSize: file.size,
    uploadPercent: 0,
    pause: false,
    chunkIndex: 0,
    errorMsg: null,
  };
  fileList.value.push(fileItem);
  if (fileItem.totalSize === 0) {
    fileItem.status = STATUS.emptyfile.value;
    return;
  }
  if (fileItem.totalSize > sysSettingStore.sysSetting.videoSize * 1024 * 1024) {
    fileItem.status = STATUS.largefile.value;
    return;
  }

  let uploadingFiles = fileList.value.filter((item) => {
    return item.status === STATUS.uploading.value;
  });

  if (uploadingFiles.length > MAX_UPLOADING) {
    return;
  }
  uploadFile(fileItem.uid);
};

const uploadFile = async (uid, chunkIndex) => {
  const currentFile = getFileByUid(uid);
  currentFile.status = STATUS.uploading.value;
  chunkIndex = chunkIndex ? chunkIndex : 0;

  const file = currentFile.file;
  const fileSize = currentFile.totalSize;
  const chunks = Math.ceil(fileSize / CHUNK_SIZE);
  if (!currentFile.uploadId) {
    let resultData = await proxy.request({
      url: proxy.Api.preUploadVideo,
      params: {
        fileName: currentFile.fileName,
        chunks,
      },
      errorCallback: (errorMsg) => {
        currentFile.status = STATUS.fail.value;
        currentFile.errorMsg = errorMsg;
      }
    });
    if (!resultData) {
      return;
    }
    currentFile.uploadId = resultData.data;
  }
  for (let i = chunkIndex; i < chunks; i++) {
    if (currentFile.pause || currentFile.del) {
      break;
    }
    let start = i * CHUNK_SIZE;
    let end = start + CHUNK_SIZE >= fileSize ? fileSize : start + CHUNK_SIZE;
    let chunkFile = file.slice(start, end);
    let uploadResult = await proxy.request({
      url: proxy.Api.uploadVideo,
      dataType: "file",
      params: {
        chunkFile: chunkFile,
        chunkIndex: i,
        uploadId: currentFile.uploadId,
      },
      showError: false,
      errorCallback: (errorMsg) => {
        currentFile.status = STATUS.fail.value;
        currentFile.errorMsg = errorMsg;
      },
      uploadProgressCallback: (event) => {
        let loaded = event.loaded;
        if (loaded >= fileSize) {
          loaded = fileSize;
        }
        currentFile.uploadSize = i * CHUNK_SIZE + loaded;
        currentFile.uploadPercent = Math.floor(
          (currentFile.uploadSize / fileSize) * 100
        );
      },
    });
    if (uploadResult == null) {
      break;
    }
    currentFile.chunkIndex = i;
    if (i < chunks - 1) {
      continue;
    }
    currentFile.status = STATUS.success.value;
    currentFile.uploadPercent = 100;
    const nextItem = fileList.value.find((item) => {
      return item.status == STATUS.waiting.value;
    });
    if (nextItem) {
      uploadFile(nextItem.uid);
    }
  }
};



const pauseUpload = (uid) => {
  const currentFile = getFileByUid(uid);
  currentFile.pause = true;
};

const restartUpload = (uid) => {
  const currentFile = getFileByUid(uid);
  currentFile.pause = false;
  uploadFile(uid, currentFile.chunkIndex);
};

const delFile = async (index) => {
  const currentFile = fileList.value[index];
  currentFile.del = true;
  fileList.value.splice(index, 1);
  if (currentFile.fileId) {
    return;
  }
  await proxy.Request({
    url: proxy.Api.delUploadVideo,
    params: {
      uploadId: currentFile.uploadId,
    },
    showError: false,
  });
};



const getUploadFileList = () => {
  try {
    // 安全检查
    if (!fileList || !Array.isArray(fileList.value)) {
      console.error('fileList is not properly initialized');
      return [];
    }

    let failCount = 0;
    let noUploadCount = 0;
    let uploadingCount = 0;
    let waitingCount = 0;

    // 统计各种状态的文件数量
    fileList.value.forEach(item => {
      if (!item || !item.status) return;

      if (item.status === STATUS.fail?.value || item.status === STATUS.emptyfile?.value) {
        failCount++;
      } else if (item.status === STATUS.uploading?.value) {
        uploadingCount++;
      } else if (item.status === STATUS.waiting?.value) {
        waitingCount++;
      }
    });

    noUploadCount = uploadingCount + waitingCount;

    // 检查上传状态
    if (failCount > 0) {
      proxy.Message?.warning("请删除上传失败的文件");
      return null;
    }

    if (noUploadCount > 0) {
      proxy.Message?.warning("文件还未上传完成无法提交");
      return null;
    }

    // 构建上传文件列表
    const uploadFileList = fileList.value
      .filter(item => item && item.status === STATUS.success?.value && item.uploadId)
      .map(item => ({
        uploadId: item.uploadId,
        fileId: item.fileId || '',
        fileName: item.fileName || '',
        status: item.status
      }));

    // 检查是否有成功上传的文件
    if (uploadFileList.length === 0) {
      proxy.Message?.warning("没有可提交的文件");
      return null;
    }

    return uploadFileList;

  } catch (error) {
    console.error('getUploadFileList error:', error);
    return [];
  }
};

const initUploader = (_startUpload, videoList) => {
  startUpload.value = _startUpload;
  fileList.value.splice(0, fileList.value.length);
  videoList.forEach((item) => {
    if (item.transferResult === 1) {
      item.status = STATUS.success.value;
    } else {
      item.status = STATUS.fail.value;
    }
    item.uid = item.fileId;
    item.uploadPercent = 100;
    fileList.value.push(item);
  });
};

defineExpose({
  getUploadFileList,
  initUploader,
});

onUnmounted(() => {
  mitter.off("startUpload");
});
</script>


<style lang="scss" scoped>
.file-list {
  background: #f6f7f8;
  border-radius: 5px;
  margin: 0px 200px 0px 20px;
  .file-item {
    padding: 10px;
    display: flex;
    margin-bottom: 10px;
    .video-p {
      flex-shrink: 0;
      position: relative;
      width: 44px;
      height: 40px;
      cursor: move;
      .icon-video {
        font-size: 40px;
        color: #a6def1;
        padding: 0px;
      }
      .video-p-info {
        width: 35px;
        line-height: 40px;
        text-align: center;
        color: #fff;
        top: 0px;
        left: 0px;
        z-index: 1;
        position: absolute;
      }
    }
    .video-info {
      flex: 1;
      min-width: 0;
      padding-left: 10px;
      .video-title {
        display: flex;
        align-items: center;
        width: 100%;
        .upload-info {
          width: 100%;
          min-width: 0;
          flex: 1;
          .title {
            width: 100%;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;

            .title-show {
              line-height: 24px;
              padding-left: 7px;
              font-size: 12px;
            }
          }
          .upload-status {
            margin-top: 5px;
            color: #999;
            font-size: 12px;
            .iconfont {
              font-size: 12px;

              &::before {
                font-size: 16px;
                margin-right: 2px;
              }
            }
          }
        }
        .op {
          margin-left: 10px;
          display: flex;
          align-items: center;
          color: #909090;
          .item {
            margin-right: 10px;
            font-size: 13px;
          }
          .percent {
            width: 30px;
          }
          .iconfont {
            cursor: pointer;
            font-size: 20px;
            color: #909090;
          }
        }
      }
      .video-progress {
        margin-right: 5px;
      }
    }
  }
}
.add-video-btn {
  padding-left: 20px;
  margin-bottom: 10px;
}
</style>