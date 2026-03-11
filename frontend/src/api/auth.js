import request from '@/utils/request';

export function login(data) {
  return request.post('/api/auth/login', data);
}

export function getCurrentUserInfo() {
  return request.get('/api/auth/info');
}

export function logout() {
  return request.post('/api/auth/logout');
}
