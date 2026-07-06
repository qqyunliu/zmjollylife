<template>
  <div class="ucenter-home">
    <div class="welcome-card">
      <div class="user-profile">
        <Avatar :avatar="userInfo.avatar" :userId="loginStore.userInfo.userId" :nickId="userInfo.nickId" :width="70" :linkable="false"></Avatar>
        <div class="info">
          <div class="greeting">
            <span class="name">{{ userInfo.nickId || '神秘UP主' }}</span>，欢迎回到个人中心！
            <el-button class="edit-profile-btn" size="small" @click="openEditProfile">编辑资料</el-button>
          </div>
          <div class="signature">
            {{ userInfo.personIntroduction || '这个人很懒，还没有写个性签名~' }}
          </div>
        </div>
      </div>
      <div class="user-stats">
        <div class="stat-item">
          <div class="num">{{ userInfo.fansCount || 0 }}</div>
          <div class="label">粉丝数</div>
        </div>
        <div class="stat-item">
          <div class="num">{{ userInfo.focusCount || 0 }}</div>
          <div class="label">关注数</div>
        </div>
      </div>
    </div>

    <el-dialog v-model="editProfileVisible" title="编辑资料" width="520px">
      <el-form label-width="80px">
        <el-form-item label="昵称">
          <el-input v-model="editProfileForm.nickId" maxlength="20" show-word-limit />
        </el-form-item>
        <el-form-item label="签名">
          <el-input v-model="editProfileForm.personIntroduction" type="textarea" :rows="4" maxlength="200" show-word-limit />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editProfileVisible = false">取消</el-button>
        <el-button type="primary" :loading="savingProfile" @click="saveProfile">保存</el-button>
      </template>
    </el-dialog>

    <div class="dashboard-grid">
      <div class="data-panel">
        <div class="panel-header">
          <div class="title">实时数据总览</div>
        </div>
        <div class="data-cards" v-loading="loadingData">
          <div class="data-card play">
            <span class="iconfont icon-play2"></span>
            <div class="data-info">
              <div class="label">总播放量</div>
              <div class="value">{{ actualData.playCount || 0 }}</div>
            </div>
          </div>
          <div class="data-card like">
            <span class="iconfont icon-like"></span>
            <div class="data-info">
              <div class="label">稿件数</div>
              <div class="value">{{ actualData.videoCount || 0 }}</div>
            </div>
          </div>
          <div class="data-card coin">
            <span class="iconfont icon-coin"></span>
            <div class="data-info">
              <div class="label">粉丝数</div>
              <div class="value">{{ actualData.fansCount || 0 }}</div>
            </div>
          </div>
        </div>
      </div>

      <div class="video-status-panel">
        <div class="panel-header">
          <div class="title">稿件状态</div>
          <router-link to="/ucenter/postVideo" class="post-btn">去投稿</router-link>
        </div>
        <div class="status-list" v-loading="loadingVideo">
          <div class="status-item">
            <div class="label">全部稿件</div>
            <div class="num total">{{ videoData.total || 0 }}</div>
          </div>
          <div class="status-item">
            <div class="label">通过/展示中</div>
            <div class="num success">{{ videoData.viewing || 0 }}</div>
          </div>
          <div class="status-item">
            <div class="label">审核中</div>
            <div class="num checking">{{ videoData.checking || 0 }}</div>
          </div>
          <div class="status-item">
            <div class="label">未通过</div>
            <div class="num fail">{{ videoData.notPass || 0 }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, watch, getCurrentInstance } from 'vue';
import { useLoginStore } from "@/stores/loginStore.js";

const { proxy } = getCurrentInstance();
const loginStore = useLoginStore();

const userInfo = ref({});
const actualData = ref({});
const videoData = ref({});

const loadingData = ref(false);
const loadingVideo = ref(false);
const editProfileVisible = ref(false);
const savingProfile = ref(false);
const editProfileForm = reactive({
  nickId: '',
  personIntroduction: ''
});

// 1. 加载主页用户基础数据
const loadUserInfo = async () => {
  if (!loginStore.userInfo.userId) return;
  let result = await proxy.request({
    url: proxy.Api.uHomeGetUserInfo,
    params: { userId: loginStore.userInfo.userId }
  });
  if (result) {
    userInfo.value = {
      nickId: result.data.userInfo?.nickId || '神秘UP主',
      fansCount: result.data.fansCount || 0,
      focusCount: result.data.focusCount || 0,
      avatar: result.data.userInfo?.avatar,
      personIntroduction: result.data.userInfo?.personIntroduction
    };
  }
};

// 2. 加载实时统计数据 (播放、点赞等)
const loadActualData = async () => {
  loadingData.value = true;
  let result = await proxy.request({
    url: proxy.Api.ucGetActualTimeStatisticsInfo
  });
  loadingData.value = false;
  if (result) {
    actualData.value = {
      playCount: result.data.playCount || 0,
      videoCount: result.data.videoCount || 0,
      fansCount: result.data.fansCount || 0
    };
  }
};

// 3. 加载稿件状态数据
const loadVideoData = async () => {
  loadingVideo.value = true;
  let result = await proxy.request({
    url: proxy.Api.getUcenterVideoCountInfo
  });
  loadingVideo.value = false;
  if (result) {
    videoData.value = {
      total: (result.data.auditPassCount || 0) + (result.data.auditFailCount || 0) + (result.data.inProgress || 0),
      viewing: result.data.auditPassCount || 0,
      checking: result.data.inProgress || 0,
      notPass: result.data.auditFailCount || 0
    };
  }
};

const initData = async () => {
  await loadUserInfo()
  await loadActualData()
  await loadVideoData()
}

watch(
  () => loginStore.userInfo?.userId,
  (userId) => {
    if (!userId) {
      return
    }
    initData()
  },
  { immediate: true }
)

const openEditProfile = () => {
  editProfileForm.nickId = userInfo.value.nickId || ''
  editProfileForm.personIntroduction = userInfo.value.personIntroduction || ''
  editProfileVisible.value = true
}

const saveProfile = async () => {
  const nickId = (editProfileForm.nickId || '').trim()
  const personIntroduction = (editProfileForm.personIntroduction || '').trim()
  if (!nickId) {
    proxy.Message.warning('昵称不能为空')
    return
  }
  savingProfile.value = true
  let result = await proxy.request({
    url: proxy.Api.uHomeUpdateUserInfo,
    params: {
      nickId,
      personIntroduction
    }
  })
  savingProfile.value = false
  if (result) {
    userInfo.value.nickId = nickId
    userInfo.value.personIntroduction = personIntroduction
    if (loginStore.userInfo) {
      loginStore.userInfo.nickId = nickId
      loginStore.userInfo.nickName = nickId
      loginStore.userInfo.personIntroduction = personIntroduction
    }
    proxy.Message.success('保存成功')
    editProfileVisible.value = false
  }
}
</script>

<style lang="scss" scoped>
.ucenter-home {
  padding: 10px;

  .welcome-card {
    display: flex;
    justify-content: space-between;
    align-items: center;
    background: #ffffff;
    border-radius: 8px;
    padding: 30px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.03);
    margin-bottom: 20px;

    .user-profile {
      display: flex;
      align-items: center;
      gap: 20px;

      .info {
        .greeting {
          font-size: 18px;
          color: #18191c;
          margin-bottom: 8px;
          display: flex;
          align-items: center;
          gap: 12px;
          .name {
            font-weight: bold;
            color: #fb7299; /* B站粉 */
          }
        }
        .signature {
          font-size: 13px;
          color: #9499a0;
        }
      }
    }

    .user-stats {
      display: flex;
      gap: 40px;
      
      .stat-item {
        text-align: center;
        cursor: pointer;
        transition: transform 0.2s;

        &:hover {
          transform: translateY(-2px);
          .num { color: #00aeec; }
        }

        .num {
          font-size: 22px;
          font-weight: bold;
          color: #18191c;
          margin-bottom: 4px;
          transition: color 0.3s;
        }
        .label {
          font-size: 13px;
          color: #9499a0;
        }
      }
    }
  }

  .dashboard-grid {
    display: grid;
    grid-template-columns: 2fr 1fr;
    gap: 20px;

    .panel-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20px;
      
      .title {
        font-size: 16px;
        font-weight: bold;
        color: #18191c;
      }

      .post-btn {
        background: #00aeec;
        color: #fff;
        padding: 6px 16px;
        border-radius: 4px;
        text-decoration: none;
        font-size: 13px;
        transition: background 0.3s;
        &:hover { background: #00b5e5; }
      }
    }

    .data-panel, .video-status-panel {
      background: #ffffff;
      border-radius: 8px;
      padding: 24px;
      box-shadow: 0 2px 10px rgba(0, 0, 0, 0.03);
    }

    .data-cards {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 15px;

      .data-card {
        display: flex;
        align-items: center;
        padding: 20px;
        background: #f8f9fa;
        border-radius: 8px;
        transition: box-shadow 0.3s, transform 0.3s;

        &:hover {
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
          transform: translateY(-2px);
        }

        .iconfont {
          font-size: 32px;
          margin-right: 15px;
        }

        &.play .iconfont { color: #00aeec; }
        &.like .iconfont { color: #ff7f24; }
        &.coin .iconfont { color: #f3a034; }
        &.collect .iconfont { color: #fb7299; }

        .data-info {
          .label {
            font-size: 13px;
            color: #9499a0;
            margin-bottom: 4px;
          }
          .value {
            font-size: 20px;
            font-weight: bold;
            color: #18191c;
          }
        }
      }
    }

    .status-list {
      display: flex;
      flex-direction: column;
      gap: 15px;

      .status-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 15px;
        background: #f8f9fa;
        border-radius: 8px;

        .label {
          font-size: 14px;
          color: #61666d;
        }
        .num {
          font-size: 18px;
          font-weight: bold;
          &.total { color: #18191c; }
          &.success { color: #33d17a; }
          &.checking { color: #f6a623; }
          &.fail { color: #f56c6c; }
        }
      }
    }
  }
}
</style>
