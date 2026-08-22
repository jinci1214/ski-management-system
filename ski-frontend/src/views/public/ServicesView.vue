<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { getCoaches, getCourses, getEquipment, getResorts, getSkiRuns, getTickets, getWeather } from '../../api/public'
import { buyTicket, buyTicketWithEquipment, joinCourse, payOrders, rentEquipment } from '../../api/user'
import PageBanner from '../../components/PageBanner.vue'
import { useAuthStore } from '../../stores/auth'
import { paymentMethods } from '../../utils/dictionaries'
import { formatDate, formatMoney } from '../../utils/format'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const loading = ref(true)
const activeTab = ref(['tickets', 'equipment', 'courses', 'coaches'].includes(route.query.tab) ? route.query.tab : 'tickets')
const keyword = ref('')
const extraFilter = ref('')
const actionLoading = ref('')
const tickets = ref([])
const equipment = ref([])
const courses = ref([])
const coaches = ref([])
const resorts = ref([])
const skiRuns = ref([])
const weather = ref([])
const failedTicketImages = ref(new Set())
const ticketDetailOpen = ref(false)
const selectedTicket = ref(null)
const selectedEquipmentIds = ref([])
const courseDetailOpen = ref(false)
const selectedCourse = ref(null)
const paymentOpen = ref(false)
const paymentMethod = ref('微信')
const pendingBundleOrders = ref([])
const pendingPaymentDescription = ref('')

const tabs = computed(() => [
  { value: 'tickets', label: '雪票购买', icon: '🎫', count: tickets.value.length },
  { value: 'equipment', label: '雪具租赁', icon: '🎿', count: equipment.value.length },
  { value: 'courses', label: '滑雪课程', icon: '🏂', count: courses.value.length },
  { value: 'coaches', label: '教练团队', icon: '🏅', count: coaches.value.length }
])
const currentItems = computed(() => ({ tickets: tickets.value, equipment: equipment.value, courses: courses.value, coaches: coaches.value }[activeTab.value] || []))
const selectedCoachId = computed(() => activeTab.value === 'courses' ? Number(route.query.coachId) || null : null)
const filterOptions = computed(() => {
  const field = activeTab.value === 'courses' ? 'level' : activeTab.value === 'coaches' ? 'level' : activeTab.value === 'equipment' ? 'resortId' : 'status'
  if (field === 'resortId') return resorts.value.map((item) => ({ label: item.name, value: String(item.id) }))
  if (field === 'status') return [{ label: '可购买', value: '1' }, { label: '已停用', value: '0' }]
  return [...new Set(currentItems.value.map((item) => item[field]).filter(Boolean))].map((value) => ({ label: value, value: String(value) }))
})
const filterPlaceholder = computed(() => activeTab.value === 'equipment' ? '全部雪场' : activeTab.value === 'tickets' ? '全部状态' : '全部等级')
const filteredItems = computed(() => {
  const key = keyword.value.trim().toLowerCase()
  return currentItems.value.filter((item) => {
    const text = [item.name, item.title, item.description, item.content, item.teacher, item.introduction, item.level].join(' ').toLowerCase()
    const matchKey = !key || text.includes(key)
    let matchFilter = true
    if (extraFilter.value) {
      if (activeTab.value === 'equipment') matchFilter = String(item.resortId) === extraFilter.value
      else if (activeTab.value === 'tickets') matchFilter = String(item.status) === extraFilter.value
      else matchFilter = String(item.level) === extraFilter.value
    }
    if (activeTab.value === 'courses' && selectedCoachId.value) {
      matchFilter = matchFilter && Number(item.coachId) === selectedCoachId.value && Number(item.number) > 0
    }
    return matchKey && matchFilter
  })
})

const resortName = (id) => resorts.value.find((item) => Number(item.id) === Number(id))?.name || '所属雪场待确认'
const findRun = (id) => skiRuns.value.find((item) => Number(item.id) === Number(id)) || null
const runName = (id) => findRun(id)?.name || '适用雪道待确认'
const ticketRunImage = (skiRunId) => {
  const image = skiRuns.value.find((item) => Number(item.id) === Number(skiRunId))?.image?.trim()
  return image && !failedTicketImages.value.has(String(skiRunId)) ? image : ''
}
const hideFailedTicketImage = (skiRunId) => {
  failedTicketImages.value = new Set([...failedTicketImages.value, String(skiRunId)])
}
const selectedRun = computed(() => findRun(selectedTicket.value?.skiRunId))
const selectedResort = computed(() => resorts.value.find((item) => Number(item.id) === Number(selectedRun.value?.resortId)) || null)
const selectedWeather = computed(() => weather.value
  .filter((item) => Number(item.resortId) === Number(selectedResort.value?.id))
  .sort((a, b) => String(b.date || '').localeCompare(String(a.date || '')))
  .slice(0, 3))
const relatedEquipment = computed(() => equipment.value.filter((item) => Number(item.resortId) === Number(selectedResort.value?.id)))
const recommendedCourses = computed(() => courses.value.filter((item) => Number(item.skiRunId) === Number(selectedTicket.value?.skiRunId) && Number(item.number) > 0))
const selectedEquipment = computed(() => relatedEquipment.value.filter((item) => selectedEquipmentIds.value.includes(Number(item.id))))
const ticketBundleAmount = computed(() => Number(selectedTicket.value?.price || 0) + selectedEquipment.value.reduce((sum, item) => sum + Number(item.price || 0), 0))
const ticketBundleDescription = computed(() => [selectedTicket.value?.name, ...selectedEquipment.value.map((item) => item.name)].filter(Boolean).join(' + '))
const pendingPaymentAmount = computed(() => pendingBundleOrders.value.reduce((sum, item) => sum + Number(item.amount || 0), 0))
const selectedCourseRun = computed(() => findRun(selectedCourse.value?.skiRunId))
const selectedCourseResort = computed(() => resorts.value.find((item) => Number(item.id) === Number(selectedCourseRun.value?.resortId)) || null)
const canOrder = (type, item) => {
  if (type === 'tickets') return Number(item.status) === 1
  if (type === 'equipment') return Number(item.remaining) > 0
  if (type === 'courses') return Number(item.number) > 0
  return false
}

const loadData = async () => {
  loading.value = true
  const results = await Promise.allSettled([getTickets(), getEquipment(), getCourses(), getCoaches(), getResorts(), getSkiRuns(), getWeather()])
  const targets = [tickets, equipment, courses, coaches, resorts, skiRuns, weather]
  results.forEach((result, index) => {
    targets[index].value = result.status === 'fulfilled' && Array.isArray(result.value) ? result.value : []
  })
  loading.value = false
}

const loginBeforeOrder = async () => {
  try {
    await ElMessageBox.confirm('登录游客账号后才能创建订单，是否现在登录？', '请先登录', {
      confirmButtonText: '去登录', cancelButtonText: '稍后再说', type: 'info'
    })
    await router.push({ path: '/login', query: { redirect: route.fullPath } })
  } catch { /* 用户取消 */ }
}

const openTicketDetail = (ticket) => {
  selectedTicket.value = ticket
  selectedEquipmentIds.value = []
  ticketDetailOpen.value = true
}

const openCourseDetail = (course) => {
  selectedCourse.value = course
  courseDetailOpen.value = true
}

const viewRecommendedCourse = async (course) => {
  ticketDetailOpen.value = false
  await router.push({ path: '/services', query: { ...route.query, tab: 'courses', courseId: String(course.id) } })
  openCourseDetail(course)
}

const openCourseFromRoute = () => {
  const courseId = Number(route.query.courseId)
  if (route.query.tab !== 'courses' || !courseId) return
  const course = courses.value.find((item) => Number(item.id) === courseId)
  if (course) openCourseDetail(course)
}

const coachAvailableCourseCount = (coach) => courses.value.filter((course) => Number(course.coachId) === Number(coach.id) && Number(course.number) > 0).length

const viewCoachCourses = async (coach) => {
  const count = coachAvailableCourseCount(coach)
  if (Number(coach.status) !== 1 || count === 0) {
    ElMessage.info(`${coach.name}当前暂无可报名课程`)
    return
  }
  await router.push({ path: '/services', query: { tab: 'courses', coachId: String(coach.id) } })
}

const selectServiceTab = async (tab) => {
  if (tab === 'courses' && (route.query.coachId || route.query.courseId)) {
    courseDetailOpen.value = false
    await router.push({ path: '/services', query: { tab: 'courses' } })
    return
  }
  activeTab.value = tab
}

const toggleEquipment = (item) => {
  const id = Number(item.id)
  selectedEquipmentIds.value = selectedEquipmentIds.value.includes(id)
    ? selectedEquipmentIds.value.filter((value) => value !== id)
    : [...selectedEquipmentIds.value, id]
}

const purchaseTicketBundle = async () => {
  if (!selectedTicket.value) return
  if (!auth.isUserLoggedIn) return loginBeforeOrder()
  const description = ticketBundleDescription.value || selectedTicket.value.name
  try {
    await ElMessageBox.confirm(`确认购买“${description}”吗？系统将创建雪票和雪具租赁订单。`, '确认购买', {
      confirmButtonText: '确认下单', cancelButtonText: '再看看', type: 'info'
    })
  } catch { return }

  actionLoading.value = `bundle-${selectedTicket.value.id}`
  try {
    const orders = await buyTicketWithEquipment({
      ticketId: selectedTicket.value.id,
      equipmentIds: selectedEquipmentIds.value
    })
    selectedEquipment.value.forEach((item) => { item.remaining = Math.max(0, Number(item.remaining) - 1) })
    pendingBundleOrders.value = Array.isArray(orders) ? orders : [orders]
    pendingPaymentDescription.value = description
    paymentMethod.value = '微信'
    paymentOpen.value = true
    ElMessage.success('订单已创建，请选择支付方式完成支付')
  } finally { actionLoading.value = '' }
}

const submitBundlePayment = async () => {
  if (!pendingBundleOrders.value.length) return
  actionLoading.value = 'bundle-payment'
  try {
    await payOrders(pendingBundleOrders.value.map((item) => item.id), paymentMethod.value)
    paymentOpen.value = false
    ticketDetailOpen.value = false
    courseDetailOpen.value = false
    selectedEquipmentIds.value = []
    ElMessage.success('支付成功，订单已完成支付')
    ElMessageBox.confirm('订单已支付，可前往个人中心查看订单。', '支付成功', {
      confirmButtonText: '查看我的订单', cancelButtonText: '继续选购', type: 'success'
    }).then(() => router.push('/account')).catch(() => {})
  } finally { actionLoading.value = '' }
}

const purchaseCourseFromDetail = async () => {
  if (!selectedCourse.value || !canOrder('courses', selectedCourse.value)) return
  if (!auth.isUserLoggedIn) return loginBeforeOrder()
  try {
    await ElMessageBox.confirm(`确认报名“${selectedCourse.value.title}”吗？系统将创建一笔待支付课程订单。`, '确认报名', {
      confirmButtonText: '确认报名', cancelButtonText: '再看看', type: 'info'
    })
  } catch { return }

  actionLoading.value = `course-detail-${selectedCourse.value.id}`
  try {
    const order = await joinCourse(selectedCourse.value.id)
    selectedCourse.value.number = Math.max(0, Number(selectedCourse.value.number) - 1)
    pendingBundleOrders.value = [order]
    pendingPaymentDescription.value = selectedCourse.value.title
    paymentMethod.value = '微信'
    paymentOpen.value = true
    ElMessage.success('课程订单已创建，请选择支付方式完成支付')
  } finally { actionLoading.value = '' }
}

const createOrder = async (type, item) => {
  if (!auth.isUserLoggedIn) return loginBeforeOrder()
  if (!canOrder(type, item)) return
  const config = {
    tickets: { label: '购买', noun: '雪票', api: buyTicket, name: item.name },
    equipment: { label: '租赁', noun: '雪具', api: rentEquipment, name: item.name },
    courses: { label: '报名', noun: '课程', api: joinCourse, name: item.title }
  }[type]
  try {
    await ElMessageBox.confirm(`确认${config.label}“${config.name}”吗？系统将创建一笔待支付订单。`, `确认${config.label}`, {
      confirmButtonText: `确认${config.label}`, cancelButtonText: '再看看', type: 'info'
    })
  } catch { return }

  actionLoading.value = `${type}-${item.id}`
  try {
    const order = await config.api(item.id)
    ElMessage.success(`${config.noun}订单创建成功`)
    if (type === 'tickets') ticketDetailOpen.value = false
    if (type === 'equipment') item.remaining = Math.max(0, Number(item.remaining) - 1)
    if (type === 'courses') item.number = Math.max(0, Number(item.number) - 1)
    ElMessageBox.confirm(`订单号：${order?.orderId || '已生成'}。可前往个人中心完成支付。`, '下单成功', {
      confirmButtonText: '查看我的订单', cancelButtonText: '继续选购', type: 'success'
    }).then(() => router.push('/account')).catch(() => {})
  } finally {
    actionLoading.value = ''
  }
}

watch(activeTab, (value) => {
  keyword.value = ''
  extraFilter.value = ''
  router.replace({ query: { ...route.query, tab: value } })
})
watch(() => route.query.tab, (value) => {
  if (['tickets', 'equipment', 'courses', 'coaches'].includes(value) && value !== activeTab.value) activeTab.value = value
})
watch(() => route.query.courseId, openCourseFromRoute)
onMounted(async () => {
  await loadData()
  openCourseFromRoute()
})
</script>

<template>
  <div>
    <PageBanner title="一站式滑雪服务" description="在线浏览雪票、雪具库存和教学课程，登录后即可创建订单，并在个人中心完成支付、取消或雪具归还。" icon="🎿">
      <template #actions><router-link :to="auth.isUserLoggedIn ? '/account' : '/login'"><el-button size="large" round color="#ffffff">{{ auth.isUserLoggedIn ? '查看我的订单' : '登录后预订' }}</el-button></router-link></template>
    </PageBanner>

    <section class="section-space service-page">
      <div class="page-container">
        <div class="service-tabs" role="tablist" aria-label="滑雪服务分类">
          <button v-for="tab in tabs" :key="tab.value" type="button" :class="{ active: activeTab === tab.value }" @click="selectServiceTab(tab.value)">
            <span>{{ tab.icon }}</span><div><strong>{{ tab.label }}</strong><small>{{ tab.count }} 项服务</small></div>
          </button>
        </div>

        <div class="toolbar">
          <div><h2>{{ tabs.find((item) => item.value === activeTab)?.label }}</h2><p>{{ selectedCoachId ? `正在查看 ${coaches.find((item) => Number(item.id) === selectedCoachId)?.name || '该教练'} 的可报名课程` : '以下信息由管理端实时维护，请以下单时状态为准' }}</p></div>
          <div class="filters"><el-input v-model="keyword" clearable placeholder="搜索名称或介绍" :prefix-icon="Search" /><el-select v-model="extraFilter" clearable :placeholder="filterPlaceholder"><el-option v-for="item in filterOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></div>
        </div>

        <div v-if="loading" class="item-grid"><el-card v-for="n in 6" :key="n" shadow="never"><el-skeleton :rows="5" animated /></el-card></div>

        <div v-else-if="filteredItems.length" class="item-grid">
          <article v-for="item in filteredItems" :key="`${activeTab}-${item.id}`" class="service-item" :class="{ 'ticket-card': activeTab === 'tickets', 'course-card': activeTab === 'courses' }" :tabindex="['tickets', 'courses'].includes(activeTab) ? 0 : undefined" @click="activeTab === 'tickets' ? openTicketDetail(item) : activeTab === 'courses' && openCourseDetail(item)" @keydown.enter="activeTab === 'tickets' ? openTicketDetail(item) : activeTab === 'courses' && openCourseDetail(item)">
            <div class="item-visual" :class="[`visual-${activeTab}`, { 'has-run-image': activeTab === 'tickets' && ticketRunImage(item.skiRunId) }]">
              <img v-if="activeTab === 'tickets' && ticketRunImage(item.skiRunId)" class="ticket-run-image" :src="ticketRunImage(item.skiRunId)" :alt="`${runName(item.skiRunId)}雪道照片`" @error="hideFailedTicketImage(item.skiRunId)">
              <template v-if="activeTab !== 'tickets'">
                <span class="visual-icon">{{ activeTab === 'equipment' ? '🎿' : activeTab === 'courses' ? '🏂' : '🏅' }}</span>
                <span class="visual-code">{{ String(item.id).padStart(2, '0') }}</span>
                <small>{{ activeTab === 'equipment' ? 'EQUIPMENT' : activeTab === 'courses' ? 'LESSON' : 'INSTRUCTOR' }}</small>
              </template>
            </div>

            <div v-if="activeTab === 'tickets'" class="item-content">
              <div class="item-heading"><el-tag :type="Number(item.status) === 1 ? 'success' : 'info'" effect="light" round>{{ Number(item.status) === 1 ? '可购买' : '已停用' }}</el-tag><strong class="price">{{ formatMoney(item.price) }}</strong></div>
              <h3>{{ item.name }}</h3><p class="minor"><el-icon><Flag /></el-icon>{{ runName(item.skiRunId) }}</p>
              <p class="description">{{ item.description || '雪票使用说明请以入场核验时的雪场规定为准。' }}</p>
              <div class="facts"><span><b>{{ item.hour || '—' }}</b> 小时有效</span><span>电子订单</span></div>
              <el-button type="primary" size="large" :disabled="!canOrder('tickets', item)" @click.stop="openTicketDetail(item)">{{ Number(item.status) === 1 ? '立即购买' : '暂不可购买' }}</el-button>
            </div>

            <div v-else-if="activeTab === 'equipment'" class="item-content">
              <div class="item-heading"><el-tag :type="Number(item.remaining) > 0 ? 'success' : 'danger'" effect="light" round>剩余 {{ item.remaining ?? 0 }} 件</el-tag><strong class="price">{{ formatMoney(item.price) }}<small>/次</small></strong></div>
              <h3>{{ item.name }}</h3><p class="minor"><el-icon><Location /></el-icon>{{ resortName(item.resortId) }}</p>
              <p class="description">现场领取和归还雪具，具体规格请到店后由工作人员协助选择。</p>
              <div class="facts"><span>总库存 <b>{{ item.total ?? '—' }}</b></span><span v-if="item.deposit != null">押金 {{ formatMoney(item.deposit) }}</span></div>
              <el-button type="primary" size="large" :disabled="!canOrder('equipment', item)" :loading="actionLoading === `equipment-${item.id}`" @click="createOrder('equipment', item)">{{ Number(item.remaining) > 0 ? '立即租赁' : '当前已租完' }}</el-button>
            </div>

            <div v-else-if="activeTab === 'courses'" class="item-content">
              <div class="item-heading"><el-tag type="primary" effect="light" round>{{ item.level || '通用课程' }}</el-tag><strong class="price">{{ formatMoney(item.price) }}</strong></div>
              <h3>{{ item.title }}</h3><p class="minor"><el-icon><User /></el-icon>{{ item.teacher || '教练待安排' }} · {{ runName(item.skiRunId) }}</p>
              <p class="description">{{ item.content || '由专业教练带领，课程内容将根据学员水平进行安排。' }}</p>
              <div class="facts"><span>{{ formatDate(item.courseTime, 'MM-DD HH:mm') }}</span><span><b>{{ item.hours || '—' }}</b> 小时 · 剩余 {{ item.number ?? 0 }} 人</span></div>
              <el-button type="primary" size="large" :disabled="!canOrder('courses', item)" @click.stop="openCourseDetail(item)">{{ Number(item.number) > 0 ? '立即报名' : '课程已满' }}</el-button>
            </div>

            <div v-else class="item-content coach-content">
              <div class="item-heading"><el-tag :type="Number(item.status) === 1 ? 'success' : 'info'" effect="light" round>{{ Number(item.status) === 1 ? '可授课' : '暂停授课' }}</el-tag><span>{{ item.gender || '教练' }}</span></div>
              <h3>{{ item.name }}</h3><p class="minor"><el-icon><Medal /></el-icon>{{ item.level || '专业教练' }} · {{ item.yearsOfExperience ?? 0 }} 年经验</p>
              <p class="description">{{ item.introduction || '持证滑雪教练，注重安全规范与动作基础，适合分阶段学习。' }}</p>
              <div class="certificate"><span>资质证书</span><b>{{ item.certificate || '已通过雪场资质审核' }}</b></div>
              <el-button size="large" plain :disabled="Number(item.status) !== 1 || coachAvailableCourseCount(item) === 0" @click.stop="viewCoachCourses(item)">{{ coachAvailableCourseCount(item) ? '查看可报名课程' : '暂无可报名课程' }}</el-button>
            </div>
          </article>
        </div>
        <el-empty v-else description="当前分类暂无符合条件的服务"><el-button @click="keyword = ''; extraFilter = ''">清除筛选</el-button></el-empty>

        <div class="order-guide">
          <div><span>1</span><strong>选择服务</strong><small>浏览价格、库存与时间</small></div><i /><div><span>2</span><strong>登录下单</strong><small>生成待支付订单</small></div><i /><div><span>3</span><strong>完成支付</strong><small>个人中心选择支付方式</small></div><i /><div><span>4</span><strong>到场核验</strong><small>按雪场规定使用服务</small></div>
        </div>
      </div>
    </section>

    <el-drawer v-model="ticketDetailOpen" size="min(620px, 94vw)" title="雪票购买详情" destroy-on-close>
      <div v-if="selectedTicket" class="ticket-detail">
        <div class="ticket-detail-hero">
          <img v-if="ticketRunImage(selectedTicket.skiRunId)" :src="ticketRunImage(selectedTicket.skiRunId)" :alt="`${runName(selectedTicket.skiRunId)}雪道照片`" @error="hideFailedTicketImage(selectedTicket.skiRunId)">
          <div class="ticket-hero-shade" />
          <div class="ticket-hero-content"><span>SKI PASS · {{ selectedTicket.hour || '—' }} 小时</span><h2>{{ selectedTicket.name }}</h2><p><el-icon><Flag /></el-icon>{{ runName(selectedTicket.skiRunId) }}</p></div>
          <strong>{{ formatMoney(selectedTicket.price) }}</strong>
        </div>

        <section class="ticket-info-section">
          <div class="ticket-section-title"><h3>对应雪场</h3><span>RESORT</span></div>
          <div v-if="selectedResort" class="resort-info"><div><strong>{{ selectedResort.name }}</strong><p><el-icon><Location /></el-icon>{{ selectedResort.city }} · {{ selectedResort.address }}</p><p><el-icon><Phone /></el-icon>{{ selectedResort.phone || '联系电话待公布' }}</p></div><el-tag :type="Number(selectedResort.status) === 1 ? 'success' : 'info'" round>{{ Number(selectedResort.status) === 1 ? '营业中' : '暂停营业' }}</el-tag></div>
          <el-empty v-else :image-size="55" description="暂未关联雪场信息" />
        </section>

        <section class="ticket-info-section">
          <div class="ticket-section-title"><h3>对应雪道</h3><span>SKI RUN</span></div>
          <div v-if="selectedRun" class="run-info"><el-image v-if="ticketRunImage(selectedTicket.skiRunId)" :src="ticketRunImage(selectedTicket.skiRunId)" :preview-src-list="[ticketRunImage(selectedTicket.skiRunId)]" preview-teleported fit="cover" /><div><el-tag :type="selectedRun.level === '初级' ? 'success' : selectedRun.level === '中级' ? 'primary' : selectedRun.level === '高级' ? 'warning' : 'danger'" effect="dark" round>{{ selectedRun.level || '未分级' }}</el-tag><strong>{{ selectedRun.name }}</strong><p>长度 {{ selectedRun.length || '—' }} 米 · 落差 {{ selectedRun.height || '—' }} 米</p></div></div>
          <el-empty v-else :image-size="55" description="暂未关联雪道信息" />
        </section>

        <section class="ticket-info-section">
          <div class="ticket-section-title"><h3>近期天气与雪况</h3><span>WEATHER</span></div>
          <div v-if="selectedWeather.length" class="ticket-weather"><div v-for="item in selectedWeather" :key="item.id"><time>{{ formatDate(item.date, 'MM/DD') }}</time><b>{{ item.weather || '—' }}</b><span>{{ item.temperatureMin ?? '—' }}° ~ {{ item.temperatureMax ?? '—' }}°</span><small>{{ item.snow || '雪况待更新' }} · 风速 {{ item.wind ?? '—' }}m/s</small></div></div>
          <el-empty v-else :image-size="55" description="暂无近期天气雪况" />
        </section>

        <section class="ticket-info-section">
          <div class="ticket-section-title"><h3>可租用雪具</h3><span>{{ relatedEquipment.length }} 项</span></div>
          <div v-if="relatedEquipment.length" class="related-list"><div v-for="item in relatedEquipment.slice(0, 4)" :key="item.id"><span>🎿</span><p><strong>{{ item.name }}</strong><small>剩余 {{ item.remaining ?? 0 }} 件 · {{ formatMoney(item.price) }}/次</small></p><el-button type="primary" :plain="!selectedEquipmentIds.includes(Number(item.id))" size="small" :disabled="Number(item.remaining) <= 0" @click="toggleEquipment(item)">{{ Number(item.remaining) <= 0 ? '已租完' : selectedEquipmentIds.includes(Number(item.id)) ? '已加购' : '加购' }}</el-button></div></div>
          <el-empty v-else :image-size="55" description="该雪场暂无雪具信息" />
        </section>

        <section class="ticket-info-section">
          <div class="ticket-section-title"><h3>推荐课程</h3><span>{{ recommendedCourses.length }} 门可报名</span></div>
          <div v-if="recommendedCourses.length" class="related-list"><div v-for="item in recommendedCourses.slice(0, 3)" :key="item.id"><span>🏂</span><p><strong>{{ item.title }}</strong><small>{{ item.teacher || '教练待安排' }} · {{ formatDate(item.courseTime, 'MM-DD HH:mm') }} · 剩余 {{ item.number }} 人</small></p><el-button text type="primary" size="small" @click="viewRecommendedCourse(item)">查看</el-button></div></div>
          <el-empty v-else :image-size="55" description="该雪道暂无可报名课程" />
        </section>

        <div class="ticket-buy-bar"><div><span>应付金额</span><small class="bundle-description">{{ ticketBundleDescription }} 共计：</small><strong>{{ formatMoney(ticketBundleAmount) }}</strong></div><el-button type="primary" size="large" :loading="actionLoading === `bundle-${selectedTicket.id}`" @click="purchaseTicketBundle">确认购买</el-button></div>
      </div>
    </el-drawer>

    <el-drawer v-model="courseDetailOpen" size="min(620px, 94vw)" title="滑雪课程详情" destroy-on-close>
      <div v-if="selectedCourse" class="course-detail">
        <div class="course-detail-hero"><div><span>SKI LESSON · {{ selectedCourse.level || '通用课程' }}</span><h2>{{ selectedCourse.title }}</h2><p>课程编号：{{ selectedCourse.id }}</p></div><strong>{{ formatMoney(selectedCourse.price) }}</strong></div>

        <section class="ticket-info-section">
          <div class="ticket-section-title"><h3>课程基本信息</h3><span>COURSE INFO</span></div>
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="课程名称" :span="2">{{ selectedCourse.title }}</el-descriptions-item>
            <el-descriptions-item label="难度等级">{{ selectedCourse.level || '未设置' }}</el-descriptions-item>
            <el-descriptions-item label="课程价格">{{ formatMoney(selectedCourse.price) }}</el-descriptions-item>
            <el-descriptions-item label="教学时间">{{ formatDate(selectedCourse.courseTime, 'YYYY-MM-DD HH:mm') }}</el-descriptions-item>
            <el-descriptions-item label="课程时长">{{ selectedCourse.hours || '—' }} 小时</el-descriptions-item>
            <el-descriptions-item label="剩余名额">{{ selectedCourse.number ?? 0 }} 人</el-descriptions-item>
            <el-descriptions-item label="教练姓名">{{ selectedCourse.teacher || '待安排' }}</el-descriptions-item>
            <el-descriptions-item label="教练编号">{{ selectedCourse.coachId || '未关联' }}</el-descriptions-item>
            <el-descriptions-item label="雪道编号">{{ selectedCourse.skiRunId || '未关联' }}</el-descriptions-item>
          </el-descriptions>
        </section>

        <section class="ticket-info-section">
          <div class="ticket-section-title"><h3>教学场地</h3><span>SKI RUN</span></div>
          <div v-if="selectedCourseRun" class="resort-info"><div><strong>{{ selectedCourseRun.name }}</strong><p>雪道等级：{{ selectedCourseRun.level || '未分级' }} · 长度 {{ selectedCourseRun.length || '—' }} 米 · 落差 {{ selectedCourseRun.height || '—' }} 米</p><p v-if="selectedCourseResort"><el-icon><Location /></el-icon>{{ selectedCourseResort.name }} · {{ selectedCourseResort.address }}</p></div></div>
          <el-empty v-else :image-size="55" description="暂未关联教学雪道" />
        </section>

        <section class="ticket-info-section">
          <div class="ticket-section-title"><h3>教学内容</h3><span>TEACHING CONTENT</span></div>
          <div class="course-content">{{ selectedCourse.content || '暂未填写教学内容。' }}</div>
        </section>

        <div class="ticket-buy-bar"><div><span>课程应付金额</span><small class="bundle-description">{{ selectedCourse.title }}</small><strong>{{ formatMoney(selectedCourse.price) }}</strong></div><el-button type="primary" size="large" :disabled="!canOrder('courses', selectedCourse)" :loading="actionLoading === `course-detail-${selectedCourse.id}`" @click="purchaseCourseFromDetail">{{ Number(selectedCourse.number) > 0 ? '立即报名' : '课程已满' }}</el-button></div>
      </div>
    </el-drawer>

    <el-dialog v-model="paymentOpen" title="支付订单" width="min(440px, 92vw)" destroy-on-close>
      <div v-if="pendingBundleOrders.length" class="bundle-payment-dialog">
        <div class="bundle-payment-summary"><span>🎫</span><p><strong>{{ pendingPaymentDescription }}</strong><small>应付金额 {{ formatMoney(pendingPaymentAmount) }}</small></p></div>
        <label>选择支付方式</label>
        <el-radio-group v-model="paymentMethod" class="bundle-payment-methods"><el-radio-button v-for="item in paymentMethods" :key="item" :value="item">{{ item }}</el-radio-button></el-radio-group>
        <p class="bundle-payment-note">毕业设计演示环境仅更新订单支付状态，不会发起真实扣款。</p>
      </div>
      <template #footer><el-button @click="paymentOpen = false">取消</el-button><el-button type="primary" :loading="actionLoading === 'bundle-payment'" @click="submitBundlePayment">确认支付 {{ formatMoney(pendingPaymentAmount) }}</el-button></template>
    </el-dialog>
  </div>
</template>

<style scoped>
.service-page{background:linear-gradient(180deg,#f4f8fb,#fff)}.service-tabs{position:relative;z-index:3;display:grid;grid-template-columns:repeat(4,1fr);gap:12px;margin-top:-106px;margin-bottom:58px}.service-tabs button{display:flex;align-items:center;gap:13px;padding:19px;border:1px solid rgba(220,232,239,.9);border-radius:16px;background:rgba(255,255,255,.96);box-shadow:0 10px 30px rgba(13,60,91,.1);color:#5f788a;text-align:left;cursor:pointer;transition:.22s}.service-tabs button>span{font-size:27px}.service-tabs button div{display:grid;gap:5px}.service-tabs strong{font-size:15px}.service-tabs small{color:#91a0aa;font-size:10px}.service-tabs button:hover,.service-tabs button.active{border-color:#1b8abd;background:#1479b8;color:#fff;transform:translateY(-3px)}.service-tabs button.active small{color:rgba(255,255,255,.7)}.toolbar{display:flex;align-items:end;justify-content:space-between;gap:25px;margin-bottom:25px}.toolbar h2{margin:0;color:#123149;font-size:29px}.toolbar p{margin:7px 0 0;color:#8294a0;font-size:12px}.filters{display:flex;gap:10px}.filters>.el-input{width:220px}.filters>.el-select{width:150px}.item-grid{display:grid;grid-template-columns:repeat(3,1fr);gap:23px}.service-item{overflow:hidden;border:1px solid #dce8ef;border-radius:18px;background:#fff;box-shadow:0 8px 25px rgba(17,70,105,.07);transition:.25s}.service-item:hover{transform:translateY(-4px);box-shadow:0 16px 36px rgba(17,70,105,.13)}.ticket-card{cursor:pointer}.ticket-card:focus-visible{outline:3px solid #60b7dc;outline-offset:3px}.item-visual{position:relative;display:flex;height:126px;align-items:center;justify-content:center;overflow:hidden;background:linear-gradient(135deg,#e5f7fc,#bde7f2);color:#157aa8}.item-visual::before,.item-visual::after{position:absolute;z-index:1;border-radius:50%;background:rgba(255,255,255,.44);content:''}.item-visual::before{right:-25px;bottom:-55px;width:150px;height:150px}.item-visual::after{top:-30px;left:-30px;width:90px;height:90px}.ticket-run-image{position:absolute;z-index:0;inset:0;width:100%;height:100%;object-fit:cover}.has-run-image{background:#154f72}.has-run-image::before,.has-run-image::after{background:rgba(3,34,55,.18)}.visual-equipment{background:linear-gradient(135deg,#e6f2ff,#cdddf6);color:#3a6ea5}.visual-courses{background:linear-gradient(135deg,#e9f8f3,#cceadf);color:#267e67}.visual-coaches{background:linear-gradient(135deg,#fff5e8,#f5dfbc);color:#a16a21}.visual-icon{position:relative;z-index:2;font-size:46px}.visual-code{position:absolute;z-index:2;right:18px;bottom:12px;font-size:38px;font-weight:800;opacity:.25}.item-visual small{position:absolute;z-index:2;bottom:13px;left:17px;font-size:9px;font-weight:800;letter-spacing:2px}.has-run-image .visual-icon,.has-run-image .item-visual small,.has-run-image small{color:#fff;text-shadow:0 2px 8px rgba(0,0,0,.35)}.item-content{display:flex;min-height:336px;flex-direction:column;padding:20px}.item-heading{display:flex;min-height:28px;align-items:center;justify-content:space-between;gap:10px}.price{color:#e66f34;font-size:21px}.price small{font-size:11px}.item-heading>span:not(.el-tag){color:#8b9aa4;font-size:12px}.item-content h3{overflow:hidden;margin:13px 0 9px;color:#15354c;font-size:20px;text-overflow:ellipsis;white-space:nowrap}.minor{display:flex;align-items:center;gap:5px;margin:0;color:#6e8798;font-size:12px}.description{display:-webkit-box;min-height:66px;margin:15px 0 0;color:#758997;font-size:12px;line-height:1.8;-webkit-box-orient:vertical;-webkit-line-clamp:3;overflow:hidden}.facts{display:flex;align-items:center;justify-content:space-between;gap:10px;margin:auto 0 17px;padding-top:14px;border-top:1px solid #e6eef2;color:#728794;font-size:11px}.item-content>.el-button,.item-content>a :deep(.el-button){width:100%}.certificate{display:grid;gap:4px;margin:auto 0 17px;padding:12px;border-radius:10px;background:#f7f3ea}.certificate span{color:#9d8a6c;font-size:10px}.certificate b{overflow:hidden;color:#654f32;font-size:12px;text-overflow:ellipsis;white-space:nowrap}.order-guide{display:flex;align-items:center;justify-content:space-between;margin-top:55px;padding:28px 35px;border-radius:18px;background:#eaf5f9}.order-guide>div{display:grid;grid-template-columns:35px 1fr;align-items:center;column-gap:10px}.order-guide div span{grid-row:1/3;display:grid;width:34px;height:34px;place-items:center;border-radius:50%;background:#1479b8;color:#fff;font-size:12px;font-weight:700}.order-guide strong{color:#294a61;font-size:13px}.order-guide small{margin-top:3px;color:#80929f;font-size:10px}.order-guide>i{width:35px;height:1px;background:#bad3df}.ticket-detail{display:grid;gap:25px;padding-bottom:8px}.ticket-detail-hero{position:relative;display:flex;min-height:185px;align-items:flex-end;overflow:hidden;margin:-20px -20px 0;padding:22px;background:linear-gradient(135deg,#0d597e,#249bbb);color:#fff}.ticket-detail-hero>img,.ticket-hero-shade{position:absolute;inset:0;width:100%;height:100%;object-fit:cover}.ticket-hero-shade{background:linear-gradient(90deg,rgba(4,35,57,.78),rgba(5,54,82,.18))}.ticket-hero-content{position:relative;z-index:1;display:grid;gap:8px}.ticket-detail-hero span{font-size:10px;font-weight:700;letter-spacing:1.5px}.ticket-detail-hero h2{margin:0;font-size:25px}.ticket-detail-hero p{display:flex;align-items:center;gap:5px;margin:0;color:rgba(255,255,255,.82);font-size:12px}.ticket-detail-hero>strong{position:absolute;z-index:1;right:22px;bottom:23px;color:#fff;font-size:25px}.ticket-info-section{display:grid;gap:11px}.ticket-section-title{display:flex;align-items:center;justify-content:space-between}.ticket-section-title h3{margin:0;color:#224359;font-size:17px}.ticket-section-title span{color:#8ca0ac;font-size:10px;letter-spacing:1px}.resort-info{display:flex;justify-content:space-between;gap:14px;padding:15px;border-radius:12px;background:#eff7fa}.resort-info>div{display:grid;gap:6px}.resort-info strong{color:#25485f;font-size:14px}.resort-info p{display:flex;align-items:center;gap:5px;margin:0;color:#718898;font-size:11px;line-height:1.5}.run-info{display:grid;grid-template-columns:145px 1fr;gap:14px;padding:10px;border:1px solid #e1ebf0;border-radius:12px}.run-info :deep(.el-image){width:145px;height:82px;border-radius:8px;cursor:zoom-in}.run-info>div{display:grid;align-content:center;justify-items:start;gap:7px}.run-info strong{color:#26495f;font-size:14px}.run-info p{margin:0;color:#7c909d;font-size:11px}.ticket-weather{display:grid;grid-template-columns:repeat(3,1fr);gap:9px}.ticket-weather>div{display:grid;justify-items:center;gap:6px;padding:12px 6px;border-radius:10px;background:#f0f8fb;text-align:center}.ticket-weather time,.ticket-weather small{color:#8296a3;font-size:10px}.ticket-weather b{color:#137aa8;font-size:14px}.ticket-weather span{color:#49697d;font-size:11px}.related-list{display:grid;gap:8px}.related-list>div{display:grid;grid-template-columns:34px 1fr auto;align-items:center;gap:9px;padding:11px 13px;border:1px solid #e1ebf0;border-radius:11px}.related-list>div>span{display:grid;width:32px;height:32px;place-items:center;border-radius:9px;background:#edf6fa;font-size:16px}.related-list p{display:grid;gap:4px;margin:0}.related-list strong{color:#315168;font-size:12px}.related-list small{color:#8497a4;font-size:10px}.ticket-buy-bar{position:sticky;bottom:0;display:flex;align-items:center;justify-content:space-between;gap:18px;padding:15px 0 0;border-top:1px solid #e1ebf0;background:#fff}.ticket-buy-bar div{display:grid;gap:3px}.ticket-buy-bar span{color:#8296a3;font-size:10px}.ticket-buy-bar strong{color:#e66f34;font-size:22px}.bundle-description{color:#557488;font-size:11px}.ticket-buy-bar :deep(.el-button){min-width:170px}
.has-run-image::before,.has-run-image::after{display:none}
.course-card{cursor:pointer}.course-card:focus-visible{outline:3px solid #60b7dc;outline-offset:3px}.course-detail{display:grid;gap:25px;padding-bottom:8px}.course-detail-hero{display:flex;align-items:flex-end;justify-content:space-between;min-height:155px;margin:-20px -20px 0;padding:25px 23px;background:linear-gradient(135deg,#217e72,#54b49d);color:#fff}.course-detail-hero>div{display:grid;gap:9px}.course-detail-hero span{font-size:10px;font-weight:700;letter-spacing:1.6px}.course-detail-hero h2{margin:0;font-size:25px}.course-detail-hero p{margin:0;color:rgba(255,255,255,.78);font-size:12px}.course-detail-hero>strong{font-size:25px}.course-content{min-height:105px;padding:16px;border-radius:12px;background:#f0f8f6;color:#4b6870;font-size:13px;line-height:1.9;white-space:pre-wrap}
.bundle-payment-dialog{display:grid;gap:20px}.bundle-payment-summary{display:grid;grid-template-columns:60px 1fr;align-items:center;gap:14px;padding:18px;border-radius:15px;background:#eaf5f9}.bundle-payment-summary>span{display:grid;width:54px;height:54px;place-items:center;border-radius:13px;background:#fff;font-size:27px}.bundle-payment-summary p{display:grid;gap:8px;margin:0}.bundle-payment-summary strong{color:#284a61;font-size:16px}.bundle-payment-summary small{color:#718b9d;font-size:13px}.bundle-payment-dialog>label{margin-bottom:-10px;color:#58758a;font-size:13px}.bundle-payment-methods{display:flex}.bundle-payment-methods :deep(.el-radio-button){flex:1}.bundle-payment-methods :deep(.el-radio-button__inner){width:100%}.bundle-payment-note{margin:0;padding:14px;border-radius:11px;background:#fff5e7;color:#ad7b43;font-size:12px;line-height:1.7}
@media(max-width:980px){.service-tabs{grid-template-columns:1fr 1fr}.item-grid{grid-template-columns:1fr 1fr}.order-guide{display:grid;grid-template-columns:1fr 1fr;gap:22px}.order-guide>i{display:none}}
@media(max-width:650px){.service-tabs{margin-top:-88px}.service-tabs button{padding:14px}.service-tabs button>span{font-size:22px}.toolbar{align-items:stretch;flex-direction:column}.filters{flex-direction:column}.filters>.el-input,.filters>.el-select{width:100%}.item-grid{grid-template-columns:1fr}.order-guide{grid-template-columns:1fr;padding:23px}.service-item{max-width:480px;width:100%;margin:0 auto}.ticket-detail-hero{min-height:165px}.ticket-detail-hero h2{font-size:21px}.ticket-detail-hero>strong{font-size:20px}.run-info{grid-template-columns:110px 1fr}.run-info :deep(.el-image){width:110px;height:74px}.ticket-weather{grid-template-columns:1fr}.ticket-buy-bar :deep(.el-button){min-width:130px}}
</style>
