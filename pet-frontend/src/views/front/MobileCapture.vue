<template>
  <div class="mc-page">
    <div class="mc-card">
      <!-- 头部 -->
      <div class="mc-head">
        <span class="mc-logo">
          <svg viewBox="0 0 48 48">
            <ellipse cx="24" cy="30" rx="11" ry="9" />
            <circle cx="12" cy="20" r="4.6" />
            <circle cx="21" cy="13.5" r="4.6" />
            <circle cx="27" cy="13.5" r="4.6" />
            <circle cx="36" cy="20" r="4.6" />
          </svg>
        </span>
        <h2>手机拍摄</h2>
        <p v-if="step === 'shoot'">拍摄宠物照片，将自动传输到电脑端识别</p>
        <p v-else-if="step === 'preview'">确认照片清晰后点击上传</p>
      </div>

      <!-- 拍摄入口 -->
      <template v-if="step === 'shoot'">
        <div class="mc-shoot" @click="openCamera">
          <span class="mc-cam-ic">
            <svg viewBox="0 0 24 24"><rect x="3" y="7" width="18" height="12.5" rx="2.5" fill="none" stroke="currentColor" stroke-width="1.8"/><path d="M8 7l1.3-2.4h5.4L16 7" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linejoin="round"/><circle cx="12" cy="13.3" r="3.4" fill="none" stroke="currentColor" stroke-width="1.8"/></svg>
          </span>
          <b>点击拍照</b>
          <small>请正对宠物，保持面部清晰、光线充足</small>
        </div>
      </template>

      <!-- 预览确认 -->
      <template v-else-if="step === 'preview'">
        <img class="mc-preview" :src="previewUrl" alt="照片预览" />
        <div class="mc-actions">
          <button class="mc-btn ghost" @click="openCamera">重新拍摄</button>
          <button class="mc-btn primary" :disabled="uploading" @click="submit">
            {{ uploading ? '上传中…' : '确认上传' }}
          </button>
        </div>
      </template>

      <!-- 完成 -->
      <template v-else-if="step === 'done'">
        <div class="mc-done">
          <span class="mc-ok-ic">
            <svg viewBox="0 0 24 24"><circle cx="12" cy="12" r="10" fill="none" stroke="currentColor" stroke-width="1.8"/><path d="M7.5 12.2l3 3 6-6.4" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
          </span>
          <b>传输成功</b>
          <p>照片已发送到电脑端<br />请回到电脑查看识别结果</p>
        </div>
      </template>

      <!-- 会话失效 -->
      <template v-else>
        <div class="mc-done">
          <span class="mc-err-ic">
            <svg viewBox="0 0 24 24"><circle cx="12" cy="12" r="10" fill="none" stroke="currentColor" stroke-width="1.8"/><path d="M12 7.5v5.5m0 3.4v.2" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>
          </span>
          <b>二维码已失效</b>
          <p>请回到电脑端重新生成二维码<br />再次扫码拍摄</p>
        </div>
      </template>

      <input
        ref="fileInput"
        type="file"
        accept="image/*"
        capture="environment"
        class="mc-input"
        @change="onFile"
      />
    </div>
    <p class="mc-brand">宠知AI · 智能宠物识别</p>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getUploadSession, uploadSessionPhoto } from '@/api/recognition'

const route = useRoute()
const sessionId = route.query.s || ''

const step = ref('shoot') // shoot | preview | done | invalid
const previewUrl = ref('')
const uploading = ref(false)
const fileInput = ref(null)
let file = null

onMounted(async () => {
  if (!sessionId) {
    step.value = 'invalid'
    return
  }
  // 校验会话有效性（失效则提示重新扫码）
  try {
    await getUploadSession(sessionId)
  } catch {
    step.value = 'invalid'
  }
})

function openCamera() {
  fileInput.value?.click()
}

function onFile(e) {
  const f = e.target.files?.[0]
  e.target.value = ''
  if (!f) return
  file = f
  previewUrl.value = URL.createObjectURL(f)
  step.value = 'preview'
}

async function submit() {
  if (!file || uploading.value) return
  uploading.value = true
  try {
    await uploadSessionPhoto(sessionId, file)
    step.value = 'done'
  } catch {
    // 拦截器已提示错误；会话失效时引导重新扫码
    step.value = 'invalid'
  } finally {
    uploading.value = false
  }
}
</script>

<style scoped>
.mc-page {
  min-height: 100vh;
  background: linear-gradient(160deg, #eef2ff 0%, #f5f0ff 55%, #eaf6ff 100%);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 24px 16px;
}
.mc-card {
  width: 100%;
  max-width: 420px;
  background: #fff;
  border-radius: 20px;
  box-shadow: 0 14px 36px rgba(90, 100, 180, 0.14);
  padding: 26px 22px 30px;
  text-align: center;
}
.mc-head h2 {
  margin: 10px 0 4px;
  font-size: 20px;
  color: #2c3e50;
}
.mc-head p {
  margin: 0 0 18px;
  font-size: 13px;
  color: #8a93a6;
}
.mc-logo {
  width: 46px;
  height: 46px;
  border-radius: 12px;
  background: linear-gradient(135deg, #6d7bf0, #6a5bd8);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 5px 12px rgba(106, 91, 216, 0.3);
}
.mc-logo svg {
  width: 26px;
  height: 26px;
  fill: #fff;
}
.mc-shoot {
  border: 2px dashed #c4cdf5;
  border-radius: 16px;
  background: #f7f9ff;
  padding: 44px 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  transition: border-color 0.2s, background 0.2s;
}
.mc-shoot:active {
  border-color: #6d7bf0;
  background: #eef1ff;
}
.mc-cam-ic {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: linear-gradient(135deg, #6d7bf0, #6a5bd8);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 4px;
}
.mc-cam-ic svg {
  width: 28px;
  height: 28px;
}
.mc-shoot b {
  font-size: 16px;
  color: #2c3e50;
}
.mc-shoot small {
  font-size: 12px;
  color: #8a93a6;
}
.mc-preview {
  width: 100%;
  max-height: 52vh;
  object-fit: contain;
  border-radius: 14px;
  background: #f4f6fb;
}
.mc-actions {
  display: flex;
  gap: 12px;
  margin-top: 16px;
}
.mc-btn {
  flex: 1;
  height: 46px;
  border: none;
  border-radius: 12px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
}
.mc-btn.primary {
  background: linear-gradient(90deg, #6d7bf0, #6a5bd8);
  color: #fff;
  box-shadow: 0 6px 14px rgba(106, 91, 216, 0.3);
}
.mc-btn.primary:disabled {
  opacity: 0.6;
}
.mc-btn.ghost {
  background: #f2f4fa;
  color: #5b6480;
}
.mc-done {
  padding: 30px 0 16px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}
.mc-done b {
  font-size: 17px;
  color: #2c3e50;
}
.mc-done p {
  margin: 0;
  font-size: 13px;
  color: #8a93a6;
  line-height: 1.8;
}
.mc-ok-ic {
  color: #12b886;
}
.mc-ok-ic svg {
  width: 54px;
  height: 54px;
}
.mc-err-ic {
  color: #f56c6c;
}
.mc-err-ic svg {
  width: 54px;
  height: 54px;
}
.mc-input {
  display: none;
}
.mc-brand {
  margin-top: 18px;
  font-size: 12px;
  color: #9aa2c0;
  letter-spacing: 1px;
}
</style>
