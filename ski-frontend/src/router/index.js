import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const resourceRoutes = [
  { path: 'resorts', name: 'AdminResorts', resource: 'resort', title: '滑雪场管理' },
  { path: 'ski-runs', name: 'AdminSkiRuns', resource: 'skiRun', title: '雪道管理' },
  { path: 'facilities', name: 'AdminFacilities', resource: 'facility', title: '设施管理' },
  { path: 'tickets', name: 'AdminTickets', resource: 'ticket', title: '票种管理' },
  { path: 'equipment', name: 'AdminEquipment', resource: 'equipment', title: '雪具库存' },
  { path: 'courses', name: 'AdminCourses', resource: 'course', title: '课程管理' },
  { path: 'coaches', name: 'AdminCoaches', resource: 'coach', title: '教练管理' },
  { path: 'weather', name: 'AdminWeather', resource: 'weather', title: '天气雪况' },
  { path: 'announcements', name: 'AdminAnnouncements', resource: 'announcement', title: '公告管理' },
  { path: 'maintenance', name: 'AdminMaintenance', resource: 'maintenance', title: '维护记录' },
  { path: 'inspections', name: 'AdminInspections', resource: 'safetyInspection', title: '安全巡检' }
].map((item) => ({
  path: item.path,
  name: item.name,
  component: () => import('../views/admin/ResourceView.vue'),
  meta: { resource: item.resource, title: item.title, requiresAdmin: true }
}))

const routes = [
  {
    path: '/',
    component: () => import('../layouts/PublicLayout.vue'),
    children: [
      { path: '', name: 'Home', component: () => import('../views/public/HomeView.vue'), meta: { title: '首页' } },
      { path: 'resorts', name: 'Resorts', component: () => import('../views/public/ResortsView.vue'), meta: { title: '雪场导览' } },
      { path: 'services', name: 'Services', component: () => import('../views/public/ServicesView.vue'), meta: { title: '滑雪服务' } },
      { path: 'announcements', name: 'Announcements', component: () => import('../views/public/AnnouncementsView.vue'), meta: { title: '公告资讯' } },
      { path: 'login', name: 'UserLogin', component: () => import('../views/public/AuthView.vue'), meta: { title: '游客登录' } },
      { path: 'account', name: 'Account', component: () => import('../views/user/AccountView.vue'), meta: { title: '个人中心', requiresUser: true } }
    ]
  },
  {
    path: '/admin/login',
    name: 'AdminLogin',
    component: () => import('../views/admin/AdminLoginView.vue'),
    meta: { title: '管理端登录' }
  },
  {
    path: '/admin',
    component: () => import('../layouts/AdminLayout.vue'),
    redirect: '/admin/dashboard',
    meta: { requiresAdmin: true },
    children: [
      { path: 'dashboard', name: 'AdminDashboard', component: () => import('../views/admin/DashboardView.vue'), meta: { title: '数据看板', requiresAdmin: true } },
      ...resourceRoutes,
      { path: 'orders', name: 'AdminOrders', component: () => import('../views/admin/OrdersView.vue'), meta: { title: '订单管理', requiresAdmin: true } },
      { path: 'feedback', name: 'AdminFeedback', component: () => import('../views/admin/FeedbackView.vue'), meta: { title: '游客反馈', requiresAdmin: true } },
      { path: 'employees', name: 'AdminEmployees', component: () => import('../views/admin/EmployeesView.vue'), meta: { title: '员工管理', requiresAdmin: true } }
    ]
  },
  { path: '/:pathMatch(.*)*', name: 'NotFound', component: () => import('../views/NotFoundView.vue'), meta: { title: '页面未找到' } }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior: () => ({ top: 0 })
})

router.beforeEach((to) => {
  const auth = useAuthStore()
  document.title = `${to.meta.title || '首页'} - 黑龙江滑雪场管理系统`

  if (to.meta.requiresAdmin && !auth.isAdminLoggedIn) {
    return { path: '/admin/login', query: { redirect: to.fullPath } }
  }
  if (to.meta.requiresUser && !auth.isUserLoggedIn) {
    return { path: '/login', query: { redirect: to.fullPath } }
  }
  if (to.path === '/admin/login' && auth.isAdminLoggedIn) return '/admin/dashboard'
  return true
})

export default router

