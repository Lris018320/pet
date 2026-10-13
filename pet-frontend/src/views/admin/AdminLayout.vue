<template>
  <el-container class="admin-layout">
    <el-aside :width="isCollapse ? '64px' : '220px'" class="aside">
      <div class="logo-bar">
        <span class="logo">
          <svg viewBox="0 0 48 48">
            <ellipse cx="24" cy="30" rx="11" ry="9" />
            <circle cx="12" cy="20" r="4.6" />
            <circle cx="21" cy="13.5" r="4.6" />
            <circle cx="27" cy="13.5" r="4.6" />
            <circle cx="36" cy="20" r="4.6" />
          </svg>
        </span>
        <span v-show="!isCollapse" class="brand">宠知AI</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :collapse-transition="false"
        background-color="transparent"
        text-color="#b3aede"
        active-text-color="#ffffff"
        router
      >
        <el-menu-item index="/admin/dashboard">
          <el-icon><DataLine /></el-icon>
          <template #title>数据看板</template>
        </el-menu-item>
        <el-menu-item index="/admin/user">
          <el-icon><User /></el-icon>
          <template #title>用户管理</template>
        </el-menu-item>
        <el-menu-item index="/admin/pet">
          <el-icon><svg viewBox="0 0 24 24" fill="currentColor" style="width:1em;height:1em"><path d="M12 13.5c-2.9 0-5.5 2.3-5.5 4.6 0 1.5 1.1 2.4 2.6 2.4 1.1 0 1.9-.5 2.9-.5s1.8.5 2.9.5c1.5 0 2.6-.9 2.6-2.4 0-2.3-2.6-4.6-5.5-4.6zM6.4 8.2c-1.2.3-2 1.6-1.8 2.9.2 1.2 1.3 2 2.5 1.8 1.2-.3 2-1.6 1.8-2.9-.2-1.2-1.3-2-2.5-1.8zm11.2 0c-1.2-.2-2.3.6-2.5 1.8-.2 1.3.6 2.6 1.8 2.9 1.2.2 2.3-.6 2.5-1.8.2-1.3-.6-2.6-1.8-2.9zM9.3 3.6c-1.1.2-1.9 1.4-1.7 2.7.2 1.2 1.2 2.1 2.4 1.9 1.1-.2 1.9-1.4 1.7-2.7-.2-1.2-1.3-2.1-2.4-1.9zm5.4 0c-1.1-.2-2.2.7-2.4 1.9-.2 1.3.6 2.5 1.7 2.7 1.2.2 2.2-.7 2.4-1.9.2-1.3-.6-2.5-1.7-2.7z"/></svg></el-icon>
          <template #title>宠物管理</template>
        </el-menu-item>
        <el-menu-item index="/admin/info">
          <el-icon><Document /></el-icon>
          <template #title>资讯管理</template>
        </el-menu-item>
        <el-menu-item index="/admin/knowledge">
          <el-icon><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" style="width:1em;height:1em"><path d="M4 4h12a3 3 0 0 1 3 3v13H7a3 3 0 0 1-3-3V4z"/><path d="M4 17h15M8 8h6M8 11h4"/></svg></el-icon>
          <template #title>知识记录</template>
        </el-menu-item>
        <el-menu-item index="/admin/product">
          <el-icon><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" style="width:1em;height:1em"><path d="M3 3h18v4H3zM3 7l3 14h12l3-14M9 11h6"/></svg></el-icon>
          <template #title>商城管理</template>
        </el-menu-item>
        <el-menu-item index="/admin/adoption">
          <el-icon><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" style="width:1em;height:1em"><path d="M19 14c1.5-1.5 3-3.7 3-6a4 4 0 0 0-7-2.7A4 4 0 0 0 5 8c0 2.3 1.5 4.5 3 6l4 4z"/><path d="M12 18v4M9 22h6"/></svg></el-icon>
          <template #title>领养寄养</template>
        </el-menu-item>
        <el-menu-item index="/admin/community">
          <el-icon><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" style="width:1em;height:1em"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87M16 3.13a4 4 0 0 1 0 7.75"/></svg></el-icon>
          <template #title>社交管理</template>
        </el-menu-item>
        <el-menu-item index="/admin/recognition">
          <el-icon><PictureFilled /></el-icon>
          <template #title>识别记录</template>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <div class="header-left">
          <el-icon class="collapse-btn" @click="isCollapse = !isCollapse">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
          <span class="page-title">{{ currentTitle }}</span>
        </div>
        <div class="header-right">
          <el-dropdown @command="onCommand">
            <span class="user-trigger">
              <el-avatar :size="32">{{ (userStore.userInfo?.account || 'A').charAt(0).toUpperCase() }}</el-avatar>
              <span class="nickname">{{ userStore.userInfo?.account }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="home">前台首页</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  User, Document, DataLine, PictureFilled, Fold, Expand,
  House, ChatDotRound, Goods, List
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const isCollapse = ref(false)

const activeMenu = computed(() => route.path)
const currentTitle = computed(() => route.meta.title || '管理后台')

async function onCommand(command) {
  if (command === 'logout') {
    try {
      await ElMessageBox.confirm('确定退出登录吗？', '提示', { type: 'warning' })
    } catch {
      return
    }
    await userStore.logout()
    ElMessage.success('已退出登录')
    router.push('/admin/login')
  } else if (command === 'home') {
    router.push('/home')
  }
}
</script>

<style scoped>
.admin-layout {
  min-height: 100vh;
}
.aside {
  background: linear-gradient(180deg, #353162 0%, #2b2848 100%);
  transition: width 0.2s;
  overflow: hidden;
}
.logo-bar {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  color: #fff;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}
.logo {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: linear-gradient(135deg, #6d7bf0, #6a5bd8);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 4px 10px rgba(106, 91, 216, 0.3);
}
.logo svg {
  width: 21px;
  height: 21px;
  fill: #fff;
}
.brand {
  font-size: 19px;
  font-weight: 700;
  letter-spacing: 0.5px;
  white-space: nowrap;
}
.aside :deep(.el-menu) {
  border-right: none;
}
.aside :deep(.el-menu-item) {
  margin: 3px 10px;
  border-radius: 8px;
}
.aside :deep(.el-menu-item:hover) {
  background: rgba(255, 255, 255, 0.07);
}
.aside :deep(.el-menu-item.is-active) {
  background: linear-gradient(90deg, #6d7bf0, #6a5bd8);
  color: #fff;
  box-shadow: 0 4px 12px rgba(106, 91, 216, 0.35);
}
.header {
  background: #fff;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
}
.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}
.collapse-btn {
  font-size: 22px;
  cursor: pointer;
  color: #606266;
}
.page-title {
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
}
.user-trigger {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  outline: none;
}
.nickname {
  color: #2c3e50;
  font-size: 14px;
}
.main {
  background: #f5f7fa;
  padding: 20px;
}
</style>
