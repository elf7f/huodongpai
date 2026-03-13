<script setup>
import { computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessageBox } from 'element-plus';
import { useAuthStore } from '@/store/auth';
import { buildLogoutConfirmMessage, dialogTitles, isDialogCancel } from '@/utils/ui';

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();

const menuItems = computed(() => {
  const baseItems = [
    { index: '/events', label: '活动列表', icon: 'Calendar' },
    { index: '/my-signups', label: '我的报名', icon: 'Tickets' }
  ];

  if (!authStore.isAdmin) {
    return baseItems;
  }

  return [
    { index: '/dashboard', label: '仪表盘', icon: 'DataLine' },
    { index: '/events', label: '活动列表', icon: 'Calendar' },
    { index: '/my-signups', label: '我的报名', icon: 'Tickets' },
    { index: '/event-manage', label: '活动管理', icon: 'Management' },
    { index: '/signup-manage', label: '报名管理', icon: 'DocumentChecked' },
    { index: '/checkin-manage', label: '签到管理', icon: 'CircleCheck' },
    { index: '/statistics', label: '统计分析', icon: 'TrendCharts' },
    { index: '/users', label: '用户管理', icon: 'UserFilled' },
    { index: '/categories', label: '活动分类', icon: 'CollectionTag' }
  ];
});

const pageTitle = computed(() => route.meta?.title || '活动派');

function navigate(path) {
  router.push(path);
}

async function handleLogout() {
  try {
    await ElMessageBox.confirm(buildLogoutConfirmMessage(), dialogTitles.logout, {
      type: 'warning'
    });
    await authStore.handleLogout();
    router.replace('/login');
  } catch (error) {
    if (!isDialogCancel(error)) {
      throw error;
    }
  }
}
</script>

<template>
  <div class="layout-shell">
    <aside class="layout-aside">
      <div class="layout-brand">
        <div class="layout-brand__logo">活</div>
        <div>
          <strong>活动派</strong>
          <p>{{ authStore.isAdmin ? '管理员工作台' : '用户中心' }}</p>
        </div>
      </div>

      <el-menu
        :default-active="route.path"
        class="layout-menu"
        unique-opened
        @select="navigate"
      >
        <el-menu-item
          v-for="item in menuItems"
          :key="item.index"
          :index="item.index"
        >
          <el-icon><component :is="item.icon" /></el-icon>
          <span>{{ item.label }}</span>
        </el-menu-item>
      </el-menu>
    </aside>

    <main class="layout-main">
      <header class="layout-header">
        <div>
          <h2>{{ pageTitle }}</h2>
          <p class="muted-text">活动报名、审核、签到与统计一体化管理</p>
        </div>

        <div class="layout-header__actions">
          <el-tag :type="authStore.isAdmin ? 'danger' : 'primary'" effect="light">
            {{ authStore.isAdmin ? '管理员' : '普通用户' }}
          </el-tag>
          <span>{{ authStore.user?.realName || authStore.user?.username }}</span>
          <el-button type="danger" plain @click="handleLogout">退出</el-button>
        </div>
      </header>

      <section class="layout-content">
        <router-view />
      </section>
    </main>
  </div>
</template>

<style scoped>
.layout-shell {
  min-height: 100vh;
  display: grid;
  grid-template-columns: 240px 1fr;
}

.layout-aside {
  background: #101828;
  color: #fff;
  padding: 20px 16px;
}

.layout-brand {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
}

.layout-brand__logo {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  background: linear-gradient(135deg, #409eff, #79bbff);
  display: grid;
  place-items: center;
  font-size: 20px;
  font-weight: 700;
}

.layout-brand strong {
  display: block;
  font-size: 18px;
}

.layout-brand p {
  margin: 4px 0 0;
  color: #98a2b3;
  font-size: 12px;
}

.layout-menu {
  border-right: none;
  background: transparent;
}

:deep(.layout-menu .el-menu-item) {
  color: #d0d5dd;
  border-radius: 10px;
  margin-bottom: 6px;
}

:deep(.layout-menu .el-menu-item.is-active) {
  color: #fff;
  background: rgb(64 158 255 / 20%);
}

.layout-main {
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.layout-header {
  height: 84px;
  padding: 18px 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(14px);
  border-bottom: 1px solid #e5e6eb;
}

.layout-header h2 {
  margin: 0;
}

.layout-header__actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.layout-content {
  padding: 24px;
}

@media (max-width: 960px) {
  .layout-shell {
    grid-template-columns: 1fr;
  }

  .layout-aside {
    display: none;
  }

  .layout-header {
    padding: 16px;
    height: auto;
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .layout-content {
    padding: 16px;
  }
}
</style>
