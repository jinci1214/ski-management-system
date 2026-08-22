<script setup>
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getAdminOrderDetail, getOrderPage, updateOrderStatus } from '../../api/admin'
import { orderStatuses, orderTypes } from '../../utils/dictionaries'
import { formatDate, formatMoney, getOrderStatus } from '../../utils/format'

const loading = ref(false)
const detailLoading = ref(false)
const saving = ref(false)
const rows = ref([])
const total = ref(0)
const detailVisible = ref(false)
const statusVisible = ref(false)
const selectedOrder = ref(null)

const query = reactive({
  orderId: '',
  userId: '',
  orderType: '',
  status: '',
  pageNum: 1,
  pageSize: 10
})

const statusForm = reactive({
  id: null,
  orderId: '',
  currentStatus: '',
  status: '',
  cancelReason: ''
})

const compactParams = () => Object.fromEntries(
  Object.entries(query).filter(([, value]) => value !== '' && value !== null && value !== undefined)
)

const loadOrders = async () => {
  loading.value = true
  try {
    const result = await getOrderPage(compactParams()) || {}
    rows.value = Array.isArray(result.data) ? result.data : []
    total.value = Number(result.total || 0)
  } finally {
    loading.value = false
  }
}

const search = () => {
  query.pageNum = 1
  loadOrders()
}

const reset = () => {
  Object.assign(query, { orderId: '', userId: '', orderType: '', status: '', pageNum: 1, pageSize: 10 })
  loadOrders()
}

const openDetail = async (row) => {
  detailVisible.value = true
  selectedOrder.value = row
  detailLoading.value = true
  try {
    selectedOrder.value = await getAdminOrderDetail(row.id) || row
  } finally {
    detailLoading.value = false
  }
}

const openStatusDialog = (row) => {
  Object.assign(statusForm, {
    id: row.id,
    orderId: row.orderId,
    currentStatus: row.status,
    status: row.status,
    cancelReason: row.cancelReason || ''
  })
  statusVisible.value = true
}

const saveStatus = async () => {
  if (!statusForm.status) {
    ElMessage.warning('请选择订单状态')
    return
  }
  if (statusForm.status === '已取消' && !statusForm.cancelReason.trim()) {
    ElMessage.warning('取消订单时请填写取消原因')
    return
  }

  saving.value = true
  try {
    await updateOrderStatus(
      statusForm.id,
      statusForm.status,
      statusForm.status === '已取消' ? statusForm.cancelReason.trim() : undefined
    )
    ElMessage.success('订单状态已更新')
    statusVisible.value = false
    await loadOrders()
  } finally {
    saving.value = false
  }
}

const handlePageSize = (size) => {
  query.pageSize = size
  query.pageNum = 1
  loadOrders()
}

const orderTypeTag = (type) => ({
  雪票: 'primary',
  雪具租赁: 'warning',
  课程: 'success'
}[type] || 'info')

loadOrders()
</script>

<template>
  <div class="orders-page">
    <div class="page-heading">
      <div>
        <p>ORDER CENTER</p>
        <h2>订单管理</h2>
        <span>查询订单记录，并跟进支付、完成、取消和退款状态</span>
      </div>
      <el-button :loading="loading" @click="loadOrders"><el-icon><Refresh /></el-icon>刷新</el-button>
    </div>

    <el-card class="filter-card" shadow="never">
      <el-form class="filter-form" :inline="true" @submit.prevent>
        <el-form-item label="订单号">
          <el-input v-model.trim="query.orderId" placeholder="输入完整或部分订单号" clearable @keyup.enter="search" />
        </el-form-item>
        <el-form-item label="游客 ID">
          <el-input v-model.trim="query.userId" placeholder="输入游客 ID" clearable @keyup.enter="search" />
        </el-form-item>
        <el-form-item label="订单类型">
          <el-select v-model="query.orderType" placeholder="全部类型" clearable>
            <el-option v-for="item in orderTypes" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item label="订单状态">
          <el-select v-model="query.status" placeholder="全部状态" clearable>
            <el-option v-for="item in orderStatuses" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item class="filter-actions">
          <el-button type="primary" @click="search"><el-icon><Search /></el-icon>查询</el-button>
          <el-button @click="reset"><el-icon><RefreshLeft /></el-icon>重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card" shadow="never">
      <div class="table-summary">
        <div><strong>订单列表</strong><span>共 {{ total }} 条记录</span></div>
        <el-tag effect="plain" round>第 {{ query.pageNum }} 页</el-tag>
      </div>
      <el-table v-loading="loading" :data="rows" stripe table-layout="fixed" empty-text="暂无符合条件的订单">
        <el-table-column prop="orderId" label="订单号" min-width="188">
          <template #default="{ row }"><span class="order-no">{{ row.orderId || '—' }}</span></template>
        </el-table-column>
        <el-table-column prop="userId" label="游客 ID" width="90" align="center" />
        <el-table-column prop="orderType" label="类型" width="115" align="center">
          <template #default="{ row }"><el-tag :type="orderTypeTag(row.orderType)" effect="light">{{ row.orderType || '未分类' }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="amount" label="订单金额" width="115" align="right">
          <template #default="{ row }"><strong class="money">{{ formatMoney(row.amount) }}</strong></template>
        </el-table-column>
        <el-table-column prop="paymentMethod" label="支付方式" width="100" align="center">
          <template #default="{ row }">{{ row.paymentMethod || '—' }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="105" align="center">
          <template #default="{ row }">
            <el-tag :type="getOrderStatus(row.status).type" effect="plain" round>{{ getOrderStatus(row.status).label }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" min-width="155">
          <template #default="{ row }">{{ formatDate(row.createTime, 'YYYY-MM-DD HH:mm') }}</template>
        </el-table-column>
        <el-table-column label="操作" width="155" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDetail(row)">详情</el-button>
            <el-button link type="warning" @click="openStatusDialog(row)">修改状态</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="query.pageNum"
          v-model:page-size="query.pageSize"
          :page-sizes="[10, 20, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          background
          @current-change="loadOrders"
          @size-change="handlePageSize"
        />
      </div>
    </el-card>

    <el-drawer v-model="detailVisible" title="订单详情" size="min(92vw, 520px)">
      <div v-loading="detailLoading" class="detail-body">
        <template v-if="selectedOrder">
          <div class="detail-banner">
            <div><span>订单号</span><strong>{{ selectedOrder.orderId || '—' }}</strong></div>
            <el-tag :type="getOrderStatus(selectedOrder.status).type" effect="dark">{{ getOrderStatus(selectedOrder.status).label }}</el-tag>
          </div>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="订单类型">{{ selectedOrder.orderType || '—' }}</el-descriptions-item>
            <el-descriptions-item label="游客 ID">{{ selectedOrder.userId || '—' }}</el-descriptions-item>
            <el-descriptions-item label="关联业务 ID">
              {{ selectedOrder.ticketId || selectedOrder.equipmentId || selectedOrder.courseId || '—' }}
            </el-descriptions-item>
            <el-descriptions-item label="订单金额"><strong class="money">{{ formatMoney(selectedOrder.amount) }}</strong></el-descriptions-item>
            <el-descriptions-item label="支付方式">{{ selectedOrder.paymentMethod || '—' }}</el-descriptions-item>
            <el-descriptions-item label="创建时间">{{ formatDate(selectedOrder.createTime, 'YYYY-MM-DD HH:mm:ss') }}</el-descriptions-item>
            <el-descriptions-item label="结束时间">{{ formatDate(selectedOrder.endTime, 'YYYY-MM-DD HH:mm:ss') }}</el-descriptions-item>
            <el-descriptions-item v-if="selectedOrder.cancelReason" label="取消原因">{{ selectedOrder.cancelReason }}</el-descriptions-item>
          </el-descriptions>
        </template>
        <el-empty v-else description="未获取到订单详情" />
      </div>
    </el-drawer>

    <el-dialog v-model="statusVisible" title="修改订单状态" width="min(92vw, 480px)" destroy-on-close>
      <div class="status-order-info">订单号：<strong>{{ statusForm.orderId }}</strong></div>
      <el-form label-position="top">
        <el-form-item label="当前状态">
          <el-tag :type="getOrderStatus(statusForm.currentStatus).type" effect="plain">{{ statusForm.currentStatus }}</el-tag>
        </el-form-item>
        <el-form-item label="调整为" required>
          <el-select v-model="statusForm.status" style="width: 100%">
            <el-option v-for="item in orderStatuses" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="statusForm.status === '已取消'" label="取消原因" required>
          <el-input v-model="statusForm.cancelReason" type="textarea" :rows="3" maxlength="200" show-word-limit placeholder="请填写取消原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="statusVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="saveStatus">确认修改</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.page-heading {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 18px;
  margin-bottom: 20px;
}

.page-heading p {
  margin: 0 0 4px;
  color: #1683b8;
  font-size: 10px;
  font-weight: 700;
  letter-spacing: 1.5px;
}

.page-heading h2 {
  margin: 0;
  color: #102f49;
  font-size: 25px;
}

.page-heading span {
  display: block;
  margin-top: 7px;
  color: #7b8e9d;
  font-size: 13px;
}

.filter-card,
.table-card {
  border-color: #e0eaf0;
  box-shadow: 0 8px 24px rgba(21, 72, 103, 0.045);
}

.filter-card {
  margin-bottom: 16px;
}

.filter-form {
  display: flex;
  flex-wrap: wrap;
  gap: 2px 0;
}

.filter-form :deep(.el-form-item) {
  margin-right: 18px;
  margin-bottom: 4px;
}

.filter-form :deep(.el-input) {
  width: 210px;
}

.filter-form :deep(.el-select) {
  width: 145px;
}

.filter-actions {
  margin-left: auto;
  margin-right: 0 !important;
}

.table-card :deep(.el-card__body) {
  padding: 0 18px 18px;
}

.table-summary {
  display: flex;
  min-height: 66px;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.table-summary strong,
.table-summary span {
  display: inline-block;
}

.table-summary strong {
  color: #19374e;
  font-size: 16px;
}

.table-summary span {
  margin-left: 10px;
  color: #8a9ba8;
  font-size: 12px;
}

.order-no {
  color: #33576f;
  font-family: Consolas, monospace;
  font-size: 12px;
}

.money {
  color: #e26e34;
  font-size: 13px;
}

.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  padding-top: 20px;
  overflow-x: auto;
}

.detail-body {
  min-height: 220px;
}

.detail-banner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 15px;
  margin-bottom: 22px;
  padding: 18px;
  border-radius: 13px;
  color: #fff;
  background: linear-gradient(125deg, #0c395c, #1479b8);
}

.detail-banner span,
.detail-banner strong {
  display: block;
}

.detail-banner span {
  margin-bottom: 5px;
  color: #a9d2e5;
  font-size: 11px;
}

.detail-banner strong {
  font-family: Consolas, monospace;
  font-size: 14px;
}

.status-order-info {
  margin-bottom: 20px;
  padding: 12px 14px;
  border-radius: 9px;
  color: #677f91;
  background: #f0f6f9;
  font-size: 13px;
}

@media (max-width: 760px) {
  .page-heading span {
    display: none;
  }

  .filter-form,
  .filter-form :deep(.el-form-item),
  .filter-form :deep(.el-input),
  .filter-form :deep(.el-select) {
    width: 100%;
  }

  .filter-form :deep(.el-form-item) {
    margin-right: 0;
  }

  .filter-actions {
    margin-left: 0;
  }

  .table-card :deep(.el-card__body) {
    padding: 0 10px 14px;
  }

  .pagination-wrap {
    justify-content: flex-start;
  }
}
</style>
