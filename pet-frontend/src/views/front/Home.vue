<template>
  <div class="home">
    <div class="home-inner">
      <!-- ============ Hero 区 ============ -->
      <section class="hero-card">
        <!-- 装饰元素 -->
        <div class="blob blob-tl"></div>
        <div class="blob blob-cyan"></div>
        <div class="blob blob-br">
          <svg class="blob-paw" viewBox="0 0 48 48">
            <ellipse cx="24" cy="30" rx="11" ry="9" />
            <circle cx="12" cy="20" r="4.6" />
            <circle cx="21" cy="13.5" r="4.6" />
            <circle cx="27" cy="13.5" r="4.6" />
            <circle cx="36" cy="20" r="4.6" />
          </svg>
        </div>
        <svg class="paw paw-orange" viewBox="0 0 48 48">
          <ellipse cx="24" cy="30" rx="11" ry="9" />
          <circle cx="12" cy="20" r="4.6" />
          <circle cx="21" cy="13.5" r="4.6" />
          <circle cx="27" cy="13.5" r="4.6" />
          <circle cx="36" cy="20" r="4.6" />
        </svg>
        <span class="sparkle sp-1">✦</span>
        <span class="sparkle sp-2">✦</span>

        <div class="hero-left">
          <span class="hero-badge">AI 让养宠更科学</span>
          <h1>认识你的宠物，<br />从一张照片开始</h1>
          <p class="hero-desc">AI 识别品种 / 年龄 / 性格特征 / 养护建议</p>
          <div class="hero-btns">
            <button class="btn-main" @click="goRecognize">
              <el-icon><Camera /></el-icon>
              立即识别
              <el-icon class="arr"><ArrowRight /></el-icon>
            </button>
            <button class="btn-outline" @click="goArchive">查看我的档案</button>
          </div>
        </div>

        <div class="hero-visual">
          <!-- 手写标注 -->
          <div class="note">
            <span>科技守护<br />宠物健康 ♡</span>
            <svg viewBox="0 0 90 40" class="note-arrow">
              <path d="M6 8 C 30 34, 58 36, 80 24" fill="none" stroke="#7c8aa5" stroke-width="2" stroke-linecap="round" />
              <path d="M72 18 L82 24 L70 29" fill="none" stroke="#7c8aa5" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" />
            </svg>
          </div>

          <div class="hero-photo">
            <img :src="IMG.hero" alt="金毛犬与猫咪" />
            <!-- AI 扫描四角框 -->
            <div class="scan-frame">
              <i class="corner tl"></i>
              <i class="corner tr"></i>
              <i class="corner bl"></i>
              <i class="corner br"></i>
            </div>

            <!-- 识别结果浮卡 -->
            <div class="ai-result">
              <div class="ar-head">
                <span class="ar-paw">
                  <svg viewBox="0 0 48 48">
                    <ellipse cx="24" cy="30" rx="11" ry="9" />
                    <circle cx="12" cy="20" r="4.6" />
                    <circle cx="21" cy="13.5" r="4.6" />
                    <circle cx="27" cy="13.5" r="4.6" />
                    <circle cx="36" cy="20" r="4.6" />
                  </svg>
                </span>
                <span class="ar-name">金毛寻回犬</span>
                <span class="ar-rate">98.7%</span>
              </div>
              <div class="ar-tags">
                <span class="ar-chevron">
                  <el-icon><ArrowDown /></el-icon>
                </span>
                <span class="ar-tag">1-3岁</span>
                <span class="ar-tag">雄性</span>
                <span class="ar-tag">温顺友善</span>
              </div>
            </div>

            <!-- 健康状态浮卡 -->
            <div class="health-float">
              <span class="hf-icon">
                <svg viewBox="0 0 24 24">
                  <path d="M12 21s-7.5-4.7-10-9.3C.4 8.6 2.3 5 5.8 5c2 0 3.4 1.1 4.2 2.3C10.8 6.1 12.2 5 14.2 5 17.7 5 19.6 8.6 22 11.7 19.5 16.3 12 21 12 21z" />
                </svg>
              </span>
              <div class="hf-text">
                <div class="hf-title">健康状态良好</div>
                <div class="hf-sub">体重 32.5kg ｜ 精神状态佳</div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- ============ 我的宠物 ============ -->
      <section class="panel pets-panel">
        <div class="panel-head">
          <span class="head-icon icon-paw">
            <svg viewBox="0 0 48 48">
              <ellipse cx="24" cy="30" rx="11" ry="9" />
              <circle cx="12" cy="20" r="4.6" />
              <circle cx="21" cy="13.5" r="4.6" />
              <circle cx="27" cy="13.5" r="4.6" />
              <circle cx="36" cy="20" r="4.6" />
            </svg>
          </span>
          <span class="head-title">我的宠物</span>
          <span class="head-sub">已为您记录 {{ pets.length }} 只宠物</span>
          <a class="head-more" @click="goArchive">管理全部宠物 <el-icon><ArrowRight /></el-icon></a>
        </div>

        <div class="pet-grid">
          <div class="pet-card" v-for="p in displayPets" :key="p._key">
            <div class="pc-photo">
              <img :src="resolvePetPhoto(p)" :alt="p.petName" />
            </div>
            <div class="pc-info">
              <div class="pc-name-row">
                <span class="pc-name">{{ p.petName }}</span>
                <span class="pc-gender" :class="genderClass(p.gender)">{{ genderSymbol(p.gender) }}</span>
              </div>
              <div class="pc-breed">{{ p.breed || '未知品种' }}</div>
              <div class="pc-age">{{ p.age || '年龄未知' }}</div>
              <div class="pc-time">最近识别：{{ p.lastTime }}</div>
            </div>
            <span class="pc-health"><i>✓</i> 健康</span>
            <button class="pc-btn" @click="viewArchive(p)">查看档案</button>
          </div>

          <div class="pet-add" @click="goArchive">
            <span class="pa-plus"><el-icon><Plus /></el-icon></span>
            <div class="pa-title">添加宠物</div>
            <div class="pa-sub">开启更多宠物的智能管理</div>
          </div>
        </div>
      </section>

      <!-- ============ 智能服务 ============ -->
      <section class="panel services-panel">
        <div class="panel-head">
          <span class="head-icon icon-spark">
            <svg viewBox="0 0 24 24">
              <path d="M12 2l1.9 5.6L19.5 9l-5.6 1.9L12 16.5l-1.9-5.6L4.5 9l5.6-1.4L12 2z" />
              <path d="M19 14.5l.9 2.6 2.6.9-2.6.9-.9 2.6-.9-2.6-2.6-.9 2.6-.9.9-2.6z" />
            </svg>
          </span>
          <span class="head-title">智能服务</span>
          <span class="head-sub">AI 技术让宠物养护更简单</span>
        </div>

        <div class="service-grid">
          <div class="service-card svc-purple" @click="goRecognize">
            <span class="svc-icon">
              <svg viewBox="0 0 48 48">
                <rect x="6" y="14" width="30" height="22" rx="6" fill="rgba(255,255,255,.95)" />
                <rect x="18" y="10" width="8" height="6" rx="2" fill="rgba(255,255,255,.95)" />
                <circle cx="21" cy="25" r="7" fill="#fff" stroke="#6d5ce0" stroke-width="2.4" />
                <circle cx="35" cy="33" r="8.5" fill="#7c5cff" />
                <text x="35" y="36.2" text-anchor="middle" font-size="8.5" font-weight="700" fill="#fff">AI</text>
              </svg>
            </span>
            <div class="svc-text">
              <div class="svc-title">AI图像识别</div>
              <div class="svc-desc">精准识别宠物品种、年龄、性格特征</div>
            </div>
            <span class="svc-arrow"><el-icon><ArrowRight /></el-icon></span>
          </div>

          <div class="service-card svc-green" @click="goArchive">
            <span class="svc-icon">
              <svg viewBox="0 0 48 48">
                <path d="M6 14a3 3 0 0 1 3-3h11l5 5h14a3 3 0 0 1 3 3v16a3 3 0 0 1-3 3H9a3 3 0 0 1-3-3V14z" fill="rgba(255,255,255,.95)" />
                <path d="M24 33s-7.5-4.4-7.5-8.6c0-2.4 1.9-4 3.9-4 1.5 0 2.8.9 3.6 2 .8-1.1 2.1-2 3.6-2 2 0 3.9 1.6 3.9 4C31.5 28.6 24 33 24 33z" fill="#18b87a" />
              </svg>
            </span>
            <div class="svc-text">
              <div class="svc-title">成长档案</div>
              <div class="svc-desc">记录宠物成长历程，生成专属档案</div>
            </div>
            <span class="svc-arrow"><el-icon><ArrowRight /></el-icon></span>
          </div>

          <div class="service-card svc-orange" @click="goRecords">
            <span class="svc-icon">
              <svg viewBox="0 0 48 48">
                <path d="M24 39S9 29.4 9 19.6C9 14.7 12.6 12 16 12c2.8 0 4.9 1.6 6 3.4C23.1 13.6 25.2 12 28 12c3.4 0 7 2.7 7 7.6C35 29.4 24 39 24 39z" fill="rgba(255,255,255,.96)" />
                <path d="M15 24h5l2.2-4 3.4 7 2-3h5.4" fill="none" stroke="#f56a3d" stroke-width="2.4" stroke-linecap="round" stroke-linejoin="round" />
              </svg>
            </span>
            <div class="svc-text">
              <div class="svc-title">健康提醒</div>
              <div class="svc-desc">疫苗、驱虫、体检等重要事项提醒</div>
            </div>
            <span class="svc-arrow"><el-icon><ArrowRight /></el-icon></span>
          </div>

          <div class="service-card svc-blue" @click="goChat">
            <span class="svc-icon">
              <svg viewBox="0 0 48 48">
                <path d="M8 12a4 4 0 0 1 4-4h18a4 4 0 0 1 4 4v12a4 4 0 0 1-4 4H17l-7 6v-6h-2a4 4 0 0 1-0-4z" fill="rgba(255,255,255,.96)" />
                <path d="M22 30h12a4 4 0 0 0 4-4V16l4 3v11a4 4 0 0 1-4 4h-9l-7 5v-9z" fill="rgba(255,255,255,.55)" />
                <text x="20.5" y="22.5" text-anchor="middle" font-size="11" font-weight="700" fill="#4a7cf0">?</text>
              </svg>
            </span>
            <div class="svc-text">
              <div class="svc-title">知识问答</div>
              <div class="svc-desc">专业宠物知识库，随时解答养宠疑问</div>
            </div>
            <span class="svc-arrow"><el-icon><ArrowRight /></el-icon></span>
          </div>
        </div>
      </section>

      <!-- ============ 四栏数据区 ============ -->
      <section class="dash-grid">
        <!-- 最近识别 -->
        <div class="panel dash-panel">
          <div class="panel-head">
            <span class="head-icon icon-clock">
              <el-icon><Clock /></el-icon>
            </span>
            <span class="head-title">最近识别</span>
            <a class="head-more" @click="goRecords">查看更多 <el-icon><ArrowRight /></el-icon></a>
          </div>
          <div class="recent-list">
            <div class="recent-item" v-for="(r, i) in recentList" :key="i" @click="goRecords">
              <div class="ri-avatar" :class="r.petType === '猫' ? 'cat' : 'dog'">
                <img v-if="r.photo" :src="r.photo" alt="" />
                <span v-else>{{ r.petType === '猫' ? '🐱' : '🐶' }}</span>
              </div>
              <div class="ri-main">
                <div class="ri-breed">{{ r.breed }}</div>
                <div class="ri-sub">
                  <span v-if="r.petName">{{ r.petName }} · </span>{{ r.confidence }}%
                </div>
                <div class="ri-status"><i class="dot">✓</i> 健康状态良好</div>
              </div>
              <div class="ri-right">
                <div class="ri-time">{{ r.time }}</div>
                <el-icon class="ri-arrow"><ArrowRight /></el-icon>
              </div>
            </div>
          </div>
        </div>

        <!-- 宠物成长数据 -->
        <div class="panel dash-panel">
          <div class="panel-head">
            <span class="head-icon icon-chart">
              <el-icon><TrendCharts /></el-icon>
            </span>
            <span class="head-title">宠物成长数据</span>
            <a class="head-more" @click="goArchive">查看详情 <el-icon><ArrowRight /></el-icon></a>
          </div>

          <div class="pet-tabs">
            <span
              v-for="p in pets"
              :key="p._key"
              class="pet-tab"
              :class="{ active: p._key === activePetKey }"
              @click="activePetKey = p._key"
            >{{ p.petName }}</span>
          </div>

          <div class="weight-block">
            <div class="block-label">体重趋势（kg）<span class="now-badge">当前 {{ chart.current }}</span></div>
            <svg class="chart" viewBox="0 0 320 132" preserveAspectRatio="none">
              <defs>
                <linearGradient id="areaFill" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="0%" stop-color="#6d5ce0" stop-opacity="0.22" />
                  <stop offset="100%" stop-color="#6d5ce0" stop-opacity="0.02" />
                </linearGradient>
              </defs>
              <line
                v-for="(t, i) in chart.ticks"
                :key="'g' + i"
                :x1="chart.padL" :x2="320 - chart.padR"
                :y1="t.y" :y2="t.y"
                stroke="#eef1f7" stroke-width="1"
              />
              <text
                v-for="(t, i) in chart.ticks"
                :key="'t' + i"
                :x="chart.padL - 8" :y="t.y + 3.5"
                text-anchor="end" font-size="9" fill="#a7afc2"
              >{{ t.v }}</text>
              <path :d="chart.area" fill="url(#areaFill)" />
              <path :d="chart.line" fill="none" stroke="#6d5ce0" stroke-width="2.2" stroke-linecap="round" />
              <circle
                v-for="(pt, i) in chart.points"
                :key="'p' + i"
                :cx="pt.x" :cy="pt.y"
                :r="i === chart.points.length - 1 ? 4.2 : 2.8"
                :fill="i === chart.points.length - 1 ? '#6d5ce0' : '#fff'"
                stroke="#6d5ce0" stroke-width="1.8"
              />
              <text
                v-for="(lb, i) in chart.labels"
                :key="'l' + i"
                :x="lb.x" :y="124"
                text-anchor="middle" font-size="9" fill="#a7afc2"
              >{{ lb.text }}</text>
            </svg>
          </div>

          <div class="recognize-block">
            <div class="block-label">识别次数 <span class="month-tag">本月</span></div>
            <div class="ring-row">
              <div class="ring">
                <svg viewBox="0 0 86 86">
                  <circle cx="43" cy="43" r="34" fill="none" stroke="#eef0f8" stroke-width="9" />
                  <circle
                    cx="43" cy="43" r="34" fill="none"
                    stroke="#6d5ce0" stroke-width="9" stroke-linecap="round"
                    :stroke-dasharray="ring.dash" stroke-dashoffset="0"
                    transform="rotate(-90 43 43)"
                  />
                </svg>
                <div class="ring-center">
                  <div class="rc-num">{{ recognizeTotal }}<small> 次</small></div>
                  <div class="rc-label">总月</div>
                </div>
              </div>
              <div class="ring-stats">
                <div class="rs-item">
                  <span class="rs-icon rs-bell">🔔</span>
                  <span class="rs-name">健康提醒</span>
                  <span class="rs-val orange">2 条</span>
                </div>
                <div class="rs-item">
                  <span class="rs-icon rs-shot">💉</span>
                  <span class="rs-name">疫苗接种</span>
                  <span class="rs-val purple">1 条</span>
                </div>
                <div class="rs-item">
                  <span class="rs-icon rs-worm">🛡️</span>
                  <span class="rs-name">驱虫记录</span>
                  <span class="rs-val gray">0 条</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 今日宠物资讯 -->
        <div class="panel dash-panel">
          <div class="panel-head">
            <span class="head-icon icon-book">
              <el-icon><Reading /></el-icon>
            </span>
            <span class="head-title">今日宠物资讯</span>
            <a class="head-more" @click="goInfo">查看更多 <el-icon><ArrowRight /></el-icon></a>
          </div>
          <div class="news-list">
            <div class="news-item" v-for="(n, i) in newsList" :key="n.id ?? 'news-' + i" @click="goInfoDetail(n.id)">
              <div class="ni-cover">
                <img v-if="n.cover" :src="n.cover" alt="" />
                <span v-else>🐾</span>
              </div>
              <div class="ni-main">
                <div class="ni-title">{{ n.title }}</div>
                <div class="ni-desc">{{ n.desc }}</div>
                <div class="ni-foot">
                  <span class="ni-date">{{ n.date }}</span>
                  <span class="ni-tag" :style="tagStyle(n.category)">{{ n.category || '养宠知识' }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 待办提醒 -->
        <div class="panel dash-panel">
          <div class="panel-head">
            <span class="head-icon icon-bell">
              <el-icon><Bell /></el-icon>
            </span>
            <span class="head-title">待办提醒</span>
            <a class="head-more" @click="goRecords">查看全部 <el-icon><ArrowRight /></el-icon></a>
          </div>
          <div class="todo-list">
            <div class="todo-item" v-for="(t, i) in todos" :key="i">
              <span class="td-icon" :class="t.iconClass">{{ t.icon }}</span>
              <div class="td-main">
                <div class="td-title">{{ t.title }}</div>
                <div class="td-sub">{{ t.petName }} · {{ t.detail }}</div>
                <div class="td-date">{{ t.date }}</div>
              </div>
              <div class="td-right">
                <span class="td-tag">未完成</span>
                <el-icon class="td-arrow"><ArrowRight /></el-icon>
              </div>
            </div>
          </div>
        </div>
      </section>
    </div>

    <!-- ============ 页脚 ============ -->
    <footer class="footer">
      <div class="footer-inner">
        <div class="f-brand">
          <span class="f-logo">
            <svg viewBox="0 0 48 48">
              <ellipse cx="24" cy="30" rx="11" ry="9" />
              <circle cx="12" cy="20" r="4.6" />
              <circle cx="21" cy="13.5" r="4.6" />
              <circle cx="27" cy="13.5" r="4.6" />
              <circle cx="36" cy="20" r="4.6" />
            </svg>
          </span>
          <span class="f-name">宠知AI</span>
          <span class="f-slogan">用 AI 连接人与宠物，让每一只宠物都被温柔以待</span>
        </div>
        <div class="f-links">
          <span>关于我们</span>
          <i>|</i>
          <span>帮助中心</span>
          <i>|</i>
          <span>联系我们</span>
          <i class="sep-dot">·</i>
          <span>© 2025 宠知AI</span>
          <i class="sep-dot">·</i>
          <span>让科技关爱宠物</span>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  Camera, ArrowRight, ArrowDown, Plus, Clock, TrendCharts, Reading, Bell
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { listMyArchives } from '@/api/archive'
import { myRecords } from '@/api/recognition'
import { listInfo } from '@/api/info'

const router = useRouter()
const userStore = useUserStore()
const API_BASE = import.meta.env.VITE_API_BASE || '/api'

/* ========== 图片资源（AI 生成） ========== */
const IMG = {
  hero: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=' +
    encodeURIComponent('Adorable golden retriever dog and grey tabby white cat sitting close together, bright modern living room with green plants and big window, soft natural sunlight, warm bokeh background, professional pet photography') +
    '&image_size=landscape_16_9',
  dog: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=' +
    encodeURIComponent('Happy golden retriever dog head portrait with tongue out smiling, soft cream studio background, professional pet photography, square avatar') +
    '&image_size=square_hd',
  cat: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=' +
    encodeURIComponent('Cute grey and white tabby Chinese domestic cat portrait with green eyes, soft neutral background, professional pet photography, square avatar') +
    '&image_size=square_hd',
  bichon: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=' +
    encodeURIComponent('Adorable white bichon frise dog portrait smiling, soft light blue pastel background, professional pet photography, square avatar') +
    '&image_size=square_hd',
  newsDog: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=' +
    encodeURIComponent('Golden retriever dog health examination at veterinary clinic, veterinarian checking the dog, warm professional scene') +
    '&image_size=landscape_4_3',
  newsCat: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=' +
    encodeURIComponent('Owner brushing fluffy tabby cat fur at home, cat shedding hair care, bright cozy living room') +
    '&image_size=landscape_4_3',
  newsVaccine: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=' +
    encodeURIComponent('Veterinarian giving vaccine injection to a small dog at modern clinic, pet vaccination care, professional') +
    '&image_size=landscape_4_3'
}

/* ========== 演示兜底数据（未登录 / 接口为空时保证高保真呈现） ========== */
const demoPets = [
  { _key: 'demo-1', petName: '豆豆', breed: '金毛寻回犬', petType: '狗', gender: '公', age: '2岁 3个月', photoUrl: IMG.dog, lastTime: '2025-06-08 14:32' },
  { _key: 'demo-2', petName: '咪咪', breed: '中华田园猫', petType: '猫', gender: '母', age: '1岁 8个月', photoUrl: IMG.cat, lastTime: '2025-06-07 16:20' },
  { _key: 'demo-3', petName: '乐乐', breed: '比熊犬', petType: '狗', gender: '公', age: '3岁 2个月', photoUrl: IMG.bichon, lastTime: '2025-06-05 11:17' }
]

const demoRecent = [
  { breed: '金毛寻回犬', petName: '豆豆', confidence: '98.7', time: '2025-06-08 14:32', petType: '狗', photo: IMG.dog },
  { breed: '中华田园猫', petName: '咪咪', confidence: '96.3', time: '2025-06-07 16:20', petType: '猫', photo: IMG.cat },
  { breed: '比熊犬', petName: '乐乐', confidence: '95.6', time: '2025-06-05 11:17', petType: '狗', photo: IMG.bichon }
]

const demoNews = [
  { id: null, title: '金毛犬的常见健康问题及预防措施', desc: '了解这些，帮助您的爱犬更健康长寿', category: '养护知识', date: '2025-06-08', cover: IMG.newsDog },
  { id: null, title: '猫咪为什么会掉毛？如何科学应对', desc: '掌握护理技巧，减少猫咪掉毛困扰', category: '猫咪百科', date: '2025-06-07', cover: IMG.newsCat },
  { id: null, title: '宠物疫苗接种的重要性', desc: '定期接种疫苗，远离传染病风险', category: '养宠指南', date: '2025-06-06', cover: IMG.newsVaccine }
]

const pets = ref(demoPets.map(p => ({ ...p })))
const recentList = ref(demoRecent.map(r => ({ ...r })))
const newsList = ref(demoNews.map(n => ({ ...n })))
const recognizeTotal = ref(28)
const activePetKey = ref('demo-1')

const displayPets = computed(() => pets.value.slice(0, 3))

/* ========== 待办提醒（无后端，取宠物名拼装；兜底为演示数据） ========== */
const todoTemplates = [
  { icon: '💉', iconClass: 'td-purple', title: '疫苗接种', detail: '狂犬疫苗', date: '2025-06-15' },
  { icon: '🌀', iconClass: 'td-orange', title: '驱虫提醒', detail: '内驱 + 外驱', date: '2025-06-20' },
  { icon: '🩺', iconClass: 'td-blue', title: '体检预约', detail: '常规体检', date: '2025-07-10' }
]
const todos = computed(() =>
  todoTemplates.map((t, i) => ({
    ...t,
    petName: pets.value[i] ? pets.value[i].petName : demoPets[i].petName
  }))
)

/* ========== 成长曲线图 ========== */
const monthLabels = ['05-01', '05-08', '05-15', '05-22', '05-29', '06-08']
const growthProfiles = {
  bigDog: { min: 25, max: 40, current: '32.5kg', points: [29.6, 30.5, 30.2, 31.1, 30.8, 32.5] },
  cat: { min: 3, max: 6, current: '4.2kg', points: [3.6, 3.8, 3.7, 4.0, 4.1, 4.2] },
  smallDog: { min: 4, max: 7, current: '5.6kg', points: [4.8, 5.0, 5.2, 5.1, 5.4, 5.6] }
}

function smoothPath(pts) {
  if (pts.length < 2) return ''
  let d = `M ${pts[0].x} ${pts[0].y}`
  for (let i = 0; i < pts.length - 1; i++) {
    const p0 = pts[i - 1] || pts[i]
    const p1 = pts[i]
    const p2 = pts[i + 1]
    const p3 = pts[i + 2] || pts[i + 1]
    const c1x = p1.x + (p2.x - p0.x) / 6
    const c1y = p1.y + (p2.y - p0.y) / 6
    const c2x = p2.x - (p3.x - p1.x) / 6
    const c2y = p2.y - (p3.y - p1.y) / 6
    d += ` C ${c1x.toFixed(1)} ${c1y.toFixed(1)} ${c2x.toFixed(1)} ${c2y.toFixed(1)} ${p2.x} ${p2.y}`
  }
  return d
}

const chart = computed(() => {
  const pet = pets.value.find(p => p._key === activePetKey.value) || pets.value[0]
  let profile
  if (pet.petName === '咪咪') profile = growthProfiles.cat
  else if (pet.petName === '乐乐') profile = growthProfiles.smallDog
  else if (pet.petType === '猫') profile = growthProfiles.cat
  else profile = growthProfiles.bigDog

  const W = 320, H = 132, padL = 30, padR = 12, padT = 12, padB = 24
  const innerW = W - padL - padR
  const innerH = H - padT - padB
  const n = profile.points.length
  const points = profile.points.map((v, i) => ({
    x: padL + innerW * i / (n - 1),
    y: padT + innerH - (v - profile.min) / (profile.max - profile.min) * innerH
  }))
  const line = smoothPath(points)
  const area = `${line} L ${points[n - 1].x} ${padT + innerH} L ${points[0].x} ${padT + innerH} Z`
  const ticks = [0, 1, 2, 3].map(i => {
    const v = profile.min + (profile.max - profile.min) * i / 3
    return { v: Math.round(v * 10) / 10, y: padT + innerH - innerH * i / 3 }
  })
  const labels = monthLabels.map((text, i) => ({
    text,
    x: padL + innerW * i / (n - 1)
  }))
  return { ...profile, points, line, area, ticks, labels, padL, padR }
})

const ring = computed(() => {
  const r = 34
  const c = 2 * Math.PI * r
  return { dash: `${c * 0.78} ${c}` }
})

/* ========== 工具方法 ========== */
function genderSymbol(g) {
  if (g === '公') return '♂'
  if (g === '母') return '♀'
  return ''
}
function genderClass(g) {
  if (g === '公') return 'male'
  if (g === '母') return 'female'
  return ''
}
function resolveImg(url) {
  if (!url) return ''
  if (url.startsWith('http')) return url
  return API_BASE + url
}
function resolvePetPhoto(p) {
  return resolveImg(p.photoUrl) || (p.petType === '猫' ? IMG.cat : IMG.dog)
}
function formatTime(t) {
  if (!t) return ''
  return String(t).replace('T', ' ').substring(0, 16)
}
function stripHtml(html) {
  if (!html) return ''
  return html.replace(/<[^>]+>/g, '').replace(/&nbsp;/g, ' ').trim()
}

const tagPalette = [
  { bg: '#e9f9f1', color: '#18a558' },
  { bg: '#e6f7f4', color: '#0ea5a4' },
  { bg: '#eef0ff', color: '#667eea' },
  { bg: '#fff4e8', color: '#f08946' }
]
function tagStyle(name) {
  const s = String(name || '').split('').reduce((acc, ch) => acc + ch.charCodeAt(0), 0)
  return tagPalette[s % tagPalette.length]
}

/* ========== 路由跳转 ========== */
function goRecognize() {
  if (!userStore.token) return router.push('/login')
  router.push('/recognition')
}
function goArchive() {
  if (!userStore.token) return router.push('/login')
  router.push('/archive')
}
function viewArchive(p) {
  if (!userStore.token) return router.push('/login')
  if (p.id) router.push(`/archive/${p.id}`)
  else router.push('/archive')
}
function goRecords() {
  if (!userStore.token) return router.push('/login')
  router.push('/my-records')
}
function goChat() {
  if (!userStore.token) return router.push('/login')
  router.push('/chat')
}
function goInfo() {
  router.push('/info')
}
function goInfoDetail(id) {
  if (id === null || id === undefined) return router.push('/info')
  router.push(`/info/${id}`)
}

/* ========== 数据加载 ========== */
async function loadPets() {
  if (!userStore.token) return
  try {
    const res = await listMyArchives()
    const list = res.data || []
    if (Array.isArray(list) && list.length > 0) {
      pets.value = list.slice(0, 3).map((p, i) => ({
        ...p,
        _key: 'real-' + p.id,
        lastTime: formatTime(p.updateTime || p.createTime)
      }))
      activePetKey.value = pets.value[0]._key
    }
  } catch (e) {
    // 保留演示数据
  }
}

async function loadRecent() {
  if (!userStore.token) return
  try {
    const res = await myRecords({ page: 1, size: 3 })
    const page = res.data || {}
    const list = page.list || []
    if (page.total) recognizeTotal.value = page.total
    if (Array.isArray(list) && list.length > 0) {
      recentList.value = list.map(r => ({
        breed: r.breed || '未知品种',
        petName: '',
        confidence: r.confidence != null ? Number(r.confidence).toFixed(1) : '--',
        time: formatTime(r.createTime),
        petType: r.petType,
        photo: resolveImg(r.imageUrl)
      }))
    }
  } catch (e) {
    // 保留演示数据
  }
}

async function loadNews() {
  try {
    const res = await listInfo({ page: 1, size: 3 })
    const list = (res.data && res.data.list) || []
    if (list.length > 0) {
      newsList.value = list.map(item => ({
        id: item.id,
        title: item.title,
        desc: stripHtml(item.content).slice(0, 22) + '…',
        category: item.category || '养宠知识',
        date: formatTime(item.createTime).slice(0, 10),
        cover: resolveImg(item.coverImage)
      }))
    }
  } catch (e) {
    // 保留演示数据
  }
}

onMounted(() => {
  loadPets()
  loadRecent()
  loadNews()
})
</script>

<style scoped>
.home {
  min-height: calc(100vh - 64px);
  background: linear-gradient(180deg, #f5f7fc 0%, #eef1f8 100%);
}
.home-inner {
  width: 100%;
  margin: 0;
  padding: 18px clamp(16px, 2vw, 32px) 0;
}

/* ================= Hero ================= */
.hero-card {
  position: relative;
  height: 340px;
  border-radius: 20px;
  overflow: hidden;
  background: linear-gradient(115deg, #ffffff 38%, #edf3ff 72%, #e7ecff 100%);
  box-shadow: 0 6px 24px rgba(41, 61, 122, 0.07);
}
.blob {
  position: absolute;
  z-index: 0;
  pointer-events: none;
}
.blob-tl {
  width: 280px;
  height: 280px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(187, 196, 255, 0.55), rgba(187, 196, 255, 0) 70%);
  top: -110px;
  left: -80px;
}
.blob-cyan {
  width: 340px;
  height: 170px;
  border-radius: 50%;
  background: radial-gradient(ellipse, rgba(143, 224, 213, 0.75), rgba(143, 224, 213, 0) 70%);
  bottom: -70px;
  right: 240px;
}
.blob-br {
  width: 250px;
  height: 250px;
  border-radius: 50%;
  background: linear-gradient(135deg, #9b8cff 0%, #7a6cf5 100%);
  bottom: -100px;
  right: -70px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.blob-paw {
  width: 90px;
  fill: rgba(255, 255, 255, 0.55);
  transform: translateY(-30px) rotate(-12deg);
}
.paw-orange {
  position: absolute;
  width: 46px;
  left: 92px;
  bottom: 30px;
  fill: #f5b971;
  opacity: 0.5;
  transform: rotate(-18deg);
  z-index: 0;
}
.sparkle {
  position: absolute;
  z-index: 1;
  color: #b9c3ff;
  pointer-events: none;
}
.sp-1 {
  right: 300px;
  top: 168px;
  font-size: 20px;
}
.sp-2 {
  right: 36px;
  top: 150px;
  font-size: 14px;
  opacity: 0.8;
}

.hero-left {
  position: relative;
  z-index: 2;
  padding: 52px 0 0 46px;
  max-width: 52%;
}
.hero-badge {
  display: inline-block;
  padding: 6px 16px;
  background: linear-gradient(135deg, #eef0ff, #e6e9ff);
  color: #6d5ce0;
  border-radius: 999px;
  font-size: 13px;
  font-weight: 600;
  box-shadow: 0 2px 8px rgba(109, 92, 224, 0.12);
}
.hero-left h1 {
  margin: 18px 0 0;
  font-size: 40px;
  line-height: 1.28;
  font-weight: 800;
  color: #202a4a;
  letter-spacing: 1px;
}
.hero-desc {
  margin: 16px 0 0;
  font-size: 15px;
  color: #6b7390;
  letter-spacing: 0.5px;
}
.hero-btns {
  margin-top: 26px;
  display: flex;
  gap: 14px;
}
.btn-main,
.btn-outline {
  display: inline-flex;
  align-items: center;
  gap: 7px;
  height: 46px;
  padding: 0 26px;
  border-radius: 999px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.22s;
}
.btn-main {
  background: linear-gradient(135deg, #6d7bf0, #6a5bd8);
  color: #fff;
  border: none;
  box-shadow: 0 8px 20px rgba(106, 91, 216, 0.32);
}
.btn-main:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 26px rgba(106, 91, 216, 0.4);
}
.btn-main .arr {
  font-size: 14px;
}
.btn-outline {
  background: #fff;
  color: #4a5070;
  border: 1.5px solid #d9def0;
}
.btn-outline:hover {
  border-color: #6d7bf0;
  color: #6d5ce0;
  transform: translateY(-2px);
}

.hero-visual {
  position: absolute;
  inset: 0;
  z-index: 1;
}
.hero-photo {
  position: absolute;
  right: 0;
  bottom: 0;
  width: 55%;
  height: 100%;
}
.hero-photo > img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  object-position: center 22%;
  mask-image: linear-gradient(90deg, rgba(0, 0, 0, 0) 0%, rgba(0, 0, 0, 0.5) 12%, #000 24%);
  -webkit-mask-image: linear-gradient(90deg, rgba(0, 0, 0, 0) 0%, rgba(0, 0, 0, 0.5) 12%, #000 24%);
}

/* 手写标注 */
.note {
  position: absolute;
  left: 43%;
  top: 56px;
  z-index: 3;
  transform: rotate(-8deg);
  text-align: center;
}
.note span {
  font-family: 'STKaiti', 'KaiTi', 'Kaiti SC', cursive;
  font-size: 19px;
  line-height: 1.35;
  color: #46506e;
  letter-spacing: 2px;
}
.note-arrow {
  width: 78px;
  height: 34px;
  margin: 0 auto;
  display: block;
}

/* 扫描四角框 */
.scan-frame {
  position: absolute;
  width: 230px;
  height: 230px;
  left: 46%;
  top: 12%;
  z-index: 2;
}
.corner {
  position: absolute;
  width: 30px;
  height: 30px;
  filter: drop-shadow(0 1px 3px rgba(60, 70, 120, 0.35));
}
.corner.tl { top: 0; left: 0; border-top: 4px solid #fff; border-left: 4px solid #fff; border-top-left-radius: 8px; }
.corner.tr { top: 0; right: 0; border-top: 4px solid #fff; border-right: 4px solid #fff; border-top-right-radius: 8px; }
.corner.bl { bottom: 0; left: 0; border-bottom: 4px solid #fff; border-left: 4px solid #fff; border-bottom-left-radius: 8px; }
.corner.br { bottom: 0; right: 0; border-bottom: 4px solid #fff; border-right: 4px solid #fff; border-bottom-right-radius: 8px; }

/* AI 结果浮卡 */
.ai-result {
  position: absolute;
  top: 24px;
  right: 26px;
  z-index: 4;
  background: rgba(255, 255, 255, 0.96);
  border-radius: 14px;
  padding: 12px 16px;
  box-shadow: 0 12px 32px rgba(41, 61, 122, 0.16);
  min-width: 232px;
}
.ar-head {
  display: flex;
  align-items: center;
  gap: 9px;
}
.ar-paw {
  width: 34px;
  height: 34px;
  border-radius: 10px;
  background: linear-gradient(135deg, #6d7bf0, #6a5bd8);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.ar-paw svg {
  width: 20px;
  fill: #fff;
}
.ar-name {
  font-size: 16px;
  font-weight: 700;
  color: #202a4a;
}
.ar-rate {
  margin-left: auto;
  font-size: 17px;
  font-weight: 800;
  color: #6d5ce0;
}
.ar-tags {
  display: flex;
  align-items: center;
  gap: 7px;
  margin-top: 9px;
}
.ar-chevron {
  width: 20px;
  height: 20px;
  border-radius: 6px;
  background: #f2f4fa;
  color: #8a93a6;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
}
.ar-tag {
  font-size: 11.5px;
  color: #5b6480;
  background: #f2f4fa;
  border-radius: 6px;
  padding: 3px 9px;
}

/* 健康浮卡 */
.health-float {
  position: absolute;
  bottom: 28px;
  right: 26px;
  z-index: 4;
  background: rgba(255, 255, 255, 0.96);
  border-radius: 14px;
  padding: 11px 16px 11px 11px;
  display: flex;
  align-items: center;
  gap: 10px;
  box-shadow: 0 12px 32px rgba(41, 61, 122, 0.16);
}
.hf-icon {
  width: 38px;
  height: 38px;
  border-radius: 50%;
  background: linear-gradient(135deg, #2dd4a0, #12b079);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.hf-icon svg {
  width: 20px;
  fill: #fff;
}
.hf-title {
  font-size: 14px;
  font-weight: 700;
  color: #0f9d69;
}
.hf-sub {
  font-size: 12px;
  color: #7a8299;
  margin-top: 2px;
}

/* ================= 通用面板 ================= */
.panel {
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 4px 18px rgba(41, 61, 122, 0.06);
  padding: 18px 22px;
  margin-top: 16px;
}
.panel-head {
  display: flex;
  align-items: center;
  gap: 9px;
  margin-bottom: 14px;
}
.head-icon {
  width: 30px;
  height: 30px;
  border-radius: 9px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.head-icon svg {
  width: 18px;
  height: 18px;
}
.head-icon .el-icon {
  font-size: 16px;
  color: #fff;
}
.icon-paw {
  background: linear-gradient(135deg, #6d7bf0, #6a5bd8);
}
.icon-paw svg { fill: #fff; }
.icon-spark {
  background: linear-gradient(135deg, #5b8def, #4a73e0);
}
.icon-spark svg { fill: #fff; }
.icon-clock { background: linear-gradient(135deg, #6d7bf0, #6a5bd8); }
.icon-chart { background: linear-gradient(135deg, #5b8def, #4a73e0); }
.icon-book { background: linear-gradient(135deg, #22c59e, #12b079); }
.icon-bell { background: linear-gradient(135deg, #6d7bf0, #6a5bd8); }
.head-title {
  font-size: 16.5px;
  font-weight: 700;
  color: #202a4a;
}
.head-sub {
  font-size: 13px;
  color: #9aa2b6;
}
.head-more {
  margin-left: auto;
  font-size: 13px;
  color: #8a93a6;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 2px;
  transition: color 0.2s;
  white-space: nowrap;
}
.head-more:hover { color: #6d5ce0; }
.head-more .el-icon { font-size: 13px; }

/* ================= 我的宠物 ================= */
.pet-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}
.pet-card {
  position: relative;
  display: flex;
  gap: 12px;
  padding: 14px;
  border-radius: 14px;
  border: 1px solid #edf0f7;
  background: linear-gradient(180deg, #ffffff 0%, #fafbff 100%);
  transition: all 0.22s;
}
.pet-card:hover {
  border-color: #d7def8;
  box-shadow: 0 10px 26px rgba(41, 61, 122, 0.1);
  transform: translateY(-3px);
}
.pc-photo {
  width: 74px;
  height: 74px;
  border-radius: 12px;
  overflow: hidden;
  flex-shrink: 0;
  background: #eef1f8;
}
.pc-photo img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.pc-info {
  min-width: 0;
  flex: 1;
  padding-right: 52px;
}
.pc-name-row {
  display: flex;
  align-items: center;
  gap: 5px;
}
.pc-name {
  font-size: 16px;
  font-weight: 700;
  color: #202a4a;
}
.pc-gender {
  font-size: 14px;
  font-weight: 700;
}
.pc-gender.male { color: #4a7cf0; }
.pc-gender.female { color: #f06292; }
.pc-breed {
  font-size: 13px;
  color: #5b6480;
  margin-top: 3px;
}
.pc-age {
  font-size: 13px;
  color: #5b6480;
  margin-top: 2px;
}
.pc-time {
  font-size: 11.5px;
  color: #a7afc2;
  margin-top: 5px;
}
.pc-health {
  position: absolute;
  top: 12px;
  right: 12px;
  font-size: 11.5px;
  font-weight: 600;
  color: #12b079;
  background: #e8f9f2;
  border-radius: 6px;
  padding: 3px 8px;
}
.pc-health i {
  font-style: normal;
  margin-right: 1px;
}
.pc-btn {
  position: absolute;
  right: 12px;
  bottom: 12px;
  border: none;
  background: linear-gradient(135deg, #6d7bf0, #6a5bd8);
  color: #fff;
  font-size: 12.5px;
  font-weight: 600;
  border-radius: 999px;
  padding: 7px 16px;
  cursor: pointer;
  transition: all 0.2s;
}
.pc-btn:hover {
  box-shadow: 0 6px 14px rgba(106, 91, 216, 0.35);
  transform: translateY(-1px);
}
.pet-add {
  border: 1.6px dashed #cfd6ee;
  border-radius: 14px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.22s;
  background: rgba(248, 250, 255, 0.6);
}
.pet-add:hover {
  border-color: #6d7bf0;
  background: #f4f6ff;
}
.pa-plus {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #6d7bf0, #6a5bd8);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  box-shadow: 0 6px 14px rgba(106, 91, 216, 0.3);
}
.pa-title {
  margin-top: 10px;
  font-size: 15px;
  font-weight: 700;
  color: #4a5070;
}
.pa-sub {
  margin-top: 3px;
  font-size: 12px;
  color: #9aa2b6;
}

/* ================= 智能服务 ================= */
.service-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}
.service-card {
  display: flex;
  align-items: center;
  gap: 13px;
  padding: 17px 18px;
  border-radius: 14px;
  cursor: pointer;
  transition: all 0.22s;
}
.service-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 10px 24px rgba(41, 61, 122, 0.1);
}
.svc-purple { background: linear-gradient(135deg, #f1efff, #e9e6ff); }
.svc-green { background: linear-gradient(135deg, #e8f9f1, #ddf6ea); }
.svc-orange { background: linear-gradient(135deg, #fff1eb, #ffe9df); }
.svc-blue { background: linear-gradient(135deg, #ecf3ff, #e2edff); }
.svc-icon {
  width: 52px;
  height: 52px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.svc-purple .svc-icon { background: linear-gradient(135deg, #7c6cf5, #6a55e0); box-shadow: 0 6px 14px rgba(106, 91, 216, 0.3); }
.svc-green .svc-icon { background: linear-gradient(135deg, #2dd4a0, #12b079); box-shadow: 0 6px 14px rgba(18, 176, 121, 0.3); }
.svc-orange .svc-icon { background: linear-gradient(135deg, #ff8a5c, #f56a3d); box-shadow: 0 6px 14px rgba(245, 106, 61, 0.3); }
.svc-blue .svc-icon { background: linear-gradient(135deg, #5b93f5, #3f74e8); box-shadow: 0 6px 14px rgba(63, 116, 232, 0.3); }
.svc-icon svg { width: 40px; height: 40px; }
.svc-text { flex: 1; min-width: 0; }
.svc-title {
  font-size: 15.5px;
  font-weight: 700;
  color: #202a4a;
}
.svc-desc {
  font-size: 12px;
  color: #7a8299;
  margin-top: 4px;
  line-height: 1.45;
}
.svc-arrow {
  width: 26px;
  height: 26px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.75);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #9aa2b6;
  flex-shrink: 0;
}
.svc-arrow .el-icon { font-size: 13px; }

/* ================= 四栏布局 ================= */
.dash-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-top: 16px;
  padding-bottom: 8px;
}
.dash-panel {
  margin-top: 0;
  padding: 16px 18px;
}
.dash-panel .panel-head {
  margin-bottom: 6px;
}
.dash-panel .head-icon {
  width: 26px;
  height: 26px;
  border-radius: 8px;
}
.dash-panel .head-title {
  font-size: 15px;
}

/* 最近识别 */
.recent-item {
  display: flex;
  align-items: center;
  gap: 11px;
  padding: 11px 6px;
  border-bottom: 1px dashed #eef1f7;
  cursor: pointer;
  border-radius: 8px;
  transition: background 0.2s;
}
.recent-item:last-child { border-bottom: none; }
.recent-item:hover { background: #f7f9ff; }
.ri-avatar {
  width: 44px;
  height: 44px;
  border-radius: 11px;
  overflow: hidden;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}
.ri-avatar.dog { background: linear-gradient(135deg, #ffe7c2, #ffd59e); }
.ri-avatar.cat { background: linear-gradient(135deg, #d6f3e4, #bcebd4); }
.ri-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.ri-main { flex: 1; min-width: 0; }
.ri-breed {
  font-size: 13.5px;
  font-weight: 700;
  color: #202a4a;
}
.ri-sub {
  font-size: 12px;
  color: #8a93a6;
  margin-top: 2px;
}
.ri-status {
  font-size: 11px;
  color: #12b079;
  margin-top: 3px;
}
.ri-status .dot {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: #22c59e;
  color: #fff;
  font-size: 8px;
  font-style: normal;
  margin-right: 3px;
}
.ri-right {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 8px;
  flex-shrink: 0;
}
.ri-time {
  font-size: 11px;
  color: #a7afc2;
}
.ri-arrow {
  font-size: 13px;
  color: #c3cad9;
}

/* 成长数据 */
.pet-tabs {
  display: flex;
  gap: 8px;
  margin: 8px 0 4px;
}
.pet-tab {
  padding: 5px 15px;
  border-radius: 999px;
  font-size: 12.5px;
  color: #5b6480;
  background: #f2f4fa;
  cursor: pointer;
  transition: all 0.2s;
}
.pet-tab.active {
  color: #fff;
  background: linear-gradient(135deg, #6d7bf0, #6a5bd8);
  font-weight: 600;
  box-shadow: 0 4px 10px rgba(106, 91, 216, 0.28);
}
.block-label {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  font-weight: 700;
  color: #3b4358;
  margin: 10px 0 2px;
}
.now-badge {
  margin-left: auto;
  font-size: 11px;
  font-weight: 500;
  color: #6d5ce0;
  background: #eef0ff;
  border-radius: 6px;
  padding: 3px 9px;
}
.chart {
  width: 100%;
  height: 132px;
  display: block;
}
.month-tag {
  font-size: 11px;
  font-weight: 500;
  color: #8a93a6;
  background: #f2f4fa;
  border-radius: 6px;
  padding: 2px 8px;
}
.ring-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 8px;
}
.ring {
  position: relative;
  width: 86px;
  height: 86px;
  flex-shrink: 0;
}
.ring svg {
  width: 100%;
  height: 100%;
}
.ring-center {
  position: absolute;
  inset: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}
.rc-num {
  font-size: 19px;
  font-weight: 800;
  color: #202a4a;
  line-height: 1.1;
}
.rc-num small {
  font-size: 11px;
  font-weight: 600;
}
.rc-label {
  font-size: 10px;
  color: #a7afc2;
  margin-top: 1px;
}
.ring-stats {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 7px;
}
.rs-item {
  display: flex;
  align-items: center;
  gap: 7px;
  font-size: 12px;
}
.rs-icon {
  width: 24px;
  height: 24px;
  border-radius: 7px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
}
.rs-bell { background: #fff1eb; }
.rs-shot { background: #eef0ff; }
.rs-worm { background: #e9f9f1; }
.rs-name {
  color: #5b6480;
}
.rs-val {
  margin-left: auto;
  font-weight: 700;
}
.rs-val.orange { color: #f5853d; }
.rs-val.purple { color: #6d5ce0; }
.rs-val.gray { color: #a7afc2; }

/* 资讯 */
.news-item {
  display: flex;
  gap: 11px;
  padding: 10px 6px;
  border-bottom: 1px dashed #eef1f7;
  cursor: pointer;
  border-radius: 8px;
  transition: background 0.2s;
}
.news-item:last-child { border-bottom: none; }
.news-item:hover { background: #f7f9ff; }
.ni-cover {
  width: 66px;
  height: 66px;
  border-radius: 10px;
  overflow: hidden;
  flex-shrink: 0;
  background: linear-gradient(135deg, #dfe8ff, #c9d6ff);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}
.ni-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.ni-main {
  flex: 1;
  min-width: 0;
}
.ni-title {
  font-size: 13.5px;
  font-weight: 700;
  color: #202a4a;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.ni-desc {
  font-size: 11.5px;
  color: #8a93a6;
  margin-top: 3px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.ni-foot {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 6px;
}
.ni-date {
  font-size: 11px;
  color: #a7afc2;
}
.ni-tag {
  font-size: 10.5px;
  border-radius: 5px;
  padding: 2px 7px;
  font-weight: 500;
}

/* 待办提醒 */
.todo-item {
  display: flex;
  align-items: center;
  gap: 11px;
  padding: 11px 6px;
  border-bottom: 1px dashed #eef1f7;
}
.todo-item:last-child { border-bottom: none; }
.td-icon {
  width: 38px;
  height: 38px;
  border-radius: 11px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  flex-shrink: 0;
}
.td-purple { background: #eef0ff; }
.td-orange { background: #fff1eb; }
.td-blue { background: #eaf2ff; }
.td-main { flex: 1; min-width: 0; }
.td-title {
  font-size: 13.5px;
  font-weight: 700;
  color: #202a4a;
}
.td-sub {
  font-size: 11.5px;
  color: #8a93a6;
  margin-top: 2px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.td-date {
  font-size: 11px;
  color: #a7afc2;
  margin-top: 2px;
}
.td-right {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 8px;
  flex-shrink: 0;
}
.td-tag {
  font-size: 11px;
  color: #f08946;
  background: #fff3ea;
  border-radius: 6px;
  padding: 3px 8px;
}
.td-arrow {
  font-size: 13px;
  color: #c3cad9;
}

/* ================= 页脚 ================= */
.footer {
  margin-top: 10px;
}
.footer-inner {
  width: 100%;
  margin: 0;
  padding: 20px clamp(16px, 2vw, 32px);
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  flex-wrap: wrap;
}
.f-brand {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  row-gap: 4px;
  gap: 9px;
  min-width: 0;
}
.f-logo {
  width: 26px;
  height: 26px;
  border-radius: 8px;
  background: linear-gradient(135deg, #6d7bf0, #6a5bd8);
  display: flex;
  align-items: center;
  justify-content: center;
}
.f-logo svg {
  width: 15px;
  fill: #fff;
}
.f-name {
  font-size: 14px;
  font-weight: 700;
  color: #3b4358;
}
.f-slogan {
  font-size: 12px;
  color: #9aa2b6;
  margin-left: 8px;
}
.f-links {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  row-gap: 6px;
  gap: 10px;
  font-size: 12px;
  color: #9aa2b6;
  min-width: 0;
}
.f-links span { cursor: default; }
.f-links i {
  font-style: normal;
  color: #d3d9e6;
}
.f-links .sep-dot { margin-left: 4px; }

/* ================= 响应式 ================= */
@media (max-width: 1180px) {
  .dash-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  .pet-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  .service-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
@media (max-width: 760px) {
  .hero-card {
    height: auto;
  }
  .hero-left {
    max-width: 100%;
    padding: 32px 24px 280px;
  }
  .hero-left h1 { font-size: 30px; }
  .hero-photo {
    width: 100%;
    height: 260px;
  }
  .note, .scan-frame, .sp-1, .paw-orange { display: none; }
  .ai-result { right: 12px; top: auto; bottom: 96px; min-width: 0; }
  .health-float { right: 12px; bottom: 12px; }
  .dash-grid,
  .pet-grid,
  .service-grid {
    grid-template-columns: 1fr;
  }
  .footer-inner {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
