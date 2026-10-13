<template>
  <div class="profile-page">
    <div class="pf-layout">
      <!-- ============ 左栏菜单 ============ -->
      <aside class="p-side">
        <nav class="side-menu">
          <button
            v-for="m in menus"
            :key="m.key"
            type="button"
            class="menu-item"
            :class="{ on: m.key === 'profile' }"
            @click="onMenu(m)"
          >
            <span class="m-ic" v-html="m.icon"></span>
            <span class="m-name">{{ m.name }}</span>
          </button>
        </nav>

        <div class="side-art">
          <p class="art-note">记录每一份陪伴<br>让爱更有温度 <small>♡</small></p>
          <img :src="IMG.sideArt" alt="温馨插画">
        </div>
      </aside>

      <!-- ============ 主区 ============ -->
      <main class="p-main">
        <!-- Hero 统计横幅 -->
        <section class="p-hero" :style="{ backgroundImage: `url(${IMG.heroGreen})` }">
          <span class="hero-paw hp1" v-html="IC.pawFill"></span>
          <span class="hero-paw hp2" v-html="IC.pawFill"></span>
          <p class="hero-hand">每一个生命<br>都值得被温柔以待 <small>🐾</small></p>
          <div class="hero-stats">
            <div class="hs-item">
              <span class="hs-ic ic-paw" v-html="IC.pawFill"></span>
              <div class="hs-txt">
                <small>已识别宠物</small>
                <b>{{ stats.pets }} <i>只</i></b>
              </div>
            </div>
            <div class="hs-sep"></div>
            <div class="hs-item">
              <span class="hs-ic ic-doc" v-html="IC.docFill"></span>
              <div class="hs-txt">
                <small>累计识别次数</small>
                <b>{{ stats.recognizes }} <i>次</i></b>
              </div>
            </div>
            <div class="hs-sep"></div>
            <div class="hs-item">
              <span class="hs-ic ic-heart" v-html="IC.heartFill"></span>
              <div class="hs-txt">
                <small>健康提醒</small>
                <b>{{ stats.reminders }} <i>项</i></b>
              </div>
            </div>
          </div>
        </section>

        <div class="pf-content">
          <!-- ===== 左侧用户卡 ===== -->
          <section class="u-card">
            <div class="uc-head">
              <div class="uc-avatar">
                <img v-if="avatarUrl" :src="avatarUrl" alt="头像" @click="goTab('basic')">
                <span v-else class="uc-avatar-ph" @click="goTab('basic')">{{ avatarLetter }}</span>
                <button type="button" class="uc-cam" title="更换头像" @click="goTab('basic')" v-html="IC.cameraSm"></button>
              </div>
              <div class="uc-meta">
                <div class="uc-name-row">
                  <b class="uc-name">{{ user.nickname || user.account || '用户' }}</b>
                  <span class="uc-role">{{ roleName }}</span>
                </div>
                <div class="uc-id">ID：{{ user.id || '—' }}</div>
                <p class="uc-bio">{{ form.bio || '与宠物相伴，探索更美好的生活 🐾' }}</p>
              </div>
            </div>

            <div class="uc-stats">
              <div class="us-box">
                <small>已识别宠物</small>
                <b>{{ stats.pets }} <i>只</i></b>
              </div>
              <div class="us-box">
                <small>累计识别次数</small>
                <b>{{ stats.recognizes }} <i>次</i></b>
              </div>
              <div class="us-box">
                <small>我的收藏</small>
                <b>{{ stats.favorites }} <i>篇</i></b>
              </div>
            </div>

            <button type="button" class="ai-entry" @click="router.push('/chat')">
              <img :src="IMG.robot" alt="AI助手">
              <div class="ae-txt">
                <b>AI宠物助手</b>
                <small>有任何问题都可以问我哦~</small>
              </div>
              <span class="ae-arrow" v-html="IC.chevronRight"></span>
            </button>

            <div class="uc-login">
              <div class="ul-title"><span v-html="IC.clock"></span><b>最近登录</b></div>
              <div class="ul-info">
                <span class="ul-time"><i v-html="IC.clock"></i>{{ lastLogin.time }}</span>
                <span class="ul-plat">{{ lastLogin.platform }}</span>
              </div>
            </div>
          </section>

          <!-- ===== 右侧设置卡 ===== -->
          <section class="f-card">
            <header class="f-head">
              <div class="f-tabs">
                <button
                  v-for="t in tabs"
                  :key="t.key"
                  type="button"
                  class="f-tab"
                  :class="{ on: tab === t.key }"
                  @click="tab = t.key"
                >{{ t.name }}</button>
              </div>
              <button
                v-if="tab !== 'security'"
                type="button"
                class="save-btn"
                :disabled="saving"
                @click="onHeaderSave"
              >{{ saving ? '保存中…' : '保存修改' }}</button>
            </header>

            <!-- 基本信息 -->
            <div v-show="tab === 'basic'" class="f-panel">
              <h3 class="panel-title">基本信息</h3>
              <div class="basic-grid">
                <div class="avatar-col">
                  <div class="big-avatar">
                    <img v-if="avatarUrl" :src="avatarUrl" alt="头像">
                    <span v-else class="ba-ph">{{ avatarLetter }}</span>
                    <button
                      type="button"
                      class="ba-cam"
                      title="更换头像"
                      @click="triggerPick"
                      v-html="IC.cameraSm"
                    ></button>
                    <input ref="fileInput" type="file" accept="image/png,image/jpeg" hidden @change="onFileChange">
                  </div>
                  <button type="button" class="change-avatar-btn" @click="triggerPick">更换头像</button>
                  <p class="avatar-tip">支持 JPG / PNG，大小不超过 5MB</p>
                </div>

                <el-form
                  ref="profileFormRef"
                  :model="form"
                  :rules="profileRules"
                  label-position="right"
                  class="basic-form"
                >
                  <el-form-item label="用户名" prop="nickname" required>
                    <el-input
                      v-model="form.nickname"
                      placeholder="请输入用户名"
                      maxlength="20"
                      show-word-limit
                      clearable
                    />
                  </el-form-item>
                  <el-form-item label="昵称">
                    <el-input v-model="form.alias" placeholder="请输入昵称" maxlength="20" />
                  </el-form-item>
                  <el-form-item label="性别">
                    <el-radio-group v-model="form.gender">
                      <el-radio v-for="g in genders" :key="g" :value="g">{{ g }}</el-radio>
                    </el-radio-group>
                  </el-form-item>
                  <el-form-item label="出生日期">
                    <el-date-picker
                      v-model="form.birthday"
                      type="date"
                      placeholder="请选择出生日期"
                      value-format="YYYY-MM-DD"
                      style="width: 100%"
                    />
                  </el-form-item>
                  <el-form-item label="个人简介">
                    <el-input
                      v-model="form.bio"
                      type="textarea"
                      :rows="4"
                      maxlength="200"
                      show-word-limit
                      resize="none"
                      placeholder="介绍一下你和宠物的故事吧~"
                    />
                  </el-form-item>
                </el-form>
              </div>
            </div>

            <!-- 账号设置 -->
            <div v-show="tab === 'account'" class="f-panel">
              <h3 class="panel-title">账号信息</h3>
              <div class="info-rows">
                <div class="info-row"><span>登录账号</span><b>{{ user.account || '—' }}</b></div>
                <div class="info-row"><span>用户 ID</span><b>{{ user.id || '—' }}</b></div>
                <div class="info-row"><span>账号角色</span><b>{{ roleName }}</b></div>
                <div class="info-row"><span>注册时间</span><b>{{ formatDate(user.createTime) }}</b></div>
              </div>

              <h3 class="panel-title mt28">本机偏好设置</h3>
              <div class="pref-rows">
                <div class="pref-row">
                  <div class="pr-txt"><b>健康提醒推送</b><small>疫苗、驱虫、体检到期时在站内提醒我</small></div>
                  <el-switch v-model="prefs.healthPush" />
                </div>
                <div class="pref-row">
                  <div class="pr-txt"><b>识别记录自动保存</b><small>每次 AI 识别成功后自动存入我的记录</small></div>
                  <el-switch v-model="prefs.autoSaveRecord" />
                </div>
                <div class="pref-row">
                  <div class="pr-txt"><b>公开我的收藏</b><small>其他用户可在主页看到你的收藏内容</small></div>
                  <el-switch v-model="prefs.favPublic" />
                </div>
              </div>
              <p class="pref-note">偏好设置仅保存在当前设备的浏览器中。</p>
            </div>

            <!-- 安全设置 -->
            <div v-show="tab === 'security'" class="f-panel">
              <h3 class="panel-title">修改密码</h3>
              <el-form
                ref="pwdFormRef"
                :model="pwdForm"
                :rules="pwdRules"
                label-position="top"
                class="pwd-form"
              >
                <el-form-item label="原密码" prop="oldPassword">
                  <el-input v-model="pwdForm.oldPassword" type="password" show-password placeholder="请输入原密码" />
                </el-form-item>
                <el-form-item label="新密码" prop="newPassword">
                  <el-input v-model="pwdForm.newPassword" type="password" show-password placeholder="6-30 位新密码" />
                </el-form-item>
                <el-form-item label="确认新密码" prop="confirmPassword">
                  <el-input v-model="pwdForm.confirmPassword" type="password" show-password placeholder="再次输入新密码" />
                </el-form-item>
                <el-form-item>
                  <button type="button" class="save-btn" :disabled="pwdSaving" @click="submitPassword">
                    {{ pwdSaving ? '提交中…' : '确认修改' }}
                  </button>
                </el-form-item>
              </el-form>
              <el-alert
                type="warning"
                :closable="false"
                title="修改密码后将自动退出登录，请使用新密码重新登录"
                class="pwd-alert"
              />
            </div>
          </section>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getCurrentUser, updateProfile, changePassword } from '@/api/auth'
import { uploadImage, myRecords } from '@/api/recognition'
import { listMyArchives } from '@/api/archive'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

/* ==================== 图标（内联 SVG） ==================== */
const IC = {
  user: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>',
  paw: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="11" cy="4" r="2"/><circle cx="18" cy="8" r="2"/><circle cx="4" cy="8" r="2"/><path d="M19.5 17.5c.5-2.5-1.8-4.5-4.4-5.2C13 11.5 9 11.5 6.9 12.3c-2.6.7-4.9 2.7-4.4 5.2.4 2 2.8 3 5.3 2.5 1-.2 2.4-.3 4.2-.3s3.2.1 4.2.3c2.5.5 4.9-.5 3.3-2.5z"/></svg>',
  chart: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M3 3v18h18"/><rect x="7" y="11" width="3" height="6" rx="1"/><rect x="12" y="7" width="3" height="10" rx="1"/><rect x="17" y="13" width="3" height="4" rx="1"/></svg>',
  shieldHeart: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/><path d="M12 14.5s-3-1.9-3-4a1.8 1.8 0 0 1 3-1.3A1.8 1.8 0 0 1 15 10.5c0 2.1-3 4-3 4z"/></svg>',
  star: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/></svg>',
  doc: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M8 6h8M8 10h8M8 14h5"/><rect x="4" y="3" width="16" height="18" rx="2"/></svg>',
  gear: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="3"/><path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 1 1-2.83 2.83l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 1 1-4 0v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 1 1-2.83-2.83l.06-.06A1.65 1.65 0 0 0 4.6 15a1.65 1.65 0 0 0-1.51-1H3a2 2 0 1 1 0-4h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 1 1 2.83-2.83l.06.06A1.65 1.65 0 0 0 9 4.6h.09A1.65 1.65 0 0 0 10 3.09V3a2 2 0 1 1 4 0v.09A1.65 1.65 0 0 0 15 4.6a1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 1 1 2.83 2.83l-.06.06A1.65 1.65 0 0 0 19.4 9c.15.32.23.66.23 1h.09a2 2 0 1 1 0 4H19.5a1.65 1.65 0 0 0-1.51 1z"/></svg>',
  cameraSm: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M23 19a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h4l2-3h6l2 3h4a2 2 0 0 1 2 2z"/><circle cx="12" cy="13" r="4"/></svg>',
  clock: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="9"/><polyline points="12 7 12 12 15.5 14"/></svg>',
  chevronRight: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.4" stroke-linecap="round" stroke-linejoin="round"><polyline points="9 18 15 12 9 6"/></svg>',
  pawFill: '<svg viewBox="0 0 24 24" fill="currentColor"><circle cx="11" cy="4.5" r="2.1"/><circle cx="18" cy="8.5" r="2"/><circle cx="4" cy="8.5" r="2"/><path d="M19.8 17.6c.6-2.7-1.9-4.8-4.6-5.5-2.2-.6-6.4-.6-8.6.1-2.7.8-5.1 2.9-4.6 5.6.4 2.1 2.9 3.1 5.5 2.6 1-.2 2.3-.3 4-.3s3 .1 4 .3c2.6.5 5.1-.5 4.3-2.8z"/></svg>',
  docFill: '<svg viewBox="0 0 24 24" fill="currentColor"><path d="M6 2h9l5 5v13a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2zm2 8v2h8v-2H8zm0 4v2h8v-2H8zm0 4v2h5v-2H8z"/></svg>',
  heartFill: '<svg viewBox="0 0 24 24" fill="currentColor"><path d="M12 21s-7.5-4.7-10-9.3C.3 8.4 2 5 5.3 5c2 0 3.4 1.2 4.2 2.4h.1C10.4 6.2 11.8 5 13.8 5 17.1 5 19 8.3 17 11.7 15.2 15 12 21 12 21z"/></svg>'
}

const menus = [
  { key: 'profile', name: '个人中心', icon: IC.user },
  { key: 'pets', name: '我的宠物', icon: IC.paw },
  { key: 'growth', name: '成长记录', icon: IC.chart },
  { key: 'health', name: '健康提醒', icon: IC.shieldHeart },
  { key: 'fav', name: '我的收藏', icon: IC.star },
  { key: 'records', name: '我的记录', icon: IC.doc },
  { key: 'settings', name: '设置', icon: IC.gear }
]

const tabs = [
  { key: 'basic', name: '基本信息' },
  { key: 'account', name: '账号设置' },
  { key: 'security', name: '安全设置' }
]
const genders = ['男', '女', '保密']

/* ==================== AI 配图（跨页复用同一形象） ==================== */
const t2i = (p, s = 'square_hd') =>
  'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=' + encodeURIComponent(p) + '&image_size=' + s
const IMG = {
  heroGreen: t2i('Happy golden retriever dog and grey white tabby cat sitting together on fresh green grass, soft blue sky, distant green hills, bright morning sunlight, airy fresh atmosphere, professional pet photography', 'landscape_16_9'),
  dog: t2i('Happy golden retriever dog head portrait with tongue out smiling, soft cream studio background, professional pet photography, square avatar'),
  robot: t2i('Cute friendly white robot pet assistant mascot with glowing blue eyes and small antenna, rounded soft 3d style, light purple blue gradient background, professional illustration'),
  sideArt: t2i('cute simple illustration of a sitting puppy dog and a kitten cat side by side, white and soft blue line art on lavender pastel blue background, minimal gentle childrens book style')
}

/* ==================== 数据 ==================== */
const user = ref({
  id: 123456,
  account: 'testuser',
  nickname: 'testuser',
  avatar: '',
  role: 'user',
  createTime: '',
  _demo: true
})

const tab = ref(route.query.tab === 'account' || route.query.tab === 'security' ? route.query.tab : 'basic')

const form = reactive({
  nickname: 'testuser',
  alias: '爱宠达人',
  gender: '男',
  birthday: '2000-01-01',
  bio: '热爱宠物，喜欢和小动物相处，分享养宠经验和知识。',
  avatar: ''
})

const prefs = reactive({
  healthPush: true,
  autoSaveRecord: true,
  favPublic: false
})

const stats = reactive({ pets: 3, recognizes: 28, favorites: 5, reminders: 4 })
const saving = ref(false)
const pwdSaving = ref(false)
const fileInput = ref(null)
const profileFormRef = ref(null)
const pwdFormRef = ref(null)

const lastLogin = reactive({ time: '2026-09-06 14:32', platform: 'Windows · Chrome' })

const profileRules = {
  nickname: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '长度为 2-20 个字符', trigger: 'blur' }
  ]
}

const pwdForm = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })
const pwdRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 30, message: '密码长度需为 6-30 位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    {
      validator: (rule, value, cb) => {
        if (value !== pwdForm.newPassword) cb(new Error('两次输入的密码不一致'))
        else cb()
      },
      trigger: 'blur'
    }
  ]
}

const API_BASE = import.meta.env.VITE_API_BASE || '/api'
function resolveImg(url) {
  if (!url) return ''
  if (url.startsWith('http')) return url
  return API_BASE + url
}
function formatDate(t) {
  if (!t) return '—'
  return String(t).replace('T', ' ').substring(0, 10)
}
function fmtDateTime(d) {
  const p = (n) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${p(d.getMonth() + 1)}-${p(d.getDate())} ${p(d.getHours())}:${p(d.getMinutes())}`
}
function detectPlatform() {
  const ua = navigator.userAgent
  let os = 'Windows'
  if (/Mac OS X/.test(ua)) os = 'macOS'
  else if (/Android/.test(ua)) os = 'Android'
  else if (/iPhone|iPad/.test(ua)) os = 'iOS'
  else if (/Linux/.test(ua)) os = 'Linux'
  let br = 'Chrome'
  if (/Edg\//.test(ua)) br = 'Edge'
  else if (/Firefox\//.test(ua)) br = 'Firefox'
  else if (/Chrome\//.test(ua)) br = 'Chrome'
  else if (/Safari\//.test(ua)) br = 'Safari'
  return `${os} · ${br}`
}

const roleName = computed(() => (user.value.role === 'admin' ? '管理员' : '普通用户'))
const extrasKey = computed(() => `profile-extras-${user.value.id || 'demo'}`)
const prefsKey = computed(() => `profile-prefs-${user.value.id || 'demo'}`)
const avatarUrl = computed(() => {
  if (form.avatar) return resolveImg(form.avatar)
  if (user.value._demo) return IMG.dog
  return ''
})
const avatarLetter = computed(() =>
  String(user.value.nickname || user.value.account || 'U').charAt(0).toUpperCase()
)

/* ==================== 菜单跳转 ==================== */
function onMenu(m) {
  switch (m.key) {
    case 'profile':
      goTab('basic')
      break
    case 'pets':
      router.push('/archive')
      break
    case 'growth':
    case 'health':
    case 'fav':
    case 'records':
      router.push('/my-records')
      break
    case 'settings':
      goTab('account')
      break
  }
}
function goTab(t) {
  tab.value = t
  router.replace({ query: t === 'basic' ? {} : { tab: t } })
}

/* ==================== 头像上传 ==================== */
function triggerPick() {
  fileInput.value && fileInput.value.click()
}
function onFileChange(e) {
  const file = e.target.files && e.target.files[0]
  if (file) doUpload(file)
  e.target.value = ''
}
function validImage(file) {
  if (!['image/jpeg', 'image/jpg', 'image/png'].includes(file.type)) {
    ElMessage.error('仅支持 JPG / PNG 格式')
    return false
  }
  if (file.size > 5 * 1024 * 1024) {
    ElMessage.error('图片大小不能超过 5MB')
    return false
  }
  return true
}
async function doUpload(file) {
  if (!validImage(file)) return
  saving.value = true
  try {
    const res = await uploadImage(file)
    form.avatar = res.data.imageUrl
    ElMessage.success('头像上传成功，点击「保存修改」后生效')
  } catch (e) {
    // 拦截器已统一提示
  } finally {
    saving.value = false
  }
}

/* ==================== 保存 ==================== */
async function onHeaderSave() {
  if (tab.value === 'basic') await saveBasic()
  else if (tab.value === 'account') savePrefs()
}

async function saveBasic() {
  if (!profileFormRef.value) return
  await profileFormRef.value.validate(async (valid) => {
    if (!valid) return
    saving.value = true
    try {
      if (!user.value._demo) {
        // 后端当前支持昵称与头像；性别/生日/简介为前端扩展资料，本机持久化
        const res = await updateProfile({ nickname: form.nickname, avatar: form.avatar })
        user.value = { ...user.value, ...res.data }
        userStore.setLogin({
          ...userStore.userInfo,
          token: userStore.token,
          nickname: res.data.nickname,
          avatar: res.data.avatar
        })
      } else {
        user.value.nickname = form.nickname
        user.value.avatar = form.avatar
      }
      localStorage.setItem(extrasKey.value, JSON.stringify({
        gender: form.gender,
        birthday: form.birthday,
        bio: form.bio,
        alias: form.alias
      }))
      ElMessage.success('资料保存成功')
    } catch (e) {
      // 拦截器已统一提示
    } finally {
      saving.value = false
    }
  })
}

function savePrefs() {
  localStorage.setItem(prefsKey.value, JSON.stringify(prefs))
  ElMessage.success('偏好设置已保存到本机')
}

async function submitPassword() {
  if (!pwdFormRef.value) return
  await pwdFormRef.value.validate(async (valid) => {
    if (!valid) return
    pwdSaving.value = true
    try {
      await changePassword({ oldPassword: pwdForm.oldPassword, newPassword: pwdForm.newPassword })
      ElMessage.success('密码修改成功，请重新登录')
      setTimeout(async () => {
        await userStore.logout()
        router.push('/login')
      }, 1200)
    } catch (e) {
      // 拦截器已统一提示
    } finally {
      pwdSaving.value = false
    }
  })
}

/* ==================== 初始化加载（真实 API 渐进增强） ==================== */
function applyUser(u) {
  if (!u) return
  user.value = { ...user.value, ...u, _demo: false }
  form.nickname = u.nickname || u.account || ''
  form.avatar = u.avatar || ''
  const ex = localStorage.getItem(extrasKey.value)
  if (ex) {
    try {
      const data = JSON.parse(ex)
      form.gender = data.gender || '保密'
      form.birthday = data.birthday || ''
      form.bio = data.bio || ''
      form.alias = data.alias || ''
    } catch (e) { /* ignore */ }
  } else {
    form.gender = '保密'
    form.birthday = ''
    form.bio = ''
    form.alias = ''
  }
}

function applyStats(archives, recRes) {
  if (typeof archives === 'number') stats.pets = archives
  else if (Array.isArray(archives) && archives.length) stats.pets = archives.length
  if (recRes && typeof recRes.total === 'number') stats.recognizes = recRes.total
}

onMounted(async () => {
  // 最近登录：优先读登录时写入的时间戳，否则取当前时间作为基线
  const savedLogin = localStorage.getItem('lastLoginAt')
  if (savedLogin) lastLogin.time = savedLogin
  else {
    lastLogin.time = fmtDateTime(new Date())
    localStorage.setItem('lastLoginAt', lastLogin.time)
  }
  lastLogin.platform = detectPlatform()

  // 本机偏好
  const savedPrefs = localStorage.getItem(prefsKey.value)
  if (savedPrefs) {
    try {
      Object.assign(prefs, JSON.parse(savedPrefs))
    } catch (e) { /* ignore */ }
  }

  // 真实用户
  try {
    const res = await getCurrentUser()
    applyUser(res.data)
  } catch (e) {
    const st = userStore.userInfo
    if (st && st.id) applyUser(st)
    // 否则保留演示数据
  }

  // 真实统计
  const [arcRes, recResult] = await Promise.allSettled([listMyArchives(), myRecords({ page: 1, size: 1 })])
  const archives = arcRes.status === 'fulfilled' ? (arcRes.value.data || []) : null
  const recData = recResult.status === 'fulfilled' ? recResult.value.data : null
  applyStats(archives, recData)
})
</script>

<style scoped>
.profile-page {
  width: 100%;
  max-width: none;
  margin: 0;
  padding: 18px clamp(16px, 2vw, 32px) 40px;
}
.pf-layout {
  display: grid;
  grid-template-columns: 208px minmax(0, 1fr);
  gap: 18px;
  align-items: start;
}

/* ==================== 左栏 ==================== */
.p-side {
  position: sticky;
  top: 18px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.side-menu {
  background: #f4f6fd;
  border: none;
  border-radius: 18px;
  box-shadow: none;
  padding: 10px;
  display: flex;
  flex-direction: column;
  gap: 2px;
}
.menu-item {
  display: flex;
  align-items: center;
  gap: 12px;
  width: 100%;
  border: 0;
  background: transparent;
  padding: 11px 14px;
  border-radius: 12px;
  font-size: 15px;
  color: #4a5068;
  cursor: pointer;
  transition: all .2s;
  text-align: left;
}
.menu-item .m-ic {
  width: 21px;
  height: 21px;
  display: inline-flex;
  color: #8a90a6;
  transition: color .2s;
}
.menu-item .m-ic :deep(svg) {
  width: 100%;
  height: 100%;
}
.menu-item:hover {
  background: #f5f7ff;
  color: #5b63f0;
}
.menu-item:hover .m-ic {
  color: #5b63f0;
}
.menu-item.on {
  background: linear-gradient(120deg, #eef0ff, #e9ecff);
  color: #5260e0;
  font-weight: 600;
  box-shadow: inset 0 0 0 1px #e0e4ff;
}
.menu-item.on .m-ic {
  color: #6d5ce0;
}

.side-art {
  position: relative;
  border-radius: 18px;
  padding: 18px 12px 0;
  background: linear-gradient(180deg, #eef1ff 0%, #e9f3ff 100%);
  overflow: hidden;
  min-height: 188px;
}
.art-note {
  margin: 0 0 6px 4px;
  font-family: 'STKaiti', 'Comic Sans MS', cursive;
  font-size: 17px;
  line-height: 1.55;
  color: #7a83c8;
  transform: rotate(-5deg);
  transform-origin: left top;
  position: relative;
  z-index: 2;
}
.art-note small {
  color: #9db0f0;
}
.side-art img {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  width: 100%;
  height: 132px;
  object-fit: contain;
  object-position: bottom center;
}

/* ==================== Hero ==================== */
.p-hero {
  position: relative;
  display: flex;
  align-items: center;
  gap: 22px;
  height: 196px;
  padding: 0 26px 0 38px;
  border-radius: 22px;
  background-color: #dce9e2;
  background-size: cover;
  background-position: center 30%;
  overflow: hidden;
  box-shadow: 0 12px 30px rgba(102, 126, 200, .10);
}
/* 浅色遮罩：保证左侧手写体与右侧统计卡可读，背景图自然透出 */
.p-hero::before {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(90deg,
    rgba(240, 249, 243, .92) 0%,
    rgba(240, 249, 243, .72) 34%,
    rgba(240, 249, 243, .18) 62%,
    rgba(240, 249, 243, .35) 100%);
}
.hero-hand {
  position: relative;
  z-index: 3;
  flex: 0 0 auto;
  white-space: nowrap;
  margin: 0 auto 0 0;
  font-family: 'STKaiti', 'Comic Sans MS', cursive;
  font-size: 27px;
  line-height: 1.5;
  font-weight: 600;
  letter-spacing: 2px;
  color: #5f6fd4;
  transform: rotate(-5deg);
  text-shadow: 0 2px 10px rgba(255, 255, 255, .8);
}
.hero-hand small {
  font-size: 20px;
}
.hero-paw {
  position: absolute;
  color: #ffffff;
  opacity: .55;
  z-index: 0;
}
.hero-paw :deep(svg) {
  display: block;
}
.hp1 {
  width: 40px;
  height: 40px;
  left: 30px;
  bottom: 22px;
  transform: rotate(-18deg);
}
.hp2 {
  width: 26px;
  height: 26px;
  left: 250px;
  top: 26px;
  opacity: .4;
  transform: rotate(14deg);
}
.hero-stats {
  position: relative;
  z-index: 4;
  flex: 0 0 auto;
  display: flex;
  align-items: center;
  gap: 14px;
  background: rgba(255, 255, 255, .95);
  border: 1px solid rgba(255, 255, 255, .9);
  border-radius: 18px;
  padding: 13px 16px;
  box-shadow: 0 14px 32px rgba(94, 110, 190, .16);
}
.hs-item {
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 0;
  flex: 0 0 auto;
}
.hs-ic {
  width: 42px;
  height: 42px;
  border-radius: 50%;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}
.hs-ic :deep(svg) {
  width: 21px;
  height: 21px;
}
.ic-paw {
  background: linear-gradient(135deg, #8a86f0, #6d5ce0);
  box-shadow: 0 6px 14px rgba(109, 92, 224, .3);
}
.ic-doc {
  background: linear-gradient(135deg, #7fb4f5, #5b93e8);
  box-shadow: 0 6px 14px rgba(91, 147, 232, .3);
}
.ic-heart {
  background: linear-gradient(135deg, #ff9bb0, #f56b8d);
  box-shadow: 0 6px 14px rgba(245, 107, 141, .3);
}
.hs-txt small {
  display: block;
  font-size: 12.5px;
  color: #8a90a6;
  margin-bottom: 2px;
  white-space: nowrap;
}
.hs-txt b {
  font-size: 22px;
  color: #2f3550;
  font-weight: 800;
  line-height: 1.1;
}
.hs-txt b i {
  font-style: normal;
  font-size: 13px;
  color: #8a90a6;
  font-weight: 500;
  margin-left: 2px;
}
.hs-sep {
  width: 1px;
  height: 30px;
  background: #eceffa;
}

/* ==================== 内容两栏 ==================== */
.pf-content {
  display: grid;
  grid-template-columns: 300px minmax(0, 1fr);
  gap: 20px;
  margin-top: 18px;
  align-items: start;
}

/* ---------- 用户卡 ---------- */
.u-card {
  background: #fff;
  border: 1px solid #edf0f8;
  border-radius: 18px;
  box-shadow: 0 10px 28px rgba(94, 110, 190, .07);
  padding: 24px 22px;
}
.uc-head {
  display: flex;
  gap: 16px;
}
.uc-avatar {
  position: relative;
  flex: 0 0 auto;
  width: 86px;
  height: 86px;
}
.uc-avatar img,
.uc-avatar-ph {
  width: 86px;
  height: 86px;
  border-radius: 50%;
  object-fit: cover;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  border: 3px solid #fff;
  box-shadow: 0 6px 16px rgba(94, 110, 190, .18);
  background: linear-gradient(135deg, #aebaf2, #8b86e8);
  color: #fff;
  font-size: 30px;
  font-weight: 700;
}
.uc-cam {
  position: absolute;
  right: -2px;
  bottom: -2px;
  width: 28px;
  height: 28px;
  border-radius: 50%;
  border: 2px solid #fff;
  background: linear-gradient(135deg, #7c8df6, #6d5ce0);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  padding: 0;
  box-shadow: 0 4px 10px rgba(109, 92, 224, .35);
}
.uc-cam :deep(svg) {
  width: 14px;
  height: 14px;
}
.uc-meta {
  min-width: 0;
  flex: 1;
  padding-top: 4px;
}
.uc-name-row {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}
.uc-name {
  font-size: 21px;
  font-weight: 800;
  color: #2c3150;
}
.uc-role {
  font-size: 12px;
  color: #5b63f0;
  background: #eef0ff;
  border-radius: 8px;
  padding: 3px 9px;
  font-weight: 500;
}
.uc-id {
  margin-top: 7px;
  font-size: 13px;
  color: #9aa0b4;
}
.uc-bio {
  margin: 9px 0 0;
  font-size: 13px;
  line-height: 1.55;
  color: #7a8098;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.uc-stats {
  display: flex;
  gap: 10px;
  margin-top: 20px;
}
.us-box {
  flex: 1;
  min-width: 0;
  background: #f6f8fd;
  border-radius: 13px;
  padding: 12px 6px;
  text-align: center;
}
.us-box small {
  display: block;
  font-size: 12px;
  color: #969cb2;
  margin-bottom: 5px;
  white-space: nowrap;
}
.us-box b {
  font-size: 21px;
  color: #2f3550;
  font-weight: 800;
}
.us-box b i {
  font-style: normal;
  font-size: 12px;
  color: #969cb2;
  font-weight: 500;
  margin-left: 1px;
}

.ai-entry {
  margin-top: 16px;
  width: 100%;
  display: flex;
  align-items: center;
  gap: 12px;
  border: 0;
  cursor: pointer;
  text-align: left;
  border-radius: 15px;
  padding: 14px 16px;
  background: linear-gradient(120deg, #edefff, #e4eeff);
  transition: transform .2s, box-shadow .2s;
}
.ai-entry:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 22px rgba(109, 120, 224, .18);
}
.ai-entry img {
  width: 46px;
  height: 46px;
  border-radius: 12px;
  object-fit: cover;
  flex: 0 0 auto;
}
.ae-txt {
  flex: 1;
  min-width: 0;
}
.ae-txt b {
  display: block;
  font-size: 15.5px;
  color: #434a8a;
  font-weight: 700;
}
.ae-txt small {
  display: block;
  margin-top: 3px;
  font-size: 12px;
  color: #8b93c4;
}
.ae-arrow {
  color: #a3abe0;
  width: 18px;
  height: 18px;
  flex: 0 0 auto;
}
.ae-arrow :deep(svg) {
  width: 100%;
  height: 100%;
}

.uc-login {
  margin-top: 18px;
  padding-top: 16px;
  border-top: 1px dashed #e8ebf6;
}
.ul-title {
  display: flex;
  align-items: center;
  gap: 7px;
  margin-bottom: 10px;
}
.ul-title span {
  width: 17px;
  height: 17px;
  color: #5b63f0;
}
.ul-title span :deep(svg) {
  width: 100%;
  height: 100%;
}
.ul-title b {
  font-size: 14.5px;
  color: #3c4260;
}
.ul-info {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}
.ul-time {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #5f6680;
}
.ul-time i {
  width: 15px;
  height: 15px;
  color: #9aa2d8;
  display: inline-flex;
}
.ul-time i :deep(svg) {
  width: 100%;
  height: 100%;
}
.ul-plat {
  font-size: 12px;
  color: #8a90a6;
  background: #f2f4fa;
  border-radius: 8px;
  padding: 3px 10px;
}

/* ---------- 设置卡 ---------- */
.f-card {
  background: #fff;
  border: 1px solid #edf0f8;
  border-radius: 18px;
  box-shadow: 0 10px 28px rgba(94, 110, 190, .07);
  padding: 20px 28px 28px;
  min-width: 0;
}
.f-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  border-bottom: 1px solid #eff1f9;
  padding: 4px 0 0;
}
.f-tabs {
  display: flex;
  gap: 30px;
  overflow-x: auto;
  scrollbar-width: none;
}
.f-tabs::-webkit-scrollbar {
  display: none;
}
.f-tab {
  position: relative;
  border: 0;
  background: transparent;
  padding: 12px 2px 14px;
  font-size: 16px;
  color: #8a90a6;
  cursor: pointer;
  white-space: nowrap;
  transition: color .2s;
}
.f-tab.on {
  color: #4350d0;
  font-weight: 700;
}
.f-tab.on::after {
  content: '';
  position: absolute;
  left: 50%;
  bottom: -1px;
  transform: translateX(-50%);
  width: 26px;
  height: 3px;
  border-radius: 3px;
  background: linear-gradient(90deg, #6d5ce0, #5b83f0);
}
.save-btn {
  border: 0;
  cursor: pointer;
  background: linear-gradient(120deg, #6d7bf0, #5b63f0);
  color: #fff;
  font-size: 15px;
  font-weight: 600;
  border-radius: 12px;
  padding: 11px 26px;
  box-shadow: 0 8px 18px rgba(91, 99, 240, .28);
  transition: transform .15s, box-shadow .15s, opacity .2s;
}
.save-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 10px 22px rgba(91, 99, 240, .36);
}
.save-btn:disabled {
  opacity: .6;
  cursor: not-allowed;
  transform: none;
}

.f-panel {
  padding-top: 22px;
}
.panel-title {
  position: relative;
  margin: 0 0 22px;
  padding-left: 12px;
  font-size: 16px;
  font-weight: 700;
  color: #2f3550;
}
.panel-title::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 16px;
  border-radius: 4px;
  background: linear-gradient(180deg, #6d7bf0, #5b63f0);
}
.mt28 {
  margin-top: 28px;
}

/* 基本信息表单 */
.basic-grid {
  display: grid;
  grid-template-columns: 132px minmax(0, 1fr);
  gap: 30px;
  align-items: start;
}
.avatar-col {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 4px;
}
.big-avatar {
  position: relative;
  width: 118px;
  height: 118px;
}
.big-avatar img,
.ba-ph {
  width: 118px;
  height: 118px;
  border-radius: 50%;
  object-fit: cover;
  border: 4px solid #fff;
  box-shadow: 0 8px 22px rgba(94, 110, 190, .2);
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #aebaf2, #8b86e8);
  color: #fff;
  font-size: 40px;
  font-weight: 700;
}
.ba-cam {
  position: absolute;
  right: 2px;
  bottom: 4px;
  width: 34px;
  height: 34px;
  border-radius: 50%;
  border: 2.5px solid #fff;
  background: linear-gradient(135deg, #7c8df6, #6d5ce0);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  padding: 0;
  box-shadow: 0 5px 12px rgba(109, 92, 224, .35);
}
.ba-cam :deep(svg) {
  width: 17px;
  height: 17px;
}
.change-avatar-btn {
  margin-top: 16px;
  background: #fff;
  border: 1.6px solid #dcdffa;
  color: #5b63f0;
  font-size: 14px;
  border-radius: 11px;
  padding: 9px 22px;
  cursor: pointer;
  transition: all .2s;
}
.change-avatar-btn:hover {
  background: #f2f4ff;
  border-color: #b9c0f8;
}
.avatar-tip {
  margin: 10px 0 0;
  font-size: 12px;
  color: #a2a8bc;
  text-align: center;
  line-height: 1.5;
  white-space: nowrap;
}

.basic-form {
  width: 100%;
  max-width: none;
}
.basic-form :deep(.el-form-item) {
  margin-bottom: 21px;
}
.basic-form :deep(.el-form-item__label) {
  font-size: 14px;
  color: #5a6072;
  font-weight: 500;
  padding-bottom: 4px;
}
.basic-form :deep(.el-input__wrapper),
.basic-form :deep(.el-textarea__inner) {
  border-radius: 11px;
  box-shadow: 0 0 0 1px #e3e7f2 inset;
  padding: 4px 14px;
  min-height: 42px;
}
.basic-form :deep(.el-textarea__inner) {
  padding: 10px 14px;
}
.basic-form :deep(.el-input__wrapper.is-focus),
.basic-form :deep(.el-textarea__inner:focus) {
  box-shadow: 0 0 0 1.5px #6d7bf0 inset;
}
.basic-form :deep(.el-radio) {
  margin-right: 28px;
  height: 42px;
}
.basic-form :deep(.el-radio__label) {
  font-size: 14px;
  color: #4a5068;
  padding-left: 8px;
}
.basic-form :deep(.el-date-editor.el-input__wrapper) {
  padding: 4px 12px;
}

/* 账号设置 */
.info-rows {
  max-width: none;
  background: #f8f9fd;
  border-radius: 14px;
  padding: 6px 22px;
}
.info-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 15px 0;
  border-bottom: 1px dashed #e6e9f4;
}
.info-row:last-child {
  border-bottom: 0;
}
.info-row span {
  font-size: 14px;
  color: #8a90a6;
}
.info-row b {
  font-size: 14.5px;
  color: #3c4260;
  font-weight: 600;
}
.pref-rows {
  max-width: none;
}
.pref-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18px;
  padding: 15px 0;
  border-bottom: 1px dashed #e6e9f4;
}
.pref-row:last-child {
  border-bottom: 0;
}
.pr-txt b {
  display: block;
  font-size: 14.5px;
  color: #3c4260;
  margin-bottom: 4px;
}
.pr-txt small {
  font-size: 12.5px;
  color: #9aa0b4;
}
.pref-note {
  margin: 14px 0 0;
  font-size: 12px;
  color: #b0b6c8;
}

/* 安全设置 */
.pwd-form {
  max-width: 460px;
}
.pwd-form :deep(.el-form-item) {
  margin-bottom: 19px;
}
.pwd-form :deep(.el-form-item__label) {
  font-size: 14px;
  color: #5a6072;
  font-weight: 500;
  padding-bottom: 6px;
}
.pwd-form :deep(.el-input__wrapper) {
  border-radius: 11px;
  box-shadow: 0 0 0 1px #e3e7f2 inset;
  padding: 4px 14px;
  min-height: 42px;
}
.pwd-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1.5px #6d7bf0 inset;
}
.pwd-alert {
  max-width: 460px;
  border-radius: 11px;
}

/* ==================== 响应式 ==================== */
@media (max-width: 1280px) {
  .pf-content {
    grid-template-columns: minmax(0, 1fr);
  }
  .u-card {
    max-width: none;
  }
}
@media (max-width: 1080px) {
  .pf-layout {
    grid-template-columns: minmax(0, 1fr);
  }
  .p-side {
    position: static;
  }
  .side-menu {
    flex-direction: row;
    overflow-x: auto;
    scrollbar-width: none;
    gap: 8px;
    padding: 10px;
  }
  .side-menu::-webkit-scrollbar {
    display: none;
  }
  .menu-item {
    width: auto;
    flex: 0 0 auto;
    white-space: nowrap;
    padding: 9px 15px;
  }
  .side-art {
    display: none;
  }
}
@media (max-width: 1360px) {
  .hero-hand {
    font-size: 23px;
  }
}
@media (max-width: 760px) {
  .profile-page {
    padding: 14px 14px 32px;
  }
  .p-hero {
    flex-direction: column;
    align-items: stretch;
    height: auto;
    padding: 20px 16px 16px;
    gap: 14px;
  }
  .hero-hand {
    font-size: 21px;
    margin-right: 0;
    transform: rotate(-4deg);
  }
  .hero-stats {
    width: 100%;
    justify-content: space-between;
    gap: 8px;
    padding: 12px 12px;
  }
  .hs-sep {
    display: none;
  }
  .hs-ic {
    width: 38px;
    height: 38px;
  }
  .hs-txt b {
    font-size: 19px;
  }
  .hs-txt small {
    font-size: 11px;
  }
  .basic-grid {
    grid-template-columns: minmax(0, 1fr);
    gap: 24px;
  }
  .avatar-col {
    flex-direction: row;
    flex-wrap: wrap;
    gap: 0 18px;
    justify-content: flex-start;
    align-items: center;
  }
  .avatar-tip {
    width: 100%;
    text-align: left;
  }
  .f-card {
    padding: 16px 16px 22px;
  }
  .f-tabs {
    gap: 20px;
  }
  .f-tab {
    font-size: 15px;
  }
  .save-btn {
    padding: 9px 18px;
    font-size: 14px;
  }
  .uc-stats {
    flex-wrap: wrap;
  }
  .us-box {
    flex: 1 1 30%;
  }
}
</style>
