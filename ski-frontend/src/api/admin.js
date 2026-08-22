import request from '../utils/request'

const resourcePaths = {
  resort: '/admin/resort',
  skiRun: '/admin/ski-run',
  facility: '/admin/facility',
  ticket: '/admin/ticket',
  equipment: '/admin/equipment',
  course: '/admin/course',
  coach: '/admin/coach',
  weather: '/admin/weather',
  announcement: '/admin/announcement',
  maintenance: '/admin/maintenance',
  safetyInspection: '/admin/safety-inspection'
}

const pathOf = (resource) => {
  const path = resourcePaths[resource]
  if (!path) throw new Error(`未知资源：${resource}`)
  return path
}

export const getDashboardStatistics = () => request.get('/admin/dashboard/statistics')

export const getResourcePage = (resource, params) =>
  request.get(`${pathOf(resource)}/page`, { params })

export const getResourceDetail = (resource, id) =>
  request.get(`${pathOf(resource)}/${id}`)

export const createResource = (resource, data) =>
  request.post(pathOf(resource), data)

export const updateResource = (resource, id, data) => {
  if (resource === 'ticket') {
    const { name, skiRunId, hour, description } = data
    return request.put(`${pathOf(resource)}/${id}`, null, {
      params: { name, skiRunId, hour, description }
    })
  }
  return request.put(`${pathOf(resource)}/${id}`, data)
}

export const deleteResource = (resource, id) =>
  request.delete(`${pathOf(resource)}/${id}`)

export const getOrderPage = (params) => request.get('/admin/orders/page', { params })
export const getAdminOrderDetail = (id) => request.get(`/admin/orders/${id}`)
export const updateOrderStatus = (id, status, cancelReason) =>
  request.put(`/admin/orders/${id}/status`, null, { params: { status, cancelReason } })

export const getFeedbackPage = (params) => request.get('/admin/feedback/page', { params })
export const replyFeedback = (id, reply) =>
  request.put(`/admin/feedback/${id}/reply`, null, { params: { reply } })
export const deleteFeedback = (id) => request.delete(`/admin/feedback/${id}`)

export const getEmployeePage = (params) => request.get('/admin/employee/page', { params })
export const createEmployee = (data) => request.post('/admin/employee', data)
export const updateEmployee = (id, data) => request.put(`/admin/employee/${id}`, data)
export const deleteEmployee = (id) => request.delete(`/admin/employee/${id}`)
export const updateEmployeeStatus = (id, status) =>
  request.put(`/admin/employee/${id}/status/${status}`)

export const uploadImage = (file) => {
  const data = new FormData()
  data.append('file', file)
  return request.post('/admin/upload/image', data)
}

export const getAllCourses = () => request.get('/admin/course')
export const getAllEquipment = () => request.get('/admin/equipment')
