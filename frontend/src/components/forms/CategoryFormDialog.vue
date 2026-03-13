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

const title = computed(() => (props.isEdit ? '编辑分类' : '新增分类'));
</script>

<template>
  <el-dialog :model-value="visible" :title="title" width="480px" @update:model-value="emit('update:visible', $event)">
    <el-form label-position="top">
      <el-form-item label="分类名称">
        <el-input v-model="form.categoryName" />
      </el-form-item>
      <el-form-item label="排序值">
        <el-input-number v-model="form.sortNum" :min="0" style="width: 100%;" />
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
