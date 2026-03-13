export const commonText = {
  cancel: '取消',
  save: '保存',
  confirm: '确认'
};

export const dialogTitles = {
  deleteConfirm: '删除确认',
  cancelSignup: '取消报名',
  logout: '退出登录'
};

export const successMessages = {
  login: '登录成功',
  signupApply: '报名成功',
  signupCancel: '取消报名成功',
  signupAuditPass: '审核通过成功',
  signupAuditReject: '审核驳回成功',
  checkin: '签到成功',
  userCreate: '用户新增成功',
  userUpdate: '用户更新成功',
  userStatusUpdate: '状态更新成功',
  categoryCreate: '分类新增成功',
  categoryUpdate: '分类更新成功',
  categoryDelete: '删除成功',
  eventCreate: '活动新增成功',
  eventUpdate: '活动更新成功',
  eventPublish: '活动发布成功',
  eventCancel: '活动取消成功',
  eventDelete: '活动删除成功'
};

export function isDialogCancel(action) {
  return action === 'cancel' || action === 'close';
}

export function buildDeleteConfirmMessage(entityLabel, targetName) {
  return `确认删除${entityLabel}“${targetName}”吗？`;
}

export function buildCancelSignupConfirmMessage(eventTitle) {
  return `确认取消报名“${eventTitle}”吗？`;
}

export function buildLogoutConfirmMessage() {
  return '确认退出当前登录账号吗？';
}
