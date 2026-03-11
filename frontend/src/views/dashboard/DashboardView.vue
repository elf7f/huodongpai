<script setup>
import { onMounted, reactive, ref } from 'vue';
import { getDashboard } from '@/api/statistics';
import { formatPercent } from '@/utils/format';

const loading = ref(false);
const dashboard = reactive({
  totalEvents: 0,
  totalSignupCount: 0,
  totalApprovedCount: 0,
  totalCheckinCount: 0,
  checkinRate: 0,
  hotEvents: []
});

async function loadDashboard() {
  loading.value = true;
  try {
    const data = await getDashboard();
    Object.assign(dashboard, data);
  } finally {
    loading.value = false;
  }
}

onMounted(() => {
  loadDashboard().catch(() => {});
});
</script>

<template>
  <div class="space-y">
    <div class="summary-grid">
      <div class="summary-item">
        <h3>活动总数</h3>
        <strong>{{ dashboard.totalEvents }}</strong>
      </div>
      <div class="summary-item">
        <h3>报名总人数</h3>
        <strong>{{ dashboard.totalSignupCount }}</strong>
      </div>
      <div class="summary-item">
        <h3>审核通过人数</h3>
        <strong>{{ dashboard.totalApprovedCount }}</strong>
      </div>
      <div class="summary-item">
        <h3>签到率</h3>
        <strong>{{ formatPercent(dashboard.checkinRate) }}</strong>
      </div>
    </div>

    <div class="page-card">
      <div class="page-toolbar">
        <div>
          <h3 style="margin: 0;">热门活动 Top5</h3>
          <p class="muted-text">按报名人数和签到人数综合排序</p>
        </div>
        <el-button :loading="loading" @click="loadDashboard">刷新</el-button>
      </div>

      <el-table :data="dashboard.hotEvents" v-loading="loading">
        <el-table-column type="index" label="#" width="60" />
        <el-table-column prop="title" label="活动标题" min-width="220" />
        <el-table-column prop="categoryName" label="分类" width="120" />
        <el-table-column prop="location" label="地点" min-width="180" show-overflow-tooltip />
        <el-table-column prop="signupCount" label="报名人数" width="100" />
        <el-table-column prop="approvedCount" label="通过人数" width="100" />
        <el-table-column prop="checkinCount" label="签到人数" width="100" />
        <el-table-column label="签到率" width="120">
          <template #default="{ row }">{{ formatPercent(row.checkinRate) }}</template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<style scoped>
.space-y {
  display: grid;
  gap: 16px;
}
</style>
