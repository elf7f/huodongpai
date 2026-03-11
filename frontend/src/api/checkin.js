import request from '@/utils/request';

export function getCheckinPage(params) {
  return request.get('/api/checkin/page', { params });
}

export function doCheckin(signupId) {
  return request.put(`/api/checkin/do/${signupId}`);
}
