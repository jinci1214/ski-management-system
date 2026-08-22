import dayjs from 'dayjs'

export const formatDate = (value, template = 'YYYY-MM-DD') => {
  if (!value) return '—'
  const date = dayjs(value)
  return date.isValid() ? date.format(template) : value
}

export const formatMoney = (value) => `¥${Number(value || 0).toFixed(2)}`

export const orderStatusMap = {
  待支付: { type: 'warning', label: '待支付' },
  已支付: { type: 'primary', label: '已支付' },
  已完成: { type: 'success', label: '已完成' },
  已取消: { type: 'info', label: '已取消' },
  已退款: { type: 'danger', label: '已退款' }
}

export const getOrderStatus = (status) =>
  orderStatusMap[status] || { type: 'info', label: status || '未知' }

export const statusText = (status) => (Number(status) === 1 ? '启用' : '停用')

