<template>
  <div class="category"
       v-show="showType == 0">
    <div class="category-list"
         :style="{
      'grid-template-columns': `repeat(${rowCategoryCount},1fr)`,
    }">
      <template v-for="(item, index) in visibleCategories"
                :key="item.categoryId">
        <!-- 有子分类的显示 popover -->
        <el-popover v-if="item.children && item.children.length > 0"
                    width="187"
                    trigger="hover"
                    :show-arrow="false"
                    :offset="5"
                    :placement="index < rowCategoryCount ? 'top' : 'bottom'">
          <template #reference>
            <router-link class="category-item"
                         :class="{ active: item.categoryCode === route.params.pCategoryCode }"
                         :to="`/v/${item.categoryCode}`">
              {{ item.categoryName }}
            </router-link>
          </template>
          <div class="child-list">
            <router-link class="child"
                         v-for="sub in item.children"
                         :key="sub.categoryCode"
                         :title="sub.categoryName"
                         :to="`/v/${item.categoryCode}/${sub.categoryCode}`">
              {{ sub.categoryName }}
            </router-link>
          </div>
        </el-popover>

        <!-- 没有子分类的直接显示 -->
        <router-link v-else
                     class="category-item"
                     :class="{ active: item.categoryCode === route.params.pCategoryCode }"
                     :to="`/v/${item.categoryCode}`">
          {{ item.categoryName }}
        </router-link>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from "vue";
import { useRoute } from "vue-router";
import { useCategoryStore } from "../../stores/categoryStore";

const route = useRoute();
const categoryStore = useCategoryStore();

// 每行显示的分类数量
const rowCategoryCount = ref(10);

// 计算可见的分类数量
const showItemCount = computed(() => {
  const total = categoryStore.categoryList.length;
  return total > rowCategoryCount.value * 2 ? rowCategoryCount.value * 2 - 1 : total;
});

// 获取要显示的分类
const visibleCategories = computed(() => {
  return categoryStore.categoryList.slice(0, showItemCount.value);
});

// 接收父组件传递的属性
const props = defineProps({
  showType: {
    type: Number,
    default: 0,
  },
  mouseOver: {
    type: Boolean,
    default: false,
  },
});
</script>

<style lang="scss" scoped>
.category {
  display: flex;
  align-items: flex-start;

  .hot {
    text-align: center;
    margin-right: 40px;
    text-decoration: none;
    color: var(--text);

    .icon-hot {
      width: 46px;
      height: 46px;
      background: #f07775;
      color: #fff;
      font-size: 20px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
    }

    .info {
      margin-top: 8px;
    }
  }

  .category-list {
    width: 100%;
    display: grid;
    grid-gap: 8px;

    .category-item {
      line-height: 30px;
      padding: 0px 5px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      letter-spacing: 2px;
      border: 1px solid #f1f2f3;
      border-radius: 6px;
      background-color: #f6f7fb;
      color: #61666d;
      text-align: center;
      font-weight: 400;
      text-decoration: none;

      &:hover {
        background: #e1e3e5;
      }
    }

    .active {
      color: var(--blue);
      border-color: var(--blue);
    }
  }
}

.child-list {
  display: flex;
  flex-wrap: wrap;

  .child {
    text-decoration: none;
    color: #61666d;
    padding: 5px;
    width: 80px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;

    &:hover {
      background: #e5e5e5;
      border-radius: 3px;
    }
  }
}

// 加载状态样式
.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
  color: #666;
}

.error-container {
  color: #f56c6c;
  padding: 10px;
  text-align: center;
}
</style>