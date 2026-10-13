<template>
  <div class="page-shell detail-page">
    <div class="page-body narrow" v-loading="loading">
      <el-button text class="back-btn" @click="goBack">
        <el-icon><ArrowLeft /></el-icon> 返回资讯列表
      </el-button>

      <el-empty v-if="!loading && !info" description="资讯不存在或已下架" />

      <article v-if="info" class="article content-card">
        <header class="article-header">
          <div class="header-inner">
            <el-tag v-if="info.category" class="cat-tag" size="large" effect="dark">
              {{ info.category }}
            </el-tag>
            <h1 class="title">{{ info.title }}</h1>
            <div class="meta">
              <span class="meta-item">
                <el-icon><Calendar /></el-icon>
                发布于 {{ formatTime(info.createTime) }}
              </span>
              <span class="meta-dot">·</span>
              <span class="meta-item">
                <el-icon><View /></el-icon>
                宠知AI
              </span>
            </div>
          </div>
        </header>
        <div class="article-body">
          <!-- eslint-disable-next-line vue/no-v-html -->
          <div class="content" v-html="info.content"></div>
        </div>
        <footer class="article-footer">
          <el-button @click="goBack">
            <el-icon style="margin-right:4px"><ArrowLeft /></el-icon>返回列表
          </el-button>
        </footer>
      </article>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft, Calendar, View } from '@element-plus/icons-vue'
import { getInfo } from '@/api/info'

const route = useRoute()
const router = useRouter()
const info = ref(null)
const loading = ref(false)

function formatTime(t) {
  if (!t) return ''
  return t.replace('T', ' ').substring(0, 16)
}

function goBack() {
  router.push('/info')
}

async function loadDetail() {
  const id = route.params.id
  if (!id) return
  loading.value = true
  try {
    const res = await getInfo(id)
    info.value = res.data
  } catch (e) {
    info.value = null
  } finally {
    loading.value = false
  }
}

onMounted(loadDetail)
</script>

<style scoped>
.back-btn {
  margin-bottom: 16px;
  padding-left: 0;
  font-size: 14px;
}
.article {
  border-radius: 16px;
  overflow: hidden;
  background: #fff;
}
.article-header {
  background: linear-gradient(135deg, #4facfe 0%, #3b82d4 100%);
  padding: 36px 40px 28px;
  color: #fff;
}
/* 超宽屏下标题保持舒适阅读宽度，渐变头部仍铺满整卡 */
.article-header .header-inner {
  max-width: 900px;
  margin: 0 auto;
}
.cat-tag {
  margin-bottom: 14px;
}
.title {
  font-size: 28px;
  color: #fff;
  margin: 0 0 14px;
  line-height: 1.4;
  font-weight: 800;
}
.meta {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 13px;
  opacity: 0.9;
  flex-wrap: wrap;
}
.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
}
.meta-dot {
  opacity: 0.6;
}
.article-body {
  padding: 32px 40px;
}
.content {
  color: #3c4858;
  font-size: 15.5px;
  line-height: 1.9;
  max-width: 900px;
  margin: 0 auto;
}
.content :deep(p) {
  margin: 14px 0;
}
.content :deep(img) {
  max-width: 100%;
  border-radius: 10px;
  margin: 16px 0;
  box-shadow: 0 4px 16px rgba(44, 62, 80, 0.1);
}
.content :deep(h2) {
  font-size: 22px;
  margin: 28px 0 12px;
  color: #2c3e50;
  padding-left: 12px;
  border-left: 4px solid #4facfe;
}
.content :deep(h3) {
  font-size: 18px;
  margin: 22px 0 10px;
  color: #2c3e50;
}
.content :deep(ul),
.content :deep(ol) {
  padding-left: 24px;
  margin: 14px 0;
}
.content :deep(li) {
  margin: 6px 0;
}
.content :deep(blockquote) {
  border-left: 4px solid #c2e9fb;
  background: #f4f9fd;
  padding: 12px 16px;
  margin: 16px 0;
  color: #606266;
  border-radius: 0 8px 8px 0;
}
.article-footer {
  padding: 20px 40px 32px;
  border-top: 1px solid #f2f3f7;
  text-align: center;
}
</style>
