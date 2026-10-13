<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon goods">📦</div>
            <div class="stat-info">
              <div class="stat-label">商品总数</div>
              <div class="stat-value">{{ stats.productCount || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon order">📋</div>
            <div class="stat-info">
              <div class="stat-label">订单总数</div>
              <div class="stat-value">{{ stats.orderCount || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon revenue">💰</div>
            <div class="stat-info">
              <div class="stat-label">营业额（已完成）</div>
              <div class="stat-value">¥{{ formatMoney(stats.revenue) }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never" class="welcome-card">
      <div class="welcome">
        <h2>欢迎回来，{{ nickname }}！</h2>
        <p>在这里管理您的商品和订单，为宠物爱好者提供优质服务。</p>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { getMerchantDashboard } from '@/api/merchant'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const stats = ref({})
const nickname = computed(() => userStore.userInfo?.nickname || '商家')

function formatMoney(val) {
  if (!val) return '0.00'
  return Number(val).toFixed(2)
}

async function loadDashboard() {
  const res = await getMerchantDashboard()
  stats.value = res.data || {}
}

onMounted(loadDashboard)
</script>

<style scoped>
.dashboard { max-width: 1100px; }
.stat-card { margin-bottom: 20px; }
.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
}
.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
}
.stat-icon.goods { background: #ecf5ff; }
.stat-icon.order { background: #f0f9eb; }
.stat-icon.revenue { background: #fdf6ec; }
.stat-label {
  color: #909399;
  font-size: 14px;
}
.stat-value {
  font-size: 26px;
  font-weight: 600;
  color: #303133;
  margin-top: 4px;
}
.welcome-card { margin-top: 4px; }
.welcome h2 {
  margin: 0 0 8px;
  color: #2c3e50;
}
.welcome p {
  margin: 0;
  color: #909399;
}
</style>
