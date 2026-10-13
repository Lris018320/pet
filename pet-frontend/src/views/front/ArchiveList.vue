<template>
  <div class="arc-page">
    <!-- ================= 左侧栏 ================= -->
    <aside class="arc-side">
      <nav class="side-menu">
        <a class="side-item on">
          <span class="si-ic"><svg viewBox="0 0 24 24"><path d="M12 14.5c3.2 0 6 2.4 6 5 0 1-1.8 1.5-6 1.5s-6-.5-6-1.5c0-2.6 2.8-5 6-5zM7.2 11.6a2.1 2.1 0 1 1 0-4.2 2.1 2.1 0 0 1 0 4.2zm9.6 0a2.1 2.1 0 1 1 0-4.2 2.1 2.1 0 0 1 0 4.2zM4.6 8.4a1.7 1.7 0 1 1 0-3.4 1.7 1.7 0 0 1 0 3.4zm14.8 0a1.7 1.7 0 1 1 0-3.4 1.7 1.7 0 0 1 0 3.4z"/></svg></span>
          我的宠物
        </a>
        <a class="side-item" @click="scrollToAlbum">
          <span class="si-ic"><svg viewBox="0 0 24 24"><rect x="3" y="5.5" width="18" height="13.5" rx="2.4" fill="none" stroke="currentColor" stroke-width="1.8"/><path d="M3 15.5l4.6-4.6 3.4 3.4 3-3 7 6.4" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"/><circle cx="8.6" cy="9.4" r="1.5" fill="currentColor"/></svg></span>
          成长相册
        </a>
        <a class="side-item" @click="scrollTo('health')">
          <span class="si-ic"><svg viewBox="0 0 24 24"><path d="M12 20s-7-4.3-9-8.4C1.5 8.4 3.4 5 6.6 5c1.9 0 3.3 1 4.4 2.6h2C14.1 6 15.5 5 17.4 5c3.2 0 5.1 3.4 3.6 6.6-2 4.1-9 8.4-9 8.4z" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linejoin="round"/></svg></span>
          健康记录
        </a>
        <a class="side-item" @click="router.push('/my-records')">
          <span class="si-ic"><svg viewBox="0 0 24 24"><path d="M5 4.5A1.5 1.5 0 0 1 6.5 3h11A1.5 1.5 0 0 1 19 4.5v15a1.5 1.5 0 0 1-1.5 1.5h-11A1.5 1.5 0 0 1 5 19.5v-15z" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linejoin="round"/><path d="M8.5 8h7M8.5 12h7M8.5 16h4.5" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/></svg></span>
          识别记录
        </a>
      </nav>

      <div class="side-pets-head">
        <b>我的宠物（{{ pets.length }}）</b>
        <button class="add-pet" @click="openDialog()">
          <svg viewBox="0 0 24 24"><path d="M12 5v14M5 12h14" stroke="currentColor" stroke-width="2.2" stroke-linecap="round"/></svg>
          添加宠物
        </button>
      </div>

      <div class="pet-list">
        <div
          v-for="p in pets"
          :key="p._id"
          class="pet-card"
          :class="{ on: activeId === p._id }"
          @click="selectPet(p)"
        >
          <img :src="p.thumb" :alt="p.petName" />
          <div class="pc-info">
            <div class="pc-name">
              {{ p.petName }}
              <span class="sex" :class="p.gender === '母' ? 'f' : 'm'">{{ p.gender === '母' ? '♀' : '♂' }}</span>
            </div>
            <div class="pc-breed">{{ p.breed }}</div>
            <div class="pc-healthy">
              <svg viewBox="0 0 24 24"><circle cx="12" cy="12" r="9" fill="#12b886"/><path d="M7.8 12.2l2.8 2.8 5.6-6" fill="none" stroke="#fff" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"/></svg>
              健康
            </div>
          </div>
          <svg class="pc-arrow" viewBox="0 0 24 24"><path d="M9 5l7 7-7 7" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
        </div>
      </div>

      <!-- 侧边栏底部装饰 -->
      <div class="side-deco">
        <svg class="sd-paw" viewBox="0 0 24 24"><path d="M12 14.5c3.2 0 6 2.4 6 5 0 1-1.8 1.5-6 1.5s-6-.5-6-1.5c0-2.6 2.8-5 6-5zM7.2 11.6a2.1 2.1 0 1 1 0-4.2 2.1 2.1 0 0 1 0 4.2zm9.6 0a2.1 2.1 0 1 1 0-4.2 2.1 2.1 0 0 1 0 4.2zM4.6 8.4a1.7 1.7 0 1 1 0-3.4 1.7 1.7 0 0 1 0 3.4zm14.8 0a1.7 1.7 0 1 1 0-3.4 1.7 1.7 0 0 1 0 3.4z"/></svg>
        <div class="sd-note">用科技守护<br>每一份陪伴 <span>♡</span></div>
      </div>
    </aside>

    <!-- ================= 右侧主区 ================= -->
    <main class="arc-main">
      <!-- 宠物 Hero -->
      <section v-if="active" class="pet-hero">
        <div class="hero-photo">
          <img :src="active.hero" :alt="active.petName" />
        </div>
        <div class="hero-info">
          <div class="hi-name">
            <h2>{{ active.petName }}</h2>
            <span class="cert">
              <svg viewBox="0 0 24 24"><circle cx="12" cy="12" r="9" fill="#fff"/><path d="M7.8 12.2l2.8 2.8 5.6-6" fill="none" stroke="#4f7cf0" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"/></svg>
              已认证
            </span>
          </div>
          <div class="hi-breed">
            {{ active.breed }}
            <span class="sex-big" :class="active.gender === '母' ? 'f' : 'm'">{{ active.gender === '母' ? '♀' : '♂' }}</span>
          </div>
          <div class="hi-tags">
            <span class="ht">
              <span class="ht-ic"><svg viewBox="0 0 24 24"><rect x="4" y="4.5" width="16" height="15.5" rx="2.5" fill="none" stroke="currentColor" stroke-width="1.8"/><path d="M8 3v3M16 3v3M4 9h16" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/><circle cx="9.5" cy="13.5" r="1.2" fill="currentColor"/><circle cx="14.5" cy="13.5" r="1.2" fill="currentColor"/><path d="M9.5 17h5" stroke="currentColor" stroke-width="1.6" stroke-linecap="round"/></svg></span>
              {{ active.age || '年龄待补充' }}
            </span>
            <span class="ht">
              <span class="ht-ic"><svg viewBox="0 0 24 24"><path d="M12 7a5 5 0 1 0 0 10 5 5 0 0 0 0-10zm0 3.2a1.8 1.8 0 1 1 0 3.6 1.8 1.8 0 0 1 0-3.6z" fill="none" stroke="currentColor" stroke-width="1.7"/><path d="M12 7V4.6m-2.4 0h4.8" stroke="currentColor" stroke-width="1.7" stroke-linecap="round"/></svg></span>
              体重 {{ active.weight || '待补充' }}
            </span>
            <span class="ht">
              <span class="ht-ic"><svg viewBox="0 0 24 24"><path d="M12 20s-7-4.3-9-8.4C1.5 8.4 3.4 5 6.6 5c1.9 0 3.3 1 4.4 2.6h2C14.1 6 15.5 5 17.4 5c3.2 0 5.1 3.4 3.6 6.6-2 4.1-9 8.4-9 8.4z" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linejoin="round"/></svg></span>
              性格 {{ active.personality || '待补充' }}
            </span>
          </div>
          <div class="hi-intro">
            <p>{{ active.intro }}</p>
            <span class="intro-edit" title="编辑档案" @click="openDialog(active)">
              <svg viewBox="0 0 24 24"><path d="M4 19.5h4.2L19.2 8.5a2 2 0 0 0-2.8-2.8L5.4 16.7v2.8z" fill="none" stroke="currentColor" stroke-width="1.7" stroke-linejoin="round"/><path d="M14.6 7.4l2.8 2.8" stroke="currentColor" stroke-width="1.7"/></svg>
            </span>
          </div>
        </div>

        <div class="hero-right">
          <span class="health-pill">
            <svg viewBox="0 0 24 24"><path d="M12 20s-7-4.3-9-8.4C1.5 8.4 3.4 5 6.6 5c1.9 0 3.3 1 4.4 2.6h2C14.1 6 15.5 5 17.4 5c3.2 0 5.1 3.4 3.6 6.6-2 4.1-9 8.4-9 8.4z" fill="#fff"/></svg>
            健康状态良好
          </span>
          <div class="hero-note">和你在一起<br>就是最好的时光 <span>♡</span></div>
          <svg class="hero-cloud" viewBox="0 0 200 120"><path d="M40 90c-18 0-30-12-26-27C18 48 32 40 46 44 52 28 76 24 88 40c8-4 24-2 30 10 16-2 28 8 26 22-2 12-14 18-28 18H40z" fill="#f7e9c8" opacity=".75"/></svg>
          <svg class="hero-paw" viewBox="0 0 24 24"><path d="M12 14.5c3.2 0 6 2.4 6 5 0 1-1.8 1.5-6 1.5s-6-.5-6-1.5c0-2.6 2.8-5 6-5zM7.2 11.6a2.1 2.1 0 1 1 0-4.2 2.1 2.1 0 0 1 0 4.2zm9.6 0a2.1 2.1 0 1 1 0-4.2 2.1 2.1 0 0 1 0 4.2zM4.6 8.4a1.7 1.7 0 1 1 0-3.4 1.7 1.7 0 0 1 0 3.4zm14.8 0a1.7 1.7 0 1 1 0-3.4 1.7 1.7 0 0 1 0 3.4z"/></svg>
        </div>
      </section>

      <!-- Tab 栏 -->
      <nav class="arc-tabs">
        <a
          v-for="t in tabs"
          :key="t.key"
          class="arc-tab"
          :class="{ on: activeTab === t.key }"
          @click="onTab(t)"
        >
          <span class="at-ic" v-html="t.icon"></span>
          {{ t.label }}
        </a>
      </nav>

      <!-- 仪表盘三列 -->
      <div class="dash-grid">
        <!-- 左列：基本信息 / AI识别记录 / 成长小记 -->
        <div class="dash-col col-left">
          <!-- 基本信息 -->
          <section ref="baseRef" class="panel arc-card">
            <div class="card-head">
              <div class="ch-title">
                <span class="ch-ic purple"><svg viewBox="0 0 24 24"><rect x="5" y="3.5" width="14" height="17" rx="2.4" fill="none" stroke="#fff" stroke-width="1.8"/><path d="M9 3v2.6M15 3v2.6M5 8.5h14M9 12.5h6M9 16h4" stroke="#fff" stroke-width="1.8" stroke-linecap="round"/></svg></span>
                <b>基本信息</b>
              </div>
              <span class="ch-edit" @click="openDialog(active)">
                <svg viewBox="0 0 24 24"><path d="M4 19.5h4.2L19.2 8.5a2 2 0 0 0-2.8-2.8L5.4 16.7v2.8z" fill="none" stroke="currentColor" stroke-width="1.7" stroke-linejoin="round"/><path d="M14.6 7.4l2.8 2.8" stroke="currentColor" stroke-width="1.7"/></svg>
                编辑
              </span>
            </div>
            <ul class="info-list">
              <li v-for="row in infoRows" :key="row.label">
                <span class="il-ic" v-html="row.icon"></span>
                <span class="il-label">{{ row.label }}</span>
                <b class="il-value" :class="{ muted: row.muted }">{{ row.value }}</b>
              </li>
            </ul>
          </section>

          <!-- AI 识别记录 -->
          <section ref="recognizeRef" class="panel arc-card">
            <div class="card-head">
              <div class="ch-title">
                <span class="ch-ic blue"><svg viewBox="0 0 24 24"><rect x="4.5" y="8" width="15" height="11" rx="3" fill="#fff"/><circle cx="9" cy="13" r="1.4" fill="#4f7cf0"/><circle cx="15" cy="13" r="1.4" fill="#4f7cf0"/><rect x="2.7" y="6.4" width="3.2" height="3.2" rx="1" fill="#fff"/></svg></span>
                <b>AI识别记录</b>
              </div>
              <a class="ch-more" @click="router.push('/my-records')">查看全部 ></a>
            </div>
            <div class="rec-cards">
              <div v-for="(r, i) in activeRecords" :key="i" class="rec-card" @click="router.push('/my-records')">
                <div class="rc-img"><img :src="r.img" :alt="r.breed" />
                  <span class="rc-paw"><svg viewBox="0 0 24 24"><path d="M12 14.5c3.2 0 6 2.4 6 5 0 1-1.8 1.5-6 1.5s-6-.5-6-1.5c0-2.6 2.8-5 6-5zM7.2 11.6a2.1 2.1 0 1 1 0-4.2 2.1 2.1 0 0 1 0 4.2zm9.6 0a2.1 2.1 0 1 1 0-4.2 2.1 2.1 0 0 1 0 4.2zM4.6 8.4a1.7 1.7 0 1 1 0-3.4 1.7 1.7 0 0 1 0 3.4zm14.8 0a1.7 1.7 0 1 1 0-3.4 1.7 1.7 0 0 1 0 3.4z"/></svg></span>
                </div>
                <div class="rc-meta">
                  <b><i>♕</i>{{ r.breed }}</b>
                  <span class="rc-conf">{{ r.confidence }}%</span>
                </div>
                <small>{{ r.time }}</small>
              </div>
              <!-- 数字档案推广 -->
              <div class="rec-promo">
                <p>{{ active.petName }}的数字档案<br>包含基本信息、健康数据、成长记录等</p>
                <button class="promo-btn" @click="viewFullArchive">查看完整档案</button>
              </div>
            </div>
          </section>

          <!-- 成长小记 -->
          <section ref="growthRef" class="panel arc-card">
            <div class="card-head">
              <div class="ch-title">
                <span class="ch-ic indigo"><svg viewBox="0 0 24 24"><rect x="4" y="4.5" width="16" height="15.5" rx="2.4" fill="none" stroke="#fff" stroke-width="1.8"/><path d="M8 3v3M16 3v3M4 9h16" stroke="#fff" stroke-width="1.8" stroke-linecap="round"/><path d="M8.5 13.5h2M13.5 13.5h2M8.5 17h2" stroke="#fff" stroke-width="1.8" stroke-linecap="round"/></svg></span>
                <b>成长小记</b>
              </div>
            </div>
            <div class="growth-wrap">
              <ul class="growth-list">
                <li v-for="(g, i) in activeGrowth" :key="i">
                  <span class="gl-dot"></span>
                  <span class="gl-date">{{ g.date }}</span>
                  <span class="gl-text">{{ g.text }}</span>
                </li>
              </ul>
              <div class="growth-note">
                <p>我的{{ active.petName }}已经陪伴我2年了，<br>希望它一直健康快乐地成长！</p>
                <span class="gn-pen"><svg viewBox="0 0 24 24"><path d="M4 19.5h4.2L19.2 8.5a2 2 0 0 0-2.8-2.8L5.4 16.7v2.8z" fill="none" stroke="currentColor" stroke-width="1.7" stroke-linejoin="round"/></svg></span>
                <span class="gn-heart">♥</span>
              </div>
            </div>
          </section>
        </div>

        <!-- 中列：体重趋势 -->
        <div class="dash-col col-mid">
          <section class="panel arc-card weight-card">
            <div class="card-head">
              <div class="ch-title">
                <span class="ch-ic blue"><svg viewBox="0 0 24 24"><path d="M4 19V5M4 19h16M8 16v-4M12 16V8M16 16v-6" stroke="#fff" stroke-width="2" stroke-linecap="round"/></svg></span>
                <b>体重变化趋势</b>
              </div>
              <div class="range-seg">
                <button
                  v-for="r in ranges"
                  :key="r"
                  :class="{ on: range === r }"
                  @click="range = r"
                >{{ r }}</button>
              </div>
            </div>

            <div class="chart-box">
              <svg v-if="chart.hasData" viewBox="0 0 520 210" class="weight-svg" preserveAspectRatio="none">
                <defs>
                  <linearGradient id="areaGrad" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="0%" stop-color="#6d8bf2" stop-opacity=".28"/>
                    <stop offset="100%" stop-color="#6d8bf2" stop-opacity=".02"/>
                  </linearGradient>
                </defs>
                <!-- 网格 -->
                <g v-for="(y, i) in chart.yTicks" :key="'g'+i">
                  <line :x1="34" :x2="498" :y1="y.y" :y2="y.y" stroke="#eef1f8" stroke-width="1"/>
                  <text :x="14" :y="y.y + 4" font-size="11" fill="#aab4d0">{{ y.v }}</text>
                </g>
                <path :d="chart.area" fill="url(#areaGrad)"/>
                <path :d="chart.line" fill="none" stroke="#5b73e8" stroke-width="2.4" stroke-linecap="round" stroke-linejoin="round"/>
                <g v-for="(pt, i) in chart.points" :key="'p'+i">
                  <circle :cx="pt.x" :cy="pt.y" :r="i === chart.points.length - 1 ? 5 : 3.4" :fill="i === chart.points.length - 1 ? '#5b63f0' : '#5b73e8'" stroke="#fff" stroke-width="1.6"/>
                </g>
                <!-- 末端气泡 -->
                <g>
                  <rect :x="chart.last.x - 30" :y="chart.last.y - 34" width="60" height="24" rx="12" fill="#5b63f0"/>
                  <text :x="chart.last.x" :y="chart.last.y - 18" text-anchor="middle" font-size="11.5" font-weight="700" fill="#fff">{{ active.weight || '32.5kg' }}</text>
                </g>
                <g v-for="(m, i) in chart.months" :key="'m'+i">
                  <text :x="chart.points[i].x" y="200" text-anchor="middle" font-size="11" fill="#aab4d0">{{ m }}</text>
                </g>
                <text x="14" y="20" font-size="11" fill="#aab4d0">kg</text>
              </svg>
              <div v-else class="chart-empty">
                <span class="ce-ic">
                  <svg viewBox="0 0 24 24"><path d="M4 19V5M4 19h16M8 16v-4M12 16V8M16 16v-6" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>
                </span>
                <b>暂无体重记录</b>
                <small>在健康记录中添加体重后，这里会展示变化趋势</small>
              </div>
            </div>

            <div class="weight-stats">
              <div class="ws-item">
                <small>当前体重</small>
                <b>{{ active.weight || '—' }}<em v-if="active.weight">kg</em></b>
              </div>
              <div class="ws-item">
                <small>平均体重</small>
                <b>{{ active.weightAvg || '—' }}<em v-if="active.weightAvg">kg</em></b>
              </div>
              <div class="ws-item ws-up">
                <small>体重变化</small>
                <b class="up">{{ active.weightDelta || '—' }} <svg viewBox="0 0 24 24"><path d="M12 5l6 7h-4v7h-4v-7H6z" fill="currentColor"/></svg></b>
                <svg class="ws-arrow" viewBox="0 0 24 24"><path d="M9 5l7 7-7 7" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
              </div>
            </div>
          </section>
        </div>

        <!-- 右列：健康 / 待办 / 最近活动 -->
        <div class="dash-col col-right">
          <!-- 健康状态 -->
          <section ref="healthRef" class="panel arc-card health-card">
            <div class="card-head">
              <div class="ch-title">
                <span class="ch-ic pink"><svg viewBox="0 0 24 24"><path d="M12 20s-7-4.3-9-8.4C1.5 8.4 3.4 5 6.6 5c1.9 0 3.3 1 4.4 2.6h2C14.1 6 15.5 5 17.4 5c3.2 0 5.1 3.4 3.6 6.6-2 4.1-9 8.4-9 8.4z" fill="#fff"/></svg></span>
                <b>健康状态</b>
              </div>
            </div>
            <div class="health-body">
              <div class="score-ring">
                <svg viewBox="0 0 140 140">
                  <circle cx="70" cy="70" r="58" fill="none" stroke="#edf0f7" stroke-width="11" stroke-linecap="round" stroke-dasharray="273 365" transform="rotate(128 70 70)"/>
                  <circle cx="70" cy="70" r="58" fill="none" stroke="url(#ringGrad)" stroke-width="11" stroke-linecap="round" stroke-dasharray="268 365" transform="rotate(128 70 70)"/>
                  <defs>
                    <linearGradient id="ringGrad" x1="0" y1="0" x2="1" y2="1">
                      <stop offset="0%" stop-color="#34d399"/>
                      <stop offset="100%" stop-color="#10b981"/>
                    </linearGradient>
                  </defs>
                </svg>
                <div class="ring-center">
                  <b>健康良好</b>
                  <small>评分 <strong>{{ active.healthScore || 98 }} 分</strong></small>
                </div>
              </div>
              <ul class="health-tags">
                <li v-for="h in healthTags" :key="h">
                  <span class="ht-check"><svg viewBox="0 0 24 24"><circle cx="12" cy="12" r="9" fill="#d7f3e8"/><path d="M8 12.2l2.6 2.6L16.2 9" fill="none" stroke="#12b886" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg></span>
                  {{ h }}
                </li>
              </ul>
            </div>
          </section>

          <!-- 待办提醒 -->
          <section class="panel arc-card todo-card">
            <div class="card-head">
              <div class="ch-title">
                <span class="ch-ic purple"><svg viewBox="0 0 24 24"><path d="M18 9.5a6 6 0 1 0-12 0c0 5-2 6-2 6h16s-2-1-2-6zM9.5 19a2.6 2.6 0 0 0 5 0" fill="none" stroke="#fff" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"/></svg></span>
                <b>待办提醒</b>
              </div>
              <a class="ch-more" @click="showAllTodos = !showAllTodos">{{ showAllTodos ? '收起' : '查看更多' }} ></a>
            </div>
            <ul class="todo-list">
              <li v-for="t in (showAllTodos ? todos : todos.slice(0, 3))" :key="t.title">
                <span class="todo-ic" :style="{ background: t.bg }">
                  <svg viewBox="0 0 24 24" v-html="t.icon"></svg>
                </span>
                <div class="todo-txt">
                  <b>{{ t.title }}</b>
                  <small>{{ t.desc }}</small>
                </div>
                <svg class="todo-arrow" viewBox="0 0 24 24"><path d="M9 5l7 7-7 7" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
              </li>
            </ul>
          </section>

          <!-- 最近活动 -->
          <section class="panel arc-card activity-card">
            <div class="card-head">
              <div class="ch-title">
                <span class="ch-ic blue"><svg viewBox="0 0 24 24"><path d="M13 3l-1.6 5.2L6 9.8l5.4 1.6L13 17l1.6-5.6L20 9.8l-5.4-1.6L13 3zM5 17l-.8 2.4L2 20l2.2.6L5 23l.8-2.4L8 20l-2.2-.6L5 17z" fill="#fff"/></svg></span>
                <b>最近活动</b>
              </div>
              <a class="ch-more" @click="showAllActivities = !showAllActivities">{{ showAllActivities ? '收起' : '查看全部' }} ></a>
            </div>
            <ul class="act-list">
              <li v-for="(a, i) in (showAllActivities ? activities : activities.slice(0, 4))" :key="i">
                <span class="act-ic" :class="a.cls" v-html="a.icon"></span>
                <span class="act-date">{{ a.date }}</span>
                <span class="act-text">{{ a.text }}</span>
              </li>
            </ul>
          </section>
        </div>
      </div>

      <!-- 成长相册 -->
      <section ref="albumRef" class="panel arc-card album-section">
        <div class="card-head">
          <div class="ch-title">
            <span class="ch-ic pink"><svg viewBox="0 0 24 24"><rect x="3" y="5.5" width="18" height="13.5" rx="2.4" fill="none" stroke="#fff" stroke-width="1.8"/><path d="M3 15.5l4.6-4.6 3.4 3.4 3-3 7 6.4" fill="none" stroke="#fff" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"/><circle cx="8.6" cy="9.4" r="1.5" fill="#fff"/></svg></span>
            <b>成长相册</b>
          </div>
          <span class="ch-edit">
            <svg viewBox="0 0 24 24"><path d="M12 5v14M5 12h14" stroke="currentColor" stroke-width="2.2" stroke-linecap="round"/></svg>
            上传照片
          </span>
        </div>
        <div class="album-grid">
          <div class="album-item" v-for="(p, i) in albumPhotos" :key="i" @click="previewPhoto(p)">
            <img :src="p.url" :alt="p.title" class="album-img" />
            <div class="album-overlay">
              <b>{{ p.title }}</b>
              <small>{{ p.date }}</small>
            </div>
          </div>
        </div>
      </section>

      <!-- 空态 -->
      <div v-if="!active && !loading" class="arc-empty panel">
        <span class="empty-ic">
          <svg viewBox="0 0 24 24"><path d="M12 14.5c3.2 0 6 2.4 6 5 0 1-1.8 1.5-6 1.5s-6-.5-6-1.5c0-2.6 2.8-5 6-5zM7.2 11.6a2.1 2.1 0 1 1 0-4.2 2.1 2.1 0 0 1 0 4.2zm9.6 0a2.1 2.1 0 1 1 0-4.2 2.1 2.1 0 0 1 0 4.2zM4.6 8.4a1.7 1.7 0 1 1 0-3.4 1.7 1.7 0 0 1 0 3.4zm14.8 0a1.7 1.7 0 1 1 0-3.4 1.7 1.7 0 0 1 0 3.4z"/></svg>
        </span>
        <b>还没有宠物档案</b>
        <p>为你的毛孩子建立第一份数字档案吧</p>
        <button class="empty-btn" @click="openDialog()">+ 添加宠物</button>
      </div>
    </main>

    <!-- ================= 新增/编辑弹窗 ================= -->
    <el-dialog
      v-model="dialogVisible"
      :title="editId ? '编辑档案' : '添加宠物'"
      width="520px"
      :close-on-click-modal="false"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="宠物名称" prop="petName">
          <el-input v-model="form.petName" placeholder="例如：豆豆" maxlength="50" show-word-limit />
        </el-form-item>
        <el-form-item label="品种" prop="breed">
          <el-input v-model="form.breed" placeholder="例如：金毛寻回犬" maxlength="50" />
        </el-form-item>
        <el-form-item label="类型" prop="petType">
          <el-radio-group v-model="form.petType">
            <el-radio value="狗">狗</el-radio>
            <el-radio value="猫">猫</el-radio>
            <el-radio value="">未知</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="form.gender">
            <el-radio value="公">公</el-radio>
            <el-radio value="母">母</el-radio>
            <el-radio value="未知">未知</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="年龄" prop="age">
          <el-input v-model="form.age" placeholder="例如：2岁3个月" maxlength="20" />
        </el-form-item>
        <el-form-item label="照片">
          <el-upload
            class="photo-uploader"
            :show-file-list="false"
            :before-upload="beforePhotoUpload"
            :http-request="customPhotoUpload"
            accept=".jpg,.jpeg,.png"
          >
            <img v-if="form.photoUrl" :src="resolveImg(form.photoUrl)" class="photo-preview" alt="宠物照片" />
            <div v-else class="photo-empty">
              <svg viewBox="0 0 24 24"><path d="M12 16v-7m0 0L9 12m3-3l3 3" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"/><rect x="3.5" y="5.5" width="17" height="13.5" rx="2.4" fill="none" stroke="currentColor" stroke-width="1.8"/></svg>
              <span>上传照片</span>
            </div>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button v-if="editId" type="danger" plain @click="onDeleteCurrent">删除档案</el-button>
          <span class="footer-spacer"></span>
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="saving" @click="submit">确认</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 图片预览 -->
    <el-dialog v-model="previewVisible" :title="previewTitle" width="600px" align-center>
      <img :src="previewUrl" alt="" style="width: 100%; border-radius: 12px;" />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  listMyArchives,
  createArchive,
  updateArchive,
  deleteArchive
} from '@/api/archive'
import { uploadImage, myRecords } from '@/api/recognition'

const router = useRouter()
const API_BASE = import.meta.env.VITE_API_BASE || '/api'

/* ==================== AI 图片资源 ==================== */
const t2i = (p, s = 'square_hd') =>
  'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=' + encodeURIComponent(p) + '&image_size=' + s
const IMG = {
  dog: t2i('Happy golden retriever dog head portrait with tongue out smiling, soft cream studio background, professional pet photography, square avatar'),
  cat: t2i('Cute grey and white tabby Chinese domestic cat portrait with green eyes, soft neutral background, professional pet photography, square avatar'),
  bichon: t2i('Adorable white bichon frise dog portrait smiling, soft light blue pastel background, professional pet photography, square avatar'),
  dogHero: t2i('Happy golden retriever dog portrait in sunny park with green trees, warm soft light, professional pet photography', 'landscape_4_3'),
  catHero: t2i('Cute grey white tabby cat relaxing near window with plants, soft daylight, professional pet photography', 'landscape_4_3'),
  bichonHero: t2i('Adorable white bichon frise dog on cozy sofa, bright warm living room, professional pet photography', 'landscape_4_3'),
  g1: t2i('Golden retriever dog portrait with blue AI scanning frame overlay, soft background, professional pet photo'),
  g2: t2i('Golden retriever dog sitting on grass smiling, outdoor park, professional pet photography'),
  g3: t2i('Golden retriever dog close up face in garden, natural light, professional pet photography'),
  g4: t2i('Golden retriever puppy dog walking outdoors, sunny day, professional pet photography'),
  c2: t2i('Grey tabby cat sitting on windowsill looking away, warm light, professional pet photography'),
  b2: t2i('White bichon frise dog playing in park with flowers, professional pet photography')
}

/* ==================== 内联图标 ==================== */
const IC = {
  paw: '<svg viewBox="0 0 24 24"><path d="M12 14.5c3.2 0 6 2.4 6 5 0 1-1.8 1.5-6 1.5s-6-.5-6-1.5c0-2.6 2.8-5 6-5zM7.2 11.6a2.1 2.1 0 1 1 0-4.2 2.1 2.1 0 0 1 0 4.2zm9.6 0a2.1 2.1 0 1 1 0-4.2 2.1 2.1 0 0 1 0 4.2zM4.6 8.4a1.7 1.7 0 1 1 0-3.4 1.7 1.7 0 0 1 0 3.4zm14.8 0a1.7 1.7 0 1 1 0-3.4 1.7 1.7 0 0 1 0 3.4z" fill="#5b7cf0"/></svg>',
  tag: '<svg viewBox="0 0 24 24"><path d="M4 4.5h6.2L20 14.3l-5.7 5.7L4.5 10.2V4.5z" fill="none" stroke="#5b7cf0" stroke-width="1.8" stroke-linejoin="round"/><circle cx="8.2" cy="8.7" r="1.3" fill="#5b7cf0"/></svg>',
  sex: '<svg viewBox="0 0 24 24"><circle cx="10" cy="14" r="4.6" fill="none" stroke="#5b7cf0" stroke-width="1.8"/><path d="M13.4 10.6L19 5m0 0h-4m4 0v4" fill="none" stroke="#5b7cf0" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"/></svg>',
  cake: '<svg viewBox="0 0 24 24"><path d="M4 20v-8a8 8 0 0 1 16 0v8M4 16.5h16M12 7V4.5M10 4.5h4" fill="none" stroke="#5b7cf0" stroke-width="1.7" stroke-linecap="round"/></svg>',
  scale: '<svg viewBox="0 0 24 24"><path d="M12 7a5 5 0 1 0 0 10 5 5 0 0 0 0-10zm0 3.2a1.8 1.8 0 1 1 0 3.6 1.8 1.8 0 0 1 0-3.6z" fill="none" stroke="#5b7cf0" stroke-width="1.7"/><path d="M12 7V4.6m-2.4 0h4.8" stroke="#5b7cf0" stroke-width="1.7" stroke-linecap="round"/></svg>',
  smile: '<svg viewBox="0 0 24 24"><circle cx="12" cy="12" r="8.2" fill="none" stroke="#5b7cf0" stroke-width="1.8"/><circle cx="9" cy="10" r="1.2" fill="#5b7cf0"/><circle cx="15" cy="10" r="1.2" fill="#5b7cf0"/><path d="M8.6 13.8c1.8 2 5 2 6.8 0" fill="none" stroke="#5b7cf0" stroke-width="1.8" stroke-linecap="round"/></svg>',
  note: '<svg viewBox="0 0 24 24"><path d="M5 4.5A1.5 1.5 0 0 1 6.5 3h11A1.5 1.5 0 0 1 19 4.5v15a1.5 1.5 0 0 1-1.5 1.5h-11A1.5 1.5 0 0 1 5 19.5v-15z" fill="none" stroke="#5b7cf0" stroke-width="1.8"/><path d="M8.5 8h7M8.5 12h7M8.5 16h4.5" stroke="#5b7cf0" stroke-width="1.8" stroke-linecap="round"/></svg>',
  info: '<svg viewBox="0 0 24 24"><rect x="5" y="3.5" width="14" height="17" rx="2.4" fill="none" stroke="#6d7bf0" stroke-width="1.8"/><path d="M9 3v2.6M15 3v2.6M5 8.5h14M9 12.5h6M9 16h4" stroke="#6d7bf0" stroke-width="1.8" stroke-linecap="round"/></svg>',
  growth: '<svg viewBox="0 0 24 24"><path d="M4 19V5M4 19h16M8 16v-4M12 16V8M16 16v-6" fill="none" stroke="#6d7bf0" stroke-width="2" stroke-linecap="round"/></svg>',
  heart: '<svg viewBox="0 0 24 24"><path d="M12 20s-7-4.3-9-8.4C1.5 8.4 3.4 5 6.6 5c1.9 0 3.3 1 4.4 2.6h2C14.1 6 15.5 5 17.4 5c3.2 0 5.1 3.4 3.6 6.6-2 4.1-9 8.4-9 8.4z" fill="none" stroke="#6d7bf0" stroke-width="1.8"/></svg>',
  ai: '<svg viewBox="0 0 24 24"><rect x="4.5" y="8" width="15" height="11" rx="3" fill="none" stroke="#6d7bf0" stroke-width="1.8"/><circle cx="9" cy="13" r="1.4" fill="#6d7bf0"/><circle cx="15" cy="13" r="1.4" fill="#6d7bf0"/><rect x="2.7" y="6.4" width="3.2" height="3.2" rx="1" fill="none" stroke="#6d7bf0" stroke-width="1.6"/></svg>',
  album: '<svg viewBox="0 0 24 24"><rect x="3" y="5.5" width="18" height="13.5" rx="2.4" fill="none" stroke="#6d7bf0" stroke-width="1.8"/><path d="M3 15.5l4.6-4.6 3.4 3.4 3-3 7 6.4" fill="none" stroke="#6d7bf0" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"/><circle cx="8.6" cy="9.4" r="1.5" fill="#6d7bf0"/></svg>'
}

/* ==================== 演示宠物数据 ==================== */
function dogRecords() {
  return [
    { img: IMG.g1, breed: '金毛寻回犬', confidence: '98.7', time: '2025-06-08 14:32' },
    { img: IMG.g2, breed: '金毛寻回犬', confidence: '96.3', time: '2025-05-20 15:16' },
    { img: IMG.g3, breed: '金毛寻回犬', confidence: '95.2', time: '2025-04-12 10:24' },
    { img: IMG.g4, breed: '金毛寻回犬', confidence: '93.6', time: '2025-03-05 09:18' }
  ]
}
function catRecords() {
  return [
    { img: IMG.cat, breed: '中华田园猫', confidence: '96.3', time: '2025-06-07 16:20' },
    { img: IMG.c2, breed: '中华田园猫', confidence: '94.8', time: '2025-05-18 11:02' },
    { img: IMG.cat, breed: '中华田园猫', confidence: '93.1', time: '2025-04-09 18:40' },
    { img: IMG.c2, breed: '中华田园猫', confidence: '91.5', time: '2025-03-02 08:55' }
  ]
}
function bichonRecords() {
  return [
    { img: IMG.bichon, breed: '比熊犬', confidence: '96.9', time: '2025-06-06 10:12' },
    { img: IMG.b2, breed: '比熊犬', confidence: '95.4', time: '2025-05-16 17:30' },
    { img: IMG.bichon, breed: '比熊犬', confidence: '94.2', time: '2025-04-08 09:46' },
    { img: IMG.b2, breed: '比熊犬', confidence: '92.7', time: '2025-03-01 14:22' }
  ]
}
function growthOf(name) {
  return [
    { date: '2025-06-08', text: '更新体重 32.5kg' },
    { date: '2025-05-20', text: '完成驱虫' },
    { date: '2025-04-12', text: '体检 | 一切正常' },
    { date: '2025-03-05', text: `记录${name}体重 30.8kg` }
  ]
}

const DEMO_PETS = [
  {
    _id: 'demo-1', id: null, petName: '豆豆', breed: '金毛寻回犬', petType: '狗', gender: '公',
    age: '2岁3个月', weight: '32.5', weightAvg: '30.2', weightDelta: '+2.3', healthScore: 98,
    personality: '温顺友好', birthday: '2023-06-08', note: '喜欢玩球、散步、游泳',
    intro: '豆豆是一只活泼开朗的金毛寻回犬，喜欢和人互动，尤其喜欢玩球和散步。',
    thumb: IMG.dog, hero: IMG.dogHero, series: [29.8, 28.9, 30.1, 30.5, 32.2, 32.5],
    records: dogRecords(), growth: growthOf('豆豆'), demo: true
  },
  {
    _id: 'demo-2', id: null, petName: '咪咪', breed: '中华田园猫', petType: '猫', gender: '母',
    age: '1岁2个月', weight: '3.8', weightAvg: '3.5', weightDelta: '+0.3', healthScore: 96,
    personality: '安静黏人', birthday: '2024-04-12', note: '喜欢晒太阳、逗猫棒',
    intro: '咪咪是一只安静又黏人的中华田园猫，白天爱晒太阳，晚上喜欢窝在主人身边。',
    thumb: IMG.cat, hero: IMG.catHero, series: [3.2, 3.3, 3.4, 3.5, 3.7, 3.8],
    records: catRecords(), growth: growthOf('咪咪'), demo: true
  },
  {
    _id: 'demo-3', id: null, petName: '乐乐', breed: '比熊犬', petType: '狗', gender: '公',
    age: '1岁6个月', weight: '5.5', weightAvg: '5.1', weightDelta: '+0.4', healthScore: 97,
    personality: '活泼热情', birthday: '2023-12-20', note: '喜欢抱抱、玩飞盘',
    intro: '乐乐是一只活泼热情的比熊犬，一身雪白卷毛，见到谁都想热情地扑上去打招呼。',
    thumb: IMG.bichon, hero: IMG.bichonHero, series: [4.6, 4.8, 5.0, 5.1, 5.3, 5.5],
    records: bichonRecords(), growth: growthOf('乐乐'), demo: true
  }
]

/* ==================== 状态 ==================== */
const pets = ref([...DEMO_PETS])
const activeId = ref(DEMO_PETS[0]._id)
const loading = ref(false)
const activeTab = ref('base')
const range = ref('近半年')
const ranges = ['近半年', '近一年', '全部']

const tabs = [
  { key: 'base', label: '基本信息', icon: IC.info },
  { key: 'growth', label: '成长记录', icon: IC.growth },
  { key: 'health', label: '健康记录', icon: IC.heart },
  { key: 'recognize', label: 'AI识别记录', icon: IC.ai },
  { key: 'album', label: '相册', icon: IC.album }
]

const healthTags = ['精神状态良好', '食欲正常', '毛发光亮', '无异常症状']
const todos = [
  {
    title: '狂犬疫苗', desc: '还有 3 天（2025-06-12）', bg: 'linear-gradient(135deg,#ff9a9a,#ff6b6b)',
    icon: '<path d="M12 20s-7-4.3-9-8.4C1.5 8.4 3.4 5 6.6 5c1.9 0 3.3 1 4.4 2.6h2C14.1 6 15.5 5 17.4 5c3.2 0 5.1 3.4 3.6 6.6-2 4.1-9 8.4-9 8.4z" fill="#fff"/>'
  },
  {
    title: '驱虫', desc: '还有 15 天（2025-06-24）', bg: 'linear-gradient(135deg,#ffc46b,#ffa93d)',
    icon: '<circle cx="9" cy="9" r="4.5" fill="none" stroke="#fff" stroke-width="1.8"/><path d="M12.2 12.2L19 19M15.5 6l1.8 1.8M6 15.5l1.8 1.8" stroke="#fff" stroke-width="1.8" stroke-linecap="round"/>'
  },
  {
    title: '体检', desc: '还有 32 天（2025-07-11）', bg: 'linear-gradient(135deg,#7fb0ff,#4f7cf0)',
    icon: '<path d="M12 3l7.5 2.8v5.4c0 4.6-3.1 7.8-7.5 9.3-4.4-1.5-7.5-4.7-7.5-9.3V5.8L12 3z" fill="none" stroke="#fff" stroke-width="1.8" stroke-linejoin="round"/><path d="M8.8 11.8l2.2 2.2 4.2-4.6" fill="none" stroke="#fff" stroke-width="1.9" stroke-linecap="round" stroke-linejoin="round"/>'
  },
  {
    title: '洗澡美容', desc: '还有 5 天（2025-06-14）', bg: 'linear-gradient(135deg,#a78bfa,#7c5cf0)',
    icon: '<path d="M4 12c0-4 3.6-7 8-7s8 3 8 7-3.6 7-8 7-8-3-8-7z" fill="none" stroke="#fff" stroke-width="1.8"/><path d="M4 12h16" stroke="#fff" stroke-width="1.8"/>'
  },
  {
    title: '遛弯打卡', desc: '今日未完成', bg: 'linear-gradient(135deg,#5ce0b0,#12b886)',
    icon: '<path d="M13 5l-2 6h5l-3 7 1-5h-4l2-8z" fill="#fff"/>'
  }
]
const activities = [
  { date: '2025-06-08', text: 'AI识别：金毛寻回犬 98.7%', cls: 'c-paw', icon: IC.paw },
  { date: '2025-06-01', text: '体重记录：32.5kg', cls: 'c-blue', icon: IC.cake },
  { date: '2025-05-20', text: '驱虫记录：已完成', cls: 'c-indigo', icon: '<svg viewBox="0 0 24 24"><path d="M12 3l7.5 2.8v5.4c0 4.6-3.1 7.8-7.5 9.3-4.4-1.5-7.5-4.7-7.5-9.3V5.8L12 3z" fill="none" stroke="#fff" stroke-width="1.8" stroke-linejoin="round"/><path d="M8.8 11.8l2.2 2.2 4.2-4.6" fill="none" stroke="#fff" stroke-width="1.9" stroke-linecap="round" stroke-linejoin="round"/></svg>' },
  { date: '2025-05-10', text: '照片上传：宠物日常', cls: 'c-pink', icon: IC.album },
  { date: '2025-04-28', text: '疫苗接种：狂犬疫苗', cls: 'c-green', icon: '<svg viewBox="0 0 24 24"><path d="M12 20s-7-4.3-9-8.4C1.5 8.4 3.4 5 6.6 5c1.9 0 3.3 1 4.4 2.6h2C14.1 6 15.5 5 17.4 5c3.2 0 5.1 3.4 3.6 6.6-2 4.1-9 8.4-9 8.4z" fill="none" stroke="currentColor" stroke-width="1.8"/></svg>' },
  { date: '2025-04-15', text: '成长记录：身高58cm', cls: 'c-orange', icon: '<svg viewBox="0 0 24 24"><path d="M5 20h14M7 20V8m5 12V4m5 16v-6" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/></svg>' },
  { date: '2025-03-20', text: '档案建立：豆豆的专属档案', cls: 'c-purple', icon: IC.note }
]
const albumPhotos = [
  { url: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=' + encodeURIComponent('Cute golden retriever puppy sitting in a sunny garden with flowers, professional pet photography'), title: '刚到家的第一天', date: '2025-03-01' },
  { url: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=' + encodeURIComponent('Happy golden retriever playing with a ball in the park, sunny day, pet photography'), title: '第一次去公园', date: '2025-03-15' },
  { url: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=' + encodeURIComponent('Golden retriever dog taking a bath, bubbles, happy expression, pet grooming'), title: '第一次洗澡', date: '2025-04-02' },
  { url: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=' + encodeURIComponent('Golden retriever celebrating birthday with a pet cake, party hat, festive atmosphere'), title: '一岁生日', date: '2025-05-10' },
  { url: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=' + encodeURIComponent('Golden retriever dog sleeping peacefully on a cozy bed, warm lighting, cute'), title: '睡颜抓拍', date: '2025-05-20' },
  { url: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=' + encodeURIComponent('Golden retriever dog running on the beach, ocean waves, sunset, joyful'), title: '海边度假', date: '2025-06-01' }
]
const showAllTodos = ref(false)
const showAllActivities = ref(false)

const active = computed(() => pets.value.find(p => p._id === activeId.value) || null)
const activeRecords = computed(() => active.value?.records || [])
const activeGrowth = computed(() => active.value?.growth || [])

const infoRows = computed(() => {
  const p = active.value
  if (!p) return []
  const v = (val) => {
    const empty = val === '' || val == null || val === '待补充'
    return { text: empty ? '待补充' : val, muted: empty }
  }
  return [
    { label: '宠物昵称', icon: IC.paw, ...wrap(v(p.petName)) },
    { label: '宠物品种', icon: IC.tag, ...wrap(v(p.breed)) },
    { label: '性别', icon: IC.sex, value: p.gender && p.gender !== '未知' ? `${p.gender === '母' ? '♀' : '♂'} ${p.gender === '母' ? '雌性' : '雄性'}` : '待补充', muted: !p.gender || p.gender === '未知' },
    { label: '出生日期', icon: IC.cake, ...wrap(v(p.birthday)) },
    { label: '体重', icon: IC.scale, value: p.weight ? p.weight + ' kg' : '待补充', muted: !p.weight },
    { label: '性格特征', icon: IC.smile, ...wrap(v(p.personality)) },
    { label: '备注', icon: IC.note, ...wrap(v(p.note)) }
  ]
  function wrap(o) { return { value: o.text, muted: o.muted } }
})

/* ==================== 体重图表 ==================== */
const chart = computed(() => {
  const series = active.value?.series || [0, 0, 0, 0, 0, 0]
  const hasData = series.every(v => v > 0)
  const left = 34, right = 498, top = 24, bottom = 168
  const min = 20, max = 40
  const x = (i) => left + i * (right - left) / (series.length - 1)
  const y = (v) => bottom - (v - min) / (max - min) * (bottom - top)
  const pts = series.map((v, i) => ({ x: x(i), y: y(v) }))
  const line = pts.map((p, i) => `${i === 0 ? 'M' : 'L'}${p.x.toFixed(1)},${p.y.toFixed(1)}`).join(' ')
  const area = `${line} L${right},${bottom} L${left},${bottom} Z`
  const yTicks = [40, 35, 30, 25, 20].map(v => ({ v, y: y(v) }))
  const months = range.value === '近一年'
    ? ['7月', '9月', '11月', '1月', '3月', '6月']
    : ['1月', '2月', '3月', '4月', '5月', '6月']
  return { hasData, points: pts, line, area, yTicks, months, last: pts[pts.length - 1] }
})

/* ==================== 交互 ==================== */
function selectPet(p) {
  activeId.value = p._id
  activeTab.value = 'base'
}
const baseRef = ref(null)
const healthRef = ref(null)
const recognizeRef = ref(null)
const growthRef = ref(null)
const albumRef = ref(null)

function onTab(t) {
  activeTab.value = t.key
  if (t.key === 'base') window.scrollTo({ top: 0, behavior: 'smooth' })
  else if (t.key === 'growth') growthRef.value?.scrollIntoView({ behavior: 'smooth', block: 'center' })
  else if (t.key === 'health') healthRef.value?.scrollIntoView({ behavior: 'smooth', block: 'center' })
  else if (t.key === 'recognize') recognizeRef.value?.scrollIntoView({ behavior: 'smooth', block: 'center' })
  else if (t.key === 'album') albumRef.value?.scrollIntoView({ behavior: 'smooth', block: 'center' })
}
function scrollTo(key) {
  if (key === 'health') healthRef.value?.scrollIntoView({ behavior: 'smooth', block: 'center' })
}
function scrollToAlbum() {
  activeTab.value = 'album'
  albumRef.value?.scrollIntoView({ behavior: 'smooth', block: 'center' })
}
function soon(name) {
  ElMessage.info(`${name}功能即将上线，敬请期待`)
}
function viewFullArchive() {
  if (active.value?.id) router.push(`/archive/${active.value.id}`)
  else ElMessage.info('保存为正式档案后可查看完整档案')
}
const previewVisible = ref(false)
const previewUrl = ref('')
const previewTitle = ref('')
function previewPhoto(p) {
  previewUrl.value = p.url
  previewTitle.value = p.title
  previewVisible.value = true
}

function resolveImg(url) {
  if (!url) return ''
  if (url.startsWith('http')) return url
  return API_BASE + url
}

/* ==================== 弹窗：新增/编辑/删除 ==================== */
const dialogVisible = ref(false)
const saving = ref(false)
const editId = ref(null)
const formRef = ref(null)
const form = reactive({
  petName: '', breed: '', petType: '', gender: '未知', age: '', photoUrl: '', source: 'manual'
})
const rules = {
  petName: [{ required: true, message: '请输入宠物名称', trigger: 'blur' }],
  breed: [{ required: true, message: '请输入品种', trigger: 'blur' }]
}

function openDialog(p) {
  if (p && p.id) {
    editId.value = p.id
    form.petName = p.petName
    form.breed = p.breed
    form.petType = p.petType || ''
    form.gender = p.gender || '未知'
    form.age = p.age || ''
    form.photoUrl = p._rawPhoto || ''
    form.source = p._source || 'manual'
  } else {
    editId.value = null
    form.petName = p?.petName || ''
    form.breed = p?.breed || ''
    form.petType = p?.petType || ''
    form.gender = p?.gender && p.gender !== '未知' ? p.gender : '未知'
    form.age = p?.age || ''
    form.photoUrl = p?._rawPhoto || ''
    form.source = 'manual'
  }
  dialogVisible.value = true
}

function beforePhotoUpload(file) {
  if (!['image/jpeg', 'image/jpg', 'image/png'].includes(file.type)) {
    ElMessage.error('仅支持 JPG、PNG、JPEG 格式')
    return false
  }
  if (file.size > 5 * 1024 * 1024) {
    ElMessage.error('图片大小不能超过 5MB')
    return false
  }
  return true
}
async function customPhotoUpload({ file }) {
  try {
    const res = await uploadImage(file)
    form.photoUrl = res.data.imageUrl
    ElMessage.success('照片上传成功')
  } catch (e) {
    // 拦截器已提示
  }
}

async function submit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    saving.value = true
    try {
      if (editId.value) {
        await updateArchive(editId.value, { ...form })
        ElMessage.success('修改成功')
      } else {
        await createArchive({ ...form })
        ElMessage.success('创建成功')
      }
      dialogVisible.value = false
      await loadList(true)
    } finally {
      saving.value = false
    }
  })
}

async function onDeleteCurrent() {
  if (!editId.value) return
  try {
    await ElMessageBox.confirm(`确认删除档案「${form.petName}」吗？`, '提示', { type: 'warning' })
  } catch {
    return
  }
  await deleteArchive(editId.value)
  ElMessage.success('删除成功')
  dialogVisible.value = false
  await loadList(true)
}

/* ==================== 真实数据接入 ==================== */
function fallbackPhoto(petType) {
  return petType === '猫' ? { thumb: IMG.cat, hero: IMG.catHero } : { thumb: IMG.dog, hero: IMG.dogHero }
}
function mapReal(item) {
  const ph = resolveImg(item.photoUrl)
  const def = fallbackPhoto(item.petType)
  const birthday = item.createTime ? String(item.createTime).replace('T', ' ').substring(0, 10) : ''
  return {
    _id: 'real-' + item.id,
    id: item.id,
    petName: item.petName || '未命名',
    breed: item.breed || '未知品种',
    petType: item.petType || '',
    gender: item.gender || '未知',
    age: item.age || '',
    weight: '', weightAvg: '', weightDelta: '', healthScore: 98,
    personality: '', birthday, note: '',
    intro: `${item.petName || '它'}是一只${item.breed || '可爱的宠物'}，更多健康与成长数据记录后将在这里展示。`,
    thumb: ph || def.thumb,
    hero: ph || def.hero,
    series: [0, 0, 0, 0, 0, 0],
    records: item.petType === '猫' ? catRecords() : dogRecords(),
    growth: growthOf(item.petName || '它'),
    _rawPhoto: item.photoUrl || '',
    _source: item.source
  }
}

async function loadList(forceReal = false) {
  loading.value = true
  try {
    const res = await listMyArchives()
    const list = res.data || []
    if (Array.isArray(list) && list.length > 0) {
      const mapped = list.map(mapReal)
      // 真实识别记录（最近 4 条，展示在第一只宠物卡片上）
      try {
        const rr = await myRecords({ page: 1, size: 4 })
        const rl = rr.data?.list || []
        if (rl.length) {
          mapped[0].records = rl.map(r => ({
            img: resolveImg(r.imageUrl) || (r.petType === '猫' ? IMG.cat : IMG.dog),
            breed: r.breed || '未知品种',
            confidence: r.confidence ? String(Math.round(Number(r.confidence) * 10) / 10) : '—',
            time: r.createTime ? String(r.createTime).replace('T', ' ').substring(0, 16) : ''
          }))
        }
      } catch (e) { /* 保留演示 */ }
      pets.value = mapped
      activeId.value = mapped[0]._id
    } else if (forceReal) {
      pets.value = []
      activeId.value = ''
    }
  } catch (e) {
    // 未登录 / 接口异常：保留演示数据
  } finally {
    loading.value = false
  }
}

onMounted(() => loadList())
</script>

<style scoped>
/* ================= 成长相册 ================= */
.album-section { margin-top: 18px; }
.album-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
  gap: 14px;
}
.album-item {
  position: relative;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  aspect-ratio: 1 / 1;
  box-shadow: 0 2px 10px rgba(0,0,0,.08);
  transition: transform .25s;
}
.album-item:hover { transform: translateY(-3px); }
.album-img { width: 100%; height: 100%; object-fit: cover; }
.album-overlay {
  position: absolute; inset: 0;
  background: linear-gradient(to top, rgba(0,0,0,.65), transparent 55%);
  display: flex; flex-direction: column; justify-content: flex-end;
  padding: 12px; color: #fff; opacity: 0; transition: opacity .25s;
}
.album-item:hover .album-overlay { opacity: 1; }
.album-overlay b { font-size: 14px; margin-bottom: 2px; }
.album-overlay small { font-size: 11px; opacity: .85; }

/* ================= 骨架 ================= */
.arc-page {
  width: 100%;
  max-width: none;
  margin: 0;
  padding: 18px clamp(16px, 2vw, 32px) 40px;
  display: grid;
  grid-template-columns: 236px minmax(0, 1fr);
  gap: 18px;
  align-items: start;
}
.panel {
  background: #fff;
  border: 1px solid #edf0f8;
  border-radius: 16px;
  box-shadow: 0 10px 28px rgba(94, 110, 190, 0.06);
}

/* ================= 左侧栏 ================= */
.arc-side {
  position: sticky;
  top: 18px;
  display: flex;
  flex-direction: column;
}
.side-menu {
  background: #fff;
  border: 1px solid #edf0f8;
  border-radius: 16px;
  padding: 10px;
  box-shadow: 0 10px 28px rgba(94, 110, 190, 0.06);
}
.side-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 11px 14px;
  border-radius: 11px;
  font-size: 14.5px;
  font-weight: 600;
  color: #5c6a8c;
  cursor: pointer;
  margin-bottom: 4px;
  transition: background 0.15s, color 0.15s;
}
.side-item:last-child { margin-bottom: 0; }
.side-item:hover { background: #f5f7ff; color: #6d5ce0; }
.side-item.on {
  background: linear-gradient(135deg, #eef1ff, #e9edff);
  color: #5b4bd6;
  box-shadow: inset 0 0 0 1px #dfe5ff;
}
.si-ic {
  width: 22px;
  height: 22px;
  display: inline-flex;
  color: #7d8bb8;
}
.side-item.on .si-ic { color: #6d5ce0; }
.si-ic svg { width: 20px; height: 20px; }

.side-pets-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: 18px 4px 10px;
}
.side-pets-head b {
  font-size: 14.5px;
  color: #2b365c;
  font-weight: 700;
}
.add-pet {
  display: inline-flex;
  align-items: center;
  gap: 3px;
  border: 1px solid #d9e0ff;
  background: #fff;
  color: #6d5ce0;
  font-size: 12px;
  font-weight: 600;
  padding: 5px 10px;
  border-radius: 999px;
  cursor: pointer;
  transition: background 0.15s;
}
.add-pet:hover { background: #f2f4ff; }
.add-pet svg { width: 12px; height: 12px; }

.pet-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.pet-card {
  position: relative;
  display: flex;
  align-items: center;
  gap: 11px;
  background: #fff;
  border: 1.5px solid #edf0f8;
  border-radius: 14px;
  padding: 11px 10px;
  cursor: pointer;
  transition: border-color 0.15s, box-shadow 0.15s, transform 0.15s;
}
.pet-card:hover { border-color: #c4cdf5; transform: translateY(-1px); }
.pet-card.on {
  border-color: #8e9cf2;
  box-shadow: 0 8px 20px rgba(109, 105, 230, 0.14);
}
.pet-card img {
  width: 56px;
  height: 56px;
  border-radius: 11px;
  object-fit: cover;
  flex-shrink: 0;
}
.pc-info { flex: 1; min-width: 0; }
.pc-name {
  font-size: 14.5px;
  font-weight: 700;
  color: #26314f;
  display: flex;
  align-items: center;
  gap: 5px;
}
.sex { font-size: 13px; font-weight: 700; font-style: normal; }
.sex.m { color: #4f7cf0; }
.sex.f { color: #f06292; }
.pc-breed {
  font-size: 12px;
  color: #8a96b8;
  margin: 2px 0 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.pc-healthy {
  display: inline-flex;
  align-items: center;
  gap: 3px;
  font-size: 11px;
  color: #12b886;
  font-weight: 600;
}
.pc-healthy svg { width: 13px; height: 13px; }
.pc-arrow {
  width: 16px;
  height: 16px;
  color: #b8c1dd;
  flex-shrink: 0;
}

.side-deco {
  position: relative;
  margin-top: 26px;
  height: 150px;
  overflow: hidden;
}
.sd-paw {
  position: absolute;
  width: 60px;
  height: 60px;
  top: 6px;
  left: 30px;
  fill: rgba(130, 150, 235, 0.18);
}
.sd-note {
  position: absolute;
  left: 6px;
  bottom: 26px;
  font-size: 16px;
  line-height: 1.7;
  font-weight: 700;
  color: rgba(100, 120, 210, 0.55);
  transform: rotate(-7deg);
  font-family: 'Comic Sans MS', 'STKaiti', cursive;
}
.sd-note span { color: rgba(240, 120, 160, 0.6); }
.side-deco::after {
  content: '';
  position: absolute;
  left: -20px;
  right: -20px;
  bottom: -30px;
  height: 70px;
  background: radial-gradient(60% 100% at 30% 100%, rgba(150, 180, 250, 0.25), transparent 70%),
              radial-gradient(50% 100% at 80% 100%, rgba(120, 210, 200, 0.22), transparent 70%);
}

/* ================= 右侧主区 ================= */
.arc-main {
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 14px;
}

/* 宠物 Hero */
.pet-hero {
  position: relative;
  display: flex;
  background: linear-gradient(120deg, #f4f8ff 0%, #eef3ff 45%, #fdf6e8 100%);
  border: 1px solid #e9eefb;
  border-radius: 20px;
  overflow: hidden;
  min-height: 248px;
}
.hero-photo {
  width: 300px;
  flex-shrink: 0;
  position: relative;
}
.hero-photo img {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.hero-photo::after {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(90deg, transparent 70%, rgba(244, 248, 255, 0.9));
}
.hero-info {
  flex: 1;
  min-width: 0;
  padding: 28px 24px 22px 28px;
  z-index: 1;
}
.hi-name {
  display: flex;
  align-items: center;
  gap: 12px;
}
.hi-name h2 {
  margin: 0;
  font-size: 30px;
  font-weight: 800;
  color: #1f2742;
  letter-spacing: 1px;
}
.cert {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  background: #e8f0ff;
  color: #4f7cf0;
  font-size: 12px;
  font-weight: 600;
  padding: 3px 10px;
  border-radius: 999px;
}
.cert svg { width: 14px; height: 14px; }
.hi-breed {
  margin-top: 6px;
  font-size: 15px;
  font-weight: 600;
  color: #4f7cf0;
  display: flex;
  align-items: center;
  gap: 6px;
}
.sex-big { font-size: 17px; font-weight: 700; }
.sex-big.m { color: #4f7cf0; }
.sex-big.f { color: #f06292; }
.hi-tags {
  display: flex;
  gap: 10px;
  margin-top: 16px;
  flex-wrap: wrap;
}
.ht {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  background: rgba(255, 255, 255, 0.85);
  border: 1px solid #e3e9fb;
  border-radius: 999px;
  padding: 6px 13px;
  font-size: 12.5px;
  color: #5c6a8c;
  font-weight: 500;
}
.ht-ic {
  width: 18px;
  height: 18px;
  display: inline-flex;
  color: #6d7bf0;
}
.ht-ic svg { width: 17px; height: 17px; }
.hi-intro {
  position: relative;
  margin-top: 16px;
  background: rgba(255, 255, 255, 0.72);
  border-radius: 12px;
  padding: 12px 44px 12px 16px;
}
.hi-intro p {
  margin: 0;
  font-size: 13px;
  line-height: 1.7;
  color: #7a86a8;
}
.intro-edit {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: #eef1ff;
  color: #6d7bf0;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}
.intro-edit:hover { background: #e2e8ff; }
.intro-edit svg { width: 14px; height: 14px; }

.hero-right {
  position: relative;
  width: 250px;
  flex-shrink: 0;
}
.health-pill {
  position: absolute;
  top: 22px;
  left: 20px;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  background: linear-gradient(135deg, #34d399, #10b981);
  color: #fff;
  font-size: 13px;
  font-weight: 600;
  padding: 8px 16px;
  border-radius: 999px;
  box-shadow: 0 8px 18px rgba(16, 185, 129, 0.28);
  z-index: 2;
}
.health-pill svg { width: 15px; height: 15px; }
.hero-note {
  position: absolute;
  top: 70px;
  right: 10px;
  font-size: 17px;
  line-height: 1.6;
  font-weight: 700;
  color: #6b7cc8;
  transform: rotate(-6deg);
  font-family: 'Comic Sans MS', 'STKaiti', cursive;
  text-align: center;
  z-index: 2;
}
.hero-note span { color: #ef7ea5; }
.hero-cloud {
  position: absolute;
  right: -30px;
  bottom: -14px;
  width: 230px;
  z-index: 0;
}
.hero-paw {
  position: absolute;
  right: 40px;
  bottom: 24px;
  width: 46px;
  height: 46px;
  fill: rgba(180, 160, 110, 0.18);
  z-index: 1;
}

/* Tab 栏 */
.arc-tabs {
  display: flex;
  align-items: center;
  gap: 6px;
  background: #fff;
  border: 1px solid #edf0f8;
  border-radius: 14px;
  padding: 8px 14px;
  box-shadow: 0 10px 28px rgba(94, 110, 190, 0.06);
}
.arc-tab {
  display: inline-flex;
  align-items: center;
  gap: 7px;
  padding: 8px 16px;
  border-radius: 10px;
  font-size: 14.5px;
  font-weight: 600;
  color: #8a96b8;
  cursor: pointer;
  position: relative;
  transition: color 0.15s, background 0.15s;
}
.arc-tab:hover { color: #6d5ce0; }
.arc-tab.on { color: #5b4bd6; }
.arc-tab.on::after {
  content: '';
  position: absolute;
  left: 16px;
  right: 16px;
  bottom: -8px;
  height: 3px;
  border-radius: 2px;
  background: linear-gradient(90deg, #6d5ce0, #8b7cf6);
}
.at-ic {
  width: 26px;
  height: 26px;
  border-radius: 8px;
  background: #f0f2fa;
  color: #8a96b8;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}
.arc-tab.on .at-ic {
  background: linear-gradient(135deg, #7c8df6, #6d5ce0);
  color: #fff;
}
.at-ic svg { width: 16px; height: 16px; }

/* 仪表盘网格 */
.dash-grid {
  display: grid;
  grid-template-columns: minmax(0, 1.02fr) minmax(0, 1.14fr) minmax(0, 0.94fr);
  gap: 14px;
  align-items: start;
}
.dash-col {
  display: flex;
  flex-direction: column;
  gap: 14px;
  min-width: 0;
}

.arc-card { padding: 18px 20px; }
.card-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 14px;
}
.ch-title {
  display: flex;
  align-items: center;
  gap: 9px;
}
.ch-title b {
  font-size: 15.5px;
  font-weight: 700;
  color: #26314f;
}
.ch-ic {
  width: 28px;
  height: 28px;
  border-radius: 9px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}
.ch-ic svg { width: 17px; height: 17px; }
.ch-ic.purple { background: linear-gradient(135deg, #8b7cf6, #6d5ce0); }
.ch-ic.blue { background: linear-gradient(135deg, #6ea4ff, #4f7cf0); }
.ch-ic.indigo { background: linear-gradient(135deg, #7c8df6, #5b63f0); }
.ch-ic.pink { background: linear-gradient(135deg, #ff9ab8, #f06292); }
.ch-edit,
.ch-more {
  font-size: 12.5px;
  color: #93a0bd;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 4px;
}
.ch-edit svg { width: 13px; height: 13px; }
.ch-edit:hover, .ch-more:hover { color: #6d5ce0; }

/* 基本信息 */
.info-list {
  list-style: none;
  margin: 0;
  padding: 0;
}
.info-list li {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 9px 0;
  border-bottom: 1px dashed #f0f2f9;
}
.info-list li:last-child { border-bottom: none; }
.il-ic {
  width: 30px;
  height: 30px;
  border-radius: 9px;
  background: #f0f4ff;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.il-ic svg { width: 17px; height: 17px; }
.il-label {
  width: 68px;
  font-size: 13px;
  color: #93a0bd;
  flex-shrink: 0;
}
.il-value {
  font-size: 13.5px;
  color: #2f3a5e;
  font-weight: 600;
}
.il-value.muted { color: #b6bfd8; font-weight: 500; }

/* 体重图 */
.range-seg {
  display: inline-flex;
  background: #f2f4fa;
  border-radius: 8px;
  padding: 3px;
}
.range-seg button {
  border: none;
  background: transparent;
  font-size: 12px;
  color: #93a0bd;
  padding: 4px 12px;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
}
.range-seg button.on {
  background: linear-gradient(135deg, #6d80f2, #5b63f0);
  color: #fff;
  font-weight: 600;
}
.chart-box {
  width: 100%;
}
.weight-svg {
  width: 100%;
  height: 210px;
  display: block;
}
.chart-empty {
  height: 210px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 7px;
  background: linear-gradient(135deg, #f7f9ff, #f2f5ff);
  border: 1px dashed #d9e0fb;
  border-radius: 14px;
}
.chart-empty .ce-ic {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: linear-gradient(135deg, #eef1ff, #e2e8ff);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #7c8df6;
  margin-bottom: 2px;
}
.chart-empty .ce-ic svg {
  width: 23px;
  height: 23px;
}
.chart-empty b {
  font-size: 14px;
  color: #5a6585;
}
.chart-empty small {
  font-size: 12px;
  color: #a2acc7;
}
.weight-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10px;
  margin-top: 12px;
}
.ws-item {
  position: relative;
  background: linear-gradient(135deg, #f7f9ff, #f3f6ff);
  border: 1px solid #edf0fd;
  border-radius: 12px;
  padding: 12px 14px;
}
.ws-item small {
  display: block;
  font-size: 12px;
  color: #93a0bd;
  margin-bottom: 5px;
}
.ws-item b {
  font-size: 21px;
  color: #26314f;
  font-weight: 800;
}
.ws-item b em {
  font-style: normal;
  font-size: 12px;
  color: #93a0bd;
  font-weight: 500;
  margin-left: 2px;
}
.ws-item b.up {
  color: #12b886;
  font-size: 17px;
  display: inline-flex;
  align-items: center;
  gap: 2px;
}
.ws-item b.up svg { width: 15px; height: 15px; }
.ws-arrow {
  position: absolute;
  right: 10px;
  top: 50%;
  transform: translateY(-50%);
  width: 16px;
  height: 16px;
  color: #b8c1dd;
}

/* 健康状态 */
.health-body {
  display: flex;
  align-items: center;
  gap: 14px;
}
.score-ring {
  position: relative;
  width: 128px;
  height: 128px;
  flex-shrink: 0;
}
.score-ring svg { width: 100%; height: 100%; }
.ring-center {
  position: absolute;
  inset: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 2px;
}
.ring-center b {
  font-size: 17px;
  color: #10b981;
  font-weight: 800;
}
.ring-center small {
  font-size: 11.5px;
  color: #93a0bd;
}
.ring-center strong { color: #5c6a8c; font-weight: 700; }
.health-tags {
  list-style: none;
  margin: 0;
  padding: 0;
  flex: 1;
  min-width: 0;
}
.health-tags li {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #46527a;
  font-weight: 500;
  padding: 5px 0;
}
.ht-check {
  width: 19px;
  height: 19px;
  flex-shrink: 0;
}
.ht-check svg { width: 19px; height: 19px; }

/* 待办 */
.todo-list,
.act-list {
  list-style: none;
  margin: 0;
  padding: 0;
}
.todo-list li {
  display: flex;
  align-items: center;
  gap: 11px;
  padding: 10px 2px;
  border-bottom: 1px dashed #f0f2f9;
}
.todo-list li:last-child { border-bottom: none; }
.todo-ic {
  width: 38px;
  height: 38px;
  border-radius: 11px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.todo-ic svg { width: 20px; height: 20px; }
.todo-txt { flex: 1; min-width: 0; }
.todo-txt b {
  display: block;
  font-size: 13.5px;
  color: #2f3a5e;
  font-weight: 700;
}
.todo-txt small {
  display: block;
  font-size: 11.5px;
  color: #9aa5c2;
  margin-top: 2px;
}
.todo-arrow {
  width: 15px;
  height: 15px;
  color: #c0c9e2;
  flex-shrink: 0;
}

/* 最近活动 */
.act-list li {
  display: flex;
  align-items: center;
  gap: 9px;
  padding: 8px 0;
}
.act-ic {
  width: 30px;
  height: 30px;
  border-radius: 9px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.act-ic svg { width: 16px; height: 16px; }
.act-ic.c-paw { background: linear-gradient(135deg, #8b7cf6, #6d5ce0); }
.act-ic.c-blue { background: linear-gradient(135deg, #6ea4ff, #4f7cf0); }
.act-ic.c-indigo { background: linear-gradient(135deg, #7c8df6, #5b63f0); }
.act-ic.c-pink { background: linear-gradient(135deg, #ff9ab8, #f06292); }
.act-date {
  font-size: 12px;
  color: #9aa5c2;
  width: 78px;
  flex-shrink: 0;
}
.act-text {
  font-size: 13px;
  color: #46527a;
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* AI 识别记录 */
.rec-cards {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 10px;
}
.rec-card {
  border: 1px solid #eef0f8;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  background: #fff;
  transition: transform 0.15s, box-shadow 0.15s;
}
.rec-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(100, 115, 200, 0.16);
}
.rc-img {
  position: relative;
  aspect-ratio: 1.25;
}
.rc-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.rc-paw {
  position: absolute;
  right: 6px;
  bottom: 6px;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: rgba(109, 99, 240, 0.85);
  display: flex;
  align-items: center;
  justify-content: center;
}
.rc-paw svg { width: 12px; height: 12px; fill: #fff; }
.rc-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 7px 8px 2px;
}
.rc-meta b {
  font-size: 11.5px;
  color: #2f3a5e;
  display: flex;
  align-items: center;
  gap: 3px;
  white-space: nowrap;
  overflow: hidden;
}
.rc-meta b i { color: #6d7bf0; font-style: normal; font-size: 10px; }
.rc-conf {
  font-size: 11px;
  color: #6d5ce0;
  font-weight: 700;
  flex-shrink: 0;
  margin-left: 4px;
}
.rec-card small {
  display: block;
  padding: 0 8px 8px;
  font-size: 10.5px;
  color: #a2acc8;
}
.rec-promo {
  background: linear-gradient(150deg, #eef1ff, #f2ecff);
  border: 1px solid #e4e1fb;
  border-radius: 12px;
  padding: 12px 10px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}
.rec-promo p {
  margin: 0 0 10px;
  font-size: 11px;
  line-height: 1.6;
  color: #6b76a8;
}
.promo-btn {
  border: none;
  background: linear-gradient(135deg, #6d5ce0, #7b6ef6);
  color: #fff;
  font-size: 12px;
  font-weight: 600;
  border-radius: 999px;
  padding: 8px 6px;
  cursor: pointer;
  box-shadow: 0 6px 14px rgba(109, 92, 224, 0.26);
}
.promo-btn:hover { opacity: 0.92; }

/* 成长小记 */
.growth-wrap {
  display: grid;
  grid-template-columns: minmax(0, 1.1fr) minmax(0, 1fr);
  gap: 18px;
}
.growth-list {
  list-style: none;
  margin: 0;
  padding: 0 0 0 4px;
}
.growth-list li {
  position: relative;
  display: flex;
  gap: 12px;
  padding: 0 0 16px 14px;
  border-left: 2px solid #e7ebfa;
}
.growth-list li:last-child { padding-bottom: 0; }
.gl-dot {
  position: absolute;
  left: -6px;
  top: 3px;
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: #6d80f2;
  box-shadow: 0 0 0 3px #e4e9ff;
}
.gl-date {
  font-size: 12px;
  color: #93a0bd;
  width: 82px;
  flex-shrink: 0;
}
.gl-text {
  font-size: 13px;
  color: #46527a;
  font-weight: 500;
}
.growth-note {
  position: relative;
  align-self: start;
  background: linear-gradient(150deg, #f4f6ff, #eef1ff);
  border-radius: 14px;
  padding: 18px 18px 22px;
}
.growth-note p {
  margin: 0;
  font-size: 12.5px;
  line-height: 1.8;
  color: #7a86b0;
}
.gn-pen {
  position: absolute;
  left: 14px;
  bottom: 14px;
  width: 26px;
  height: 26px;
  border-radius: 50%;
  background: #fff;
  color: #8b95d8;
  display: flex;
  align-items: center;
  justify-content: center;
}
.gn-pen svg { width: 13px; height: 13px; }
.gn-heart {
  position: absolute;
  right: 14px;
  bottom: 12px;
  color: #c3ccf2;
  font-size: 16px;
}

/* 空态 */
.arc-empty {
  padding: 70px 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}
.empty-ic {
  width: 84px;
  height: 84px;
  border-radius: 50%;
  background: #f2f5ff;
  display: flex;
  align-items: center;
  justify-content: center;
}
.empty-ic svg {
  width: 42px;
  height: 42px;
  fill: #a9b6e8;
}
.arc-empty b { font-size: 17px; color: #3a4670; }
.arc-empty p { margin: 0 0 8px; font-size: 13px; color: #98a3c0; }
.empty-btn {
  border: none;
  background: linear-gradient(135deg, #6d5ce0, #7b6ef6);
  color: #fff;
  font-size: 14px;
  font-weight: 600;
  border-radius: 999px;
  padding: 11px 28px;
  cursor: pointer;
  box-shadow: 0 8px 18px rgba(109, 92, 224, 0.28);
}

/* 弹窗 */
.photo-uploader :deep(.el-upload) {
  border: 1.5px dashed #d4d9e8;
  border-radius: 12px;
  cursor: pointer;
  width: 150px;
  height: 150px;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  transition: border-color 0.2s;
}
.photo-uploader :deep(.el-upload:hover) { border-color: #6d5ce0; }
.photo-preview { width: 100%; height: 100%; object-fit: cover; }
.photo-empty {
  color: #909399;
  text-align: center;
  font-size: 12.5px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 7px;
}
.photo-empty svg { width: 30px; height: 30px; }
.dialog-footer {
  display: flex;
  align-items: center;
}
.footer-spacer { flex: 1; }

/* ================= 响应式 ================= */
@media (max-width: 1380px) {
  .dash-grid {
    grid-template-columns: minmax(0, 1fr) minmax(0, 1fr);
  }
  .col-right {
    grid-column: 1 / -1;
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 14px;
  }
}
@media (max-width: 1080px) {
  .arc-page {
    grid-template-columns: 1fr;
  }
  .arc-side {
    position: static;
  }
  .pet-list {
    flex-direction: row;
    overflow-x: auto;
    scrollbar-width: none;
    padding-bottom: 4px;
  }
  .pet-card { min-width: 210px; }
  .side-deco { display: none; }
  .dash-grid {
    grid-template-columns: 1fr;
  }
  .col-right {
    grid-column: auto;
    display: flex;
  }
  .hero-right { display: none; }
  .rec-cards {
    grid-template-columns: repeat(3, 1fr);
  }
}
@media (max-width: 680px) {
  .pet-hero { flex-direction: column; }
  .hero-photo { width: 100%; height: 220px; }
  .hero-photo::after { display: none; }
  .health-body { flex-direction: column; align-items: flex-start; }
  .growth-wrap { grid-template-columns: 1fr; }
  .rec-cards { grid-template-columns: repeat(2, 1fr); }
  .weight-stats { grid-template-columns: 1fr; }
  .arc-tabs { overflow-x: auto; scrollbar-width: none; }
}
</style>
