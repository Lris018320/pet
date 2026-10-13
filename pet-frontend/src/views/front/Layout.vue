<template>
  <el-container class="layout">
    <el-header class="header">
      <div class="header-inner">
        <div class="brand" @click="router.push('/home')">
          <span class="brand-logo">
            <svg viewBox="0 0 48 48">
              <ellipse cx="24" cy="30" rx="11" ry="9" />
              <circle cx="12" cy="20" r="4.6" />
              <circle cx="21" cy="13.5" r="4.6" />
              <circle cx="27" cy="13.5" r="4.6" />
              <circle cx="36" cy="20" r="4.6" />
            </svg>
          </span>
          <span class="brand-text">宠知AI</span>
        </div>
        <el-menu
          :default-active="activeMenu"
          mode="horizontal"
          class="nav"
          router
          :ellipsis="false"
        >
          <el-menu-item index="/home">首页</el-menu-item>
          <el-menu-item index="/recognition">AI 识别</el-menu-item>
          <el-menu-item index="/archive">我的档案</el-menu-item>
          <el-menu-item index="/info">宠物资讯</el-menu-item>
          <el-menu-item index="/my-records">我的记录</el-menu-item>
          <el-menu-item index="/mall">商城</el-menu-item>
          <el-menu-item index="/adoption">领养寄养</el-menu-item>
          <el-menu-item index="/community">社区</el-menu-item>
        </el-menu>
        <div class="user-area">
          <template v-if="userStore.token">
            <el-dropdown @command="onCommand">
              <span class="user-trigger">
                <el-avatar :size="34" :src="resolveAvatar(userStore.userInfo?.avatar)">
                  {{ (userStore.userInfo?.nickname || 'U').charAt(0).toUpperCase() }}
                </el-avatar>
                <span class="nickname">{{ userStore.userInfo?.nickname }}</span>
                <el-icon class="caret"><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item v-if="userStore.userInfo?.role === 'admin'" divided command="admin">
                    <el-icon><DataLine /></el-icon> 管理后台
                  </el-dropdown-item>
                  <el-dropdown-item command="profile">
                    <el-icon><User /></el-icon> 个人中心
                  </el-dropdown-item>
                  <el-dropdown-item command="my-records">
                    <el-icon><Clock /></el-icon> 我的记录
                  </el-dropdown-item>
                  <el-dropdown-item command="order">
                    <el-icon><List /></el-icon> 我的订单
                  </el-dropdown-item>
                  <el-dropdown-item command="cart">
                    <el-icon><ShoppingCart /></el-icon> 购物车
                  </el-dropdown-item>
                  <el-dropdown-item divided command="logout">
                    <el-icon><SwitchButton /></el-icon> 退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
            <span class="setting-btn" title="个人设置" @click="router.push('/profile')">
              <el-icon><Setting /></el-icon>
            </span>
          </template>
          <template v-else>
            <el-button plain @click="router.push('/login')">登录</el-button>
            <el-button type="primary" @click="router.push('/register')">免费注册</el-button>
          </template>
        </div>
      </div>
    </el-header>
    <el-main class="main">
      <router-view />
    </el-main>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowDown, User, Clock, SwitchButton, Setting, DataLine } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)

const API_BASE = import.meta.env.VITE_API_BASE || '/api'
function resolveAvatar(url) {
  if (!url) return ''
  if (url.startsWith('http')) return url
  return API_BASE + url
}

async function onCommand(command) {
  if (command === 'logout') {
    try {
      await ElMessageBox.confirm('确定退出登录吗？', '提示', {
        type: 'warning'
      })
    } catch {
      return
    }
    await userStore.logout()
    ElMessage.success('已退出登录')
    router.push('/home')
  } else if (command === 'admin') {
    router.push('/admin/dashboard')
  } else if (command === 'profile') {
    router.push('/profile')
  } else if (command === 'my-records') {
    router.push('/my-records')
  } else if (command === 'order') {
    router.push('/order')
  } else if (command === 'cart') {
    router.push('/cart')
  }
}
</script>

<style scoped>
.layout {
  min-height: 100vh;
  background: #fff;
}
.header {
  background: #fff;
  border-bottom: none;
  padding: 0;
  height: 64px;
  line-height: 64px;
  position: sticky;
  top: 0;
  z-index: 100;
  box-shadow: 0 1px 8px rgba(0, 0, 0, 0.04);
}
.header-inner {
  width: 100%;
  margin: 0;
  height: 100%;
  display: flex;
  align-items: center;
  padding: 0 clamp(16px, 2vw, 32px);
}
.brand {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  margin-right: 32px;
  flex-shrink: 0;
}
.brand-logo {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: linear-gradient(135deg, #6d7bf0, #6a5bd8);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 10px rgba(106, 91, 216, 0.3);
}
.brand-logo svg {
  width: 21px;
  height: 21px;
  fill: #fff;
}
.brand-text {
  font-size: 19px;
  font-weight: 700;
  color: #202a4a;
  letter-spacing: 0.5px;
}
.setting-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  color: #5b6480;
  cursor: pointer;
  font-size: 18px;
  transition: all 0.2s;
}
.setting-btn:hover {
  background: #f2f4fa;
  color: #6d5ce0;
  transform: rotate(35deg);
}
.nav {
  flex: 1;
  min-width: 0;
  border-bottom: none !important;
  background: transparent !important;
  overflow-x: auto;
  scrollbar-width: none;
}
.nav::-webkit-scrollbar {
  display: none;
}
.nav :deep(.el-menu-item) {
  height: 64px;
  line-height: 64px;
  font-size: 15px;
  font-weight: 500;
  color: #606266;
  border-bottom: 3px solid transparent !important;
}
.nav :deep(.el-menu-item.is-active) {
  color: #667eea;
  border-bottom-color: #667eea !important;
  background: transparent !important;
}
.nav :deep(.el-menu-item:hover) {
  color: #667eea;
  background: rgba(102, 126, 234, 0.06) !important;
}
.user-area {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-left: 16px;
}
.user-trigger {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  outline: none;
  padding: 4px 12px 4px 4px;
  border-radius: 999px;
  transition: background 0.2s;
}
.user-trigger:hover {
  background: #f5f7fa;
}
.nickname {
  font-size: 14px;
  color: #2c3e50;
  font-weight: 500;
}
.caret {
  font-size: 12px;
  color: #909399;
}
.main {
  padding: 0;
}

/* 窄屏防护：避免顶部内容撑出横向滚动 */
.user-area {
  flex-shrink: 0;
}
@media (max-width: 860px) {
  .brand-text,
  .nickname,
  .caret {
    display: none;
  }
  .brand {
    margin-right: 14px;
  }
  .nav :deep(.el-menu-item) {
    padding: 0 14px;
  }
}
</style>
