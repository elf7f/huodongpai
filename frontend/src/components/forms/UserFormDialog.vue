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
  }
});

const emit = defineEmits(['update:visible', 'submit']);

const title = computed(() => (props.isEdit ? '编辑用户' : '新增用户'));
</script>

<template>
  <el-dialog :model-value="visible" :title="title" width="520px" @update:model-value="emit('update:visible', $event)">
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
      <el-button @click="emit('update:visible', false)">{{ commonText.cancel }}</el-button>
      <el-button type="primary" @click="emit('submit')">{{ commonText.save }}</el-button>
    </template>
  </el-dialog>
</template>
