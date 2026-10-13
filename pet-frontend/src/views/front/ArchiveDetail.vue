<template>
  <div class="page-shell archive-detail-page" v-loading="loading">
    <div class="page-body narrow" v-if="archive">
      <el-page-header @back="goBack" class="back-header">
        <template #content>
          <span class="page-title">{{ archive.petName }} · 档案详情</span>
        </template>
      </el-page-header>

      <el-card class="content-card main-card">
        <div class="hero">
          <div class="thumb">
            <img
              v-if="archive.photoUrl"
              :src="resolveImg(archive.photoUrl)"
              :alt="archive.petName"
            />
            <div v-else class="thumb-empty">
              <el-icon><Picture /></el-icon>
            </div>
          </div>
          <div class="info">
            <h1>{{ archive.petName }}</h1>
            <div class="meta-grid">
              <div class="meta-item">
                <span class="label">品种</span>
                <span class="value link" @click="goBreedDetail">{{ archive.breed || '—' }}</span>
              </div>
              <div class="meta-item">
                <span class="label">类型</span>
                <span class="value">{{ archive.petType || '—' }}</span>
              </div>
              <div class="meta-item">
                <span class="label">性别</span>
                <span class="value">{{ genderText(archive.gender) }}</span>
              </div>
              <div class="meta-item">
                <span class="label">年龄</span>
                <span class="value">{{ archive.age ? archive.age + ' 岁' : '—' }}</span>
              </div>
              <div class="meta-item">
                <span class="label">来源</span>
                <el-tag size="small" :type="archive.source === 'ai' ? 'success' : 'info'">
                  {{ archive.source === 'ai' ? 'AI 识别建档' : '手动建档' }}
                </el-tag>
              </div>
              <div class="meta-item">
                <span class="label">建档时间</span>
                <span class="value">{{ formatTime(archive.createTime) }}</span>
              </div>
            </div>
          </div>
        </div>

        <el-divider />

        <div class="actions">
          <el-button type="primary" @click="goEdit">编辑</el-button>
          <el-button @click="goBack">返回列表</el-button>
        </div>
      </el-card>
    </div>

    <el-empty v-else-if="!loading" description="档案不存在或无权访问" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Picture } from '@element-plus/icons-vue'
import { getArchive } from '@/api/archive'
import { getBreedByName } from '@/api/breed'

const route = useRoute()
const router = useRouter()

const archive = ref(null)
const loading = ref(false)

const API_BASE = import.meta.env.VITE_API_BASE || '/api'
function resolveImg(url) {
  if (!url) return ''
  if (url.startsWith('http')) return url
  return API_BASE + url
}
function formatTime(t) {
  if (!t) return '—'
  return t.replace('T', ' ').substring(0, 10)
}
function genderText(g) {
  if (g === 'M' || g === '公' || g === 'male') return '公'
  if (g === 'F' || g === '母' || g === 'female') return '母'
  return '—'
}

async function load() {
  const id = route.params.id
  if (!id) return
  loading.value = true
  try {
    const res = await getArchive(id)
    archive.value = res.data
  } finally {
    loading.value = false
  }
}

async function goBreedDetail() {
  if (!archive.value?.breed) return
  try {
    const res = await getBreedByName(archive.value.breed)
    if (res.data && res.data.id) {
      router.push(`/breed/${res.data.id}`)
      return
    }
  } catch (e) {
    // ignore
  }
  // 找不到则跳列表
  router.push('/breed')
}

function goEdit() {
  router.push('/archive?edit=' + route.params.id)
}

function goBack() {
  router.push('/archive')
}

onMounted(load)
</script>

<style scoped>
.back-header {
  margin-bottom: 16px;
}
.page-title {
  font-weight: 600;
  font-size: 16px;
}
.main-card {
  border-radius: 14px;
}
.hero {
  display: flex;
  gap: 24px;
  align-items: flex-start;
}
.thumb {
  width: 220px;
  height: 220px;
  border-radius: 12px;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  flex-shrink: 0;
}
.thumb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.thumb-empty {
  color: #c0c4cc;
  font-size: 48px;
}
.info {
  flex: 1;
}
.info h1 {
  font-size: 26px;
  color: #2c3e50;
  margin-bottom: 16px;
}
.meta-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px 24px;
}
.meta-item {
  display: flex;
  gap: 8px;
  align-items: center;
}
.label {
  color: #909399;
  font-size: 13px;
  width: 70px;
  flex-shrink: 0;
}
.value {
  color: #2c3e50;
  font-size: 14px;
}
.link {
  color: #409eff;
  cursor: pointer;
  text-decoration: underline;
}
.actions {
  text-align: center;
}
</style>
