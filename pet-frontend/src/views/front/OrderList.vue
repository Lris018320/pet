<template>
  <div class="order-page">
    <div class="page-shell">
      <h2>我的订单</h2>
      <div class="status-tabs">
        <el-radio-group v-model="status" @change="loadOrders">
          <el-radio-button label="">全部</el-radio-button>
          <el-radio-button label="pending">待付款</el-radio-button>
          <el-radio-button label="paid">已付款</el-radio-button>
          <el-radio-button label="shipped">已发货</el-radio-button>
          <el-radio-button label="completed">已完成</el-radio-button>
          <el-radio-button label="cancelled">已取消</el-radio-button>
        </el-radio-group>
      </div>

      <div v-loading="loading">
        <div v-for="order in orders" :key="order.id" class="order-card" @click="goDetail(order.id)">
          <div class="order-head">
            <span>订单号：{{ order.orderNo }}</span>
            <span class="order-time">{{ order.createTime }}</span>
            <span class="order-status" :class="order.status">{{ statusText(order.status) }}</span>
          </div>
          <div class="order-foot">
            <span>共 1 件商品</span>
            <span class="total">¥{{ order.totalAmount }}</span>
          </div>
          <div class="order-actions" @click.stop>
            <el-button v-if="order.status === 'pending'" size="small" @click="pay(order.id)">去支付</el-button>
            <el-button v-if="order.status === 'pending'" size="small" type="danger" @click="cancel(order.id)">取消</el-button>
            <el-button v-if="order.status === 'shipped'" size="small" type="primary" @click="confirm(order.id)">确认收货</el-button>
          </div>
        </div>
        <el-empty v-if="!loading && orders.length === 0" description="暂无订单" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { myOrders, payOrder, cancelOrder, confirmOrder } from '@/api/mall'

const router = useRouter()
const orders = ref([])
const status = ref('')
const loading = ref(false)

const statusMap = {
  pending: '待付款', paid: '已付款', shipped: '已发货',
  completed: '已完成', cancelled: '已取消'
}
function statusText(s) { return statusMap[s] || s }

async function loadOrders() {
  loading.value = true
  try {
    const res = await myOrders({ page: 1, size: 50, status: status.value })
    orders.value = res.data.list || []
  } catch (e) { ElMessage.error('加载失败') }
  finally { loading.value = false }
}

function goDetail(id) { router.push(`/order/${id}`) }

async function pay(id) {
  try {
    await payOrder(id)
    ElMessage.success('支付成功')
    loadOrders()
  } catch (e) {}
}

async function cancel(id) {
  try {
    await ElMessageBox.confirm('确定取消该订单吗？', '提示', { type: 'warning' })
    await cancelOrder(id)
    ElMessage.success('已取消')
    loadOrders()
  } catch (e) {}
}

async function confirm(id) {
  try {
    await confirmOrder(id)
    ElMessage.success('已确认收货')
    loadOrders()
  } catch (e) {}
}

onMounted(loadOrders)
</script>

<style scoped>
.order-page { min-height: 100%; background: #f5f7fa; }
.page-shell { padding: 18px clamp(16px, 2vw, 32px) 40px; max-width: 800px; margin: 0 auto; }
.page-shell h2 { margin: 0 0 16px; }
.status-tabs { margin-bottom: 16px; }
.order-card {
  background: #fff; border-radius: 12px; padding: 16px; margin-bottom: 12px; cursor: pointer;
  box-shadow: 0 2px 8px rgba(0,0,0,.06);
}
.order-head { display: flex; justify-content: space-between; align-items: center; font-size: 13px; color: #909399; margin-bottom: 12px; }
.order-time { flex: 1; text-align: center; }
.order-status { font-weight: 600; }
.order-status.pending { color: #e6a23c; }
.order-status.paid { color: #409eff; }
.order-status.shipped { color: #67c23a; }
.order-status.completed { color: #909399; }
.order-status.cancelled { color: #c0c4cc; }
.order-foot { display: flex; justify-content: space-between; padding-top: 12px; border-top: 1px solid #f0f0f0; }
.order-foot .total { color: #f56c6c; font-weight: 600; }
.order-actions { display: flex; justify-content: flex-end; gap: 8px; margin-top: 12px; }
</style>
