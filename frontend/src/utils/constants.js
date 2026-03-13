export const roleOptions = [
  { label: '管理员', value: 'admin' },
  { label: '普通用户', value: 'user' }
];

export const enableStatusOptions = [
  { label: '启用', value: 1 },
  { label: '禁用', value: 0 }
];

export const eventBaseStatusOptions = [
  { label: '草稿', value: 'draft' },
  { label: '已发布', value: 'published' },
  { label: '已取消', value: 'cancelled' }
];

export const eventRuntimeStatusOptions = [
  { label: '报名中', value: 'signup_open' },
  { label: '报名结束', value: 'signup_closed' },
  { label: '进行中', value: 'ongoing' },
  { label: '已结束', value: 'finished' },
  { label: '已取消', value: 'cancelled' },
  { label: '草稿', value: 'draft' }
];

export const eventRuntimeFilterOptions = eventRuntimeStatusOptions.filter(
  (item) => !['cancelled', 'draft'].includes(item.value)
);

export const signupStatusOptions = [
  { label: '待审核', value: 'pending' },
  { label: '已通过', value: 'approved' },
  { label: '已驳回', value: 'rejected' },
  { label: '已取消', value: 'cancelled' }
];

export const checkinStatusOptions = [
  { label: '未签到', value: 0 },
  { label: '已签到', value: 1 }
];

export function getOptionLabel(options, value) {
  return options.find((item) => item.value === value)?.label ?? value ?? '-';
}
