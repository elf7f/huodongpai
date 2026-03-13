<script setup>
import { onMounted, reactive, ref } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { addCategory, deleteCategory, getCategoryList, updateCategory } from '@/api/category';
import CategoryFormDialog from '@/components/forms/CategoryFormDialog.vue';
import StatusTag from '@/components/StatusTag.vue';
import { enableStatusOptions } from '@/utils/constants';
import { buildDeleteConfirmMessage, dialogTitles, isDialogCancel, successMessages } from '@/utils/ui';

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
    ElMessage.success(successMessages.categoryUpdate);
  } else {
    await addCategory(form);
    ElMessage.success(successMessages.categoryCreate);
  }
  dialogVisible.value = false;
  await loadList();
}

async function remove(row) {
  try {
    await ElMessageBox.confirm(buildDeleteConfirmMessage('分类', row.categoryName), dialogTitles.deleteConfirm, {
      type: 'warning'
    });
    await deleteCategory(row.id);
    ElMessage.success(successMessages.categoryDelete);
    await loadList();
  } catch (error) {
    if (!isDialogCancel(error)) {
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
        <el-option v-for="item in enableStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
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

    <CategoryFormDialog v-model:visible="dialogVisible" :is-edit="isEdit" :form="form" @submit="submit" />
  </div>
</template>
