import { defineStore } from 'pinia'
import { ref } from 'vue'
import * as authApi from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))

  function setLogin(loginVO) {
    token.value = loginVO.token
    userInfo.value = {
      id: loginVO.id,
      account: loginVO.account,
      nickname: loginVO.nickname,
      avatar: loginVO.avatar,
      role: loginVO.role
    }
    localStorage.setItem('token', loginVO.token)
    localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
  }

  function clearLogin() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  async function fetchCurrentUser() {
    const res = await authApi.getCurrentUser()
    if (res.data) {
      userInfo.value = { ...userInfo.value, ...res.data }
      localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
    }
    return res.data
  }

  async function logout() {
    try {
      await authApi.logout()
    } finally {
      clearLogin()
    }
  }

  return {
    token,
    userInfo,
    setLogin,
    clearLogin,
    fetchCurrentUser,
    logout
  }
})
