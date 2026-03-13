<script setup>
import { onMounted, reactive, ref } from 'vue';
import { ElMessage } from 'element-plus';
import { getCategoryList } from '@/api/category';
import { applySignup } from '@/api/signup';
import { getEventDetail, getEventPage } from '@/api/event';
import StatusTag from '@/components/StatusTag.vue';
import { eventRuntimeFilterOptions } from '@/utils/constants';
import { formatDateTime } from '@/utils/format';
import { successMessages } from '@/utils/ui';

const loading = ref(false);
const detailVisible = ref(false);
const detailLoading = ref(false);
const categories = ref([]);

const query = reactive({
  pageNum: 1,
  pageSize: 10,
  title: '',
  categoryId: undefined,
  runtimeStatus: ''
});

const pageData = reactive({
  total: 0,
  list: []
});

const detail = ref(null);

async function loadCategories() {
  categories.value = await getCategoryList({});
}

async function loadPage() {
  loading.value = true;
  try {
    const data = await getEventPage(query);
    pageData.total = data.total;
    pageData.list = data.list;
  } finally {
    loading.value = false;
  }
}

async function showDetail(id) {
  detailLoading.value = true;
  detailVisible.value = true;
  try {
    detail.value = await getEventDetail(id);
  } finally {
    detailLoading.value = false;
  }
}

async function handleApply(id) {
  await applySignup(id);
  ElMessage.success(successMessages.signupApply);
  await Promise.all([loadPage(), showDetail(id)]);
}

onMounted(() => {
  Promise.all([loadCategories(), loadPage()]).catch(() => {});
});
</script>

<template>
  <div class="page-card">
    <div class="page-toolbar">
      <div>
        <h3 style="margin: 0;">活动列表</h3>
        <p class="muted-text">查看活动详情并提交报名</p>
      </div>
    </div>

    <div class="page-search">
      <el-input v-model="query.title" placeholder="活动标题" clearable style="width: 220px;" />
      <el-select v-model="query.categoryId" clearable placeholder="分类" style="width: 180px;">
        <el-option v-for="item in categories" :key="item.id" :label="item.categoryName" :value="item.id" />
      </el-select>
      <el-select v-model="query.runtimeStatus" clearable placeholder="活动状态" style="width: 180px;">
        <el-option v-for="item in eventRuntimeFilterOptions" :key="item.value" :label="item.label" :value="item.value" />
      </el-select>
      <el-button type="primary" :loading="loading" @click="query.pageNum = 1; loadPage()">查询</el-button>
    </div>

    <el-table :data="pageData.list" v-loading="loading">
      <el-table-column prop="title" label="活动标题" min-width="220" />
      <el-table-column prop="categoryName" label="分类" width="120" />
      <el-table-column prop="location" label="地点" min-width="180" show-overflow-tooltip />
      <el-table-column label="活动状态" width="120">
        <template #default="{ row }">
          <StatusTag category="eventRuntime" :value="row.runtimeStatus" />
        </template>
      </el-table-column>
      <el-table-column prop="remainingSlots" label="剩余名额" width="100" />
      <el-table-column label="开始时间" min-width="170">
        <template #default="{ row }">{{ formatDateTime(row.startTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="showDetail(row.id)">详情</el-button>
          <el-button
            link
            type="success"
            :disabled="row.runtimeStatus !== 'signup_open' || row.remainingSlots <= 0"
            @click="handleApply(row.id)"
          >
            报名
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
  </div>

  <el-drawer v-model="detailVisible" title="活动详情" size="48%">
    <div v-loading="detailLoading">
      <template v-if="detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="标题">{{ detail.title }}</el-descriptions-item>
          <el-descriptions-item label="分类">{{ detail.categoryName }}</el-descriptions-item>
          <el-descriptions-item label="地点">{{ detail.location }}</el-descriptions-item>
          <el-descriptions-item label="报名状态">
            <StatusTag category="signup" :value="detail.currentUserSignupStatus" />
          </el-descriptions-item>
          <el-descriptions-item label="活动状态">
            <StatusTag category="eventRuntime" :value="detail.runtimeStatus" />
          </el-descriptions-item>
          <el-descriptions-item label="剩余名额">{{ detail.remainingSlots }}</el-descriptions-item>
          <el-descriptions-item label="开始时间">{{ formatDateTime(detail.startTime) }}</el-descriptions-item>
          <el-descriptions-item label="结束时间">{{ formatDateTime(detail.endTime) }}</el-descriptions-item>
          <el-descriptions-item label="报名截止">{{ formatDateTime(detail.signupDeadline) }}</el-descriptions-item>
          <el-descriptions-item label="需要审核">{{ detail.needAudit === 1 ? '是' : '否' }}</el-descriptions-item>
        </el-descriptions>

        <div class="page-card" style="margin-top: 16px;">
          <h4 style="margin-top: 0;">活动详情介绍</h4>
          <p style="white-space: pre-wrap; line-height: 1.8;">{{ detail.description }}</p>
        </div>
      </template>
    </div>
  </el-drawer>
</template>
