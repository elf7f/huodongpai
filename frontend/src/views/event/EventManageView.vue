<script setup>
import { onMounted, reactive, ref } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import {
  addEvent,
  cancelEvent,
  deleteEvent,
  getEventDetail,
  getEventManagePage,
  publishEvent,
  updateEvent
} from '@/api/event';
import { getCategoryList } from '@/api/category';
import EventFormDialog from '@/components/forms/EventFormDialog.vue';
import StatusTag from '@/components/StatusTag.vue';
import { eventBaseStatusOptions, eventRuntimeFilterOptions } from '@/utils/constants';
import { formatDateTime } from '@/utils/format';
import { buildDeleteConfirmMessage, dialogTitles, isDialogCancel, successMessages } from '@/utils/ui';

const loading = ref(false);
const dialogVisible = ref(false);
const detailVisible = ref(false);
const isEdit = ref(false);
const categories = ref([]);
const detail = ref(null);

const query = reactive({
  pageNum: 1,
  pageSize: 10,
  title: '',
  categoryId: undefined,
  baseStatus: '',
  runtimeStatus: ''
});

const pageData = reactive({
  total: 0,
  list: []
});

const form = reactive({
  id: null,
  title: '',
  categoryId: undefined,
  coverUrl: '',
  location: '',
  startTime: '',
  endTime: '',
  signupDeadline: '',
  maxParticipants: 20,
  needAudit: 1,
  description: ''
});

function resetForm() {
  Object.assign(form, {
    id: null,
    title: '',
    categoryId: categories.value[0]?.id,
    coverUrl: '',
    location: '',
    startTime: '',
    endTime: '',
    signupDeadline: '',
    maxParticipants: 20,
    needAudit: 1,
    description: ''
  });
}

async function loadCategories() {
  categories.value = await getCategoryList({ includeDisabled: true });
  if (!form.categoryId && categories.value.length > 0) {
    form.categoryId = categories.value[0].id;
  }
}

async function loadPage() {
  loading.value = true;
  try {
    const data = await getEventManagePage(query);
    pageData.total = data.total;
    pageData.list = data.list;
  } finally {
    loading.value = false;
  }
}

function openCreate() {
  isEdit.value = false;
  resetForm();
  dialogVisible.value = true;
}

async function openEdit(row) {
  isEdit.value = true;
  const data = await getEventDetail(row.id);
  Object.assign(form, data);
  dialogVisible.value = true;
}

async function showDetail(id) {
  detail.value = await getEventDetail(id);
  detailVisible.value = true;
}

async function submit() {
  if (isEdit.value) {
    await updateEvent(form);
    ElMessage.success(successMessages.eventUpdate);
  } else {
    await addEvent(form);
    ElMessage.success(successMessages.eventCreate);
  }
  dialogVisible.value = false;
  await loadPage();
}

async function handlePublish(row) {
  await publishEvent(row.id);
  ElMessage.success(successMessages.eventPublish);
  await loadPage();
}

async function handleCancel(row) {
  await cancelEvent(row.id);
  ElMessage.success(successMessages.eventCancel);
  await loadPage();
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(buildDeleteConfirmMessage('活动', row.title), dialogTitles.deleteConfirm, { type: 'warning' });
    await deleteEvent(row.id);
    ElMessage.success(successMessages.eventDelete);
    await loadPage();
  } catch (error) {
    if (!isDialogCancel(error)) {
      throw error;
    }
  }
}

onMounted(() => {
  Promise.all([loadCategories(), loadPage()]).catch(() => {});
});
</script>

<template>
  <div class="page-card">
    <div class="page-toolbar">
      <div>
        <h3 style="margin: 0;">活动管理</h3>
        <p class="muted-text">创建、发布、取消和维护活动</p>
      </div>
      <el-button type="primary" @click="openCreate">新增活动</el-button>
    </div>

    <div class="page-search">
      <el-input v-model="query.title" placeholder="活动标题" clearable style="width: 220px;" />
      <el-select v-model="query.categoryId" clearable placeholder="分类" style="width: 180px;">
        <el-option v-for="item in categories" :key="item.id" :label="item.categoryName" :value="item.id" />
      </el-select>
      <el-select v-model="query.baseStatus" clearable placeholder="基础状态" style="width: 180px;">
        <el-option v-for="item in eventBaseStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
      </el-select>
      <el-select v-model="query.runtimeStatus" clearable placeholder="运行状态" style="width: 180px;">
        <el-option v-for="item in eventRuntimeFilterOptions" :key="item.value" :label="item.label" :value="item.value" />
      </el-select>
      <el-button type="primary" :loading="loading" @click="query.pageNum = 1; loadPage()">查询</el-button>
    </div>

    <el-table :data="pageData.list" v-loading="loading">
      <el-table-column prop="title" label="活动标题" min-width="200" />
      <el-table-column prop="categoryName" label="分类" width="120" />
      <el-table-column label="基础状态" width="120">
        <template #default="{ row }">
          <StatusTag category="eventBase" :value="row.baseStatus" />
        </template>
      </el-table-column>
      <el-table-column label="运行状态" width="120">
        <template #default="{ row }">
          <StatusTag category="eventRuntime" :value="row.runtimeStatus" />
        </template>
      </el-table-column>
      <el-table-column prop="signupCount" label="报名" width="80" />
      <el-table-column prop="approvedCount" label="通过" width="80" />
      <el-table-column prop="checkinCount" label="签到" width="80" />
      <el-table-column label="开始时间" min-width="170">
        <template #default="{ row }">{{ formatDateTime(row.startTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="260" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="showDetail(row.id)">详情</el-button>
          <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
          <el-button v-if="row.baseStatus !== 'published'" link type="success" @click="handlePublish(row)">发布</el-button>
          <el-button v-if="row.baseStatus !== 'cancelled'" link type="warning" @click="handleCancel(row)">取消</el-button>
          <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
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
  </div>

  <EventFormDialog
    v-model:visible="dialogVisible"
    :is-edit="isEdit"
    :form="form"
    :categories="categories"
    @submit="submit"
  />

  <el-drawer v-model="detailVisible" title="活动详情" size="48%">
    <template v-if="detail">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="标题">{{ detail.title }}</el-descriptions-item>
        <el-descriptions-item label="分类">{{ detail.categoryName }}</el-descriptions-item>
        <el-descriptions-item label="地点">{{ detail.location }}</el-descriptions-item>
        <el-descriptions-item label="报名截止">{{ formatDateTime(detail.signupDeadline) }}</el-descriptions-item>
        <el-descriptions-item label="基础状态">
          <StatusTag category="eventBase" :value="detail.baseStatus" />
        </el-descriptions-item>
        <el-descriptions-item label="运行状态">
          <StatusTag category="eventRuntime" :value="detail.runtimeStatus" />
        </el-descriptions-item>
        <el-descriptions-item label="活动开始">{{ formatDateTime(detail.startTime) }}</el-descriptions-item>
        <el-descriptions-item label="活动结束">{{ formatDateTime(detail.endTime) }}</el-descriptions-item>
        <el-descriptions-item label="报名人数">{{ detail.signupCount }}</el-descriptions-item>
        <el-descriptions-item label="签到人数">{{ detail.checkinCount }}</el-descriptions-item>
      </el-descriptions>
      <div class="page-card" style="margin-top: 16px;">
        <h4 style="margin-top: 0;">活动详情介绍</h4>
        <p style="white-space: pre-wrap; line-height: 1.8;">{{ detail.description }}</p>
      </div>
    </template>
  </el-drawer>
</template>
