<script setup>
import { onMounted, reactive, ref } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { addCategory, deleteCategory, getCategoryList, updateCategory } from '@/api/category';
import StatusTag from '@/components/StatusTag.vue';

const loading = ref(false);
const dialogVisible = ref(false);
const isEdit = ref(false);
const list = ref([]);

const query = reactive({
  keyword: '',
  status: undefined,
  includeDisabled: true
});

const form = reactive({
  id: null,
  categoryName: '',
  sortNum: 1,
  status: 1
});

async function loadList() {
  loading.value = true;
  try {
    list.value = await getCategoryList(query);
  } finally {
    loading.value = false;
  }
}

function resetForm() {
  form.id = null;
  form.categoryName = '';
  form.sortNum = 1;
  form.status = 1;
}

function openCreate() {
  isEdit.value = false;
  resetForm();
  dialogVisible.value = true;
}

function openEdit(row) {
  isEdit.value = true;
  Object.assign(form, row);
  dialogVisible.value = true;
}

async function submit() {
  if (isEdit.value) {
    await updateCategory(form);
    ElMessage.success('分类更新成功');
  } else {
    await addCategory(form);
    ElMessage.success('分类新增成功');
  }
  dialogVisible.value = false;
  await loadList();
}

async function remove(row) {
  try {
    await ElMessageBox.confirm(`确认删除分类“${row.categoryName}”吗？`, '删除确认', {
      type: 'warning'
    });
    await deleteCategory(row.id);
    ElMessage.success('删除成功');
    await loadList();
  } catch (error) {
    if (error !== 'cancel' && error !== 'close') {
      throw error;
    }
  }
}

onMounted(() => {
  loadList().catch(() => {});
});
</script>

<template>
  <div class="page-card">
    <div class="page-toolbar">
      <div>
        <h3 style="margin: 0;">活动分类</h3>
        <p class="muted-text">维护活动业务分类和排序</p>
      </div>
      <el-button type="primary" @click="openCreate">新增分类</el-button>
    </div>

    <div class="page-search">
      <el-input v-model="query.keyword" placeholder="分类名称" clearable style="width: 220px;" />
      <el-select v-model="query.status" placeholder="状态" clearable style="width: 180px;">
        <el-option label="启用" :value="1" />
        <el-option label="禁用" :value="0" />
      </el-select>
      <el-button type="primary" :loading="loading" @click="loadList">查询</el-button>
    </div>

    <el-table :data="list" v-loading="loading">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="categoryName" label="分类名称" min-width="160" />
      <el-table-column prop="sortNum" label="排序" width="100" />
      <el-table-column label="状态" width="120">
        <template #default="{ row }">
          <StatusTag category="enable" :value="row.status" />
        </template>
      </el-table-column>
      <el-table-column prop="updateTime" label="更新时间" min-width="180" />
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
          <el-button link type="danger" @click="remove(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑分类' : '新增分类'" width="480px">
      <el-form label-position="top">
        <el-form-item label="分类名称">
          <el-input v-model="form.categoryName" />
        </el-form-item>
        <el-form-item label="排序值">
          <el-input-number v-model="form.sortNum" :min="0" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>
