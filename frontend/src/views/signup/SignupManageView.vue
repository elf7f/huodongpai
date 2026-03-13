<script setup>
import { onMounted, reactive, ref } from 'vue';
import { ElMessage } from 'element-plus';
import { getEventManagePage } from '@/api/event';
import { auditSignupPass, auditSignupReject, getSignupManagePage } from '@/api/signup';
import StatusTag from '@/components/StatusTag.vue';
import { signupStatusOptions } from '@/utils/constants';
import { formatDateTime } from '@/utils/format';
import { commonText, successMessages } from '@/utils/ui';

const loading = ref(false);
const auditVisible = ref(false);
const auditAction = ref('pass');
const events = ref([]);
const currentRow = ref(null);

const query = reactive({
  pageNum: 1,
  pageSize: 10,
  eventId: undefined,
  status: '',
  keyword: ''
});

const pageData = reactive({
  total: 0,
  list: []
});

const auditForm = reactive({
  remark: ''
});

async function loadEvents() {
  const data = await getEventManagePage({ pageNum: 1, pageSize: 100 });
  events.value = data.list;
}

async function loadPage() {
  loading.value = true;
  try {
    const data = await getSignupManagePage(query);
    pageData.total = data.total;
    pageData.list = data.list;
  } finally {
    loading.value = false;
  }
}

function openAudit(row, action) {
  currentRow.value = row;
  auditAction.value = action;
  auditForm.remark = '';
  auditVisible.value = true;
}

async function submitAudit() {
  if (auditAction.value === 'pass') {
    await auditSignupPass(currentRow.value.signupId, auditForm);
    ElMessage.success(successMessages.signupAuditPass);
  } else {
    await auditSignupReject(currentRow.value.signupId, auditForm);
    ElMessage.success(successMessages.signupAuditReject);
  }
  auditVisible.value = false;
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
        <h3 style="margin: 0;">报名管理</h3>
        <p class="muted-text">查看待审核记录并执行通过或驳回</p>
      </div>
    </div>

    <div class="page-search">
      <el-select v-model="query.eventId" clearable filterable placeholder="活动" style="width: 220px;">
        <el-option v-for="item in events" :key="item.id" :label="item.title" :value="item.id" />
      </el-select>
      <el-select v-model="query.status" clearable placeholder="报名状态" style="width: 180px;">
        <el-option v-for="item in signupStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
      </el-select>
      <el-input v-model="query.keyword" placeholder="活动/用户名/姓名" clearable style="width: 220px;" />
      <el-button type="primary" :loading="loading" @click="query.pageNum = 1; loadPage()">查询</el-button>
    </div>

    <el-table :data="pageData.list" v-loading="loading">
      <el-table-column prop="eventTitle" label="活动标题" min-width="180" />
      <el-table-column prop="username" label="用户名" min-width="120" />
      <el-table-column prop="realName" label="姓名" width="120" />
      <el-table-column prop="phone" label="手机号" min-width="140" />
      <el-table-column label="报名状态" width="120">
        <template #default="{ row }"><StatusTag category="signup" :value="row.status" /></template>
      </el-table-column>
      <el-table-column label="签到状态" width="120">
        <template #default="{ row }"><StatusTag category="checkin" :value="row.checkinStatus" /></template>
      </el-table-column>
      <el-table-column label="报名时间" min-width="160">
        <template #default="{ row }">{{ formatDateTime(row.signupTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button link type="success" :disabled="row.status !== 'pending'" @click="openAudit(row, 'pass')">通过</el-button>
          <el-button link type="danger" :disabled="row.status !== 'pending'" @click="openAudit(row, 'reject')">驳回</el-button>
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

  <el-dialog v-model="auditVisible" :title="auditAction === 'pass' ? '审核通过' : '审核驳回'" width="520px">
    <el-form label-position="top">
      <el-form-item label="审核备注">
        <el-input v-model="auditForm.remark" type="textarea" :rows="4" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="auditVisible = false">取消</el-button>
      <el-button :type="auditAction === 'pass' ? 'success' : 'danger'" @click="submitAudit">{{ commonText.confirm }}</el-button>
    </template>
  </el-dialog>
</template>
