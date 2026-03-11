<script setup>
import { onMounted, reactive, ref } from 'vue';
import { ElMessage } from 'element-plus';
import { doCheckin, getCheckinPage } from '@/api/checkin';
import { getEventManagePage } from '@/api/event';
import StatusTag from '@/components/StatusTag.vue';
import { formatDateTime } from '@/utils/format';

const loading = ref(false);
const events = ref([]);

const query = reactive({
  pageNum: 1,
  pageSize: 10,
  eventId: undefined,
  keyword: '',
  checkinStatus: undefined
});

const pageData = reactive({
  total: 0,
  list: []
});

async function loadEvents() {
  const data = await getEventManagePage({ pageNum: 1, pageSize: 100 });
  events.value = data.list;
}

async function loadPage() {
  loading.value = true;
  try {
    const data = await getCheckinPage(query);
    pageData.total = data.total;
    pageData.list = data.list;
  } finally {
    loading.value = false;
  }
}

async function handleCheckin(row) {
  await doCheckin(row.signupId);
  ElMessage.success('签到成功');
  await loadPage();
}

onMounted(() => {
  Promise.all([loadEvents(), loadPage()]).catch(() => {});
});
</script>

<template>
  <div class="page-card">
    <div class="page-toolbar">
      <div>
        <h3 style="margin: 0;">签到管理</h3>
        <p class="muted-text">针对已审核通过人员执行线下签到</p>
      </div>
    </div>

    <div class="page-search">
      <el-select v-model="query.eventId" clearable filterable placeholder="活动" style="width: 220px;">
        <el-option v-for="item in events" :key="item.id" :label="item.title" :value="item.id" />
      </el-select>
      <el-input v-model="query.keyword" placeholder="活动/用户名/姓名" clearable style="width: 220px;" />
      <el-select v-model="query.checkinStatus" clearable placeholder="签到状态" style="width: 180px;">
        <el-option label="未签到" :value="0" />
        <el-option label="已签到" :value="1" />
      </el-select>
      <el-button type="primary" :loading="loading" @click="query.pageNum = 1; loadPage()">查询</el-button>
    </div>

    <el-table :data="pageData.list" v-loading="loading">
      <el-table-column prop="eventTitle" label="活动标题" min-width="180" />
      <el-table-column prop="username" label="用户名" min-width="120" />
      <el-table-column prop="realName" label="姓名" width="120" />
      <el-table-column prop="phone" label="手机号" min-width="140" />
      <el-table-column label="活动时间" min-width="180">
        <template #default="{ row }">{{ formatDateTime(row.startTime) }}</template>
      </el-table-column>
      <el-table-column label="签到状态" width="120">
        <template #default="{ row }"><StatusTag category="checkin" :value="row.checkinStatus" /></template>
      </el-table-column>
      <el-table-column label="签到时间" min-width="160">
        <template #default="{ row }">{{ formatDateTime(row.checkinTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{ row }">
          <el-button link type="success" :disabled="row.checkinStatus === 1" @click="handleCheckin(row)">签到</el-button>
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
