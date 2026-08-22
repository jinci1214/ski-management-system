import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: '/api',
  timeout: 12000
})

request.interceptors.request.use((config) => {
  const isAdminRequest = config.url?.startsWith('/admin')
  const tokenKey = isAdminRequest ? 'ski_admin_token' : 'ski_user_token'
  const token = localStorage.getItem(tokenKey)
  if (token) config.headers.token = token
  return config
})

request.interceptors.response.use(
  (response) => {
    const result = response.data
    if (result && typeof result.code !== 'undefined') {
      if (result.code === 1) return result.data
      ElMessage.error(result.msg || '操作失败，请稍后重试')
      return Promise.reject(new Error(result.msg || '请求失败'))
    }
    return result
  },
  (error) => {
    const status = error.response?.status
    if (status === 401 || status === 403) {
      const isAdminRequest = error.config?.url?.startsWith('/admin')
      const prefix = isAdminRequest ? 'ski_admin' : 'ski_user'
      localStorage.removeItem(`${prefix}_token`)
      localStorage.removeItem(`${prefix}_profile`)
      const target = isAdminRequest ? '/admin/login' : '/login'
      if (window.location.pathname !== target) window.location.href = target
      ElMessage.warning('登录状态已失效，请重新登录')
    } else {
      ElMessage.error(error.response?.data?.msg || error.message || '网络连接异常')
    }
    return Promise.reject(error)
  }
)

export default request

