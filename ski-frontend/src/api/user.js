import request from '../utils/request'

export const buyTicket = (ticketId) => request.post(`/user/ticket/buy/${ticketId}`)
export const buyTicketWithEquipment = (data) => request.post('/user/ticket/buy-with-equipment', data)
export const rentEquipment = (equipmentId) => request.post(`/user/equipment/rent/${equipmentId}`)
export const joinCourse = (courseId) => request.post(`/user/course/join/${courseId}`)

export const getMyOrders = () => request.get('/user/orders/my')
export const getOrderDetail = (id) => request.get(`/user/orders/${id}`)
export const payOrder = (id, paymentMethod = '微信') =>
  request.put(`/user/orders/${id}/pay`, null, { params: { paymentMethod } })
export const payOrders = (orderIds, paymentMethod = '微信') =>
  request.put('/user/orders/pay-batch', { orderIds, paymentMethod })
export const cancelOrder = (id, cancelReason = '行程变化') =>
  request.put(`/user/orders/${id}/cancel`, null, { params: { cancelReason } })
export const returnOrder = (id) => request.put(`/user/orders/${id}/return`)

export const createFeedback = (data) => request.post('/user/feedback', data)
export const getMyFeedback = () => request.get('/user/feedback/my')
export const markMyFeedbackRepliesRead = () => request.put('/user/feedback/my/read')
