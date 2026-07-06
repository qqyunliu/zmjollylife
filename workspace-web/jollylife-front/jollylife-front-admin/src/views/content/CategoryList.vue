<template>
  <el-row :gutter="10">
    <el-col :span="16">
      <el-card class="table-data-card">
        <template #header>
          <div class="header">
            <div class="title">一级分类</div>
            <div class="btn"
                 @click="showEdit({},0)">新增分类</div>
          </div>
        </template>
        <Table ref="tableInfoRef"
               :columns="columns"
               :fetch="loadDataList"
               :dataSource="tableData"
               :options="tableOptions"
               :extHeight="tableOptions.extHeight"
               :showPagination="false"
               @rowClick="rowClick">
          <template #icon="{ index, row }">
            <div class="cover">
              <cover :source="row.icon"
                     defaultImg="default_image.png.png">
              </cover>
            </div>
          </template>
          <template #background="{ index,row}">
            <div class="category-background">
              <cover :source="row.background"
                     fit="cover"
                     defaultImg="default_banner.png">
              </cover>
            </div>
          </template>
          <template #slotOperation="{ index,row}">
            <div class="row-op-panel">
              <a class="a-link"
                 href="javascript:void(0)"
                 @click="showEdit(row,0)">修改</a>
              <el-divider direction="vertical" />
              <a class="a-link"
                 href="javascript:void(0)"
                 @click="delCategory(row)">删除</a>
              <el-divider direction="vertical" />
              <a href="javascript:void(0)"
                 @click="changeSort(0,index,'up')"
                 :class="[index==0? 'disabled' : 'a-link']">上移</a>
              <el-divider direction="vertical" />
              <a href="javascript:void(0)"
                 @click="changeSort(0,index,'down')"
                 :class="[index==tableData.list.length-1 ? 'disabled' : 'a-link']">下移</a>
            </div>
          </template>

        </Table>
      </el-card>
    </el-col>
    <el-col :span="8">
      <el-card class="table-data-card">
        <template #header>
          <div class="header">
            <div class="title">二级分类</div>
            <div class="btn"
                 @click="showEdit({},1)">新增二分类</div>
          </div>
        </template>
        <Table :columns="columnSub"
               :dataSource="subCategoryData"
               :options="tableOptions"
               :extHeight="tableOptions.extHeight"
               :showPagination="false">
          <template #slotOperation="{ index,row}">
            <div class="row-op-panel">
              <a class="a-link"
                 href="javascript:void(0)"
                 @click="showEdit(row,1)">修改</a>
              <el-divider direction="vertical" />
              <a class="a-link"
                 href="javascript:void(0)"
                 @click="delCategory(row)">删除</a>
              <el-divider direction="vertical" />
              <a href="javascript:void(0)"
                 @click="changeSort(1,index,'up')"
                 :class="[index==0? 'disabled' : 'a-link']">上移</a>
              <el-divider direction="vertical" />
              <a href="javascript:void(0)"
                 @click="changeSort(1,index,'down')"
                 :class="[index==subCategoryData.list.length-1 ? 'disabled' : 'a-link']">下移</a>
            </div>
          </template>
        </Table>
      </el-card>
    </el-col>
  </el-row>
</template>

<script setup>
import { ref, getCurrentInstance, nextTick, onMounted, onUpdated, reactive } from "vue";
const { proxy } = getCurrentInstance();
import { useRoute, useRouter } from "vue-router";
const route = useRoute();
const router = useRouter();

// 修复：正确初始化数据结构
const tableData = ref({ list: [] });
const subCategoryData = ref({ list: [] }); // 修复：初始化为对象结构
const currentSelectCategory = ref(null);
const allCategoryData = ref([]); // 存储所有原始分类数据

const tableOptions = ref({
  extHeight: 0,
});

const columns = [
  {
    label: "图标",
    prop: "icon",
    scopedSlots: "icon",
    width: 70,
  },
  {
    label: "背景",
    prop: "background",
    scopedSlots: "background",
    width: 180,
  },
  {
    label: "分类编号",
    prop: "categoryCode",
    width: 180,
  },
  {
    label: "分类名称",
    prop: "categoryName",
  },
  {
    label: "操作",
    prop: "type",
    scopedSlots: "slotOperation",
    width: 200,
  },
];
const columnSub = columns.slice(columns.length - 3, columns.length);
const tableInfoRef = ref();

// 修复：完全重写loadDataList函数，彻底分离数据
const loadDataList = async () => {
  try {
    let result = await proxy.request({
      url: proxy.Api.loadCategory,
    });

    if (!result || !result.data) {
      console.error("加载分类数据失败或数据为空");
      tableData.value.list = [];
      subCategoryData.value.list = [];
      return;
    }

    // 深拷贝原始数据，避免引用问题
    const categoryList = Array.isArray(result.data) ?
      JSON.parse(JSON.stringify(result.data)) : [];

    // 存储所有原始数据
    allCategoryData.value = categoryList;

    // 分离一级分类和二级分类数据
    const rootCategories = categoryList.filter(cat =>
      (cat.pCategoryId === 0 || cat.p_category_id === 0)
    );

    console.log('原始数据:', categoryList);
    console.log('一级分类:', rootCategories);

    // 创建纯净的一级分类数据（不包含children，避免循环引用）
    const cleanRootCategories = rootCategories.map(cat => ({
      categoryId: cat.category_id || cat.categoryId,           // 注意：数据库是 category_id
      categoryCode: cat.category_code || cat.categoryCode,     // 数据库是 category_code  
      categoryName: cat.category_name || cat.categoryName,     // 数据库是 category_name
      pCategoryId: cat.p_category_id || cat.pCategoryId,       // 数据库是 p_category_id
      icon: cat.icon,
      background: cat.background,
      sort: cat.sort
    }));

    // 设置一级分类数据（不包含children）
    tableData.value.list = cleanRootCategories;
    console.log('构建的一级分类数据:', cleanRootCategories);

    // 设置默认选中第一个一级分类
    if (currentSelectCategory.value == null && cleanRootCategories.length > 0) {
      currentSelectCategory.value = cleanRootCategories[0];
      // 获取对应的二级分类
      console.log('初始化时选中的一级分类:', cleanRootCategories[0]);
      updateSubCategories(cleanRootCategories[0].categoryId);
    } else if (currentSelectCategory.value) {
      // 更新当前选中分类
      const foundCategory = cleanRootCategories.find((item) => {
        return item.categoryId == currentSelectCategory.value.categoryId;
      });

      if (foundCategory) {
        currentSelectCategory.value = foundCategory;
        updateSubCategories(foundCategory.categoryId);
      } else {
        // 如果找不到之前选中的分类，默认选中第一个
        if (cleanRootCategories.length > 0) {
          currentSelectCategory.value = cleanRootCategories[0];
          updateSubCategories(cleanRootCategories[0].categoryId);
        }
      }
    }

    // 设置表格当前行
    nextTick(() => {
      if (currentSelectCategory.value && tableInfoRef.value) {
        console.log('设置当前选中行:', currentSelectCategory.value);
        tableInfoRef.value.setCurrentRow("categoryId", currentSelectCategory.value.categoryId);
      }
    });

  } catch (error) {
    console.error("加载分类数据出错:", error);
    tableData.value.list = [];
    subCategoryData.value.list = [];
  }
};

// 新增：更新二级分类数据的函数
const updateSubCategories = (parentCategoryId) => {
  console.log('更新二级分类，父分类ID:', parentCategoryId);
  console.log('所有分类数据:', allCategoryData.value);

  const childCategories = allCategoryData.value.filter(cat => {
    // 优先使用数据库字段名 (下划线格式)
    const catParentId = cat.p_category_id || cat.pCategoryId;
    const isChild = catParentId === parentCategoryId && catParentId !== 0;

    console.log(`分类 ${cat.category_name || cat.categoryName} (${cat.category_id || cat.categoryId}): 父ID=${catParentId}, 是否为子分类=${isChild}`);
    return isChild;
  });

  console.log('筛选出的二级分类:', childCategories);

  // 创建纯净的二级分类数据
  const cleanChildCategories = childCategories.map(child => ({
    categoryId: child.category_id || child.categoryId,           // 优先使用数据库字段名
    categoryCode: child.category_code || child.categoryCode,
    categoryName: child.category_name || child.categoryName,
    pCategoryId: child.p_category_id || child.pCategoryId,
    icon: child.icon,
    background: child.background,
    sort: child.sort
  }));

  subCategoryData.value.list = cleanChildCategories;
  console.log('设置的二级分类数据:', subCategoryData.value.list);
};

// 修复：行点击事件处理 - 正确解构参数
const rowClick = (params) => {
  console.log('rowClick 接收到的参数:', params);

  // 从参数中提取实际的行数据
  const row = params.row || params;
  console.log('实际的行数据:', row);
  console.log('行数据的所有属性:', Object.keys(row));

  currentSelectCategory.value = row;

  // 尝试所有可能的字段名
  const categoryId = row.categoryId || row.category_id || row.id || row.Category_id;
  console.log('获取到的categoryId:', categoryId, '类型:', typeof categoryId);

  if (categoryId !== undefined && categoryId !== null) {
    updateSubCategories(categoryId);
  } else {
    console.error('无法获取categoryId from row:', row);
    console.log('尝试遍历行数据的所有属性:');
    Object.keys(row).forEach(key => {
      console.log(`${key}: ${row[key]} (${typeof row[key]})`);
    });
  }
};

// 添加：编辑函数占位符
const showEdit = (row, type) => {
  // TODO: 实现编辑逻辑
  console.log('编辑分类:', row, '类型:', type);
};

// 添加：删除函数占位符
const delCategory = (row) => {
  // TODO: 实现删除逻辑
  console.log('删除分类:', row);
};

// 添加：排序函数占位符
const changeSort = (type, index, direction) => {
  // TODO: 实现排序逻辑
  console.log('排序:', type, index, direction);
};

</script>

<style scoped>
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title {
  font-weight: bold;
}

.btn {
  background: #409eff;
  color: white;
  padding: 6px 12px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.btn:hover {
  background: #66b1ff;
}

.cover {
  width: 50px;
  height: 50px;
}

.category-background {
  width: 150px;
  height: 60px;
}

.row-op-panel {
  display: flex;
  align-items: center;
}

.a-link {
  color: #409eff;
  text-decoration: none;
  cursor: pointer;
}

.a-link:hover {
  color: #66b1ff;
}

.disabled {
  color: #c0c4cc;
  cursor: not-allowed;
}

.table-data-card {
  margin-bottom: 20px;
}
</style>