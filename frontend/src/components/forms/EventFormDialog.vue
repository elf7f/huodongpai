<script setup>
import { computed } from 'vue';
import { commonText } from '@/utils/ui';

const props = defineProps({
  visible: {
    type: Boolean,
    required: true
  },
  isEdit: {
    type: Boolean,
    required: true
  },
  form: {
    type: Object,
    required: true
  },
  categories: {
    type: Array,
    required: true
  }
});

const emit = defineEmits(['update:visible', 'submit']);

const title = computed(() => (props.isEdit ? '编辑活动' : '新增活动'));
</script>

<template>
  <el-dialog :model-value="visible" :title="title" width="760px" @update:model-value="emit('update:visible', $event)">
    <el-form label-position="top">
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="活动标题"><el-input v-model="form.title" /></el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="活动分类">
            <el-select v-model="form.categoryId" style="width: 100%;">
              <el-option v-for="item in categories" :key="item.id" :label="item.categoryName" :value="item.id" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="活动地点"><el-input v-model="form.location" /></el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="封面地址"><el-input v-model="form.coverUrl" /></el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="开始时间">
            <el-date-picker v-model="form.startTime" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" style="width: 100%;" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="结束时间">
            <el-date-picker v-model="form.endTime" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" style="width: 100%;" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="报名截止时间">
            <el-date-picker v-model="form.signupDeadline" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" style="width: 100%;" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="最大报名人数">
            <el-input-number v-model="form.maxParticipants" :min="1" style="width: 100%;" />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="是否需要审核">
            <el-radio-group v-model="form.needAudit">
              <el-radio :value="1">需要审核</el-radio>
              <el-radio :value="0">无需审核</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="活动详情">
            <el-input v-model="form.description" type="textarea" :rows="5" />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>

    <template #footer>
      <el-button @click="emit('update:visible', false)">{{ commonText.cancel }}</el-button>
      <el-button type="primary" @click="emit('submit')">{{ commonText.save }}</el-button>
    </template>
  </el-dialog>
</template>
