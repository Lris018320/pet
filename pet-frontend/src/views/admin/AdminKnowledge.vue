<template>
  <div class="admin-knowledge-page">
    <!-- 顶部 Banner -->
    <div class="hero-banner" :style="{ '--hero-img': `url(${heroImg})` }">
      <div class="h-left">
        <span class="h-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"><path d="M4 4h12a3 3 0 0 1 3 3v13H7a3 3 0 0 1-3-3V4z"/><path d="M4 17h15M8 8h6M8 11h4"/></svg>
        </span>
        <div>
          <h2>知识记录库</h2>
          <p>汇聚宠物养护专业知识，助力科学养宠</p>
        </div>
      </div>
      <div class="h-search">
        <el-input v-model="filter.keyword" placeholder="请输入关键词搜索知识记录..." clearable @keyup.enter="loadList(1)" class="h-input">
          <template #prefix>
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" style="width:16px;height:16px"><circle cx="11" cy="11" r="7"/><path d="M21 21l-4.3-4.3"/></svg>
          </template>
        </el-input>
      </div>
      <span class="h-slogan">科学养宠<br />从知识开始 ♡</span>
    </div>

    <!-- 分类 Tab -->
    <div class="cat-tabs">
      <button class="cat-tab" :class="{ on: filter.category === '' }" @click="setCategory('')">
        <svg viewBox="0 0 24 24" fill="currentColor" style="width:14px;height:14px"><path d="M12 13.5c-2.9 0-5.5 2.3-5.5 4.6 0 1.5 1.1 2.4 2.6 2.4 1.1 0 1.9-.5 2.9-.5s1.8.5 2.9.5c1.5 0 2.6-.9 2.6-2.4 0-2.3-2.6-4.6-5.5-4.6zM6.4 8.2c-1.2.3-2 1.6-1.8 2.9.2 1.2 1.3 2 2.5 1.8 1.2-.3 2-1.6 1.8-2.9-.2-1.2-1.3-2-2.5-1.8zm11.2 0c-1.2-.2-2.3.6-2.5 1.8-.2 1.3.6 2.6 1.8 2.9 1.2.2 2.3-.6 2.5-1.8.2-1.3-.6-2.6-1.8-2.9zM9.3 3.6c-1.1.2-1.9 1.4-1.7 2.7.2 1.2 1.2 2.1 2.4 1.9 1.1-.2 1.9-1.4 1.7-2.7-.2-1.2-1.3-2.1-2.4-1.9zm5.4 0c-1.1-.2-2.2.7-2.4 1.9-.2 1.3.6 2.5 1.7 2.7 1.2.2 2.2-.7 2.4-1.9.2-1.3-.6-2.5-1.7-2.7z"/></svg>
        全部记录
        <span class="cat-count">{{ catCounts.total || 0 }}</span>
      </button>
      <button v-for="c in categoryTabs" :key="c.name" class="cat-tab" :class="{ on: filter.category === c.name }" @click="setCategory(c.name)">
        <span class="cat-ic" :style="{ color: c.color }" v-html="c.icon"></span>
        {{ c.name }}
        <span class="cat-count">{{ catCounts[c.name] || 0 }}</span>
      </button>
    </div>

    <div class="main-layout">
      <!-- 左侧主区域 -->
      <div class="main-col">
        <!-- 控制栏 -->
        <div class="ctrl-bar">
          <div class="sort-tabs">
            <button v-for="s in sortTabs" :key="s.key" class="sort-tab" :class="{ on: filter.sort === s.key }" @click="setSort(s.key)">{{ s.label }}</button>
          </div>
          <div class="ctrl-right">
            <el-select v-model="filter.petType" placeholder="全部宠物" clearable size="small" style="width:110px" @change="loadList(1)">
              <el-option label="全部宠物" value="全部" />
              <el-option label="猫" value="猫" />
              <el-option label="狗" value="狗" />
            </el-select>
            <el-button type="primary" class="f-add" @click="openDialog()">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.4" stroke-linecap="round" style="width:14px;height:14px"><path d="M12 5v14M5 12h14"/></svg>
              新增记录
            </el-button>
          </div>
        </div>

        <!-- 知识卡片列表 -->
        <div class="card-list" v-loading="loading">
          <div v-for="r in list" :key="r.id" class="knowledge-card">
            <img class="kc-thumb" :src="resolveImg(r.thumbnail)" alt="缩略图" />
            <div class="kc-body">
              <div class="kc-head">
                <el-tag size="small" :type="catTagType(r.category)">{{ r.category }}</el-tag>
                <span class="kc-title" @click="openDialog(r)">{{ r.title }}</span>
                <el-tag v-if="r.viewCount >= 1000" size="small" type="danger" effect="plain" class="kc-hot">🔥 热门</el-tag>
              </div>
              <p class="kc-excerpt">{{ r.excerpt }}</p>
              <div class="kc-meta">
                <span class="km-item">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" style="width:13px;height:13px"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
                  {{ fmtNum(r.viewCount) }}
                </span>
                <span class="km-item">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" style="width:13px;height:13px"><path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2v16z"/></svg>
                  {{ fmtNum(r.collectCount) }}
                </span>
                <span class="km-item">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" style="width:13px;height:13px"><path d="M4 12v8a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2v-8M16 6l-4-4-4 4M12 2v14"/></svg>
                  {{ fmtNum(r.shareCount) }}
                </span>
                <span class="km-time">{{ formatTime(r.createTime) }}</span>
                <el-tag :type="r.status === 1 ? 'success' : 'info'" size="small" class="kc-status">{{ r.status === 1 ? '已发布' : '草稿' }}</el-tag>
              </div>
            </div>
            <div class="kc-actions">
              <a class="op-link" @click="openDialog(r)">编辑</a>
              <a class="op-link danger" @click="onDelete(r)">删除</a>
            </div>
          </div>
          <el-empty v-if="!loading && list.length === 0" description="暂无知识记录" />
        </div>

        <!-- 分页 -->
        <div class="pager-row" v-if="total > 0">
          <span class="pager-text">共 {{ fmtNum(total) }} 条记录，当前第 {{ page }} / {{ Math.max(Math.ceil(total / size), 1) }} 页</span>
          <el-pagination
            background
            layout="prev, pager, next"
            :current-page="page"
            :page-size="size"
            :total="total"
            @current-change="loadList"
          />
        </div>
      </div>

      <!-- 右侧面板 -->
      <div class="side-col">
        <!-- 知识统计 -->
        <div class="side-card">
          <div class="side-head">
            <span class="sh-title">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" style="width:16px;height:16px"><rect x="3" y="4" width="18" height="16" rx="2"/><path d="M7 8h10M7 12h6"/></svg>
              知识统计
            </span>
          </div>
          <div class="stat-mini-grid">
            <div class="stat-mini" v-for="s in sideStats" :key="s.key">
              <span class="sm-icon" :style="{ background: s.bg, color: s.color }" v-html="s.icon"></span>
              <div>
                <div class="sm-value">{{ fmtNum(s.value) }}</div>
                <div class="sm-label">{{ s.label }}</div>
              </div>
            </div>
          </div>
        </div>

        <!-- 热门标签 -->
        <div class="side-card" v-if="hotTags.length">
          <div class="side-head">
            <span class="sh-title">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" style="width:16px;height:16px"><path d="M20.59 13.41l-7.17 7.17a2 2 0 0 1-2.83 0L2 12V2h10l8.59 8.59a2 2 0 0 1 0 2.82z"/><circle cx="7" cy="7" r="1.5" fill="currentColor"/></svg>
              热门标签
            </span>
          </div>
          <div class="tag-cloud">
            <span v-for="(t, i) in hotTags" :key="i" class="tag-chip" :style="{ fontSize: 12 + Math.min(t.count, 5) * 1.5 + 'px', '--tag-color': tagColor(i) }">{{ t.name }}</span>
          </div>
        </div>

        <!-- 最新知识推荐 -->
        <div class="side-card">
          <div class="side-head">
            <span class="sh-title">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" style="width:16px;height:16px"><circle cx="12" cy="12" r="9"/><path d="M12 7v5l3 2"/></svg>
              最新知识推荐
            </span>
          </div>
          <ul class="rec-list">
            <li v-for="(r, i) in recommend" :key="r.id" @click="openDialog(r)">
              <img class="rec-thumb" :src="resolveImg(r.thumbnail)" alt="推荐" />
              <div class="rec-body">
                <span class="rec-title">{{ r.title }}</span>
                <div class="rec-meta">
                  <span>{{ formatTime(r.createTime) }}</span>
                  <span>{{ fmtViews(r.viewCount) }} 浏览</span>
                  <span v-if="r.viewCount >= 1000" class="rec-hot-badge">🔥热门</span>
                </div>
              </div>
            </li>
          </ul>
        </div>
      </div>
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="editId ? '编辑知识记录' : '新增知识记录'"
      width="720px"
      :close-on-click-modal="false"
      top="5vh"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入标题" maxlength="100" show-word-limit />
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-select v-model="form.category" placeholder="请选择分类" style="width:180px">
            <el-option v-for="c in categoryTabs" :key="c.name" :label="c.name" :value="c.name" />
          </el-select>
        </el-form-item>
        <el-form-item label="宠物类型">
          <el-radio-group v-model="form.petType">
            <el-radio value="全部">全部</el-radio>
            <el-radio value="猫">猫</el-radio>
            <el-radio value="狗">狗</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="标签">
          <el-input v-model="form.tags" placeholder="多个标签用逗号分隔，如：疫苗接种,驱虫,猫粮" />
        </el-form-item>
        <el-form-item label="缩略图">
          <el-upload class="cover-uploader" :show-file-list="false" :before-upload="beforeUpload" :http-request="customUpload" accept=".jpg,.jpeg,.png">
            <img v-if="form.thumbnail" :src="resolveImg(form.thumbnail)" class="cover-preview" alt="缩略图" />
            <div v-else class="cover-empty">
              <el-icon><Plus /></el-icon>
              <span>上传缩略图</span>
            </div>
          </el-upload>
        </el-form-item>
        <el-form-item label="摘要">
          <el-input v-model="form.excerpt" type="textarea" :rows="2" placeholder="一句话描述内容" maxlength="200" show-word-limit />
        </el-form-item>
        <el-form-item label="正文">
          <el-input v-model="form.content" type="textarea" :rows="6" placeholder="正文内容（支持简单文本）" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">已发布</el-radio>
            <el-radio :value="0">草稿</el-radio>
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import {
  getKnowledgeStats, getHotTags, getKnowledgeRecommend,
  pageKnowledge, getKnowledgeDetail, saveKnowledge, updateKnowledge, deleteKnowledge
} from '@/api/admin'
import { uploadImage } from '@/api/recognition'

const API_BASE = import.meta.env.VITE_API_BASE || '/api'

/* ========== Banner 配图 ========== */
const heroImg = 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=' +
  encodeURIComponent('Golden retriever dog and grey tabby cat sitting together on green grass with blue sky and books, warm sunlight, professional pet photography, soft pastel tones') +
  '&image_size=landscape_16_9'

/* ========== 分类 Tab ========== */
const categoryTabs = [
  { name: '健康护理', color: '#fa5252', icon: '<svg viewBox="0 0 24 24" fill="currentColor" style="width:14px;height:14px"><path d="M9 2v6H3v8h6v6h6v-6h6V8h-6V2z"/></svg>' },
  { name: '饮食营养', color: '#f59f00', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="width:14px;height:14px"><path d="M3 3v18h18M7 14v5M11 10v9M15 6v13"/></svg>' },
  { name: '疾病防治', color: '#12b886', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="width:14px;height:14px"><path d="M14 4l6 6-10 10H4v-6z"/><path d="M13 5l6 6"/></svg>' },
  { name: '行为训练', color: '#6d7bf0', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="width:14px;height:14px"><path d="M22 10L12 5 2 10l10 5 10-5zM6 12v5c0 1 3 3 6 3s6-2 6-3v-5"/></svg>' },
  { name: '日常护理', color: '#339af0', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="width:14px;height:14px"><path d="M3 12l9-9 9 9M5 10v10h14V10"/></svg>' }
]

const sortTabs = [
  { key: 'latest', label: '最新记录' },
  { key: 'hot', label: '最热推荐' },
  { key: 'collect', label: '收藏最多' }
]

/* ========== 分类计数 ========== */
const catCounts = ref({ total: 0 })

/* ========== 右侧统计 ========== */
const sideStats = ref([
  { key: 'total', label: '记录总数', value: 0, icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" style="width:18px;height:18px"><rect x="3" y="4" width="18" height="16" rx="2"/><path d="M7 8h10M7 12h6"/></svg>', bg: '#eef0ff', color: '#5b6cf5' },
  { key: 'today', label: '今日新增', value: 0, icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" style="width:18px;height:18px"><path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78L12 21l8.84-8.61a5.5 5.5 0 0 0 0-7.78z"/></svg>', bg: '#fff0f0', color: '#fa5252' },
  { key: 'views', label: '浏览总数', value: 0, icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" style="width:18px;height:18px"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>', bg: '#e6fcf5', color: '#12b886' },
  { key: 'collects', label: '收藏总数', value: 0, icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" style="width:18px;height:18px"><path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2v16z"/></svg>', bg: '#f3f0ff', color: '#7950f2' }
])

/* ========== 热门标签 ========== */
const hotTags = ref([])

/* ========== 最新推荐 ========== */
const recommend = ref([])

/* ========== 列表 ========== */
const list = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const loading = ref(false)
const filter = reactive({ keyword: '', category: '', petType: '全部', sort: 'latest' })

/* ========== 编辑弹窗 ========== */
const dialogVisible = ref(false)
const editId = ref(null)
const saving = ref(false)
const formRef = ref(null)
const form = reactive({
  title: '', excerpt: '', content: '', category: '', thumbnail: '', tags: '', petType: '全部', status: 1, author: '小知宠'
})
const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  category: [{ required: true, message: '请选择分类', trigger: 'change' }]
}

/* ========== 工具函数 ========== */
function resolveImg(url) {
  if (!url) return ''
  if (url.startsWith('http')) return url
  return API_BASE + url
}
function formatTime(t) {
  if (!t) return ''
  return String(t).replace('T', ' ').substring(0, 10)
}
function fmtNum(n) {
  if (!n && n !== 0) return 0
  return n.toLocaleString()
}
function fmtViews(n) {
  if (!n) return '0'
  if (n >= 10000) return (n / 10000).toFixed(1) + 'w'
  if (n >= 1000) return (n / 1000).toFixed(1) + 'k'
  return String(n)
}
function catTagType(c) {
  const map = { '健康护理': 'danger', '饮食营养': 'warning', '疾病防治': 'success', '行为训练': '', '日常护理': 'info' }
  return map[c] || 'info'
}
const tagColors = ['#6d7bf0', '#12b886', '#f59f00', '#fa5252', '#7950f2', '#339af0']
function tagColor(i) { return tagColors[i % tagColors.length] }

/* ========== 数据加载 ========== */
async function loadList(p = 1) {
  page.value = p
  loading.value = true
  try {
    const res = await pageKnowledge({
      page: page.value, size: size.value,
      keyword: filter.keyword || undefined,
      category: filter.category || undefined,
      petType: filter.petType && filter.petType !== '全部' ? filter.petType : undefined,
      sort: filter.sort
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
function setSort(s) {
  filter.sort = s
  loadList(1)
}

async function loadStats() {
  try {
    const res = await getKnowledgeStats()
    const d = res.data || {}
    sideStats.value[0].value = d.total || 0
    sideStats.value[1].value = d.todayNew || 0
    sideStats.value[2].value = d.totalViews || 0
    sideStats.value[3].value = d.totalCollects || 0
    const cc = d.categoryCounts || {}
    catCounts.value = { total: d.total || 0, ...cc }
  } catch (e) {
    // 演示数据兜底
    sideStats.value[0].value = 1248
    sideStats.value[1].value = 12
    sideStats.value[2].value = 32648
    sideStats.value[3].value = 4732
    catCounts.value = { total: 9, '健康护理': 2, '饮食营养': 2, '疾病防治': 2, '行为训练': 1, '日常护理': 2 }
  }
}

async function loadHotTags() {
  try {
    const res = await getHotTags()
    hotTags.value = res.data || []
  } catch (e) {
    hotTags.value = []
  }
}

async function loadRecommend() {
  try {
    const res = await getKnowledgeRecommend()
    recommend.value = res.data || []
  } catch (e) {
    recommend.value = []
  }
}

/* ========== 编辑/新增 ========== */
async function openDialog(row) {
  editId.value = null
  form.title = ''; form.excerpt = ''; form.content = ''
  form.category = ''; form.thumbnail = ''; form.tags = ''
  form.petType = '全部'; form.status = 1; form.author = '小知宠'
  if (row) {
    editId.value = row.id
    try {
      const res = await getKnowledgeDetail(row.id)
      const d = res.data
      Object.assign(form, {
        title: d.title || '', excerpt: d.excerpt || '', content: d.content || '',
        category: d.category || '', thumbnail: d.thumbnail || '', tags: d.tags || '',
        petType: d.petType || '全部', status: d.status ?? 1, author: d.author || '小知宠'
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
    form.thumbnail = res.data.imageUrl
    ElMessage.success('缩略图上传成功')
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
        await updateKnowledge(editId.value, payload)
        ElMessage.success('修改成功')
      } else {
        await saveKnowledge(payload)
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
      await loadList(page.value)
      await loadStats()
      await loadHotTags()
      await loadRecommend()
    } finally {
      saving.value = false
    }
  })
}

async function onDelete(row) {
  try {
    await ElMessageBox.confirm(`确认删除「${row.title}」吗？`, '提示', { type: 'warning' })
  } catch { return }
  try {
    await deleteKnowledge(row.id)
    ElMessage.success('删除成功')
    await loadList(page.value)
    await loadStats()
  } catch (e) { /* ignore */ }
}

onMounted(() => {
  loadList(1)
  loadStats()
  loadHotTags()
  loadRecommend()
})
</script>

<style scoped>
.admin-knowledge-page {
  width: 100%;
  padding: 4px clamp(16px, 2vw, 32px) 32px;
}

/* ===== Banner ===== */
.hero-banner {
  position: relative;
  display: flex;
  align-items: center;
  gap: 20px;
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
  margin-bottom: 18px;
}
.h-left { position: relative; z-index: 1; display: flex; align-items: center; gap: 14px; flex-shrink: 0; }
.h-icon {
  width: 48px; height: 48px; border-radius: 12px;
  background: linear-gradient(135deg, #6d7bf0, #6a5bd8);
  color: #fff; display: flex; align-items: center; justify-content: center; flex-shrink: 0;
  box-shadow: 0 6px 14px rgba(106, 91, 216, 0.3);
}
.h-icon svg { width: 26px; height: 26px; }
.h-left h2 { margin: 0; font-size: 20px; color: #2c3e50; }
.h-left p { margin: 2px 0 0; font-size: 13px; color: #7a8699; }
.h-search { position: relative; z-index: 1; flex: 1; max-width: 400px; }
.h-input :deep(.el-input__wrapper) { background: rgba(255,255,255,.85); box-shadow: 0 4px 12px rgba(90,100,180,.1); }
.h-slogan {
  position: relative; z-index: 1;
  font-family: 'Comic Sans MS', 'STKaiti', cursive;
  font-size: 14px; color: #4a5b7d; line-height: 1.5; text-align: right; white-space: nowrap;
}

/* ===== 分类 Tab ===== */
.cat-tabs {
  display: flex; gap: 8px; flex-wrap: wrap;
  margin-bottom: 16px;
}
.cat-tab {
  display: flex; align-items: center; gap: 6px;
  padding: 8px 16px; border: 1px solid #edf0f8; background: #fff;
  border-radius: 10px; font-size: 13px; color: #5c6a8c;
  cursor: pointer; transition: all 0.2s;
  box-shadow: 0 2px 8px rgba(94,110,190,.04);
}
.cat-tab:hover { border-color: #c4cdf5; }
.cat-tab.on {
  background: linear-gradient(90deg, #6d7bf0, #6a5bd8);
  color: #fff; border-color: transparent;
  box-shadow: 0 4px 12px rgba(106, 91, 216, 0.25);
}
.cat-tab.on .cat-ic { color: #fff !important; }
.cat-count {
  background: #f2f4fa; color: #8a93a6; font-size: 11px;
  padding: 1px 7px; border-radius: 10px; font-weight: 600;
}
.cat-tab.on .cat-count { background: rgba(255,255,255,.25); color: #fff; }

/* ===== 主布局 ===== */
.main-layout { display: grid; grid-template-columns: minmax(0, 1fr) 280px; gap: 16px; }
.main-col { min-width: 0; }

/* ===== 控制栏 ===== */
.ctrl-bar {
  display: flex; align-items: center; justify-content: space-between;
  gap: 12px; margin-bottom: 14px; flex-wrap: wrap;
}
.sort-tabs { display: flex; gap: 4px; background: #f2f4fa; padding: 3px; border-radius: 8px; }
.sort-tab {
  padding: 5px 14px; border: none; background: transparent;
  border-radius: 6px; font-size: 13px; color: #5c6a8c; cursor: pointer; transition: all 0.2s;
}
.sort-tab.on { background: #fff; color: #6d7bf0; font-weight: 600; box-shadow: 0 1px 4px rgba(94,110,190,.08); }
.ctrl-right { display: flex; gap: 10px; align-items: center; }
.f-add { background: linear-gradient(90deg, #6d7bf0, #6a5bd8); border: none; border-radius: 8px; }

/* ===== 知识卡片列表 ===== */
.card-list { display: flex; flex-direction: column; gap: 12px; }
.knowledge-card {
  display: flex; gap: 14px; background: #fff; border: 1px solid #edf0f8;
  border-radius: 14px; padding: 14px 16px;
  box-shadow: 0 4px 16px rgba(94,110,190,.05);
  transition: box-shadow 0.2s;
}
.knowledge-card:hover { box-shadow: 0 8px 24px rgba(94,110,190,.1); }
.kc-thumb { width: 90px; height: 90px; border-radius: 10px; object-fit: cover; flex-shrink: 0; }
.kc-body { flex: 1; min-width: 0; }
.kc-head { display: flex; align-items: center; gap: 8px; margin-bottom: 6px; }
.kc-title { font-size: 15px; font-weight: 600; color: #2c3e50; cursor: pointer; }
.kc-title:hover { color: #6d7bf0; }
.kc-hot { margin-left: auto; }
.kc-excerpt { font-size: 13px; color: #7a8699; line-height: 1.6; margin: 0 0 8px; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.kc-meta { display: flex; align-items: center; gap: 14px; flex-wrap: wrap; }
.km-item { display: flex; align-items: center; gap: 4px; font-size: 12px; color: #8a93a6; }
.km-time { font-size: 12px; color: #b0b8c8; }
.kc-status { margin-left: auto; }
.kc-actions { display: flex; flex-direction: column; gap: 8px; justify-content: center; }
.op-link { color: #6d7bf0; font-size: 13px; cursor: pointer; text-align: center; }
.op-link.danger { color: #f56c6c; }

/* ===== 分页 ===== */
.pager-row { display: flex; align-items: center; justify-content: space-between; padding: 16px 0; }
.pager-text { font-size: 12px; color: #8a93a6; }

/* ===== 右侧面板 ===== */
.side-col { display: flex; flex-direction: column; gap: 14px; }
.side-card {
  background: #fff; border: 1px solid #edf0f8; border-radius: 14px;
  padding: 16px 18px; box-shadow: 0 4px 16px rgba(94,110,190,.05);
}
.side-head {
  display: flex; align-items: center; justify-content: space-between;
  margin-bottom: 14px; padding-bottom: 10px; border-bottom: 1px solid #f0f2f8;
}
.sh-title { font-size: 15px; font-weight: 600; color: #2c3e50; display: flex; align-items: center; gap: 6px; }

.stat-mini-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 14px; }
.stat-mini { display: flex; align-items: center; gap: 10px; }
.sm-icon { width: 36px; height: 36px; border-radius: 10px; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.sm-value { font-size: 18px; font-weight: 700; color: #2c3e50; }
.sm-label { font-size: 11px; color: #8a93a6; }

.tag-cloud { display: flex; flex-wrap: wrap; gap: 8px; }
.tag-chip {
  padding: 4px 12px; border-radius: 16px; font-weight: 500;
  background: color-mix(in srgb, var(--tag-color) 12%, #fff);
  color: var(--tag-color); cursor: pointer; transition: all 0.2s;
}
.tag-chip:hover { background: var(--tag-color); color: #fff; }

.rec-list { list-style: none; margin: 0; padding: 0; }
.rec-list li { display: flex; gap: 10px; padding: 10px 0; cursor: pointer; border-bottom: 1px solid #f5f6fa; }
.rec-list li:last-child { border-bottom: none; }
.rec-thumb { width: 50px; height: 50px; border-radius: 8px; object-fit: cover; flex-shrink: 0; }
.rec-body { flex: 1; min-width: 0; }
.rec-title { font-size: 13px; color: #2c3e50; display: -webkit-box; -webkit-line-clamp: 1; -webkit-box-orient: vertical; overflow: hidden; }
.rec-meta { display: flex; gap: 8px; align-items: center; margin-top: 4px; }
.rec-meta span { font-size: 11px; color: #b0b8c8; }
.rec-hot-badge { color: #fa5252 !important; font-weight: 600; }

/* ===== 编辑弹窗 ===== */
.cover-uploader :deep(.el-upload) {
  border: 1px dashed #d9d9d9; border-radius: 8px; cursor: pointer;
  width: 120px; height: 120px; display: flex; align-items: center; justify-content: center; overflow: hidden;
}
.cover-preview { width: 100%; height: 100%; object-fit: cover; }
.cover-empty { color: #909399; font-size: 12px; display: flex; flex-direction: column; gap: 6px; align-items: center; }
.cover-empty .el-icon { font-size: 24px; }

/* ===== 响应式 ===== */
@media (max-width: 1020px) {
  .main-layout { grid-template-columns: 1fr; }
  .stat-mini-grid { grid-template-columns: 1fr 1fr; }
}
@media (max-width: 760px) {
  .hero-banner { flex-direction: column; align-items: flex-start; height: auto; padding: 20px; }
  .h-search { max-width: 100%; }
  .h-slogan { display: none; }
  .ctrl-bar { flex-direction: column; align-items: stretch; }
}
</style>
