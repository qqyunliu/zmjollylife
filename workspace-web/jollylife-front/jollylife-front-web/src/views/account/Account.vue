<template>
  <Dialog :show="loginStore.showLogin"
          :title="dialogConfig.title"
          :buttons="dialogConfig.buttons"
          width="1000px"
          :showCancel="false"
          :top="150"
          @close="closeDialog">
    <div class="dialog-panel">
      <div class="bg">
        <img src="../../assets/login_bg.png" />
      </div>
      <el-form class="login-register"
               :model="formData"
               :rules="rules"
               ref="formDataRef"
               @submit.prevent>
        <div class="tab-panel">
          <div :class="[onType == 1 ? 'active' : '']"
               @click="showPanel(1)">
            登录
          </div>
          <el-divider direction="vertical" />
          <div :class="[onType == 0 ? 'active' : '']"
               @click="showPanel(0)">
            注册
          </div>
        </div>

        <el-form-item prop="email">
          <el-input clearable
                    placeholder="请输入邮箱"
                    v-model.trim="formData.email"
                    :maxlength="150"
                    size="large">
            <template #prefix>
              <span class="iconfont icon-account"></span>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item prop="password"
                      v-if="onType == 1">
          <el-input show-password
                    placeholder="请输入密码"
                    v-model.trim="formData.password"
                    size="large">
            <template #prefix>
              <span class="iconfont icon-password"></span>
            </template>
          </el-input>
        </el-form-item>

        <div v-if="onType == 0">
          <el-form-item prop="nickName">
            <el-input placeholder="请输入昵称"
                      v-model.trim="formData.nickName"
                      :maxlength="150"
                      size="large">
              <template #prefix>
                <span class="iconfont icon-account"></span>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="registerPassword">
            <el-input show-password
                      placeholder="请输入密码"
                      v-model.trim="formData.registerPassword"
                      size="large">
              <template #prefix>
                <span class="iconfont icon-password"></span>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="reRegisterPassword"
                        v-if="onType == 0">
            <el-input show-password
                      placeholder="请再次输入密码"
                      v-model.trim="formData.reRegisterPassword"
                      size="large">
              <template #prefix>
                <span class="iconfont icon-password"></span>
              </template>
            </el-input>
          </el-form-item>
        </div>
        <el-form-item prop="checkCode">
          <div class="check-code-panel">
            <div class="input">
              <el-input placeholder="请输入验证码"
                        v-model.trim="formData.checkCode"
                        size="large">
                <template #prefix>
                  <span class="iconfont icon-checkcode"></span>
                </template>
              </el-input>
            </div>

            <img :src="checkCodeInfo.checkCode"
                 @click="changeCheckCode">
          </div>
        </el-form-item label=""
                       props=" ">
        <!--ei-input-->
        <el-button type="primary"
                   @click="doSubmit"
                   class="login-btn"
                   size="large">
          <span v-if="onType == 0">注册</span>
          <span v-if="onType == 1">登录</span>
        </el-button>
      </el-form>
    </div>
  </Dialog>
</template>

<script setup>
import { ref, getCurrentInstance, nextTick, onMounted, onUpdated } from "vue";
const { proxy } = getCurrentInstance();
import { useRoute, useRouter } from "vue-router";
const route = useRoute();
const router = useRouter();
import md5 from "js-md5";
import Dialog from "../../components/Dialog.vue";

import { useLoginStore } from "../../stores/loginStore.js";
const loginStore = useLoginStore();

const checkCodeInfo = ref({});
const changeCheckCode = async () => {
  // 这里添加验证码刷新逻辑Q

  let result = await proxy.request({
    url: proxy.Api.checkCode,
    params: {
      type: "login",
    },
  });
  if (!result) {
    return;
  }
  checkCodeInfo.value = result.data;
};

const submitForm = () => {
  // 这里添加表单提交逻辑
  console.log("表单提交");
  dialogConfig.value.show = false;
};

const handleClose = () => {
  dialogConfig.show = false;
};

const dialogConfig = ref({
  buttons: [],
});

const formData = ref({});
const formDataRef = ref(null);

const checkRePassword = (rule, value, callback) => {
  if (value !== formData.value.registerPassword) {
    callback(new Error("两次输入的密码不一致!"));
  } else {
    callback();
  }
};

const checkCode = (rule, value, callback) => {
  if (value != formData.value.registerPassword) {
    callback(new Error(rule.message));
  } else {
    callback();
  }
};
const rules = {
  email: [
    { required: true, message: "请输入邮箱" },
    {
      validator: proxy.Verify.email,
      message: "请输入正确的邮箱",
    }, //pe: "email", message: "请输入正确的邮箱" }
  ],
  password: [{ required: true, message: "请输入密码" }],
  nickname: [{ required: true, message: "请输入昵称" }],
  registerPassword: [
    { required: true, message: "请输入密码" },
    {
      validator: proxy.Verify.password,
      message: "密码至少6位，包含字母和数字",
    },
  ],
  reRegisterPassword: [
    { required: true, message: "请再次输入密码" },
    {
      validator: proxy.Verify.password,
      message: "两次输入的密码不一致",
    },
  ],

  checkCode: [{ required: true, message: "请输入验证码" }],
};

const onType = ref(1);

const resetForm = () => {
  changeCheckCode();
  nextTick(() => {
    formDataRef.value.resetFields();
    formData.value = {};
  });
};


const doSubmit = () => {
  formDataRef.value.validate(async (valid) => {
    if (!valid) {
      return;
    }
    let params = {};
    Object.assign(params, formData.value);
    params.checkCodeKey = checkCodeInfo.value.checkCodeKey;
    if (onType.value == 1) {
      params.password = md5(params.password);
    } else if (onType.value == 0) {
      // 注册时发送原始密码，让后端加密
      delete params.reRegisterPassword;
    }
    let result = await proxy.request({
      url: onType.value == 0 ? proxy.Api.register : proxy.Api.login,
      params,
      errorCallback: () => {
        changeCheckCode();
      },
    });

    console.log("完整的result:", result);
    console.log("onType.value:", onType.value);
    if (!result) {
      return;
    }
    if (onType.value == 0) {
      proxy.Message.success("注册成功，请登录");
      showPanel(1);
    } else if (onType.value == 1) {
      proxy.Message.success("登录成功");
      loginStore.setLogin(false);
      loginStore.saveUserInfo(result.data);
    }
  });
};

const closeDialog = () => {
  loginStore.showLogin = false;
};

const showPanel = (type) => {
  onType.value = type;
  if (loginStore.showLogin) {
    resetForm();
  }
};

onUpdated(() => {
  showPanel(1);
});

onMounted(() => {
  showPanel(1);
});
</script>

<style lang="scss" scoped>
.dialog-panel {
  display: flex;
  align-items: center;
  justify-content: space-around;
  .bg {
    width: 450px;
    height: 480px;
    overflow: hidden;
    img {
      width: 100%;
    }
  }
  .login-register {
    width: 350px;
    .tab-panel {
      margin: 10px auto;
      display: flex;
      width: 130px;
      font-size: 18px;
      align-items: centedthr;
      justify-content: space-around;
      cursor: pointer;
      .active {
        color: var(--blue2);
      }
    }
    .no-account {
      width: 100%;
      display: flex;
      justify-content: space-around;
    }
    .login-btn {
      width: 100%;
    }
    .bottom-btn {
      margin-bottom: 0px;
    }
  }
}

.check-code-panel {
  display: flex;
  align-items: center;
  width: 100%;
  .input {
    flex: 1;
  }
  .right-panel {
    margin-left: 5px;
    cursor: pointer;
  }
  img {
    cursor: pointer;
  }
}
</style>>