<script setup>
import { onMounted, reactive, ref } from 'vue';
import { ElMessage } from 'element-plus';
import { addUser, getUserPage, updateUser, updateUserStatus } from '@/api/user';
import StatusTag from '@/components/StatusTag.vue';
import { roleOptions } from '@/utils/constants';

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
    ElMessage.success('用户更新成功');
  } else {
    await addUser(form);
    ElMessage.success('用户新增成功');
  }
  dialogVisible.value = false;
  await loadPage();
}

async function toggleStatus(row) {
  const nextStatus = row.status === 1 ? 0 : 1;
  await updateUserStatus(row.id, { status: nextStatus });
  ElMessage.success('状态更新成功');
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
        <el-option label="启用" :value="1" />
        <el-option label="禁用" :value="0" />
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑用户' : '新增用户'" width="520px">
      <el-form label-position="top">
        <el-form-item label="用户名">
          <el-input v-model="form.username" />
        </el-form-item>
        <el-form-item :label="isEdit ? '重置密码(可选)' : '密码'">
          <el-input v-model="form.password" show-password />
        </el-form-item>
        <el-form-item label="真实姓名">
          <el-input v-model="form.realName" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="角色">
          <el-radio-group v-model="form.role">
            <el-radio value="admin">管理员</el-radio>
            <el-radio value="user">普通用户</el-radio>
          </el-radio-group>
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
