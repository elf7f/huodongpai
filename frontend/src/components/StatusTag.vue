<script setup>
import { computed } from 'vue';
import {
  checkinStatusOptions,
  enableStatusOptions,
  eventBaseStatusOptions,
  eventRuntimeStatusOptions,
  getOptionLabel,
  roleOptions,
  signupStatusOptions
} from '@/utils/constants';

const props = defineProps({
  category: {
    type: String,
    required: true
  },
  value: {
    type: [String, Number],
    default: ''
  }
});

const optionMap = {
  role: roleOptions,
  enable: enableStatusOptions,
  eventBase: eventBaseStatusOptions,
  eventRuntime: eventRuntimeStatusOptions,
  signup: signupStatusOptions,
  checkin: checkinStatusOptions
};

const label = computed(() => getOptionLabel(optionMap[props.category] || [], props.value));

const tagType = computed(() => {
  const value = props.value;
  if (props.category === 'enable') {
    return value === 1 ? 'success' : 'info';
  }
  if (props.category === 'signup') {
    return {
      pending: 'warning',
      approved: 'success',
      rejected: 'danger',
      cancelled: 'info'
    }[value] || '';
  }
  if (props.category === 'eventBase' || props.category === 'eventRuntime') {
    return {
      draft: 'info',
      published: 'success',
      signup_open: 'success',
      signup_closed: 'warning',
      ongoing: '',
      finished: 'info',
      cancelled: 'danger'
    }[value] || '';
  }
  if (props.category === 'checkin') {
    return value === 1 ? 'success' : 'info';
  }
  if (props.category === 'role') {
    return value === 'admin' ? 'danger' : 'primary';
  }
  return '';
});
</script>

<template>
  <el-tag :type="tagType" effect="light">{{ label }}</el-tag>
</template>
