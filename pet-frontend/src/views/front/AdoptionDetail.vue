<template>
  <div class="adoption-detail" v-loading="loading">
    <div class="page-shell" v-if="info">
      <el-button text @click="$router.back()">‹ 返回</el-button>

      <div class="detail-card">
        <div class="detail-img">
          <img :src="info.photoUrl" :alt="info.petName" />
          <span class="type-tag" :class="info.type">
            {{ info.type === 'adoption' ? '领养' : '寄养' }}
          </span>
        </div>
        <div class="detail-info">
          <h1>{{ info.petName }}</h1>
          <div class="meta">
            <span>品种：{{ info.breed || '未知' }}</span>
            <span>类型：{{ info.petType || '-' }}</span>
            <span>年龄：{{ info.age || '-' }}</span>
            <span>性别：{{ info.gender || '-' }}</span>
          </div>
          <div class="desc">
            <h3>详情描述</h3>
            <p>{{ info.description }}</p>
          </div>
          <div class="contact">
            <h3>联系方式</h3>
            <p>{{ info.contact }}</p>
          </div>
          <el-button type="primary" size="large" @click="showApply = true" :disabled="!isLogin">
            申请{{ info.type === 'adoption' ? '领养' : '寄养' }}
          </el-button>
        </div>
      </div>
    </div>

    <el-dialog v-model="showApply" :title="'申请' + (info?.type === 'adoption' ? '领养' : '寄养')" width="420px">
      <el-form @submit.prevent>
        <el-form-item label="留言">
          <el-input v-model="applyForm.message" type="textarea" :rows="3" placeholder="请简单介绍您的情况" />
        </el-form-item>
        <el-form-item label="联系方式">
          <el-input v-model="applyForm.contact" placeholder="手机号或微信" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showApply = false">取消</el-button>
        <el-button type="primary" @click="submitApply">提交申请</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getAdoption, applyAdoption } from '@/api/adoption'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const isLogin = ref(!!userStore.token)

const info = ref(null)
const loading = ref(false)
const showApply = ref(false)
const applyForm = ref({ message: '', contact: '' })

async function loadDetail() {
  loading.value = true
  try {
    const res = await getAdoption(route.params.id)
    info.value = res.data
  } catch (e) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

async function submitApply() {
  if (!applyForm.value.contact) {
    ElMessage.warning('请填写联系方式')
    return
  }
  try {
    await applyAdoption(route.params.id, applyForm.value)
    ElMessage.success('申请已提交，等待发布者审核')
    showApply.value = false
  } catch (e) {
    // 错误已由拦截器提示
  }
}

onMounted(loadDetail)
</script>

<style scoped>
.adoption-detail { min-height: 100%; background: #f5f7fa; }
.page-shell { padding: 18px clamp(16px, 2vw, 32px) 40px; max-width: 900px; margin: 0 auto; }
.detail-card { background: #fff; border-radius: 16px; overflow: hidden; margin-top: 12px;
  display: flex; gap: 24px; padding: 24px; }
.detail-img { position: relative; width: 320px; flex-shrink: 0; border-radius: 12px; overflow: hidden; }
.detail-img img { width: 100%; height: 280px; object-fit: cover; }
.type-tag { position: absolute; top: 12px; left: 12px; padding: 4px 12px; border-radius: 12px;
  font-size: 12px; color: #fff; }
.type-tag.adoption { background: #67c23a; }
.type-tag.foster { background: #e6a23c; }
.detail-info { flex: 1; }
.detail-info h1 { margin: 0 0 12px; }
.meta { display: flex; flex-wrap: wrap; gap: 16px; color: #909399; font-size: 14px; margin-bottom: 16px; }
.desc, .contact { margin-bottom: 16px; }
.desc h3, .contact h3 { font-size: 15px; margin: 0 0 6px; color: #303133; }
.desc p, .contact p { margin: 0; color: #606266; line-height: 1.6; }
@media (max-width: 760px) {
  .detail-card { flex-direction: column; }
  .detail-img { width: 100%; }
}
</style>
