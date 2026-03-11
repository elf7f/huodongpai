import { defineStore } from 'pinia';
import { getCurrentUserInfo, login, logout } from '@/api/auth';
import { getToken, getUser, removeToken, removeUser, setToken, setUser } from '@/utils/storage';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: '',
    user: null
  }),
  getters: {
    isLoggedIn: (state) => Boolean(state.token),
    isAdmin: (state) => state.user?.role === 'admin'
  },
  actions: {
    initFromStorage() {
      this.token = getToken();
      this.user = getUser();
    },
    setAuth(token, user) {
      this.token = token;
      this.user = user;
      setToken(token);
      setUser(user);
    },
    clearAuth() {
      this.token = '';
      this.user = null;
      removeToken();
      removeUser();
    },
    async handleLogin(payload) {
      const data = await login(payload);
      this.setAuth(data.token, data.userInfo);
      return data;
    },
    async refreshUserInfo() {
      const userInfo = await getCurrentUserInfo();
      this.user = userInfo;
      setUser(userInfo);
      return userInfo;
    },
    async handleLogout() {
      try {
        await logout();
      } finally {
        this.clearAuth();
      }
    }
  }
});
