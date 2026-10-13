<template>
  <div class="publish-page">
    <div class="page-shell">
      <div class="form-card">
        <h2>发布帖子</h2>
        <el-form :model="form" label-width="80px" @submit.prevent>
          <el-form-item label="标题">
            <el-input v-model="form.title" placeholder="请输入标题" />
          </el-form-item>
          <el-form-item label="分类">
            <el-select v-model="form.category" placeholder="请选择分类">
              <el-option label="日常" value="日常" />
              <el-option label="晒宠" value="晒宠" />
              <el-option label="求助" value="求助" />
              <el-option label="问答" value="问答" />
            </el-select>
          </el-form-item>
          <el-form-item label="封面图">
            <el-input v-model="form.coverImage" placeholder="封面图URL（可留空）" />
          </el-form-item>
          <el-form-item label="正文">
            <el-input v-model="form.content" type="textarea" :rows="8" placeholder="请输入正文内容" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="submit">发布</el-button>
            <el-button @click="$router.back()">取消</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { savePost } from '@/api/community'

const router = useRouter()
const form = reactive({ title: '', category: '日常', coverImage: '', content: '' })

async function submit() {
  if (!form.title.trim()) { ElMessage.warning('请输入标题'); return }
  if (!form.content.trim()) { ElMessage.warning('请输入正文'); return }
  try {
    await savePost(form)
    ElMessage.success('发布成功')
    router.push('/community')
  } catch (e) {}
}
</script>

<style scoped>
.publish-page { min-height: 100%; background: #f5f7fa; }
.page-shell { padding: 18px clamp(16px, 2vw, 32px) 40px; max-width: 700px; margin: 0 auto; }
.form-card { background: #fff; border-radius: 16px; padding: 30px; margin-top: 12px; }
.form-card h2 { margin: 0 0 24px; }
</style>
