import request from '@/utils/request';

export function getDashboard() {
  return request.get('/api/statistics/dashboard');
}

export function getHotEvents(params) {
  return request.get('/api/statistics/hot-events', { params });
}

export function getSignupTrend(params) {
  return request.get('/api/statistics/signup-trend', { params });
}
