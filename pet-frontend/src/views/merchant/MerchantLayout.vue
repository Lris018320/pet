<template>
  <el-container class="merchant-layout">
    <el-aside :width="isCollapse ? '64px' : '220px'" class="aside">
      <div class="logo-bar">
        <span class="logo">🐾</span>
        <span v-show="!isCollapse" class="brand">商家后台</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :collapse-transition="false"
        background-color="#1e3a5f"
        text-color="#bfcbd9"
        active-text-color="#67c23a"
        router
      >
        <el-menu-item index="/merchant/dashboard">
          <el-icon><DataLine /></el-icon>
          <template #title>商家看板</template>
        </el-menu-item>
        <el-menu-item index="/merchant/product">
          <el-icon><Goods /></el-icon>
          <template #title>商品管理</template>
        </el-menu-item>
        <el-menu-item index="/merchant/order">
          <el-icon><List /></el-icon>
          <template #title>订单管理</template>
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
              <el-avatar :size="32">{{ (userStore.userInfo?.nickname || 'M').charAt(0).toUpperCase() }}</el-avatar>
              <span class="nickname">{{ userStore.userInfo?.nickname }}</span>
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
import { DataLine, Goods, List, Fold, Expand } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const isCollapse = ref(false)

const activeMenu = computed(() => route.path)
const currentTitle = computed(() => route.meta.title || '商家后台')

async function onCommand(command) {
  if (command === 'logout') {
    try {
      await ElMessageBox.confirm('确定退出登录吗？', '提示', { type: 'warning' })
    } catch {
      return
    }
    userStore.clearLogin()
    ElMessage.success('已退出登录')
    router.push('/merchant/login')
  } else if (command === 'home') {
    router.push('/home')
  }
}
</script>

<style scoped>
.merchant-layout { min-height: 100vh; }
.aside {
  background: #1e3a5f;
  transition: width 0.2s;
  overflow: hidden;
}
.logo-bar {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #fff;
  border-bottom: 1px solid #2c4a6f;
}
.logo { font-size: 28px; }
.brand {
  font-size: 18px;
  font-weight: 600;
  white-space: nowrap;
}
.aside :deep(.el-menu) { border-right: none; }
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
