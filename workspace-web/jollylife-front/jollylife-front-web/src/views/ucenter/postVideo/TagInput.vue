 <template>
  <div class="tag-input-panel">
    <div class="tag-list">
      <el-tag class="tag-item"
              v-for="tag in modelValue"
              :key="tag"
              closable
              :disable-transitions="false"
              @close="handleClose(tag)">{{tag}}</el-tag>
    </div>
    <div class="tag-input">
      <el-input placeholder="按回车键Enter创建标签 "
                v-model="inputValue"
                :maxlength="15"
                resize="none"
                show-word-limit
                @keyup.enter="inputChange"></el-input>
    </div>
    <div class="info">最多还可以 输入{{ 10 - modelValue.length }}个标签</div>
  </div>
</template>

<script setup>
import { ref, reactive, getCurrentInstance, nextTick } from "vue";
const { proxy } = getCurrentInstance();
import { useRoute, useRouter } from "vue-router";
const route = useRoute();
const router = useRouter();


const props = defineProps({
  modelValue: {
    type: Array,
    default: [],
  },
});

const emit = defineEmits(['update:modelValue']);

const handleClose = (tag) => {
  const newTags = [...props.modelValue];
  newTags.splice(newTags.indexOf(tag), 1);
  emit('update:modelValue', newTags);
};

const inputValue = ref();
const inputChange = () => {
  if (!inputValue.value) {
    return;
  }
  if (props.modelValue.length >= 10) {
    proxy.Message.warning("最多只能输入10个标签");
    inputValue.value = "";
    return;
  }
  const newTags = [...props.modelValue, inputValue.value];
  emit('update:modelValue', newTags);
  inputValue.value = "";
};
</script>

<style lang="scss" scoped>
.tag-input-panel {
  width: 100%;
  border: 1px solid #ddd;
  display: flex;
  flex-wrap: wrap;
  border-radius: 5px;
  padding: 0px 10px;
  .tag-list {
    .tag-item {
      margin-right: 10px;
    }
  }
  .tag-input {
    flex: 1;
    min-width: 150px;
    margin-right: 10px;
    :deep(.el-input__wrapper) {
      box-shadow: none;
      padding: 0px;
    }
  }
  .info {
    color: #939393;
    font-size: 12px;
  }
}
</style>