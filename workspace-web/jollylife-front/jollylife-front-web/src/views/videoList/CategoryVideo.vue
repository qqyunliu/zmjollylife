<template>
  <div class="category-video-body"></div>
  <videoList></videoList>
</template>

<script setup>
import videoList from "../videoList/videoList.vue";
import { ref, reactive, getCurrentInstance, nextTick, onMounted, watch } from "vue";
const { proxy } = getCurrentInstance();
import { useRoute, useRouter } from "vue-router";
const route = useRoute();
const router = useRouter();

import { useCategoryStore } from "../../stores/categoryStore";
const categoryStore = useCategoryStore();
const categoryFixied = ref(false);

const loadCategory = () => {
  categoryStore.setCureentPCategory(route.params.pCategoryCode);
};

onMounted(() => {
  loadCategory();
});

watch(() => route.params.pCategoryCode, () => {
  loadCategory();
});
</script>

<style scoped>
</style>