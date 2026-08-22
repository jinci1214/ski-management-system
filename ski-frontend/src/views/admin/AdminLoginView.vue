<script setup>
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '../../stores/auth'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const formRef = ref()
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入管理员账号', trigger: 'blur' },
    { min: 2, max: 30, message: '账号长度应为 2—30 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入登录密码', trigger: 'blur' },
    { min: 3, max: 40, message: '密码长度应为 3—40 个字符', trigger: 'blur' }
  ]
}

const submit = async () => {
  if (!formRef.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    await auth.adminLogin({ ...form })
    ElMessage.success('登录成功，欢迎进入管理端')
    const redirect = typeof route.query.redirect === 'string' &&
      route.query.redirect.startsWith('/admin/') &&
      route.query.redirect !== '/admin/login'
      ? route.query.redirect
      : '/admin/dashboard'
    await router.replace(redirect)
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <main class="login-page">
    <section class="intro-panel">
      <div class="intro-content">
        <div class="system-label"><span></span> HEILONGJIANG SKI MANAGEMENT</div>
        <h1>让每一片雪场<br><em>安全、高效、有序</em></h1>
        <p>
          集雪场运营、票务服务、设备维护、安全巡检与游客反馈于一体，
          为黑龙江滑雪场提供清晰可靠的日常管理支持。
        </p>
        <div class="feature-grid">
          <div><el-icon><DataAnalysis /></el-icon><strong>经营数据</strong><small>核心指标及时掌握</small></div>
          <div><el-icon><WarnTriangleFilled /></el-icon><strong>安全保障</strong><small>巡检维护完整留痕</small></div>
          <div><el-icon><Place /></el-icon><strong>雪场运营</strong><small>场地设施集中管理</small></div>
        </div>
      </div>
      <div class="mountain mountain-back"></div>
      <div class="mountain mountain-front"></div>
      <div class="snow-dots"></div>
    </section>

    <section class="form-panel">
      <div class="login-card">
        <div class="mobile-brand">
          <div class="brand-icon"><el-icon><Sunny /></el-icon></div>
          <span>龙江雪域</span>
        </div>
        <div class="card-heading">
          <span>MANAGEMENT PORTAL</span>
          <h2>管理端登录</h2>
          <p>请使用已启用的员工账号登录系统</p>
        </div>

        <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @keyup.enter="submit">
          <el-form-item label="管理员账号" prop="username">
            <el-input v-model.trim="form.username" size="large" placeholder="请输入账号" autocomplete="username" clearable>
              <template #prefix><el-icon><User /></el-icon></template>
            </el-input>
          </el-form-item>
          <el-form-item label="登录密码" prop="password">
            <el-input
              v-model="form.password"
              size="large"
              type="password"
              placeholder="请输入密码"
              autocomplete="current-password"
              show-password
            >
              <template #prefix><el-icon><Lock /></el-icon></template>
            </el-input>
          </el-form-item>
          <el-button class="login-button" type="primary" size="large" :loading="loading" @click="submit">
            {{ loading ? '正在验证...' : '登录管理系统' }}
            <el-icon v-if="!loading"><Right /></el-icon>
          </el-button>
        </el-form>

        <div class="security-note">
          <el-icon><CircleCheck /></el-icon>
          <span>系统将安全保存本次登录状态，请勿在公共设备上长期登录</span>
        </div>
        <router-link class="back-home" to="/">
          <el-icon><Back /></el-icon>返回游客端首页
        </router-link>
      </div>
    </section>
  </main>
</template>

<style scoped>
.login-page {
  display: grid;
  min-height: 100vh;
  grid-template-columns: minmax(430px, 1.08fr) minmax(420px, 0.92fr);
  background: #f5f9fc;
}

.intro-panel {
  position: relative;
  display: flex;
  overflow: hidden;
  align-items: center;
  padding: 8vw;
  color: #fff;
  background:
    radial-gradient(circle at 18% 22%, rgba(41, 191, 215, 0.28), transparent 28%),
    linear-gradient(145deg, #071f36 0%, #0a395d 64%, #12688d 100%);
}

.intro-content {
  position: relative;
  z-index: 3;
  width: min(600px, 100%);
  transform: translateY(-3vh);
}

.system-label {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #9bdbe8;
  font-size: 11px;
  letter-spacing: 2px;
}

.system-label span {
  width: 30px;
  height: 1px;
  background: #54d2e4;
}

.intro-content h1 {
  margin: 28px 0 20px;
  font-size: clamp(39px, 4.4vw, 66px);
  font-weight: 700;
  line-height: 1.18;
  letter-spacing: -2px;
}

.intro-content h1 em {
  color: #8ee9f3;
  font-style: normal;
}

.intro-content > p {
  max-width: 550px;
  margin: 0;
  color: #bed7e6;
  font-size: 15px;
  line-height: 2;
}

.feature-grid {
  display: grid;
  max-width: 590px;
  margin-top: 48px;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}

.feature-grid > div {
  padding: 17px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.06);
  backdrop-filter: blur(8px);
}

.feature-grid .el-icon {
  display: block;
  margin-bottom: 15px;
  color: #81e1ec;
  font-size: 21px;
}

.feature-grid strong,
.feature-grid small {
  display: block;
}

.feature-grid strong {
  font-size: 14px;
}

.feature-grid small {
  margin-top: 6px;
  color: #92b8cc;
  font-size: 11px;
}

.mountain {
  position: absolute;
  right: -12%;
  bottom: -23%;
  width: 92%;
  aspect-ratio: 1;
  transform: rotate(45deg);
  border-radius: 42px;
  background: rgba(255, 255, 255, 0.055);
}

.mountain-back {
  right: 36%;
  bottom: -47%;
  background: rgba(83, 195, 218, 0.08);
}

.snow-dots {
  position: absolute;
  inset: 0;
  opacity: 0.3;
  background-image: radial-gradient(circle, #fff 1px, transparent 1.5px);
  background-size: 54px 54px;
  mask-image: linear-gradient(to bottom, black, transparent 78%);
}

.form-panel {
  display: grid;
  place-items: center;
  padding: 48px;
}

.login-card {
  width: min(430px, 100%);
}

.mobile-brand {
  display: flex;
  align-items: center;
  gap: 11px;
  margin-bottom: 58px;
  color: #12324a;
  font-size: 18px;
  font-weight: 700;
}

.brand-icon {
  display: grid;
  width: 40px;
  height: 40px;
  place-items: center;
  border-radius: 12px;
  color: #fff;
  background: linear-gradient(135deg, #1378b7, #27b9cf);
}

.card-heading > span {
  color: #1885b7;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 1.7px;
}

.card-heading h2 {
  margin: 10px 0 8px;
  color: #102e46;
  font-size: 32px;
}

.card-heading p {
  margin: 0 0 34px;
  color: #7a8e9e;
  font-size: 14px;
}

.login-card :deep(.el-form-item) {
  margin-bottom: 24px;
}

.login-card :deep(.el-form-item__label) {
  color: #304d63;
  font-weight: 600;
}

.login-card :deep(.el-input__wrapper) {
  min-height: 48px;
  border-radius: 11px;
  box-shadow: 0 0 0 1px #d7e5ed inset;
}

.login-button {
  width: 100%;
  height: 50px;
  margin-top: 4px;
  border-radius: 11px;
  font-weight: 600;
}

.login-button .el-icon {
  margin-left: 8px;
}

.security-note {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  margin-top: 22px;
  padding: 12px 14px;
  border-radius: 9px;
  color: #708899;
  background: #eef6fa;
  font-size: 11px;
  line-height: 1.6;
}

.security-note .el-icon {
  margin-top: 2px;
  color: #25a37b;
}

.back-home {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  margin-top: 30px;
  color: #688194;
  font-size: 13px;
}

.back-home:hover {
  color: #1479b8;
}

@media (max-width: 880px) {
  .login-page {
    display: block;
    background:
      linear-gradient(rgba(7, 31, 54, 0.66), rgba(7, 31, 54, 0.88)),
      linear-gradient(145deg, #0a395d, #1380a0);
  }

  .intro-panel {
    display: none;
  }

  .form-panel {
    min-height: 100vh;
    padding: 28px 18px;
  }

  .login-card {
    padding: 32px 28px;
    border-radius: 20px;
    background: #fff;
    box-shadow: 0 24px 70px rgba(0, 16, 30, 0.3);
  }

  .mobile-brand {
    margin-bottom: 38px;
  }
}

@media (max-width: 480px) {
  .form-panel {
    padding: 14px;
  }

  .login-card {
    padding: 27px 21px;
  }

  .card-heading h2 {
    font-size: 27px;
  }
}
</style>
