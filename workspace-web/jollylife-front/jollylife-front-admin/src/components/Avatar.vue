<template>


  <div>
    <!-- 已登录：可点击跳转用户页 -->
    <router-link v-if="userId" :to="`/user/${userId}`" target="_blank">
      <Cover
        :lazy="lazy"
        :source="avatar"
        :defaultImg="defaultAvatar"
        borderRadius="50%"
        :scale="1"
        :width="width"
      />
    </router-link>

    <!-- 未登录：显示默认头像 -->
    <Cover
      v-else
      :lazy="lazy"
      :source="avatar"
      :defaultImg="defaultAvatar"
      borderRadius="50%" 
      :scale="1"
      :width="width"
    />
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRoute, useRouter } from "vue-router";
const route = useRoute();
const router = useRouter();

const defaultAvatar = "/user.png";// 替换为真实路径

const props = defineProps({
  width: {
    type: Number,
    default: 50,
  },
  avatar: {
    type: String,
    default: '', // 默认头像路径
  },
  userId: {
    type: String,
  },
  lazy: {
    type: Boolean,
    default: true,
  },
});
</script>

<style lang="scss" scoped>
/* 可根据需要添加样式 */
div {
  display: inline-block;
}
</style>