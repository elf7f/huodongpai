<script setup>
import { reactive, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { useAuthStore } from '@/store/auth';

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();
const loading = ref(false);

const form = reactive({
  username: 'admin',
  password: '123456'
});

async function handleLogin() {
  loading.value = true;
  try {
    await authStore.handleLogin(form);
    ElMessage.success('登录成功');
    const redirect = route.query.redirect;
    router.replace(redirect || (authStore.isAdmin ? '/dashboard' : '/events'));
  } catch (error) {
    return error;
  } finally {
    loading.value = false;
  }
}

function fillUserDemo() {
  form.username = 'test01';
  form.password = '123456';
}

function fillAdminDemo() {
  form.username = 'admin';
  form.password = '123456';
}
</script>

<template>
  <div class="login-page">
    <div class="login-panel">
      <div class="login-panel__intro">
        <h1>活动派</h1>
        <p>活动报名与签到管理系统</p>
        <div class="login-panel__demo">
          <el-button type="primary" plain @click="fillAdminDemo">填充管理员账号</el-button>
          <el-button plain @click="fillUserDemo">填充普通用户账号</el-button>
        </div>
      </div>

      <el-form label-position="top" @submit.prevent="handleLogin">
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input
            v-model="form.password"
            show-password
            placeholder="请输入密码"
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-button type="primary" :loading="loading" class="login-submit" @click="handleLogin">
          登录系统
        </el-button>
      </el-form>

      <div class="login-tips">
        <p>管理员：`admin / 123456`</p>
        <p>普通用户：`test01 / 123456`</p>
      </div>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  min-height: 100vh;
  display: grid;
  place-items: center;
  background:
    radial-gradient(circle at top left, rgb(64 158 255 / 25%), transparent 35%),
    radial-gradient(circle at bottom right, rgb(103 194 58 / 20%), transparent 35%),
    #f5f7fa;
  padding: 20px;
}

.login-panel {
  width: min(460px, 100%);
  background: rgb(255 255 255 / 92%);
  backdrop-filter: blur(14px);
  border-radius: 24px;
  box-shadow: 0 24px 60px rgb(15 23 42 / 10%);
  padding: 32px;
}

.login-panel__intro h1 {
  margin: 0;
  font-size: 32px;
}

.login-panel__intro p {
  margin: 8px 0 0;
  color: #86909c;
}

.login-panel__demo {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  margin: 20px 0 12px;
}

.login-submit {
  width: 100%;
  margin-top: 6px;
}

.login-tips {
  margin-top: 20px;
  padding: 16px;
  border-radius: 16px;
  background: #f7f8fa;
  color: #4e5969;
  font-size: 13px;
}

.login-tips p {
  margin: 6px 0;
}
</style>
