<script setup>
import { onMounted, reactive, ref } from 'vue';
import { ElMessage } from 'element-plus';
import { addUser, getUserPage, updateUser, updateUserStatus } from '@/api/user';
import UserFormDialog from '@/components/forms/UserFormDialog.vue';
import StatusTag from '@/components/StatusTag.vue';
import { enableStatusOptions, roleOptions } from '@/utils/constants';
import { successMessages } from '@/utils/ui';

const loading = ref(false);
const dialogVisible = ref(false);
const isEdit = ref(false);

const query = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: '',
  role: '',
  status: undefined
});

const pageData = reactive({
  total: 0,
  list: []
});

const form = reactive({
  id: null,
  username: '',
  password: '',
  realName: '',
  phone: '',
  role: 'user',
  status: 1
});

async function loadPage() {
  loading.value = true;
  try {
    const data = await getUserPage(query);
    pageData.total = data.total;
    pageData.list = data.list;
  } finally {
    loading.value = false;
  }
}

function resetForm() {
  Object.assign(form, {
    id: null,
    username: '',
    password: '',
    realName: '',
    phone: '',
    role: 'user',
    status: 1
  });
}

function openCreate() {
  isEdit.value = false;
  resetForm();
  dialogVisible.value = true;
}

function openEdit(row) {
  isEdit.value = true;
  Object.assign(form, row, { password: '' });
  dialogVisible.value = true;
}

async function submit() {
  if (isEdit.value) {
    await updateUser(form);
    ElMessage.success(successMessages.userUpdate);
  } else {
    await addUser(form);
    ElMessage.success(successMessages.userCreate);
  }
  dialogVisible.value = false;
  await loadPage();
}

async function toggleStatus(row) {
  const nextStatus = row.status === 1 ? 0 : 1;
  await updateUserStatus(row.id, { status: nextStatus });
  ElMessage.success(successMessages.userStatusUpdate);
  await loadPage();
}

onMounted(() => {
  loadPage().catch(() => {});
});
</script>

<template>
  <div class="page-card">
    <div class="page-toolbar">
      <div>
        <h3 style="margin: 0;">用户管理</h3>
        <p class="muted-text">支持管理员与普通用户统一维护</p>
      </div>
      <el-button type="primary" @click="openCreate">新增用户</el-button>
    </div>

    <div class="page-search">
      <el-input v-model="query.keyword" placeholder="用户名/姓名/手机号" clearable style="width: 220px;" />
      <el-select v-model="query.role" clearable placeholder="角色" style="width: 160px;">
        <el-option v-for="item in roleOptions" :key="item.value" :label="item.label" :value="item.value" />
      </el-select>
      <el-select v-model="query.status" clearable placeholder="状态" style="width: 160px;">
        <el-option v-for="item in enableStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
      </el-select>
      <el-button type="primary" :loading="loading" @click="query.pageNum = 1; loadPage()">查询</el-button>
    </div>

    <el-table :data="pageData.list" v-loading="loading">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户名" min-width="140" />
      <el-table-column prop="realName" label="真实姓名" min-width="120" />
      <el-table-column prop="phone" label="手机号" min-width="140" />
      <el-table-column label="角色" width="120">
        <template #default="{ row }">
          <StatusTag category="role" :value="row.role" />
        </template>
      </el-table-column>
      <el-table-column label="状态" width="120">
        <template #default="{ row }">
          <StatusTag category="enable" :value="row.status" />
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" min-width="180" />
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
          <el-button link :type="row.status === 1 ? 'warning' : 'success'" @click="toggleStatus(row)">
            {{ row.status === 1 ? '禁用' : '启用' }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="page-footer">
      <el-pagination
        v-model:current-page="query.pageNum"
        v-model:page-size="query.pageSize"
        layout="total, prev, pager, next, sizes"
        :total="pageData.total"
        @change="loadPage"
      />
    </div>

    <UserFormDialog v-model:visible="dialogVisible" :is-edit="isEdit" :form="form" @submit="submit" />
  </div>
</template>
