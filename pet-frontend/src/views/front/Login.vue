<template>
  <div class="auth-page">
    <!-- ============ 左侧品牌展示区 ============ -->
    <aside class="brand-side">
      <img class="brand-photo" :src="IMG.scene" alt=""/>
      <div class="brand-mask"></div>
      <span class="paw-deco pd-1" v-html="IC.paw"></span>
      <span class="paw-deco pd-2" v-html="IC.paw"></span>
      <span class="paw-deco pd-3" v-html="IC.paw"></span>

      <div class="brand-inner">
        <!-- 顶部 Logo -->
        <div class="brand-top">
          <span class="brand-logo" v-html="IC.paw"></span>
          <div class="brand-name">
            <b>宠知AI</b>
            <small>AI PET RECOGNITION</small>
          </div>
        </div>

        <!-- 主标题 -->
        <h1 class="brand-title">
          用<span class="grad">AI识别</span>宠物<br/>
          让爱更有科学
        </h1>
        <p class="brand-line">识别 · 档案 · 健康 · 资讯 · 记录</p>

        <!-- 功能列表 -->
        <ul class="feature-list">
          <li v-for="f in features" :key="f.title">
            <span class="fi-icon" :style="{ background: f.bg }" v-html="f.icon"></span>
            <div class="fi-text">
              <b>{{ f.title }}</b>
              <small>{{ f.desc }}</small>
            </div>
          </li>
        </ul>

        <!-- AI 识别浮卡 -->
        <div class="scan-chip">
          <span class="scan-ico" v-html="IC.scan"></span>
          AI识别中<span class="dot-loading">…</span>
        </div>
        <div class="ai-card">
          <img class="ai-dog" :src="IMG.dog" alt="金毛寻回犬"/>
          <div class="ai-main">
            <div class="ai-row1">
              <b>金毛寻回犬</b>
              <em>98.7%</em>
            </div>
            <div class="ai-row2">
              <span v-html="IC.petMini"></span>
              <i>年龄：2岁</i>
              <i>性别：雄性</i>
            </div>
            <div class="ai-row3">
              <span class="heart-ico" v-html="IC.heartSm"></span>
              健康：良好
            </div>
          </div>
        </div>

        <!-- 手写标注 -->
        <p class="brand-hand">每一只宠物<br/>都值得被温柔以待 <em>♡</em></p>
      </div>
    </aside>

    <!-- ============ 右侧登录区 ============ -->
    <main class="form-side">
      <svg class="form-wave" viewBox="0 0 600 300" preserveAspectRatio="none">
        <path d="M0,210 C150,150 330,250 600,170 L600,300 L0,300 Z" fill="#f1edff"/>
        <path d="M0,250 C170,205 360,292 600,225 L600,300 L0,300 Z" fill="#e9e3ff" opacity=".7"/>
      </svg>
      <span class="corner-paw" v-html="IC.paw"></span>
      <p class="corner-hand">与宠物相遇<br/>从这里开始 <em>♡</em></p>

      <div class="login-card">
        <!-- 卡头 -->
        <div class="card-head">
          <span class="card-paw" v-html="IC.pawGrad"></span>
          <div class="card-titles">
            <h2>欢迎登录</h2>
            <p>开启您的宠物智能管理之旅</p>
          </div>
        </div>

        <!-- 登录方式 Tab -->
        <div class="mode-tabs">
          <button type="button" :class="{ on: mode === 'pwd' }" @click="switchMode('pwd')">密码登录</button>
          <button type="button" :class="{ on: mode === 'sms' }" @click="switchMode('sms')">手机验证码登录</button>
        </div>

        <el-form
          ref="formRef"
          :model="form"
          :rules="mode === 'pwd' ? pwdRules : smsRules"
          size="large"
          class="login-form"
          @keyup.enter="onSubmit"
        >
          <!-- 密码登录 -->
          <template v-if="mode === 'pwd'">
            <el-form-item prop="account">
              <el-input v-model="form.account" placeholder="请输入用户名/手机号" clearable>
                <template #prefix><span class="inp-ico" v-html="IC.user"></span></template>
              </el-input>
            </el-form-item>
            <el-form-item prop="password">
              <el-input
                v-model="form.password"
                :type="showPwd ? 'text' : 'password'"
                placeholder="请输入密码"
              >
                <template #prefix><span class="inp-ico" v-html="IC.lock"></span></template>
                <template #suffix>
                  <span class="eye-ico" @click="showPwd = !showPwd" v-html="showPwd ? IC.eye : IC.eyeOff"></span>
                </template>
              </el-input>
            </el-form-item>
            <div class="form-extra">
              <el-checkbox v-model="remember">记住账号</el-checkbox>
              <a class="forget" @click="onForget">忘记密码？</a>
            </div>
          </template>

          <!-- 验证码登录 -->
          <template v-else>
            <el-form-item prop="phone">
              <el-input v-model="form.phone" placeholder="请输入手机号" maxlength="11" clearable>
                <template #prefix><span class="inp-ico" v-html="IC.phone"></span></template>
              </el-input>
            </el-form-item>
            <el-form-item prop="code">
              <el-input v-model="form.code" placeholder="请输入验证码" maxlength="6">
                <template #prefix><span class="inp-ico" v-html="IC.shield"></span></template>
                <template #append>
                  <button class="code-btn" type="button" :disabled="countdown > 0" @click="sendCode">
                    {{ countdown > 0 ? `${countdown}s 后重发` : '获取验证码' }}
                  </button>
                </template>
              </el-input>
            </el-form-item>
            <p class="sms-note">未注册的手机号验证后将自动创建账号</p>
          </template>

          <button class="submit-btn" type="button" :disabled="loading" @click="onSubmit">
            <span>{{ loading ? '登录中…' : (mode === 'pwd' ? '登 录' : '登 录 / 注 册') }}</span>
            <i v-html="IC.arrow"></i>
          </button>
        </el-form>

        <!-- 快速登录 -->
        <div class="quick">
          <span class="q-line"></span>
          <span class="q-text">或&nbsp;快速登录</span>
          <span class="q-line"></span>
        </div>
        <div class="third-list">
          <button type="button" class="third wx" @click="onThird('微信')" aria-label="微信登录">
            <svg viewBox="0 0 24 24"><path fill="#fff" d="M9.4 4C5.6 4 2.6 6.5 2.6 9.6c0 1.8 1 3.4 2.6 4.5l-.5 1.7 2-1c.5.2 1.1.2 1.7.2h.3c-.1-.4-.2-.9-.2-1.3 0-2.7 2.6-4.8 5.7-4.8h.3C14 6.3 11.9 4 9.4 4Zm-2.3 4c-.5 0-.9-.4-.9-.9s.4-.9.9-.9.9.4.9.9-.4.9-.9.9Zm4.7 0c-.5 0-.9-.4-.9-.9s.4-.9.9-.9.9.4.9.9-.4.9-.9.9Zm2.1 1.6c-2.8 0-5 1.8-5 4.1 0 2.3 2.2 4.1 5 4.1.6 0 1.1-.1 1.6-.2l1.7.9-.4-1.4c1.3-.9 2.1-2.2 2.1-3.6 0-2.2-2.3-3.9-5-3.9Zm-1.7 3c.4 0 .8.3.8.8s-.4.8-.8.8-.8-.3-.8-.8.4-.8.8-.8Zm3.5 0c.4 0 .8.3.8.8s-.4.8-.8.8-.8-.3-.8-.8.4-.8.8-.8Z"/></svg>
          </button>
          <button type="button" class="third qq" @click="onThird('QQ')" aria-label="QQ登录">
            <svg viewBox="0 0 24 24"><path fill="#fff" d="M12 2.5c-2.3 0-3.9 1.8-3.9 4.3 0 .4 0 .8.1 1.1-1 .9-2.1 2.4-2.4 4.3-.2 1.2-.5 2-.9 1.9-.5-.1-.2-1.4-.5-2.3-.2-.6-.9-.8-1-.1-.2 1.6.5 3.2 1.1 3.9.1.9.5 1.7 1 2.2-.5.2-1 .7-1 1.1 0 .4.8.3 1.5.1.5.5 1.2.8 1.9.8-.2.3-.3.6-.3.9 0 .9 1 1.3 2.3 1.3s2.3-.4 2.3-1.3c0-.3-.1-.6-.3-.9.7 0 1.4-.3 1.9-.8.7.2 1.5.3 1.5-.1 0-.4-.5-.9-1-1.1.5-.5.9-1.3 1-2.2.6-.7 1.3-2.3 1.1-3.9-.1-.7-.8-.5-1 .1-.3.9 0 2.2-.5 2.3-.4.1-.7-.7-.9-1.9-.3-1.9-1.4-3.4-2.4-4.3.1-.3.1-.7.1-1.1 0-2.5-1.6-4.3-3.9-4.3Z"/></svg>
          </button>
          <button type="button" class="third wb" @click="onThird('微博')" aria-label="微博登录">
            <svg viewBox="0 0 24 24"><path fill="#fff" d="M10.1 19.6c-3.6.4-6.7-1.2-7-3.7-.2-2.4 2.6-4.7 6.1-5.1 3.6-.4 6.7 1.2 7 3.7.3 2.5-2.5 4.7-6.1 5.1Zm.7-6.9c-1.9-.3-3.6.7-3.9 2.2-.2 1.6 1.2 3.1 3.1 3.4 1.9.3 3.6-.7 3.8-2.3.3-1.5-1.1-3-3-3.3Zm-.4 3.9c-.6.2-1.2-.1-1.3-.6-.1-.5.3-1.1.9-1.3.6-.2 1.2.1 1.3.6.1.5-.3 1-.9 1.3Zm1.3-1.5c-.2.1-.5 0-.6-.2-.1-.2 0-.5.2-.6.2-.1.5 0 .6.2.1.2 0 .5-.2.6Zm7.4-5.3c-.3-.1-.5-.2-.4-.5.3-.8.3-1.5 0-1.9-.5-.8-1.9-.8-3.5-.1 0 0-.5.2-.4-.2.2-.7.2-1.3-.1-1.6-.6-.7-2.7 0-4.6 1.6-1.4 1.2-2.3 2.4-2.3 3.4 0 2 2.5 3.1 5 3.1 3.2 0 5.4-1.9 5.4-3.4 0-.9-.7-1.4-1.3-1.6 1.1 0 1.8.3 2 .8.1.3-.1.5-.3.6-.3.1-.3.4 0 .6.6.2 1.3-.1 1.4-.9.1-.7-.4-1.5-1.3-1.9Zm-2.4-.7c-1.4.2-2.8.9-3.8 1.8-.8.7-1.2 1.5-.9 2 .1.3.4.5.8.4.7-.1 1-.7 1.2-1.3.3-.9 1.3-1.6 2.5-1.8.5-.1.8-.5.7-1-.1-.4-.3-.3-.5-.1Z"/></svg>
          </button>
        </div>

        <p class="to-reg">还没有账号？<router-link to="/register">立即注册</router-link></p>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { login } from '@/api/auth'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

/* ============ AI 配图（金毛形象跨页复用 Home.vue） ============ */
const t2i = (p, s) => 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=' + encodeURIComponent(p) + '&image_size=' + s
const IMG = {
  scene: t2i('Golden retriever dog and grey tabby white cat lying together on soft blue blanket, bright airy bedroom with big window and green plants, blue toy bone and purple ball, warm sunlight, professional pet photography', 'landscape_16_9'),
  dog: t2i('Happy golden retriever dog head portrait with tongue out smiling, soft cream studio background, professional pet photography, square avatar', 'square_hd')
}

/* ============ 内联 SVG 图标 ============ */
const pawPath = 'M12 13.2c-2.6 0-5 2-5 4.1 0 1.4 1.1 2.2 2.5 2.2 1 0 1.6-.5 2.5-.5s1.5.5 2.5.5c1.4 0 2.5-.8 2.5-2.2 0-2.1-2.4-4.1-5-4.1ZM7.2 11.6c1-.3 1.5-1.5 1.1-2.7-.4-1.2-1.5-2-2.5-1.7-1 .3-1.5 1.5-1.1 2.7.4 1.1 1.5 2 2.5 1.7Zm9.6 0c1 .3 2.1-.6 2.5-1.7.4-1.2-.1-2.4-1.1-2.7-1-.3-2.1.5-2.5 1.7-.4 1.2.1 2.4 1.1 2.7ZM9.6 8c.8-.4 1.1-1.5.6-2.5-.5-1-1.5-1.4-2.2-1-.8.4-1.1 1.5-.6 2.4.4.9 1.4 1.5 2.2 1.1Zm4.8 0c.8.4 1.8-.2 2.2-1.1.5-.9.2-2-.6-2.4-.7-.4-1.7 0-2.2 1-.5 1-.2 2.1.6 2.5Z'
const IC = {
  paw: `<svg viewBox="0 0 24 24"><path fill="#6d5ce0" d="${pawPath}"/></svg>`,
  pawGrad: `<svg viewBox="0 0 24 24"><defs><linearGradient id="pawG" x1="0" y1="0" x2="1" y2="1"><stop offset="0%" stop-color="#5b8cff"/><stop offset="100%" stop-color="#8b5cf6"/></linearGradient></defs><path fill="url(#pawG)" d="${pawPath}"/></svg>`,
  user: `<svg viewBox="0 0 24 24"><circle cx="12" cy="8" r="4" fill="none" stroke="currentColor" stroke-width="1.8"/><path d="M4.5 20c.8-3.4 3.8-5 7.5-5s6.7 1.6 7.5 5" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/></svg>`,
  lock: `<svg viewBox="0 0 24 24"><rect x="5" y="10.5" width="14" height="9.5" rx="2.4" fill="none" stroke="currentColor" stroke-width="1.8"/><path d="M8 10.5V8a4 4 0 0 1 8 0v2.5" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/><circle cx="12" cy="15.2" r="1.3" fill="currentColor"/></svg>`,
  eye: `<svg viewBox="0 0 24 24"><path d="M2.5 12S6 5.8 12 5.8 21.5 12 21.5 12 18 18.2 12 18.2 2.5 12 2.5 12Z" fill="none" stroke="currentColor" stroke-width="1.8"/><circle cx="12" cy="12" r="3" fill="none" stroke="currentColor" stroke-width="1.8"/></svg>`,
  eyeOff: `<svg viewBox="0 0 24 24"><path d="M4 4l16 16M9.2 9.4A3 3 0 0 0 12 15c.9 0 1.7-.4 2.2-1.1M6.4 6.6C3.9 8.3 2.5 12 2.5 12S6 18.2 12 18.2c1.8 0 3.4-.6 4.6-1.5m2.4-2.5c.9-1.2 1.5-2.2 1.5-2.2S18 5.8 12 5.8c-.7 0-1.3.1-1.9.3" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/></svg>`,
  phone: `<svg viewBox="0 0 24 24"><rect x="7" y="2.8" width="10" height="18.4" rx="2.6" fill="none" stroke="currentColor" stroke-width="1.8"/><circle cx="12" cy="18" r="1" fill="currentColor"/></svg>`,
  shield: `<svg viewBox="0 0 24 24"><path d="M12 3l7 2.5v5.8c0 4.6-3 7.7-7 9.2-4-1.5-7-4.6-7-9.2V5.5L12 3Z" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linejoin="round"/><path d="m9 11.6 2 2 4-4.2" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"/></svg>`,
  arrow: `<svg viewBox="0 0 24 24"><path d="M5 12h13m0 0-5.5-5.5M18 12l-5.5 5.5" fill="none" stroke="#fff" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"/></svg>`,
  scan: `<svg viewBox="0 0 24 24"><path d="M4 8V5.5A1.5 1.5 0 0 1 5.5 4H8M16 4h2.5A1.5 1.5 0 0 1 20 5.5V8M20 16v2.5a1.5 1.5 0 0 1-1.5 1.5H16M8 20H5.5A1.5 1.5 0 0 1 4 18.5V16" fill="none" stroke="currentColor" stroke-width="1.9" stroke-linecap="round"/><circle cx="12" cy="12" r="2.6" fill="none" stroke="currentColor" stroke-width="1.7"/></svg>`,
  petMini: `<svg viewBox="0 0 24 24"><path fill="#8b93b8" d="${pawPath}"/></svg>`,
  heartSm: `<svg viewBox="0 0 24 24"><path d="M12 20.5s-7-4.6-9-8.6C1.6 9 3 5.8 6.2 5.8c1.9 0 3.2 1.1 3.8 2 .6-.9 1.9-2 3.8-2 3.2 0 4.6 3.2 3.2 6.1-2 4-9 8.6-9 8.6Z" transform="scale(.86) translate(2 2)"/></svg>`
}
const grad = (a, b) => `linear-gradient(135deg, ${a}, ${b})`
const features = [
  { title: 'AI智能识别', desc: '识别品种、年龄、性别、特征', bg: grad('#7c8df6', '#6d5ce0'), icon: `<svg viewBox="0 0 24 24"><path d="M4 8.5A2.5 2.5 0 0 1 6.5 6h.8l.9-1.4A2 2 0 0 1 9.9 3.8h4.2a2 2 0 0 1 1.7.8L16.7 6h.8A2.5 2.5 0 0 1 20 8.5v7A2.5 2.5 0 0 1 17.5 18h-11A2.5 2.5 0 0 1 4 15.5v-7Z" fill="#fff"/><circle cx="12" cy="12" r="3.4" fill="none" stroke="#6d5ce0" stroke-width="1.8"/></svg>` },
  { title: '电子档案管理', desc: '建立专属宠物档案', bg: grad('#5bb7f5', '#3f8ef0'), icon: `<svg viewBox="0 0 24 24"><path d="M6 3.5h8L18.5 8v11.5a1 1 0 0 1-1 1H6a1 1 0 0 1-1-1V4.5a1 1 0 0 1 1-1Z" fill="#fff"/><path d="M14 3.5V8h4.5" fill="none" stroke="#3f8ef0" stroke-width="1.6" stroke-linejoin="round"/><path d="M8 12h7M8 15.5h7" stroke="#3f8ef0" stroke-width="1.7" stroke-linecap="round"/></svg>` },
  { title: '健康管理提醒', desc: '疫苗、驱虫、体检等贴心提醒', bg: grad('#ff7eb0', '#f0528f'), icon: `<svg viewBox="0 0 24 24"><path d="M12 20.5s-7-4.6-9-8.6C1.6 9 3 5.8 6.2 5.8c1.9 0 3.2 1.1 3.8 2 .6-.9 1.9-2 3.8-2 3.2 0 4.6 3.2 3.2 6.1-2 4-9 8.6-9 8.6Z" fill="#fff"/><path d="M4 12h3l1.4-3 2.6 6 1.8-3H19" fill="none" stroke="#f0528f" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"/></svg>` },
  { title: '宠物知识科普', desc: '科学养宠，快乐相伴', bg: grad('#b08bff', '#8b5cf6'), icon: `<svg viewBox="0 0 24 24"><path d="M11.5 5.2c-2-1.2-4.5-1.5-7-.7v13c2.5-.8 5-.5 7 .7m0-13c2-1.2 4.5-1.5 7-.7v13c-2.5-.8-5-.5-7 .7m0-13v13" fill="none" stroke="#fff" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"/></svg>` }
]

/* ============ 表单状态 ============ */
const formRef = ref(null)
const loading = ref(false)
const mode = ref('pwd')
const showPwd = ref(false)
const remember = ref(false)
const countdown = ref(0)
let timer = null

const form = reactive({ account: '', password: '', phone: '', code: '' })

const pwdRules = {
  account: [{ required: true, message: '请输入用户名/手机号', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 30, message: '密码长度为 6-30 位', trigger: 'blur' }
  ]
}
const validatePhone = (rule, value, cb) => {
  if (!value) return cb(new Error('请输入手机号'))
  if (!/^1[3-9]\d{9}$/.test(value)) return cb(new Error('请输入正确的手机号'))
  cb()
}
const smsRules = {
  phone: [{ validator: validatePhone, trigger: 'blur' }],
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { len: 6, message: '验证码为 6 位数字', trigger: 'blur' }
  ]
}

onMounted(() => {
  const saved = localStorage.getItem('rememberAccount')
  if (saved) {
    form.account = saved
    remember.value = true
  }
})
onBeforeUnmount(() => timer && clearInterval(timer))

function switchMode(m) {
  mode.value = m
  formRef.value && formRef.value.clearValidate()
}

function sendCode() {
  if (!/^1[3-9]\d{9}$/.test(form.phone)) {
    ElMessage.warning('请先输入正确的手机号')
    return
  }
  ElMessage.success('演示验证码已发送：123456')
  countdown.value = 60
  timer = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) clearInterval(timer)
  }, 1000)
}

function onForget() {
  ElMessageBox.prompt('请输入注册时的手机号，我们将向您发送验证码', '找回密码', {
    confirmButtonText: '下一步',
    cancelButtonText: '取消',
    inputPattern: /^1[3-9]\d{9}$/,
    inputErrorMessage: '请输入正确的手机号'
  }).then(({ value }) => {
    ElMessage.success(`演示验证码已发送至 ${value}：123456`)
    ElMessageBox.prompt('请输入收到的验证码', '验证身份', {
      confirmButtonText: '验证',
      cancelButtonText: '取消',
      inputPattern: /^\d{6}$/,
      inputErrorMessage: '验证码为6位数字'
    }).then(() => {
      ElMessageBox.prompt('请输入新密码（6-30位）', '重置密码', {
        confirmButtonText: '确认重置',
        cancelButtonText: '取消',
        inputType: 'password',
        inputPattern: /^.{6,30}$/,
        inputErrorMessage: '密码长度为6-30位'
      }).then(() => {
        ElMessage.success('密码重置成功，请使用新密码登录')
      }).catch(() => {})
    }).catch(() => {})
  }).catch(() => {})
}

function onThird(name) {
  ElMessage.info(`${name}登录需配置第三方OAuth接口，演示环境暂未开通`)
}

async function onSubmit() {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
  } catch {
    return
  }
  loading.value = true
  try {
    if (mode.value === 'sms') {
      // 演示模式：验证码 123456 即可登录
      if (form.code !== '123456') {
        ElMessage.error('验证码错误，演示验证码为 123456')
        return
      }
      const res = await login({ account: form.phone.trim(), password: '123456' })
      userStore.setLogin(res.data)
      ElMessage.success('登录成功')
      router.push('/home')
      return
    }
    const res = await login({ account: form.account.trim(), password: form.password })
    userStore.setLogin(res.data)
    if (remember.value) {
      localStorage.setItem('rememberAccount', form.account.trim())
    } else {
      localStorage.removeItem('rememberAccount')
    }
    ElMessage.success('登录成功')
    router.push('/home')
  } catch {
    /* 接口错误提示由 request 响应拦截器统一弹出 */
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-page {
  min-height: 100vh;
  display: grid;
  grid-template-columns: minmax(0, 1.18fr) minmax(420px, .82fr);
  background: #fff;
}

/* ================= 左侧品牌区 ================= */
.brand-side {
  position: relative;
  overflow: hidden;
  min-height: 100vh;
  display: flex;
}
.brand-photo {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  object-position: center;
}
.brand-mask {
  position: absolute;
  inset: 0;
  background:
    linear-gradient(95deg, rgba(232,239,255,.94) 0%, rgba(225,235,255,.72) 32%, rgba(210,226,255,.25) 62%, rgba(199,219,255,.08) 100%),
    linear-gradient(0deg, rgba(196,213,251,.55) 0%, rgba(196,213,251,0) 38%);
}
.paw-deco {
  position: absolute;
  z-index: 1;
  opacity: .14;
}
.paw-deco svg { width: 100%; height: 100%; display: block; }
.pd-1 { width: 56px; height: 56px; top: 12%; right: 30%; }
.pd-2 { width: 34px; height: 34px; top: 42%; left: 31%; opacity: .1; }
.pd-3 { width: 42px; height: 42px; bottom: 10%; left: 40%; opacity: .1; }

.brand-inner {
  position: relative;
  z-index: 2;
  width: 100%;
  padding: clamp(24px, 3.2vw, 46px);
  display: flex;
  flex-direction: column;
}
.brand-top {
  display: flex;
  align-items: center;
  gap: 12px;
}
.brand-logo {
  width: 52px;
  height: 52px;
  flex: none;
  filter: drop-shadow(0 6px 14px rgba(109, 92, 224, .3));
}
.brand-logo svg { width: 100%; height: 100%; }
.brand-name b {
  display: block;
  font-size: 23px;
  font-weight: 800;
  color: #23306b;
  letter-spacing: 1px;
}
.brand-name small {
  display: block;
  font-size: 11px;
  letter-spacing: 3px;
  color: #6d7db5;
  margin-top: 2px;
}

.brand-title {
  margin: clamp(40px, 6vh, 72px) 0 0;
  font-size: clamp(34px, 3.5vw, 52px);
  line-height: 1.22;
  font-weight: 900;
  color: #27305c;
  letter-spacing: 1px;
}
.brand-title .grad {
  background: linear-gradient(90deg, #6d5ce0, #9b5cf6);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}
.brand-line {
  margin-top: 18px;
  font-size: clamp(14px, 1.1vw, 17px);
  color: #5c6a9e;
  letter-spacing: 4px;
  font-weight: 500;
}

.feature-list {
  margin: clamp(26px, 4vh, 44px) 0 0;
  padding: 0;
  list-style: none;
  display: flex;
  flex-direction: column;
  gap: 16px;
  max-width: 380px;
}
.feature-list li {
  display: flex;
  align-items: center;
  gap: 14px;
}
.fi-icon {
  width: 46px;
  height: 46px;
  flex: none;
  border-radius: 50%;
  display: grid;
  place-items: center;
  box-shadow: 0 8px 18px rgba(109, 92, 224, .28);
}
.fi-icon svg { width: 24px; height: 24px; }
.fi-text b {
  display: block;
  font-size: 16px;
  color: #2b3768;
  font-weight: 700;
}
.fi-text small {
  display: block;
  margin-top: 3px;
  font-size: 12.5px;
  color: #7380ad;
}

/* AI 识别浮卡 */
.scan-chip {
  position: absolute;
  z-index: 3;
  top: clamp(120px, 19%, 190px);
  right: clamp(30px, 22%, 280px);
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 18px;
  border-radius: 12px;
  background: rgba(255, 255, 255, .92);
  color: #5b63f0;
  font-weight: 700;
  font-size: 15px;
  box-shadow: 0 12px 30px rgba(90, 110, 190, .18);
  backdrop-filter: blur(6px);
}
.scan-ico { width: 22px; height: 22px; }
.dot-loading { animation: blink 1.2s infinite; }
@keyframes blink { 0%, 100% { opacity: .3; } 50% { opacity: 1; } }

.ai-card {
  position: absolute;
  z-index: 3;
  top: clamp(176px, 26%, 250px);
  right: clamp(20px, 4vw, 56px);
  width: 300px;
  display: flex;
  gap: 12px;
  padding: 14px 16px;
  border-radius: 16px;
  background: rgba(255, 255, 255, .95);
  box-shadow: 0 18px 44px rgba(90, 110, 190, .22);
}
.ai-dog {
  width: 66px;
  height: 66px;
  border-radius: 12px;
  object-fit: cover;
  flex: none;
}
.ai-main { min-width: 0; flex: 1; }
.ai-row1 {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
}
.ai-row1 b { font-size: 17px; color: #2b3768; }
.ai-row1 em { font-style: normal; font-size: 15px; font-weight: 800; color: #6d5ce0; }
.ai-row2 {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 8px;
  color: #7c87ad;
  font-size: 12px;
}
.ai-row2 svg { width: 15px; height: 15px; flex: none; }
.ai-row2 i { font-style: normal; }
.ai-row3 {
  display: flex;
  align-items: center;
  gap: 5px;
  margin-top: 7px;
  font-size: 12.5px;
  color: #5c6a9e;
}
.heart-ico {
  width: 15px;
  height: 15px;
  color: #f0528f;
}
.heart-ico svg { width: 100%; height: 100%; }

.brand-hand {
  position: absolute;
  z-index: 2;
  left: clamp(28px, 4vw, 60px);
  bottom: clamp(24px, 5vh, 52px);
  margin: 0;
  font-family: 'Comic Sans MS', 'STKaiti', cursive;
  font-size: clamp(17px, 1.4vw, 22px);
  line-height: 1.5;
  color: #5a6bc4;
  transform: rotate(-7deg);
  font-weight: 700;
}
.brand-hand em { font-style: normal; color: #f0528f; }

/* ================= 右侧表单区 ================= */
.form-side {
  position: relative;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 24px;
  overflow-y: auto;
  background: #fbfbff;
}
.form-wave {
  position: absolute;
  right: 0;
  bottom: 0;
  width: 100%;
  height: 46%;
  pointer-events: none;
}
.corner-paw {
  position: absolute;
  top: 26px;
  right: 60px;
  width: 90px;
  opacity: .07;
}
.corner-paw svg { width: 100%; height: 100%; }
.corner-hand {
  position: absolute;
  top: 30px;
  right: 30px;
  margin: 0;
  text-align: right;
  font-family: 'Comic Sans MS', 'STKaiti', cursive;
  font-weight: 700;
  font-size: 17px;
  line-height: 1.45;
  color: #6d78c8;
  transform: rotate(-6deg);
}
.corner-hand em { font-style: normal; color: #f0528f; }

.login-card {
  position: relative;
  z-index: 2;
  width: 100%;
  max-width: 430px;
  background: #fff;
  border: 1px solid #eef0fa;
  border-radius: 22px;
  box-shadow: 0 24px 60px rgba(99, 108, 190, .12);
  padding: 38px 40px 32px;
}
.card-head {
  display: flex;
  align-items: center;
  gap: 14px;
  justify-content: center;
}
.card-paw { width: 58px; height: 58px; flex: none; }
.card-paw svg { width: 100%; height: 100%; }
.card-titles h2 {
  margin: 0;
  font-size: 27px;
  font-weight: 800;
  color: #232c58;
}
.card-titles p {
  margin: 5px 0 0;
  font-size: 13.5px;
  color: #98a1c4;
}

/* 模式 Tab */
.mode-tabs {
  margin: 26px 0 20px;
  height: 48px;
  padding: 5px;
  display: flex;
  border-radius: 14px;
  background: #f2f4fa;
}
.mode-tabs button {
  flex: 1;
  border: none;
  cursor: pointer;
  border-radius: 10px;
  background: transparent;
  font-size: 15px;
  font-weight: 600;
  color: #8b94b8;
  transition: all .25s;
}
.mode-tabs button.on {
  background: linear-gradient(90deg, #5b73e8, #7c5cf0);
  color: #fff;
  box-shadow: 0 8px 18px rgba(109, 92, 224, .32);
}

/* 表单元素 */
.login-form :deep(.el-form-item) {
  margin-bottom: 18px;
}
.login-form :deep(.el-input__wrapper) {
  border-radius: 13px;
  padding: 2px 14px;
  box-shadow: 0 0 0 1.5px #e7eaf6 inset;
  background: #fbfbfe;
  height: 50px;
  transition: box-shadow .2s;
}
.login-form :deep(.el-input__wrapper.is-focus),
.login-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1.5px #8b8ef0 inset;
  background: #fff;
}
.login-form :deep(.el-input__inner) {
  font-size: 14.5px;
  color: #2b3360;
  height: 50px;
}
.login-form :deep(.el-input__inner::placeholder) { color: #b3bad6; }
.inp-ico {
  width: 20px;
  height: 20px;
  color: #9aa3c8;
  display: inline-flex;
}
.inp-ico svg { width: 100%; height: 100%; }
.eye-ico {
  width: 20px;
  height: 20px;
  color: #a6aecf;
  cursor: pointer;
}
.eye-ico svg { width: 100%; height: 100%; }
.login-form :deep(.el-input-group__append) {
  border-radius: 0 13px 13px 0;
  box-shadow: 0 0 0 1.5px #e7eaf6 inset;
  border: none;
  background: #f4f5fc;
  padding: 0 16px;
}
.code-btn {
  border: none;
  background: transparent;
  color: #6d5ce0;
  font-size: 13.5px;
  font-weight: 600;
  cursor: pointer;
  white-space: nowrap;
}
.code-btn:disabled { color: #b3bad6; cursor: not-allowed; }
.sms-note {
  margin: -8px 0 16px;
  font-size: 12px;
  color: #a6aecf;
}

.form-extra {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: -4px 0 20px;
}
.form-extra :deep(.el-checkbox__label) {
  font-size: 13.5px;
  color: #6d759a;
}
.forget {
  font-size: 13.5px;
  color: #6d5ce0;
  cursor: pointer;
  font-weight: 600;
}
.forget:hover { text-decoration: underline; }

/* 主按钮 */
.submit-btn {
  width: 100%;
  height: 54px;
  border: none;
  border-radius: 27px;
  cursor: pointer;
  background: linear-gradient(90deg, #4f7cff 0%, #6d5ce0 55%, #935cf6 100%);
  box-shadow: 0 14px 28px rgba(109, 92, 224, .38);
  color: #fff;
  font-size: 17px;
  font-weight: 700;
  letter-spacing: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 14px;
  transition: transform .2s, box-shadow .2s, opacity .2s;
}
.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 18px 34px rgba(109, 92, 224, .45);
}
.submit-btn:disabled { opacity: .75; cursor: wait; }
.submit-btn i {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  background: rgba(255, 255, 255, .22);
  display: grid;
  place-items: center;
  font-style: normal;
}
.submit-btn i svg { width: 17px; height: 17px; }

/* 快速登录 */
.quick {
  display: flex;
  align-items: center;
  gap: 12px;
  margin: 24px 0 18px;
}
.q-line { flex: 1; height: 1px; background: #e8ebf7; }
.q-text { font-size: 12.5px; color: #a6aecf; white-space: nowrap; }
.third-list {
  display: flex;
  justify-content: center;
  gap: 26px;
}
.third {
  width: 46px;
  height: 46px;
  border-radius: 50%;
  border: none;
  cursor: pointer;
  display: grid;
  place-items: center;
  padding: 0;
  transition: transform .2s, box-shadow .2s;
}
.third:hover { transform: translateY(-3px); }
.third svg { width: 26px; height: 26px; }
.third.wx { background: #22c55e; box-shadow: 0 8px 18px rgba(34, 197, 94, .35); }
.third.qq { background: #36a3f5; box-shadow: 0 8px 18px rgba(54, 163, 245, .35); }
.third.wb { background: #f04b5e; box-shadow: 0 8px 18px rgba(240, 75, 94, .35); }

.to-reg {
  margin: 24px 0 0;
  text-align: center;
  font-size: 14px;
  color: #98a1c4;
}
.to-reg a {
  color: #6d5ce0;
  font-weight: 700;
  margin-left: 2px;
}
.to-reg a:hover { text-decoration: underline; }

/* ================= 响应式 ================= */
@media (max-width: 1020px) {
  .auth-page { grid-template-columns: 1fr; }
  .brand-side { display: none; }
  .form-side {
    background: linear-gradient(160deg, #eef2ff 0%, #f6f2ff 100%);
  }
}
@media (max-width: 520px) {
  .form-side { padding: 24px 14px; }
  .login-card { padding: 30px 22px 26px; border-radius: 18px; }
  .corner-hand, .corner-paw { display: none; }
}
</style>
