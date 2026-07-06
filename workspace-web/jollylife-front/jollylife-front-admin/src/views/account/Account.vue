<template>
  <div class="login-container">
    <div class="login-box">
      <h1 class="login-title">用户登录</h1>

      <form @submit.prevent="doSubmit"
            class="login-form">
        <!-- 用户名/邮箱输入框 -->
        <div class="form-group">
          <label for="account">用户名或邮箱</label>
          <input type="text"
                 id="account"
                 v-model="formData.account"
                 placeholder="请输入用户名或邮箱" />
          <span class="error-message"
                v-if="errors.account">{{ errors.account }}</span>
        </div>

        <!-- 密码输入框 -->
        <div class="form-group">
          <label for="password">密码</label>
          <input type="password"
                 id="password"
                 v-model="formData.password"
                 placeholder="请输入密码" />
          <span class="error-message"
                v-if="errors.password">{{ errors.password }}</span>
        </div>

        <!-- 验证码 -->
        <div class="form-group">
          <label for="checkCode">验证码</label>
          <div class="check-code-panel">
            <input type="text"
                   id="checkCode"
                   v-model="formData.checkCode"
                   placeholder="请输入验证码" />
            <img :src="checkCodeInfo.checkCode"
                 @click="changeCheckCode" />
          </div>
          <span class="error-message"
                v-if="errors.checkCode">{{ errors.checkCode }}</span>
        </div>

        <!-- 登录按钮 -->
        <button type="submit"
                class="login-button">登录
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, getCurrentInstance, nextTick, onMounted, onUpdated, reactive } from "vue";
const { proxy } = getCurrentInstance();
import { useRoute, useRouter } from "vue-router";
const route = useRoute();
const router = useRouter();
import md5 from "js-md5";

// 表单数据
const formData = ref({
  account: "",
  password: "",
  checkCode: "",
});

// 错误信息
const errors = reactive({
  account: "",
  password: "",
  checkCode: "",
});

// 验证码
const checkCodeInfo = ref({});
const changeCheckCode = async () => {
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

// 表单验证规则
const rules = {
  account: [
    { required: true, message: "请输入用户名或邮箱" },
    { min: 3, message: "用户名至少3个字符" },
  ],
  password: [
    { required: true, message: "请输入密码" },
    { min: 6, message: "密码至少6个字符" },
  ],
  checkCode: [
    { required: true, message: "请输入验证码" },
  ],
};

// 验证表单
const validateForm = () => {
  // 清空之前的错误信息
  Object.keys(errors).forEach(key => {
    errors[key] = "";
  });

  let isValid = true;

  // 验证用户名
  if (!formData.value.account) {
    errors.account = "请输入用户名或邮箱";
    isValid = false;
  } else if (formData.value.account.length < 3) {
    errors.account = "用户名至少3个字符";
    isValid = false;
  }

  // 验证密码
  if (!formData.value.password) {
    errors.password = "请输入密码";
    isValid = false;
  } else if (formData.value.password.length < 6) {
    errors.password = "密码至少6个字符";
    isValid = false;
  }

  // 验证验证码
  if (!formData.value.checkCode) {
    errors.checkCode = "请输入验证码";
    isValid = false;
  }

  return isValid;
};

// 初始化验证码
changeCheckCode();

const doSubmit = async () => {
  // 验证表单
  if (!validateForm()) {
    return;
  }

  try {
    let params = {};
    Object.assign(params, formData.value);
    params.checkCodeKey = checkCodeInfo.value.checkCodeKey;
    params.password = md5(params.password);

    let result = await proxy.request({
      url: proxy.Api.login,
      params, // 修复了拼写错误
    });

    if (!result) {
      return;
    }

    router.push("/home");
    proxy.Message.success("登录成功"); // 修复了语法错误
    proxy.VueCookies.set("account", result.data);
  } catch (error) {
    console.error("登录失败:", error);
    // 可以在这里处理登录失败的情况
    changeCheckCode(); // 刷新验证码
  }
};
</script>

<style scoped>
/* 保持原有样式不变 */
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #7e6565;
  background-image: linear-gradient(120deg, #20345c 0%, #e9ebee 100%);
  padding: 20px;
}

.login-box {
  width: 100%;
  max-width: 400px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  padding: 30px;
  box-sizing: border-box;
}

.login-title {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
  font-size: 24px;
  font-weight: 500;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  font-size: 14px;
  color: #555;
}

.form-group input {
  padding: 12px 15px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  transition: border-color 0.3s;
}

.form-group input:focus {
  border-color: #409eff;
  outline: none;
}

.error-message {
  color: #f56c6c;
  font-size: 12px;
  height: 16px;
}

.login-button {
  background-color: #409eff;
  color: white;
  border: none;
  padding: 12px;
  border-radius: 4px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.3s;
}

.login-button:hover {
  background-color: #66b1ff;
}

.login-button:disabled {
  background-color: #a0cfff;
  cursor: not-allowed;
}

.check-code-panel {
  display: flex;
  align-items: center;
  gap: 10px;
}

.check-code-panel input {
  flex: 1;
  padding: 12px 15px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.check-code-panel img {
  height: 40px;
  cursor: pointer;
  border: 1px solid #ddd;
  border-radius: 4px;
}
</style>