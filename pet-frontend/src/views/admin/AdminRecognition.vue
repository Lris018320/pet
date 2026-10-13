<template>
  <div class="admin-recognition-page">
    <div class="page-title-row">
      <h2>识别记录</h2>
      <p class="sub">查询平台累计的 AI 识别记录</p>
    </div>

    <el-card shadow="never" class="filter-card">
      <el-form :inline="true" @submit.prevent>
        <el-form-item label="用户ID">
          <el-input v-model="filter.userId" placeholder="精确" clearable @keyup.enter="loadList(1)" style="width:120px" />
        </el-form-item>
        <el-form-item label="品种">
          <el-input v-model="filter.breed" placeholder="模糊" clearable @keyup.enter="loadList(1)" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="filter.success" placeholder="全部" clearable style="width:120px">
            <el-option label="成功" :value="1" />
            <el-option label="失败" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadList(1)">查询</el-button>
          <el-button @click="resetFilter">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never" class="table-card">
      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column label="ID" prop="id" width="80" />
        <el-table-column label="用户ID" prop="userId" width="100" />
        <el-table-column label="图片" width="100">
          <template #default="{ row }">
            <el-image
              v-if="row.imageUrl"
              :src="resolveImg(row.imageUrl)"
              fit="cover"
              style="width:60px;height:60px;border-radius:4px"
              :preview-src-list="[resolveImg(row.imageUrl)]"
            />
            <span v-else class="text-muted">—</span>
          </template>
        </el-table-column>
        <el-table-column label="类型" prop="petType" width="80">
          <template #default="{ row }">
            <span v-if="row.petType">{{ row.petType }}</span>
            <span v-else class="text-muted">—</span>
          </template>
        </el-table-column>
        <el-table-column label="品种" prop="breed" min-width="120">
          <template #default="{ row }">
            <span v-if="row.breed">{{ row.breed }}</span>
            <span v-else class="text-muted">—</span>
          </template>
        </el-table-column>
        <el-table-column label="置信度" width="110">
          <template #default="{ row }">
            <el-tag v-if="row.success === 1" size="small" type="success">{{ row.confidence }}%</el-tag>
            <span v-else class="text-muted">—</span>
          </template>
        </el-table-column>
        <el-table-column label="结果" width="100">
          <template #default="{ row }">
            <el-tag :type="row.success === 1 ? 'success' : 'danger'" size="small">
              {{ row.success === 1 ? '成功' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="识别时间" min-width="160">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        <template #empty>
          <el-empty description="暂无记录" />
        </template>
      </el-table>

      <div class="pager">
        <el-pagination
          background
          layout="total, prev, pager, next, jumper"
          :current-page="page"
          :page-size="size"
          :total="total"
          @current-change="loadList"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { pageRecognition } from '@/api/admin'

const list = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const loading = ref(false)
const filter = reactive({ userId: '', breed: '', success: null })

const API_BASE = import.meta.env.VITE_API_BASE || '/api'
function resolveImg(url) {
  if (!url) return ''
  if (url.startsWith('http')) return url
  return API_BASE + url
}
function formatTime(t) {
  if (!t) return ''
  return t.replace('T', ' ').substring(0, 16)
}

async function loadList(p = 1) {
  page.value = p
  loading.value = true
  try {
    const res = await pageRecognition({
      page: page.value,
      size: size.value,
      userId: filter.userId || undefined,
      breed: filter.breed || undefined,
      success: filter.success
    })
    list.value = res.data.list || []
    total.value = res.data.total || 0
  } finally {
    loading.value = false
  }
}

function resetFilter() {
  filter.userId = ''
  filter.breed = ''
  filter.success = null
  loadList(1)
}

onMounted(() => loadList(1))
</script>

<style scoped>
.admin-recognition-page {
  padding: 4px;
}
.page-title-row {
  margin-bottom: 20px;
}
.page-title-row h2 {
  font-size: 22px;
  color: #2c3e50;
  margin: 0 0 4px;
}
.sub {
  color: #909399;
  font-size: 13px;
  margin: 0;
}
.filter-card {
  border-radius: 8px;
  margin-bottom: 16px;
}
.table-card {
  border-radius: 8px;
}
.text-muted {
  color: #c0c4cc;
  font-size: 13px;
}
.pager {
  margin-top: 16px;
  text-align: right;
}
</style>
