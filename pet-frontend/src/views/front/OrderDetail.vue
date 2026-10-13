<template>
  <div class="order-detail" v-loading="loading">
    <div class="page-shell" v-if="order">
      <el-button text @click="$router.back()">‹ 返回</el-button>
      <div class="detail-card">
        <div class="status-bar" :class="order.status">
          <span class="status-text">{{ statusText(order.status) }}</span>
        </div>

        <div class="section">
          <h3>收货信息</h3>
          <p>{{ order.receiverName }} {{ order.receiverPhone }}</p>
          <p>{{ order.receiverAddress }}</p>
        </div>

        <div class="section">
          <h3>商品清单</h3>
          <div v-for="item in items" :key="item.id" class="item-row">
            <span>{{ item.productName }}</span>
            <span>¥{{ item.price }} × {{ item.quantity }}</span>
            <span class="subtotal">¥{{ item.subtotal }}</span>
          </div>
          <div class="total-row">
            <span>订单总额</span>
            <span class="total-amount">¥{{ order.totalAmount }}</span>
          </div>
        </div>

        <div class="section">
          <h3>订单信息</h3>
          <p>订单号：{{ order.orderNo }}</p>
          <p>下单时间：{{ order.createTime }}</p>
          <p v-if="order.payTime">支付时间：{{ order.payTime }}</p>
          <p v-if="order.shipTime">发货时间：{{ order.shipTime }}</p>
          <p v-if="order.completeTime">完成时间：{{ order.completeTime }}</p>
        </div>

        <div class="actions">
          <el-button v-if="order.status === 'pending'" type="primary" @click="pay">去支付</el-button>
          <el-button v-if="order.status === 'pending'" type="danger" @click="cancel">取消订单</el-button>
          <el-button v-if="order.status === 'shipped'" type="primary" @click="confirm">确认收货</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrder, getOrderItems, payOrder, cancelOrder, confirmOrder } from '@/api/mall'

const route = useRoute()
const order = ref(null)
const items = ref([])
const loading = ref(false)

const statusMap = {
  pending: '待付款', paid: '已付款', shipped: '已发货',
  completed: '已完成', cancelled: '已取消'
}
function statusText(s) { return statusMap[s] || s }

async function loadDetail() {
  loading.value = true
  try {
    const [o, i] = await Promise.all([getOrder(route.params.id), getOrderItems(route.params.id)])
    order.value = o.data
    items.value = i.data || []
  } catch (e) { ElMessage.error('加载失败') }
  finally { loading.value = false }
}

async function pay() {
  try { await payOrder(route.params.id); ElMessage.success('支付成功'); loadDetail() } catch (e) {}
}
async function cancel() {
  try {
    await ElMessageBox.confirm('确定取消该订单吗？', '提示', { type: 'warning' })
    await cancelOrder(route.params.id); ElMessage.success('已取消'); loadDetail()
  } catch (e) {}
}
async function confirm() {
  try { await confirmOrder(route.params.id); ElMessage.success('已确认收货'); loadDetail() } catch (e) {}
}

onMounted(loadDetail)
</script>

<style scoped>
.order-detail { min-height: 100%; background: #f5f7fa; }
.page-shell { padding: 18px clamp(16px, 2vw, 32px) 40px; max-width: 700px; margin: 0 auto; }
.detail-card { background: #fff; border-radius: 16px; margin-top: 12px; overflow: hidden; }
.status-bar { padding: 20px; color: #fff; text-align: center; font-size: 20px; font-weight: 600; }
.status-bar.pending { background: #e6a23c; }
.status-bar.paid { background: #409eff; }
.status-bar.shipped { background: #67c23a; }
.status-bar.completed { background: #909399; }
.status-bar.cancelled { background: #c0c4cc; }
.section { padding: 20px; border-bottom: 1px solid #f0f0f0; }
.section h3 { margin: 0 0 12px; font-size: 15px; }
.section p { margin: 4px 0; color: #606266; }
.item-row { display: flex; justify-content: space-between; padding: 8px 0; font-size: 14px; }
.item-row .subtotal { color: #f56c6c; }
.total-row { display: flex; justify-content: flex-end; gap: 12px; padding-top: 12px; font-size: 16px; }
.total-amount { color: #f56c6c; font-weight: 600; }
.actions { padding: 20px; display: flex; justify-content: flex-end; gap: 10px; }
</style>
