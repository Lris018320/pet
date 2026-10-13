<template>
  <div class="page-shell breed-detail-page" v-loading="loading">
    <div class="page-body narrow" v-if="breed">
      <el-page-header @back="goBack" class="back-header">
        <template #content>
          <span class="page-title">{{ breed.breedName }}</span>
        </template>
      </el-page-header>

      <div class="hero">
        <div class="hero-avatar" :class="breed.petType === '狗' ? 'dog' : 'cat'">
          {{ breed.breedName?.charAt(0) }}
        </div>
        <div class="hero-info">
          <h1>{{ breed.breedName }}</h1>
          <div class="meta-line">
            <el-tag :type="breed.petType === '狗' ? 'warning' : 'success'">{{ breed.petType }}</el-tag>
          </div>
        </div>
      </div>

      <div class="sections">
        <el-card class="content-card section">
          <template #header>
            <div class="section-title"><el-icon><Document /></el-icon> 品种描述</div>
          </template>
          <div class="content">{{ breed.description || '暂无' }}</div>
        </el-card>

        <el-card class="content-card section">
          <template #header>
            <div class="section-title"><el-icon><Aim /></el-icon> 性格特点</div>
          </template>
          <div class="content">{{ breed.personality || '暂无' }}</div>
        </el-card>

        <el-card class="content-card section">
          <template #header>
            <div class="section-title warn"><el-icon><Warning /></el-icon> 喂养禁忌</div>
          </template>
          <div class="content">{{ breed.feedingTaboo || '暂无' }}</div>
        </el-card>

        <el-card class="content-card section">
          <template #header>
            <div class="section-title"><el-icon><MagicStick /></el-icon> 护理建议</div>
          </template>
          <div class="content">{{ breed.careTips || '暂无' }}</div>
        </el-card>
      </div>

      <div class="actions">
        <el-button @click="goBack">返回列表</el-button>
      </div>
    </div>

    <el-empty v-else-if="!loading" description="品种不存在或已下架" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Document, Aim, Warning, MagicStick } from '@element-plus/icons-vue'
import { getBreed } from '@/api/breed'

const route = useRoute()
const router = useRouter()

const breed = ref(null)
const loading = ref(false)

async function load() {
  const id = route.params.id
  if (!id) return
  loading.value = true
  try {
    const res = await getBreed(id)
    breed.value = res.data
  } finally {
    loading.value = false
  }
}

function goBack() {
  router.push('/breed')
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
.hero {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 24px;
  display: flex;
  gap: 20px;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
}
.hero-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 36px;
  font-weight: 600;
}
.hero-avatar.dog {
  background: linear-gradient(135deg, #f0a029, #e6a23c);
}
.hero-avatar.cat {
  background: linear-gradient(135deg, #67c23a, #5daf34);
}
.hero-info h1 {
  font-size: 26px;
  color: #2c3e50;
  margin-bottom: 8px;
}
.section {
  margin-bottom: 16px;
  border-radius: 10px;
}
.section-title {
  font-weight: 600;
  color: #2c3e50;
  display: flex;
  align-items: center;
  gap: 6px;
}
.section-title.warn {
  color: #e6a23c;
}
.content {
  color: #606266;
  line-height: 1.8;
  white-space: pre-wrap;
}
.actions {
  text-align: center;
  margin-top: 20px;
}
</style>
