import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from '@/store/auth';

const routes = [
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/login/LoginView.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    component: () => import('@/layout/AppLayout.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        name: 'home',
        redirect: (to) => {
          const authStore = useAuthStore();
          return authStore.isAdmin ? '/dashboard' : '/events';
        }
      },
      {
        path: 'dashboard',
        name: 'dashboard',
        component: () => import('@/views/dashboard/DashboardView.vue'),
        meta: { title: '首页仪表盘', roles: ['admin'] }
      },
      {
        path: 'events',
        name: 'events',
        component: () => import('@/views/event/EventListView.vue'),
        meta: { title: '活动列表' }
      },
      {
        path: 'my-signups',
        name: 'my-signups',
        component: () => import('@/views/signup/MySignupView.vue'),
        meta: { title: '我的报名' }
      },
      {
        path: 'event-manage',
        name: 'event-manage',
        component: () => import('@/views/event/EventManageView.vue'),
        meta: { title: '活动管理', roles: ['admin'] }
      },
      {
        path: 'signup-manage',
        name: 'signup-manage',
        component: () => import('@/views/signup/SignupManageView.vue'),
        meta: { title: '报名管理', roles: ['admin'] }
      },
      {
        path: 'checkin-manage',
        name: 'checkin-manage',
        component: () => import('@/views/checkin/CheckinManageView.vue'),
        meta: { title: '签到管理', roles: ['admin'] }
      },
      {
        path: 'statistics',
        name: 'statistics',
        component: () => import('@/views/statistics/StatisticsView.vue'),
        meta: { title: '统计分析', roles: ['admin'] }
      },
      {
        path: 'users',
        name: 'users',
        component: () => import('@/views/user/UserManageView.vue'),
        meta: { title: '用户管理', roles: ['admin'] }
      },
      {
        path: 'categories',
        name: 'categories',
        component: () => import('@/views/category/CategoryManageView.vue'),
        meta: { title: '活动分类', roles: ['admin'] }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'not-found',
    component: () => import('@/views/NotFoundView.vue'),
    meta: { title: '页面不存在' }
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

router.beforeEach((to) => {
  const authStore = useAuthStore();
  if (!authStore.token) {
    authStore.initFromStorage();
  }

  document.title = `${to.meta?.title || '活动派'} - 活动派`;

  if (to.meta?.requiresAuth && !authStore.isLoggedIn) {
    return { name: 'login', query: { redirect: to.fullPath } };
  }

  if (to.name === 'login' && authStore.isLoggedIn) {
    return authStore.isAdmin ? '/dashboard' : '/events';
  }

  const roles = to.meta?.roles;
  if (roles?.length && !roles.includes(authStore.user?.role)) {
    return authStore.isAdmin ? '/dashboard' : '/events';
  }

  return true;
});

export default router;
