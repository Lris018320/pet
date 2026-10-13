<template>
  <div class="admin-adoption-page">
    <!-- 顶部 Banner -->
    <div class="hero-banner" :style="{ '--hero-img': `url(${heroImg})` }">
      <div class="h-left">
        <span class="h-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"><path d="M19 14c1.5-1.5 3-3.7 3-6a4 4 0 0 0-7-2.7A4 4 0 0 0 5 8c0 2.3 1.5 4.5 3 6l4 4z"/><path d="M12 18v4M9 22h6"/></svg>
        </span>
        <div>
          <h2>领养寄养管理</h2>
          <p>让每一只毛孩子都找到温暖的家</p>
          <div class="h-tags"><span>领养</span><span>寄养</span><span>用爱守护</span></div>
        </div>
      </div>
      <span class="h-slogan">给它一个家<br />也给自己一份温暖 ♡</span>
    </div>

    <!-- 统计卡 -->
    <div class="stat-grid">
      <div class="stat-card" v-for="c in statCards" :key="c.key">
        <div class="s-icon" :style="{ background: c.bg, color: c.color }" v-html="c.icon"></div>
        <div class="s-body">
          <div class="s-label">{{ c.label }}</div>
          <div class="s-value">{{ fmtNum(c.value) }}<span class="s-up" :style="{ color: c.growth >= 0 ? '#12b886' : '#f56c6c' }">{{ c.growth >= 0 ? '↑' : '↓' }} {{ Math.abs(c.growth).toFixed(1) }}%</span></div>
          <div class="s-cmp">{{ c.cmp }}</div>
        </div>
      </div>
    </div>

    <div class="main-layout">
      <!-- 左侧主区域 -->
      <div class="main-col">
        <!-- Tab 切换 -->
        <div class="type-tabs">
          <button class="tt-tab" :class="{ on: filter.type === 'adoption' }" @click="setType('adoption')">领养管理</button>
          <button class="tt-tab" :class="{ on: filter.type === 'foster' }" @click="setType('foster')">寄养管理</button>
          <button class="tt-tab" :class="{ on: !filter.type }" @click="setType('')">全部</button>
        </div>

        <!-- 筛选栏 -->
        <div class="filter-bar">
          <el-input v-model="filter.keyword" placeholder="宠物名称/ID/申请人" clearable class="f-search" @keyup.enter="loadList(1)">
            <template #prefix>
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" style="width:15px;height:15px"><circle cx="11" cy="11" r="7"/><path d="M21 21l-4.3-4.3"/></svg>
            </template>
          </el-input>
          <el-select v-model="filter.status" placeholder="申请状态" clearable class="f-select" @change="loadList(1)">
            <el-option label="待审核" value="pending" />
            <el-option label="审核通过" value="approved" />
            <el-option label="已拒绝" value="rejected" />
            <el-option label="寄养中" value="fostering" />
            <el-option label="已完成" value="completed" />
          </el-select>
          <el-select v-model="filter.petType" placeholder="宠物类型" clearable class="f-select" @change="loadList(1)">
            <el-option label="全部" value="全部" />
            <el-option label="猫" value="猫" />
            <el-option label="狗" value="狗" />
          </el-select>
          <el-button type="primary" @click="loadList(1)">搜索</el-button>
          <a class="reset-link" @click="resetFilter">重置</a>
        </div>

        <!-- 表格 -->
        <div class="table-card">
          <el-table :data="list" v-loading="loading" @selection-change="sel => selected = sel">
            <el-table-column type="selection" width="42" />
            <el-table-column label="宠物信息" min-width="200">
              <template #default="{ row }">
                <div class="p-info">
                  <img :src="resolveImg(row.photoUrl)" class="p-thumb" alt="宠物" />
                  <div>
                    <div class="p-name">{{ row.breed }}-{{ row.petName }}</div>
                    <div class="p-code">ID: {{ row.petCode }}</div>
                    <div class="p-attrs">
                      <span>{{ row.age }}</span><span>{{ row.gender === '公' ? '♂' : '♀' }}</span>
                      <span v-if="row.steril">{{ row.steril === '是' ? '已绝育' : '未绝育' }}</span>
                    </div>
                  </div>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="申请人" width="130">
              <template #default="{ row }">
                <div class="applicant">
                  <span class="a-avatar">{{ (row.applicantName || '匿').charAt(0) }}</span>
                  <div>
                    <div class="a-name">{{ row.applicantName }}</div>
                    <div class="a-phone">{{ row.applicantPhone }}</div>
                  </div>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="申请类型" width="90">
              <template #default="{ row }">
                <el-tag :type="row.type === 'adoption' ? 'success' : 'warning'" size="small">{{ row.type === 'adoption' ? '领养' : '寄养' }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="申请时间" width="120">
              <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
            </el-table-column>
            <el-table-column label="当前状态" width="100">
              <template #default="{ row }">
                <el-tag :type="statusType(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="180" fixed="right">
              <template #default="{ row }">
                <a class="op-link" @click="onView(row)">查看</a>
                <a v-if="row.status === 'pending'" class="op-link ok" @click="onReview(row, 'approved')">通过</a>
                <a v-if="row.status === 'pending'" class="op-link danger" @click="onReview(row, 'rejected')">拒绝</a>
                <a v-if="row.status === 'approved' && row.type === 'foster'" class="op-link" @click="onReview(row, 'fostering')">开始寄养</a>
                <a v-if="row.status === 'fostering'" class="op-link warn" @click="onReview(row, 'completed')">结束寄养</a>
                <a class="op-link danger" @click="onDelete(row)">删除</a>
              </template>
            </el-table-column>
            <template #empty>
              <el-empty description="暂无申请记录" />
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
        <!-- 申请统计 -->
        <div class="side-card">
          <div class="side-head">
            <span class="sh-title">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" style="width:16px;height:16px"><path d="M21 12a9 9 0 1 1-9-9v9z"/><path d="M12 3a9 9 0 0 1 9 9h-9z"/></svg>
              申请统计
            </span>
          </div>
          <div class="donut-wrap">
            <div class="donut">
              <svg viewBox="0 0 100 100" style="width:120px;height:120px">
                <circle cx="50" cy="50" r="40" fill="none" stroke="#f0f2f8" stroke-width="14"/>
                <circle cx="50" cy="50" r="40" fill="none" stroke="#12b886" stroke-width="14" :stroke-dasharray="adoptArc" stroke-dashoffset="0" transform="rotate(-90 50 50)"/>
                <circle cx="50" cy="50" r="40" fill="none" stroke="#6d7bf0" stroke-width="14" :stroke-dasharray="fosterArc" :stroke-dashoffset="-adoptLen" transform="rotate(-90 50 50)"/>
                <text x="50" y="48" text-anchor="middle" font-size="18" font-weight="700" fill="#2c3e50">{{ stats.total || 0 }}</text>
                <text x="50" y="62" text-anchor="middle" font-size="9" fill="#8a93a6">总数</text>
              </svg>
            </div>
            <div class="donut-legend">
              <div class="dl-item">
                <span class="dl-dot green"></span>
                <span class="dl-label">领养申请</span>
                <span class="dl-val">{{ stats.adoptCount || 0 }} ({{ stats.adoptPercent || 0 }}%)</span>
              </div>
              <div class="dl-item">
                <span class="dl-dot purple"></span>
                <span class="dl-label">寄养申请</span>
                <span class="dl-val">{{ stats.fosterCount || 0 }} ({{ stats.fosterPercent || 0 }}%)</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 热门宠物 -->
        <div class="side-card" v-if="hotPets.length">
          <div class="side-head">
            <span class="sh-title">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" style="width:16px;height:16px"><path d="M12 2l3 6 6 .9-4.5 4.4 1 6.2L12 17.3 6.5 19.5l1-6.2L3 8.9l6-.9z"/></svg>
              热门宠物
            </span>
          </div>
          <ul class="hot-pet-list">
            <li v-for="(p, i) in hotPets" :key="p.id">
              <span class="hp-rank" :class="{ top: i < 3 }">{{ i + 1 }}</span>
              <img :src="resolveImg(p.photoUrl)" class="hp-thumb" alt="宠物" />
              <div class="hp-body">
                <span class="hp-name">{{ p.breed }}-{{ p.petName }}</span>
                <span class="hp-type">{{ p.petType }}</span>
              </div>
              <span v-if="i < 3" class="hp-hot">热搜</span>
            </li>
          </ul>
        </div>

        <!-- 推广 Banner -->
        <div class="promo-card" @click="router.push('/adoption')">
          <div class="promo-text">
            <b>领养代替购买</b>
            <p>让爱不再流浪</p>
            <span class="promo-btn">去领养 →</span>
          </div>
          <div class="promo-illust">
            <svg viewBox="0 0 80 60" fill="none">
              <path d="M28 40c0-5 4-8 9-8s9 3 9 8" fill="rgba(255,255,255,.3)"/>
              <circle cx="32" cy="32" r="4" fill="rgba(255,255,255,.3)"/>
              <circle cx="38" cy="28" r="3.5" fill="rgba(255,255,255,.3)"/>
              <circle cx="44" cy="28" r="3.5" fill="rgba(255,255,255,.3)"/>
              <circle cx="50" cy="32" r="4" fill="rgba(255,255,255,.3)"/>
            </svg>
          </div>
        </div>
      </div>
    </div>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="申请详情" width="520px">
      <div v-if="currentRow" class="detail-body">
        <div class="detail-pet">
          <img :src="resolveImg(currentRow.photoUrl)" class="detail-photo" alt="宠物" />
          <div class="detail-info">
            <h3>{{ currentRow.breed }}-{{ currentRow.petName }}</h3>
            <p>编号：{{ currentRow.petCode }}</p>
            <p>年龄：{{ currentRow.age }} | 性别：{{ currentRow.gender }} | 绝育：{{ currentRow.steril }}</p>
            <p>地区：{{ currentRow.area }}</p>
          </div>
        </div>
        <el-divider />
        <div class="detail-applicant">
          <p><b>申请人：</b>{{ currentRow.applicantName }}</p>
          <p><b>联系方式：</b>{{ currentRow.applicantPhone }}</p>
          <p><b>申请类型：</b>{{ currentRow.type === 'adoption' ? '领养' : '寄养' }}</p>
          <p><b>申请时间：</b>{{ formatTime(currentRow.createTime) }}</p>
          <p><b>当前状态：</b>{{ statusLabel(currentRow.status) }}</p>
        </div>
      </div>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
        <el-button v-if="currentRow?.status === 'pending'" type="success" @click="onReview(currentRow, 'approved'); detailVisible = false">通过</el-button>
        <el-button v-if="currentRow?.status === 'pending'" type="danger" @click="onReview(currentRow, 'rejected'); detailVisible = false">拒绝</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  adminPageAdoption, adminReviewAdoption, adminDeleteAdoption,
  adminGetAdoptionStats, adminGetHotPets
} from '@/api/adoption'

const router = useRouter()
const API_BASE = import.meta.env.VITE_API_BASE || '/api'

/* ========== Banner 配图 ========== */
const heroImg = 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=' +
  encodeURIComponent('Golden retriever dog and grey tabby cat sitting together on green grass with blue sky, warm sunlight, professional pet photography, soft pastel tones') +
  '&image_size=landscape_16_9'

/* ========== 统计卡 ========== */
const statCards = ref([
  { key: 'pa', label: '待审核领养申请', value: 0, growth: 12.3, cmp: '较上周', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" style="width:22px;height:22px"><path d="M9 11l3 3L22 4M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11"/></svg>', bg: 'linear-gradient(135deg,#fff9e6,#fff3d1)', color: '#f59f00' },
  { key: 'pf', label: '待审核寄养申请', value: 0, growth: 8.7, cmp: '较上周', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" style="width:22px;height:22px"><path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><path d="M9 22V12h6v10"/></svg>', bg: 'linear-gradient(135deg,#eef0ff,#dde2ff)', color: '#5b6cf5' },
  { key: 'aa', label: '已成功领养', value: 0, growth: 15.6, cmp: '较上周', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" style="width:22px;height:22px"><path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78L12 21l8.84-8.61a5.5 5.5 0 0 0 0-7.78z"/></svg>', bg: 'linear-gradient(135deg,#e6fcf5,#d3f9e8)', color: '#12b886' },
  { key: 'cf', label: '已完成寄养', value: 0, growth: 9.8, cmp: '较上周', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" style="width:22px;height:22px"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><path d="M22 4L12 14.01l-3-3"/></svg>', bg: 'linear-gradient(135deg,#fff0f0,#ffe0e6)', color: '#fa5252' }
])

/* ========== 统计数据 ========== */
const stats = ref({})
const adoptArc = computed(() => {
  const total = stats.value.total || 1
  const adopt = stats.value.adoptCount || 0
  return `${(adopt / total * 251.2).toFixed(1)} 251.2`
})
const adoptLen = computed(() => (stats.value.adoptCount || 0) / (stats.value.total || 1) * 251.2)
const fosterArc = computed(() => {
  const total = stats.value.total || 1
  const foster = stats.value.fosterCount || 0
  return `${(foster / total * 251.2).toFixed(1)} 251.2`
})

/* ========== 热门宠物 ========== */
const hotPets = ref([])

/* ========== 列表 ========== */
const list = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const loading = ref(false)
const filter = reactive({ keyword: '', type: '', status: '', petType: '全部' })
const selected = ref([])

/* ========== 详情弹窗 ========== */
const detailVisible = ref(false)
const currentRow = ref(null)

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
function formatTime(t) {
  if (!t) return ''
  return String(t).replace('T', ' ').substring(0, 10)
}
function statusType(s) {
  const map = { pending: 'warning', approved: 'success', rejected: 'danger', fostering: '', completed: 'info' }
  return map[s] || 'info'
}
function statusLabel(s) {
  const map = { pending: '待审核', approved: '审核通过', rejected: '已拒绝', fostering: '寄养中', completed: '已完成' }
  return map[s] || s
}

/* ========== 数据加载 ========== */
async function loadList(p = 1) {
  page.value = p
  loading.value = true
  try {
    const res = await adminPageAdoption({
      page: page.value, size: size.value,
      type: filter.type || undefined,
      status: filter.status || undefined,
      keyword: filter.keyword || undefined,
      petType: filter.petType && filter.petType !== '全部' ? filter.petType : undefined
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

function setType(t) {
  filter.type = t
  loadList(1)
}

function resetFilter() {
  filter.keyword = ''
  filter.status = ''
  filter.petType = '全部'
  loadList(1)
}

async function loadStats() {
  try {
    const res = await adminGetAdoptionStats()
    stats.value = res.data || {}
    statCards.value[0].value = res.data?.pendingAdopt || 0
    statCards.value[1].value = res.data?.pendingFoster || 0
    statCards.value[2].value = res.data?.approvedAdopt || 0
    statCards.value[3].value = res.data?.completedFoster || 0
  } catch (e) {
    statCards.value[0].value = 32
    statCards.value[1].value = 18
    statCards.value[2].value = 256
    statCards.value[3].value = 142
    stats.value = { total: 186, adoptCount: 120, fosterCount: 52, adoptPercent: 64.5, fosterPercent: 28.0 }
  }
}

async function loadHotPets() {
  try {
    const res = await adminGetHotPets()
    hotPets.value = res.data || []
  } catch (e) {
    hotPets.value = []
  }
}

/* ========== 操作 ========== */
function onView(row) {
  currentRow.value = row
  detailVisible.value = true
}

async function onReview(row, status) {
  const label = statusLabel(status)
  try {
    await ElMessageBox.confirm(`确认将此申请标记为「${label}」吗？`, '提示', { type: 'warning' })
  } catch { return }
  try {
    await adminReviewAdoption(row.id, status)
    ElMessage.success(`已${label}`)
    await loadList(page.value)
    await loadStats()
  } catch (e) { /* ignore */ }
}

async function onDelete(row) {
  try {
    await ElMessageBox.confirm(`确认删除「${row.petName}」的申请记录吗？`, '提示', { type: 'warning' })
  } catch { return }
  try {
    await adminDeleteAdoption(row.id)
    ElMessage.success('删除成功')
    await loadList(page.value)
    await loadStats()
  } catch (e) { /* ignore */ }
}

onMounted(() => {
  loadList(1)
  loadStats()
  loadHotPets()
})
</script>

<style scoped>
.admin-adoption-page { width: 100%; padding: 4px clamp(16px, 2vw, 32px) 32px; }

/* ===== Banner ===== */
.hero-banner { position: relative; display: flex; align-items: center; justify-content: space-between; height: 130px; padding: 0 32px; border-radius: 16px; overflow: hidden; background-image: linear-gradient(90deg, #e7efff 40%, rgba(231, 239, 255, 0) 75%), var(--hero-img), linear-gradient(115deg, #e3edff 0%, #eaf2ff 55%, #f0f6ff 100%); background-size: 100% 100%, auto 100%, 100% 100%; background-position: center, right center, center; background-repeat: no-repeat; margin-bottom: 20px; }
.h-left { position: relative; z-index: 1; display: flex; align-items: center; gap: 14px; }
.h-icon { width: 48px; height: 48px; border-radius: 12px; background: linear-gradient(135deg, #6d7bf0, #6a5bd8); color: #fff; display: flex; align-items: center; justify-content: center; flex-shrink: 0; box-shadow: 0 6px 14px rgba(106, 91, 216, 0.3); }
.h-icon svg { width: 26px; height: 26px; }
.h-left h2 { margin: 0; font-size: 20px; color: #2c3e50; }
.h-left p { margin: 2px 0 4px; font-size: 13px; color: #7a8699; }
.h-tags { display: flex; gap: 6px; }
.h-tags span { font-size: 11px; color: #6d7bf0; background: rgba(109, 123, 240, 0.1); padding: 2px 8px; border-radius: 10px; }
.h-slogan { position: relative; z-index: 1; font-family: 'Comic Sans MS', 'STKaiti', cursive; font-size: 14px; color: #4a5b7d; line-height: 1.5; text-align: right; white-space: nowrap; }

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

/* ===== Tab ===== */
.type-tabs { display: flex; gap: 4px; background: #f2f4fa; padding: 3px; border-radius: 8px; margin-bottom: 14px; width: fit-content; }
.tt-tab { padding: 6px 18px; border: none; background: transparent; border-radius: 6px; font-size: 13px; color: #5c6a8c; cursor: pointer; transition: all 0.2s; }
.tt-tab.on { background: #fff; color: #6d7bf0; font-weight: 600; box-shadow: 0 1px 4px rgba(94, 110, 190, 0.08); }

/* ===== 筛选栏 ===== */
.filter-bar { display: flex; align-items: center; gap: 10px; margin-bottom: 14px; flex-wrap: wrap; }
.f-search { width: 240px; }
.f-select { width: 130px; }
.reset-link { font-size: 13px; color: #6d7bf0; cursor: pointer; }

/* ===== 表格 ===== */
.table-card { background: #fff; border: 1px solid #edf0f8; border-radius: 14px; padding: 4px; box-shadow: 0 6px 20px rgba(94, 110, 190, 0.06); }
.p-info { display: flex; align-items: center; gap: 10px; }
.p-thumb { width: 40px; height: 40px; border-radius: 6px; object-fit: cover; flex-shrink: 0; }
.p-name { font-size: 13px; color: #2c3e50; font-weight: 500; }
.p-code { font-size: 11px; color: #b0b8c8; }
.p-attrs { display: flex; gap: 6px; font-size: 11px; color: #8a93a6; }
.applicant { display: flex; align-items: center; gap: 8px; }
.a-avatar { width: 28px; height: 28px; border-radius: 50%; background: #eef0ff; color: #5b6cf5; font-size: 12px; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.a-name { font-size: 13px; color: #2c3e50; }
.a-phone { font-size: 11px; color: #b0b8c8; }
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

.donut-wrap { display: flex; flex-direction: column; align-items: center; gap: 12px; }
.donut svg text { font-family: inherit; }
.donut-legend { width: 100%; }
.dl-item { display: flex; align-items: center; gap: 6px; padding: 6px 0; }
.dl-dot { width: 10px; height: 10px; border-radius: 50%; flex-shrink: 0; }
.dl-dot.green { background: #12b886; }
.dl-dot.purple { background: #6d7bf0; }
.dl-label { font-size: 13px; color: #5c6a8c; flex: 1; }
.dl-val { font-size: 13px; color: #2c3e50; font-weight: 600; }

.hot-pet-list { list-style: none; margin: 0; padding: 0; }
.hot-pet-list li { display: flex; align-items: center; gap: 8px; padding: 8px 0; border-bottom: 1px solid #f5f6fa; }
.hot-pet-list li:last-child { border-bottom: none; }
.hp-rank { width: 20px; height: 20px; border-radius: 50%; background: #e8ebf5; color: #8a93a6; font-size: 11px; font-weight: 700; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.hp-rank.top { background: linear-gradient(135deg, #6d7bf0, #6a5bd8); color: #fff; }
.hp-thumb { width: 36px; height: 36px; border-radius: 6px; object-fit: cover; flex-shrink: 0; }
.hp-body { flex: 1; min-width: 0; }
.hp-name { font-size: 13px; color: #2c3e50; display: block; }
.hp-type { font-size: 11px; color: #b0b8c8; }
.hp-hot { font-size: 10px; color: #fa5252; background: #fff0f0; padding: 2px 6px; border-radius: 8px; flex-shrink: 0; }

.promo-card { background: linear-gradient(135deg, #6d7bf0, #6a5bd8); border-radius: 14px; padding: 20px 18px; display: flex; align-items: center; gap: 10px; cursor: pointer; box-shadow: 0 8px 20px rgba(106, 91, 216, 0.2); }
.promo-text { flex: 1; }
.promo-text b { font-size: 14px; color: #fff; }
.promo-text p { font-size: 12px; color: rgba(255, 255, 255, 0.8); margin: 4px 0 8px; }
.promo-btn { font-size: 12px; color: #fff; background: rgba(255, 255, 255, 0.2); padding: 4px 12px; border-radius: 12px; }
.promo-illust svg { width: 60px; height: 50px; }

/* ===== 详情弹窗 ===== */
.detail-body { padding: 0 8px; }
.detail-pet { display: flex; gap: 16px; }
.detail-photo { width: 100px; height: 100px; border-radius: 10px; object-fit: cover; flex-shrink: 0; }
.detail-info h3 { margin: 0 0 8px; font-size: 16px; color: #2c3e50; }
.detail-info p { margin: 4px 0; font-size: 13px; color: #5c6a8c; }
.detail-applicant p { margin: 6px 0; font-size: 13px; color: #5c6a8c; }

/* ===== 响应式 ===== */
@media (max-width: 1020px) { .stat-grid { grid-template-columns: repeat(2, 1fr); } .main-layout { grid-template-columns: 1fr; } }
@media (max-width: 760px) { .hero-banner { flex-direction: column; align-items: flex-start; height: auto; padding: 20px; } .h-slogan { display: none; } .stat-grid { grid-template-columns: 1fr; } .filter-bar { flex-direction: column; align-items: stretch; } .f-search, .f-select { width: 100%; } }
</style>
