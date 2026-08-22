<script setup>
import { computed, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { registerUser } from '../../api/auth'
import SnowLogo from '../../components/SnowLogo.vue'
import { useAuthStore } from '../../stores/auth'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const mode = ref(route.query.mode === 'register' ? 'register' : 'login')
const loginFormRef = ref()
const registerFormRef = ref()
const submitting = ref(false)
const showLoginPassword = ref(false)
const showRegisterPassword = ref(false)

const loginForm = reactive({ username: '', password: '' })
const registerForm = reactive({ username: '', password: '', confirmPassword: '', name: '', phone: '' })
const isRegister = computed(() => mode.value === 'register')

const loginRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}
const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度为 3—20 个字符', trigger: 'blur' }
  ],
  name: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的 11 位手机号', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 24, message: '密码长度为 6—24 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: (_rule, value, callback) => value === registerForm.password ? callback() : callback(new Error('两次输入的密码不一致')), trigger: 'blur' }
  ]
}

const setMode = (value) => {
  mode.value = value
  const query = { ...route.query }
  if (value === 'register') query.mode = 'register'
  else delete query.mode
  router.replace({ query })
}

const safeRedirect = () => {
  const target = String(route.query.redirect || '/account')
  return target.startsWith('/') && !target.startsWith('//') ? target : '/account'
}

const submitLogin = async () => {
  try { await loginFormRef.value.validate() } catch { return }
  submitting.value = true
  try {
    await auth.userLogin({ username: loginForm.username.trim(), password: loginForm.password })
    ElMessage.success('登录成功，欢迎来到龙江雪境')
    await router.replace(safeRedirect())
  } finally {
    submitting.value = false
  }
}

const submitRegister = async () => {
  try { await registerFormRef.value.validate() } catch { return }
  submitting.value = true
  try {
    await registerUser({
      username: registerForm.username.trim(),
      password: registerForm.password,
      name: registerForm.name.trim(),
      phone: registerForm.phone.trim()
    })
    await auth.userLogin({ username: registerForm.username.trim(), password: registerForm.password })
    ElMessage.success('注册成功，已为你自动登录')
    await router.replace(safeRedirect())
  } finally {
    submitting.value = false
  }
}

watch(() => route.query.mode, (value) => { mode.value = value === 'register' ? 'register' : 'login' })
</script>

<template>
  <div class="auth-page">
    <div class="auth-snow" aria-hidden="true"><span>❄</span><span>✦</span><span>❄</span><span>•</span></div>
    <div class="auth-mountain mountain-back" aria-hidden="true" />
    <div class="auth-mountain mountain-front" aria-hidden="true" />
    <div class="page-container auth-layout">
      <section class="auth-story">
        <span class="story-kicker">WELCOME TO HEILONGJIANG</span>
        <h1>登录雪境<br />开启你的冰雪旅程</h1>
        <p>一个账号即可完成雪票购买、雪具租赁、课程报名、订单管理与意见反馈。</p>
        <div class="story-points">
          <div><el-icon><CircleCheckFilled /></el-icon><span><strong>真实雪场数据</strong><small>雪道、天气和设施信息统一查询</small></span></div>
          <div><el-icon><CircleCheckFilled /></el-icon><span><strong>一站式服务</strong><small>购买、租赁、报名与订单处理</small></span></div>
          <div><el-icon><CircleCheckFilled /></el-icon><span><strong>安全行程提醒</strong><small>及时获取雪场公告和天气雪况</small></span></div>
        </div>
      </section>

      <section class="auth-card glass-card">
        <div class="mobile-logo"><SnowLogo /></div>
        <div v-if="auth.isUserLoggedIn" class="logged-notice"><el-icon><CircleCheckFilled /></el-icon><span>你已登录游客账号</span><router-link to="/account">进入个人中心</router-link></div>
        <div class="mode-tabs">
          <button type="button" :class="{ active: !isRegister }" @click="setMode('login')">游客登录</button>
          <button type="button" :class="{ active: isRegister }" @click="setMode('register')">创建账号</button>
        </div>

        <transition name="form-switch" mode="out-in">
          <div v-if="!isRegister" key="login" class="form-panel">
            <div class="form-heading"><span>WELCOME BACK</span><h2>欢迎回来</h2><p>登录后继续管理你的滑雪行程</p></div>
            <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" label-position="top" @keyup.enter="submitLogin">
              <el-form-item label="用户名" prop="username"><el-input v-model="loginForm.username" size="large" autocomplete="username" placeholder="请输入用户名" prefix-icon="User" /></el-form-item>
              <el-form-item label="密码" prop="password"><el-input v-model="loginForm.password" size="large" autocomplete="current-password" placeholder="请输入密码" prefix-icon="Lock" :type="showLoginPassword ? 'text' : 'password'"><template #suffix><button class="password-toggle" type="button" @click="showLoginPassword = !showLoginPassword"><el-icon><View /></el-icon></button></template></el-input></el-form-item>
              <el-button class="submit-button" type="primary" size="large" :loading="submitting" @click="submitLogin">登录游客账号 <el-icon class="el-icon--right"><ArrowRight /></el-icon></el-button>
            </el-form>
            <p class="switch-tip">还没有账号？<button type="button" @click="setMode('register')">立即注册</button></p>
          </div>

          <div v-else key="register" class="form-panel register-panel">
            <div class="form-heading"><span>JOIN THE JOURNEY</span><h2>创建游客账号</h2><p>填写基本信息，注册后将自动登录</p></div>
            <el-form ref="registerFormRef" :model="registerForm" :rules="registerRules" label-position="top" @keyup.enter="submitRegister">
              <div class="form-columns"><el-form-item label="用户名" prop="username"><el-input v-model="registerForm.username" size="large" autocomplete="username" placeholder="3—20 个字符" /></el-form-item><el-form-item label="真实姓名" prop="name"><el-input v-model="registerForm.name" size="large" autocomplete="name" placeholder="请输入姓名" /></el-form-item></div>
              <el-form-item label="联系电话" prop="phone"><el-input v-model="registerForm.phone" size="large" autocomplete="tel" maxlength="11" placeholder="用于游客服务联系" prefix-icon="Phone" /></el-form-item>
              <el-form-item label="设置密码" prop="password"><el-input v-model="registerForm.password" size="large" autocomplete="new-password" placeholder="至少 6 个字符" prefix-icon="Lock" :type="showRegisterPassword ? 'text' : 'password'"><template #suffix><button class="password-toggle" type="button" @click="showRegisterPassword = !showRegisterPassword"><el-icon><View /></el-icon></button></template></el-input></el-form-item>
              <el-form-item label="确认密码" prop="confirmPassword"><el-input v-model="registerForm.confirmPassword" size="large" autocomplete="new-password" placeholder="再次输入密码" prefix-icon="Lock" type="password" /></el-form-item>
              <el-button class="submit-button" type="primary" size="large" :loading="submitting" @click="submitRegister">注册并登录 <el-icon class="el-icon--right"><ArrowRight /></el-icon></el-button>
            </el-form>
            <p class="agreement">注册即表示你知悉：系统仅使用所填信息提供游客订单和联系服务。</p>
            <p class="switch-tip">已有账号？<button type="button" @click="setMode('login')">返回登录</button></p>
          </div>
        </transition>
      </section>
    </div>
  </div>
</template>

<style scoped>
.auth-page{position:relative;min-height:calc(100vh - 76px);overflow:hidden;background:linear-gradient(125deg,#07233d 0%,#0d6592 57%,#3ab9c9);color:#fff}.auth-page::before{position:absolute;inset:0;background:radial-gradient(circle at 80% 5%,rgba(255,255,255,.2),transparent 25%);content:''}.auth-snow{position:absolute;inset:0}.auth-snow span{position:absolute;color:rgba(255,255,255,.45)}.auth-snow span:nth-child(1){top:12%;left:8%;font-size:28px}.auth-snow span:nth-child(2){top:24%;left:47%;font-size:20px}.auth-snow span:nth-child(3){right:7%;bottom:18%;font-size:33px}.auth-snow span:nth-child(4){top:62%;left:19%;font-size:26px}.auth-mountain{position:absolute;border-radius:50% 50% 0 0;transform:rotate(-10deg)}.mountain-back{right:30%;bottom:-52%;width:55%;height:64%;background:rgba(184,234,244,.24)}.mountain-front{right:-15%;bottom:-54%;width:75%;height:76%;background:linear-gradient(150deg,rgba(248,254,255,.87),rgba(197,233,242,.5))}.auth-layout{position:relative;z-index:2;display:grid;min-height:calc(100vh - 76px);grid-template-columns:1fr 520px;align-items:center;gap:80px;padding-top:55px;padding-bottom:55px}.story-kicker{font-size:11px;font-weight:700;letter-spacing:3px;opacity:.72}.auth-story h1{margin:20px 0 0;font-size:clamp(42px,5vw,62px);line-height:1.2;letter-spacing:-2px}.auth-story>p{max-width:550px;margin:23px 0 0;color:rgba(255,255,255,.72);font-size:15px;line-height:1.9}.story-points{display:grid;gap:17px;margin-top:34px}.story-points>div{display:flex;align-items:center;gap:12px}.story-points>.el-icon{color:#7ce1e8;font-size:20px}.story-points span{display:grid;gap:4px}.story-points strong{font-size:13px}.story-points small{color:rgba(255,255,255,.58);font-size:11px}.auth-card{max-height:calc(100vh - 110px);overflow-y:auto;padding:29px 38px;color:#17354c}.mobile-logo{display:none}.logged-notice{display:flex;align-items:center;gap:7px;margin-bottom:15px;padding:10px 13px;border-radius:10px;background:#e9f8ef;color:#2b7750;font-size:12px}.logged-notice a{margin-left:auto;color:#1479b8;font-weight:700}.mode-tabs{display:grid;grid-template-columns:1fr 1fr;margin-bottom:29px;padding:4px;border-radius:12px;background:#eaf2f6}.mode-tabs button{padding:10px;border:0;border-radius:9px;background:transparent;color:#6f8595;font-weight:600;cursor:pointer}.mode-tabs button.active{background:#fff;color:#1479b8;box-shadow:0 4px 12px rgba(20,80,115,.09)}.form-heading{margin-bottom:24px}.form-heading>span{color:#1682b2;font-size:9px;font-weight:800;letter-spacing:2px}.form-heading h2{margin:7px 0 0;font-size:27px}.form-heading p{margin:7px 0 0;color:#8294a1;font-size:12px}.form-panel :deep(.el-form-item__label){color:#49657a;font-size:12px;font-weight:600}.form-panel :deep(.el-input__wrapper){border-radius:10px;box-shadow:0 0 0 1px #d8e6ed inset}.password-toggle{display:grid;place-items:center;padding:4px;border:0;background:transparent;color:#8496a2;cursor:pointer}.submit-button{width:100%;margin-top:5px}.switch-tip{margin:20px 0 0;color:#8394a0;font-size:12px;text-align:center}.switch-tip button{padding:0;border:0;background:transparent;color:#1479b8;font-weight:700;cursor:pointer}.form-columns{display:grid;grid-template-columns:1fr 1fr;gap:12px}.agreement{margin:13px 0 0;color:#94a2ab;font-size:10px;line-height:1.6;text-align:center}.form-switch-enter-active,.form-switch-leave-active{transition:.2s ease}.form-switch-enter-from{transform:translateX(12px);opacity:0}.form-switch-leave-to{transform:translateX(-12px);opacity:0}
@media(max-width:950px){.auth-layout{grid-template-columns:1fr;max-width:600px;padding-top:65px;padding-bottom:65px}.auth-story{display:none}.auth-card{max-height:none}.mobile-logo{display:flex;justify-content:center;margin-bottom:24px}}
@media(max-width:600px){.auth-page{min-height:calc(100vh - 68px);background:#edf6fa}.auth-layout{width:100%;padding:22px 14px}.auth-card{padding:24px 20px;border:0;border-radius:17px;box-shadow:none}.form-columns{grid-template-columns:1fr}.register-panel .form-heading{margin-bottom:18px}}
</style>
