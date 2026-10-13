<template>
  <div class="post-detail" v-loading="loading">
    <div class="page-shell" v-if="post">
      <el-button text @click="$router.back()">‹ 返回</el-button>

      <div class="detail-card">
        <h1>{{ post.title }}</h1>
        <div class="post-meta">
          <span class="cat-tag">{{ post.category || '日常' }}</span>
          <span>♡ {{ likeCount }}</span>
          <span>💬 {{ post.commentCount || 0 }}</span>
        </div>
        <div class="post-content">{{ post.content }}</div>

        <div class="action-bar">
          <el-button :type="liked ? 'danger' : 'default'" @click="toggleLike">
            {{ liked ? '已点赞' : '点赞' }} ({{ likeCount }})
          </el-button>
        </div>
      </div>

      <div class="comment-section">
        <h3>评论 ({{ comments.length }})</h3>
        <div class="comment-input" v-if="isLogin">
          <el-input v-model="commentText" type="textarea" :rows="2" placeholder="说点什么..." />
          <el-button type="primary" @click="submitComment">发表评论</el-button>
        </div>
        <div v-else class="comment-login-tip">
          登录后即可评论 <el-link @click="$router.push('/login')">去登录</el-link>
        </div>
        <div class="comment-list">
          <div v-for="c in comments" :key="c.id" class="comment-item">
            <el-avatar :size="32">{{ 'U' }}</el-avatar>
            <div class="comment-body">
              <span class="comment-user">用户{{ c.userId }}</span>
              <p class="comment-text">{{ c.content }}</p>
              <span class="comment-time">{{ formatTime(c.createTime) }}</span>
            </div>
          </div>
          <el-empty v-if="comments.length === 0" description="暂无评论" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getPost, likePost, addComment, listComments } from '@/api/community'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const userStore = useUserStore()
const isLogin = ref(!!userStore.token)

const post = ref(null)
const comments = ref([])
const likeCount = ref(0)
const liked = ref(false)
const commentText = ref('')
const loading = ref(false)

async function loadDetail() {
  loading.value = true
  try {
    const res = await getPost(route.params.id)
    post.value = res.data
    likeCount.value = res.data.likeCount || 0
    loadComments()
  } catch (e) { ElMessage.error('加载失败') }
  finally { loading.value = false }
}

async function loadComments() {
  const res = await listComments(route.params.id, { page: 1, size: 50 })
  comments.value = res.data.list || []
}

async function toggleLike() {
  if (!isLogin.value) { ElMessage.warning('请先登录'); return }
  const res = await likePost(route.params.id)
  likeCount.value = res.data
  liked.value = !liked.value
}

async function submitComment() {
  if (!isLogin.value) { ElMessage.warning('请先登录'); return }
  if (!commentText.value.trim()) { ElMessage.warning('请输入评论内容'); return }
  try {
    await addComment(route.params.id, { content: commentText.value })
    commentText.value = ''
    ElMessage.success('评论成功')
    loadComments()
  } catch (e) {}
}

function formatTime(t) { return t ? t.substring(0, 16) : '' }

onMounted(loadDetail)
</script>

<style scoped>
.post-detail { min-height: 100%; background: #f5f7fa; }
.page-shell { padding: 18px clamp(16px, 2vw, 32px) 40px; max-width: 800px; margin: 0 auto; }
.detail-card { background: #fff; border-radius: 16px; padding: 24px; margin-top: 12px; }
.detail-card h1 { margin: 0 0 12px; font-size: 22px; }
.post-meta { display: flex; gap: 14px; font-size: 13px; color: #909399; margin-bottom: 16px; }
.cat-tag { background: #ecf5ff; color: #409eff; padding: 2px 8px; border-radius: 4px; }
.post-content { line-height: 1.8; color: #303133; margin-bottom: 20px; white-space: pre-wrap; word-break: break-word; }
.action-bar { padding-top: 12px; border-top: 1px solid #ebeef5; }
.comment-section { background: #fff; border-radius: 16px; padding: 24px; margin-top: 16px; }
.comment-section h3 { margin: 0 0 16px; }
.comment-input { display: flex; gap: 10px; margin-bottom: 20px; }
.comment-input :deep(.el-textarea) { flex: 1; }
.comment-login-tip { padding: 12px; background: #f5f7fa; border-radius: 8px; margin-bottom: 16px; text-align: center; color: #909399; }
.comment-list { display: flex; flex-direction: column; gap: 16px; }
.comment-item { display: flex; gap: 10px; }
.comment-body { flex: 1; }
.comment-user { font-weight: 600; font-size: 13px; }
.comment-text { margin: 4px 0; font-size: 14px; color: #606266; }
.comment-time { font-size: 12px; color: #c0c4cc; }
</style>
