<template>
  <div class="admin-info-page">
    <!-- 顶部 Banner -->
    <div class="hero-banner" :style="{ '--hero-img': `url(${heroImg})` }">
      <div class="h-left">
        <span class="h-icon">
          <svg viewBox="0 0 24 24" fill="currentColor"><path d="M19 3H5a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V5a2 2 0 0 0-2-2zM8.5 6.5a1.5 1.5 0 1 1 0 3 1.5 1.5 0 0 1 0-3zm7 0a1.5 1.5 0 1 1 0 3 1.5 1.5 0 0 1 0-3zM12 11c2.8 0 5 2.2 5 5v1H7v-1c0-2.8 2.2-5 5-5z"/></svg>
        </span>
        <div>
          <h2>资讯管理</h2>
          <p>发布宠物相关资讯，帮助用户获取科学养宠知识</p>
        </div>
      </div>
      <span class="h-slogan">让每一位宠物主人<br />都能获得有用的信息 ♡</span>
    </div>

    <!-- 统计卡 -->
    <div class="stat-grid">
      <div class="stat-card" v-for="c in statCards" :key="c.key">
        <div class="s-icon" :style="{ background: c.bg, color: c.color }" v-html="c.icon"></div>
        <div class="s-body">
          <div class="s-label">{{ c.label }}</div>
          <div class="s-value">
            {{ fmtNum(c.value) }}
            <span class="s-up" v-if="c.growth !== null" :style="{ color: c.growth >= 0 ? '#12b886' : '#f56c6c' }">{{ c.growth >= 0 ? '↑' : '↓' }} {{ Math.abs(c.growth).toFixed(1) }}%</span>
          </div>
          <div class="s-cmp">{{ c.cmp }}</div>
        </div>
      </div>
    </div>

    <div class="main-layout">
      <!-- 左侧主区域 -->
      <div class="main-col">
        <!-- 筛选栏 -->
        <div class="filter-bar">
          <div class="cat-tabs">
            <button class="cat-tab" :class="{ on: filter.category === '' }" @click="setCategory('')">全部</button>
            <button v-for="c in categories" :key="c" class="cat-tab" :class="{ on: filter.category === c }" @click="setCategory(c)">{{ c }}</button>
          </div>
          <div class="filter-right">
            <el-input
              v-model="filter.keyword"
              placeholder="请输入文章标题/关键词"
              clearable
              class="f-search"
              @keyup.enter="loadList(1)"
            >
              <template #prefix>
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" style="width:15px;height:15px"><circle cx="11" cy="11" r="7"/><path d="M21 21l-4.3-4.3"/></svg>
              </template>
            </el-input>
            <el-button type="primary" class="f-add" @click="openDialog()">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.4" stroke-linecap="round" style="width:14px;height:14px"><path d="M12 5v14M5 12h14"/></svg>
              发布资讯
            </el-button>
          </div>
        </div>

        <!-- 表格 -->
        <div class="table-card">
          <el-table :data="list" v-loading="loading" @selection-change="sel => selected = sel">
            <el-table-column type="selection" width="42" />
            <el-table-column label="封面图" width="80">
              <template #default="{ row }">
                <img v-if="row.coverImage" :src="resolveImg(row.coverImage)" class="row-cover" alt="封面" />
                <span v-else class="no-cover">—</span>
              </template>
            </el-table-column>
            <el-table-column label="标题" min-width="240" show-overflow-tooltip>
              <template #default="{ row }">
                <span class="row-title">{{ row.title }}</span>
              </template>
            </el-table-column>
            <el-table-column label="分类" width="100">
              <template #default="{ row }">
                <el-tag size="small" :type="catTagType(row.category)">{{ row.category || '—' }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="作者" prop="author" width="90">
              <template #default="{ row }">{{ row.author || '小知宠' }}</template>
            </el-table-column>
            <el-table-column label="浏览量" width="90">
              <template #default="{ row }">
                <span class="view-count">{{ fmtNum(row.viewCount || 0) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="发布时间" width="120">
              <template #default="{ row }">{{ formatTime(row.updateTime || row.createTime) }}</template>
            </el-table-column>
            <el-table-column label="状态" width="80">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">{{ row.status === 1 ? '已发布' : '已下架' }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="160" fixed="right">
              <template #default="{ row }">
                <a class="op-link" @click="openDialog(row)">编辑</a>
                <a v-if="row.status === 1" class="op-link warn" @click="onToggleStatus(row, 0)">下架</a>
                <a v-else class="op-link ok" @click="onToggleStatus(row, 1)">发布</a>
                <a class="op-link danger" @click="onDelete(row)">删除</a>
              </template>
            </el-table-column>
            <template #empty>
              <el-empty description="暂无资讯" />
            </template>
          </el-table>
          <div class="pager-row">
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
      </div>

      <!-- 右侧面板 -->
      <div class="side-col">
        <!-- 热门资讯 -->
        <div class="side-card">
          <div class="side-head">
            <span class="sh-title">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" style="width:16px;height:16px"><path d="M12 2v0m0 0l3 6 6 .9-4.5 4.4 1 6.2L12 17.3 6.5 19.5l1-6.2L3 8.9l6-.9z"/></svg>
              热门资讯
            </span>
            <a class="sh-more" @click="router.push('/info')">查看更多 &gt;</a>
          </div>
          <ul class="hot-list">
            <li v-for="(h, i) in hotList" :key="h.id" @click="router.push(`/info/${h.id}`)">
              <span class="hot-rank" :class="{ top: i < 3 }">{{ i + 1 }}</span>
              <span class="hot-title">{{ h.title }}</span>
              <span class="hot-views">{{ fmtViews(h.viewCount) }}</span>
            </li>
          </ul>
        </div>

        <!-- 最新动态 -->
        <div class="side-card">
          <div class="side-head">
            <span class="sh-title">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" style="width:16px;height:16px"><circle cx="12" cy="12" r="9"/><path d="M12 7v5l3 2"/></svg>
              最新动态
            </span>
          </div>
          <ul class="feed-list">
            <li v-for="f in feedList" :key="f.id">
              <span class="feed-dot"></span>
              <div class="feed-body">
                <span class="feed-user">{{ f.author }}</span>
                <span class="feed-act">发布了新文章《{{ f.title }}》</span>
                <span class="feed-time">{{ formatTime(f.updateTime || f.createTime) }}</span>
              </div>
            </li>
          </ul>
        </div>

        <!-- 推广卡片 -->
        <div class="promo-card" @click="router.push('/info')">
          <div class="promo-text">
            <b>发现更多养宠知识</b>
            <p>海量宠物资讯，科学养宠一站式</p>
            <span class="promo-btn">浏览资讯 →</span>
          </div>
          <div class="promo-illust">
            <svg viewBox="0 0 120 100" fill="none">
              <ellipse cx="60" cy="80" rx="50" ry="12" fill="#e8d5f5" opacity=".4"/>
              <path d="M35 70c0-8 7-14 15-14s15 6 15 14" fill="#fff3e0"/>
              <circle cx="42" cy="50" r="6" fill="#fff3e0"/>
              <circle cx="50" cy="44" r="5" fill="#fff3e0"/>
              <circle cx="58" cy="44" r="5" fill="#fff3e0"/>
              <circle cx="66" cy="50" r="6" fill="#fff3e0"/>
              <circle cx="46" cy="46" r="1.5" fill="#333"/>
              <circle cx="62" cy="46" r="1.5" fill="#333"/>
              <path d="M50 52c2 2 4 2 6 0" stroke="#333" stroke-width="1.5" stroke-linecap="round" fill="none"/>
              <path d="M85 78c0-6 5-10 10-10s10 4 10 10" fill="#e8d5f5" opacity=".6"/>
              <circle cx="90" cy="68" r="4" fill="#e8d5f5" opacity=".6"/>
              <circle cx="96" cy="64" r="3.5" fill="#e8d5f5" opacity=".6"/>
              <circle cx="102" cy="68" r="4" fill="#e8d5f5" opacity=".6"/>
            </svg>
          </div>
        </div>
      </div>
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="editId ? '编辑资讯' : '新增资讯'"
      width="780px"
      :close-on-click-modal="false"
      top="5vh"
      @closed="destroyEditor"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入标题" maxlength="100" show-word-limit />
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-select v-model="form.category" placeholder="请选择分类" style="width:200px">
            <el-option v-for="c in categories" :key="c" :label="c" :value="c" />
          </el-select>
        </el-form-item>
        <el-form-item label="作者">
          <el-input v-model="form.author" placeholder="作者" style="width:200px" />
        </el-form-item>
        <el-form-item label="封面图">
          <el-upload
            class="cover-uploader"
            :show-file-list="false"
            :before-upload="beforeUpload"
            :http-request="customUpload"
            accept=".jpg,.jpeg,.png"
          >
            <img v-if="form.coverImage" :src="resolveImg(form.coverImage)" class="cover-preview" alt="封面" />
            <div v-else class="cover-empty">
              <el-icon><Plus /></el-icon>
              <span>上传封面</span>
            </div>
          </el-upload>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">已发布</el-radio>
            <el-radio :value="0">下架</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="正文" prop="content">
          <div v-if="dialogVisible" class="editor-wrap">
            <Toolbar class="editor-toolbar" :editor="editorRef" :defaultConfig="toolbarConfig" mode="default" />
            <Editor
              class="editor-body"
              v-model="form.content"
              :defaultConfig="editorConfig"
              mode="default"
              @onCreated="handleCreated"
            />
          </div>
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
import { ref, reactive, shallowRef, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import '@wangeditor/editor/dist/css/style.css'
import {
  adminPageInfo, adminGetInfo, adminSaveInfo,
  adminUpdateInfo, adminDeleteInfo, adminUpdateInfoStatus,
  getInfoStats, getInfoHot, getInfoFeed
} from '@/api/info'
import { uploadImage } from '@/api/recognition'

const router = useRouter()
const API_BASE = import.meta.env.VITE_API_BASE || '/api'

/* ========== Banner 配图 ========== */
const heroImg = 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=' +
  encodeURIComponent('Golden retriever dog and grey tabby cat sitting together on green grass with blue sky, warm sunlight, professional pet photography, soft pastel tones') +
  '&image_size=landscape_16_9'

/* ========== 统计卡 ========== */
const statCards = ref([
  { key: 'total', label: '总资讯数', value: 0, growth: null, cmp: '较上月', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"><rect x="3" y="4" width="18" height="16" rx="2"/><path d="M7 8h10M7 12h6M7 16h4"/></svg>', bg: 'linear-gradient(135deg,#eef0ff,#dde2ff)', color: '#5b6cf5' },
  { key: 'today', label: '今日发布', value: 0, growth: null, cmp: '较昨日', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"><circle cx="12" cy="12" r="9"/><path d="M12 7v5l3 2"/></svg>', bg: 'linear-gradient(135deg,#e6fcf5,#d3f9e8)', color: '#12b886' },
  { key: 'views', label: '总浏览量', value: 0, growth: null, cmp: '较上月', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>', bg: 'linear-gradient(135deg,#fff9e6,#fff3d1)', color: '#f59f00' },
  { key: 'hot', label: '热门文章', value: 0, growth: null, cmp: '较上月', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"><path d="M12 2l3 6 6 .9-4.5 4.4 1 6.2L12 17.3 6.5 19.5l1-6.2L3 8.9l6-.9z"/></svg>', bg: 'linear-gradient(135deg,#fff0f0,#ffe0e6)', color: '#fa5252' }
])

/* ========== 筛选与列表 ========== */
const list = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const loading = ref(false)
const filter = reactive({ keyword: '', category: '' })
const selected = ref([])
const categories = ['喂养', '洗护', '疾病', '习性']

/* ========== 右侧面板 ========== */
const hotList = ref([])
const feedList = ref([])

/* ========== 编辑弹窗 ========== */
const dialogVisible = ref(false)
const editId = ref(null)
const saving = ref(false)
const formRef = ref(null)

const editorRef = shallowRef(null)
const toolbarConfig = {}
const editorConfig = {
  placeholder: '请输入正文…',
  MENU_CONF: {
    uploadImage: {
      async customUpload(file, insertFn) {
        try {
          const res = await uploadImage(file)
          insertFn(resolveImg(res.data.imageUrl), '', resolveImg(res.data.imageUrl))
        } catch (e) {
          ElMessage.error('图片上传失败')
        }
      }
    }
  }
}
function handleCreated(editor) { editorRef.value = editor }
function destroyEditor() {
  if (editorRef.value) { editorRef.value.destroy(); editorRef.value = null }
}
onBeforeUnmount(() => { destroyEditor() })

const form = reactive({
  title: '', content: '', coverImage: '', category: '', status: 1, author: '小知宠'
})
const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入正文', trigger: 'blur' }]
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
  const map = { '喂养': 'warning', '洗护': 'success', '疾病': 'danger', '习性': '' }
  return map[c] || 'info'
}

/* ========== 数据加载 ========== */
async function loadList(p = 1) {
  page.value = p
  loading.value = true
  try {
    const res = await adminPageInfo({
      page: page.value, size: size.value,
      keyword: filter.keyword || undefined,
      category: filter.category || undefined
    })
    list.value = res.data.list || []
    total.value = res.data.total || 0
  } catch (e) {
    // 接口失败保留空列表
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
    const res = await getInfoStats()
    const d = res.data || {}
    statCards.value[0].value = d.total || 0
    statCards.value[0].growth = d.totalGrowth ?? 0
    statCards.value[1].value = d.todayPublished || 0
    statCards.value[1].growth = d.todayGrowth ?? 0
    statCards.value[2].value = d.totalViews || 0
    statCards.value[2].growth = d.viewsGrowth ?? 0
    statCards.value[3].value = d.hotCount || 0
    statCards.value[3].growth = d.hotGrowth ?? 0
  } catch (e) {
    // 演示数据兜底
    statCards.value[0].value = 186
    statCards.value[1].value = 5
    statCards.value[2].value = 12846
    statCards.value[3].value = 28
  }
}

async function loadHot() {
  try {
    const res = await getInfoHot()
    hotList.value = res.data || []
  } catch (e) {
    hotList.value = []
  }
}

async function loadFeed() {
  try {
    const res = await getInfoFeed()
    feedList.value = res.data || []
  } catch (e) {
    feedList.value = []
  }
}

/* ========== 编辑/新增 ========== */
async function openDialog(row) {
  editId.value = null
  form.title = ''
  form.content = ''
  form.coverImage = ''
  form.category = ''
  form.author = '小知宠'
  form.status = 1
  if (row) {
    editId.value = row.id
    try {
      const res = await adminGetInfo(row.id)
      const d = res.data
      form.title = d.title
      form.content = d.content || ''
      form.coverImage = d.coverImage || ''
      form.category = d.category || ''
      form.author = d.author || '小知宠'
      form.status = d.status
    } catch { /* ignore */ }
  }
  dialogVisible.value = true
}

function beforeUpload(file) {
  const validTypes = ['image/jpeg', 'image/jpg', 'image/png']
  if (!validTypes.includes(file.type)) {
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
        await adminUpdateInfo(editId.value, payload)
        ElMessage.success('修改成功')
      } else {
        await adminSaveInfo(payload)
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
      await loadList(page.value)
      await loadStats()
      await loadHot()
      await loadFeed()
    } finally {
      saving.value = false
    }
  })
}

async function onToggleStatus(row, status) {
  try {
    await adminUpdateInfoStatus(row.id, status)
    ElMessage.success(status === 1 ? '已发布' : '已下架')
    await loadList(page.value)
    await loadStats()
  } catch (e) { /* ignore */ }
}

async function onDelete(row) {
  try {
    await ElMessageBox.confirm(`确认删除资讯「${row.title}」吗？`, '提示', { type: 'warning' })
  } catch { return }
  try {
    await adminDeleteInfo(row.id)
    ElMessage.success('删除成功')
    await loadList(page.value)
    await loadStats()
  } catch (e) { /* ignore */ }
}

onMounted(() => {
  loadList(1)
  loadStats()
  loadHot()
  loadFeed()
})
</script>

<style scoped>
.admin-info-page {
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
.h-left {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  gap: 14px;
}
.h-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  background: linear-gradient(135deg, #6d7bf0, #6a5bd8);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 6px 14px rgba(106, 91, 216, 0.3);
}
.h-icon svg { width: 26px; height: 26px; }
.h-left h2 { margin: 0; font-size: 20px; color: #2c3e50; }
.h-left p { margin: 2px 0 0; font-size: 13px; color: #7a8699; }
.h-slogan {
  position: relative;
  z-index: 1;
  font-family: 'Comic Sans MS', 'STKaiti', cursive;
  font-size: 15px;
  color: #4a5b7d;
  line-height: 1.5;
  text-align: right;
  white-space: nowrap;
}

/* ===== 统计卡 ===== */
.stat-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 20px;
}
.stat-card {
  background: #fff;
  border: 1px solid #edf0f8;
  border-radius: 14px;
  padding: 18px 20px;
  display: flex;
  align-items: center;
  gap: 14px;
  box-shadow: 0 6px 20px rgba(94, 110, 190, 0.06);
}
.s-icon {
  width: 46px;
  height: 46px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.s-icon svg { width: 22px; height: 22px; }
.s-body { min-width: 0; }
.s-label { font-size: 13px; color: #8a93a6; margin-bottom: 4px; }
.s-value { font-size: 22px; font-weight: 700; color: #2c3e50; display: flex; align-items: baseline; gap: 6px; }
.s-up { font-size: 12px; font-weight: 600; }
.s-cmp { font-size: 11px; color: #b0b8c8; margin-top: 2px; }

/* ===== 主布局 ===== */
.main-layout {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 280px;
  gap: 16px;
}
.main-col { min-width: 0; }

/* ===== 筛选栏 ===== */
.filter-bar {
  background: #fff;
  border: 1px solid #edf0f8;
  border-radius: 14px;
  padding: 14px 16px;
  margin-bottom: 14px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  flex-wrap: wrap;
}
.cat-tabs {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}
.cat-tab {
  padding: 6px 14px;
  border: none;
  background: #f2f4fa;
  border-radius: 20px;
  font-size: 13px;
  color: #5c6a8c;
  cursor: pointer;
  transition: all 0.2s;
}
.cat-tab.on {
  background: #fff;
  color: #6d7bf0;
  font-weight: 600;
  box-shadow: 0 2px 8px rgba(109, 123, 240, 0.15);
}
.filter-right {
  display: flex;
  gap: 10px;
  align-items: center;
}
.f-search { width: 220px; }
.f-add {
  background: linear-gradient(90deg, #6d7bf0, #6a5bd8);
  border: none;
  border-radius: 8px;
}

/* ===== 表格 ===== */
.table-card {
  background: #fff;
  border: 1px solid #edf0f8;
  border-radius: 14px;
  padding: 4px;
  box-shadow: 0 6px 20px rgba(94, 110, 190, 0.06);
}
.row-cover {
  width: 56px;
  height: 38px;
  object-fit: cover;
  border-radius: 6px;
}
.no-cover { color: #c0c4cc; font-size: 12px; }
.row-title { font-size: 13px; color: #2c3e50; }
.view-count { font-size: 13px; color: #5b6cf5; font-weight: 600; }
.op-link {
  color: #6d7bf0;
  font-size: 13px;
  cursor: pointer;
  margin-right: 10px;
}
.op-link.warn { color: #f59f00; }
.op-link.ok { color: #12b886; }
.op-link.danger { color: #f56c6c; }
.pager-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
}
.pager-text { font-size: 12px; color: #8a93a6; }

/* ===== 右侧面板 ===== */
.side-col {
  display: flex;
  flex-direction: column;
  gap: 14px;
}
.side-card {
  background: #fff;
  border: 1px solid #edf0f8;
  border-radius: 14px;
  padding: 16px 18px;
  box-shadow: 0 6px 20px rgba(94, 110, 190, 0.06);
}
.side-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
  padding-bottom: 10px;
  border-bottom: 1px solid #f0f2f8;
}
.sh-title {
  font-size: 15px;
  font-weight: 600;
  color: #2c3e50;
  display: flex;
  align-items: center;
  gap: 6px;
}
.sh-more {
  font-size: 12px;
  color: #6d7bf0;
  cursor: pointer;
}
.hot-list { list-style: none; margin: 0; padding: 0; }
.hot-list li {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 0;
  cursor: pointer;
  border-bottom: 1px solid #f5f6fa;
}
.hot-list li:last-child { border-bottom: none; }
.hot-rank {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: #e8ebf5;
  color: #8a93a6;
  font-size: 11px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.hot-rank.top { background: linear-gradient(135deg, #6d7bf0, #6a5bd8); color: #fff; }
.hot-title {
  flex: 1;
  font-size: 13px;
  color: #2c3e50;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.hot-views { font-size: 12px; color: #f59f00; font-weight: 600; flex-shrink: 0; }

.feed-list { list-style: none; margin: 0; padding: 0; }
.feed-list li {
  display: flex;
  gap: 10px;
  padding: 10px 0;
  border-bottom: 1px solid #f5f6fa;
}
.feed-list li:last-child { border-bottom: none; }
.feed-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #6d7bf0;
  flex-shrink: 0;
  margin-top: 6px;
}
.feed-body { flex: 1; min-width: 0; }
.feed-user { font-size: 13px; font-weight: 600; color: #2c3e50; }
.feed-act { font-size: 12px; color: #7a8699; display: block; margin: 2px 0; }
.feed-time { font-size: 11px; color: #b0b8c8; }

/* ===== 推广卡 ===== */
.promo-card {
  background: linear-gradient(135deg, #6d7bf0, #6a5bd8);
  border-radius: 14px;
  padding: 20px 18px;
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  box-shadow: 0 8px 20px rgba(106, 91, 216, 0.2);
}
.promo-text { flex: 1; }
.promo-text b { font-size: 14px; color: #fff; }
.promo-text p { font-size: 12px; color: rgba(255, 255, 255, 0.8); margin: 4px 0 8px; }
.promo-btn {
  font-size: 12px;
  color: #fff;
  background: rgba(255, 255, 255, 0.2);
  padding: 4px 12px;
  border-radius: 12px;
}
.promo-illust svg { width: 80px; height: 70px; }

/* ===== 编辑弹窗 ===== */
.cover-uploader :deep(.el-upload) {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  width: 200px;
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}
.cover-preview { width: 100%; height: 100%; object-fit: cover; }
.cover-empty {
  color: #909399;
  font-size: 13px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.cover-empty .el-icon { font-size: 26px; }
.editor-wrap {
  width: 100%;
  border: 1px solid #dcdfe6;
  border-radius: 6px;
  overflow: hidden;
}
.editor-toolbar { border-bottom: 1px solid #e8e8e8; }
.editor-body { height: 320px !important; overflow-y: hidden; }

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
  .f-search { width: 100%; }
}
</style>
