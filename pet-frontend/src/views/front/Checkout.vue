<template>
  <div class="checkout-page">
    <div class="page-shell">
      <h2>确认订单</h2>
      <div class="checkout-card">
        <h3>收货信息</h3>
        <el-form :model="form" label-width="90px">
          <el-form-item label="收件人">
            <el-input v-model="form.receiverName" />
          </el-form-item>
          <el-form-item label="电话">
            <el-input v-model="form.receiverPhone" />
          </el-form-item>
          <el-form-item label="地址">
            <el-input v-model="form.receiverAddress" type="textarea" :rows="2" />
          </el-form-item>
        </el-form>
      </div>

      <div class="checkout-card">
        <h3>商品清单</h3>
        <div v-for="item in items" :key="item.productId" class="order-item">
          <img :src="item.coverImage" class="item-img" />
          <div class="item-info">
            <h4>{{ item.name }}</h4>
            <p>¥{{ item.price }} × {{ item.quantity }}</p>
          </div>
          <span class="item-subtotal">¥{{ (item.price * item.quantity).toFixed(2) }}</span>
        </div>
        <div class="total-row">
          <span>合计</span>
          <span class="total-amount">¥{{ totalAmount.toFixed(2) }}</span>
        </div>
      </div>

      <el-button type="primary" size="large" class="submit-btn" @click="submitOrder">提交订单</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getCart, getProduct, createOrder } from '@/api/mall'

const route = useRoute()
const router = useRouter()

const form = reactive({ receiverName: '', receiverPhone: '', receiverAddress: '' })
const items = ref([])
const cartIds = ref([])

const totalAmount = computed(() => items.value.reduce((s, i) => s + i.price * i.quantity, 0))

async function loadItems() {
  if (route.query.productId) {
    // 立即购买
    const p = await getProduct(route.query.productId)
    items.value = [{
      productId: p.data.id, name: p.data.name, price: p.data.price,
      coverImage: p.data.coverImage, quantity: parseInt(route.query.quantity) || 1
    }]
  } else if (route.query.cartIds) {
    // 从购物车
    cartIds.value = route.query.cartIds.split(',').map(Number)
    const cartRes = await getCart()
    const selected = cartRes.data.filter(c => cartIds.value.includes(c.id))
    for (const c of selected) {
      const p = await getProduct(c.productId)
      items.value.push({
        productId: p.data.id, name: p.data.name, price: p.data.price,
        coverImage: p.data.coverImage, quantity: c.quantity
      })
    }
  }
}

async function submitOrder() {
  if (!form.receiverName || !form.receiverPhone || !form.receiverAddress) {
    ElMessage.warning('请填写完整收货信息')
    return
  }
  try {
    const payload = { ...form }
    if (route.query.productId) {
      payload.productId = parseInt(route.query.productId)
      payload.quantity = parseInt(route.query.quantity) || 1
    } else {
      payload.cartIds = cartIds.value
    }
    const res = await createOrder(payload)
    ElMessage.success('订单创建成功')
    router.push(`/order/${res.data}`)
  } catch (e) {}
}

onMounted(loadItems)
</script>

<style scoped>
.checkout-page { min-height: 100%; background: #f5f7fa; }
.page-shell { padding: 18px clamp(16px, 2vw, 32px) 40px; max-width: 700px; margin: 0 auto; }
.page-shell h2 { margin: 0 0 16px; }
.checkout-card { background: #fff; border-radius: 12px; padding: 20px; margin-bottom: 16px; }
.checkout-card h3 { margin: 0 0 16px; font-size: 16px; }
.order-item { display: flex; align-items: center; gap: 12px; padding: 10px 0; border-bottom: 1px solid #f0f0f0; }
.order-item:last-of-type { border-bottom: none; }
.item-img { width: 50px; height: 50px; border-radius: 6px; object-fit: cover; }
.item-info { flex: 1; }
.item-info h4 { margin: 0 0 4px; font-size: 14px; }
.item-info p { margin: 0; color: #909399; font-size: 13px; }
.item-subtotal { color: #f56c6c; font-weight: 600; }
.total-row { display: flex; justify-content: flex-end; gap: 12px; padding-top: 12px; font-size: 16px; }
.total-amount { color: #f56c6c; font-weight: 600; font-size: 20px; }
.submit-btn { width: 100%; margin-top: 8px; }
</style>
