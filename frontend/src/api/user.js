import request from '@/utils/request';

export function getUserPage(params) {
  return request.get('/api/user/page', { params });
}

export function addUser(data) {
  return request.post('/api/user/add', data);
}

export function updateUser(data) {
  return request.put('/api/user/update', data);
}

export function updateUserStatus(id, data) {
  return request.put(`/api/user/status/${id}`, data);
}
