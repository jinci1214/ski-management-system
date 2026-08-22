<script setup>
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { useAuthStore } from '../stores/auth'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const drawerVisible = ref(false)
const loggingOut = ref(false)

const adminName = computed(() => auth.admin?.name || auth.admin?.userName || '管理员')
const adminPosition = computed(() => auth.admin?.position || '系统管理员')
const pageTitle = computed(() => route.meta.title || '管理工作台')
const avatarText = computed(() => adminName.value.slice(0, 1))

const closeMobileMenu = () => {
  drawerVisible.value = false
}

const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定退出管理端吗？', '退出登录', {
      confirmButtonText: '确定退出',
      cancelButtonText: '继续工作',
      type: 'warning'
    })
  } catch {
    return
  }

  loggingOut.value = true
  try {
    await auth.logout('admin')
  } finally {
    loggingOut.value = false
    await router.replace('/admin/login')
  }
}
</script>

<template>
  <div class="admin-shell">
    <aside class="desktop-sidebar">
      <div class="brand">
        <div class="brand-mark">
          <el-icon><Sunny /></el-icon>
        </div>
        <div>
          <strong>龙江雪域</strong>
          <span>滑雪场管理系统</span>
        </div>
      </div>
      <el-scrollbar class="menu-scrollbar">
        <el-menu
          class="admin-menu"
          :default-active="route.path"
          router
          @select="closeMobileMenu"
        >
          <el-menu-item index="/admin/dashboard">
            <el-icon><DataAnalysis /></el-icon>
            <span>数据看板</span>
          </el-menu-item>

          <el-sub-menu index="venue">
            <template #title>
              <el-icon><Place /></el-icon>
              <span>雪场运营</span>
            </template>
            <el-menu-item index="/admin/resorts">滑雪场管理</el-menu-item>
            <el-menu-item index="/admin/ski-runs">雪道管理</el-menu-item>
            <el-menu-item index="/admin/facilities">设施管理</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="business">
            <template #title>
              <el-icon><ShoppingBag /></el-icon>
              <span>业务管理</span>
            </template>
            <el-menu-item index="/admin/tickets">票种管理</el-menu-item>
            <el-menu-item index="/admin/equipment">雪具库存</el-menu-item>
            <el-menu-item index="/admin/courses">课程管理</el-menu-item>
            <el-menu-item index="/admin/coaches">教练管理</el-menu-item>
            <el-menu-item index="/admin/orders">订单管理</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="service">
            <template #title>
              <el-icon><Service /></el-icon>
              <span>游客服务</span>
            </template>
            <el-menu-item index="/admin/weather">天气雪况</el-menu-item>
            <el-menu-item index="/admin/announcements">公告管理</el-menu-item>
            <el-menu-item index="/admin/feedback">游客反馈</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="safety">
            <template #title>
              <el-icon><WarnTriangleFilled /></el-icon>
              <span>安全保障</span>
            </template>
            <el-menu-item index="/admin/maintenance">维护记录</el-menu-item>
            <el-menu-item index="/admin/inspections">安全巡检</el-menu-item>
          </el-sub-menu>

          <el-menu-item index="/admin/employees">
            <el-icon><UserFilled /></el-icon>
            <span>员工管理</span>
          </el-menu-item>
        </el-menu>
      </el-scrollbar>
      <div class="sidebar-footer">
        <span class="status-dot"></span>
        系统服务运行正常
      </div>
    </aside>

    <el-drawer
      v-model="drawerVisible"
      class="mobile-drawer"
      direction="ltr"
      size="min(84vw, 300px)"
      :with-header="false"
    >
      <div class="brand drawer-brand">
        <div class="brand-mark"><el-icon><Sunny /></el-icon></div>
        <div><strong>龙江雪域</strong><span>管理工作台</span></div>
      </div>
      <el-scrollbar class="drawer-menu-scrollbar">
        <el-menu
          class="admin-menu drawer-menu"
          :default-active="route.path"
          router
          @select="closeMobileMenu"
        >
          <el-menu-item index="/admin/dashboard"><el-icon><DataAnalysis /></el-icon><span>数据看板</span></el-menu-item>
          <el-sub-menu index="mobile-venue">
            <template #title><el-icon><Place /></el-icon><span>雪场运营</span></template>
            <el-menu-item index="/admin/resorts">滑雪场管理</el-menu-item>
            <el-menu-item index="/admin/ski-runs">雪道管理</el-menu-item>
            <el-menu-item index="/admin/facilities">设施管理</el-menu-item>
          </el-sub-menu>
          <el-sub-menu index="mobile-business">
            <template #title><el-icon><ShoppingBag /></el-icon><span>业务管理</span></template>
            <el-menu-item index="/admin/tickets">票种管理</el-menu-item>
            <el-menu-item index="/admin/equipment">雪具库存</el-menu-item>
            <el-menu-item index="/admin/courses">课程管理</el-menu-item>
            <el-menu-item index="/admin/coaches">教练管理</el-menu-item>
            <el-menu-item index="/admin/orders">订单管理</el-menu-item>
          </el-sub-menu>
          <el-sub-menu index="mobile-service">
            <template #title><el-icon><Service /></el-icon><span>游客服务</span></template>
            <el-menu-item index="/admin/weather">天气雪况</el-menu-item>
            <el-menu-item index="/admin/announcements">公告管理</el-menu-item>
            <el-menu-item index="/admin/feedback">游客反馈</el-menu-item>
          </el-sub-menu>
          <el-sub-menu index="mobile-safety">
            <template #title><el-icon><WarnTriangleFilled /></el-icon><span>安全保障</span></template>
            <el-menu-item index="/admin/maintenance">维护记录</el-menu-item>
            <el-menu-item index="/admin/inspections">安全巡检</el-menu-item>
          </el-sub-menu>
          <el-menu-item index="/admin/employees"><el-icon><UserFilled /></el-icon><span>员工管理</span></el-menu-item>
        </el-menu>
      </el-scrollbar>
    </el-drawer>

    <section class="admin-body">
      <header class="topbar">
        <div class="topbar-left">
          <el-button class="menu-trigger" text circle @click="drawerVisible = true">
            <el-icon size="22"><Menu /></el-icon>
          </el-button>
          <div>
            <span class="eyebrow">黑龙江滑雪场管理系统</span>
            <h1>{{ pageTitle }}</h1>
          </div>
        </div>
        <el-dropdown trigger="click">
          <button class="profile-button" type="button">
            <el-avatar :size="38">{{ avatarText }}</el-avatar>
            <span class="profile-copy">
              <strong>{{ adminName }}</strong>
              <small>{{ adminPosition }}</small>
            </span>
            <el-icon><ArrowDown /></el-icon>
          </button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item disabled>{{ adminPosition }}</el-dropdown-item>
              <el-dropdown-item divided :disabled="loggingOut" @click="handleLogout">
                <el-icon><SwitchButton /></el-icon>退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </header>

      <main class="admin-main">
        <router-view v-slot="{ Component }">
          <transition name="fade-slide" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </main>
    </section>
  </div>
</template>

<style scoped>
.admin-shell {
  min-height: 100vh;
  background:
    radial-gradient(circle at 100% 0%, rgba(39, 185, 207, 0.09), transparent 30%),
    #f4f8fb;
}

.desktop-sidebar {
  position: fixed;
  inset: 0 auto 0 0;
  z-index: 20;
  display: flex;
  width: 248px;
  flex-direction: column;
  color: #fff;
  background: linear-gradient(180deg, #092641 0%, #0b3557 72%, #0c4266 100%);
  box-shadow: 12px 0 36px rgba(8, 41, 68, 0.14);
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
  height: 82px;
  padding: 0 22px;
}

.brand-mark {
  display: grid;
  width: 42px;
  height: 42px;
  flex: 0 0 auto;
  place-items: center;
  border: 1px solid rgba(255, 255, 255, 0.24);
  border-radius: 13px;
  color: #b9f3ff;
  background: rgba(255, 255, 255, 0.1);
}

.brand strong,
.brand span {
  display: block;
}

.brand strong {
  font-size: 17px;
  letter-spacing: 1px;
}

.brand span {
  margin-top: 3px;
  color: #9dc5dc;
  font-size: 11px;
}

.menu-scrollbar {
  flex: 1;
}

.admin-menu {
  border-right: 0;
  background: transparent;
  --el-menu-text-color: #bed4e2;
  --el-menu-hover-text-color: #fff;
  --el-menu-bg-color: transparent;
  --el-menu-hover-bg-color: rgba(255, 255, 255, 0.08);
  --el-menu-active-color: #fff;
}

.admin-menu :deep(.el-menu-item),
.admin-menu :deep(.el-sub-menu__title) {
  height: 48px;
  margin: 3px 12px;
  border-radius: 10px;
  line-height: 48px;
}

.admin-menu :deep(.el-menu-item.is-active) {
  background: linear-gradient(90deg, #1479b8, #1c91bf);
  box-shadow: 0 8px 18px rgba(4, 29, 50, 0.22);
}

.admin-menu :deep(.el-sub-menu .el-menu-item) {
  min-width: 0;
  padding-left: 52px !important;
  font-size: 13px;
}

.sidebar-footer {
  margin: 12px 16px 18px;
  padding: 12px 14px;
  border: 1px solid rgba(255, 255, 255, 0.09);
  border-radius: 10px;
  color: #9ec2d7;
  background: rgba(255, 255, 255, 0.05);
  font-size: 12px;
}

.status-dot {
  display: inline-block;
  width: 7px;
  height: 7px;
  margin-right: 7px;
  border-radius: 50%;
  background: #60d49c;
  box-shadow: 0 0 0 4px rgba(96, 212, 156, 0.12);
}

.admin-body {
  min-width: 0;
  margin-left: 248px;
}

.topbar {
  position: sticky;
  top: 0;
  z-index: 15;
  display: flex;
  height: 82px;
  align-items: center;
  justify-content: space-between;
  padding: 0 32px;
  border-bottom: 1px solid rgba(220, 232, 239, 0.88);
  background: rgba(255, 255, 255, 0.88);
  backdrop-filter: blur(16px);
}

.topbar-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.eyebrow {
  display: block;
  margin-bottom: 3px;
  color: #7990a1;
  font-size: 11px;
  letter-spacing: 0.8px;
}

.topbar h1 {
  margin: 0;
  color: #102f49;
  font-size: 21px;
}

.menu-trigger {
  display: none;
}

.profile-button {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 7px 10px 7px 7px;
  border: 0;
  border-radius: 13px;
  color: #244158;
  background: transparent;
  cursor: pointer;
}

.profile-button:hover {
  background: #eef6fa;
}

.profile-button :deep(.el-avatar) {
  color: #fff;
  background: linear-gradient(135deg, #1479b8, #28b8cf);
}

.profile-copy {
  min-width: 74px;
  text-align: left;
}

.profile-copy strong,
.profile-copy small {
  display: block;
}

.profile-copy strong {
  font-size: 13px;
}

.profile-copy small {
  margin-top: 2px;
  color: #8193a1;
  font-size: 11px;
}

.admin-main {
  min-height: calc(100vh - 82px);
  padding: 26px 30px 40px;
}

.drawer-brand {
  color: #fff;
  background: #0b2c49;
}

.drawer-menu-scrollbar {
  height: calc(100vh - 82px);
  background: #0b2c49;
}

.mobile-drawer :deep(.el-drawer__body) {
  padding: 0;
  background: #0b2c49;
}

@media (max-width: 960px) {
  .desktop-sidebar {
    display: none;
  }

  .admin-body {
    margin-left: 0;
  }

  .menu-trigger {
    display: inline-flex;
  }

  .topbar {
    padding: 0 20px;
  }

  .admin-main {
    padding: 22px 20px 34px;
  }
}

@media (max-width: 560px) {
  .topbar {
    height: 70px;
    padding: 0 14px;
  }

  .eyebrow,
  .profile-copy {
    display: none;
  }

  .topbar h1 {
    font-size: 18px;
  }

  .profile-button {
    gap: 5px;
  }

  .admin-main {
    min-height: calc(100vh - 70px);
    padding: 16px 12px 28px;
  }
}
</style>
