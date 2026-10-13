<template>
  <div class="info-page">
    <div class="info-layout">
      <!-- ============ 左栏：品牌卡 + 菜单 + 装饰 ============ -->
      <aside class="info-side">
        <div class="side-brand">
          <span class="brand-ic" v-html="IC.paw"></span>
          <div class="brand-txt">
            <b>宠物资讯</b>
            <small>科学养宠 · 从这里开始</small>
          </div>
        </div>

        <nav class="side-menu">
          <button
            v-for="m in menus"
            :key="m.key"
            type="button"
            class="menu-item"
            :class="{ on: activeCat === m.key }"
            @click="setCat(m.key, true)"
          >
            <span class="m-ic" v-html="m.icon"></span>
            <span class="m-name">{{ m.name }}</span>
          </button>
        </nav>

        <div class="side-art">
          <img :src="IMG.sideArt" alt="dog and cat line art">
          <p class="hand-note">每一份知识<small>♡</small><br>都是对宠物的爱</p>
        </div>
      </aside>

      <!-- ============ 主区 ============ -->
      <main class="info-main">
        <!-- Hero 轮播 -->
        <section class="hero-swiper">
          <transition name="hero-fade" mode="out-in">
            <div class="hero-slide" :key="bannerIdx">
              <img class="hero-img" :src="banners[bannerIdx].img" alt="hero">
              <div class="hero-text">
                <span class="hero-tag"><span class="ht-ic" v-html="IC.paw"></span>{{ banners[bannerIdx].tag }}</span>
                <h1>{{ banners[bannerIdx].title }}</h1>
                <p>{{ banners[bannerIdx].sub }}</p>
                <button type="button" class="hero-btn" @click="goArticles">探索更多 <span>→</span></button>
              </div>
            </div>
          </transition>

          <p class="hero-hand">更懂宠物 <small>♡</small><br>更懂爱</p>
          <div class="hero-wave" v-html="IC.wave"></div>

          <div class="hero-dots">
            <i
              v-for="(b, i) in banners"
              :key="i"
              :class="{ on: bannerIdx === i }"
              @click="bannerIdx = i"
            ></i>
          </div>
        </section>

        <!-- 分类胶囊 -->
        <section class="cat-bar">
          <button
            v-for="c in cats"
            :key="c.key"
            type="button"
            class="cat-chip"
            :class="{ on: activeCat === c.key }"
            @click="setCat(c.key)"
          >
            <span v-if="c.icon" class="c-dot" :style="activeCat === c.key ? {} : { background: c.bg }">
              <span class="cd-ic" :style="{ color: activeCat === c.key ? '#fff' : c.fg }" v-html="c.icon"></span>
            </span>
            {{ c.name }}
          </button>
        </section>

        <!-- 热门推荐 / 分类文章 -->
        <section class="art-section" ref="artSecRef">
          <div class="sec-head">
            <div class="sh-l">
              <span class="fire-ic" v-html="IC.fire"></span>
              <b>{{ sectionTitle }}</b>
            </div>
            <a v-if="filteredArticles.length > 6 || (isReal && realHasMore)" class="sh-more" @click="toggleMore">
              {{ expanded ? '收起' : '查看更多' }}
              <span :class="{ up: expanded }">›</span>
            </a>
          </div>

          <div v-loading="loading" class="art-grid">
            <article
              v-for="a in shownArticles"
              :key="a.rid"
              class="art-card"
              @click="goDetail(a)"
            >
              <div class="ac-cover">
                <img :src="a.cover" :alt="a.title" loading="lazy">
                <span class="ac-tag" :style="{ background: a.tagColor }">{{ a.tagName }}</span>
              </div>
              <div class="ac-body">
                <h3>{{ a.title }}</h3>
                <p>{{ a.summary }}</p>
                <div class="ac-meta">
                  <span class="am-date"><span class="am-ic" v-html="IC.calendar"></span>{{ a.date }}</span>
                  <span class="am-view"><span class="am-ic" v-html="IC.eye"></span>{{ a.views }}</span>
                </div>
              </div>
            </article>
          </div>

          <!-- 空态 -->
          <div v-if="!loading && filteredArticles.length === 0" class="art-empty">
            <span class="ae-ic" v-html="IC.search"></span>
            <b>该分类下暂时没有资讯</b>
            <small>换个分类看看，更多精彩内容正在赶来～</small>
            <button type="button" class="ae-btn" @click="setCat('all')">查看全部资讯</button>
          </div>
        </section>
      </main>

      <!-- ============ 右栏 ============ -->
      <aside class="info-right">
        <!-- 热门话题 -->
        <section class="r-card">
          <div class="r-head">
            <div class="rh-l">
              <span class="fire-ic sm" v-html="IC.fire"></span>
              <b>热门话题</b>
            </div>
            <a class="r-more" @click="setCat('all', true)">查看更多 <span>›</span></a>
          </div>
          <ul class="topic-list">
            <li v-for="(t, i) in topics" :key="t.title" @click="setCat(t.key, true)">
              <span class="t-no" :class="'n' + (i + 1)">{{ i + 1 }}</span>
              <span class="t-title">{{ t.title }}</span>
              <span class="t-view"><span class="tv-ic" v-html="IC.eye"></span>{{ t.views }}</span>
            </li>
          </ul>
        </section>

        <!-- 推荐阅读 -->
        <section class="r-card">
          <div class="r-head">
            <div class="rh-l">
              <span class="book-ic" v-html="IC.book"></span>
              <b>推荐阅读</b>
            </div>
            <a class="r-more" @click="goArticles">查看更多 <span>›</span></a>
          </div>
          <ul class="read-list">
            <li v-for="r in recommends" :key="r.rid" @click="goDetail(r)">
              <img :src="r.cover" :alt="r.title" loading="lazy">
              <div class="rl-info">
                <b>{{ r.title }}</b>
                <div class="rl-meta">
                  <span><span class="am-ic" v-html="IC.calendar"></span>{{ r.date }}</span>
                  <span><span class="am-ic" v-html="IC.eye"></span>{{ r.views }}</span>
                </div>
              </div>
            </li>
          </ul>
        </section>

        <!-- AI 宠物助手 -->
        <section class="ai-card">
          <div class="ai-head">
            <span class="ai-ic" v-html="IC.robot"></span>
            <div class="ai-txt">
              <b>AI宠物助手</b>
              <small>有任何养宠问题？我来帮你解答！</small>
            </div>
          </div>
          <div class="ai-input">
            <input v-model="askText" type="text" placeholder="请输入你的问题..." @keyup.enter="sendAsk">
            <button type="button" aria-label="发送" @click="sendAsk"><span v-html="IC.send"></span></button>
          </div>
        </section>
      </aside>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { listInfo } from '@/api/info'

const router = useRouter()
const API_BASE = import.meta.env.VITE_API_BASE || '/api'

/* ==================== 内联 SVG 图标 ==================== */
const IC = {
  paw: '<svg viewBox="0 0 24 24"><ellipse cx="12" cy="14.8" rx="4.1" ry="3.3" fill="currentColor"/><circle cx="6.9" cy="10.8" r="1.7" fill="currentColor"/><circle cx="10.1" cy="8" r="1.7" fill="currentColor"/><circle cx="13.9" cy="8" r="1.7" fill="currentColor"/><circle cx="17.1" cy="10.8" r="1.7" fill="currentColor"/></svg>',
  fire: '<svg viewBox="0 0 24 24"><path d="M12 3s4.2 3.7 4.2 7.9a4.2 4.2 0 0 1-8.4 0c0-1.1.4-2.1 1-2.9.1 1 .8 1.6 1.4 1.6C9.9 7.7 12 5.6 12 3Z" fill="currentColor"/><path d="M10.6 16.6c.4.9 1.2 1.4 1.4 1.4s1-.5 1.4-1.4c.2-.5 0-1-.3-1.4-.4.5-.8.7-1.1.7s-.7-.2-1.1-.7c-.3.4-.5.9-.3 1.4Z" fill="#fff" opacity=".55"/></svg>',
  calendar: '<svg viewBox="0 0 24 24"><rect x="4" y="5.5" width="16" height="14.5" rx="2.5" fill="none" stroke="currentColor" stroke-width="1.7"/><path d="M8 3.8v2.8M16 3.8v2.8M4 9.8h16" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round"/></svg>',
  eye: '<svg viewBox="0 0 24 24"><path d="M2.5 12S6 6.6 12 6.6 21.5 12 21.5 12 18 17.4 12 17.4 2.5 12 2.5 12Z" fill="none" stroke="currentColor" stroke-width="1.7" stroke-linejoin="round"/><circle cx="12" cy="12" r="2.3" fill="currentColor"/></svg>',
  search: '<svg viewBox="0 0 24 24"><circle cx="10.8" cy="10.8" r="6.3" fill="none" stroke="currentColor" stroke-width="2"/><path d="M15.6 15.6 20 20" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round"/></svg>',
  book: '<svg viewBox="0 0 24 24"><path d="M5 5.3A2.3 2.3 0 0 1 7.3 3H19v14.2H7.3A2.3 2.3 0 0 0 5 19.5z" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linejoin="round"/><path d="M19 17.2v2.3H7.3A2.3 2.3 0 0 1 5 17.2" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linejoin="round"/><path d="M8.4 7.4h7.2M8.4 10.6h5" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round"/></svg>',
  robot: '<svg viewBox="0 0 24 24"><path d="M12 3.6v2.6M9.6 4.9h4.8" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round"/><rect x="6.4" y="7.8" width="11.2" height="10.6" rx="3.2" fill="currentColor"/><circle cx="9.9" cy="12.6" r="1.35" fill="#fff"/><circle cx="14.1" cy="12.6" r="1.35" fill="#fff"/><path d="M9.8 16h4.4" stroke="#fff" stroke-width="1.5" stroke-linecap="round"/><path d="M3.8 12.6h2.6M17.6 12.6h2.6" fill="none" stroke="currentColor" stroke-width="1.7" stroke-linecap="round"/></svg>',
  send: '<svg viewBox="0 0 24 24"><path d="M5 12l15-7-6 15-3-6-6-2Z" fill="currentColor"/></svg>',
  wave: '<svg class="wave-svg" viewBox="0 0 1440 120" preserveAspectRatio="none"><path d="M0 78 C 200 118 380 40 600 70 C 820 100 980 118 1200 80 C 1320 60 1400 70 1440 76 L1440 120 L0 120 Z" fill="#dcecf7" opacity=".55"/><path d="M0 94 C 240 124 420 70 660 92 C 900 114 1080 120 1280 98 C 1360 90 1410 94 1440 96 L1440 120 L0 120 Z" fill="#cfe6f2" opacity=".6"/></svg>'
}

/* 分类 / 菜单小图标 */
const IC_DOG = '<svg viewBox="0 0 24 24"><circle cx="12" cy="13.4" r="5.6" fill="currentColor"/><path d="M7.6 9.6 6 6.2c-.3-.7.6-1.3 1.2-.8L9.8 8M16.4 9.6 18 6.2c.3-.7-.6-1.3-1.2-.8L14.2 8" fill="currentColor"/><circle cx="9.9" cy="13" r="1" fill="#fff"/><circle cx="14.1" cy="13" r="1" fill="#fff"/><path d="M10.8 15.6h2.4l-1.2 1.4z" fill="#fff"/></svg>'
const IC_CAT = '<svg viewBox="0 0 24 24"><path d="M6.6 8.2 5 4.4l3.6 1.7h6.8L19 4.4l-1.6 3.8c.7 1.3 1 2.8.8 4.4-.4 3.1-3 5.4-6.2 5.4s-5.8-2.3-6.2-5.4c-.2-1.6.1-3.1.8-4.4Z" fill="currentColor"/><circle cx="9.8" cy="13" r=".95" fill="#fff"/><circle cx="14.2" cy="13" r=".95" fill="#fff"/><path d="M11.4 15.6h1.2l-.6.7z" fill="#fff"/></svg>'
const IC_SHIELD = '<svg viewBox="0 0 24 24"><path d="M12 3.2l7 2.6v5.7c0 4.5-3 7.6-7 9.3-4-1.7-7-4.8-7-9.3V5.8z" fill="currentColor"/><path d="M12 8.4v6M9 11.4h6" stroke="#fff" stroke-width="1.8" stroke-linecap="round"/></svg>'
const IC_PLUS = '<svg viewBox="0 0 24 24"><circle cx="12" cy="12" r="8.4" fill="currentColor"/><path d="M12 8.2v7.6M8.2 12h7.6" stroke="#fff" stroke-width="2" stroke-linecap="round"/></svg>'
const IC_GEAR = '<svg viewBox="0 0 24 24"><path d="M12 3.2l1.3 2.2 2.5-.5.7 2.5 2.4 1-.8 2.4.8 2.4-2.4 1-.7 2.5-2.5-.5-1.3 2.2-1.3-2.2-2.5.5-.7-2.5-2.4-1 .8-2.4-.8-2.4 2.4-1 .7-2.5 2.5.5z" fill="currentColor"/><circle cx="12" cy="12" r="3.2" fill="#fff"/></svg>'
const IC_LEAF = '<svg viewBox="0 0 24 24"><path d="M5 19.5C5 11 11 5.5 19.5 4.5 18.5 13 13 19 4.5 20z" fill="currentColor"/><path d="M5 19.5c3.2-4.2 6.4-7.3 10.4-9.6" fill="none" stroke="#fff" stroke-width="1.5" stroke-linecap="round"/></svg>'
const IC_CALENDAR_C = '<svg viewBox="0 0 24 24"><rect x="4.2" y="6" width="15.6" height="14" rx="2.6" fill="currentColor"/><path d="M8 3.8v3.6M16 3.8v3.6M4.2 10.4h15.6" stroke="#fff" stroke-width="1.7" stroke-linecap="round"/><rect x="8" y="13" width="3" height="2.6" rx=".8" fill="#fff"/></svg>'
const IC_STAR = '<svg viewBox="0 0 24 24"><path d="M12 3.4l2.5 5.1 5.6.8-4 4 1 5.6L12 16.3l-5.1 2.6 1-5.6-4-4 5.6-.8z" fill="currentColor"/></svg>'
const IC_KNOW = '<svg viewBox="0 0 24 24"><path d="M5 5.3A2.3 2.3 0 0 1 7.3 3H19v14.2H7.3A2.3 2.3 0 0 0 5 19.5z" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linejoin="round"/><path d="M8.4 7.4h7.2M8.4 10.6h5" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round"/></svg>'
const IC_MEDKIT = '<svg viewBox="0 0 24 24"><rect x="4" y="7" width="16" height="13" rx="2.4" fill="none" stroke="currentColor" stroke-width="1.8"/><path d="M9 7V5.4h6V7M12 10.6v5.8M9.1 13.5h5.8" fill="none" stroke="currentColor" stroke-width="1.7" stroke-linecap="round"/></svg>'
const IC_WRENCH = '<svg viewBox="0 0 24 24"><path d="M15.7 4.4a3.6 3.6 0 0 0-4.7 4.3L5.2 14.5a1.9 1.9 0 0 0 2.6 2.6l5.8-5.8a3.6 3.6 0 0 0 4.3-4.7l-2.3 2.3-2.1-.4-.4-2.1z" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linejoin="round"/></svg>'
const IC_BAG = '<svg viewBox="0 0 24 24"><rect x="5" y="8" width="14" height="12" rx="2.4" fill="none" stroke="currentColor" stroke-width="1.8"/><path d="M9 8a3 3 0 0 1 6 0M5 12h14" fill="none" stroke="currentColor" stroke-width="1.7" stroke-linecap="round"/></svg>'
const IC_TAG = '<svg viewBox="0 0 24 24"><path d="M4 4h6.8L20 13.2 13.2 20 4 10.8z" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linejoin="round"/><circle cx="8.4" cy="8.4" r="1.4" fill="currentColor"/></svg>'

/* ==================== AI 配图 ==================== */
const t2i = (p, s = 'landscape_4_3') =>
  'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=' + encodeURIComponent(p) + '&image_size=' + s
const IMG = {
  heroDogCat: t2i('Adorable golden retriever dog and grey tabby white cat sitting close together, bright modern living room with green plants and big window, soft natural sunlight, warm bokeh background, professional pet photography', 'landscape_16_9'),
  vaccine: t2i('Veterinarian giving vaccine injection to a small dog at modern bright clinic, pet vaccination care, professional pet photography'),
  a1: t2i('Happy golden retriever lying on green grass in sunny park, tongue out smiling, professional pet photography'),
  a2: t2i('Cute tabby cat kneading with paws on soft cozy blanket, close up, warm home interior, professional pet photography'),
  a3: t2i('Golden retriever eating healthy dog food from metal bowl at bright home, pet nutrition diet, professional pet photography'),
  a4: t2i('Veterinarian doctor examining skin of a tabby cat at modern clinic, cat health check, professional pet photography'),
  a5: t2i('Adorable white bichon frise dog walking on fresh spring grass with little flowers, bright sunlight, professional pet photography'),
  a6: t2i('Beautiful blue-eyed ragdoll cat with fluffy light fur portrait, soft cream background, professional pet photography'),
  a7: t2i('Puppy receiving vaccine shot at veterinary clinic, owner comforting, clean modern clinic, professional pet photography'),
  a8: t2i('Smart corgi dog training session at home with owner hand gesture, bright living room, professional pet photography'),
  a9: t2i('Grey tabby cat relaxing in shade with fan in hot summer, cool indoor scene, professional pet photography'),
  r1: t2i('Golden retriever dog health examination at veterinary clinic, veterinarian checking the dog with stethoscope, warm professional scene'),
  r2: t2i('Grey tabby cat stretching and showing body language on cozy sofa, bright living room, professional pet photography', 'square_hd'),
  r3: t2i('Calm golden retriever dog resting head on paws looking peaceful, soft indoor light, professional pet photography', 'square_hd'),
  sideArt: t2i('minimalist continuous line art drawing of a cute dog and cat sitting together, light lavender blue and mint gradient background, simple thin outline illustration, lots of empty space', 'square_hd')
}

/* ==================== 分类体系 ==================== */
const TAG_META = {
  knowledge: { name: '养宠知识', color: '#4c7ef0' },
  training: { name: '行为训练', color: '#8b5cf6' },
  diet: { name: '饮食营养', color: '#22b573' },
  disease: { name: '疾病防治', color: '#f0524d' },
  daily: { name: '日常护理', color: '#17b3c4' },
  breed: { name: '品种百科', color: '#f59a23' }
}

const cats = [
  { key: 'all', name: '全部' },
  { key: 'dog', name: '犬类', icon: IC_DOG, bg: '#fff0e2', fg: '#f0913f' },
  { key: 'cat', name: '猫类', icon: IC_CAT, bg: '#efeaff', fg: '#8b6ef0' },
  { key: 'health', name: '健康护理', icon: IC_SHIELD, bg: '#e4f8ee', fg: '#18b26b' },
  { key: 'disease', name: '疾病防治', icon: IC_PLUS, bg: '#ffeceb', fg: '#ef5b50' },
  { key: 'training', name: '行为训练', icon: IC_GEAR, bg: '#f6ebff', fg: '#a55ce8' },
  { key: 'diet', name: '饮食营养', icon: IC_LEAF, bg: '#e6f8ee', fg: '#1fb86c' },
  { key: 'daily', name: '日常生活', icon: IC_CALENDAR_C, bg: '#eceaff', fg: '#6d5ce0' }
]

const menus = [
  { key: 'all', name: '热门推荐', icon: IC_STAR },
  { key: 'knowledge', name: '养宠知识', icon: IC_KNOW },
  { key: 'disease', name: '疾病防治', icon: IC_MEDKIT },
  { key: 'training', name: '行为训练', icon: IC_WRENCH },
  { key: 'daily', name: '日常护理', icon: IC_BAG },
  { key: 'breed', name: '品种百科', icon: IC_TAG }
]

/* ==================== 演示数据（兜底） ==================== */
const DEMO_ARTICLES = [
  {
    rid: 'd1', title: '金毛犬常见健康问题及预防指南', tag: 'knowledge',
    summary: '了解金毛犬的常见疾病、预防措施以及日常护理要点，帮助爱宠健康成长。',
    date: '2025-06-12', views: '12.6k', cover: IMG.a1,
    keys: ['knowledge', 'health', 'dog']
  },
  {
    rid: 'd2', title: '猫咪为什么喜欢踩奶？', tag: 'training',
    summary: '从行为学角度解析猫咪踩奶的原因，以及如何正确应对这一行为。',
    date: '2025-06-10', views: '8.9k', cover: IMG.a2,
    keys: ['training', 'cat']
  },
  {
    rid: 'd3', title: '狗狗饮食指南：不同年龄段的营养需求', tag: 'diet',
    summary: '幼犬、成犬、老年犬的饮食有何不同？如何科学搭配狗粮？',
    date: '2025-06-08', views: '7.4k', cover: IMG.a3,
    keys: ['diet', 'dog', 'daily']
  },
  {
    rid: 'd4', title: '猫咪常见皮肤病的识别与治疗', tag: 'disease',
    summary: '皮肤病是猫咪常见的健康问题，了解症状和治疗方法很重要。',
    date: '2025-06-05', views: '6.8k', cover: IMG.a4,
    keys: ['disease', 'cat', 'health']
  },
  {
    rid: 'd5', title: '春季宠物护理小贴士', tag: 'daily',
    summary: '换季时节，如何做好宠物的日常护理，避免生病？',
    date: '2025-06-03', views: '5.2k', cover: IMG.a5,
    keys: ['daily', 'health', 'dog']
  },
  {
    rid: 'd6', title: '布偶猫的性格特点与饲养建议', tag: 'breed',
    summary: '温柔的布偶猫深受喜爱，了解它的性格和饲养要点，轻松养好猫。',
    date: '2025-06-01', views: '4.7k', cover: IMG.a6,
    keys: ['breed', 'cat']
  },
  {
    rid: 'd7', title: '狗狗疫苗接种全攻略：时间与注意事项', tag: 'disease',
    summary: '幼犬到成年犬的疫苗时间表、接种前后护理与常见反应处理。',
    date: '2025-05-28', views: '4.1k', cover: IMG.a7,
    keys: ['disease', 'health', 'dog']
  },
  {
    rid: 'd8', title: '如何训练狗狗定点排便？', tag: 'training',
    summary: '掌握正向强化训练法，一周养成定点排便好习惯。',
    date: '2025-05-24', views: '3.6k', cover: IMG.a8,
    keys: ['training', 'dog']
  },
  {
    rid: 'd9', title: '猫咪夏季防暑降温完全指南', tag: 'daily',
    summary: '高温天气如何识别猫咪中暑信号，并为它打造凉爽环境。',
    date: '2025-05-20', views: '3.2k', cover: IMG.a9,
    keys: ['daily', 'health', 'cat']
  }
].map(a => ({ ...a, tagName: TAG_META[a.tag].name, tagColor: TAG_META[a.tag].color }))

const topics = [
  { title: '狗狗疫苗接种时间表', views: '12.3k', key: 'disease' },
  { title: '猫咪绝育的好处', views: '10.8k', key: 'cat' },
  { title: '宠物驱虫指南', views: '9.6k', key: 'disease' },
  { title: '宠物出现呕吐怎么办', views: '8.4k', key: 'disease' },
  { title: '狗狗掉毛严重的原因', views: '7.2k', key: 'daily' }
]

const DEMO_RECOMMENDS = [
  { rid: 'r1', title: '宠物体检的必要性与项目', date: '2025-05-28', views: '5.6k', cover: IMG.r1 },
  { rid: 'r2', title: '猫咪的肢体语言解读', date: '2025-05-25', views: '4.3k', cover: IMG.r2 },
  { rid: 'r3', title: '宠物心理健康与情绪管理', date: '2025-05-22', views: '3.9k', cover: IMG.r3 }
]

/* ==================== Hero 轮播 ==================== */
const banners = [
  { tag: '宠物小知识', title: '科学养宠 · 从了解开始', sub: '汇集专业宠物知识，守护毛孩子的健康与幸福', img: IMG.heroDogCat },
  { tag: '疾病防治', title: '定期疫苗驱虫，穿上“防护甲”', sub: '科学预防传染病，让宠物远离可避免的健康风险', img: IMG.vaccine },
  { tag: '行为训练', title: '读懂宠物行为，让陪伴更默契', sub: '从肢体语言到习惯养成，做真正懂它的主人', img: IMG.a2 }
]
const bannerIdx = ref(0)
let bannerTimer = null
function startBanner() {
  stopBanner()
  bannerTimer = setInterval(() => {
    bannerIdx.value = (bannerIdx.value + 1) % banners.length
  }, 5000)
}
function stopBanner() {
  if (bannerTimer) { clearInterval(bannerTimer); bannerTimer = null }
}

/* ==================== 列表状态 ==================== */
const articles = ref(DEMO_ARTICLES)
const recommends = ref(DEMO_RECOMMENDS)
const isReal = ref(false)
const realHasMore = ref(false)
const total = ref(0)
const page = ref(1)
const size = 9
const loading = ref(false)
const activeCat = ref('all')
const expanded = ref(false)
const askText = ref('')
const artSecRef = ref(null)

const catNameOf = (key) => {
  if (key === 'all') return null
  const c = cats.find(x => x.key === key)
  return c ? c.name : (TAG_META[key]?.name || '热门推荐')
}
const sectionTitle = computed(() => {
  if (activeCat.value === 'all') return '热门推荐'
  return catNameOf(activeCat.value) || '热门推荐'
})

const filteredArticles = computed(() => {
  if (activeCat.value === 'all') return articles.value
  return articles.value.filter(a => (a.keys || []).includes(activeCat.value))
})
const shownArticles = computed(() => {
  const list = filteredArticles.value
  return expanded.value ? list : list.slice(0, 6)
})

/* ==================== 交互 ==================== */
function setCat(key, scroll = false) {
  activeCat.value = key
  expanded.value = false
  if (scroll && artSecRef.value) {
    artSecRef.value.scrollIntoView({ behavior: 'smooth', block: 'start' })
  }
}
function goArticles() {
  artSecRef.value?.scrollIntoView({ behavior: 'smooth', block: 'start' })
}
async function toggleMore() {
  if (!expanded.value && isReal.value && realHasMore.value) {
    await loadMore()
  }
  expanded.value = !expanded.value
}
function goDetail(item) {
  if (item.id) {
    router.push(`/info/${item.id}`)
  } else {
    ElMessage.info('演示资讯：接入真实数据后可查看详情')
  }
}
function sendAsk() {
  router.push('/chat')
}

/* ==================== 真实 API 渐进增强 ==================== */
function resolveImg(url) {
  if (!url) return ''
  if (url.startsWith('http')) return url
  return API_BASE + url
}
function formatTime(t) {
  if (!t) return ''
  return String(t).replace('T', ' ').substring(0, 10)
}

/* 后端分类（喂养/洗护/疾病/习性 等）→ 设计稿分类键 */
function realCategoryKey(cat = '') {
  const c = String(cat || '')
  if (/病|疫|医疗/.test(c)) return 'disease'
  if (/喂|食|饮|营养/.test(c)) return 'diet'
  if (/洗|护|护理/.test(c)) return 'daily'
  if (/习性|训练|行为/.test(c)) return 'training'
  if (/品种|百科/.test(c)) return 'breed'
  return 'knowledge'
}
function realKeys(item, catKey) {
  const keys = [catKey]
  if (catKey === 'disease') keys.push('health')
  if (catKey === 'daily') keys.push('health')
  const text = String(item.title || '') + String(item.category || '')
  if (/猫/.test(text)) keys.push('cat')
  if (/狗|犬/.test(text)) keys.push('dog')
  return Array.from(new Set(keys))
}
const FALLBACK_COVERS = [IMG.a1, IMG.a2, IMG.a3, IMG.a5, IMG.a6, IMG.r1]
function mapReal(list) {
  return list.map((x, i) => {
    const catKey = realCategoryKey(x.category)
    const meta = TAG_META[catKey] || TAG_META.knowledge
    return {
      id: x.id,
      rid: 'real-' + x.id,
      title: x.title || '未命名资讯',
      tag: catKey,
      tagName: meta.name,
      tagColor: meta.color,
      summary: x.summary || x.description || '点击阅读全文，了解更多科学养宠知识。',
      date: formatTime(x.createTime),
      views: x.viewCount != null ? formatViews(x.viewCount) : '—',
      cover: x.coverImage ? resolveImg(x.coverImage) : FALLBACK_COVERS[i % FALLBACK_COVERS.length],
      keys: realKeys(x, catKey)
    }
  })
}
function formatViews(v) {
  const n = Number(v)
  if (isNaN(n)) return v
  return n >= 10000 ? (n / 10000).toFixed(1) + 'w' : (n >= 1000 ? (n / 1000).toFixed(1) + 'k' : String(n))
}

async function loadList(p = 1, append = false) {
  loading.value = true
  try {
    const res = await listInfo({ page: p, size })
    const list = res.data?.list || []
    const mapped = mapReal(list)
    if (mapped.length) {
      isReal.value = true
      total.value = res.data?.total || 0
      realHasMore.value = articles.value.length + mapped.length < total.value
      articles.value = append ? articles.value.concat(mapped) : mapped
      if (!append) recommends.value = mapped.slice(0, 3)
      page.value = p
    } else if (!append) {
      /* 后端为空：保留演示兜底 */
      isReal.value = false
    }
  } catch (e) {
    /* 未登录 / 后端不可用：保留演示数据 */
    isReal.value = false
  } finally {
    loading.value = false
  }
}
async function loadMore() {
  await loadList(page.value + 1, true)
}

onMounted(() => {
  loadList(1)
  startBanner()
})
onUnmounted(stopBanner)
</script>

<style scoped>
/* ==================== 页面骨架 ==================== */
.info-page {
  width: 100%;
  max-width: none;
  margin: 0;
  padding: 18px clamp(16px, 2vw, 32px) 40px;
}
.info-layout {
  display: grid;
  grid-template-columns: 208px minmax(0, 1fr) 286px;
  gap: 18px;
  align-items: start;
}

/* ==================== 左栏 ==================== */
.info-side {
  position: sticky;
  top: 18px;
  display: flex;
  flex-direction: column;
  gap: 14px;
}
.side-brand {
  display: flex;
  align-items: center;
  gap: 11px;
  padding: 16px 15px;
  border-radius: 16px;
  background: linear-gradient(135deg, #eef1ff, #e4e9ff);
  border: 1px solid #e3e8ff;
}
.brand-ic {
  flex: none;
  width: 44px;
  height: 44px;
  border-radius: 13px;
  background: linear-gradient(135deg, #7c8df6, #5b63f0);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8px 18px rgba(109, 108, 230, .3);
}
.brand-ic svg {
  width: 25px;
  height: 25px;
}
.brand-txt {
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 3px;
}
.brand-txt b {
  font-size: 16.5px;
  color: #2f3a66;
  font-weight: 800;
  white-space: nowrap;
}
.brand-txt small {
  font-size: 11.5px;
  color: #8b95bd;
  white-space: nowrap;
}
.side-menu {
  background: #fff;
  border: 1px solid #edf0f8;
  border-radius: 16px;
  box-shadow: 0 10px 28px rgba(94, 110, 190, .06);
  padding: 10px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.menu-item {
  display: flex;
  align-items: center;
  gap: 11px;
  width: 100%;
  border: 0;
  background: transparent;
  padding: 11px 12px;
  border-radius: 11px;
  cursor: pointer;
  color: #5c6788;
  font-size: 14.5px;
  font-weight: 600;
  text-align: left;
  transition: background .2s, color .2s;
}
.menu-item .m-ic {
  flex: none;
  width: 22px;
  height: 22px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #97a1c4;
  transition: color .2s;
}
.menu-item .m-ic svg {
  width: 21px;
  height: 21px;
}
.menu-item:hover {
  background: #f5f7ff;
  color: #5b63f0;
}
.menu-item:hover .m-ic {
  color: #7c8df6;
}
.menu-item.on {
  background: linear-gradient(135deg, #eef1ff, #e7ebff);
  color: #5b63f0;
}
.menu-item.on .m-ic {
  color: #6d5ce0;
}
.side-art {
  position: relative;
  border-radius: 16px;
  background: linear-gradient(180deg, #f3f6ff 0%, #e9f0ff 55%, #e6f5f1 100%);
  border: 1px solid #e8edfb;
  padding: 10px 10px 0;
  overflow: hidden;
}
.side-art img {
  width: 100%;
  display: block;
  border-radius: 12px 12px 0 0;
}
.hand-note {
  position: absolute;
  top: 8px;
  left: 0;
  right: 0;
  text-align: center;
  font-family: 'Comic Sans MS', 'STKaiti', cursive;
  font-size: 15px;
  line-height: 1.45;
  color: #8d97d6;
  transform: rotate(-7deg);
  letter-spacing: 1px;
  text-shadow: 0 1px 0 rgba(255, 255, 255, .8);
}
.hand-note small {
  color: #f06292;
  font-size: 12px;
}

/* ==================== 主区 ==================== */
.info-main {
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* Hero 轮播 */
.hero-swiper {
  position: relative;
  height: 300px;
  border-radius: 22px;
  overflow: hidden;
  background: linear-gradient(120deg, #eaf3ff 0%, #e6f0ff 38%, #e8f6f1 100%);
  box-shadow: 0 14px 34px rgba(94, 110, 190, .1);
}
.hero-slide {
  position: absolute;
  inset: 0;
}
.hero-img {
  position: absolute;
  right: 0;
  bottom: 0;
  height: 100%;
  width: 56%;
  object-fit: cover;
  object-position: center 30%;
  -webkit-mask-image: linear-gradient(90deg, transparent 0%, #000 22%, #000 100%);
  mask-image: linear-gradient(90deg, transparent 0%, #000 22%, #000 100%);
}
.hero-text {
  position: relative;
  z-index: 2;
  padding: 44px 0 0 40px;
  max-width: 56%;
}
.hero-tag {
  display: inline-flex;
  align-items: center;
  gap: 7px;
  background: rgba(255, 255, 255, .75);
  backdrop-filter: blur(4px);
  border: 1px solid rgba(255, 255, 255, .9);
  color: #5b63f0;
  font-size: 13px;
  font-weight: 700;
  padding: 6px 14px;
  border-radius: 999px;
  box-shadow: 0 6px 16px rgba(109, 110, 190, .12);
}
.ht-ic {
  width: 16px;
  height: 16px;
  display: inline-flex;
}
.ht-ic svg {
  width: 100%;
  height: 100%;
}
.hero-text h1 {
  margin: 16px 0 12px;
  font-size: 33px;
  line-height: 1.28;
  font-weight: 900;
  color: #26305a;
  letter-spacing: .5px;
}
.hero-text p {
  margin: 0 0 22px;
  font-size: 14.5px;
  color: #6b759c;
}
.hero-btn {
  border: 0;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  height: 44px;
  padding: 0 26px;
  border-radius: 999px;
  color: #fff;
  font-size: 15px;
  font-weight: 700;
  background: linear-gradient(135deg, #7c8df6, #5b63f0);
  box-shadow: 0 10px 22px rgba(105, 105, 230, .35);
  transition: transform .2s, box-shadow .2s;
}
.hero-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 14px 26px rgba(105, 105, 230, .42);
}
.hero-btn span {
  font-size: 17px;
  transition: transform .2s;
}
.hero-btn:hover span {
  transform: translateX(3px);
}
.hero-hand {
  position: absolute;
  top: 26px;
  right: 40px;
  z-index: 3;
  margin: 0;
  font-family: 'Comic Sans MS', 'STKaiti', cursive;
  font-size: 19px;
  line-height: 1.45;
  color: #7e88c4;
  transform: rotate(12deg);
  text-align: center;
  text-shadow: 0 1px 0 rgba(255, 255, 255, .7);
  pointer-events: none;
}
.hero-hand small {
  color: #f06292;
  font-size: 14px;
}
.hero-wave {
  position: absolute;
  left: 0;
  right: 0;
  bottom: -2px;
  z-index: 1;
  line-height: 0;
  pointer-events: none;
}
.wave-svg {
  width: 100%;
  height: 78px;
  display: block;
}
.hero-dots {
  position: absolute;
  right: 36px;
  bottom: 20px;
  z-index: 4;
  display: flex;
  align-items: center;
  gap: 8px;
}
.hero-dots i {
  width: 8px;
  height: 8px;
  border-radius: 999px;
  background: rgba(110, 130, 190, .35);
  cursor: pointer;
  transition: all .25s;
}
.hero-dots i.on {
  width: 22px;
  background: #5b6ee8;
}
.hero-fade-enter-active,
.hero-fade-leave-active {
  transition: opacity .35s ease;
}
.hero-fade-enter-from,
.hero-fade-leave-to {
  opacity: 0;
}

/* 分类胶囊 */
.cat-bar {
  background: #fff;
  border: 1px solid #edf0f8;
  border-radius: 16px;
  box-shadow: 0 10px 28px rgba(94, 110, 190, .06);
  padding: 13px 16px;
  display: flex;
  align-items: center;
  gap: 10px;
  overflow-x: auto;
  scrollbar-width: none;
}
.cat-bar::-webkit-scrollbar {
  display: none;
}
.cat-chip {
  flex: none;
  display: inline-flex;
  align-items: center;
  gap: 7px;
  height: 38px;
  padding: 0 16px;
  border-radius: 999px;
  border: 1.5px solid #eceffa;
  background: #fff;
  color: #5c6788;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all .2s;
  white-space: nowrap;
}
.cat-chip:hover {
  border-color: #d6dcff;
  color: #5b63f0;
  transform: translateY(-1px);
}
.cat-chip.on {
  border-color: transparent;
  background: linear-gradient(135deg, #7c8df6, #5b63f0);
  color: #fff;
  box-shadow: 0 8px 18px rgba(105, 105, 230, .32);
}
.c-dot {
  width: 23px;
  height: 23px;
  border-radius: 50%;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}
.cat-chip.on .c-dot {
  background: rgba(255, 255, 255, .22);
}
.cd-ic {
  width: 15px;
  height: 15px;
  display: inline-flex;
}
.cd-ic svg {
  width: 100%;
  height: 100%;
}

/* 文章区块 */
.art-section {
  background: transparent;
}
.sec-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: 2px 2px 14px;
}
.sh-l {
  display: flex;
  align-items: center;
  gap: 9px;
}
.sh-l b {
  font-size: 18px;
  color: #2c365e;
  font-weight: 800;
}
.fire-ic {
  width: 32px;
  height: 32px;
  border-radius: 10px;
  background: linear-gradient(135deg, #ff8a5c, #f0524d);
  color: #fff;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 6px 14px rgba(240, 92, 80, .3);
}
.fire-ic.sm {
  width: 27px;
  height: 27px;
  border-radius: 9px;
}
.fire-ic svg {
  width: 18px;
  height: 18px;
}
.sh-more {
  font-size: 13px;
  color: #8b95bd;
  font-weight: 600;
  cursor: pointer;
  transition: color .2s;
  user-select: none;
}
.sh-more:hover {
  color: #5b63f0;
}
.sh-more span {
  display: inline-block;
  transition: transform .2s;
}
.sh-more span.up {
  transform: rotate(-90deg);
}

/* 文章网格 */
.art-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
  min-height: 120px;
}
.art-card {
  background: #fff;
  border: 1px solid #edf0f8;
  border-radius: 16px;
  overflow: hidden;
  cursor: pointer;
  box-shadow: 0 8px 24px rgba(94, 110, 190, .06);
  transition: transform .25s ease, box-shadow .25s ease;
  display: flex;
  flex-direction: column;
}
.art-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 18px 38px rgba(94, 110, 190, .14);
}
.ac-cover {
  position: relative;
  height: 158px;
  overflow: hidden;
  background: #eef1f8;
}
.ac-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform .4s ease;
}
.art-card:hover .ac-cover img {
  transform: scale(1.06);
}
.ac-tag {
  position: absolute;
  left: 11px;
  bottom: 11px;
  color: #fff;
  font-size: 12px;
  font-weight: 700;
  padding: 4px 11px;
  border-radius: 7px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, .12);
}
.ac-body {
  padding: 14px 15px 13px;
  display: flex;
  flex-direction: column;
  flex: 1;
}
.ac-body h3 {
  margin: 0 0 8px;
  font-size: 15.5px;
  line-height: 1.45;
  font-weight: 800;
  color: #2c365e;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  min-height: 45px;
}
.ac-body p {
  margin: 0 0 12px;
  font-size: 12.5px;
  line-height: 1.6;
  color: #96a0bf;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.ac-meta {
  margin-top: auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 12px;
  color: #a5aeca;
}
.am-date,
.am-view {
  display: inline-flex;
  align-items: center;
  gap: 5px;
}
.am-ic {
  width: 14px;
  height: 14px;
  display: inline-flex;
}
.am-ic svg {
  width: 100%;
  height: 100%;
}

/* 空态 */
.art-empty {
  background: #fff;
  border: 1px dashed #dbe1f6;
  border-radius: 16px;
  padding: 54px 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}
.ae-ic {
  width: 62px;
  height: 62px;
  border-radius: 50%;
  background: linear-gradient(135deg, #eef1ff, #e2e8ff);
  color: #7c8df6;
  display: flex;
  align-items: center;
  justify-content: center;
}
.ae-ic svg {
  width: 28px;
  height: 28px;
}
.art-empty b {
  font-size: 15.5px;
  color: #525e85;
}
.art-empty small {
  font-size: 12.5px;
  color: #a5aeca;
}
.ae-btn {
  margin-top: 8px;
  border: 0;
  cursor: pointer;
  height: 38px;
  padding: 0 22px;
  border-radius: 999px;
  color: #fff;
  font-size: 13.5px;
  font-weight: 700;
  background: linear-gradient(135deg, #7c8df6, #5b63f0);
  box-shadow: 0 8px 18px rgba(105, 105, 230, .3);
}

/* ==================== 右栏 ==================== */
.info-right {
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 16px;
  position: sticky;
  top: 18px;
}
.r-card {
  background: #fff;
  border: 1px solid #edf0f8;
  border-radius: 16px;
  box-shadow: 0 10px 28px rgba(94, 110, 190, .06);
  padding: 16px;
}
.r-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}
.rh-l {
  display: flex;
  align-items: center;
  gap: 8px;
}
.rh-l b {
  font-size: 15.5px;
  color: #2c365e;
  font-weight: 800;
}
.book-ic {
  width: 27px;
  height: 27px;
  border-radius: 9px;
  background: linear-gradient(135deg, #86b4ff, #5b7ef0);
  color: #fff;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 6px 14px rgba(91, 126, 240, .28);
}
.book-ic svg {
  width: 16px;
  height: 16px;
}
.r-more {
  font-size: 12.5px;
  color: #a5aeca;
  cursor: pointer;
  font-weight: 500;
  white-space: nowrap;
  transition: color .2s;
}
.r-more:hover {
  color: #5b63f0;
}

/* 热门话题 */
.topic-list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
}
.topic-list li {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 9px 2px;
  cursor: pointer;
  border-bottom: 1px dashed #f0f2f9;
  transition: background .2s;
}
.topic-list li:last-child {
  border-bottom: 0;
}
.topic-list li:hover .t-title {
  color: #5b63f0;
}
.t-no {
  flex: none;
  width: 20px;
  height: 20px;
  border-radius: 6px;
  background: #c3cbE4;
  color: #fff;
  font-size: 12px;
  font-weight: 800;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}
.t-no.n1 { background: linear-gradient(135deg, #ff7a6b, #f0524d); }
.t-no.n2 { background: linear-gradient(135deg, #ffb05c, #ff9248); }
.t-no.n3 { background: linear-gradient(135deg, #ffce5c, #f5b53a); }
.t-title {
  flex: 1;
  min-width: 0;
  font-size: 13px;
  color: #525e85;
  font-weight: 600;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  transition: color .2s;
}
.t-view {
  flex: none;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #aeb7d4;
}
.tv-ic {
  width: 13px;
  height: 13px;
  display: inline-flex;
}
.tv-ic svg {
  width: 100%;
  height: 100%;
}

/* 推荐阅读 */
.read-list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 13px;
}
.read-list li {
  display: flex;
  gap: 11px;
  cursor: pointer;
}
.read-list img {
  flex: none;
  width: 64px;
  height: 64px;
  border-radius: 11px;
  object-fit: cover;
  background: #eef1f8;
  transition: transform .25s;
}
.read-list li:hover img {
  transform: scale(1.05);
}
.rl-info {
  min-width: 0;
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 2px 0;
}
.rl-info b {
  font-size: 13.5px;
  line-height: 1.45;
  font-weight: 700;
  color: #3d4770;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  transition: color .2s;
}
.read-list li:hover .rl-info b {
  color: #5b63f0;
}
.rl-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 11.5px;
  color: #a5aeca;
}
.rl-meta span {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

/* AI 助手 */
.ai-card {
  border-radius: 18px;
  padding: 18px 16px 16px;
  background:
    radial-gradient(120% 90% at 0% 0%, rgba(150, 170, 255, .35), transparent 60%),
    linear-gradient(150deg, #eef1ff 0%, #e7edff 55%, #f0ecff 100%);
  border: 1px solid #e0e6ff;
  box-shadow: 0 12px 30px rgba(109, 110, 200, .14);
}
.ai-head {
  display: flex;
  align-items: center;
  gap: 11px;
  margin-bottom: 14px;
}
.ai-ic {
  flex: none;
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: linear-gradient(135deg, #7c8df6, #5b63f0);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8px 18px rgba(105, 105, 230, .35);
}
.ai-ic svg {
  width: 25px;
  height: 25px;
}
.ai-txt {
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 3px;
}
.ai-txt b {
  font-size: 15.5px;
  color: #2f3a66;
  font-weight: 800;
}
.ai-txt small {
  font-size: 11.5px;
  color: #8b95bd;
  line-height: 1.4;
}
.ai-input {
  display: flex;
  align-items: center;
  gap: 8px;
  background: #fff;
  border-radius: 999px;
  padding: 5px 6px 5px 15px;
  box-shadow: 0 6px 16px rgba(105, 105, 230, .12);
}
.ai-input input {
  flex: 1;
  min-width: 0;
  border: 0;
  outline: 0;
  background: transparent;
  font-size: 12.5px;
  color: #3d4770;
}
.ai-input input::placeholder {
  color: #b3bbd6;
}
.ai-input button {
  flex: none;
  width: 34px;
  height: 34px;
  border-radius: 50%;
  border: 0;
  cursor: pointer;
  background: linear-gradient(135deg, #7c8df6, #5b63f0);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 6px 14px rgba(105, 105, 230, .35);
  transition: transform .2s;
}
.ai-input button:hover {
  transform: scale(1.07);
}
.ai-input button svg {
  width: 17px;
  height: 17px;
}

/* ==================== 响应式 ==================== */
@media (max-width: 1380px) {
  .info-layout {
    grid-template-columns: 196px minmax(0, 1fr);
  }
  .info-right {
    position: static;
    grid-column: 1 / -1;
    display: grid;
    grid-template-columns: repeat(3, minmax(0, 1fr));
    gap: 16px;
  }
}
@media (max-width: 1080px) {
  .info-layout {
    grid-template-columns: minmax(0, 1fr);
  }
  .info-side {
    position: static;
    flex-direction: column;
  }
  .side-brand {
    display: none;
  }
  .side-menu {
    flex-direction: row;
    overflow-x: auto;
    scrollbar-width: none;
    gap: 8px;
  }
  .side-menu::-webkit-scrollbar {
    display: none;
  }
  .menu-item {
    flex: none;
    width: auto;
    white-space: nowrap;
  }
  .side-art {
    display: none;
  }
  .art-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
  .info-right {
    grid-template-columns: 1fr 1fr;
  }
  .ai-card {
    grid-column: 1 / -1;
  }
  .hero-text {
    max-width: 64%;
    padding: 36px 0 0 30px;
  }
  .hero-img {
    width: 50%;
  }
  .hero-text h1 {
    font-size: 27px;
  }
}
@media (max-width: 680px) {
  .info-page {
    padding: 14px 14px 32px;
  }
  .hero-swiper {
    height: 240px;
  }
  .hero-text {
    max-width: 72%;
    padding: 28px 0 0 20px;
  }
  .hero-text h1 {
    font-size: 21px;
    margin: 12px 0 8px;
  }
  .hero-text p {
    font-size: 12.5px;
    margin-bottom: 14px;
  }
  .hero-btn {
    height: 38px;
    padding: 0 18px;
    font-size: 13.5px;
  }
  .hero-hand {
    display: none;
  }
  .hero-img {
    width: 58%;
    -webkit-mask-image: linear-gradient(90deg, transparent 0%, #000 45%, #000 100%);
    mask-image: linear-gradient(90deg, transparent 0%, #000 45%, #000 100%);
  }
  .art-grid {
    grid-template-columns: minmax(0, 1fr);
  }
  .info-right {
    grid-template-columns: minmax(0, 1fr);
  }
}
</style>
