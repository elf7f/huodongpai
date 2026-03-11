import axios from 'axios';
import { ElMessage } from 'element-plus';
import { getToken, removeToken, removeUser } from './storage';

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '',
  timeout: 15000
});

request.interceptors.request.use((config) => {
  const token = getToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

request.interceptors.response.use(
  (response) => {
    const payload = response.data;
    if (payload?.code === 200) {
      return payload.data;
    }
    const message = payload?.message || '请求失败';
    ElMessage.error(message);
    return Promise.reject(new Error(message));
  },
  (error) => {
    const status = error?.response?.status;
    const message = error?.response?.data?.message || error?.message || '网络异常';
    if (status === 401) {
      removeToken();
      removeUser();
      if (window.location.pathname !== '/login') {
        window.location.replace('/login');
      }
    }
    ElMessage.error(message);
    return Promise.reject(error);
  }
);

export default request;
