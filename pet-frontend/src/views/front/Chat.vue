<template>
  <div class="page-shell chat-page">
    <PageBanner
      icon="💬"
      title="AI 养护问答"
      subtitle="有问必答的宠物养护小助手，饮食、洗护、疫苗、训练都可以问"
      theme="purple"
    >
      <template #actions>
        <el-button :disabled="messages.length === 0" @click="confirmClear">
          清空对话
        </el-button>
      </template>
    </PageBanner>

    <div class="page-body narrow">
      <el-card class="content-card chat-card" :body-style="{ padding: '0' }">
        <!-- 消息窗口 -->
        <div ref="windowRef" class="chat-window" v-loading="loading">
          <!-- 空状态：欢迎语 + 快捷提问 -->
          <div v-if="!loading && messages.length === 0" class="welcome">
            <div class="msg-row assistant">
              <div class="avatar ai-avatar">🐾</div>
              <div class="msg-main">
                <div class="bubble ai">
                  你好呀！我是宠物养护小助手～<br />
                  不管是猫咪还是狗狗，<b>饮食喂养、洗澡护理、疫苗驱虫、行为训练、常见不适</b>都可以问我。
                </div>
              </div>
            </div>
            <div class="welcome-tips">
              <div class="tips-title">💡 你可以试试问我：</div>
              <div class="quick-tags">
                <span
                  v-for="q in quickQuestions"
                  :key="q"
                  class="quick-tag"
                  @click="send(q)"
                >{{ q }}</span>
              </div>
            </div>
          </div>

          <!-- 消息列表 -->
          <template v-else>
            <div
              v-for="msg in messages"
              :key="msg.id"
              class="msg-row"
              :class="msg.role"
            >
              <div class="avatar" :class="msg.role === 'user' ? 'user-avatar' : 'ai-avatar'">
                {{ msg.role === 'user' ? userInitial : '🐾' }}
              </div>
              <div class="msg-main">
                <div class="bubble" :class="msg.role">{{ msg.content }}</div>
                <div class="msg-time">{{ formatTime(msg.createTime) }}</div>
              </div>
            </div>
          </template>

          <!-- AI 正在输入 -->
          <div v-if="sending" class="msg-row assistant">
            <div class="avatar ai-avatar">🐾</div>
            <div class="msg-main">
              <div class="bubble ai typing">
                <span class="dot"></span>
                <span class="dot"></span>
                <span class="dot"></span>
              </div>
            </div>
          </div>
        </div>

        <!-- 快捷问题条 -->
        <div v-if="messages.length > 0" class="quick-bar">
          <span
            v-for="q in quickQuestions.slice(0, 4)"
            :key="q"
            class="quick-tag"
            :class="{ disabled: sending }"
            @click="!sending && send(q)"
          >{{ q }}</span>
        </div>

        <!-- 输入区 -->
        <div class="input-bar">
          <el-input
            v-model="input"
            type="textarea"
            :autosize="{ minRows: 1, maxRows: 4 }"
            resize="none"
            placeholder="输入你的养护疑问，Enter 发送，Shift + Enter 换行"
            :disabled="sending"
            @keydown.enter.exact.prevent="send()"
          />
          <el-button
            type="primary"
            class="send-btn"
            :loading="sending"
            :disabled="!input.trim()"
            @click="send()"
          >
            发送
          </el-button>
        </div>
      </el-card>

      <p class="disclaimer">⚠️ AI 建议仅供日常养护参考，不能替代专业兽医诊断，宠物严重不适请及时就医</p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, nextTick, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import PageBanner from '@/components/PageBanner.vue'
import { sendMessage, getChatHistory, clearChatHistory } from '@/api/chat'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

const messages = ref([])
const input = ref('')
const loading = ref(false)
const sending = ref(false)
const windowRef = ref(null)

const quickQuestions = [
  '狗狗能吃巧克力吗？',
  '猫咪多久洗一次澡？',
  '幼犬要打什么疫苗？',
  '宠物拉肚子怎么办？',
  '金毛怎么养？',
  '猫咪掉毛严重怎么办？'
]

const userInitial = computed(() => {
  const nickname = userStore.userInfo?.nickname
  return nickname ? nickname.charAt(0).toUpperCase() : '我'
})

function scrollToBottom() {
  nextTick(() => {
    const el = windowRef.value
    if (el) el.scrollTop = el.scrollHeight
  })
}

async function loadHistory() {
  loading.value = true
  try {
    const res = await getChatHistory()
    messages.value = res.data || []
    scrollToBottom()
  } finally {
    loading.value = false
  }
}

async function send(quickText) {
  const text = (typeof quickText === 'string' ? quickText : input.value).trim()
  if (!text || sending.value) return
  input.value = ''

  // 乐观追加用户消息
  messages.value.push({
    id: 'local-' + Date.now(),
    role: 'user',
    content: text,
    createTime: nowStr()
  })
  sending.value = true
  scrollToBottom()

  try {
    const res = await sendMessage(text)
    if (res.data) {
      messages.value.push(res.data)
    }
  } catch (e) {
    // 错误提示已由 request 拦截器统一处理
  } finally {
    sending.value = false
    scrollToBottom()
  }
}

async function confirmClear() {
  if (messages.value.length === 0) return
  try {
    await ElMessageBox.confirm('确定清空全部对话记录吗？此操作不可恢复', '清空对话', {
      type: 'warning'
    })
  } catch {
    return
  }
  try {
    await clearChatHistory()
    messages.value = []
    ElMessage.success('对话已清空')
  } catch (e) {
    // ignore
  }
}

function formatTime(t) {
  if (!t) return ''
  return t.replace('T', ' ').substring(5, 16)
}

function nowStr() {
  const d = new Date()
  const pad = (n) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
}

onMounted(loadHistory)
</script>

<style scoped>
.chat-card {
  overflow: hidden;
}

/* 消息窗口 */
.chat-window {
  height: 58vh;
  min-height: 380px;
  overflow-y: auto;
  padding: 24px 22px;
  background: linear-gradient(180deg, #f8f9fd 0%, #f2f4fa 100%);
}

/* 消息行 */
.msg-row {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  margin-bottom: 20px;
}
.msg-row.user {
  flex-direction: row-reverse;
}
.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  flex-shrink: 0;
}
.ai-avatar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  box-shadow: 0 4px 10px rgba(102, 126, 234, 0.3);
}
.user-avatar {
  background: #fff;
  color: #667eea;
  font-size: 15px;
  font-weight: 700;
  border: 2px solid #c4cef8;
}
.msg-main {
  display: flex;
  flex-direction: column;
  max-width: 76%;
}
.msg-row.user .msg-main {
  align-items: flex-end;
}
.bubble {
  padding: 12px 16px;
  border-radius: 14px;
  font-size: 14px;
  line-height: 1.75;
  white-space: pre-wrap;
  word-break: break-word;
}
.bubble.ai {
  background: #fff;
  color: #2c3e50;
  border: 1px solid #eceef5;
  border-top-left-radius: 4px;
  box-shadow: 0 2px 8px rgba(44, 62, 80, 0.05);
}
.bubble.user {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  border-top-right-radius: 4px;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.25);
}
.msg-time {
  font-size: 11px;
  color: #b0b3bb;
  margin-top: 5px;
  padding: 0 4px;
}

/* 正在输入动画 */
.typing {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 16px 18px;
}
.typing .dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: #c0c4cc;
  animation: typing-blink 1.2s infinite ease-in-out;
}
.typing .dot:nth-child(2) {
  animation-delay: 0.2s;
}
.typing .dot:nth-child(3) {
  animation-delay: 0.4s;
}
@keyframes typing-blink {
  0%, 60%, 100% {
    opacity: 0.3;
    transform: translateY(0);
  }
  30% {
    opacity: 1;
    transform: translateY(-4px);
  }
}

/* 欢迎区快捷问题 */
.welcome-tips {
  margin: 10px 0 0 50px;
}
.tips-title {
  font-size: 13px;
  color: #909399;
  margin-bottom: 10px;
}
.quick-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

/* 快捷问题标签 */
.quick-bar {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  padding: 12px 18px;
  background: #fff;
  border-top: 1px solid #f0f2f7;
}
.quick-tag {
  padding: 6px 14px;
  border-radius: 999px;
  background: #f0f3fc;
  color: #5a6fd9;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
  user-select: none;
}
.quick-tag:hover {
  background: #667eea;
  color: #fff;
  transform: translateY(-1px);
}
.quick-tag.disabled {
  opacity: 0.6;
  pointer-events: none;
}

/* 输入区 */
.input-bar {
  display: flex;
  align-items: flex-end;
  gap: 12px;
  padding: 14px 18px;
  background: #fff;
  border-top: 1px solid #f0f2f7;
}
.send-btn {
  height: 40px;
  padding: 0 26px;
  flex-shrink: 0;
}

.disclaimer {
  text-align: center;
  color: #a0a3ab;
  font-size: 12px;
  margin-top: 14px;
}

@media (max-width: 600px) {
  .chat-window {
    padding: 16px 12px;
    min-height: 340px;
  }
  .msg-main {
    max-width: 82%;
  }
  .welcome-tips {
    margin-left: 0;
  }
}
</style>
