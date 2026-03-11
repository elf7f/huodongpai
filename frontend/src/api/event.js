import request from '@/utils/request';

export function getEventPage(params) {
  return request.get('/api/event/page', { params });
}

export function getEventManagePage(params) {
  return request.get('/api/event/manage/page', { params });
}

export function getEventDetail(id) {
  return request.get(`/api/event/${id}`);
}

export function addEvent(data) {
  return request.post('/api/event/add', data);
}

export function updateEvent(data) {
  return request.put('/api/event/update', data);
}

export function publishEvent(id) {
  return request.put(`/api/event/publish/${id}`);
}

export function cancelEvent(id) {
  return request.put(`/api/event/cancel/${id}`);
}

export function deleteEvent(id) {
  return request.delete(`/api/event/${id}`);
}
