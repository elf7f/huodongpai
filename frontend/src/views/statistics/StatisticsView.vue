<script setup>
import { onMounted, reactive, ref } from 'vue';
import { getHotEvents, getSignupTrend } from '@/api/statistics';
import { formatPercent } from '@/utils/format';

const loading = ref(false);
const query = reactive({
  limit: 10,
  days: 7
});
const state = reactive({
  hotEvents: [],
  trend: []
});

async function loadData() {
  loading.value = true;
  try {
    const [hotEvents, trend] = await Promise.all([
      getHotEvents({ limit: query.limit }),
      getSignupTrend({ days: query.days })
    ]);
    state.hotEvents = hotEvents;
    state.trend = trend;
  } finally {
    loading.value = false;
  }
}

onMounted(() => {
  loadData().catch(() => {});
});
</script>

<template>
  <div class="two-column">
    <div class="page-card">
      <div class="page-toolbar">
        <div>
          <h3 style="margin: 0;">热门活动排行</h3>
          <p class="muted-text">可按榜单数量筛选</p>
        </div>
        <div style="display: flex; gap: 12px; align-items: center;">
          <el-input-number v-model="query.limit" :min="1" :max="20" />
          <el-button type="primary" :loading="loading" @click="loadData">查询</el-button>
        </div>
      </div>

      <el-table :data="state.hotEvents" v-loading="loading">
        <el-table-column type="index" label="#" width="60" />
        <el-table-column prop="title" label="活动标题" min-width="180" />
        <el-table-column prop="categoryName" label="分类" width="120" />
        <el-table-column prop="signupCount" label="报名人数" width="96" />
        <el-table-column prop="approvedCount" label="通过人数" width="96" />
        <el-table-column prop="checkinCount" label="签到人数" width="96" />
        <el-table-column label="签到率" width="110">
          <template #default="{ row }">{{ formatPercent(row.checkinRate) }}</template>
        </el-table-column>
      </el-table>
    </div>

    <div class="page-card">
      <div class="page-toolbar">
        <div>
          <h3 style="margin: 0;">报名趋势</h3>
          <p class="muted-text">最近 N 天报名量</p>
        </div>
        <div style="display: flex; gap: 12px; align-items: center;">
          <el-input-number v-model="query.days" :min="1" :max="90" />
          <el-button type="primary" :loading="loading" @click="loadData">查询</el-button>
        </div>
      </div>

      <el-table :data="state.trend" v-loading="loading">
        <el-table-column prop="date" label="日期" min-width="150" />
        <el-table-column prop="signupCount" label="报名量" min-width="120" />
      </el-table>
    </div>
  </div>
</template>
