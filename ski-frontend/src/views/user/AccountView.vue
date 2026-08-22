<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import dayjs from 'dayjs'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserProfile, updatePassword, updateUsername, updateUserProfile, uploadUserAvatar } from '../../api/auth'
import { cancelOrder, createFeedback, getMyFeedback, getMyOrders, getOrderDetail, markMyFeedbackRepliesRead, payOrder, returnOrder } from '../../api/user'
import PageBanner from '../../components/PageBanner.vue'
import { useAuthStore } from '../../stores/auth'
import { feedbackTypes, paymentMethods } from '../../utils/dictionaries'
import { formatDate, formatMoney, getOrderStatus } from '../../utils/format'

const auth = useAuthStore()
const activeTab = ref('orders')
const profileLoading = ref(true)
const ordersLoading = ref(true)
const feedbackLoading = ref(true)
const profile = ref(null)
const editProfileOpen = ref(false)
const profileSaving = ref(false)
const profileFormRef = ref()
const profileForm = reactive({ name: '', phone: '' })
const usernameOpen = ref(false)
const usernameSaving = ref(false)
const usernameFormRef = ref()
const usernameForm = reactive({ username: '' })
const passwordOpen = ref(false)
const passwordSaving = ref(false)
const passwordFormRef = ref()
const passwordForm = reactive({ currentPassword: '', newPassword: '', confirmPassword: '' })
const showCurrentPassword = ref(false)
const showNewPassword = ref(false)
const orders = ref([])
const feedbackList = ref([])
const orderStatusFilter = ref('全部')
const detailOpen = ref(false)
const detailLoading = ref(false)
const currentOrder = ref(null)
const paymentOpen = ref(false)
const paymentMethod = ref('微信')
const cancelOpen = ref(false)
const cancelReason = ref('')
const actionLoading = ref('')
const actionOrder = ref(null)
const feedbackFormRef = ref()
const feedbackSubmitting = ref(false)
const feedbackForm = reactive({ type: '服务评价', rating: 5, content: '' })

const displayName = computed(() => profile.value?.name || auth.user?.name || '游客')
const avatarUrl = computed(() => profile.value?.avatar || auth.user?.avatar || '')
const filteredOrders = computed(() => orderStatusFilter.value === '全部' ? orders.value : orders.value.filter((item) => item.status === orderStatusFilter.value))
const orderStatuses = computed(() => ['全部', ...new Set(orders.value.map((item) => item.status).filter(Boolean))])
const pendingCount = computed(() => orders.value.filter((item) => item.status === '待支付').length)
const paidAmount = computed(() => orders.value.filter((item) => ['已支付', '已完成'].includes(item.status)).reduce((sum, item) => sum + Number(item.amount || 0), 0))
const shortOrderId = (orderId) => `${String(orderId || '').slice(0, 10)}…`
const unreadReplyCount = computed(() => feedbackList.value.filter((item) => item.reply && !item.replyRead).length)

const feedbackRules = {
  type: [{ required: true, message: '请选择反馈类型', trigger: 'change' }],
  rating: [{ required: true, message: '请给出评分', trigger: 'change' }],
  content: [
    { required: true, message: '请输入反馈内容', trigger: 'blur' },
    { min: 5, max: 500, message: '反馈内容为 5—500 个字符', trigger: 'blur' }
  ]
}

const profileRules = {
  name: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' },
    { max: 30, message: '真实姓名不能超过 30 个字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的 11 位手机号', trigger: 'blur' }
  ]
}

const usernameRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { pattern: /^[A-Za-z0-9_]{3,20}$/, message: '用户名须为 3—20 位字母、数字或下划线', trigger: 'blur' }
  ]
}

const passwordRules = {
  currentPassword: [{ required: true, message: '请输入当前密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 24, message: '新密码长度为 6—24 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { validator: (_rule, value, callback) => value === passwordForm.newPassword ? callback() : callback(new Error('两次输入的新密码不一致')), trigger: 'blur' }
  ]
}

const loadProfile = async () => {
  profileLoading.value = true
  try {
    profile.value = await getUserProfile()
  } finally { profileLoading.value = false }
}
const loadOrders = async () => {
  ordersLoading.value = true
  try {
    const data = await getMyOrders()
    orders.value = Array.isArray(data) ? data : []
  } finally { ordersLoading.value = false }
}
const loadFeedback = async () => {
  feedbackLoading.value = true
  try {
    const data = await getMyFeedback()
    feedbackList.value = Array.isArray(data) ? data : []
  } finally { feedbackLoading.value = false }
}

const openFeedback = async () => {
  activeTab.value = 'feedback'
  await loadFeedback()
  if (!unreadReplyCount.value) return
  try {
    await markMyFeedbackRepliesRead()
    feedbackList.value = feedbackList.value.map((item) => item.reply ? { ...item, replyRead: true } : item)
  } catch {
    // 已展示反馈内容，即使标记已读失败也不影响游客查看。
  }
}

const openProfileEditor = () => {
  profileForm.name = profile.value?.name || ''
  profileForm.phone = profile.value?.phone || ''
  editProfileOpen.value = true
}

const submitProfile = async () => {
  try { await profileFormRef.value.validate() } catch { return }
  profileSaving.value = true
  try {
    const updated = await updateUserProfile({ name: profileForm.name.trim(), phone: profileForm.phone.trim() })
    profile.value = updated
    auth.updateUserProfile(updated)
    editProfileOpen.value = false
    ElMessage.success('个人资料已更新')
  } finally { profileSaving.value = false }
}

const getNextChangeTime = (value) => value ? dayjs(value).add(1, 'month') : null
const canChange = (value) => !value || !dayjs().isBefore(getNextChangeTime(value))
const changeTip = (value) => canChange(value) ? '本月可修改一次' : `下次可修改：${getNextChangeTime(value).format('YYYY年MM月DD日 HH:mm')}`

const applyProfile = (updated) => {
  profile.value = updated
  auth.updateUserProfile(updated)
}

const openUsernameEditor = () => {
  usernameForm.username = profile.value?.username || auth.user?.userName || ''
  usernameOpen.value = true
}

const submitUsername = async () => {
  try { await usernameFormRef.value.validate() } catch { return }
  usernameSaving.value = true
  try {
    const updated = await updateUsername({ username: usernameForm.username.trim() })
    applyProfile(updated)
    usernameOpen.value = false
    ElMessage.success('用户名已修改，下次可在一个月后修改')
  } finally { usernameSaving.value = false }
}

const openPasswordEditor = () => {
  Object.assign(passwordForm, { currentPassword: '', newPassword: '', confirmPassword: '' })
  passwordOpen.value = true
}

const submitPassword = async () => {
  try { await passwordFormRef.value.validate() } catch { return }
  passwordSaving.value = true
  try {
    const updated = await updatePassword({ currentPassword: passwordForm.currentPassword, newPassword: passwordForm.newPassword })
    applyProfile(updated)
    passwordOpen.value = false
    ElMessage.success('密码已修改，请妥善保管新密码')
  } finally { passwordSaving.value = false }
}

const beforeAvatarUpload = (file) => {
  if (!file.type.startsWith('image/')) {
    ElMessage.warning('只能上传图片文件')
    return false
  }
  if (file.size > 2 * 1024 * 1024) {
    ElMessage.warning('头像图片不能超过 2MB')
    return false
  }
  return true
}

const handleAvatarUpload = async ({ file }) => {
  const updated = await uploadUserAvatar(file)
  applyProfile(updated)
  ElMessage.success('头像已更新')
}

const replaceOrder = (updated) => {
  if (!updated) return
  const index = orders.value.findIndex((item) => Number(item.id) === Number(updated.id))
  if (index >= 0) orders.value.splice(index, 1, updated)
  if (Number(currentOrder.value?.id) === Number(updated.id)) currentOrder.value = updated
}

const openOrderDetail = async (order) => {
  currentOrder.value = order
  detailOpen.value = true
  detailLoading.value = true
  try {
    const detail = await getOrderDetail(order.id)
    if (detail) currentOrder.value = detail
  } finally { detailLoading.value = false }
}

const openPayment = (order) => {
  actionOrder.value = order
  paymentMethod.value = '微信'
  paymentOpen.value = true
}
const submitPayment = async () => {
  if (!actionOrder.value) return
  actionLoading.value = `pay-${actionOrder.value.id}`
  try {
    const updated = await payOrder(actionOrder.value.id, paymentMethod.value)
    replaceOrder(updated)
    paymentOpen.value = false
    ElMessage.success('订单支付成功')
  } finally { actionLoading.value = '' }
}

const openCancel = (order) => {
  actionOrder.value = order
  cancelReason.value = ''
  cancelOpen.value = true
}
const submitCancel = async () => {
  if (!actionOrder.value) return
  actionLoading.value = `cancel-${actionOrder.value.id}`
  try {
    const updated = await cancelOrder(actionOrder.value.id, cancelReason.value.trim() || '用户主动取消')
    replaceOrder(updated)
    cancelOpen.value = false
    ElMessage.success(updated?.status === '已退款' ? '订单已取消，状态更新为已退款' : '订单已取消')
  } finally { actionLoading.value = '' }
}

const handleReturn = async (order) => {
  try {
    await ElMessageBox.confirm(`确认已归还“${order.productName || '该雪具'}”吗？确认后订单将完成并恢复库存。`, '雪具归还', {
      confirmButtonText: '确认归还', cancelButtonText: '暂不归还', type: 'warning'
    })
  } catch { return }
  actionLoading.value = `return-${order.id}`
  try {
    const updated = await returnOrder(order.id)
    replaceOrder(updated)
    ElMessage.success('雪具归还成功，订单已完成')
  } finally { actionLoading.value = '' }
}

const submitFeedback = async () => {
  try { await feedbackFormRef.value.validate() } catch { return }
  feedbackSubmitting.value = true
  try {
    await createFeedback({ ...feedbackForm, content: feedbackForm.content.trim() })
    ElMessage.success('反馈已提交，我们会尽快处理')
    feedbackForm.type = '服务评价'
    feedbackForm.rating = 5
    feedbackForm.content = ''
    feedbackFormRef.value?.clearValidate()
    await loadFeedback()
  } finally { feedbackSubmitting.value = false }
}

const statusInfo = (value) => getOrderStatus(value)
const orderIcon = (type) => type === '雪票' ? '🎫' : type === '雪具租赁' ? '🎿' : type === '课程' ? '🏂' : '❄'
const canCancel = (order) => ['待支付', '已支付'].includes(order.status)
const canReturn = (order) => order.orderType === '雪具租赁' && order.status === '已支付'

onMounted(() => Promise.allSettled([loadProfile(), loadOrders(), loadFeedback()]))
</script>

<template>
  <div>
    <PageBanner :title="`${displayName}，欢迎回来`" description="在这里查看个人资料、处理滑雪服务订单，并向雪场提交意见与建议。" icon="👤">
      <template #actions><router-link to="/services"><el-button size="large" round color="#ffffff">继续选择服务</el-button></router-link></template>
    </PageBanner>

    <section class="account-page section-space">
      <div class="page-container account-layout">
        <aside class="account-sidebar glass-card">
          <div class="user-summary">
            <span class="large-avatar"><img v-if="avatarUrl" :src="avatarUrl" alt="游客头像"><template v-else>{{ displayName.slice(0, 1) }}</template></span>
            <div><strong>{{ displayName }}</strong><small>@{{ profile?.username || auth.user?.userName || 'visitor' }}</small></div>
          </div>
          <nav>
            <button type="button" :class="{ active: activeTab === 'orders' }" @click="activeTab = 'orders'"><el-icon><Tickets /></el-icon><span>我的订单</span><b v-if="pendingCount">{{ pendingCount }}</b></button>
            <button type="button" :class="{ active: activeTab === 'profile' }" @click="activeTab = 'profile'"><el-icon><User /></el-icon><span>个人资料</span></button>
            <button type="button" :class="{ active: activeTab === 'feedback' }" @click="openFeedback"><el-icon><ChatDotRound /></el-icon><span>意见反馈</span><b v-if="unreadReplyCount">{{ unreadReplyCount }}</b></button>
          </nav>
          <div class="sidebar-tip"><span>❄</span><p>出发前请留意天气雪况和雪场最新公告。</p><router-link to="/announcements">查看公告</router-link></div>
        </aside>

        <main class="account-content">
          <transition name="fade-slide" mode="out-in">
            <div v-if="activeTab === 'orders'" key="orders">
              <div class="content-heading"><div><span>MY ORDERS</span><h2>我的订单</h2><p>支付、取消服务订单，已支付的雪具订单可在归还后确认完成。</p></div><router-link to="/services"><el-button type="primary">预订新服务</el-button></router-link></div>

              <div class="stat-grid">
                <div><span class="stat-icon blue">📋</span><p><small>全部订单</small><strong>{{ orders.length }}</strong></p></div>
                <div><span class="stat-icon orange">⏳</span><p><small>待支付</small><strong>{{ pendingCount }}</strong></p></div>
                <div><span class="stat-icon green">¥</span><p><small>已支付金额</small><strong>{{ formatMoney(paidAmount) }}</strong></p></div>
              </div>

              <div class="order-toolbar"><div class="status-tabs"><button v-for="item in orderStatuses" :key="item" type="button" :class="{ active: orderStatusFilter === item }" @click="orderStatusFilter = item">{{ item }}</button></div><el-button :loading="ordersLoading" circle plain aria-label="刷新订单" @click="loadOrders"><el-icon><Refresh /></el-icon></el-button></div>

              <div class="order-panel">
                <el-table v-loading="ordersLoading" :data="filteredOrders" class="desktop-table" @row-click="openOrderDetail">
                  <el-table-column label="服务项目" min-width="190"><template #default="{ row }"><div class="product-cell"><span>{{ orderIcon(row.orderType) }}</span><div><strong>{{ row.productName || row.orderType }}</strong><small>{{ row.orderType }}</small></div></div></template></el-table-column>
                  <el-table-column label="订单号" width="120"><template #default="{ row }"><span class="order-code">{{ shortOrderId(row.orderId) }}</span></template></el-table-column>
                  <el-table-column label="金额" width="95"><template #default="{ row }"><strong class="amount">{{ formatMoney(row.amount) }}</strong></template></el-table-column>
                  <el-table-column label="下单时间" width="135"><template #default="{ row }"><span class="date-text">{{ formatDate(row.createTime, 'YYYY-MM-DD HH:mm') }}</span></template></el-table-column>
                  <el-table-column label="状态" width="90"><template #default="{ row }"><el-tag :type="statusInfo(row.status).type" effect="light" round>{{ statusInfo(row.status).label }}</el-tag></template></el-table-column>
                  <el-table-column label="操作" width="210" fixed="right"><template #default="{ row }"><div class="table-actions" @click.stop><el-button v-if="row.status === '待支付'" type="primary" size="small" :loading="actionLoading === `pay-${row.id}`" @click="openPayment(row)">支付</el-button><el-button v-if="canReturn(row)" type="success" plain size="small" :loading="actionLoading === `return-${row.id}`" @click="handleReturn(row)">归还</el-button><el-button v-if="canCancel(row)" plain size="small" :loading="actionLoading === `cancel-${row.id}`" @click="openCancel(row)">取消</el-button><el-button text size="small" @click="openOrderDetail(row)">详情</el-button></div></template></el-table-column>
                  <template #empty><el-empty :image-size="80" description="暂无订单，去选择一项滑雪服务吧" /></template>
                </el-table>

                <div v-loading="ordersLoading" class="mobile-orders">
                  <article v-for="row in filteredOrders" :key="row.id">
                    <div class="mobile-order-head"><span>{{ orderIcon(row.orderType) }}</span><div><strong>{{ row.productName || row.orderType }}</strong><small>{{ formatDate(row.createTime, 'YYYY-MM-DD HH:mm') }}</small></div><el-tag :type="statusInfo(row.status).type" size="small" round>{{ row.status }}</el-tag></div>
                    <div class="mobile-order-info"><span>订单号 {{ row.orderId }}</span><strong>{{ formatMoney(row.amount) }}</strong></div>
                    <div class="mobile-order-actions"><el-button v-if="row.status === '待支付'" type="primary" size="small" @click="openPayment(row)">支付</el-button><el-button v-if="canReturn(row)" type="success" plain size="small" @click="handleReturn(row)">归还</el-button><el-button v-if="canCancel(row)" plain size="small" @click="openCancel(row)">取消</el-button><el-button text size="small" @click="openOrderDetail(row)">查看详情</el-button></div>
                  </article>
                  <el-empty v-if="!ordersLoading && !filteredOrders.length" :image-size="75" description="暂无订单" />
                </div>
              </div>
            </div>

            <div v-else-if="activeTab === 'profile'" key="profile">
              <div class="content-heading"><div><span>PERSONAL PROFILE</span><h2>个人资料</h2><p>以下资料用于订单归属和游客服务联系。</p></div><el-button type="primary" @click="openProfileEditor"><el-icon><EditPen /></el-icon> 编辑资料</el-button></div>
              <div v-loading="profileLoading" class="profile-card">
                <div class="profile-cover"><el-upload class="avatar-upload" :show-file-list="false" accept="image/*" :before-upload="beforeAvatarUpload" :http-request="handleAvatarUpload"><span class="profile-avatar"><img v-if="avatarUrl" :src="avatarUrl" alt="游客头像"><template v-else>{{ displayName.slice(0, 1) }}</template><i>更换头像</i></span></el-upload><div><h3>{{ displayName }}</h3><p>黑龙江冰雪旅行者</p></div><el-tag type="success" effect="dark" round>游客账号</el-tag></div>
                <div class="profile-fields">
                  <div><span><el-icon><User /></el-icon>用户名</span><strong>{{ profile?.username || auth.user?.userName || '—' }}</strong><el-button link type="primary" :disabled="!canChange(profile?.usernameUpdateTime)" @click="openUsernameEditor">修改用户名</el-button><small class="change-tip">{{ changeTip(profile?.usernameUpdateTime) }}</small></div>
                  <div><span><el-icon><Postcard /></el-icon>真实姓名</span><strong>{{ profile?.name || '—' }}</strong></div>
                  <div><span><el-icon><Phone /></el-icon>联系电话</span><strong>{{ profile?.phone || '—' }}</strong></div>
                  <div><span><el-icon><Calendar /></el-icon>注册时间</span><strong>{{ formatDate(profile?.createTime, 'YYYY年MM月DD日') }}</strong></div>
                </div>
                <div class="profile-actions"><el-button plain @click="openPasswordEditor" :disabled="!canChange(profile?.passwordUpdateTime)"><el-icon><Lock /></el-icon> 修改密码</el-button><small>{{ changeTip(profile?.passwordUpdateTime) }}</small></div>
                <div class="profile-note"><el-icon><InfoFilled /></el-icon><p><strong>资料说明</strong><span>头像、姓名和联系电话可随时修改；用户名和密码分别每月仅可修改一次。</span></p></div>
              </div>
            </div>

            <div v-else key="feedback">
              <div class="content-heading"><div><span>VISITOR FEEDBACK</span><h2>意见反馈</h2><p>欢迎评价服务或提出设施与安全建议，管理人员回复后会显示在这里。</p></div></div>
              <div class="feedback-layout">
                <div class="feedback-form-card">
                  <h3>提交新反馈</h3><p>请尽量描述具体时间、地点和问题，便于工作人员处理。</p>
                  <el-form ref="feedbackFormRef" :model="feedbackForm" :rules="feedbackRules" label-position="top">
                    <el-form-item label="反馈类型" prop="type"><el-select v-model="feedbackForm.type" style="width:100%"><el-option v-for="item in feedbackTypes" :key="item" :label="item" :value="item" /></el-select></el-form-item>
                    <el-form-item label="服务评分" prop="rating"><el-rate v-model="feedbackForm.rating" show-text :texts="['不满意', '待改进', '一般', '满意', '非常满意']" /></el-form-item>
                    <el-form-item label="反馈内容" prop="content"><el-input v-model="feedbackForm.content" type="textarea" :rows="6" maxlength="500" show-word-limit placeholder="请描述你的体验、建议或遇到的问题……" /></el-form-item>
                    <el-button type="primary" size="large" :loading="feedbackSubmitting" @click="submitFeedback">提交反馈 <el-icon class="el-icon--right"><Promotion /></el-icon></el-button>
                  </el-form>
                </div>
                <div class="feedback-history">
                  <div class="history-title"><h3>我的反馈</h3><el-button :loading="feedbackLoading" circle plain size="small" @click="loadFeedback"><el-icon><Refresh /></el-icon></el-button></div>
                  <div v-loading="feedbackLoading" class="feedback-list">
                    <article v-for="item in feedbackList" :key="item.id"><div class="feedback-head"><el-tag effect="plain">{{ item.type || '其他' }}</el-tag><el-rate :model-value="item.rating" disabled /><time>{{ formatDate(item.createTime, 'YYYY-MM-DD HH:mm') }}</time></div><p>{{ item.content }}</p><div v-if="item.reply" class="reply"><span>管理回复</span><p>{{ item.reply }}</p><time>{{ formatDate(item.replyTime, 'YYYY-MM-DD HH:mm') }}</time></div><div v-else class="waiting"><el-icon><Clock /></el-icon>等待工作人员处理</div></article>
                    <el-empty v-if="!feedbackLoading && !feedbackList.length" :image-size="70" description="尚未提交反馈" />
                  </div>
                </div>
              </div>
            </div>
          </transition>
        </main>
      </div>
    </section>

    <el-dialog v-model="paymentOpen" title="支付订单" width="min(440px, 92vw)" destroy-on-close>
      <div v-if="actionOrder" class="action-dialog"><div class="dialog-order"><span>{{ orderIcon(actionOrder.orderType) }}</span><p><strong>{{ actionOrder.productName || actionOrder.orderType }}</strong><small>应付金额 {{ formatMoney(actionOrder.amount) }}</small></p></div><label>选择支付方式</label><el-radio-group v-model="paymentMethod" class="payment-methods"><el-radio-button v-for="item in paymentMethods" :key="item" :value="item">{{ item }}</el-radio-button></el-radio-group><p class="simulation-note"><el-icon><InfoFilled /></el-icon>毕业设计演示环境仅更新订单支付状态，不会发起真实扣款。</p></div>
      <template #footer><el-button @click="paymentOpen = false">取消</el-button><el-button type="primary" :loading="actionLoading.startsWith('pay-')" @click="submitPayment">确认支付 {{ formatMoney(actionOrder?.amount) }}</el-button></template>
    </el-dialog>

    <el-dialog v-model="cancelOpen" title="取消订单" width="min(440px, 92vw)" destroy-on-close>
      <div class="action-dialog"><p class="cancel-tip">{{ actionOrder?.status === '已支付' ? '该订单已支付，取消后状态将更新为已退款，并恢复相关库存。' : '取消待支付订单后，相关雪具或课程名额将恢复。' }}</p><label>取消原因</label><el-input v-model="cancelReason" type="textarea" :rows="3" maxlength="200" show-word-limit placeholder="例如：行程发生变化" /></div>
      <template #footer><el-button @click="cancelOpen = false">暂不取消</el-button><el-button type="danger" :loading="actionLoading.startsWith('cancel-')" @click="submitCancel">确认取消</el-button></template>
    </el-dialog>

    <el-dialog v-model="editProfileOpen" title="编辑个人资料" width="min(440px, 92vw)" destroy-on-close @closed="profileFormRef?.clearValidate()">
      <el-form ref="profileFormRef" :model="profileForm" :rules="profileRules" label-position="top">
        <el-form-item label="用户名"><el-input :model-value="profile?.username || auth.user?.userName" disabled /></el-form-item>
        <el-form-item label="真实姓名" prop="name"><el-input v-model="profileForm.name" maxlength="30" show-word-limit placeholder="请输入真实姓名" /></el-form-item>
        <el-form-item label="联系电话" prop="phone"><el-input v-model="profileForm.phone" maxlength="11" placeholder="请输入 11 位手机号" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="editProfileOpen = false">取消</el-button><el-button type="primary" :loading="profileSaving" @click="submitProfile">保存修改</el-button></template>
    </el-dialog>

    <el-dialog v-model="usernameOpen" title="修改用户名" width="min(440px, 92vw)" destroy-on-close @closed="usernameFormRef?.clearValidate()">
      <p class="dialog-hint">用户名每月仅可修改一次，修改后下次可修改时间将自动顺延一个月。</p>
      <el-form ref="usernameFormRef" :model="usernameForm" :rules="usernameRules" label-position="top"><el-form-item label="新用户名" prop="username"><el-input v-model="usernameForm.username" maxlength="20" placeholder="3—20 位字母、数字或下划线" /></el-form-item></el-form>
      <template #footer><el-button @click="usernameOpen = false">取消</el-button><el-button type="primary" :loading="usernameSaving" @click="submitUsername">确认修改</el-button></template>
    </el-dialog>

    <el-dialog v-model="passwordOpen" title="修改密码" width="min(440px, 92vw)" destroy-on-close @closed="passwordFormRef?.clearValidate()">
      <p class="dialog-hint">密码每月仅可修改一次，请先验证当前密码。</p>
      <el-form ref="passwordFormRef" :model="passwordForm" :rules="passwordRules" label-position="top"><el-form-item label="当前密码" prop="currentPassword"><el-input v-model="passwordForm.currentPassword" :type="showCurrentPassword ? 'text' : 'password'" autocomplete="current-password"><template #append><el-button text @click="showCurrentPassword = !showCurrentPassword">{{ showCurrentPassword ? '隐藏' : '显示' }}</el-button></template></el-input></el-form-item><el-form-item label="新密码" prop="newPassword"><el-input v-model="passwordForm.newPassword" :type="showNewPassword ? 'text' : 'password'" maxlength="24" autocomplete="new-password"><template #append><el-button text @click="showNewPassword = !showNewPassword">{{ showNewPassword ? '隐藏' : '显示' }}</el-button></template></el-input></el-form-item><el-form-item label="确认新密码" prop="confirmPassword"><el-input v-model="passwordForm.confirmPassword" type="password" maxlength="24" autocomplete="new-password" /></el-form-item></el-form>
      <template #footer><el-button @click="passwordOpen = false">取消</el-button><el-button type="primary" :loading="passwordSaving" @click="submitPassword">确认修改</el-button></template>
    </el-dialog>

    <el-drawer v-model="detailOpen" size="min(540px, 94vw)" title="订单详情">
      <div v-loading="detailLoading" class="order-detail">
        <template v-if="currentOrder"><div class="detail-product"><span>{{ orderIcon(currentOrder.orderType) }}</span><div><small>{{ currentOrder.orderType }}</small><h3>{{ currentOrder.productName || '滑雪服务' }}</h3></div><strong>{{ formatMoney(currentOrder.amount) }}</strong></div><el-descriptions :column="1" border><el-descriptions-item label="订单号">{{ currentOrder.orderId }}</el-descriptions-item><el-descriptions-item label="订单状态"><el-tag :type="statusInfo(currentOrder.status).type">{{ currentOrder.status }}</el-tag></el-descriptions-item><el-descriptions-item label="支付方式">{{ currentOrder.paymentMethod || '尚未支付' }}</el-descriptions-item><el-descriptions-item label="游客姓名">{{ currentOrder.userName || displayName }}</el-descriptions-item><el-descriptions-item label="联系电话">{{ currentOrder.userPhone || profile?.phone || '—' }}</el-descriptions-item><el-descriptions-item label="下单时间">{{ formatDate(currentOrder.createTime, 'YYYY-MM-DD HH:mm:ss') }}</el-descriptions-item><el-descriptions-item v-if="currentOrder.endTime" label="结束时间">{{ formatDate(currentOrder.endTime, 'YYYY-MM-DD HH:mm:ss') }}</el-descriptions-item><el-descriptions-item v-if="currentOrder.cancelReason" label="取消原因">{{ currentOrder.cancelReason }}</el-descriptions-item></el-descriptions><div class="detail-actions"><el-button v-if="currentOrder.status === '待支付'" type="primary" @click="openPayment(currentOrder)">支付订单</el-button><el-button v-if="canReturn(currentOrder)" type="success" plain @click="handleReturn(currentOrder)">确认归还</el-button><el-button v-if="canCancel(currentOrder)" plain @click="openCancel(currentOrder)">取消订单</el-button></div></template>
      </div>
    </el-drawer>
  </div>
</template>

<style scoped>
.account-page{background:linear-gradient(180deg,#f4f8fb,#fff)}.account-layout{position:relative;z-index:3;display:grid;grid-template-columns:250px 1fr;align-items:start;gap:27px;margin-top:-96px}.account-sidebar{position:sticky;top:98px;overflow:hidden;padding:20px}.user-summary{display:flex;align-items:center;gap:12px;padding:7px 4px 20px;border-bottom:1px solid #e0ebf0}.large-avatar{display:grid;width:49px;height:49px;flex:0 0 auto;place-items:center;border-radius:15px;background:linear-gradient(145deg,#1479b8,#39bdcb);color:#fff;font-size:20px;font-weight:800}.user-summary div{display:grid;min-width:0;gap:5px}.user-summary strong{overflow:hidden;color:#1b3b52;text-overflow:ellipsis;white-space:nowrap}.user-summary small{overflow:hidden;color:#8999a4;font-size:10px;text-overflow:ellipsis;white-space:nowrap}.account-sidebar nav{display:grid;gap:5px;margin-top:18px}.account-sidebar nav button{display:grid;grid-template-columns:22px 1fr auto;align-items:center;gap:8px;padding:12px 13px;border:0;border-radius:10px;background:transparent;color:#627b8d;text-align:left;cursor:pointer}.account-sidebar nav button:hover,.account-sidebar nav button.active{background:#e6f4f9;color:#126f9e;font-weight:700}.account-sidebar nav b{display:grid;min-width:20px;height:20px;place-items:center;border-radius:20px;background:#ff8450;color:#fff;font-size:10px}.sidebar-tip{margin-top:24px;padding:16px;border-radius:13px;background:linear-gradient(145deg,#0e6b9c,#29aabf);color:#fff}.sidebar-tip>span{font-size:22px}.sidebar-tip p{margin:9px 0;color:rgba(255,255,255,.77);font-size:10px;line-height:1.7}.sidebar-tip a{font-size:11px;font-weight:700}.account-content{min-width:0;padding:32px;border:1px solid #dfeaf0;border-radius:20px;background:#fff;box-shadow:0 13px 38px rgba(15,67,99,.09)}.content-heading{display:flex;align-items:end;justify-content:space-between;gap:20px;margin-bottom:28px}.content-heading span{color:#1783b2;font-size:9px;font-weight:800;letter-spacing:2px}.content-heading h2{margin:7px 0 0;color:#14344c;font-size:29px}.content-heading p{margin:8px 0 0;color:#8395a1;font-size:12px}.stat-grid{display:grid;grid-template-columns:repeat(3,1fr);gap:12px;margin-bottom:26px}.stat-grid>div{display:flex;align-items:center;gap:12px;padding:16px;border:1px solid #e0ebf0;border-radius:13px}.stat-icon{display:grid;width:42px;height:42px;place-items:center;border-radius:12px}.stat-icon.blue{background:#e5f4fb}.stat-icon.orange{background:#fff0e8}.stat-icon.green{background:#e5f6ee;color:#27825a;font-weight:800}.stat-grid p{display:grid;gap:5px;margin:0}.stat-grid small{color:#8294a0;font-size:10px}.stat-grid strong{color:#203f55;font-size:17px}.order-toolbar{display:flex;align-items:center;justify-content:space-between;margin-bottom:15px}.status-tabs{display:flex;gap:4px;overflow-x:auto}.status-tabs button{padding:8px 12px;border:0;border-radius:9px;background:transparent;color:#778c9a;font-size:11px;white-space:nowrap;cursor:pointer}.status-tabs button.active{background:#e5f3f9;color:#1479b8;font-weight:700}.order-panel{overflow:hidden;border:1px solid #e0eaf0;border-radius:14px}.desktop-table :deep(.el-table__row){cursor:pointer}.product-cell{display:flex;align-items:center;gap:10px}.product-cell>span{display:grid;width:35px;height:35px;place-items:center;border-radius:9px;background:#edf6fa;font-size:17px}.product-cell div{display:grid;gap:4px}.product-cell strong{color:#29485e;font-size:12px}.product-cell small{color:#8a9aa5;font-size:10px}.order-code{display:inline-block;max-width:100px;overflow:hidden;color:#708796;font-family:monospace;font-size:10px;text-overflow:ellipsis;white-space:nowrap}.amount{color:#e46f35;font-size:12px}.date-text{color:#748a99;font-size:10px}.table-actions{display:flex;align-items:center}.mobile-orders{display:none}.profile-card{overflow:hidden;border:1px solid #dce8ef;border-radius:17px}.profile-cover{display:flex;align-items:center;gap:15px;padding:32px;background:linear-gradient(125deg,#0b4169,#1591b0);color:#fff}.profile-avatar{display:grid;width:67px;height:67px;place-items:center;border:2px solid rgba(255,255,255,.7);border-radius:20px;background:rgba(255,255,255,.16);font-size:27px;font-weight:800}.profile-cover div{display:grid;gap:5px}.profile-cover h3{margin:0;font-size:23px}.profile-cover p{margin:0;color:rgba(255,255,255,.68);font-size:11px}.profile-cover>.el-tag{margin-left:auto}.profile-fields{display:grid;grid-template-columns:1fr 1fr;padding:10px 31px}.profile-fields>div{display:grid;gap:9px;padding:24px 5px;border-bottom:1px solid #e3ecf1}.profile-fields>div:nth-last-child(-n+2){border-bottom:0}.profile-fields span{display:flex;align-items:center;gap:7px;color:#7b8f9d;font-size:11px}.profile-fields strong{color:#24465d;font-size:14px}.profile-note{display:flex;align-items:flex-start;gap:10px;margin:0 31px 30px;padding:16px;border-radius:12px;background:#edf7fa;color:#1479b8}.profile-note p{display:grid;gap:5px;margin:0}.profile-note strong{color:#31556c;font-size:12px}.profile-note span{color:#7d909d;font-size:10px;line-height:1.6}.feedback-layout{display:grid;grid-template-columns:.8fr 1.2fr;gap:22px}.feedback-form-card,.feedback-history{padding:22px;border:1px solid #dce8ef;border-radius:15px}.feedback-form-card>h3,.history-title h3{margin:0;color:#23445a;font-size:17px}.feedback-form-card>p{margin:8px 0 20px;color:#8496a2;font-size:10px;line-height:1.6}.feedback-form-card :deep(.el-form-item__label){color:#527084;font-size:11px;font-weight:600}.feedback-form-card :deep(.el-button){width:100%}.history-title{display:flex;align-items:center;justify-content:space-between;margin-bottom:13px}.feedback-list{display:grid;max-height:600px;gap:12px;overflow-y:auto}.feedback-list>article{padding:16px;border-radius:12px;background:#f7fafc}.feedback-head{display:flex;align-items:center;gap:8px}.feedback-head time{margin-left:auto;color:#8b9ba5;font-size:9px}.feedback-list article>p{margin:13px 0 0;color:#496579;font-size:12px;line-height:1.75}.reply{margin-top:14px;padding:12px;border-left:3px solid #2bb3c5;border-radius:0 9px 9px 0;background:#e9f6f8}.reply>span{color:#13809b;font-size:10px;font-weight:700}.reply p{margin:6px 0;color:#466778;font-size:11px;line-height:1.7}.reply time{color:#8b9ba5;font-size:9px}.waiting{display:flex;align-items:center;gap:5px;margin-top:12px;color:#a48a68;font-size:10px}.action-dialog{display:grid;gap:14px}.dialog-order{display:flex;align-items:center;gap:12px;padding:14px;border-radius:12px;background:#edf6fa}.dialog-order>span{font-size:27px}.dialog-order p{display:grid;gap:4px;margin:0}.dialog-order small{color:#758c9c}.action-dialog>label{color:#536f82;font-size:12px;font-weight:700}.payment-methods{display:grid;grid-template-columns:repeat(3,1fr)}.payment-methods :deep(.el-radio-button__inner){width:100%}.simulation-note{display:flex;align-items:flex-start;gap:6px;margin:0;padding:11px;border-radius:8px;background:#fff6ea;color:#9b7243;font-size:10px;line-height:1.6}.cancel-tip{margin:0;color:#6b8292;font-size:12px;line-height:1.7}.order-detail{padding:5px}.detail-product{display:grid;grid-template-columns:50px 1fr auto;align-items:center;gap:13px;margin-bottom:23px;padding:18px;border-radius:14px;background:linear-gradient(145deg,#e9f7fb,#f7fcfd)}.detail-product>span{font-size:31px}.detail-product div{display:grid;gap:4px}.detail-product small{color:#788f9e;font-size:10px}.detail-product h3{margin:0;color:#24465d}.detail-product>strong{color:#e66e33}.detail-actions{display:flex;gap:8px;margin-top:24px}
@media(max-width:1020px){.account-layout{grid-template-columns:210px 1fr}.account-content{padding:24px}.feedback-layout{grid-template-columns:1fr}.desktop-table{display:none}.mobile-orders{display:grid;gap:10px;padding:12px}.mobile-orders article{padding:14px;border:1px solid #e0eaf0;border-radius:12px}.mobile-order-head{display:grid;grid-template-columns:36px 1fr auto;align-items:center;gap:9px}.mobile-order-head>span{font-size:22px}.mobile-order-head div{display:grid;gap:4px}.mobile-order-head strong{font-size:12px}.mobile-order-head small{color:#8a9aa5;font-size:9px}.mobile-order-info{display:flex;justify-content:space-between;margin-top:13px;padding-top:11px;border-top:1px solid #e6eef2;color:#8395a1;font-size:9px}.mobile-order-info strong{color:#df6a32;font-size:13px}.mobile-order-actions{display:flex;justify-content:flex-end;margin-top:10px}}
@media(max-width:760px){.account-layout{display:block;margin-top:-75px}.account-sidebar{position:relative;top:auto;margin-bottom:15px}.user-summary,.sidebar-tip{display:none}.account-sidebar nav{grid-template-columns:repeat(3,1fr);margin:0}.account-sidebar nav button{display:flex;justify-content:center;padding:10px 5px}.account-sidebar nav button b{display:none}.account-content{padding:20px}.stat-grid{grid-template-columns:1fr 1fr}.stat-grid>div:last-child{grid-column:1/-1}.content-heading{align-items:flex-start;flex-direction:column}.profile-fields{grid-template-columns:1fr;padding:10px 22px}.profile-fields>div{border-bottom:1px solid #e3ecf1!important}.profile-fields>div:last-child{border-bottom:0!important}.profile-cover{padding:24px 20px}.profile-note{margin:0 20px 23px}.feedback-form-card,.feedback-history{padding:18px}}
@media(max-width:480px){.account-sidebar nav button{font-size:11px}.account-sidebar nav .el-icon{display:none}.stat-grid>div{padding:12px}.stat-icon{width:36px;height:36px}.mobile-order-actions{flex-wrap:wrap}.profile-cover>.el-tag{display:none}.payment-methods{grid-template-columns:1fr}.payment-methods :deep(.el-radio-button__inner){border:1px solid var(--el-border-color)!important;border-radius:7px!important}}
.large-avatar img,.profile-avatar img{width:100%;height:100%;object-fit:cover}.avatar-upload{display:flex}.avatar-upload :deep(.el-upload){border-radius:20px;cursor:pointer}.profile-avatar{position:relative;overflow:hidden}.profile-avatar i{position:absolute;right:0;bottom:0;left:0;padding:5px 0;background:rgba(7,48,75,.65);color:#fff;font-size:9px;font-style:normal;text-align:center;opacity:0;transition:opacity .2s}.profile-avatar:hover i{opacity:1}.profile-fields .el-button{justify-self:start;height:auto;margin:-4px 0 -7px;padding:0;font-size:11px}.change-tip{color:#9aaab4;font-size:9px}.profile-actions{display:flex;align-items:center;gap:10px;margin:0 31px 18px}.profile-actions small{color:#8b9ca6;font-size:10px}.dialog-hint{margin:0 0 16px;padding:11px;border-radius:9px;background:#edf7fa;color:#668494;font-size:11px;line-height:1.6}@media(max-width:760px){.profile-actions{margin:0 20px 17px;align-items:flex-start;flex-direction:column;gap:5px}}
</style>
