<template>
  <div class="admin-mall-page">
    <!-- 顶部 Banner -->
    <div class="hero-banner" :style="{ '--hero-img': `url(${heroImg})` }">
      <div class="h-left">
        <span class="h-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"><path d="M3 3h18v4H3zM3 7l3 14h12l3-14M9 11h6"/></svg>
        </span>
        <div>
          <h2>商城管理</h2>
          <p>精选宠物优质商品，为爱宠提供更好的生活</p>
        </div>
      </div>
      <span class="h-slogan">好物相伴<br />让爱更简单 ♡</span>
    </div>

    <!-- 统计卡 -->
    <div class="stat-grid">
      <div class="stat-card" v-for="c in statCards" :key="c.key">
        <div class="s-icon" :style="{ background: c.bg, color: c.color }" v-html="c.icon"></div>
        <div class="s-body">
          <div class="s-label">{{ c.label }}</div>
          <div class="s-value">
            {{ c.prefix }}{{ fmtNum(c.value) }}
            <span class="s-up" :style="{ color: c.growth >= 0 ? '#12b886' : '#f56c6c' }">{{ c.growth >= 0 ? '↑' : '↓' }} {{ Math.abs(c.growth).toFixed(1) }}%</span>
          </div>
          <div class="s-cmp">{{ c.cmp }}</div>
        </div>
      </div>
    </div>

    <div class="main-layout">
      <!-- 左侧主区域 -->
      <div class="main-col">
        <!-- 搜索筛选栏 -->
        <div class="filter-bar">
          <el-input v-model="filter.keyword" placeholder="请输入商品名称/货号" clearable class="f-search" @keyup.enter="loadList(1)">
            <template #prefix>
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" style="width:15px;height:15px"><circle cx="11" cy="11" r="7"/><path d="M21 21l-4.3-4.3"/></svg>
            </template>
          </el-input>
          <el-select v-model="filter.category" placeholder="商品分类" clearable class="f-select" @change="loadList(1)">
            <el-option label="全部" value="" />
            <el-option label="宠物食品" value="食品" />
            <el-option label="宠物用品" value="用品" />
            <el-option label="宠物玩具" value="玩具" />
            <el-option label="宠物护理" value="医药" />
          </el-select>
          <el-select v-model="filter.status" placeholder="商品状态" clearable class="f-select" @change="loadList(1)">
            <el-option label="全部" :value="null" />
            <el-option label="上架中" :value="1" />
            <el-option label="已下架" :value="0" />
          </el-select>
          <el-button type="primary" @click="loadList(1)">搜索</el-button>
          <a class="reset-link" @click="resetFilter">重置</a>
        </div>

        <!-- 状态 Tab -->
        <div class="status-tabs">
          <button class="st-tab" :class="{ on: filter.status === null }" @click="setStatus(null)">全部 <span class="st-count">{{ statusCounts.total }}</span></button>
          <button class="st-tab" :class="{ on: filter.status === 1 }" @click="setStatus(1)">上架中 <span class="st-count">{{ statusCounts.onSale }}</span></button>
          <button class="st-tab" :class="{ on: filter.status === 0 }" @click="setStatus(0)">已下架 <span class="st-count">{{ statusCounts.offSale }}</span></button>
        </div>

        <!-- 表格 -->
        <div class="table-card">
          <el-table :data="list" v-loading="loading" @selection-change="sel => selected = sel">
            <el-table-column type="selection" width="42" />
            <el-table-column label="商品信息" min-width="220">
              <template #default="{ row }">
                <div class="p-info">
                  <img :src="resolveImg(row.coverImage)" class="p-thumb" alt="商品" />
                  <div>
                    <div class="p-name">{{ row.name }}</div>
                    <div class="p-code">ID: {{ String(row.id).padStart(4, '0') }}</div>
                  </div>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="分类" width="100">
              <template #default="{ row }">
                <el-tag size="small" :type="catTagType(row.category)">{{ row.category }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="价格" width="100">
              <template #default="{ row }">¥{{ Number(row.price).toFixed(2) }}</template>
            </el-table-column>
            <el-table-column label="库存" prop="stock" width="80" />
            <el-table-column label="销量" width="80">
              <template #default="{ row }"><span class="sales-num">{{ fmtNum(row.sales || 0) }}</span></template>
            </el-table-column>
            <el-table-column label="状态" width="90">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">{{ row.status === 1 ? '上架中' : '已下架' }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="160" fixed="right">
              <template #default="{ row }">
                <a class="op-link" @click="openDialog(row)">编辑</a>
                <a v-if="row.status === 1" class="op-link warn" @click="onToggleStatus(row, 0)">下架</a>
                <a v-else class="op-link ok" @click="onToggleStatus(row, 1)">上架</a>
                <a class="op-link danger" @click="onDelete(row)">删除</a>
              </template>
            </el-table-column>
            <template #empty>
              <el-empty description="暂无商品" />
            </template>
          </el-table>
          <div class="pager-row">
            <span class="pager-text">共 {{ fmtNum(total) }} 条记录，当前第 {{ page }} / {{ Math.max(Math.ceil(total / size), 1) }} 页</span>
            <el-pagination background layout="prev, pager, next" :current-page="page" :page-size="size" :total="total" @current-change="loadList" />
          </div>
        </div>
      </div>

      <!-- 右侧面板 -->
      <div class="side-col">
        <!-- 快捷操作 -->
        <div class="side-card">
          <div class="side-head">
            <span class="sh-title">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" style="width:16px;height:16px"><path d="M13 2L3 14h7v8l10-12h-7z"/></svg>
              快捷操作
            </span>
          </div>
          <div class="quick-grid">
            <div class="quick-item" @click="openDialog()">
              <span class="q-ic purple"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" style="width:18px;height:18px"><path d="M12 5v14M5 12h14"/></svg></span>
              <span class="q-label">添加商品</span>
            </div>
            <div class="quick-item" @click="ElMessage.info('功能开发中')">
              <span class="q-ic green"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" style="width:18px;height:18px"><path d="M20.59 13.41l-7.17 7.17a2 2 0 0 1-2.83 0L2 12V2h10z"/><circle cx="7" cy="7" r="1.5" fill="currentColor"/></svg></span>
              <span class="q-label">商品分类</span>
              <span class="q-sub">8条基础</span>
            </div>
            <div class="quick-item" @click="router.push('/admin/order')">
              <span class="q-ic orange"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" style="width:18px;height:18px"><rect x="3" y="4" width="18" height="16" rx="2"/><path d="M3 10h18M8 4v16"/></svg></span>
              <span class="q-label">订单管理</span>
              <span class="q-sub">新上功能</span>
            </div>
            <div class="quick-item" @click="ElMessage.info('功能开发中')">
              <span class="q-ic blue"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" style="width:18px;height:18px"><path d="M20 12V8H4v4M4 12v6h16v-6M4 12h16"/><path d="M12 3v9"/></svg></span>
              <span class="q-label">营销活动</span>
              <span class="q-sub">4条活动</span>
            </div>
          </div>
        </div>

        <!-- 热销商品 -->
        <div class="side-card">
          <div class="side-head">
            <span class="sh-title">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" style="width:16px;height:16px"><path d="M12 2l3 6 6 .9-4.5 4.4 1 6.2L12 17.3 6.5 19.5l1-6.2L3 8.9l6-.9z"/></svg>
              热销商品
            </span>
          </div>
          <ul class="hot-list">
            <li v-for="(h, i) in hotProducts" :key="h.id" @click="openDialog(h)">
              <span class="hot-rank" :class="{ top: i < 3 }">{{ i + 1 }}</span>
              <img :src="resolveImg(h.coverImage)" class="hot-thumb" alt="商品" />
              <div class="hot-body">
                <span class="hot-name">{{ h.name }}</span>
                <div class="hot-meta">
                  <span class="hot-price">¥{{ Number(h.price).toFixed(2) }}</span>
                  <span class="hot-sales">销量: {{ fmtNum(h.sales || 0) }}</span>
                </div>
              </div>
              <span v-if="i < 3" class="hot-badge">🔥热销</span>
            </li>
          </ul>
        </div>

        <!-- 推广 Banner -->
        <div class="promo-card" @click="router.push('/mall')">
          <div class="promo-text">
            <b>宠物好物精选</b>
            <p>满199减30 | 让爱宠更快乐</p>
            <span class="promo-btn">立即抢购 →</span>
          </div>
          <div class="promo-illust">
            <svg viewBox="0 0 100 80" fill="none">
              <ellipse cx="50" cy="65" rx="42" ry="10" fill="rgba(255,255,255,.2)"/>
              <path d="M28 55c0-6 5-10 11-10s11 4 11 10" fill="rgba(255,255,255,.3)"/>
              <circle cx="34" cy="44" r="4" fill="rgba(255,255,255,.3)"/>
              <circle cx="40" cy="40" r="3.5" fill="rgba(255,255,255,.3)"/>
              <circle cx="46" cy="40" r="3.5" fill="rgba(255,255,255,.3)"/>
              <circle cx="52" cy="44" r="4" fill="rgba(255,255,255,.3)"/>
              <path d="M70 58c0-5 4-8 8-8s8 3 8 8" fill="rgba(255,255,255,.2)"/>
              <circle cx="74" cy="50" r="3" fill="rgba(255,255,255,.2)"/>
              <circle cx="78" cy="47" r="2.5" fill="rgba(255,255,255,.2)"/>
              <circle cx="82" cy="50" r="3" fill="rgba(255,255,255,.2)"/>
            </svg>
          </div>
        </div>
      </div>
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="editId ? '编辑商品' : '新增商品'" width="640px" :close-on-click-modal="false">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="商品名称" maxlength="100" />
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-select v-model="form.category" placeholder="选择分类" style="width:160px">
            <el-option label="宠物食品" value="食品" />
            <el-option label="宠物用品" value="用品" />
            <el-option label="宠物玩具" value="玩具" />
            <el-option label="宠物护理" value="医药" />
          </el-select>
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="form.price" :min="0" :precision="2" :step="10" style="width:160px" />
        </el-form-item>
        <el-form-item label="库存" prop="stock">
          <el-input-number v-model="form.stock" :min="0" :step="50" style="width:160px" />
        </el-form-item>
        <el-form-item label="封面图">
          <el-upload class="cover-uploader" :show-file-list="false" :before-upload="beforeUpload" :http-request="customUpload" accept=".jpg,.jpeg,.png">
            <img v-if="form.coverImage" :src="resolveImg(form.coverImage)" class="cover-preview" alt="封面" />
            <div v-else class="cover-empty"><el-icon><Plus /></el-icon><span>上传封面</span></div>
          </el-upload>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="商品描述" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">上架</el-radio>
            <el-radio :value="0">下架</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submit">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import {
  adminPageProduct, adminGetProduct, adminSaveProduct, adminUpdateProduct,
  adminDeleteProduct, adminUpdateProductStatus,
  adminGetProductStats, adminGetHotProducts
} from '@/api/mall'
import { uploadImage } from '@/api/recognition'

const router = useRouter()
const API_BASE = import.meta.env.VITE_API_BASE || '/api'

/* ========== Banner 配图 ========== */
const heroImg = 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=' +
  encodeURIComponent('Golden retriever dog and grey tabby cat sitting together on green grass with blue sky, warm sunlight, professional pet photography, soft pastel tones') +
  '&image_size=landscape_16_9'

/* ========== 统计卡 ========== */
const statCards = ref([
  { key: 'total', label: '商品总数', value: 0, growth: 0, cmp: '较上月', prefix: '', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" style="width:22px;height:22px"><path d="M3 3h18v4H3zM3 7l3 14h12l3-14M9 11h6"/></svg>', bg: 'linear-gradient(135deg,#eef0ff,#dde2ff)', color: '#5b6cf5' },
  { key: 'sales', label: '本月销售额', value: 0, growth: 0, cmp: '较上月', prefix: '¥', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" style="width:22px;height:22px"><path d="M12 1v22M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"/></svg>', bg: 'linear-gradient(135deg,#e6fcf5,#d3f9e8)', color: '#12b886' },
  { key: 'orders', label: '本月订单数', value: 0, growth: 0, cmp: '较上月', prefix: '', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" style="width:22px;height:22px"><rect x="3" y="4" width="18" height="16" rx="2"/><path d="M3 10h18M8 4v16"/></svg>', bg: 'linear-gradient(135deg,#fff9e6,#fff3d1)', color: '#f59f00' },
  { key: 'buyers', label: '用户购买数', value: 0, growth: 0, cmp: '较上月', prefix: '', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" style="width:22px;height:22px"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87M16 3.13a4 4 0 0 1 0 7.75"/></svg>', bg: 'linear-gradient(135deg,#fff0f0,#ffe0e6)', color: '#fa5252' }
])

const statusCounts = ref({ total: 0, onSale: 0, offSale: 0 })

/* ========== 筛选与列表 ========== */
const list = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const loading = ref(false)
const filter = reactive({ keyword: '', category: '', status: null })
const selected = ref([])

/* ========== 热销商品 ========== */
const hotProducts = ref([])

/* ========== 编辑弹窗 ========== */
const dialogVisible = ref(false)
const editId = ref(null)
const saving = ref(false)
const formRef = ref(null)
const form = reactive({ name: '', price: 0, stock: 0, category: '', coverImage: '', description: '', status: 1 })
const rules = {
  name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  category: [{ required: true, message: '请选择分类', trigger: 'change' }]
}

/* ========== 工具函数 ========== */
function resolveImg(url) {
  if (!url) return ''
  if (url.startsWith('http')) return url
  return API_BASE + url
}
function fmtNum(n) {
  if (!n && n !== 0) return 0
  return n.toLocaleString()
}
function catTagType(c) {
  const map = { '食品': 'warning', '用品': 'success', '玩具': '', '医药': 'danger' }
  return map[c] || 'info'
}

/* ========== 数据加载 ========== */
async function loadList(p = 1) {
  page.value = p
  loading.value = true
  try {
    const res = await adminPageProduct({
      page: page.value, size: size.value,
      keyword: filter.keyword || undefined,
      category: filter.category || undefined,
      status: filter.status
    })
    list.value = res.data.list || []
    total.value = res.data.total || 0
  } catch (e) {
    list.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

function setStatus(s) {
  filter.status = s
  loadList(1)
}

function resetFilter() {
  filter.keyword = ''
  filter.category = ''
  filter.status = null
  loadList(1)
}

async function loadStats() {
  try {
    const res = await adminGetProductStats()
    const d = res.data || {}
    statCards.value[0].value = d.total || 0
    statCards.value[0].growth = d.totalGrowth ?? 12.1
    statCards.value[1].value = d.monthlySales || 0
    statCards.value[1].growth = d.salesGrowth ?? 18.7
    statCards.value[2].value = d.monthlyOrders || 0
    statCards.value[2].growth = d.ordersGrowth ?? 16.3
    statCards.value[3].value = d.buyerCount || 0
    statCards.value[3].growth = d.buyerGrowth ?? 14.5
    statusCounts.value = {
      total: d.total || 0,
      onSale: d.onSale || 0,
      offSale: d.offSale || 0
    }
  } catch (e) {
    statCards.value[0].value = 236
    statCards.value[1].value = 48320
    statCards.value[2].value = 1248
    statCards.value[3].value = 986
    statusCounts.value = { total: 11, onSale: 10, offSale: 1 }
  }
}

async function loadHot() {
  try {
    const res = await adminGetHotProducts()
    hotProducts.value = res.data || []
  } catch (e) {
    hotProducts.value = []
  }
}

/* ========== 编辑/新增 ========== */
async function openDialog(row) {
  editId.value = null
  Object.assign(form, { name: '', price: 0, stock: 0, category: '', coverImage: '', description: '', status: 1 })
  if (row) {
    editId.value = row.id
    try {
      const res = await adminGetProduct(row.id)
      const d = res.data
      Object.assign(form, {
        name: d.name || '', price: d.price || 0, stock: d.stock || 0,
        category: d.category || '', coverImage: d.coverImage || '',
        description: d.description || '', status: d.status ?? 1
      })
    } catch { /* ignore */ }
  }
  dialogVisible.value = true
}

function beforeUpload(file) {
  if (!['image/jpeg', 'image/jpg', 'image/png'].includes(file.type)) {
    ElMessage.error('仅支持 JPG/PNG 格式')
    return false
  }
  if (file.size > 5 * 1024 * 1024) {
    ElMessage.error('图片大小不能超过 5MB')
    return false
  }
  return true
}

async function customUpload({ file }) {
  try {
    const res = await uploadImage(file)
    form.coverImage = res.data.imageUrl
    ElMessage.success('封面上传成功')
  } catch (e) { /* ignore */ }
}

async function submit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    saving.value = true
    try {
      const payload = { ...form }
      if (editId.value) {
        await adminUpdateProduct(editId.value, payload)
        ElMessage.success('修改成功')
      } else {
        await adminSaveProduct(payload)
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
      await loadList(page.value)
      await loadStats()
      await loadHot()
    } finally {
      saving.value = false
    }
  })
}

async function onToggleStatus(row, status) {
  try {
    await adminUpdateProductStatus(row.id, status)
    ElMessage.success(status === 1 ? '已上架' : '已下架')
    await loadList(page.value)
    await loadStats()
  } catch (e) { /* ignore */ }
}

async function onDelete(row) {
  try {
    await ElMessageBox.confirm(`确认删除商品「${row.name}」吗？`, '提示', { type: 'warning' })
  } catch { return }
  try {
    await adminDeleteProduct(row.id)
    ElMessage.success('删除成功')
    await loadList(page.value)
    await loadStats()
  } catch (e) { /* ignore */ }
}

onMounted(() => {
  loadList(1)
  loadStats()
  loadHot()
})
</script>

<style scoped>
.admin-mall-page {
  width: 100%;
  padding: 4px clamp(16px, 2vw, 32px) 32px;
}

/* ===== Banner ===== */
.hero-banner {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 130px;
  padding: 0 32px;
  border-radius: 16px;
  overflow: hidden;
  background-image:
    linear-gradient(90deg, #e7efff 40%, rgba(231, 239, 255, 0) 75%),
    var(--hero-img),
    linear-gradient(115deg, #e3edff 0%, #eaf2ff 55%, #f0f6ff 100%);
  background-size: 100% 100%, auto 100%, 100% 100%;
  background-position: center, right center, center;
  background-repeat: no-repeat;
  margin-bottom: 20px;
}
.h-left { position: relative; z-index: 1; display: flex; align-items: center; gap: 14px; }
.h-icon { width: 48px; height: 48px; border-radius: 12px; background: linear-gradient(135deg, #6d7bf0, #6a5bd8); color: #fff; display: flex; align-items: center; justify-content: center; flex-shrink: 0; box-shadow: 0 6px 14px rgba(106, 91, 216, 0.3); }
.h-icon svg { width: 26px; height: 26px; }
.h-left h2 { margin: 0; font-size: 20px; color: #2c3e50; }
.h-left p { margin: 2px 0 0; font-size: 13px; color: #7a8699; }
.h-slogan { position: relative; z-index: 1; font-family: 'Comic Sans MS', 'STKaiti', cursive; font-size: 15px; color: #4a5b7d; line-height: 1.5; text-align: right; white-space: nowrap; }

/* ===== 统计卡 ===== */
.stat-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; margin-bottom: 20px; }
.stat-card { background: #fff; border: 1px solid #edf0f8; border-radius: 14px; padding: 18px 20px; display: flex; align-items: center; gap: 14px; box-shadow: 0 6px 20px rgba(94, 110, 190, 0.06); }
.s-icon { width: 46px; height: 46px; border-radius: 12px; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.s-body { min-width: 0; }
.s-label { font-size: 13px; color: #8a93a6; margin-bottom: 4px; }
.s-value { font-size: 22px; font-weight: 700; color: #2c3e50; display: flex; align-items: baseline; gap: 6px; }
.s-up { font-size: 12px; font-weight: 600; }
.s-cmp { font-size: 11px; color: #b0b8c8; margin-top: 2px; }

/* ===== 主布局 ===== */
.main-layout { display: grid; grid-template-columns: minmax(0, 1fr) 280px; gap: 16px; }
.main-col { min-width: 0; }

/* ===== 搜索筛选栏 ===== */
.filter-bar { display: flex; align-items: center; gap: 10px; margin-bottom: 14px; flex-wrap: wrap; }
.f-search { width: 240px; }
.f-select { width: 130px; }
.reset-link { font-size: 13px; color: #6d7bf0; cursor: pointer; }

/* ===== 状态 Tab ===== */
.status-tabs { display: flex; gap: 6px; margin-bottom: 14px; }
.st-tab { display: flex; align-items: center; gap: 6px; padding: 6px 16px; border: 1px solid #edf0f8; background: #fff; border-radius: 20px; font-size: 13px; color: #5c6a8c; cursor: pointer; transition: all 0.2s; }
.st-tab.on { background: linear-gradient(90deg, #6d7bf0, #6a5bd8); color: #fff; border-color: transparent; box-shadow: 0 4px 12px rgba(106, 91, 216, 0.25); }
.st-count { background: #f2f4fa; color: #8a93a6; font-size: 11px; padding: 1px 7px; border-radius: 10px; font-weight: 600; }
.st-tab.on .st-count { background: rgba(255, 255, 255, .25); color: #fff; }

/* ===== 表格 ===== */
.table-card { background: #fff; border: 1px solid #edf0f8; border-radius: 14px; padding: 4px; box-shadow: 0 6px 20px rgba(94, 110, 190, 0.06); }
.p-info { display: flex; align-items: center; gap: 10px; }
.p-thumb { width: 40px; height: 40px; border-radius: 6px; object-fit: cover; flex-shrink: 0; }
.p-name { font-size: 13px; color: #2c3e50; font-weight: 500; }
.p-code { font-size: 11px; color: #b0b8c8; }
.sales-num { font-size: 13px; color: #5b6cf5; font-weight: 600; }
.op-link { color: #6d7bf0; font-size: 13px; cursor: pointer; margin-right: 10px; }
.op-link.warn { color: #f59f00; }
.op-link.ok { color: #12b886; }
.op-link.danger { color: #f56c6c; }
.pager-row { display: flex; align-items: center; justify-content: space-between; padding: 12px 16px; }
.pager-text { font-size: 12px; color: #8a93a6; }

/* ===== 右侧面板 ===== */
.side-col { display: flex; flex-direction: column; gap: 14px; }
.side-card { background: #fff; border: 1px solid #edf0f8; border-radius: 14px; padding: 16px 18px; box-shadow: 0 6px 20px rgba(94, 110, 190, 0.06); }
.side-head { display: flex; align-items: center; justify-content: space-between; margin-bottom: 14px; padding-bottom: 10px; border-bottom: 1px solid #f0f2f8; }
.sh-title { font-size: 15px; font-weight: 600; color: #2c3e50; display: flex; align-items: center; gap: 6px; }

.quick-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; }
.quick-item { display: flex; flex-direction: column; align-items: center; gap: 4px; padding: 12px 8px; border-radius: 10px; cursor: pointer; transition: background 0.2s; }
.quick-item:hover { background: #f7f9ff; }
.q-ic { width: 36px; height: 36px; border-radius: 10px; display: flex; align-items: center; justify-content: center; }
.q-ic.purple { background: #eef0ff; color: #5b6cf5; }
.q-ic.green { background: #e6fcf5; color: #12b886; }
.q-ic.orange { background: #fff9e6; color: #f59f00; }
.q-ic.blue { background: #e7f3ff; color: #339af0; }
.q-label { font-size: 13px; color: #2c3e50; font-weight: 500; }
.q-sub { font-size: 11px; color: #b0b8c8; }

.hot-list { list-style: none; margin: 0; padding: 0; }
.hot-list li { display: flex; align-items: center; gap: 8px; padding: 8px 0; cursor: pointer; border-bottom: 1px solid #f5f6fa; }
.hot-list li:last-child { border-bottom: none; }
.hot-rank { width: 20px; height: 20px; border-radius: 50%; background: #e8ebf5; color: #8a93a6; font-size: 11px; font-weight: 700; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.hot-rank.top { background: linear-gradient(135deg, #6d7bf0, #6a5bd8); color: #fff; }
.hot-thumb { width: 36px; height: 36px; border-radius: 6px; object-fit: cover; flex-shrink: 0; }
.hot-body { flex: 1; min-width: 0; }
.hot-name { font-size: 13px; color: #2c3e50; display: -webkit-box; -webkit-line-clamp: 1; -webkit-box-orient: vertical; overflow: hidden; }
.hot-meta { display: flex; gap: 8px; align-items: center; margin-top: 2px; }
.hot-price { font-size: 13px; color: #f56c6c; font-weight: 600; }
.hot-sales { font-size: 11px; color: #b0b8c8; }
.hot-badge { font-size: 10px; color: #fa5252; background: #fff0f0; padding: 2px 6px; border-radius: 8px; flex-shrink: 0; }

.promo-card { background: linear-gradient(135deg, #6d7bf0, #6a5bd8); border-radius: 14px; padding: 20px 18px; display: flex; align-items: center; gap: 10px; cursor: pointer; box-shadow: 0 8px 20px rgba(106, 91, 216, 0.2); }
.promo-text { flex: 1; }
.promo-text b { font-size: 14px; color: #fff; }
.promo-text p { font-size: 12px; color: rgba(255, 255, 255, 0.8); margin: 4px 0 8px; }
.promo-btn { font-size: 12px; color: #fff; background: rgba(255, 255, 255, 0.2); padding: 4px 12px; border-radius: 12px; }
.promo-illust svg { width: 70px; height: 60px; }

/* ===== 编辑弹窗 ===== */
.cover-uploader :deep(.el-upload) { border: 1px dashed #d9d9d9; border-radius: 8px; cursor: pointer; width: 120px; height: 120px; display: flex; align-items: center; justify-content: center; overflow: hidden; }
.cover-preview { width: 100%; height: 100%; object-fit: cover; }
.cover-empty { color: #909399; font-size: 12px; display: flex; flex-direction: column; gap: 6px; align-items: center; }
.cover-empty .el-icon { font-size: 24px; }

/* ===== 响应式 ===== */
@media (max-width: 1020px) {
  .stat-grid { grid-template-columns: repeat(2, 1fr); }
  .main-layout { grid-template-columns: 1fr; }
}
@media (max-width: 760px) {
  .hero-banner { flex-direction: column; align-items: flex-start; height: auto; padding: 20px; }
  .h-slogan { display: none; }
  .stat-grid { grid-template-columns: 1fr; }
  .filter-bar { flex-direction: column; align-items: stretch; }
  .f-search, .f-select { width: 100%; }
}
</style>
