<script setup>
import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import SnowLogo from '../components/SnowLogo.vue'
import { useAuthStore } from '../stores/auth'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const mobileOpen = ref(false)
const scrolled = ref(false)

const navItems = [
  { label: '首页', path: '/' },
  { label: '雪场导览', path: '/resorts' },
  { label: '滑雪服务', path: '/services' },
  { label: '公告资讯', path: '/announcements' }
]

const displayName = computed(() => auth.user?.name || auth.user?.userName || '游客')
const loginLink = computed(() => route.path === '/login'
  ? route.fullPath
  : { path: '/login', query: { redirect: route.fullPath } })

const updateScroll = () => { scrolled.value = window.scrollY > 18 }
onMounted(() => {
  updateScroll()
  window.addEventListener('scroll', updateScroll, { passive: true })
})
onBeforeUnmount(() => window.removeEventListener('scroll', updateScroll))
watch(() => route.fullPath, () => { mobileOpen.value = false })

const logout = async () => {
  try {
    await ElMessageBox.confirm('确定退出当前账号吗？', '退出登录', {
      confirmButtonText: '退出',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await auth.logout('user')
    ElMessage.success('已安全退出')
    await router.push('/')
  } catch (error) {
    if (error !== 'cancel' && error !== 'close') ElMessage.error('退出失败，请稍后重试')
  }
}
</script>

<template>
  <div class="public-shell">
    <header class="site-header" :class="{ scrolled }">
      <div class="page-container header-inner">
        <router-link to="/" class="brand" aria-label="返回首页"><SnowLogo /></router-link>

        <nav class="desktop-nav" aria-label="主导航">
          <router-link
            v-for="item in navItems"
            :key="item.path"
            :to="item.path"
            :class="{ active: item.path === '/' ? route.path === '/' : route.path.startsWith(item.path) }"
          >{{ item.label }}</router-link>
        </nav>

        <div class="header-actions">
          <template v-if="auth.isUserLoggedIn">
            <router-link to="/account" class="account-link">
              <span class="avatar">{{ displayName.slice(0, 1) }}</span>
              <span>{{ displayName }}</span>
            </router-link>
            <el-button text class="logout-button" @click="logout">退出</el-button>
          </template>
          <router-link v-else :to="loginLink">
            <el-button type="primary" round>登录 / 注册</el-button>
          </router-link>
          <button class="menu-button" type="button" aria-label="打开导航菜单" @click="mobileOpen = true">
            <span /><span /><span />
          </button>
        </div>
      </div>
    </header>

    <main class="site-main">
      <router-view v-slot="{ Component }">
        <transition name="fade-slide" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>

    <footer class="site-footer">
      <div class="page-container footer-grid">
        <div class="footer-brand">
          <SnowLogo inverse />
          <p>服务黑龙江冰雪旅游，让雪场信息、滑雪服务与游客行程管理更简单。</p>
        </div>
        <div class="footer-links">
          <strong>快捷入口</strong>
          <router-link v-for="item in navItems" :key="item.path" :to="item.path">{{ item.label }}</router-link>
        </div>
        <div class="footer-links">
          <strong>游客服务</strong>
          <router-link to="/services">在线购票</router-link>
          <router-link to="/services">雪具租赁</router-link>
          <router-link to="/account">订单与反馈</router-link>
        </div>
        <div class="footer-contact">
          <strong>温馨提示</strong>
          <p>出行前请关注天气、雪况及雪场公告，合理选择适合自身水平的雪道。</p>
        </div>
      </div>
      <div class="page-container footer-bottom">
        <span>© 2026 黑龙江滑雪场管理系统</span>
        <span>毕业设计 · 冰雪旅游数字化服务平台</span>
      </div>
    </footer>

    <el-drawer v-model="mobileOpen" direction="rtl" size="min(82vw, 330px)" :with-header="false">
      <div class="mobile-panel">
        <SnowLogo />
        <nav aria-label="移动端导航">
          <router-link
            v-for="item in navItems"
            :key="item.path"
            :to="item.path"
            :class="{ active: item.path === '/' ? route.path === '/' : route.path.startsWith(item.path) }"
          >
            <span>{{ item.label }}</span><el-icon><ArrowRight /></el-icon>
          </router-link>
        </nav>
        <router-link v-if="auth.isUserLoggedIn" to="/account" class="mobile-account">进入个人中心</router-link>
        <router-link v-else to="/login" class="mobile-account">登录 / 注册</router-link>
      </div>
    </el-drawer>
  </div>
</template>

<style scoped>
.public-shell { min-height: 100vh; background: #f4f8fb; }
.site-header {
  position: sticky;
  top: 0;
  z-index: 40;
  border-bottom: 1px solid rgba(220, 232, 239, .68);
  background: rgba(255, 255, 255, .9);
  backdrop-filter: blur(18px);
  transition: box-shadow .25s ease, background .25s ease;
}
.site-header.scrolled { background: rgba(255,255,255,.96); box-shadow: 0 8px 28px rgba(12,54,82,.08); }
.header-inner { display: flex; height: 76px; align-items: center; justify-content: space-between; gap: 28px; }
.brand { display: inline-flex; }
.desktop-nav { display: flex; height: 100%; align-items: center; gap: 5px; }
.desktop-nav a {
  position: relative;
  display: inline-flex;
  height: 100%;
  align-items: center;
  padding: 0 18px;
  color: #4d687d;
  font-size: 15px;
  font-weight: 600;
}
.desktop-nav a::after {
  position: absolute;
  right: 18px;
  bottom: 0;
  left: 18px;
  height: 3px;
  border-radius: 4px 4px 0 0;
  background: #1479b8;
  content: '';
  opacity: 0;
  transform: scaleX(.35);
  transition: .22s ease;
}
.desktop-nav a:hover,
.desktop-nav a.active { color: #0d5f94; }
.desktop-nav a.active::after { opacity: 1; transform: scaleX(1); }
.header-actions { display: flex; align-items: center; gap: 8px; }
.account-link { display: flex; align-items: center; gap: 8px; color: #264a64; font-size: 14px; font-weight: 600; }
.avatar {
  display: grid;
  width: 34px;
  height: 34px;
  place-items: center;
  border-radius: 50%;
  background: linear-gradient(145deg, #1479b8, #27b9cf);
  color: #fff;
}
.logout-button { color: #718595; }
.menu-button { display: none; width: 38px; height: 38px; padding: 9px; border: 0; border-radius: 10px; background: #eaf5fa; cursor: pointer; }
.menu-button span { display: block; height: 2px; margin: 4px 0; border-radius: 2px; background: #145b85; }
.site-main { min-height: 65vh; }
.site-footer { padding: 58px 0 24px; background: #08233c; color: rgba(255,255,255,.74); }
.footer-grid { display: grid; grid-template-columns: 1.7fr .75fr .85fr 1.3fr; gap: 52px; }
.footer-brand p,
.footer-contact p { margin: 18px 0 0; font-size: 13px; line-height: 1.8; }
.footer-links { display: grid; align-content: start; gap: 13px; font-size: 13px; }
.footer-links strong,
.footer-contact strong { margin-bottom: 4px; color: #fff; font-size: 15px; }
.footer-links a:hover { color: #71d7e5; }
.footer-bottom { display: flex; justify-content: space-between; margin-top: 48px; padding-top: 20px; border-top: 1px solid rgba(255,255,255,.1); font-size: 12px; }
.mobile-panel { display: grid; gap: 34px; padding: 8px 4px; }
.mobile-panel nav { display: grid; }
.mobile-panel nav a { display: flex; align-items: center; justify-content: space-between; padding: 17px 4px; border-bottom: 1px solid #e5eef3; color: #486477; font-weight: 600; }
.mobile-panel nav a.active { color: #1479b8; }
.mobile-account { padding: 13px 18px; border-radius: 12px; background: #1479b8; color: #fff; text-align: center; font-weight: 700; }

@media (max-width: 960px) {
  .desktop-nav { display: none; }
  .menu-button { display: block; }
  .footer-grid { grid-template-columns: 1.3fr 1fr 1fr; }
  .footer-contact { display: none; }
}
@media (max-width: 640px) {
  .header-inner { height: 68px; }
  .header-actions > a:first-child .el-button { padding: 8px 13px; }
  .account-link span:last-child,
  .logout-button { display: none; }
  .footer-grid { grid-template-columns: 1fr 1fr; gap: 36px 24px; }
  .footer-brand { grid-column: 1 / -1; }
  .footer-bottom { flex-direction: column; gap: 8px; }
}
</style>
