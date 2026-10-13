import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

const request = axios.create({
  baseURL: '/api',
  timeout: 15000
})

// 请求拦截器：自动携带 token
request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = 'Bearer ' + token
    }
    return config
  },
  (error) => Promise.reject(error)
)

// 响应拦截器：统一处理 code/异常
request.interceptors.response.use(
  (response) => {
    const res = response.data
    if (res.code === 200) {
      return res
    }
    // 业务异常
    ElMessage.error(res.message || '请求失败')
    // 401 未登录/token 失效
    if (res.code === 40101 || res.code === 40102) {
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      const path = window.location.pathname
      if (path.startsWith('/merchant')) {
        router.push('/merchant/login')
      } else {
        router.push('/login')
      }
    }
    return Promise.reject(new Error(res.message || '请求失败'))
  },
  (error) => {
    // HTTP 异常
    let msg = '网络异常，请稍后重试'
    if (error.response) {
      const status = error.response.status
      if (status === 401) {
        msg = '未登录或登录已失效'
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        const path = window.location.pathname
        if (path.startsWith('/merchant')) {
          router.push('/merchant/login')
        } else {
          router.push('/login')
        }
      } else if (status === 403) {
        msg = '无访问权限'
      } else if (status === 500) {
        msg = '服务器异常'
      }
    } else if (error.message && error.message.includes('timeout')) {
      msg = '请求超时'
    }
    ElMessage.error(msg)
    return Promise.reject(error)
  }
)

export default request
