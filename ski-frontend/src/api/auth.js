import request from '../utils/request'

export const loginUser = (data) => request.post('/user/user/login', data)
export const registerUser = (data) => request.post('/user/user/register', data)
export const getUserProfile = () => request.get('/user/user/profile')
export const updateUserProfile = (data) => request.put('/user/user/profile', data)
export const updateUsername = (data) => request.put('/user/user/profile/username', data)
export const updatePassword = (data) => request.put('/user/user/profile/password', data)
export const uploadUserAvatar = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/user/user/profile/avatar', formData)
}
export const logoutUser = () => request.post('/user/user/logout')

export const loginAdmin = (data) => request.post('/admin/employee/login', data)
export const logoutAdmin = () => request.post('/admin/employee/logout')
