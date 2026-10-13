<template>
  <div class="auth-page">
    <!-- ============ 左侧品牌展示区（与登录页统一） ============ -->
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

    <!-- ============ 右侧注册区 ============ -->
    <main class="form-side">
      <svg class="form-wave" viewBox="0 0 600 300" preserveAspectRatio="none">
        <path d="M0,210 C150,150 330,250 600,170 L600,300 L0,300 Z" fill="#f1edff"/>
        <path d="M0,250 C170,205 360,292 600,225 L600,300 L0,300 Z" fill="#e9e3ff" opacity=".7"/>
      </svg>
      <span class="corner-paw" v-html="IC.paw"></span>
      <p class="corner-hand">遇见你的<br/>专属陪伴 <em>♡</em></p>

      <div class="login-card reg-card">
        <!-- 卡头 -->
        <div class="card-head">
          <span class="card-paw" v-html="IC.pawGrad"></span>
          <div class="card-titles">
            <h2>欢迎注册</h2>
            <p>开启您的宠物智能管理之旅</p>
          </div>
        </div>

        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          size="large"
          class="login-form"
          @keyup.enter="onSubmit"
        >
          <el-form-item prop="account">
            <el-input v-model="form.account" placeholder="请设置账号（3-20位字符）" maxlength="20" clearable>
              <template #prefix><span class="inp-ico" v-html="IC.user"></span></template>
            </el-input>
          </el-form-item>
          <el-form-item prop="nickname">
            <el-input v-model="form.nickname" placeholder="请输入昵称（选填，不填默认用账号）" maxlength="12" clearable>
              <template #prefix><span class="inp-ico" v-html="IC.edit"></span></template>
            </el-input>
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="form.password"
              :type="showPwd ? 'text' : 'password'"
              placeholder="请设置密码（6-30位字符）"
            >
              <template #prefix><span class="inp-ico" v-html="IC.lock"></span></template>
              <template #suffix>
                <span class="eye-ico" @click="showPwd = !showPwd" v-html="showPwd ? IC.eye : IC.eyeOff"></span>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item prop="confirm">
            <el-input
              v-model="form.confirm"
              :type="showPwd2 ? 'text' : 'password'"
              placeholder="请再次输入密码"
            >
              <template #prefix><span class="inp-ico" v-html="IC.lock"></span></template>
              <template #suffix>
                <span class="eye-ico" @click="showPwd2 = !showPwd2" v-html="showPwd2 ? IC.eye : IC.eyeOff"></span>
              </template>
            </el-input>
          </el-form-item>

          <button class="submit-btn" type="button" :disabled="loading" @click="onSubmit">
            <span>{{ loading ? '注册中…' : '注 册' }}</span>
            <i v-html="IC.arrow"></i>
          </button>
        </el-form>

        <p class="to-reg">已有账号？<router-link to="/login">前往登录</router-link></p>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { register } from '@/api/auth'

const router = useRouter()

/* ============ AI 配图（与登录页完全一致） ============ */
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
  edit: `<svg viewBox="0 0 24 24"><path d="M4 20h4.2L19.3 8.9a2.1 2.1 0 0 0-3-3L5.2 17 4 20Z" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linejoin="round"/><path d="m14.5 7.8 2.7 2.7" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/></svg>`,
  lock: `<svg viewBox="0 0 24 24"><rect x="5" y="10.5" width="14" height="9.5" rx="2.4" fill="none" stroke="currentColor" stroke-width="1.8"/><path d="M8 10.5V8a4 4 0 0 1 8 0v2.5" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/><circle cx="12" cy="15.2" r="1.3" fill="currentColor"/></svg>`,
  eye: `<svg viewBox="0 0 24 24"><path d="M2.5 12S6 5.8 12 5.8 21.5 12 21.5 12 18 18.2 12 18.2 2.5 12 2.5 12Z" fill="none" stroke="currentColor" stroke-width="1.8"/><circle cx="12" cy="12" r="3" fill="none" stroke="currentColor" stroke-width="1.8"/></svg>`,
  eyeOff: `<svg viewBox="0 0 24 24"><path d="M4 4l16 16M9.2 9.4A3 3 0 0 0 12 15c.9 0 1.7-.4 2.2-1.1M6.4 6.6C3.9 8.3 2.5 12 2.5 12S6 18.2 12 18.2c1.8 0 3.4-.6 4.6-1.5m2.4-2.5c.9-1.2 1.5-2.2 1.5-2.2S18 5.8 12 5.8c-.7 0-1.3.1-1.9.3" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/></svg>`,
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
const showPwd = ref(false)
const showPwd2 = ref(false)

const form = reactive({ account: '', nickname: '', password: '', confirm: '' })

const validateAccount = (rule, value, cb) => {
  if (!value) return cb(new Error('请输入账号'))
  if (value.length < 3 || value.length > 20) return cb(new Error('账号长度需为 3-20 位'))
  if (!/^[A-Za-z0-9_\u4e00-\u9fa5]+$/.test(value)) return cb(new Error('账号仅支持中英文、数字、下划线'))
  cb()
}
const validatePassword = (rule, value, cb) => {
  if (!value) return cb(new Error('请输入密码'))
  if (value.length < 6 || value.length > 30) return cb(new Error('密码长度需为 6-30 位'))
  if (form.confirm) formRef.value.validateField('confirm')
  cb()
}
const validateConfirm = (rule, value, cb) => {
  if (!value) return cb(new Error('请再次输入密码'))
  if (value !== form.password) return cb(new Error('两次输入的密码不一致'))
  cb()
}

const rules = {
  account: [{ validator: validateAccount, trigger: 'blur' }],
  password: [{ validator: validatePassword, trigger: 'blur' }],
  confirm: [{ validator: validateConfirm, trigger: 'blur' }]
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
    /* 字段顺序与 RegisterDTO 严格一致：account / password / nickname */
    await register({
      account: form.account.trim(),
      password: form.password,
      nickname: form.nickname.trim()
    })
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch {
    /* 接口错误提示由 request 响应拦截器统一弹出，输入保留在表单中 */
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
  padding: 32px 24px;
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
  padding: 34px 40px 30px;
}
.card-head {
  display: flex;
  align-items: center;
  gap: 14px;
  justify-content: center;
  margin-bottom: 22px;
}
.card-paw { width: 56px; height: 56px; flex: none; }
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

/* 表单元素（与登录页一致） */
.login-form :deep(.el-form-item) {
  margin-bottom: 17px;
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

/* 主按钮 */
.submit-btn {
  width: 100%;
  height: 54px;
  margin-top: 4px;
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

.to-reg {
  margin: 20px 0 0;
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
  .login-card { padding: 28px 22px 24px; border-radius: 18px; }
  .corner-hand, .corner-paw { display: none; }
}
</style>
