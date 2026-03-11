import request from '@/utils/request';

export function applySignup(eventId) {
  return request.post(`/api/signup/apply/${eventId}`);
}

export function getMySignupPage(params) {
  return request.get('/api/signup/my-page', { params });
}

export function cancelSignup(signupId) {
  return request.put(`/api/signup/cancel/${signupId}`);
}

export function getSignupManagePage(params) {
  return request.get('/api/signup/page', { params });
}

export function auditSignupPass(signupId, data) {
  return request.put(`/api/signup/audit/pass/${signupId}`, data);
}

export function auditSignupReject(signupId, data) {
  return request.put(`/api/signup/audit/reject/${signupId}`, data);
}
