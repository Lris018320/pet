<template>
  <div class="page-shell breed-page">
    <PageBanner
      icon="🐾"
      title="宠物品种科普"
      subtitle="浏览各品种性格、喂养禁忌与护理建议"
      theme="dark"
    />

    <div class="page-body">
      <!-- 筛选区 -->
      <el-card class="filter-card" :body-style="{ padding: '14px 18px' }">
        <el-radio-group v-model="petType" @change="loadList(1)">
          <el-radio-button value="">全部</el-radio-button>
          <el-radio-button value="狗">狗</el-radio-button>
          <el-radio-button value="猫">猫</el-radio-button>
        </el-radio-group>
        <el-input
          v-model="keyword"
          placeholder="搜索品种名"
          clearable
          class="search-input"
          @keyup.enter="loadList(1)"
          @clear="loadList(1)"
        >
          <template #append>
            <el-button :icon="Search" @click="loadList(1)" />
          </template>
        </el-input>
      </el-card>

      <!-- 列表 -->
      <div v-loading="loading">
        <el-empty v-if="!loading && list.length === 0" description="暂无品种科普" />

        <el-row v-else :gutter="20">
          <el-col
            v-for="item in list"
            :key="item.id"
            :xs="24"
            :sm="12"
            :md="8"
            :lg="6"
            class="breed-col"
          >
            <el-card class="breed-card" shadow="hover" @click="goDetail(item.id)">
              <div class="card-body">
                <div class="avatar" :class="item.petType === '狗' ? 'dog' : 'cat'">
                  {{ item.breedName?.charAt(0) }}
                </div>
                <div class="info">
                  <div class="name-line">
                    <span class="name">{{ item.breedName }}</span>
                    <el-tag size="small" :type="item.petType === '狗' ? 'warning' : 'success'">
                      {{ item.petType }}
                    </el-tag>
                  </div>
                  <div class="desc">
                    {{ truncate(item.description, 40) }}
                  </div>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <div class="pager" v-if="total > size">
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Search } from '@element-plus/icons-vue'
import { listBreeds } from '@/api/breed'
import PageBanner from '@/components/PageBanner.vue'

const router = useRouter()
const list = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(12)
const loading = ref(false)
const petType = ref('')
const keyword = ref('')

function truncate(str, n) {
  if (!str) return ''
  return str.length > n ? str.substring(0, n) + '...' : str
}

async function loadList(p = 1) {
  page.value = p
  loading.value = true
  try {
    const res = await listBreeds({
      page: page.value,
      size: size.value,
      petType: petType.value || undefined,
      keyword: keyword.value || undefined
    })
    list.value = res.data.list || []
    total.value = res.data.total || 0
  } finally {
    loading.value = false
  }
}

function goDetail(id) {
  router.push(`/breed/${id}`)
}

onMounted(() => loadList(1))
</script>

<style scoped>
.filter-card :deep(.el-card__body) {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
}
.search-input {
  width: 260px;
}
.breed-col {
  margin-bottom: 20px;
}
.breed-card {
  border-radius: 14px;
  cursor: pointer;
  transition: transform 0.25s, box-shadow 0.25s;
  border: none;
}
.breed-card:hover {
  transform: translateY(-3px);
}
.card-body {
  display: flex;
  gap: 12px;
  align-items: flex-start;
}
.avatar {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 22px;
  font-weight: 600;
}
.avatar.dog {
  background: linear-gradient(135deg, #f0a029, #e6a23c);
}
.avatar.cat {
  background: linear-gradient(135deg, #67c23a, #5daf34);
}
.info {
  flex: 1;
  min-width: 0;
}
.name-line {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 6px;
}
.name {
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
}
.desc {
  color: #606266;
  font-size: 13px;
  line-height: 1.5;
}
.pager {
  margin-top: 24px;
  text-align: center;
}
</style>
