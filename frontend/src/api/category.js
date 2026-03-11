import request from '@/utils/request';

export function getCategoryList(params) {
  return request.get('/api/event-category/list', { params });
}

export function addCategory(data) {
  return request.post('/api/event-category/add', data);
}

export function updateCategory(data) {
  return request.put('/api/event-category/update', data);
}

export function deleteCategory(id) {
  return request.delete(`/api/event-category/${id}`);
}
