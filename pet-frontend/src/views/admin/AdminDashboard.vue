<template>
  <div class="dashboard-page" v-loading="loading">
    <!-- 欢迎横幅 -->
    <div class="welcome-card">
      <div class="w-left">
        <h2 class="w-title">
          你好，管理员
          <svg class="w-paw" viewBox="0 0 24 24" fill="currentColor"><path d="M12 13.5c-2.9 0-5.5 2.3-5.5 4.6 0 1.5 1.1 2.4 2.6 2.4 1.1 0 1.9-.5 2.9-.5s1.8.5 2.9.5c1.5 0 2.6-.9 2.6-2.4 0-2.3-2.6-4.6-5.5-4.6zM6.4 8.2c-1.2.3-2 1.6-1.8 2.9.2 1.2 1.3 2 2.5 1.8 1.2-.3 2-1.6 1.8-2.9-.2-1.2-1.3-2-2.5-1.8zm11.2 0c-1.2-.2-2.3.6-2.5 1.8-.2 1.3.6 2.6 1.8 2.9 1.2.2 2.3-.6 2.5-1.8.2-1.3-.6-2.6-1.8-2.9zM9.3 3.6c-1.1.2-1.9 1.4-1.7 2.7.2 1.2 1.2 2.1 2.4 1.9 1.1-.2 1.9-1.4 1.7-2.7-.2-1.2-1.3-2.1-2.4-1.9zm5.4 0c-1.1-.2-2.2.7-2.4 1.9-.2 1.3.6 2.5 1.7 2.7 1.2.2 2.2-.7 2.4-1.9.2-1.3-.6-2.5-1.7-2.7z"/></svg>
        </h2>
        <p class="w-sub">欢迎来到宠知AI后台管理系统</p>
        <div class="w-meta">
          <span class="w-date">{{ now }}</span>
          <button class="refresh-btn" @click="loadAll">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><path d="M21 12a9 9 0 1 1-2.64-6.36M21 3v6h-6"/></svg>
            刷新
          </button>
        </div>
      </div>
      <!-- 内联 SVG 插画作为 Banner 背景（右锚定，左缘渐隐融入） -->
      <svg class="w-bg" viewBox="0 0 280 140" aria-hidden="true">
        <circle cx="246" cy="26" r="24" fill="#ffffff" opacity=".55" />
        <circle cx="28" cy="114" r="16" fill="#ffffff" opacity=".5" />
        <path d="M12 20s-7.5-4.6-9.5-9C1 7.5 3.5 4 7 4c2.1 0 3.7 1.2 5 3 1.3-1.8 2.9-3 5-3 3.5 0 6 3.5 4.5 7-2 4.4-9.5 9-9.5 9z" fill="#f7a8c4" transform="translate(36 26) scale(.62)" />
        <path d="M12 20s-7.5-4.6-9.5-9C1 7.5 3.5 4 7 4c2.1 0 3.7 1.2 5 3 1.3-1.8 2.9-3 5-3 3.5 0 6 3.5 4.5 7-2 4.4-9.5 9-9.5 9z" fill="#fbd0e0" transform="translate(232 100) scale(.5)" />
        <path :d="PAW_PATH" fill="#c3cff5" transform="translate(230 20) scale(.85) rotate(16)" />
        <path :d="PAW_PATH" fill="#d3dcf8" transform="translate(8 92) scale(.75) rotate(-14)" />
        <!-- 狗 -->
        <g>
          <ellipse cx="76" cy="56" rx="11" ry="17" fill="#dba75e" transform="rotate(-20 76 56)" />
          <ellipse cx="140" cy="56" rx="11" ry="17" fill="#dba75e" transform="rotate(20 140 56)" />
          <circle cx="108" cy="76" r="36" fill="#f3cf9a" />
          <ellipse cx="108" cy="91" rx="15" ry="11" fill="#fdeed3" />
          <circle cx="95" cy="70" r="4" fill="#4a3b2a" />
          <circle cx="121" cy="70" r="4" fill="#4a3b2a" />
          <circle cx="96.4" cy="68.6" r="1.2" fill="#fff" />
          <circle cx="122.4" cy="68.6" r="1.2" fill="#fff" />
          <ellipse cx="108" cy="87" rx="5.5" ry="4.2" fill="#4a3b2a" />
          <path d="M108 91 q0 7 -8 8 M108 91 q0 7 8 8" stroke="#4a3b2a" stroke-width="2" fill="none" stroke-linecap="round" />
          <ellipse cx="87" cy="82" rx="5" ry="3" fill="#f7a8a8" opacity=".45" />
          <ellipse cx="129" cy="82" rx="5" ry="3" fill="#f7a8a8" opacity=".45" />
        </g>
        <!-- 猫 -->
        <g>
          <path d="M176 68 l-5 -18 16 9 z" fill="#f7c183" />
          <path d="M216 68 l5 -18 -16 9 z" fill="#f7c183" />
          <path d="M176.5 64 l-2.6 -9.5 8.5 4.8 z" fill="#fbd9ae" />
          <path d="M215.5 64 l2.6 -9.5 -8.5 4.8 z" fill="#fbd9ae" />
          <circle cx="196" cy="88" r="29" fill="#f7c183" />
          <path d="M188 62 v8 M196 59 v9 M204 62 v8" stroke="#e2a163" stroke-width="2" stroke-linecap="round" />
          <circle cx="186" cy="84" r="3.6" fill="#4a3b2a" />
          <circle cx="206" cy="84" r="3.6" fill="#4a3b2a" />
          <path d="M196 90 l-3.4 3.6 h6.8 z" fill="#e2856d" />
          <path d="M172 88 h-13 M172 93 h-12 M220 88 h13 M220 93 h12" stroke="#e2a163" stroke-width="1.6" stroke-linecap="round" />
          <ellipse cx="179" cy="92" rx="4" ry="2.6" fill="#f7a8a8" opacity=".5" />
          <ellipse cx="213" cy="92" rx="4" ry="2.6" fill="#f7a8a8" opacity=".5" />
        </g>
      </svg>
    </div>

    <!-- 统计卡 -->
    <div class="stat-grid">
      <div class="stat-card" v-for="c in statCards" :key="c.key">
        <div class="s-icon" :style="{ background: c.bg, color: c.color }" v-html="c.icon"></div>
        <div class="s-body">
          <div class="s-label">{{ c.label }}</div>
          <div class="s-value">{{ fmtNum(c.value) }}</div>
          <div class="s-growth">
            <span class="s-up">{{ c.growth >= 0 ? '↑' : '↓' }}{{ Math.abs(c.growth).toFixed(1) }}%</span>
            <span class="s-cmp">较上月</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 主区三列 -->
    <div class="main-grid">
      <!-- 左列 -->
      <div class="col-left">
        <div class="card">
          <div class="card-head">
            <h3 class="card-title">平台数据趋势</h3>
            <div class="legend">
              <span v-for="s in series" :key="s.key" class="lg-item">
                <i class="lg-dot" :style="{ background: s.color }"></i>{{ s.name }}
              </span>
            </div>
            <el-select v-model="days" size="small" class="day-select" @change="loadAll">
              <el-option :value="7" label="近7天" />
              <el-option :value="30" label="近30天" />
            </el-select>
          </div>
          <div class="chart-wrap">
            <svg :viewBox="`0 0 ${CH.w} ${CH.h}`" class="line-chart" preserveAspectRatio="none">
              <!-- 横向网格线 + y轴刻度 -->
              <g v-for="(t, i) in yTicks" :key="'g' + i">
                <line :x1="CH.pl" :x2="CH.w - CH.pr" :y1="yPos(t)" :y2="yPos(t)" stroke="#eef1f8" stroke-width="1" />
                <text :x="CH.pl - 8" :y="yPos(t) + 4" text-anchor="end" class="axis-text">{{ fmtAxis(t) }}</text>
              </g>
              <!-- x 轴日期 -->
              <text
                v-for="(r, i) in trend" :key="'x' + i"
                v-show="showXLabel(i)"
                :x="xPos(i)" :y="CH.h - 8" text-anchor="middle" class="axis-text"
              >{{ r.date }}</text>
              <!-- 折线 -->
              <polyline
                v-for="s in series" :key="s.key"
                :points="linePoints(s.key)"
                fill="none" :stroke="s.color" stroke-width="2.4" stroke-linecap="round" stroke-linejoin="round"
              />
              <!-- 数据点 -->
              <g v-for="s in series" :key="'d' + s.key">
                <circle
                  v-for="(r, i) in trend" :key="i"
                  :cx="xPos(i)" :cy="yPos(Number(r[s.key]) || 0)" r="3"
                  fill="#fff" :stroke="s.color" stroke-width="2"
                />
              </g>
            </svg>
          </div>
        </div>
      </div>

      <!-- 中列 -->
      <div class="col-mid">
        <div class="card">
          <div class="card-head">
            <h3 class="card-title">宠物品种分布</h3>
          </div>
          <div class="donut-wrap">
            <svg viewBox="0 0 200 200" class="donut">
              <circle cx="100" cy="100" r="70" fill="none" stroke="#f1f3fa" stroke-width="26" />
              <circle
                v-for="(seg, i) in donutSegments" :key="i"
                cx="100" cy="100" r="70" fill="none"
                :stroke="seg.color" stroke-width="26"
                :stroke-dasharray="seg.dash" :stroke-dashoffset="seg.offset"
                transform="rotate(-90 100 100)"
              />
              <text x="100" y="94" text-anchor="middle" class="donut-num">{{ fmtNum(petTotal) }}</text>
              <text x="100" y="116" text-anchor="middle" class="donut-label">宠物总数</text>
            </svg>
            <ul class="donut-legend">
              <li v-for="b in breedDist" :key="b.name">
                <i class="lg-dot" :style="{ background: b.color }"></i>
                <span class="dl-name">{{ b.name }}</span>
                <span class="dl-pct">{{ Number(b.pct || 0).toFixed(1) }}%</span>
              </li>
            </ul>
          </div>
        </div>
      </div>

      <!-- 右列 -->
      <div class="col-right">
        <div class="card">
          <div class="card-head">
            <h3 class="card-title">快捷操作</h3>
          </div>
          <div class="quick-grid">
            <button
              v-for="q in quickActions" :key="q.label"
              class="quick-item" :style="{ background: q.bg, color: q.color }"
              @click="go(q.path)"
            >
              <span class="q-icon" v-html="q.icon"></span>
              <span class="q-label">{{ q.label }}</span>
            </button>
          </div>
        </div>

        <div class="card">
          <div class="card-head">
            <h3 class="card-title">最近动态</h3>
            <a class="more-link" @click="moreFeed">查看更多 &gt;</a>
          </div>
          <ul class="feed-list">
            <li v-for="(f, i) in feed" :key="i" class="feed-item">
              <span class="f-icon" :style="{ background: f.bg, color: f.color }" v-html="f.icon"></span>
              <div class="f-body">
                <div class="f-title">{{ f.title }}</div>
                <div class="f-sub">{{ f.sub }}</div>
              </div>
              <span class="f-time">{{ f.time }}</span>
            </li>
          </ul>
        </div>

      </div>

      <!-- 最新用户注册（横跨左中两列，填补空隙） -->
      <div class="card user-card">
        <div class="card-head">
          <h3 class="card-title">最新用户注册</h3>
          <a class="more-link" @click="go('/admin/user')">查看更多 &gt;</a>
        </div>
        <el-table :data="latestUsers" size="default" class="user-table">
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="nickname" label="用户名" min-width="110" show-overflow-tooltip />
          <el-table-column prop="account" label="账号" min-width="110" show-overflow-tooltip />
          <el-table-column prop="createTime" label="注册时间" min-width="150">
            <template #default="{ row }">{{ fmtTime(row.createTime) }}</template>
          </el-table-column>
          <el-table-column label="状态" width="90">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
                {{ row.status === 1 ? '正常' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="110">
            <template #default>
              <a class="op-link" @click="go('/admin/user')">查看</a>
              <a class="op-link" @click="go('/admin/user')">编辑</a>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>

    <!-- 底部关爱横幅 -->
    <div class="bottom-banner">
      <div class="b-text">
        <h3>关爱宠物 从我们做起</h3>
        <p>让每一只宠物都被温柔以待 · 科学养护 · 用心陪伴</p>
        <button class="promo-btn" @click="go('/admin/pet')">前往宠物管理 →</button>
      </div>
      <svg class="b-svg" viewBox="0 0 300 120" aria-label="宠物插画">
        <circle cx="272" cy="26" r="20" fill="#ffffff" opacity=".6" />
        <circle cx="20" cy="100" r="12" fill="#ffffff" opacity=".45" />
        <!-- 坐着的小猫 -->
        <g>
          <path d="M118 96 q26 6 24 -20" stroke="#a9bdf0" stroke-width="8" fill="none" stroke-linecap="round" />
          <ellipse cx="94" cy="90" rx="30" ry="24" fill="#a9bdf0" />
          <path d="M66 44 l-4 -15 14 8 z" fill="#b9c8f5" />
          <path d="M94 40 l5 -15 12 11 z" fill="#b9c8f5" />
          <circle cx="80" cy="58" r="23" fill="#b9c8f5" />
          <circle cx="72" cy="55" r="3" fill="#3f4a6b" />
          <circle cx="88" cy="55" r="3" fill="#3f4a6b" />
          <path d="M80 61 l-2.8 3 h5.6 z" fill="#e2856d" />
          <path d="M60 62 h-12 M60 67 h-11 M100 62 h12 M100 67 h11" stroke="#9db3e8" stroke-width="1.5" stroke-linecap="round" />
        </g>
        <!-- 小球 -->
        <circle cx="176" cy="98" r="11" fill="#f7a8c4" />
        <path d="M166 94 q10 6 20 2" stroke="#fff" stroke-width="2.4" fill="none" stroke-linecap="round" />
        <!-- 爱心与爪印 -->
        <path d="M12 20s-7.5-4.6-9.5-9C1 7.5 3.5 4 7 4c2.1 0 3.7 1.2 5 3 1.3-1.8 2.9-3 5-3 3.5 0 6 3.5 4.5 7-2 4.4-9.5 9-9.5 9z" fill="#ffffff" opacity=".85" transform="translate(140 28) scale(.6)" />
        <path :d="PAW_PATH" fill="#ffffff" opacity=".8" transform="translate(200 22) scale(.8) rotate(14)" />
        <path :d="PAW_PATH" fill="#ffffff" opacity=".6" transform="translate(236 66) scale(.65) rotate(-12)" />
      </svg>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getDashboard } from '@/api/admin'

const router = useRouter()
const loading = ref(false)
const days = ref(7)

const PAW_PATH = 'M12 13.5c-2.9 0-5.5 2.3-5.5 4.6 0 1.5 1.1 2.4 2.6 2.4 1.1 0 1.9-.5 2.9-.5s1.8.5 2.9.5c1.5 0 2.6-.9 2.6-2.4 0-2.3-2.6-4.6-5.5-4.6zM6.4 8.2c-1.2.3-2 1.6-1.8 2.9.2 1.2 1.3 2 2.5 1.8 1.2-.3 2-1.6 1.8-2.9-.2-1.2-1.3-2-2.5-1.8zm11.2 0c-1.2-.2-2.3.6-2.5 1.8-.2 1.3.6 2.6 1.8 2.9 1.2.2 2.3-.6 2.5-1.8.2-1.3-.6-2.6-1.8-2.9zM9.3 3.6c-1.1.2-1.9 1.4-1.7 2.7.2 1.2 1.2 2.1 2.4 1.9 1.1-.2 1.9-1.4 1.7-2.7-.2-1.2-1.3-2.1-2.4-1.9zm5.4 0c-1.1-.2-2.2.7-2.4 1.9-.2 1.3.6 2.5 1.7 2.7 1.2.2 2.2-.7 2.4-1.9.2-1.3-.6-2.5-1.7-2.7z'

/* ===== 实时时钟 ===== */
const now = ref('')
let timer = null
function tick() {
  const d = new Date()
  const p = n => String(n).padStart(2, '0')
  now.value = `${d.getFullYear()}-${p(d.getMonth() + 1)}-${p(d.getDate())} ${p(d.getHours())}:${p(d.getMinutes())}:${p(d.getSeconds())}`
}

/* ===== 图标（内联 SVG） ===== */
const ICON = {
  user: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><circle cx="12" cy="8" r="4"/><path d="M4 20c0-4 3.6-6.5 8-6.5s8 2.5 8 6.5"/></svg>',
  paw: '<svg viewBox="0 0 24 24" fill="currentColor"><path d="M12 13.5c-2.9 0-5.5 2.3-5.5 4.6 0 1.5 1.1 2.4 2.6 2.4 1.1 0 1.9-.5 2.9-.5s1.8.5 2.9.5c1.5 0 2.6-.9 2.6-2.4 0-2.3-2.6-4.6-5.5-4.6zM6.4 8.2c-1.2.3-2 1.6-1.8 2.9.2 1.2 1.3 2 2.5 1.8 1.2-.3 2-1.6 1.8-2.9-.2-1.2-1.3-2-2.5-1.8zm11.2 0c-1.2-.2-2.3.6-2.5 1.8-.2 1.3.6 2.6 1.8 2.9 1.2.2 2.3-.6 2.5-1.8.2-1.3-.6-2.6-1.8-2.9zM9.3 3.6c-1.1.2-1.9 1.4-1.7 2.7.2 1.2 1.2 2.1 2.4 1.9 1.1-.2 1.9-1.4 1.7-2.7-.2-1.2-1.3-2.1-2.4-1.9zm5.4 0c-1.1-.2-2.2.7-2.4 1.9-.2 1.3.6 2.5 1.7 2.7 1.2.2 2.2-.7 2.4-1.9.2-1.3-.6-2.5-1.7-2.7z"/></svg>',
  chip: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><rect x="6" y="6" width="12" height="12" rx="2"/><rect x="10" y="10" width="4" height="4"/><path d="M9 2v3M15 2v3M9 19v3M15 19v3M2 9h3M2 15h3M19 9h3M19 15h3"/></svg>',
  chat: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 12c0 4-4 7-9 7-1.2 0-2.4-.2-3.4-.6L4 19l1-3.2C3.8 14.6 3 13.4 3 12c0-4 4-7 9-7s9 3 9 7z"/></svg>',
  doc: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><path d="M6 2h9l5 5v15H6z"/><path d="M14 2v6h6M9 13h6M9 17h6"/></svg>',
  goods: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M4 7l8-4 8 4v10l-8 4-8-4z"/><path d="M4 7l8 4 8-4M12 11v10"/></svg>',
  heart: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 20s-7.5-4.6-9.5-9C1 7.5 3.5 4 7 4c2.1 0 3.7 1.2 5 3 1.3-1.8 2.9-3 5-3 3.5 0 6 3.5 4.5 7-2 4.4-9.5 9-9.5 9z"/></svg>',
  home: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M3 11l9-8 9 8"/><path d="M5 10v10h14V10"/><path d="M10 20v-6h4v6"/></svg>'
}

/* ===== 统计卡（演示兜底） ===== */
const stats = ref({
  userCount: 12846, petCount: 8732, recognitionCount: 56320, consultCount: 18624,
  userGrowth: 12.5, petGrowth: 8.7, recognitionGrowth: 21.3, consultGrowth: 15.6
})

const statCards = computed(() => [
  { key: 'user', label: '注册用户数', value: stats.value.userCount, growth: stats.value.userGrowth ?? 0, icon: ICON.user, bg: '#e8f1ff', color: '#3b7cff' },
  { key: 'pet', label: '宠物总数', value: stats.value.petCount, growth: stats.value.petGrowth ?? 0, icon: ICON.paw, bg: '#e6f9f0', color: '#12b886' },
  { key: 'ai', label: 'AI识别次数', value: stats.value.recognitionCount, growth: stats.value.recognitionGrowth ?? 0, icon: ICON.chip, bg: '#f1ebff', color: '#8b5cf6' },
  { key: 'consult', label: '咨询总数', value: stats.value.consultCount, growth: stats.value.consultGrowth ?? 0, icon: ICON.chat, bg: '#ffeef5', color: '#f76c9b' }
])

/* ===== 折线图 ===== */
const series = [
  { key: 'users', name: '注册用户', color: '#4f8cff' },
  { key: 'pets', name: '宠物档案', color: '#36c98e' },
  { key: 'recognitions', name: 'AI识别', color: '#8b5cf6' },
  { key: 'consults', name: '咨询', color: '#f5a623' }
]

const demoTrend = () => {
  const base = [
    [820, 460, 2100, 690], [932, 521, 2450, 742], [901, 487, 2280, 715],
    [1094, 552, 2680, 801], [1190, 604, 2845, 866], [1050, 576, 2560, 820],
    [1235, 648, 3010, 905]
  ]
  const names = ['08-31', '09-01', '09-02', '09-03', '09-04', '09-05', '09-06']
  return base.map((b, i) => ({
    date: names[i], users: b[0], pets: b[1], recognitions: b[2], consults: b[3]
  }))
}
const trend = ref(demoTrend())

const CH = { w: 600, h: 220, pl: 44, pr: 14, pt: 16, pb: 28 }

function niceMax(m) {
  if (m <= 4) return 4
  const step = Math.pow(10, Math.floor(Math.log10(m)))
  const r = m / step
  const nr = r <= 1 ? 1 : r <= 2 ? 2 : r <= 2.5 ? 2.5 : r <= 5 ? 5 : 10
  return nr * step
}

const maxY = computed(() => {
  let m = 1
  trend.value.forEach(r => series.forEach(s => { m = Math.max(m, Number(r[s.key]) || 0) }))
  return niceMax(m)
})

const yTicks = computed(() => {
  const m = maxY.value
  return [0, m / 4, m / 2, (m * 3) / 4, m]
})

function xPos(i) {
  const n = trend.value.length
  if (n <= 1) return CH.pl
  return CH.pl + (i * (CH.w - CH.pl - CH.pr)) / (n - 1)
}
function yPos(v) {
  return CH.pt + (CH.h - CH.pt - CH.pb) * (1 - v / maxY.value)
}
function linePoints(key) {
  return trend.value.map((r, i) => `${xPos(i)},${yPos(Number(r[key]) || 0)}`).join(' ')
}
function showXLabel(i) {
  const n = trend.value.length
  if (n <= 10) return true
  return i % Math.ceil(n / 8) === 0 || i === n - 1
}
function fmtAxis(v) {
  return v >= 10000 ? (v / 10000) + 'w' : v >= 1000 ? (v / 1000) + 'k' : String(Math.round(v))
}

/* ===== 品种分布环形图 ===== */
const BREED_COLORS = { '犬类': '#4f8cff', '猫类': '#36c98e', '兔类': '#f5a623', '鸟类': '#8b5cf6', '其他': '#f76c9b' }
const breedDist = ref([
  { name: '犬类', count: 4567, pct: 52.3 },
  { name: '猫类', count: 3030, pct: 34.7 },
  { name: '兔类', count: 594, pct: 6.8 },
  { name: '鸟类', count: 279, pct: 3.2 },
  { name: '其他', count: 262, pct: 2.9 }
])
const petTotal = computed(() => stats.value.petCount || 0)

const donutSegments = computed(() => {
  const C = 2 * Math.PI * 70
  let off = 0
  return breedDist.value.map(b => {
    const len = (C * (Number(b.pct) || 0)) / 100
    const seg = { color: BREED_COLORS[b.name] || '#c0c4d6', dash: `${len} ${C - len}`, offset: -off }
    off += len
    return seg
  })
})

/* ===== 最新用户（演示兜底） ===== */
const latestUsers = ref([
  { id: 10086, nickname: '爱宠小王子', account: 'petlover01', createTime: '2026-09-06 15:42:33', status: 1 },
  { id: 10085, nickname: '喵星人家长', account: 'catmom02', createTime: '2026-09-06 14:18:20', status: 1 },
  { id: 10084, nickname: '金毛日常', account: 'golden03', createTime: '2026-09-06 11:05:47', status: 1 },
  { id: 10083, nickname: '布偶猫舍', account: 'ragdoll04', createTime: '2026-09-05 20:37:12', status: 1 },
  { id: 10082, nickname: '兔子不吃草', account: 'rabbit05', createTime: '2026-09-05 18:22:59', status: 1 }
])

/* ===== 快捷操作 ===== */
const quickActions = [
  { label: '添加用户', path: '/admin/user', icon: ICON.user, bg: '#e8f1ff', color: '#3b7cff' },
  { label: '发布资讯', path: '/admin/info', icon: ICON.doc, bg: '#e6f9f0', color: '#12b886' },
  { label: '商品管理', path: '/admin/product', icon: ICON.goods, bg: '#f1ebff', color: '#8b5cf6' },
  { label: '领养管理', path: '/admin/adoption', icon: ICON.heart, bg: '#fff4e5', color: '#f5a623' },
  { label: '寄养管理', path: '/admin/adoption', icon: ICON.home, bg: '#e6f9f4', color: '#14b8a6' },
  { label: '社区管理', path: '/admin/community', icon: ICON.chat, bg: '#ffeef5', color: '#f76c9b' }
]

/* ===== 最近动态（演示兜底，接口返回后覆盖） ===== */
const FEED_STYLE = {
  user: { icon: ICON.user, bg: '#e8f1ff', color: '#3b7cff' },
  pet: { icon: ICON.paw, bg: '#e6f9f0', color: '#12b886' },
  recognition: { icon: ICON.chip, bg: '#f1ebff', color: '#8b5cf6' },
  adoption: { icon: ICON.heart, bg: '#fff4e5', color: '#f5a623' },
  post: { icon: ICON.chat, bg: '#ffeef5', color: '#f76c9b' },
  order: { icon: ICON.goods, bg: '#fff4e5', color: '#f5a623' }
}

const feed = ref([
  { title: '新用户注册', sub: '用户：小可爱', time: '16:32', ...FEED_STYLE.user },
  { title: '新增宠物档案', sub: '金毛 · 豆豆', time: '15:47', ...FEED_STYLE.pet },
  { title: 'AI识别完成', sub: '识别结果：布偶猫', time: '14:20', ...FEED_STYLE.recognition },
  { title: '新的领养申请', sub: '申请人：李女士', time: '11:08', ...FEED_STYLE.adoption },
  { title: '社区新帖子', sub: '《我家猫主子的一天》', time: '09:55', ...FEED_STYLE.post }
])

function fmtFeedTime(t) {
  if (!t) return ''
  const s = String(t).replace('T', ' ')
  const today = new Date().toISOString().slice(0, 10)
  return s.startsWith(today) ? s.substring(11, 16) : s.substring(5, 16)
}

function mapFeed(list) {
  return list.map(f => ({
    title: f.title,
    sub: f.sub,
    time: fmtFeedTime(f.time),
    ...(FEED_STYLE[f.type] || FEED_STYLE.user)
  }))
}

/* ===== 数据加载 ===== */
function applyData(d) {
  stats.value = {
    userCount: d.userCount ?? 0,
    petCount: d.petCount ?? 0,
    recognitionCount: d.recognitionCount ?? 0,
    consultCount: d.consultCount ?? 0,
    userGrowth: d.userGrowth ?? 0,
    petGrowth: d.petGrowth ?? 0,
    recognitionGrowth: d.recognitionGrowth ?? 0,
    consultGrowth: d.consultGrowth ?? 0
  }
  if (Array.isArray(d.trend) && d.trend.length) trend.value = d.trend
  if (Array.isArray(d.breedDist) && d.breedDist.length) {
    breedDist.value = d.breedDist.map(b => ({ ...b, color: BREED_COLORS[b.name] || '#c0c4d6' }))
  }
  if (Array.isArray(d.latestUsers) && d.latestUsers.length) latestUsers.value = d.latestUsers
  if (Array.isArray(d.feed) && d.feed.length) feed.value = mapFeed(d.feed)
}

async function loadAll() {
  loading.value = true
  try {
    const res = await getDashboard(days.value)
    applyData(res.data || {})
  } catch (e) {
    /* 接口失败保留演示数据 */
  } finally {
    loading.value = false
  }
}

/* ===== 工具 ===== */
function fmtNum(n) {
  return Number(n || 0).toLocaleString('en-US')
}
function fmtTime(t) {
  return String(t || '').replace('T', ' ').substring(0, 19)
}
function go(path) {
  router.push(path)
}
function moreFeed() {
  ElMessage.info('功能开发中')
}

onMounted(() => {
  tick()
  timer = setInterval(tick, 1000)
  loadAll()
})
onUnmounted(() => {
  if (timer) clearInterval(timer)
})
</script>

<style scoped>
.dashboard-page {
  padding: 4px;
}

/* ===== 欢迎横幅 ===== */
.welcome-card {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  background: linear-gradient(120deg, #eef2ff 0%, #f3edff 55%, #efe9ff 100%);
  border-radius: 16px;
  padding: 26px 30px;
  margin-bottom: 18px;
  overflow: hidden;
}
.w-title {
  margin: 0 0 8px;
  font-size: 24px;
  color: #2c3e50;
  display: flex;
  align-items: center;
  gap: 8px;
}
.w-paw {
  width: 24px;
  height: 24px;
  color: #f5a623;
}
.w-sub {
  margin: 0 0 14px;
  color: #7a8699;
  font-size: 14px;
}
.w-meta {
  display: flex;
  align-items: center;
  gap: 14px;
}
.w-date {
  color: #9099ab;
  font-size: 13px;
  font-variant-numeric: tabular-nums;
}
.refresh-btn {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  border: none;
  background: #3b7cff;
  color: #fff;
  font-size: 13px;
  padding: 7px 16px;
  border-radius: 20px;
  cursor: pointer;
  transition: opacity .2s;
}
.refresh-btn:hover { opacity: .88; }
.refresh-btn svg { width: 14px; height: 14px; }
.w-left {
  position: relative;
  z-index: 1;
}
.w-bg {
  position: absolute;
  top: 0;
  right: 24px;
  height: 100%;
  width: auto;
  pointer-events: none;
  -webkit-mask-image: linear-gradient(90deg, transparent 0%, #000 38%);
  mask-image: linear-gradient(90deg, transparent 0%, #000 38%);
}

/* ===== 统计卡 ===== */
.stat-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
  margin-bottom: 18px;
}
.stat-card {
  background: #fff;
  border: 1px solid #edf0f8;
  border-radius: 16px;
  box-shadow: 0 10px 28px rgba(94, 110, 190, .07);
  padding: 20px;
  display: flex;
  gap: 14px;
  align-items: flex-start;
}
.s-icon {
  width: 50px;
  height: 50px;
  border-radius: 13px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.s-icon :deep(svg) { width: 26px; height: 26px; }
.s-label {
  color: #909399;
  font-size: 13px;
  margin-bottom: 4px;
}
.s-value {
  font-size: 26px;
  font-weight: 700;
  color: #2c3e50;
  line-height: 1.2;
  font-variant-numeric: tabular-nums;
}
.s-growth {
  margin-top: 5px;
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 6px;
}
.s-up { color: #f56c6c; font-weight: 600; }
.s-cmp { color: #b0b6c5; }

/* ===== 主区三列 ===== */
.main-grid {
  display: grid;
  grid-template-columns: minmax(0, 1.7fr) minmax(0, 1fr) 290px;
  gap: 16px;
  align-items: start;
}
.col-left, .col-mid, .col-right {
  display: flex;
  flex-direction: column;
  gap: 16px;
  min-width: 0;
}
/* 右列纵向贯穿两行，用户表紧跟图表下方，避免大片留白 */
.col-right { grid-column: 3; grid-row: 1 / 3; }
.user-card { grid-column: 1 / 3; }
.card {
  background: #fff;
  border: 1px solid #edf0f8;
  border-radius: 16px;
  box-shadow: 0 10px 28px rgba(94, 110, 190, .07);
  padding: 18px 20px;
}
.card-head {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 14px;
  flex-wrap: wrap;
}
.card-title {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
}
.more-link {
  margin-left: auto;
  color: #3b7cff;
  font-size: 13px;
  cursor: pointer;
  white-space: nowrap;
}
.more-link:hover { text-decoration: underline; }

/* 折线图 */
.legend {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
  margin-left: auto;
}
.lg-item {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  font-size: 12px;
  color: #7a8699;
}
.lg-dot {
  width: 9px;
  height: 9px;
  border-radius: 50%;
  display: inline-block;
  flex-shrink: 0;
}
.day-select { width: 96px; }
.chart-wrap { width: 100%; }
.line-chart {
  width: 100%;
  height: 240px;
  display: block;
}
.axis-text {
  font-size: 11px;
  fill: #a0a8bb;
}

/* 用户表 */
.user-table :deep(.el-table__header th) {
  background: #f7f9fd;
  color: #7a8699;
  font-weight: 600;
}
.op-link {
  color: #3b7cff;
  font-size: 13px;
  cursor: pointer;
  margin-right: 12px;
}
.op-link:hover { text-decoration: underline; }

/* 环形图 */
.donut-wrap {
  display: flex;
  align-items: center;
  gap: 18px;
  flex-wrap: wrap;
}
.donut {
  width: 190px;
  height: 190px;
  flex-shrink: 0;
}
.donut-num {
  font-size: 24px;
  font-weight: 700;
  fill: #2c3e50;
}
.donut-label {
  font-size: 12px;
  fill: #9099ab;
}
.donut-legend {
  list-style: none;
  margin: 0;
  padding: 0;
  flex: 1;
  min-width: 130px;
}
.donut-legend li {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 7px 0;
  font-size: 13px;
}
.dl-name { color: #4a5568; flex: 1; }
.dl-pct { color: #2c3e50; font-weight: 600; font-variant-numeric: tabular-nums; }

/* 快捷操作 */
.quick-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 10px;
}
.quick-item {
  border: none;
  border-radius: 12px;
  padding: 13px 6px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 7px;
  cursor: pointer;
  transition: transform .18s, box-shadow .18s;
}
.quick-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(94, 110, 190, .14);
}
.q-icon { display: flex; }
.q-icon :deep(svg) { width: 22px; height: 22px; }
.q-label { font-size: 12px; font-weight: 500; }

/* 最近动态 */
.feed-list {
  list-style: none;
  margin: 0;
  padding: 0;
}
.feed-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 9px 0;
}
.feed-item + .feed-item { border-top: 1px dashed #eef1f8; }
.f-icon {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.f-icon :deep(svg) { width: 18px; height: 18px; }
.f-body { flex: 1; min-width: 0; }
.f-title {
  font-size: 13px;
  color: #2c3e50;
  font-weight: 500;
}
.f-sub {
  font-size: 12px;
  color: #a0a8bb;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.f-time {
  font-size: 12px;
  color: #b0b6c5;
  flex-shrink: 0;
}

/* 底部关爱横幅 */
.bottom-banner {
  margin-top: 16px;
  background: linear-gradient(115deg, #d8e6ff 0%, #e2eeff 45%, #e7f6ff 100%);
  border: 1px solid #edf0f8;
  border-radius: 16px;
  box-shadow: 0 10px 28px rgba(94, 110, 190, .07);
  padding: 24px 34px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  overflow: hidden;
}
.b-text h3 {
  margin: 0 0 8px;
  font-size: 20px;
  color: #2c3e50;
}
.b-text p {
  margin: 0 0 14px;
  font-size: 13px;
  color: #7a8699;
}
.promo-btn {
  border: none;
  background: #3b7cff;
  color: #fff;
  font-size: 13px;
  padding: 9px 20px;
  border-radius: 20px;
  cursor: pointer;
  transition: opacity .2s;
}
.promo-btn:hover { opacity: .88; }
.b-svg {
  width: 300px;
  height: 120px;
  flex-shrink: 0;
}

/* 响应式 */
@media (max-width: 1400px) {
  .main-grid { grid-template-columns: minmax(0, 1.6fr) minmax(0, 1fr); }
  .col-right { grid-column: 1 / -1; grid-row: auto; display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); align-items: stretch; }
}
@media (max-width: 1020px) {
  .stat-grid { grid-template-columns: repeat(2, minmax(0, 1fr)); }
  .main-grid { grid-template-columns: 1fr; }
  .col-right { display: flex; }
  .user-card { grid-column: 1 / -1; }
}
@media (max-width: 640px) {
  .welcome-card { flex-direction: column; align-items: flex-start; }
  .w-bg { opacity: .35; }
  .stat-grid { grid-template-columns: 1fr; }
  .bottom-banner { flex-direction: column; align-items: flex-start; padding: 20px; }
  .b-svg { width: 100%; height: auto; }
}
</style>
