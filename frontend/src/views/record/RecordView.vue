<script setup>
import { onMounted } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

onMounted(() => {
  router.replace('/my-signups');
});
</script>

<template>
  <div class="record-redirect">
    <p>正在跳转到我的报名...</p>
  </div>
</template>

<style scoped>
.record-redirect {
  min-height: 160px;
  display: grid;
  place-items: center;
  color: #606266;
}
</style>
