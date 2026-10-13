<template>
  <div class="product-detail" v-loading="loading">
    <div class="page-shell" v-if="product">
      <el-button text @click="$router.back()">‹ 返回</el-button>
      <div class="detail-card">
        <div class="product-img">
          <img :src="product.coverImage" :alt="product.name" />
        </div>
        <div class="product-info">
          <h1>{{ product.name }}</h1>
          <p class="price">¥{{ product.price }}</p>
          <div class="info-row">
            <span class="label">分类</span>
            <span>{{ product.category || '-' }}</span>
          </div>
          <div class="info-row">
            <span class="label">库存</span>
            <span>{{ product.stock }}</span>
          </div>
          <div class="info-row">
            <span class="label">数量</span>
            <el-input-number v-model="quantity" :min="1" :max="product.stock" />
          </div>
          <div class="actions">
            <el-button type="primary" size="large" @click="addToCart" :disabled="!isLogin">加入购物车</el-button>
            <el-button type="success" size="large" @click="buyNow" :disabled="!isLogin">立即购买</el-button>
          </div>
          <div class="login-tip" v-if="!isLogin">
            请先 <el-link @click="$router.push('/login')">登录</el-link> 后购买
          </div>
        </div>
      </div>
      <div class="desc-card" v-if="product.description">
        <h3>商品描述</h3>
        <p>{{ product.description }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getProduct, addToCart as addCart } from '@/api/mall'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const isLogin = ref(!!userStore.token)

const product = ref(null)
const quantity = ref(1)
const loading = ref(false)

async function loadDetail() {
  loading.value = true
  try {
    const res = await getProduct(route.params.id)
    product.value = res.data
  } catch (e) { ElMessage.error('加载失败') }
  finally { loading.value = false }
}

async function addToCart() {
  try {
    await addCart({ productId: product.value.id, quantity: quantity.value })
    ElMessage.success('已加入购物车')
  } catch (e) {}
}

function buyNow() {
  router.push({
    path: '/checkout',
    query: { productId: product.value.id, quantity: quantity.value }
  })
}

onMounted(loadDetail)
</script>

<style scoped>
.product-detail { min-height: 100%; background: #f5f7fa; }
.page-shell { padding: 18px clamp(16px, 2vw, 32px) 40px; max-width: 1000px; margin: 0 auto; }
.detail-card { background: #fff; border-radius: 16px; padding: 24px; margin-top: 12px;
  display: flex; gap: 24px; }
.product-img { width: 360px; flex-shrink: 0; border-radius: 12px; overflow: hidden; }
.product-img img { width: 100%; height: 320px; object-fit: cover; }
.product-info { flex: 1; }
.product-info h1 { margin: 0 0 12px; }
.price { color: #f56c6c; font-size: 32px; font-weight: 600; margin: 0 0 16px; }
.info-row { display: flex; align-items: center; gap: 12px; margin-bottom: 12px; }
.info-row .label { color: #909399; width: 50px; }
.actions { margin-top: 20px; display: flex; gap: 12px; }
.login-tip { margin-top: 10px; color: #909399; font-size: 13px; }
.desc-card { background: #fff; border-radius: 16px; padding: 24px; margin-top: 16px; }
.desc-card h3 { margin: 0 0 12px; }
.desc-card p { margin: 0; line-height: 1.8; color: #606266; white-space: pre-wrap; }
@media (max-width: 760px) {
  .detail-card { flex-direction: column; }
  .product-img { width: 100%; }
}
</style>
