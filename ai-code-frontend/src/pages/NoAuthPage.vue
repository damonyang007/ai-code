<template>
  <div class="no-auth-page">
    <div class="content-card">
      <div class="code">401</div>
      <h1>你没有权限访问这个页面</h1>
      <p>请联系管理员开通权限，或者返回首页继续浏览其他内容。</p>
      <a-space>
        <a-button type="primary" @click="router.push('/')">返回首页</a-button>
        <a-button @click="router.back()">返回上一页</a-button>
      </a-space>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()
</script>

<style scoped>
.no-auth-page {
  min-height: calc(100vh - 128px);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 32px 16px;
  background:
    radial-gradient(circle at top, rgba(24, 144, 255, 0.12), transparent 48%),
    linear-gradient(180deg, #f7fbff 0%, #eef5ff 100%);
}

.content-card {
  width: min(520px, 100%);
  padding: 40px 32px;
  text-align: center;
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid rgba(24, 144, 255, 0.12);
  border-radius: 24px;
  box-shadow: 0 24px 60px rgba(15, 23, 42, 0.08);
}

.code {
  font-size: 72px;
  line-height: 1;
  font-weight: 700;
  letter-spacing: 4px;
  color: #1677ff;
  margin-bottom: 12px;
}

h1 {
  margin: 0 0 12px;
  font-size: 28px;
  color: #1f2937;
}

p {
  margin: 0 0 24px;
  color: #64748b;
  line-height: 1.7;
}
</style>
