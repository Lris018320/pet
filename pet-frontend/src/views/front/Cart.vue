<template>
  <div class="cart-page">
    <div class="page-shell">
      <h2>购物车</h2>
      <div v-loading="loading">
        <div v-if="cartList.length > 0" class="cart-list">
          <div v-for="item in cartList" :key="item.id" class="cart-item">
            <el-checkbox v-model="selectedIds" :label="item.id" />
            <img :src="getProductInfo(item.productId).coverImage" class="cart-img" />
            <div class="cart-info">
              <h4>{{ getProductInfo(item.productId).name }}</h4>
              <p class="price">¥{{ getProductInfo(item.productId).price }}</p>
            </div>
            <el-input-number v-model="item.quantity" :min="1" :max="getProductInfo(item.productId).stock || 999" @change="updateQty(item)" />
            <span class="subtotal">¥{{ subtotal(item) }}</span>
            <el-button type="danger" text @click="removeItem(item.id)">删除</el-button>
          </div>
        </div>
        <el-empty v-else description="购物车是空的" />
      </div>
      <div class="cart-footer" v-if="cartList.length > 0">
        <span>已选 {{ selectedIds.length }} 件</span>
        <span class="total">合计：¥{{ totalAmount.toFixed(2) }}</span>
        <el-button type="primary" :disabled="selectedIds.length === 0" @click="goCheckout">
          去结算
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getCart, updateCart, removeCart, getProduct } from '@/api/mall'

const router = useRouter()
const cartList = ref([])
const productMap = ref({})
const selectedIds = ref([])
const loading = ref(false)

async function loadCart() {
  loading.value = true
  try {
    const res = await getCart()
    cartList.value = res.data || []
    // 批量加载商品信息
    for (const item of cartList.value) {
      if (!productMap.value[item.productId]) {
        const p = await getProduct(item.productId)
        productMap.value[item.productId] = p.data
      }
    }
  } catch (e) { ElMessage.error('加载失败') }
  finally { loading.value = false }
}

function getProductInfo(id) {
  return productMap.value[id] || { name: '商品', price: 0, coverImage: '' }
}

function subtotal(item) {
  return (getProductInfo(item.productId).price || 0) * item.quantity
}

const totalAmount = computed(() => {
  return cartList.value
    .filter(i => selectedIds.value.includes(i.id))
    .reduce((sum, i) => sum + subtotal(i), 0)
})

async function updateQty(item) {
  await updateCart(item.id, item.quantity)
}

async function removeItem(id) {
  await removeCart(id)
  cartList.value = cartList.value.filter(i => i.id !== id)
  selectedIds.value = selectedIds.value.filter(i => i !== id)
}

function goCheckout() {
  if (selectedIds.value.length === 0) {
    ElMessage.warning('请选择要结算的商品')
    return
  }
  router.push({ path: '/checkout', query: { cartIds: selectedIds.value.join(',') } })
}

onMounted(loadCart)
</script>

<style scoped>
.cart-page { min-height: 100%; background: #f5f7fa; }
.page-shell { padding: 18px clamp(16px, 2vw, 32px) 40px; max-width: 900px; margin: 0 auto; }
.page-shell h2 { margin: 0 0 16px; }
.cart-list { background: #fff; border-radius: 12px; padding: 16px; }
.cart-item { display: flex; align-items: center; gap: 12px; padding: 12px 0; border-bottom: 1px solid #f0f0f0; }
.cart-item:last-child { border-bottom: none; }
.cart-img { width: 60px; height: 60px; border-radius: 8px; object-fit: cover; }
.cart-info { flex: 1; }
.cart-info h4 { margin: 0 0 4px; font-size: 14px; }
.price { color: #f56c6c; margin: 0; }
.subtotal { width: 80px; text-align: right; color: #f56c6c; font-weight: 600; }
.cart-footer {
  position: sticky; bottom: 0; background: #fff; padding: 16px; border-radius: 12px;
  margin-top: 16px; display: flex; align-items: center; justify-content: flex-end; gap: 16px;
  box-shadow: 0 -2px 8px rgba(0,0,0,.06);
}
.cart-footer .total { font-size: 18px; color: #f56c6c; font-weight: 600; }
</style>
