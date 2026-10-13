<template>
  <div class="records-page">
    <div class="records-layout">
      <!-- ============ 左栏 ============ -->
      <aside class="rec-side">
        <div class="side-brand">
          <span class="brand-ic" v-html="IC.paw"></span>
          <div class="brand-txt">
            <b>我的记录</b>
            <small>记录宠物的每一个重要时刻</small>
          </div>
        </div>

        <nav class="side-menu">
          <button
            v-for="m in menus"
            :key="m.key"
            type="button"
            class="menu-item"
            :class="{ on: activeType === m.key }"
            @click="setType(m.key, true)"
          >
            <span class="m-ic" v-html="m.icon"></span>
            <span class="m-name">{{ m.name }}</span>
          </button>
        </nav>

        <div class="pets-box">
          <div class="pb-head">
            <b>我的宠物</b>
            <a @click="goArchive">管理宠物 <span>›</span></a>
          </div>
          <div
            v-for="p in pets"
            :key="p._id"
            class="pet-mini"
            :class="{ on: activePetId === p._id }"
            @click="selectPet(p._id)"
          >
            <img :src="p.avatar" :alt="p.petName">
            <div class="pm-info">
              <div class="pm-name">
                <b>{{ p.petName }}</b>
                <i :class="p.gender === '母' ? 'ic-female' : 'ic-male'">{{ p.gender === '母' ? '♀' : '♂' }}</i>
              </div>
              <small>{{ p.breed }}</small>
              <span class="pm-health"><i></i>健康</span>
            </div>
          </div>
        </div>

        <div class="side-art">
          <img :src="IMG.sideArt" alt="cute puppies">
          <p class="hand-note">记录点滴，<br>见证成长 <small>♡</small></p>
        </div>
      </aside>

      <!-- ============ 主区 ============ -->
      <main class="rec-main">
        <!-- Hero -->
        <section class="rec-hero">
          <div class="hero-left">
            <span class="hero-logo" v-html="IC.camera"></span>
            <h1>我的记录</h1>
            <p>每一次识别、每一次成长、每一次关爱，<br>都值得被记录。<span class="hp-paw" v-html="IC.pawSm"></span></p>
          </div>
          <img class="hero-img" :src="IMG.heroDogCat" alt="dog and cat">
          <p class="hero-hand">宠物的<br>每一段成长<br>都很珍贵 <small>♡</small></p>
          <div class="hero-wave" v-html="IC.wave"></div>
        </section>

        <!-- 统计卡 -->
        <section class="stat-grid">
          <div
            v-for="s in statCards"
            :key="s.key"
            class="stat-card"
            :class="{ on: activeType === s.key }"
            @click="setType(s.key, true)"
          >
            <span class="st-ic" :style="{ background: s.bg }" v-html="s.icon"></span>
            <div class="st-info">
              <small>{{ s.name }}</small>
              <b>{{ s.value }}<em>{{ s.unit }}</em></b>
              <span class="st-delta" :class="s.deltaType || 'up'">{{ s.deltaText }}</span>
            </div>
          </div>
        </section>

        <!-- 筛选 + 列表 -->
        <section class="list-panel" ref="listPanelRef">
          <div class="filter-bar">
            <div class="type-tabs">
              <button
                v-for="t in typeTabs"
                :key="t.key"
                type="button"
                :class="{ on: activeType === t.key }"
                @click="setType(t.key)"
              >
                <span class="tt-ic" v-html="t.icon"></span>{{ t.name }}
              </button>
            </div>
            <div class="filter-tools">
              <el-date-picker
                v-model="dateRange"
                type="daterange"
                range-separator="~"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                value-format="YYYY-MM-DD"
                size="default"
                class="range-picker"
                :clearable="true"
                @update:model-value="onDateTouch"
              />
              <el-select v-model="petFilter" placeholder="全部宠物" class="pet-select" @change="() => {}">
                <el-option label="全部宠物" value="" />
                <el-option v-for="p in petOptions" :key="p._id" :label="p.petName" :value="p.petName" />
              </el-select>
            </div>
          </div>

          <div v-loading="loading" class="rec-rows">
            <div v-for="r in filteredRecords" :key="r.rid" class="rec-row">
              <div class="rr-thumb" @click="previewImg(r)">
                <img :src="r.thumb" :alt="r.title" loading="lazy">
                <i v-if="r.fav" class="rr-fav" v-html="IC.starSolid"></i>
              </div>
              <div class="rr-main">
                <h4>{{ r.title }}</h4>
                <p class="rr-meta">
                  <template v-for="(seg, i) in r.metaSegs" :key="i">
                    <span v-if="i > 0" class="rr-sep">|</span>{{ seg }}
                  </template>
                </p>
              </div>
              <div class="rr-pet" v-if="r.petName">
                <img :src="r.petAvatar" :alt="r.petName">
                <b>{{ r.petName }}</b>
              </div>
              <div class="rr-time">
                <span class="rt-ic" v-html="IC.calendar"></span>{{ r.time }}
              </div>
              <span class="rr-tag" :class="'tag-' + r.type">{{ typeName(r.type) }}</span>
              <button type="button" class="rr-btn" @click="viewDetail(r)">查看详情 <span>›</span></button>
            </div>

            <!-- 空态 -->
            <div v-if="!loading && filteredRecords.length === 0" class="rows-empty">
              <span class="re-ic" v-html="IC.docEmpty"></span>
              <b>{{ emptyTitle }}</b>
              <small>{{ emptyTip }}</small>
              <button type="button" class="re-btn" @click="setType('all')">查看全部记录</button>
            </div>
          </div>
        </section>
      </main>

      <!-- ============ 右栏 ============ -->
      <aside class="rec-right">
        <!-- 宠物卡 -->
        <section class="rp-card">
          <div class="rp-top">
            <img class="rp-avatar" :src="activePet.avatar" :alt="activePet.petName">
            <div class="rp-id">
              <div class="rp-name-row">
                <b>{{ activePet.petName }}</b>
                <a class="rp-edit" @click="goArchive"><span v-html="IC.edit"></span>编辑</a>
              </div>
              <p>{{ activePet.breed }} <i :class="activePet.gender === '母' ? 'ic-female' : 'ic-male'">{{ activePet.gender === '母' ? '♀' : '♂' }}</i> {{ activePet.age }}</p>
              <div class="rp-badges">
                <span class="rp-badge b-health"><i v-html="IC.heartSm"></i>健康</span>
                <span class="rp-badge b-shot"><i v-html="IC.shieldSm"></i>已免疫</span>
                <span class="rp-badge b-worm"><i v-html="IC.pillSm"></i>已驱虫</span>
              </div>
            </div>
          </div>

          <div class="rp-tabs">
            <button
              v-for="t in rpTabList"
              :key="t.key"
              type="button"
              :class="{ on: rpTab === t.key }"
              @click="rpTab = t.key"
            >{{ t.name }}</button>
          </div>

          <!-- 成长曲线 -->
          <div v-if="rpTab === 'curve'" class="rp-panel">
            <p class="rp-panel-title">体重变化（kg）</p>
            <svg v-if="chart.hasWeight" class="rp-svg" viewBox="0 0 260 132">
              <defs>
                <linearGradient id="rpArea" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="0%" stop-color="#6d8bf2" stop-opacity=".28"/>
                  <stop offset="100%" stop-color="#6d8bf2" stop-opacity=".02"/>
                </linearGradient>
              </defs>
              <g v-for="(y, i) in chart.yTicks" :key="'g'+i">
                <line :x1="26" :x2="246" :y1="y.y" :y2="y.y" stroke="#eef1f8" stroke-width="1"/>
                <text :x="8" :y="y.y + 3.5" font-size="9" fill="#aab4d0">{{ y.v }}</text>
              </g>
              <path :d="chart.area" fill="url(#rpArea)"/>
              <path :d="chart.line" fill="none" stroke="#5b73e8" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"/>
              <g v-for="(pt, i) in chart.points" :key="'p'+i">
                <circle :cx="pt.x" :cy="pt.y" :r="i === chart.points.length - 1 ? 4 : 2.8" :fill="i === chart.points.length - 1 ? '#5b63f0' : '#5b73e8'" stroke="#fff" stroke-width="1.4"/>
              </g>
              <rect :x="chart.last.x - 27" :y="chart.last.y - 26" width="54" height="19" rx="9.5" fill="#5b63f0"/>
              <text :x="chart.last.x" :y="chart.last.y - 13" text-anchor="middle" font-size="10" font-weight="700" fill="#fff">{{ activePet.lastWeight }}kg</text>
              <g v-for="(m, i) in chart.months" :key="'m'+i">
                <text :x="chart.points[i].x" y="120" text-anchor="middle" font-size="9" fill="#aab4d0">{{ m }}</text>
              </g>
            </svg>
            <div v-else class="rp-chart-empty">
              <span v-html="IC.scaleSm"></span>
              暂无体重记录
            </div>
          </div>

          <!-- 体重记录 -->
          <div v-else-if="rpTab === 'weight'" class="rp-panel">
            <div v-if="activePet.weightLogs.length" class="w-log">
              <div v-for="(w, i) in activePet.weightLogs" :key="i" class="wl-row">
                <span class="wl-dot" :class="{ first: i === 0 }"></span>
                <div class="wl-body">
                  <b>{{ w.date }}</b>
                  <small>体重记录</small>
                </div>
                <span class="wl-val">{{ w.w }}<em v-if="w.delta">{{ w.delta }}</em></span>
              </div>
            </div>
            <div v-else class="rp-mini-empty"><span v-html="IC.scaleSm"></span>暂无体重记录</div>
          </div>

          <!-- 识别记录 -->
          <div v-else class="rp-panel">
            <div v-if="activePet.recognizes.length" class="r-log">
              <div v-for="(r, i) in activePet.recognizes" :key="i" class="rl-row">
                <span class="rl-dot"></span>
                <div class="rl-body">
                  <b>{{ r.breed }}</b>
                  <small>{{ r.time }}</small>
                </div>
                <span class="rl-conf">{{ r.conf }}</span>
              </div>
            </div>
            <div v-else class="rp-mini-empty"><span v-html="IC.cameraSm"></span>暂无识别记录</div>
          </div>
        </section>

        <!-- 最近健康提醒 -->
        <section class="tip-card">
          <div class="tip-head">
            <b>最近健康提醒</b>
            <a>全部提醒 <span>›</span></a>
          </div>
          <div v-for="(t, i) in reminders" :key="i" class="tip-row">
            <span class="tip-ic" :style="{ background: t.bg }" v-html="t.icon"></span>
            <div class="tip-body">
              <b>{{ t.pet }}　{{ t.title }}</b>
              <small>{{ t.desc }}</small>
            </div>
            <span class="tip-date">{{ t.date }}</span>
          </div>
        </section>

        <!-- AI 宠物助手 -->
        <section class="ai-entry" @click="goChat">
          <span class="ai-ic" v-html="IC.robot"></span>
          <div class="ai-txt">
            <b>AI宠物助手</b>
            <small>有任何养宠问题？我来帮你解答！</small>
          </div>
          <span class="ai-arrow" v-html="IC.chevron"></span>
        </section>
      </aside>
    </div>

    <!-- 图片预览 -->
    <el-image-viewer
      v-if="viewerVisible"
      :url-list="[viewerUrl]"
      @close="viewerVisible = false"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { myRecords } from '@/api/recognition'
import { listMyArchives } from '@/api/archive'
import { getBreedByName } from '@/api/breed'

const router = useRouter()
const API_BASE = import.meta.env.VITE_API_BASE || '/api'

/* ==================== 图标 ==================== */
const IC = {
  paw: '<svg viewBox="0 0 24 24"><ellipse cx="12" cy="14.8" rx="4.1" ry="3.3" fill="currentColor"/><circle cx="6.9" cy="10.8" r="1.7" fill="currentColor"/><circle cx="10.1" cy="8" r="1.7" fill="currentColor"/><circle cx="13.9" cy="8" r="1.7" fill="currentColor"/><circle cx="17.1" cy="10.8" r="1.7" fill="currentColor"/></svg>',
  pawSm: '<svg viewBox="0 0 24 24"><ellipse cx="12" cy="14.8" rx="3.6" ry="2.9" fill="currentColor"/><circle cx="7.4" cy="11.2" r="1.5" fill="currentColor"/><circle cx="10.2" cy="8.8" r="1.5" fill="currentColor"/><circle cx="13.8" cy="8.8" r="1.5" fill="currentColor"/><circle cx="16.6" cy="11.2" r="1.5" fill="currentColor"/></svg>',
  camera: '<svg viewBox="0 0 24 24"><path d="M4 8.5h3.2l1.2-1.8h7.2l1.2 1.8H20a1.5 1.5 0 0 1 1.5 1.5v8A1.5 1.5 0 0 1 20 19.5H4a1.5 1.5 0 0 1-1.5-1.5v-8A1.5 1.5 0 0 1 4 8.5z" fill="rgba(255,255,255,.25)"/><circle cx="12" cy="13.8" r="3.4" fill="none" stroke="#fff" stroke-width="1.9"/><circle cx="17.2" cy="10.6" r=".8" fill="#fff"/></svg>',
  cameraTab: '<svg viewBox="0 0 24 24"><path d="M4 8.5h3.2l1.2-1.8h7.2l1.2 1.8H20a1.5 1.5 0 0 1 1.5 1.5v8A1.5 1.5 0 0 1 20 19.5H4a1.5 1.5 0 0 1-1.5-1.5v-8A1.5 1.5 0 0 1 4 8.5z" fill="none" stroke="currentColor" stroke-width="1.8"/><circle cx="12" cy="13.8" r="3.2" fill="none" stroke="currentColor" stroke-width="1.7"/></svg>',
  cameraSm: '<svg viewBox="0 0 24 24"><path d="M4 8.5h3.2l1.2-1.8h7.2l1.2 1.8H20a1.5 1.5 0 0 1 1.5 1.5v8A1.5 1.5 0 0 1 20 19.5H4a1.5 1.5 0 0 1-1.5-1.5v-8A1.5 1.5 0 0 1 4 8.5z" fill="none" stroke="currentColor" stroke-width="1.8"/><circle cx="12" cy="13.8" r="3.2" fill="none" stroke="currentColor" stroke-width="1.7"/></svg>',
  growthDoc: '<svg viewBox="0 0 24 24"><path d="M6 3.8h9l4 4V20a1.2 1.2 0 0 1-1.2 1.2H6A1.2 1.2 0 0 1 4.8 20V5A1.2 1.2 0 0 1 6 3.8z" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linejoin="round"/><path d="M8.6 14.8l2.3-2.6 2 1.8 2.6-3.2" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"/></svg>',
  chartUp: '<svg viewBox="0 0 24 24"><path d="M4 19V5M4 19h16M8 16v-4.5M12 16V8.5M16 16v-6.5" fill="none" stroke="currentColor" stroke-width="2.1" stroke-linecap="round"/><path d="M15.2 6.2H19V10" fill="none" stroke="currentColor" stroke-width="2.1" stroke-linecap="round" stroke-linejoin="round"/></svg>',
  shieldHeart: '<svg viewBox="0 0 24 24"><path d="M12 3.2l7 2.6v5.7c0 4.5-3 7.6-7 9.3-4-1.7-7-4.8-7-9.3V5.8z" fill="currentColor"/><path d="M12 15.6s-3.4-2-3.4-4.1c0-1.2 1-2 2-2 .6 0 1.1.3 1.4.8.3-.5.8-.8 1.4-.8 1 0 2 .8 2 2 0 2.1-3.4 4.1-3.4 4.1z" fill="#fff"/></svg>',
  star: '<svg viewBox="0 0 24 24"><path d="M12 3.4l2.5 5.1 5.6.8-4 4 1 5.6L12 16.3l-5.1 2.6 1-5.6-4-4 5.6-.8z" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linejoin="round"/></svg>',
  starSolid: '<svg viewBox="0 0 24 24"><path d="M12 3.4l2.5 5.1 5.6.8-4 4 1 5.6L12 16.3l-5.1 2.6 1-5.6-4-4 5.6-.8z" fill="#ffc53d"/></svg>',
  calendar: '<svg viewBox="0 0 24 24"><rect x="4" y="5.5" width="16" height="14.5" rx="2.5" fill="none" stroke="currentColor" stroke-width="1.7"/><path d="M8 3.8v2.8M16 3.8v2.8M4 9.8h16" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round"/></svg>',
  docEmpty: '<svg viewBox="0 0 24 24"><path d="M6 3.8h9l4 4V20a1.2 1.2 0 0 1-1.2 1.2H6A1.2 1.2 0 0 1 4.8 20V5A1.2 1.2 0 0 1 6 3.8z" fill="none" stroke="currentColor" stroke-width="1.7" stroke-linejoin="round"/><path d="M14.5 4v4.2H18.6M8.8 13h6.4M8.8 16h4.2" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round"/></svg>',
  edit: '<svg viewBox="0 0 24 24"><path d="M4 20l4.6-1L19 8.6a2.1 2.1 0 0 0-3-3L5.6 16 4 20z" fill="none" stroke="currentColor" stroke-width="1.7" stroke-linejoin="round"/><path d="M13.8 6.8l3 3" fill="none" stroke="currentColor" stroke-width="1.7"/></svg>',
  heartSm: '<svg viewBox="0 0 24 24"><path d="M12 19s-6.4-3.9-8.2-7.5C2.6 8.9 4.3 6.2 7 6.2c1.7 0 2.9.9 3.7 2h2.6c.8-1.1 2-2 3.7-2 2.7 0 4.4 2.7 3.2 5.3C18.4 15.1 12 19 12 19z" fill="currentColor"/></svg>',
  shieldSm: '<svg viewBox="0 0 24 24"><path d="M12 3.2l7 2.6v5.7c0 4.5-3 7.6-7 9.3-4-1.7-7-4.8-7-9.3V5.8z" fill="currentColor"/><path d="M8.9 11.9l2.1 2.1 4-4.2" fill="none" stroke="#fff" stroke-width="1.9" stroke-linecap="round" stroke-linejoin="round"/></svg>',
  pillSm: '<svg viewBox="0 0 24 24"><rect x="3.8" y="8.6" width="16.4" height="6.8" rx="3.4" transform="rotate(-35 12 12)" fill="currentColor"/><path d="M8.4 8.9l6.7 6.2" stroke="#fff" stroke-width="1.5" stroke-linecap="round"/></svg>',
  scaleSm: '<svg viewBox="0 0 24 24"><path d="M12 7a5 5 0 1 0 0 10 5 5 0 0 0 0-10zm0 3.2a1.8 1.8 0 1 1 0 3.6 1.8 1.8 0 0 1 0-3.6z" fill="none" stroke="currentColor" stroke-width="1.7"/><path d="M12 7V4.6m-2.4 0h4.8" stroke="currentColor" stroke-width="1.7" stroke-linecap="round"/></svg>',
  robot: '<svg viewBox="0 0 24 24"><path d="M12 3.6v2.6M9.6 4.9h4.8" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round"/><rect x="6.4" y="7.8" width="11.2" height="10.6" rx="3.2" fill="currentColor"/><circle cx="9.9" cy="12.6" r="1.35" fill="#fff"/><circle cx="14.1" cy="12.6" r="1.35" fill="#fff"/><path d="M9.8 16h4.4" stroke="#fff" stroke-width="1.5" stroke-linecap="round"/><path d="M3.8 12.6h2.6M17.6 12.6h2.6" fill="none" stroke="currentColor" stroke-width="1.7" stroke-linecap="round"/></svg>',
  chevron: '<svg viewBox="0 0 24 24"><path d="M9 5l7 7-7 7" fill="none" stroke="currentColor" stroke-width="2.1" stroke-linecap="round" stroke-linejoin="round"/></svg>',
  syringe: '<svg viewBox="0 0 24 24"><path d="M14.5 4.2l5.3 5.3M16.6 2.1l5.3 5.3M17.2 6.9l-2.1 2.1-5.6-5.6 2.1-2.1zM9.5 3.4l5.6 5.6-8.4 8.4H3.6v-3.1zM6.7 17.4l2.8 2.8M4.6 19.5l2.8 2.8" fill="none" stroke="#fff" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round"/></svg>',
  pill: '<svg viewBox="0 0 24 24"><rect x="4" y="8.8" width="16" height="6.4" rx="3.2" transform="rotate(-38 12 12)" fill="currentColor"/><path d="M8.6 9l6.4 6" stroke="#fff" stroke-width="1.5" stroke-linecap="round"/></svg>',
  stetho: '<svg viewBox="0 0 24 24"><path d="M5 4.5v4.2a4.8 4.8 0 0 0 9.6 0V4.5M5 4.5H3.4m11.2 0h1.6M9.8 13.5v2.4a4.6 4.6 0 0 0 9.2 0v-1.9" fill="none" stroke="#fff" stroke-width="1.7" stroke-linecap="round"/><circle cx="19" cy="11.6" r="1.7" fill="#fff"/></svg>',
  wave: '<svg class="wave-svg" viewBox="0 0 1440 120" preserveAspectRatio="none"><path d="M0 78 C 200 118 380 40 600 70 C 820 100 980 118 1200 80 C 1320 60 1400 70 1440 76 L1440 120 L0 120 Z" fill="#dcecf7" opacity=".55"/><path d="M0 94 C 240 124 420 70 660 92 C 900 114 1080 120 1280 98 C 1360 90 1410 94 1440 96 L1440 120 L0 120 Z" fill="#cfe6f2" opacity=".6"/></svg>'
}

/* ==================== AI 配图（跨页复用同一形象） ==================== */
const t2i = (p, s = 'square_hd') =>
  'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=' + encodeURIComponent(p) + '&image_size=' + s
const IMG = {
  heroDogCat: t2i('Adorable golden retriever dog and grey tabby white cat sitting close together, bright modern living room with green plants and big window, soft natural sunlight, warm bokeh background, professional pet photography', 'landscape_16_9'),
  dog: t2i('Happy golden retriever dog head portrait with tongue out smiling, soft cream studio background, professional pet photography, square avatar'),
  cat: t2i('Cute grey and white tabby Chinese domestic cat portrait with green eyes, soft neutral background, professional pet photography, square avatar'),
  bichon: t2i('Adorable white bichon frise dog portrait smiling, soft light blue pastel background, professional pet photography, square avatar'),
  g1: t2i('Golden retriever dog portrait with blue AI scanning frame overlay, soft background, professional pet photo'),
  c2: t2i('Grey tabby cat sitting on windowsill looking away, warm light, professional pet photography'),
  b2: t2i('White bichon frise dog playing in park with flowers, professional pet photography'),
  gGrass: t2i('Golden retriever dog sitting on grass smiling, outdoor park, professional pet photography'),
  catCheck: t2i('Veterinarian doctor examining a tabby cat with thermometer at modern bright clinic, cat health check, professional pet photography'),
  sideArt: t2i('cute cartoon illustration of two happy fluffy puppies sitting together, soft blue lavender pastel background with light leaves, simple flat childrens book illustration style, gentle colors')
}

/* ==================== 类型体系 ==================== */
const TYPE_META = {
  recognize: { name: '识别记录' },
  growth: { name: '成长记录' },
  health: { name: '健康记录' }
}
function typeName(t) {
  return TYPE_META[t]?.name || '记录'
}

const menus = [
  { key: 'recognize', name: '识别记录', icon: IC.cameraTab },
  { key: 'growth', name: '成长记录', icon: IC.growthDoc },
  { key: 'health', name: '健康记录', icon: IC.shieldHeart },
  { key: 'favorite', name: '我的收藏', icon: IC.star }
]
const typeTabs = [
  { key: 'all', name: '全部记录', icon: IC.docEmpty },
  { key: 'recognize', name: '识别记录', icon: IC.cameraTab },
  { key: 'growth', name: '成长记录', icon: IC.chartUp },
  { key: 'health', name: '健康记录', icon: IC.shieldHeart }
]
const rpTabList = [
  { key: 'curve', name: '成长曲线' },
  { key: 'weight', name: '体重记录' },
  { key: 'recognize', name: '识别记录' }
]

/* ==================== 演示宠物 ==================== */
function demoPets() {
  return [
    {
      _id: 'demo-1', id: null, petName: '豆豆', breed: '金毛寻回犬', gender: '公', age: '2岁',
      avatar: IMG.dog, petType: '狗',
      series: [29.8, 28.9, 30.1, 30.5, 32.2, 32.5],
      lastWeight: '32.5',
      weightLogs: [
        { date: '2025-06-20', w: '32.5kg', delta: '+2.3kg' },
        { date: '2025-05-18', w: '32.2kg', delta: '+1.7kg' },
        { date: '2025-04-10', w: '30.5kg', delta: '+0.4kg' },
        { date: '2025-03-06', w: '30.1kg', delta: '+1.2kg' }
      ],
      recognizes: [
        { breed: '金毛寻回犬', conf: '98.7%', time: '2025-06-28 14:32' },
        { breed: '金毛寻回犬', conf: '96.3%', time: '2025-05-20 15:16' },
        { breed: '金毛寻回犬', conf: '95.2%', time: '2025-04-12 10:24' }
      ]
    },
    {
      _id: 'demo-2', id: null, petName: '咪咪', breed: '中华田园猫', gender: '母', age: '1岁',
      avatar: IMG.cat, petType: '猫',
      series: [3.2, 3.3, 3.4, 3.6, 3.7, 3.8],
      lastWeight: '3.8',
      weightLogs: [
        { date: '2025-06-15', w: '4.2kg', delta: '+0.4kg' },
        { date: '2025-05-12', w: '3.8kg', delta: '+0.2kg' },
        { date: '2025-04-08', w: '3.6kg', delta: '+0.2kg' }
      ],
      recognizes: [
        { breed: '中华田园猫', conf: '96.3%', time: '2025-06-27 16:20' },
        { breed: '中华田园猫', conf: '94.8%', time: '2025-05-18 11:02' },
        { breed: '中华田园猫', conf: '93.1%', time: '2025-04-09 18:40' }
      ]
    },
    {
      _id: 'demo-3', id: null, petName: '乐乐', breed: '比熊犬', gender: '公', age: '3岁',
      avatar: IMG.bichon, petType: '狗',
      series: [5.1, 5.2, 5.3, 5.4, 5.4, 5.5],
      lastWeight: '5.5',
      weightLogs: [
        { date: '2025-06-06', w: '5.5kg', delta: '+0.1kg' },
        { date: '2025-05-06', w: '5.4kg', delta: '0kg' },
        { date: '2025-04-05', w: '5.4kg', delta: '+0.1kg' }
      ],
      recognizes: [
        { breed: '比熊犬', conf: '95.6%', time: '2025-06-25 11:17' },
        { breed: '比熊犬', conf: '95.4%', time: '2025-05-16 17:30' },
        { breed: '比熊犬', conf: '94.2%', time: '2025-04-08 09:46' }
      ]
    }
  ]
}

/* ==================== 演示记录 ==================== */
function demoRecords() {
  const [dog, cat, bichon] = [IMG.dog, IMG.cat, IMG.bichon]
  return [
    {
      rid: 'd-r1', id: null, type: 'recognize', fav: true,
      thumb: IMG.g1, title: '金毛寻回犬',
      metaSegs: ['识别准确率 98.7%', '年龄：2岁左右'],
      petName: '豆豆', petAvatar: dog, time: '2025-06-28 14:32', date: '2025-06-28'
    },
    {
      rid: 'd-r2', id: null, type: 'recognize', fav: true,
      thumb: IMG.c2, title: '中华田园猫',
      metaSegs: ['识别准确率 96.3%', '年龄：1岁左右'],
      petName: '咪咪', petAvatar: cat, time: '2025-06-27 16:20', date: '2025-06-27'
    },
    {
      rid: 'd-r3', id: null, type: 'recognize', fav: false,
      thumb: IMG.b2, title: '比熊犬',
      metaSegs: ['识别准确率 95.6%', '年龄：3岁左右'],
      petName: '乐乐', petAvatar: bichon, time: '2025-06-25 11:17', date: '2025-06-25'
    },
    {
      rid: 'd-g1', id: null, type: 'growth', fav: true,
      thumb: IMG.gGrass, title: '金毛寻回犬成长记录',
      metaSegs: ['体重：32.5kg', '体长：55cm', '健康良好'],
      petName: '豆豆', petAvatar: dog, time: '2025-06-20 09:45', date: '2025-06-20'
    },
    {
      rid: 'd-h1', id: null, type: 'health', fav: false,
      thumb: IMG.catCheck, title: '猫咪体检记录',
      metaSegs: ['体重：4.2kg', '体温：38.5℃', '状态：健康'],
      petName: '咪咪', petAvatar: cat, time: '2025-06-15 10:30', date: '2025-06-15'
    }
  ]
}

const reminders = [
  { pet: '豆豆', title: '疫苗接种', desc: '距下次接种还有 3 天', date: '2025-06-15', bg: 'linear-gradient(135deg,#ffc45c,#ff9f43)', icon: IC.syringe },
  { pet: '咪咪', title: '驱虫', desc: '距下次驱虫还有 17 天', date: '2025-06-21', bg: 'linear-gradient(135deg,#b19bff,#8b5cf6)', icon: IC.pill },
  { pet: '乐乐', title: '体检', desc: '距下次体检还有 12 天', date: '2025-06-27', bg: 'linear-gradient(135deg,#7db8ff,#4c8df0)', icon: IC.stetho }
]

/* ==================== 页面状态 ==================== */
const pets = ref(demoPets())
const records = ref(demoRecords())
const activePetId = ref('demo-1')
const activeType = ref('all')
const rpTab = ref('curve')
const dateRange = ref(['2025-05-01', '2025-06-30'])
const petFilter = ref('')
const loading = ref(false)
const viewerVisible = ref(false)
const viewerUrl = ref('')
const listPanelRef = ref(null)
const recognizeTotal = ref(null)
let dateTouched = false

const statCards = computed(() => [
  { key: 'recognize', name: '识别记录', value: recognizeTotal.value ?? 28, unit: '次', icon: IC.cameraTab, bg: 'linear-gradient(135deg,#7db8ff,#4c8df0)', deltaText: '较上月 +5' },
  { key: 'growth', name: '成长记录', value: 12, unit: '条', icon: IC.chartUp, bg: 'linear-gradient(135deg,#3fd9a6,#12b886)', deltaText: '较上月 +3' },
  { key: 'health', name: '健康记录', value: 8, unit: '条', icon: IC.shieldHeart, bg: 'linear-gradient(135deg,#ff93b5,#f06292)', deltaText: '较上月 +2' },
  { key: 'favorite', name: '收藏内容', value: 6, unit: '条', icon: IC.star, bg: 'linear-gradient(135deg,#ffd05c,#ffb020)', deltaText: '较上月 +1' }
])

const petOptions = computed(() => pets.value)
const activePet = computed(() => pets.value.find(p => p._id === activePetId.value) || pets.value[0])

/* 右栏体重图（猫/小型犬刻度自适应） */
const chart = computed(() => {
  const series = activePet.value?.series || [0, 0, 0, 0, 0, 0]
  const hasWeight = series.every(v => v > 0)
  const maxV = Math.max(...series)
  const minV = Math.min(...series)
  let lo, hi
  if (maxV >= 20) { lo = Math.floor(minV - 2); hi = Math.ceil(maxV + 3) }
  else { lo = Math.max(0, Number((minV - 1).toFixed(1))); hi = Number((maxV + 1).toFixed(1)) }
  if (hi - lo < 4) { hi = lo + 4 }
  const left = 26, right = 246, top = 16, bottom = 100
  const x = (i) => left + i * (right - left) / (series.length - 1)
  const y = (v) => bottom - (v - lo) / (hi - lo) * (bottom - top)
  const pts = series.map((v, i) => ({ x: x(i), y: y(v) }))
  const line = pts.map((p, i) => `${i === 0 ? 'M' : 'L'}${p.x.toFixed(1)},${p.y.toFixed(1)}`).join(' ')
  const area = `${line} L${right},${bottom} L${left},${bottom} Z`
  const ticks = 5
  const yTicks = Array.from({ length: ticks }, (_, i) => {
    const v = hi - (hi - lo) * i / (ticks - 1)
    return { v: Number(v.toFixed(1)), y: y(v) }
  })
  const months = ['1月', '2月', '3月', '4月', '5月', '6月']
  return { hasWeight, points: pts, line, area, yTicks, months, last: pts[pts.length - 1] }
})

const emptyTitle = computed(() => {
  if (activeType.value === 'favorite') return '还没有收藏内容'
  if (petFilter.value) return `${petFilter.value} 暂时没有相关记录`
  return `${typeName(activeType.value)}为空`
})
const emptyTip = computed(() => {
  if (activeType.value === 'favorite') return '在记录上点击收藏，重要时刻随时回看'
  if (dateRange.value && dateRange.value.length === 2) return '当前筛选条件下没有记录，试试调整时间范围'
  return '换个分类或筛选条件看看吧'
})

const filteredRecords = computed(() => {
  return records.value.filter(r => {
    if (activeType.value === 'favorite') {
      if (!r.fav) return false
    } else if (activeType.value !== 'all' && r.type !== activeType.value) {
      return false
    }
    if (petFilter.value && r.petName !== petFilter.value) return false
    if (dateRange.value && dateRange.value.length === 2) {
      const [s, e] = dateRange.value
      if (s && r.date < s) return false
      if (e && r.date > e) return false
    }
    return true
  })
})

/* ==================== 交互 ==================== */
function setType(key, scroll = false) {
  activeType.value = key
  if (scroll && listPanelRef.value) {
    listPanelRef.value.scrollIntoView({ behavior: 'smooth', block: 'start' })
  }
}
function selectPet(id) {
  activePetId.value = id
  const p = pets.value.find(x => x._id === id)
  if (p) petFilter.value = p.petName
}
function onDateTouch() {
  dateTouched = true
}
function goArchive() {
  router.push('/archive')
}
function goChat() {
  router.push('/chat')
}
function previewImg(r) {
  if (!r.thumb) return
  viewerUrl.value = r.thumb
  viewerVisible.value = true
}
async function viewDetail(r) {
  if (r.type === 'recognize' && r.id && r.breed) {
    try {
      const res = await getBreedByName(r.breed)
      if (res.data && res.data.id) {
        router.push(`/breed/${res.data.id}`)
        return
      }
    } catch (e) { /* ignore */ }
    ElMessage.info('未找到该品种的科普信息')
    return
  }
  if (r.type === 'growth' || r.type === 'health') {
    router.push('/archive')
    return
  }
  if (r.thumb) {
    viewerUrl.value = r.thumb
    viewerVisible.value = true
    return
  }
  ElMessage.info('该记录暂无详情')
}

/* ==================== 真实 API 渐进增强 ==================== */
function resolveImg(url) {
  if (!url) return ''
  if (url.startsWith('http')) return url
  return API_BASE + url
}
function formatTime(t) {
  if (!t) return ''
  return String(t).replace('T', ' ').substring(0, 16)
}
function fallbackPhoto(type) {
  return type === '猫' ? IMG.cat : IMG.dog
}

function mapArchive(item) {
  return {
    _id: 'real-' + item.id,
    id: item.id,
    petName: item.petName || '我的宠物',
    breed: item.breed || '未知品种',
    gender: item.gender === '母' ? '母' : (item.gender === '公' ? '公' : '公'),
    age: item.age || '',
    petType: item.petType || '狗',
    avatar: item.photoUrl ? resolveImg(item.photoUrl) : fallbackPhoto(item.petType),
    series: [0, 0, 0, 0, 0, 0],
    lastWeight: '',
    weightLogs: [],
    recognizes: []
  }
}

function mapRecognize(x, i) {
  const type = x.petType === '猫' ? '猫' : '狗'
  const pet = pets.value.find(p => p.petType === type)
  const confidence = x.confidence != null ? `${x.confidence}%` : '—'
  const segs = [`识别准确率 ${confidence}`]
  if (x.petType) segs.push(`类型：${x.petType}`)
  return {
    rid: 'real-' + x.id,
    id: x.id,
    type: 'recognize',
    fav: false,
    thumb: x.imageUrl ? resolveImg(x.imageUrl) : (x.petType === '猫' ? IMG.c2 : IMG.g1),
    title: (x.success === 0 || x.success === false) ? '识别失败' : (x.breed || '未知品种'),
    breed: x.breed || '',
    metaSegs: segs,
    petName: pet ? pet.petName : '',
    petAvatar: pet ? pet.avatar : IMG.dog,
    time: formatTime(x.createTime),
    date: formatTime(x.createTime).substring(0, 10)
  }
}

async function loadAll() {
  loading.value = true
  let realRecognizes = []
  let realTotal = 0
  /* 档案 */
  try {
    const ar = await listMyArchives()
    const al = ar.data || []
    if (Array.isArray(al) && al.length) {
      pets.value = al.map(mapArchive).concat(pets.value).slice(0, 6)
      activePetId.value = pets.value[0]._id
    }
  } catch (e) { /* 保留演示宠物 */ }
  /* 识别记录 */
  try {
    const rr = await myRecords({ page: 1, size: 9 })
    realRecognizes = (rr.data?.list || []).map(mapRecognize)
    realTotal = rr.data?.total || 0
  } catch (e) { /* 保留演示记录 */ }

  if (realRecognizes.length) {
    recognizeTotal.value = realTotal
    /* 真实识别记录 + 演示成长/健康记录（后端暂无该模块，保证高保真呈现） */
    const demoGH = records.value.filter(r => r.type !== 'recognize')
    records.value = realRecognizes.concat(demoGH)
    if (!dateTouched) dateRange.value = null
    petFilter.value = ''
  }
  loading.value = false
}

onMounted(loadAll)
</script>

<style scoped>
/* ==================== 骨架 ==================== */
.records-page {
  width: 100%;
  max-width: none;
  margin: 0;
  padding: 18px clamp(16px, 2vw, 32px) 40px;
}
.records-layout {
  display: grid;
  grid-template-columns: 208px minmax(0, 1fr) 300px;
  gap: 18px;
  align-items: start;
}

/* ==================== 左栏 ==================== */
.rec-side {
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
/* 我的宠物 */
.pets-box {
  background: #fff;
  border: 1px solid #edf0f8;
  border-radius: 16px;
  box-shadow: 0 10px 28px rgba(94, 110, 190, .06);
  padding: 13px 12px 10px;
}
.pb-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 3px 9px;
}
.pb-head b {
  font-size: 14px;
  color: #2c365e;
  font-weight: 800;
}
.pb-head a {
  font-size: 12px;
  color: #8b95bd;
  cursor: pointer;
  white-space: nowrap;
}
.pb-head a:hover {
  color: #5b63f0;
}
.pet-mini {
  display: flex;
  gap: 10px;
  padding: 9px 8px;
  border-radius: 12px;
  cursor: pointer;
  border: 1.5px solid transparent;
  transition: background .2s, border-color .2s;
}
.pet-mini + .pet-mini {
  border-top: 1px dashed #f0f2f9;
  border-radius: 0;
}
.pet-mini:hover {
  background: #f7f9ff;
}
.pet-mini.on {
  background: linear-gradient(135deg, #f3f5ff, #ecefff);
  border-color: #cdd6ff;
}
.pet-mini img {
  flex: none;
  width: 46px;
  height: 46px;
  border-radius: 12px;
  object-fit: cover;
}
.pm-info {
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 2px;
}
.pm-name {
  display: flex;
  align-items: center;
  gap: 5px;
}
.pm-name b {
  font-size: 14px;
  color: #2c365e;
  font-weight: 800;
}
.pm-name i {
  font-style: normal;
  font-size: 12px;
}
.ic-male {
  color: #4c8df0;
}
.ic-female {
  color: #f06292;
}
.pm-info > small {
  font-size: 11.5px;
  color: #96a0bf;
}
.pm-health {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 11px;
  color: #12b886;
  font-weight: 700;
}
.pm-health i {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: #1fd49a;
  box-shadow: 0 0 0 3px rgba(31, 212, 154, .15);
}
/* 底部装饰 */
.side-art {
  position: relative;
  border-radius: 16px;
  background: linear-gradient(180deg, #f3f6ff 0%, #e9f0ff 60%, #e6f5f1 100%);
  border: 1px solid #e8edfb;
  padding: 8px 8px 0;
  overflow: hidden;
}
.side-art img {
  width: 100%;
  display: block;
  border-radius: 12px 12px 0 0;
}
.hand-note {
  position: absolute;
  top: 10px;
  left: 0;
  right: 0;
  text-align: center;
  font-family: 'Comic Sans MS', 'STKaiti', cursive;
  font-size: 15px;
  line-height: 1.5;
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
.rec-main {
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* Hero */
.rec-hero {
  position: relative;
  height: 196px;
  border-radius: 22px;
  overflow: hidden;
  background: linear-gradient(120deg, #eaf3ff 0%, #e6f0ff 40%, #e8f6f1 100%);
  box-shadow: 0 14px 34px rgba(94, 110, 190, .1);
}
.hero-left {
  position: relative;
  z-index: 2;
  padding: 38px 0 0 34px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.hero-logo {
  width: 58px;
  height: 58px;
  border-radius: 18px;
  background: linear-gradient(135deg, #7c8df6, #5b63f0);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 10px 22px rgba(105, 105, 230, .35);
}
.hero-logo svg {
  width: 32px;
  height: 32px;
}
.rec-hero h1 {
  margin: 2px 0 0;
  font-size: 31px;
  font-weight: 900;
  color: #26305a;
  letter-spacing: 1px;
}
.rec-hero p {
  margin: 0;
  font-size: 14.5px;
  line-height: 1.7;
  color: #6b759c;
}
.hp-paw {
  display: inline-block;
  width: 17px;
  height: 17px;
  color: #7c8df6;
  vertical-align: -3px;
  margin-left: 3px;
}
.hp-paw svg {
  width: 100%;
  height: 100%;
}
.hero-img {
  position: absolute;
  right: 0;
  bottom: 0;
  height: 100%;
  width: 50%;
  object-fit: cover;
  object-position: center 25%;
  -webkit-mask-image: linear-gradient(90deg, transparent 0%, #000 30%, #000 100%);
  mask-image: linear-gradient(90deg, transparent 0%, #000 30%, #000 100%);
}
.hero-hand {
  position: absolute;
  top: 26px;
  right: 44px;
  z-index: 3;
  margin: 0;
  font-family: 'Comic Sans MS', 'STKaiti', cursive;
  font-size: 18px;
  line-height: 1.55;
  color: #7e88c4;
  transform: rotate(11deg);
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
  height: 60px;
  display: block;
}

/* 统计卡 */
.stat-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 14px;
}
.stat-card {
  background: #fff;
  border: 1px solid #edf0f8;
  border-radius: 16px;
  box-shadow: 0 10px 28px rgba(94, 110, 190, .06);
  padding: 16px 15px;
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  transition: transform .2s, box-shadow .2s, border-color .2s;
}
.stat-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 16px 32px rgba(94, 110, 190, .13);
}
.stat-card.on {
  border-color: #b9c6ff;
  box-shadow: 0 14px 30px rgba(105, 105, 230, .2);
}
.st-ic {
  flex: none;
  width: 48px;
  height: 48px;
  border-radius: 15px;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8px 16px rgba(94, 110, 190, .18);
}
.st-ic svg {
  width: 26px;
  height: 26px;
}
.st-info {
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 2px;
}
.st-info small {
  font-size: 12.5px;
  color: #8b95bd;
  font-weight: 600;
}
.st-info b {
  font-size: 25px;
  line-height: 1.15;
  color: #26305a;
  font-weight: 900;
}
.st-info b em {
  font-style: normal;
  font-size: 12.5px;
  color: #8b95bd;
  font-weight: 600;
  margin-left: 3px;
}
.st-delta {
  font-size: 11px;
  font-weight: 600;
}
.st-delta.up {
  color: #12b886;
}

/* 筛选+列表面板 */
.list-panel {
  background: #fff;
  border: 1px solid #edf0f8;
  border-radius: 16px;
  box-shadow: 0 10px 28px rgba(94, 110, 190, .06);
  padding: 6px 18px 8px;
}
.filter-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 14px;
  flex-wrap: wrap;
  padding: 10px 0;
  border-bottom: 1px solid #f0f2f9;
}
.type-tabs {
  display: flex;
  align-items: center;
  gap: 26px;
}
.type-tabs button {
  position: relative;
  border: 0;
  background: transparent;
  padding: 8px 1px;
  font-size: 14.5px;
  font-weight: 600;
  color: #8b95bd;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  transition: color .2s;
}
.type-tabs button .tt-ic {
  width: 17px;
  height: 17px;
  display: inline-flex;
}
.type-tabs button .tt-ic svg {
  width: 100%;
  height: 100%;
}
.type-tabs button:hover {
  color: #5b63f0;
}
.type-tabs button.on {
  color: #4c5bd8;
  font-weight: 800;
}
.type-tabs button.on::after {
  content: '';
  position: absolute;
  left: 0;
  right: 0;
  bottom: -11px;
  height: 3px;
  border-radius: 3px 3px 0 0;
  background: linear-gradient(90deg, #7c8df6, #5b63f0);
}
.filter-tools {
  display: flex;
  align-items: center;
  gap: 10px;
}
.range-picker {
  width: 262px;
}
.pet-select {
  width: 118px;
}
.filter-tools :deep(.el-range-editor.el-input__wrapper),
.filter-tools :deep(.el-select__wrapper) {
  border-radius: 10px;
  box-shadow: 0 0 0 1px #e4e8f5 inset;
}
.filter-tools :deep(.el-range-editor.is-active),
.filter-tools :deep(.el-range-editor.is-focus),
.filter-tools :deep(.el-select__wrapper.is-focused) {
  box-shadow: 0 0 0 1px #8b9bf3 inset !important;
}

/* 记录行 */
.rec-rows {
  min-height: 200px;
}
.rec-row {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px 4px;
  border-bottom: 1px solid #f3f5fb;
}
.rec-row:last-child {
  border-bottom: 0;
}
.rr-thumb {
  position: relative;
  flex: none;
  width: 118px;
  height: 88px;
  border-radius: 12px;
  overflow: hidden;
  background: #eef1f8;
  cursor: zoom-in;
}
.rr-thumb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform .3s;
}
.rr-thumb:hover img {
  transform: scale(1.06);
}
.rr-fav {
  position: absolute;
  top: 6px;
  right: 6px;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: rgba(255, 255, 255, .9);
  display: flex;
  align-items: center;
  justify-content: center;
}
.rr-fav svg {
  width: 14px;
  height: 14px;
}
.rr-main {
  flex: 1;
  min-width: 0;
}
.rr-main h4 {
  margin: 0 0 9px;
  font-size: 16.5px;
  font-weight: 800;
  color: #2c365e;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.rr-meta {
  margin: 0;
  font-size: 12.5px;
  color: #96a0bf;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 0;
}
.rr-sep {
  margin: 0 10px;
  color: #d6dcf0;
}
.rr-pet {
  flex: none;
  display: flex;
  align-items: center;
  gap: 7px;
  min-width: 78px;
}
.rr-pet img {
  width: 26px;
  height: 26px;
  border-radius: 50%;
  object-fit: cover;
}
.rr-pet b {
  font-size: 13px;
  color: #45507a;
  font-weight: 700;
}
.rr-time {
  flex: none;
  display: inline-flex;
  align-items: center;
  gap: 5px;
  font-size: 12.5px;
  color: #a5aeca;
  min-width: 142px;
}
.rt-ic {
  width: 14px;
  height: 14px;
  display: inline-flex;
}
.rt-ic svg {
  width: 100%;
  height: 100%;
}
.rr-tag {
  flex: none;
  font-size: 12px;
  font-weight: 700;
  padding: 4px 11px;
  border-radius: 7px;
  min-width: 66px;
  text-align: center;
}
.tag-recognize {
  color: #0ea874;
  background: #e2f7ef;
}
.tag-growth {
  color: #4c6ef0;
  background: #e7ecff;
}
.tag-health {
  color: #f0524d;
  background: #ffeceb;
}
.rr-btn {
  flex: none;
  border: 1.5px solid #dfe4f8;
  background: #fff;
  color: #6d7ce8;
  font-size: 13px;
  font-weight: 700;
  padding: 8px 16px;
  border-radius: 999px;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 3px;
  transition: all .2s;
}
.rr-btn:hover {
  border-color: #aebbf6;
  background: #f5f7ff;
}
.rr-btn span {
  font-size: 15px;
}

/* 空态 */
.rows-empty {
  padding: 56px 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}
.re-ic {
  width: 62px;
  height: 62px;
  border-radius: 50%;
  background: linear-gradient(135deg, #eef1ff, #e2e8ff);
  color: #7c8df6;
  display: flex;
  align-items: center;
  justify-content: center;
}
.re-ic svg {
  width: 28px;
  height: 28px;
}
.rows-empty b {
  font-size: 15.5px;
  color: #525e85;
}
.rows-empty small {
  font-size: 12.5px;
  color: #a5aeca;
}
.re-btn {
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
.rec-right {
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 16px;
  position: sticky;
  top: 18px;
}
/* 宠物卡 */
.rp-card {
  background: #fff;
  border: 1px solid #edf0f8;
  border-radius: 16px;
  box-shadow: 0 10px 28px rgba(94, 110, 190, .06);
  padding: 16px;
}
.rp-top {
  display: flex;
  gap: 13px;
}
.rp-avatar {
  flex: none;
  width: 62px;
  height: 62px;
  border-radius: 16px;
  object-fit: cover;
}
.rp-id {
  flex: 1;
  min-width: 0;
}
.rp-name-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.rp-name-row b {
  font-size: 18px;
  color: #26305a;
  font-weight: 900;
}
.rp-edit {
  display: inline-flex;
  align-items: center;
  gap: 3px;
  font-size: 12px;
  color: #8b95bd;
  cursor: pointer;
}
.rp-edit:hover {
  color: #5b63f0;
}
.rp-edit svg {
  width: 13px;
  height: 13px;
}
.rp-id p {
  margin: 5px 0 9px;
  font-size: 12.5px;
  color: #8b95bd;
}
.rp-id p i {
  font-style: normal;
  font-size: 12px;
  margin: 0 2px;
}
.rp-badges {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}
.rp-badge {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 11.5px;
  font-weight: 700;
  padding: 3.5px 10px;
  border-radius: 999px;
}
.rp-badge svg {
  width: 12px;
  height: 12px;
}
.b-health {
  color: #0ea874;
  background: #e2f7ef;
}
.b-shot {
  color: #4c6ef0;
  background: #e9eeff;
}
.b-worm {
  color: #8b5cf6;
  background: #f2ebff;
}
.rp-tabs {
  margin-top: 14px;
  display: flex;
  border-bottom: 1px solid #eef1f8;
}
.rp-tabs button {
  position: relative;
  flex: 1;
  border: 0;
  background: transparent;
  padding: 9px 0;
  font-size: 13px;
  font-weight: 600;
  color: #96a0bf;
  cursor: pointer;
  transition: color .2s;
}
.rp-tabs button:hover {
  color: #5b63f0;
}
.rp-tabs button.on {
  color: #4c5bd8;
  font-weight: 800;
}
.rp-tabs button.on::after {
  content: '';
  position: absolute;
  left: 18%;
  right: 18%;
  bottom: -1px;
  height: 2.5px;
  border-radius: 3px 3px 0 0;
  background: linear-gradient(90deg, #7c8df6, #5b63f0);
}
.rp-panel {
  padding-top: 12px;
}
.rp-panel-title {
  margin: 0 0 6px;
  font-size: 12.5px;
  font-weight: 700;
  color: #5c6788;
}
.rp-svg {
  width: 100%;
  height: 138px;
  display: block;
}
.rp-chart-empty,
.rp-mini-empty {
  height: 132px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #a5aeca;
  font-size: 12.5px;
  background: linear-gradient(135deg, #f7f9ff, #f2f5ff);
  border: 1px dashed #d9e0fb;
  border-radius: 12px;
}
.rp-chart-empty svg,
.rp-mini-empty svg {
  width: 24px;
  height: 24px;
}
/* 体重记录时间线 */
.wl-row {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 7px 0;
}
.wl-dot {
  flex: none;
  width: 9px;
  height: 9px;
  border-radius: 50%;
  background: #c5cdf0;
  box-shadow: 0 0 0 4px #eef1ff;
}
.wl-dot.first {
  background: #5b63f0;
  box-shadow: 0 0 0 4px #e3e8ff;
}
.wl-body {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 2px;
}
.wl-body b {
  font-size: 13px;
  color: #3d4770;
  font-weight: 700;
}
.wl-body small {
  font-size: 11px;
  color: #a5aeca;
}
.wl-val {
  font-size: 14px;
  font-weight: 800;
  color: #26305a;
}
.wl-val em {
  font-style: normal;
  display: block;
  font-size: 10.5px;
  font-weight: 600;
  color: #12b886;
  text-align: right;
}
/* 识别记录小列表 */
.rl-row {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 0;
}
.rl-dot {
  flex: none;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #7c8df6;
  box-shadow: 0 0 0 4px #ecefff;
}
.rl-body {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 2px;
}
.rl-body b {
  font-size: 13px;
  color: #3d4770;
  font-weight: 700;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.rl-body small {
  font-size: 11px;
  color: #a5aeca;
}
.rl-conf {
  flex: none;
  font-size: 13px;
  font-weight: 800;
  color: #5b63f0;
}

/* 健康提醒 */
.tip-card {
  background: #fff;
  border: 1px solid #edf0f8;
  border-radius: 16px;
  box-shadow: 0 10px 28px rgba(94, 110, 190, .06);
  padding: 16px;
}
.tip-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
}
.tip-head b {
  font-size: 15.5px;
  color: #2c365e;
  font-weight: 800;
}
.tip-head a {
  font-size: 12px;
  color: #8b95bd;
  cursor: pointer;
}
.tip-head a:hover {
  color: #5b63f0;
}
.tip-row {
  display: flex;
  align-items: center;
  gap: 11px;
  padding: 10px 0;
}
.tip-row + .tip-row {
  border-top: 1px dashed #f0f2f9;
}
.tip-ic {
  flex: none;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 6px 14px rgba(94, 110, 190, .16);
}
.tip-ic svg {
  width: 21px;
  height: 21px;
}
.tip-body {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 3px;
}
.tip-body b {
  font-size: 13.5px;
  color: #3d4770;
  font-weight: 800;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.tip-body small {
  font-size: 11.5px;
  color: #96a0bf;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.tip-date {
  flex: none;
  font-size: 11.5px;
  color: #a5aeca;
}

/* AI 入口 */
.ai-entry {
  display: flex;
  align-items: center;
  gap: 12px;
  border-radius: 16px;
  padding: 15px 16px;
  background:
    radial-gradient(120% 120% at 0% 0%, rgba(150, 170, 255, .35), transparent 60%),
    linear-gradient(150deg, #eef1ff 0%, #e7edff 55%, #f0ecff 100%);
  border: 1px solid #e0e6ff;
  box-shadow: 0 12px 30px rgba(109, 110, 200, .14);
  cursor: pointer;
  transition: transform .2s, box-shadow .2s;
}
.ai-entry:hover {
  transform: translateY(-2px);
  box-shadow: 0 16px 34px rgba(109, 110, 200, .22);
}
.ai-entry .ai-ic {
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
.ai-entry .ai-ic svg {
  width: 25px;
  height: 25px;
}
.ai-entry .ai-txt {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 3px;
}
.ai-entry .ai-txt b {
  font-size: 15px;
  color: #2f3a66;
  font-weight: 800;
}
.ai-entry .ai-txt small {
  font-size: 11.5px;
  color: #8b95bd;
}
.ai-arrow {
  flex: none;
  width: 24px;
  height: 24px;
  color: #8b95bd;
}
.ai-arrow svg {
  width: 100%;
  height: 100%;
}

/* ==================== 响应式 ==================== */
@media (max-width: 1380px) {
  .records-layout {
    grid-template-columns: 196px minmax(0, 1fr);
  }
  .rec-right {
    position: static;
    grid-column: 1 / -1;
    display: grid;
    grid-template-columns: repeat(3, minmax(0, 1fr));
    gap: 16px;
  }
}
@media (max-width: 1080px) {
  .records-layout {
    grid-template-columns: minmax(0, 1fr);
  }
  .rec-side {
    position: static;
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
  .pets-box {
    display: none;
  }
  .side-art {
    display: none;
  }
  .stat-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
  .rec-right {
    grid-template-columns: 1fr 1fr;
  }
  .rp-card {
    grid-column: 1 / -1;
  }
  .rr-pet,
  .rr-time {
    min-width: 0;
  }
}
@media (max-width: 680px) {
  .records-page {
    padding: 14px 14px 32px;
  }
  .rec-hero {
    height: 170px;
  }
  .hero-left {
    padding: 26px 0 0 20px;
  }
  .hero-logo {
    width: 46px;
    height: 46px;
  }
  .hero-logo svg {
    width: 26px;
    height: 26px;
  }
  .rec-hero h1 {
    font-size: 23px;
  }
  .rec-hero p {
    font-size: 12.5px;
  }
  .hero-hand {
    display: none;
  }
  .hero-img {
    width: 48%;
  }
  .stat-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 10px;
  }
  .st-ic {
    width: 40px;
    height: 40px;
  }
  .st-info b {
    font-size: 21px;
  }
  .type-tabs {
    gap: 16px;
    overflow-x: auto;
    scrollbar-width: none;
    width: 100%;
  }
  .type-tabs::-webkit-scrollbar {
    display: none;
  }
  .filter-tools {
    width: 100%;
  }
  .range-picker {
    flex: 1;
    width: auto;
    min-width: 0;
  }
  .pet-select {
    width: 104px;
  }
  .rec-row {
    flex-wrap: wrap;
    gap: 10px;
    padding: 14px 2px;
  }
  .rr-thumb {
    width: 96px;
    height: 72px;
  }
  .rr-main {
    flex: 1 1 calc(100% - 112px);
  }
  .rr-btn {
    margin-left: auto;
  }
  .rec-right {
    grid-template-columns: minmax(0, 1fr);
  }
}
</style>
