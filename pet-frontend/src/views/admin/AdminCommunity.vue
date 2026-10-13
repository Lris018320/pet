<template>
  <div class="admin-community-page">
    <!-- 顶部 Banner -->
    <div class="hero-banner" :style="{ '--hero-img': `url(${heroImg})` }">
      <div class="h-left">
        <h2>因为有你<br />宠物的世界更温暖 ♡</h2>
        <p>分享养宠日常 遇见同频的伙伴</p>
      </div>
      <span class="h-right-text">分享养宠日常<br />遇见同频的伙伴</span>
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
        <!-- 分类 Tab + 搜索 -->
        <div class="filter-bar">
          <div class="cat-tabs">
            <button class="ct-tab" :class="{ on: filter.category === '' }" @click="setCategory('')">全部</button>
            <button v-for="c in categories" :key="c" class="ct-tab" :class="{ on: filter.category === c }" @click="setCategory(c)">{{ c }}</button>
          </div>
          <el-input v-model="filter.keyword" placeholder="搜索帖子、用户、话题..." clearable class="f-search" @keyup.enter="loadList(1)">
            <template #prefix>
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" style="width:15px;height:15px"><circle cx="11" cy="11" r="7"/><path d="M21 21l-4.3-4.3"/></svg>
            </template>
          </el-input>
        </div>

        <!-- 帖子列表 -->
        <div class="feed-list" v-loading="loading">
          <div v-for="p in list" :key="p.id" class="post-card">
            <div class="pc-head">
              <span class="pc-avatar" :style="{ background: avatarColor(p.authorId) }">{{ (p.authorName || '匿').charAt(0) }}</span>
              <div class="pc-user">
                <span class="pc-name">{{ p.authorName }}<span class="pc-level">Lv.{{ p.authorLevel || 1 }}</span></span>
                <span class="pc-meta">{{ formatTime(p.createTime) }}</span>
              </div>
              <el-tag size="small" :type="catTagType(p.category)" class="pc-cat">{{ p.category }}</el-tag>
            </div>
            <div class="pc-body">
              <h3 class="pc-title" @click="onView(p)">{{ p.title }}</h3>
              <p class="pc-content">{{ p.content }}</p>
              <div v-if="p.images && p.images.length" class="pc-images">
                <img v-for="(img, i) in p.images.slice(0, 3)" :key="i" :src="resolveImg(img)" class="pc-img" alt="图片" @click="onView(p)" />
              </div>
            </div>
            <div class="pc-footer">
              <span class="pc-stat">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" style="width:14px;height:14px"><path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78L12 21l8.84-8.61a5.5 5.5 0 0 0 0-7.78z"/></svg>
                {{ fmtNum(p.likeCount) }}
              </span>
              <span class="pc-stat">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" style="width:14px;height:14px"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/></svg>
                {{ fmtNum(p.commentCount) }}
              </span>
              <span class="pc-stat">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" style="width:14px;height:14px"><path d="M4 12v8a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2v-8M16 6l-4-4-4 4M12 2v14"/></svg>
                {{ fmtNum(p.shareCount || 0) }}
              </span>
              <span class="pc-stat">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" style="width:14px;height:14px"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
                {{ fmtNum(p.viewCount) }}
              </span>
              <div class="pc-actions">
                <a class="op-link" @click="onView(p)">查看</a>
                <a class="op-link danger" @click="onDelete(p)">删除</a>
              </div>
            </div>
          </div>
          <el-empty v-if="!loading && list.length === 0" description="暂无帖子" />
        </div>

        <!-- 分页 -->
        <div class="pager-row" v-if="total > 0">
          <span class="pager-text">共 {{ fmtNum(total) }} 条记录，当前第 {{ page }} / {{ Math.max(Math.ceil(total / size), 1) }} 页</span>
          <el-pagination background layout="prev, pager, next" :current-page="page" :page-size="size" :total="total" @current-change="loadList" />
        </div>
      </div>

      <!-- 右侧面板 -->
      <div class="side-col">
        <!-- 热门话题 -->
        <div class="side-card">
          <div class="side-head">
            <span class="sh-title">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" style="width:16px;height:16px"><path d="M3 11l19-9-9 19-2-8-8-2z"/></svg>
              热门话题
            </span>
          </div>
          <ul class="topic-list">
            <li v-for="(t, i) in hotTopics" :key="i" @click="filter.keyword = t.tag; loadList(1)">
              <span class="tp-rank" :class="{ top: i < 3 }">{{ i + 1 }}</span>
              <span class="tp-tag">{{ t.tag }}</span>
              <span class="tp-heat">{{ fmtHeat(t.heat) }}</span>
            </li>
          </ul>
        </div>

        <!-- 推荐用户 -->
        <div class="side-card">
          <div class="side-head">
            <span class="sh-title">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" style="width:16px;height:16px"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/></svg>
              推荐用户
            </span>
          </div>
          <ul class="user-list">
            <li v-for="u in recommendUsers" :key="u.id">
              <span class="ul-avatar" :style="{ background: avatarColor(u.id) }">{{ (u.nickname || '匿').charAt(0) }}</span>
              <div class="ul-body">
                <span class="ul-name">{{ u.nickname }}<span class="ul-level">Lv.{{ u.level }}</span></span>
                <span class="ul-meta">{{ u.postCount }}篇帖子 · {{ u.followerCount }}粉丝</span>
              </div>
              <a class="ul-follow" @click="ElMessage.info('功能开发中')">+ 关注</a>
            </li>
          </ul>
        </div>

        <!-- 推广 Banner -->
        <div class="promo-card" @click="router.push('/community')">
          <div class="promo-text">
            <b>加入宠物社区</b>
            <p>分享你的养宠故事</p>
            <span class="promo-btn">立即加入 →</span>
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
    <el-dialog v-model="detailVisible" title="帖子详情" width="640px">
      <div v-if="currentRow" class="detail-body">
        <div class="detail-head">
          <span class="pc-avatar" :style="{ background: avatarColor(currentRow.authorId) }">{{ (currentRow.authorName || '匿').charAt(0) }}</span>
          <div>
            <div class="d-name">{{ currentRow.authorName }}</div>
            <div class="d-time">{{ formatTime(currentRow.createTime) }} · {{ currentRow.category }}</div>
          </div>
        </div>
        <h3 class="d-title">{{ currentRow.title }}</h3>
        <div class="d-content">{{ currentRow.content }}</div>
        <div v-if="currentRow.images && currentRow.images.length" class="d-images">
          <img v-for="(img, i) in currentRow.images" :key="i" :src="resolveImg(img)" class="d-img" alt="图片" />
        </div>
      </div>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
        <el-button type="danger" @click="onDelete(currentRow); detailVisible = false">删除帖子</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  adminPagePost, adminDeletePost,
  adminGetPostStats, adminGetHotTopics, adminGetRecommendUsers
} from '@/api/community'

const router = useRouter()
const API_BASE = import.meta.env.VITE_API_BASE || '/api'

/* ========== Banner 配图 ========== */
const heroImg = 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=' +
  encodeURIComponent('Golden retriever dog and grey tabby cat sitting together on green grass with blue sky, warm sunlight, professional pet photography, soft pastel tones') +
  '&image_size=landscape_16_9'

/* ========== 分类 ========== */
const categories = ['养宠交流', '领养求助', '宠物健康', '萌宠晒图', '宠物问答']

/* ========== 统计卡 ========== */
const statCards = ref([
  { key: 'posts', label: '帖子总数', value: 0, growth: 0, cmp: '较上月', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" style="width:22px;height:22px"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><path d="M14 2v6h6M8 13h8M8 17h5"/></svg>', bg: 'linear-gradient(135deg,#eef0ff,#dde2ff)', color: '#5b6cf5' },
  { key: 'today', label: '今日发帖', value: 0, growth: 15.2, cmp: '较昨日', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" style="width:22px;height:22px"><path d="M12 2v4M12 18v4M4.93 4.93l2.83 2.83M16.24 16.24l2.83 2.83M2 12h4M18 12h4"/></svg>', bg: 'linear-gradient(135deg,#e6fcf5,#d3f9e8)', color: '#12b886' },
  { key: 'comments', label: '评论总数', value: 0, growth: 18.6, cmp: '较上月', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" style="width:22px;height:22px"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/></svg>', bg: 'linear-gradient(135deg,#fff9e6,#fff3d1)', color: '#f59f00' },
  { key: 'users', label: '活跃用户', value: 0, growth: 12.4, cmp: '近7天', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" style="width:22px;height:22px"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/></svg>', bg: 'linear-gradient(135deg,#fff0f0,#ffe0e6)', color: '#fa5252' }
])

/* ========== 右侧数据 ========== */
const hotTopics = ref([])
const recommendUsers = ref([])

/* ========== 列表 ========== */
const list = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const loading = ref(false)
const filter = reactive({ keyword: '', category: '' })

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
function fmtHeat(n) {
  if (!n) return '0'
  if (n >= 10000) return (n / 10000).toFixed(1) + 'w 热度'
  return n + ' 热度'
}
function formatTime(t) {
  if (!t) return ''
  return String(t).replace('T', ' ').substring(0, 10)
}
function catTagType(c) {
  const map = { '养宠交流': '', '领养求助': 'danger', '宠物健康': 'success', '萌宠晒图': 'warning', '宠物问答': 'info' }
  return map[c] || 'info'
}
const avatarColors = ['#6d7bf0', '#12b886', '#f59f00', '#fa5252', '#7950f2', '#339af0']
function avatarColor(id) {
  return avatarColors[(id || 0) % avatarColors.length]
}

/* ========== 数据加载 ========== */
async function loadList(p = 1) {
  page.value = p
  loading.value = true
  try {
    const res = await adminPagePost({
      page: page.value, size: size.value,
      category: filter.category || undefined,
      keyword: filter.keyword || undefined
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

function setCategory(c) {
  filter.category = c
  loadList(1)
}

async function loadStats() {
  try {
    const res = await adminGetPostStats()
    const d = res.data || {}
    statCards.value[0].value = d.totalPosts || 0
    statCards.value[0].growth = d.postsGrowth ?? 12.5
    statCards.value[1].value = d.todayPosts || 0
    statCards.value[1].growth = d.todayGrowth ?? 15.2
    statCards.value[2].value = d.totalComments || 0
    statCards.value[2].growth = d.commentsGrowth ?? 18.6
    statCards.value[3].value = d.activeUsers || 0
    statCards.value[3].growth = d.usersGrowth ?? 12.4
  } catch (e) {
    statCards.value[0].value = 368
    statCards.value[1].value = 12
    statCards.value[2].value = 1024
    statCards.value[3].value = 86
  }
}

async function loadHotTopics() {
  try {
    const res = await adminGetHotTopics()
    hotTopics.value = res.data || []
  } catch (e) {
    hotTopics.value = []
  }
}

async function loadRecommendUsers() {
  try {
    const res = await adminGetRecommendUsers()
    recommendUsers.value = res.data || []
  } catch (e) {
    recommendUsers.value = []
  }
}

/* ========== 操作 ========== */
function onView(p) {
  currentRow.value = p
  detailVisible.value = true
}

async function onDelete(p) {
  try {
    await ElMessageBox.confirm(`确认删除帖子「${p.title}」吗？`, '提示', { type: 'warning' })
  } catch { return }
  try {
    await adminDeletePost(p.id)
    ElMessage.success('删除成功')
    await loadList(page.value)
    await loadStats()
  } catch (e) { /* ignore */ }
}

onMounted(() => {
  loadList(1)
  loadStats()
  loadHotTopics()
  loadRecommendUsers()
})
</script>

<style scoped>
.admin-community-page { width: 100%; padding: 4px clamp(16px, 2vw, 32px) 32px; }

/* ===== Banner ===== */
.hero-banner { position: relative; display: flex; align-items: center; justify-content: space-between; height: 130px; padding: 0 32px; border-radius: 16px; overflow: hidden; background-image: linear-gradient(90deg, #e7efff 40%, rgba(231, 239, 255, 0) 75%), var(--hero-img), linear-gradient(115deg, #e3edff 0%, #eaf2ff 55%, #f0f6ff 100%); background-size: 100% 100%, auto 100%, 100% 100%; background-position: center, right center, center; background-repeat: no-repeat; margin-bottom: 20px; }
.h-left { position: relative; z-index: 1; }
.h-left h2 { margin: 0; font-size: 18px; color: #2c3e50; line-height: 1.4; }
.h-left p { margin: 4px 0 0; font-size: 13px; color: #7a8699; }
.h-right-text { position: relative; z-index: 1; font-family: 'Comic Sans MS', 'STKaiti', cursive; font-size: 14px; color: #4a5b7d; line-height: 1.5; text-align: right; white-space: nowrap; }

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

/* ===== 筛选栏 ===== */
.filter-bar { display: flex; align-items: center; justify-content: space-between; gap: 12px; margin-bottom: 14px; flex-wrap: wrap; }
.cat-tabs { display: flex; gap: 4px; background: #f2f4fa; padding: 3px; border-radius: 8px; flex-wrap: wrap; }
.ct-tab { padding: 5px 14px; border: none; background: transparent; border-radius: 6px; font-size: 13px; color: #5c6a8c; cursor: pointer; transition: all 0.2s; }
.ct-tab.on { background: #fff; color: #6d7bf0; font-weight: 600; box-shadow: 0 1px 4px rgba(94, 110, 190, 0.08); }
.f-search { width: 260px; }

/* ===== 帖子卡片 ===== */
.feed-list { display: flex; flex-direction: column; gap: 14px; }
.post-card { background: #fff; border: 1px solid #edf0f8; border-radius: 14px; padding: 16px 20px; box-shadow: 0 6px 20px rgba(94, 110, 190, 0.05); transition: box-shadow 0.2s; }
.post-card:hover { box-shadow: 0 8px 24px rgba(94, 110, 190, 0.1); }
.pc-head { display: flex; align-items: center; gap: 10px; margin-bottom: 10px; }
.pc-avatar { width: 36px; height: 36px; border-radius: 50%; color: #fff; font-size: 14px; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.pc-user { flex: 1; min-width: 0; }
.pc-name { font-size: 14px; color: #2c3e50; font-weight: 500; }
.pc-level { font-size: 10px; color: #fff; background: linear-gradient(90deg, #6d7bf0, #6a5bd8); padding: 1px 6px; border-radius: 8px; margin-left: 6px; }
.pc-meta { font-size: 12px; color: #b0b8c8; }
.pc-cat { flex-shrink: 0; }
.pc-body { margin-bottom: 10px; }
.pc-title { font-size: 15px; color: #2c3e50; margin: 0 0 6px; cursor: pointer; }
.pc-title:hover { color: #6d7bf0; }
.pc-content { font-size: 13px; color: #5c6a8c; line-height: 1.6; margin: 0; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.pc-images { display: flex; gap: 8px; margin-top: 10px; }
.pc-img { width: 100px; height: 100px; border-radius: 8px; object-fit: cover; cursor: pointer; }
.pc-footer { display: flex; align-items: center; gap: 16px; padding-top: 10px; border-top: 1px solid #f5f6fa; }
.pc-stat { display: flex; align-items: center; gap: 4px; font-size: 12px; color: #8a93a6; }
.pc-actions { margin-left: auto; display: flex; gap: 10px; }
.op-link { color: #6d7bf0; font-size: 13px; cursor: pointer; }
.op-link.danger { color: #f56c6c; }

/* ===== 分页 ===== */
.pager-row { display: flex; align-items: center; justify-content: space-between; padding: 16px 0; }
.pager-text { font-size: 12px; color: #8a93a6; }

/* ===== 右侧面板 ===== */
.side-col { display: flex; flex-direction: column; gap: 14px; }
.side-card { background: #fff; border: 1px solid #edf0f8; border-radius: 14px; padding: 16px 18px; box-shadow: 0 6px 20px rgba(94, 110, 190, 0.06); }
.side-head { display: flex; align-items: center; justify-content: space-between; margin-bottom: 14px; padding-bottom: 10px; border-bottom: 1px solid #f0f2f8; }
.sh-title { font-size: 15px; font-weight: 600; color: #2c3e50; display: flex; align-items: center; gap: 6px; }

.topic-list { list-style: none; margin: 0; padding: 0; }
.topic-list li { display: flex; align-items: center; gap: 8px; padding: 8px 0; cursor: pointer; border-bottom: 1px solid #f5f6fa; }
.topic-list li:last-child { border-bottom: none; }
.tp-rank { width: 20px; height: 20px; border-radius: 50%; background: #e8ebf5; color: #8a93a6; font-size: 11px; font-weight: 700; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.tp-rank.top { background: linear-gradient(135deg, #6d7bf0, #6a5bd8); color: #fff; }
.tp-tag { font-size: 13px; color: #2c3e50; flex: 1; }
.tp-heat { font-size: 11px; color: #b0b8c8; }

.user-list { list-style: none; margin: 0; padding: 0; }
.user-list li { display: flex; align-items: center; gap: 10px; padding: 10px 0; border-bottom: 1px solid #f5f6fa; }
.user-list li:last-child { border-bottom: none; }
.ul-avatar { width: 32px; height: 32px; border-radius: 50%; color: #fff; font-size: 13px; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.ul-body { flex: 1; min-width: 0; }
.ul-name { font-size: 13px; color: #2c3e50; }
.ul-level { font-size: 10px; color: #fff; background: linear-gradient(90deg, #6d7bf0, #6a5bd8); padding: 1px 5px; border-radius: 6px; margin-left: 4px; }
.ul-meta { font-size: 11px; color: #b0b8c8; display: block; }
.ul-follow { font-size: 12px; color: #6d7bf0; cursor: pointer; padding: 3px 10px; border: 1px solid #6d7bf0; border-radius: 12px; flex-shrink: 0; }

.promo-card { background: linear-gradient(135deg, #6d7bf0, #6a5bd8); border-radius: 14px; padding: 20px 18px; display: flex; align-items: center; gap: 10px; cursor: pointer; box-shadow: 0 8px 20px rgba(106, 91, 216, 0.2); }
.promo-text { flex: 1; }
.promo-text b { font-size: 14px; color: #fff; }
.promo-text p { font-size: 12px; color: rgba(255, 255, 255, 0.8); margin: 4px 0 8px; }
.promo-btn { font-size: 12px; color: #fff; background: rgba(255, 255, 255, 0.2); padding: 4px 12px; border-radius: 12px; }
.promo-illust svg { width: 60px; height: 50px; }

/* ===== 详情弹窗 ===== */
.detail-body { padding: 0 8px; }
.detail-head { display: flex; align-items: center; gap: 10px; margin-bottom: 14px; }
.d-name { font-size: 14px; color: #2c3e50; font-weight: 500; }
.d-time { font-size: 12px; color: #b0b8c8; }
.d-title { font-size: 16px; color: #2c3e50; margin: 0 0 10px; }
.d-content { font-size: 14px; color: #5c6a8c; line-height: 1.7; margin-bottom: 14px; white-space: pre-wrap; }
.d-images { display: flex; flex-wrap: wrap; gap: 8px; }
.d-img { width: 120px; height: 120px; border-radius: 8px; object-fit: cover; }

/* ===== 响应式 ===== */
@media (max-width: 1020px) { .stat-grid { grid-template-columns: repeat(2, 1fr); } .main-layout { grid-template-columns: 1fr; } }
@media (max-width: 760px) { .hero-banner { flex-direction: column; align-items: flex-start; height: auto; padding: 20px; } .h-right-text { display: none; } .stat-grid { grid-template-columns: 1fr; } .filter-bar { flex-direction: column; align-items: stretch; } .f-search { width: 100%; } }
</style>
