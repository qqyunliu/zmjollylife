<template>
  <div>
    <router-link v-if="linkable && userId && avatar" :to="`/user/${userId}`" target="_blank">
      <Cover
        :lazy="lazy"
        :source="avatar"
        :defaultImg="defaultAvatar"
        borderRadius="50%"
        :scale="1"
        :width="width"
      />
    </router-link>
    <Cover
      v-else-if="!linkable && userId && avatar"
      :lazy="lazy"
      :source="avatar"
      :defaultImg="defaultAvatar"
      borderRadius="50%"
      :scale="1"
      :width="width"
    />
    <div v-else-if="userId && nickId" class="avatar-letter" :style="{width: width + 'px', height: width + 'px', fontSize: (width * 0.4) + 'px'}">
      <router-link v-if="linkable" :to="`/user/${userId}`" target="_blank" class="letter-link">
        {{ nickId.charAt(0).toUpperCase() }}
      </router-link>
      <span v-else class="letter-link">{{ nickId.charAt(0).toUpperCase() }}</span>
    </div>
    <router-link v-else-if="linkable && userId" :to="`/user/${userId}`" target="_blank">
      <Cover
        :lazy="lazy"
        :source="avatar"
        :defaultImg="defaultAvatar"
        borderRadius="50%"
        :scale="1"
        :width="width"
      />
    </router-link>
    <Cover
      v-else-if="userId"
      :lazy="lazy"
      :source="avatar"
      :defaultImg="defaultAvatar"
      borderRadius="50%"
      :scale="1"
      :width="width"
    />
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

const defaultAvatar = "/user.png";

const props = defineProps({
  width: {
    type: Number,
    default: 50,
  },
  avatar: {
    type: String,
    default: '',
  },
  userId: {
    type: String,
  },
  nickId: {
    type: String,
    default: '',
  },
  lazy: {
    type: Boolean,
    default: true,
  },
  linkable: {
    type: Boolean,
    default: true,
  },
});
</script>

<style lang="scss" scoped>
div {
  display: inline-block;
}

.avatar-letter {
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #ffffff;
  border-radius: 50%;
  border: 1px solid #f0f0f0;
}

.letter-link {
  color: #e74c3c;
  text-decoration: none;
}
</style>
