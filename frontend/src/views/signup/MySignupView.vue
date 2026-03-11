<script setup>
import { onMounted, reactive, ref } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { cancelSignup, getMySignupPage } from '@/api/signup';
import StatusTag from '@/components/StatusTag.vue';
import { formatDateTime } from '@/utils/format';

const loading = ref(false);
const query = reactive({
  pageNum: 1,
  pageSize: 10,
  status: '',
  keyword: ''
});

const pageData = reactive({
  total: 0,
  list: []
});

async function loadPage() {
  loading.value = true;
  try {
    const data = await getMySignupPage(query);
    pageData.total = data.total;
    pageData.list = data.list;
  } finally {
    loading.value = false;
  }
}

async function handleCancel(row) {
  try {
    await ElMessageBox.confirm(`确认取消报名“${row.eventTitle}”吗？`, '取消报名', { type: 'warning' });
    await cancelSignup(row.signupId);
    ElMessage.success('取消报名成功');
    await loadPage();
  } catch (error) {
    if (error !== 'cancel' && error !== 'close') {
      throw error;
    }
  }
}

onMounted(() => {
  loadPage().catch(() => {});
});
</script>

<template>
  <div class="page-card">
    <div class="page-toolbar">
      <div>
        <h3 style="margin: 0;">我的报名</h3>
        <p class="muted-text">查看审核状态和签到状态</p>
      </div>
    </div>

    <div class="page-search">
      <el-input v-model="query.keyword" placeholder="活动标题" clearable style="width: 220px;" />
      <el-select v-model="query.status" clearable placeholder="报名状态" style="width: 180px;">
        <el-option label="待审核" value="pending" />
        <el-option label="已通过" value="approved" />
        <el-option label="已驳回" value="rejected" />
        <el-option label="已取消" value="cancelled" />
      </el-select>
      <el-button type="primary" :loading="loading" @click="query.pageNum = 1; loadPage()">查询</el-button>
    </div>

    <el-table :data="pageData.list" v-loading="loading">
      <el-table-column prop="eventTitle" label="活动标题" min-width="220" />
      <el-table-column prop="location" label="地点" min-width="180" show-overflow-tooltip />
      <el-table-column label="活动状态" width="120">
        <template #default="{ row }"><StatusTag category="eventRuntime" :value="row.runtimeStatus" /></template>
      </el-table-column>
      <el-table-column label="报名状态" width="120">
        <template #default="{ row }"><StatusTag category="signup" :value="row.status" /></template>
      </el-table-column>
      <el-table-column label="签到状态" width="120">
        <template #default="{ row }"><StatusTag category="checkin" :value="row.checkinStatus" /></template>
      </el-table-column>
      <el-table-column label="报名时间" min-width="160">
        <template #default="{ row }">{{ formatDateTime(row.signupTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{ row }">
          <el-button
            link
            type="danger"
            :disabled="!['pending', 'approved'].includes(row.status)"
            @click="handleCancel(row)"
          >
            取消报名
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
</template>
