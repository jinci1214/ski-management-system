import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import { loginAdmin, loginUser, logoutAdmin, logoutUser } from '../api/auth'

const readProfile = (key) => {
  try {
    return JSON.parse(localStorage.getItem(key) || 'null')
  } catch {
    return null
  }
}

export const useAuthStore = defineStore('auth', () => {
  const userToken = ref(localStorage.getItem('ski_user_token') || '')
  const adminToken = ref(localStorage.getItem('ski_admin_token') || '')
  const user = ref(readProfile('ski_user_profile'))
  const admin = ref(readProfile('ski_admin_profile'))

  const isUserLoggedIn = computed(() => Boolean(userToken.value))
  const isAdminLoggedIn = computed(() => Boolean(adminToken.value))

  const saveSession = (data, role) => {
    const tokenKey = `ski_${role}_token`
    const profileKey = `ski_${role}_profile`
    localStorage.setItem(tokenKey, data.token)
    localStorage.setItem(profileKey, JSON.stringify(data))
    if (role === 'admin') {
      adminToken.value = data.token
      admin.value = data
    } else {
      userToken.value = data.token
      user.value = data
    }
  }

  const userLogin = async (form) => {
    const data = await loginUser(form)
    saveSession(data, 'user')
    return data
  }

  const adminLogin = async (form) => {
    const data = await loginAdmin(form)
    saveSession(data, 'admin')
    return data
  }

  const clearSession = (role) => {
    localStorage.removeItem(`ski_${role}_token`)
    localStorage.removeItem(`ski_${role}_profile`)
    if (role === 'admin') {
      adminToken.value = ''
      admin.value = null
    } else {
      userToken.value = ''
      user.value = null
    }
  }

  const updateUserProfile = (profile) => {
    const updated = {
      ...(user.value || {}),
      id: profile.id,
      userName: profile.username,
      name: profile.name,
      phone: profile.phone,
      avatar: profile.avatar
    }
    localStorage.setItem('ski_user_profile', JSON.stringify(updated))
    user.value = updated
  }

  const logout = async (role) => {
    try {
      if (role === 'admin' && isAdminLoggedIn.value) await logoutAdmin()
      if (role === 'user' && isUserLoggedIn.value) await logoutUser()
    } finally {
      clearSession(role)
    }
  }

  return {
    userToken,
    adminToken,
    user,
    admin,
    isUserLoggedIn,
    isAdminLoggedIn,
    userLogin,
    adminLogin,
    updateUserProfile,
    logout,
    clearSession
  }
})
