<template>
  <div class="mall-page">
    <div class="mall-layout">
      <!-- 左侧主区 -->
      <div class="mall-main">
        <!-- Hero Banner -->
        <section class="hero-banner">
          <div class="hero-text">
            <h2 class="hero-title">给毛孩子<br />更好的生活</h2>
            <p class="hero-sub">精选宠物用品 · 品质保障 · 快速送达</p>
            <el-button class="hero-btn" type="primary" round @click="scrollToGoods">立即选购 →</el-button>
          </div>
          <img :src="IMG.heroPets" class="hero-img" alt="pets" />
        </section>

        <!-- 分类导航 -->
        <section class="category-nav">
          <div
            v-for="c in categories"
            :key="c.name"
            class="cat-item"
            @click="category = c.name; loadList(1)"
          >
            <div class="cat-icon" :style="{ background: c.bg }">
              <span class="cat-emoji">{{ c.icon }}</span>
            </div>
            <span class="cat-name">{{ c.name }}</span>
          </div>
        </section>

        <!-- 商品 Tab -->
        <section class="goods-section" id="goods">
          <div class="goods-header">
            <div class="goods-tabs">
              <span
                v-for="t in tabs"
                :key="t"
                class="goods-tab"
                :class="{ active: activeTab === t }"
                @click="activeTab = t"
              >{{ t }}</span>
            </div>
            <el-button text class="more-btn" @click="loadList(page)">查看更多 ›</el-button>
          </div>

          <!-- 商品网格 -->
          <div class="product-grid" v-loading="loading">
            <div
              v-for="p in list"
              :key="p.id"
              class="product-card"
              @click="goDetail(p.id)"
            >
              <div class="product-img-box">
                <span v-if="p.tag" class="product-tag" :class="'tag-' + p.tagType">{{ p.tag }}</span>
                <img :src="p.coverImage" :alt="p.name" class="product-img" />
              </div>
              <div class="product-body">
                <h3 class="product-name">{{ p.name }}</h3>
                <p class="product-desc">{{ p.description }}</p>
                <div class="product-price-row">
                  <span class="price-now">¥{{ p.price }}</span>
                  <span class="price-old" v-if="p.originalPrice">¥{{ p.originalPrice }}</span>
                </div>
                <div class="product-footer">
                  <span class="sold">已售 {{ p.sold || 0 }}</span>
                  <el-button
                    circle
                    size="small"
                    class="cart-btn"
                    @click.stop="quickAddCart(p)"
                  >
                    <el-icon><ShoppingCart /></el-icon>
                  </el-button>
                </div>
              </div>
            </div>
            <el-empty v-if="!loading && list.length === 0" description="暂无商品" />
          </div>
        </section>

        <!-- 服务保障 -->
        <section class="service-bar">
          <div v-for="s in services" :key="s.title" class="service-item">
            <div class="service-icon" :style="{ background: s.bg }">
              <el-icon :size="22" color="#fff"><component :is="s.icon" /></el-icon>
            </div>
            <div class="service-text">
              <div class="service-title">{{ s.title }}</div>
              <div class="service-sub">{{ s.sub }}</div>
            </div>
          </div>
          <img :src="IMG.heroPets" class="service-pets" alt="" />
        </section>
      </div>

      <!-- 右侧边栏 -->
      <aside class="mall-sidebar">
        <!-- 我的购物车 -->
        <section class="sidebar-card cart-card">
          <div class="card-head">
            <div class="card-title">
              <el-icon color="#6d5ce0"><ShoppingCart /></el-icon>
              <span>我的购物车</span>
            </div>
            <el-button text class="go-cart" @click="$router.push('/cart')">去结算 ›</el-button>
          </div>

          <div v-loading="cartLoading" class="cart-list">
            <div v-if="cartList.length > 0">
              <div v-for="item in cartList" :key="item.id" class="cart-row">
                <img :src="getProductInfo(item.productId).coverImage" class="cart-img" />
                <div class="cart-info">
                  <div class="cart-name">{{ getProductInfo(item.productId).name }}</div>
                  <div class="cart-price">¥{{ getProductInfo(item.productId).price }}</div>
                </div>
                <div class="cart-ops">
                  <el-input-number
                    v-model="item.quantity"
                    :min="1"
                    :max="getProductInfo(item.productId).stock || 999"
                    size="small"
                    controls-position="right"
                    @change="updateQty(item)"
                  />
                  <el-button text size="small" class="cart-del" @click="removeItem(item.id)">
                    <el-icon><Delete /></el-icon>
                  </el-button>
                </div>
              </div>
            </div>
            <el-empty v-else description="购物车是空的" :image-size="60" />
          </div>

          <div class="cart-footer" v-if="cartList.length > 0">
            <span class="cart-total-label">合计：</span>
            <span class="cart-total">¥{{ cartTotal.toFixed(2) }}</span>
            <el-button type="primary" class="checkout-btn" @click="goCheckout">去结算</el-button>
          </div>
        </section>

        <!-- AI 宠物助手 -->
        <section class="sidebar-card ai-card">
          <div class="ai-icon">
            <el-icon :size="34" color="#6d5ce0"><ChatDotRound /></el-icon>
          </div>
          <div class="ai-text">
            <div class="ai-title">AI宠物助手</div>
            <div class="ai-sub">为你推荐适合的宠物商品和养护建议~</div>
          </div>
          <el-button type="primary" round class="ai-btn" @click="goChat">立即咨询</el-button>
        </section>

        <!-- 为你推荐 -->
        <section class="sidebar-card recommend-card">
          <div class="card-head">
            <div class="card-title">
              <el-icon color="#ff8a3d"><Promotion /></el-icon>
              <span>为你推荐</span>
            </div>
            <el-button text class="go-cart" @click="shuffleRecommend">换一批</el-button>
          </div>
          <div class="recommend-grid">
            <div v-for="r in recommendList" :key="r.name" class="rec-item" @click="category = r.name; loadList(1)">
              <img :src="r.img" class="rec-img" />
              <span class="rec-name">{{ r.name }}</span>
            </div>
          </div>
        </section>
      </aside>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ShoppingCart, Delete, ChatDotRound, Promotion, Service, Van, RefreshLeft, Headset } from '@element-plus/icons-vue'
import { listProduct, getProduct, getCart, addToCart, updateCart, removeCart } from '@/api/mall'

const router = useRouter()

/* ========== 图片资源（AI 生成） ========== */
const IMG = {
  heroPets: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=' +
    encodeURIComponent('Adorable golden retriever dog and grey tabby white cat sitting close together, bright modern living room with green plants and big window, soft natural sunlight, warm bokeh background, professional pet photography') +
    '&image_size=landscape_16_9',
  catFood: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=' +
    encodeURIComponent('Royal Canin cat food bag 2kg on white background, premium pet food product photography') +
    '&image_size=square_hd',
  catBed: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=' +
    encodeURIComponent('Cozy round donut shaped cat bed cushion, soft grey plush pet bed, product photography on white background') +
    '&image_size=square_hd',
  chickenTreat: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=' +
    encodeURIComponent('Freeze dried chicken pet treats bag, healthy dog cat snack product packaging on white background') +
    '&image_size=square_hd',
  hairballGel: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=' +
    encodeURIComponent('Pet nutritional hairball gel tube for cats, pet healthcare supplement product on white background') +
    '&image_size=square_hd',
  harness: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=' +
    encodeURIComponent('Adjustable pet dog harness and leash set, purple nylon no pull harness, product photography white background') +
    '&image_size=square_hd'
}

/* ========== 分类导航 ========== */
const categories = [
  { name: '主粮零食', icon: '🥣', bg: 'linear-gradient(135deg,#fff3e0,#ffe0b2)' },
  { name: '宠物用品', icon: '🎽', bg: 'linear-gradient(135deg,#e3f2fd,#bbdefb)' },
  { name: '医疗保健', icon: '💊', bg: 'linear-gradient(135deg,#fce4ec,#f8bbd0)' },
  { name: '清洁护理', icon: '🧴', bg: 'linear-gradient(135deg,#e0f2f1,#b2dfdb)' },
  { name: '玩具用品', icon: '🎾', bg: 'linear-gradient(135deg,#f3e5f5,#e1bee7)' },
  { name: '生活周边', icon: '🏠', bg: 'linear-gradient(135deg,#e8f5e9,#c8e6c9)' }
]

/* ========== 服务保障 ========== */
const services = [
  { title: '正品保障', sub: '品牌直供 假一赔十', bg: '#6d5ce0', icon: Service },
  { title: '极速发货', sub: '全国包邮 极速送达', bg: '#5c9ce0', icon: Van },
  { title: '7天无理由退换', sub: '让购物更安心', bg: '#5ce0b0', icon: RefreshLeft },
  { title: '专业客服', sub: '在线解答 贴心服务', bg: '#e0a05c', icon: Headset }
]

/* ========== 为你推荐 ========== */
const allRecommend = [
  { name: '猫粮', img: IMG.catFood },
  { name: '狗粮', img: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=' + encodeURIComponent('Premium dry dog food bag on white background product photography') + '&image_size=square_hd' },
  { name: '猫窝', img: IMG.catBed },
  { name: '零食', img: IMG.chickenTreat },
  { name: '保健品', img: IMG.hairballGel },
  { name: '清洁护理', img: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=' + encodeURIComponent('Pet shampoo bottle and grooming brush set on white background product photography') + '&image_size=square_hd' },
  { name: '玩具', img: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=' + encodeURIComponent('Colorful pet dog chew toy ball and rope toy on white background product photography') + '&image_size=square_hd' },
  { name: '牵引绳', img: IMG.harness },
  { name: '服饰', img: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=' + encodeURIComponent('Cute pet dog cat clothes hoodie sweater on white background product photography') + '&image_size=square_hd' },
  { name: '生活周边', img: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=' + encodeURIComponent('Pet travel carrier bag and accessories on white background product photography') + '&image_size=square_hd' }
]
const recommendList = ref(allRecommend.slice(0, 10))
function shuffleRecommend() {
  recommendList.value = [...allRecommend].sort(() => Math.random() - 0.5).slice(0, 10)
}

/* ========== 商品列表 ========== */
const tabs = ['热门推荐', '新品上架', '限时特惠', '品时精选']
const activeTab = ref('热门推荐')
const category = ref('')
const list = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const loading = ref(false)

const demoProducts = [
  { id: 101, name: '皇家猫粮 成猫粮 2kg', description: '全价粮 适合1-7岁成猫', price: 159, originalPrice: 199, sold: 1286, coverImage: IMG.catFood, tag: '热销', tagType: 'hot', category: '主粮零食' },
  { id: 102, name: '宠物圆形猫窝', description: '柔软舒适 四季通用', price: 89, originalPrice: 129, sold: 356, coverImage: IMG.catBed, tag: '新品', tagType: 'new', category: '宠物用品' },
  { id: 103, name: '宠物零食冻干鸡肉粒', description: '纯肉制作 无添加', price: 39, originalPrice: 59, sold: 892, coverImage: IMG.chickenTreat, tag: '热卖', tagType: 'hot', category: '主粮零食' },
  { id: 104, name: '宠物营养化毛膏', description: '促进肠胃健康 帮助排毛', price: 69, originalPrice: 99, sold: 620, coverImage: IMG.hairballGel, tag: '推荐', tagType: 'rec', category: '医疗保健' },
  { id: 105, name: '宠物胸背牵引绳', description: '防爆冲 透气舒适', price: 79, originalPrice: 109, sold: 542, coverImage: IMG.harness, tag: '新品', tagType: 'new', category: '宠物用品' }
]

async function loadList(p = page.value) {
  page.value = p
  loading.value = true
  try {
    const res = await listProduct({ page: p, size: size.value, category: category.value })
    const arr = res.data?.list || []
    if (arr.length) {
      list.value = arr.map((p, i) => ({
        ...p,
        tag: p.tag || ['热销', '新品', '推荐', '热卖'][i % 4],
        tagType: p.tagType || ['hot', 'new', 'rec', 'hot'][i % 4],
        originalPrice: p.originalPrice || (Math.round(p.price * 1.3)),
        sold: p.sold || Math.floor(Math.random() * 1000) + 100,
        description: p.description || '精选好物 品质保障'
      }))
      total.value = res.data?.total || 0
    } else {
      list.value = demoProducts
    }
  } catch (e) {
    list.value = demoProducts
  } finally {
    loading.value = false
  }
}

function goDetail(id) {
  router.push(`/mall/${id}`)
}

function scrollToGoods() {
  document.getElementById('goods')?.scrollIntoView({ behavior: 'smooth' })
}

/* ========== 购物车 ========== */
const cartList = ref([])
const productMap = ref({})
const cartLoading = ref(false)

async function loadCart() {
  const token = localStorage.getItem('token')
  if (!token) {
    cartList.value = []
    return
  }
  cartLoading.value = true
  try {
    const res = await getCart()
    cartList.value = res.data || []
    for (const item of cartList.value) {
      if (!productMap.value[item.productId]) {
        const p = await getProduct(item.productId)
        productMap.value[item.productId] = p.data
      }
    }
  } catch (e) {
    cartList.value = []
  } finally {
    cartLoading.value = false
  }
}

function getProductInfo(id) {
  return productMap.value[id] || { name: '商品', price: 0, coverImage: '', stock: 999 }
}

const cartTotal = computed(() =>
  cartList.value.reduce((s, i) => s + (getProductInfo(i.productId).price || 0) * i.quantity, 0)
)

async function updateQty(item) {
  try { await updateCart(item.id, item.quantity) } catch (e) {}
}

async function removeItem(id) {
  try {
    await removeCart(id)
    cartList.value = cartList.value.filter(i => i.id !== id)
  } catch (e) {}
}

async function quickAddCart(p) {
  const token = localStorage.getItem('token')
  if (!token) {
    ElMessage.warning('请先登录后再加入购物车')
    router.push('/login')
    return
  }
  try {
    await addToCart({ productId: p.id, quantity: 1 })
    ElMessage.success('已加入购物车')
    loadCart()
  } catch (e) {
    ElMessage.error('加入购物车失败')
  }
}

function goCheckout() {
  if (cartList.value.length === 0) {
    ElMessage.warning('购物车是空的')
    return
  }
  router.push({ path: '/checkout', query: { cartIds: cartList.value.map(i => i.id).join(',') } })
}

function goChat() {
  router.push('/chat')
}

onMounted(() => {
  loadList(1)
  loadCart()
})
</script>

<style scoped>
.mall-page { min-height: 100%; background: #f5f7fb; }
.mall-layout {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 340px;
  gap: 20px;
  padding: 18px clamp(16px, 2vw, 32px) 40px;
  width: 100%;
  max-width: none;
  margin: 0;
}
.mall-main { min-width: 0; }
.mall-sidebar { min-width: 0; }

/* Hero */
.hero-banner {
  position: relative;
  display: flex; align-items: center; justify-content: space-between;
  background: linear-gradient(125deg, #eef3ff, #e6ecff, #dfe8ff);
  border-radius: 22px;
  padding: 36px 44px;
  overflow: hidden;
  margin-bottom: 18px;
}
.hero-text { z-index: 2; }
.hero-title {
  font-size: 38px; font-weight: 800; color: #2c3e7b;
  line-height: 1.25; margin: 0 0 10px;
  font-family: 'STKaiti', 'KaiTi', serif;
}
.hero-sub { color: #6b7aa8; font-size: 15px; margin: 0 0 22px; }
.hero-btn {
  height: 44px; padding: 0 28px; font-size: 15px;
  background: linear-gradient(135deg, #7c8df6, #6d5ce0);
  border: none; box-shadow: 0 6px 18px rgba(109, 92, 224, .35);
}
.hero-img { width: 340px; height: 220px; object-fit: cover; border-radius: 18px; z-index: 1; }

/* 分类导航 */
.category-nav {
  display: grid; grid-template-columns: repeat(6, 1fr); gap: 12px;
  background: #fff; border-radius: 16px; padding: 22px 18px;
  margin-bottom: 18px;
  box-shadow: 0 2px 10px rgba(94, 110, 190, .05);
}
.cat-item { display: flex; flex-direction: column; align-items: center; gap: 10px; cursor: pointer; }
.cat-icon {
  width: 56px; height: 56px; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
}
.cat-emoji { font-size: 26px; }
.cat-name { font-size: 13px; color: #4a5578; font-weight: 500; }

/* 商品区 */
.goods-section { background: #fff; border-radius: 16px; padding: 18px; margin-bottom: 18px; }
.goods-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 16px; }
.goods-tabs { display: flex; gap: 22px; }
.goods-tab {
  font-size: 16px; color: #8a93ab; cursor: pointer; padding-bottom: 6px;
  border-bottom: 2px solid transparent; transition: all .2s; font-weight: 500;
}
.goods-tab.active { color: #6d5ce0; border-bottom-color: #6d5ce0; font-weight: 600; }
.more-btn { color: #8a93ab; }

.product-grid { display: grid; grid-template-columns: repeat(5, 1fr); gap: 14px; }
.product-card {
  background: #fff; border: 1px solid #edf0f8; border-radius: 14px;
  overflow: hidden; cursor: pointer; transition: all .25s;
}
.product-card:hover { transform: translateY(-4px); box-shadow: 0 10px 28px rgba(94, 110, 190, .12); }
.product-img-box { position: relative; height: 170px; background: #f7f8fc; }
.product-img { width: 100%; height: 100%; object-fit: cover; }
.product-tag {
  position: absolute; top: 8px; left: 8px; padding: 2px 8px;
  border-radius: 6px; font-size: 11px; color: #fff; font-weight: 600;
}
.tag-hot { background: #f56c6c; }
.tag-new { background: #67c23a; }
.tag-rec { background: #e6a23c; }
.product-body { padding: 10px 12px 12px; }
.product-name { margin: 0 0 4px; font-size: 14px; height: 20px; overflow: hidden; color: #303133; }
.product-desc { margin: 0 0 8px; font-size: 12px; color: #909399; height: 16px; overflow: hidden; }
.product-price-row { display: flex; align-items: baseline; gap: 6px; margin-bottom: 6px; }
.price-now { color: #f56c6c; font-size: 18px; font-weight: 700; }
.price-old { color: #c0c4cc; font-size: 12px; text-decoration: line-through; }
.product-footer { display: flex; align-items: center; justify-content: space-between; }
.sold { font-size: 12px; color: #909399; }
.cart-btn { background: #6d5ce0; color: #fff; border: none; }
.cart-btn:hover { background: #5a49d1; }

/* 服务保障 */
.service-bar {
  position: relative;
  display: flex; align-items: center; gap: 28px;
  background: #fff; border-radius: 16px; padding: 22px 28px;
  box-shadow: 0 2px 10px rgba(94, 110, 190, .05);
  overflow: hidden;
}
.service-item { display: flex; align-items: center; gap: 12px; flex: 1; min-width: 0; }
.service-icon {
  width: 44px; height: 44px; border-radius: 50%;
  display: flex; align-items: center; justify-content: center; flex-shrink: 0;
}
.service-text { min-width: 0; }
.service-title { font-size: 14px; font-weight: 600; color: #303133; }
.service-sub { font-size: 12px; color: #909399; }
.service-pets {
  position: absolute; right: 0; bottom: 0; width: 180px; height: 90px;
  object-fit: cover; border-radius: 12px 0 0 0; opacity: .85;
}

/* 右侧边栏 */
.sidebar-card {
  background: #fff; border-radius: 16px; padding: 16px;
  margin-bottom: 16px; box-shadow: 0 2px 10px rgba(94, 110, 190, .05);
}
.card-head { display: flex; align-items: center; justify-content: space-between; margin-bottom: 14px; }
.card-title { display: flex; align-items: center; gap: 6px; font-size: 15px; font-weight: 600; color: #303133; }
.go-cart { color: #6d5ce0; font-size: 13px; }

.cart-list { max-height: 280px; overflow-y: auto; }
.cart-row { display: flex; align-items: center; gap: 10px; padding: 10px 0; border-bottom: 1px solid #f2f4fa; }
.cart-row:last-child { border-bottom: none; }
.cart-img { width: 52px; height: 52px; border-radius: 8px; object-fit: cover; flex-shrink: 0; }
.cart-info { flex: 1; min-width: 0; }
.cart-name { font-size: 13px; color: #303133; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.cart-price { color: #f56c6c; font-size: 14px; font-weight: 600; margin-top: 2px; }
.cart-ops { display: flex; align-items: center; gap: 4px; flex-shrink: 0; }
.cart-del { color: #c0c4cc; padding: 4px; }
.cart-footer { display: flex; align-items: center; gap: 8px; margin-top: 14px; padding-top: 14px; border-top: 1px solid #f2f4fa; }
.cart-total-label { font-size: 13px; color: #909399; }
.cart-total { flex: 1; color: #f56c6c; font-size: 18px; font-weight: 700; }
.checkout-btn {
  background: linear-gradient(135deg, #7c8df6, #6d5ce0);
  border: none; height: 36px; padding: 0 20px;
}

/* AI 助手 */
.ai-card { display: flex; flex-direction: column; align-items: center; text-align: center; gap: 10px; }
.ai-icon {
  width: 56px; height: 56px; border-radius: 50%;
  background: linear-gradient(135deg, #eef3ff, #e0e7ff);
  display: flex; align-items: center; justify-content: center;
}
.ai-title { font-size: 15px; font-weight: 600; color: #303133; }
.ai-sub { font-size: 12px; color: #909399; }
.ai-btn {
  background: linear-gradient(135deg, #7c8df6, #6d5ce0);
  border: none; height: 36px; padding: 0 24px;
}

/* 为你推荐 */
.recommend-grid { display: grid; grid-template-columns: repeat(5, 1fr); gap: 12px; }
.rec-item { display: flex; flex-direction: column; align-items: center; gap: 6px; cursor: pointer; }
.rec-img { width: 48px; height: 48px; border-radius: 12px; object-fit: cover; }
.rec-name { font-size: 12px; color: #606266; }

/* 响应式 */
@media (max-width: 1280px) {
  .mall-layout { grid-template-columns: minmax(0, 1fr) 300px; }
  .product-grid { grid-template-columns: repeat(4, 1fr); }
  .hero-img { width: 280px; }
}
@media (max-width: 1024px) {
  .mall-layout { grid-template-columns: 1fr; }
  .mall-sidebar { order: -1; }
  .product-grid { grid-template-columns: repeat(3, 1fr); }
}
@media (max-width: 768px) {
  .product-grid { grid-template-columns: repeat(2, 1fr); }
  .category-nav { grid-template-columns: repeat(3, 1fr); }
  .hero-banner { flex-direction: column; text-align: center; padding: 24px; }
  .hero-img { width: 100%; height: 180px; margin-top: 16px; }
}
</style>
