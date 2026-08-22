<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()
</script>

<template>
  <main class="not-found">
    <div class="mountain" aria-hidden="true"></div>
    <p class="code">404</p>
    <h1>这条雪道还未开放</h1>
    <p>你访问的页面不存在，可以返回首页继续浏览。</p>
    <el-button type="primary" round size="large" @click="router.push('/')">返回首页</el-button>
  </main>
</template>

<style scoped>
.not-found {
  min-height: 100vh;
  display: grid;
  place-content: center;
  justify-items: center;
  padding: 32px;
  color: #16324a;
  text-align: center;
  background: linear-gradient(180deg, #e8f6fc 0%, #f7fbfd 65%, #fff 100%);
}

.mountain {
  width: 220px;
  height: 110px;
  margin-bottom: -26px;
  clip-path: polygon(0 100%, 28% 42%, 42% 64%, 62% 18%, 100% 100%);
  background: linear-gradient(135deg, #c9e6f2, #6ca8c6);
}

.code {
  position: relative;
  margin: 0;
  color: #1479b8;
  font-size: 112px;
  font-weight: 900;
  line-height: 1;
}

h1 {
  margin: 12px 0;
}

.not-found > p:not(.code) {
  margin: 0 0 28px;
  color: #6d8294;
}
</style>
