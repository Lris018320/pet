<template>
  <div class="community-page">
    <!-- ================= Hero Banner ================= -->
    <section class="hero" :style="{ backgroundImage: `url(${IMG.hero})` }">
      <div class="hero-left">
        <div class="hero-title">
          <span class="ht-line ht-l1">分享养宠日常</span>
          <span class="ht-line ht-l2">遇见同频伙伴</span>
        </div>
        <p class="hero-sub">在这里，和千万宠友一起 交流养宠经验，分享萌宠瞬间</p>
        <el-button class="hero-btn" round @click="goPublish">发布帖子
          <svg viewBox="0 0 24 24" class="btn-arrow"><path d="M5 12h13m0 0-5.5-5.5M18 12l-5.5 5.5" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"/></svg>
        </el-button>
      </div>
      <span class="hero-heart" v-html="IC.heart"></span>
      <span class="hero-paw p1" v-html="IC.paw"></span>
      <span class="hero-paw p2" v-html="IC.paw"></span>
    </section>

    <!-- ================= 分类图标行 ================= -->
    <section class="cat-row">
      <div
        v-for="c in categories"
        :key="c.name"
        class="cat-item"
        :class="{ on: activeCat === c.name }"
        @click="onCategory(c)"
      >
        <div class="cat-icon" :style="{ background: c.bg }">
          <svg viewBox="0 0 24 24" v-html="c.icon"></svg>
        </div>
        <span class="cat-name">{{ c.name }}</span>
      </div>
    </section>

    <!-- ================= 主区：左主右栏 ================= -->
    <div class="main-grid">
      <!-- 左侧主区 -->
      <div class="main-left">
        <div class="feed-head">
          <div class="feed-tabs">
            <span
              v-for="t in tabs"
              :key="t.key"
              class="feed-tab"
              :class="{ on: activeTab === t.key }"
              @click="onTab(t.key)"
            >{{ t.label }}</span>
          </div>
          <el-select v-model="sortCat" placeholder="全部" size="small" class="feed-select" @change="loadList(1)">
            <el-option label="全部" value="" />
            <el-option label="日常" value="日常" />
            <el-option label="晒宠" value="晒宠" />
            <el-option label="求助" value="求助" />
            <el-option label="问答" value="问答" />
          </el-select>
        </div>

        <div class="post-grid" v-loading="loading">
          <div
            v-for="(p, i) in list"
            :key="p.id ?? p._key"
            class="post-card"
            :class="{ featured: i === 0 && page === 1 }"
            @click="goDetail(p.id)"
          >
            <div class="pc-cover">
              <img :src="p.coverImage" :alt="p.title" />
            </div>
            <div class="pc-body">
              <div class="pc-author">
                <img :src="p.avatar" class="pc-avatar" :alt="p.nickname" />
                <div class="pc-author-info">
                  <b>{{ p.nickname }}</b>
                  <small>{{ p.timeAgo }}</small>
                </div>
                <span class="pc-more" @click.stop>···</span>
              </div>
              <h3 class="pc-title">{{ p.title }}</h3>
              <p class="pc-desc">{{ p.content }}</p>
              <div class="pc-tags">
                <span v-for="tag in p.tags" :key="tag" class="pc-tag"># {{ tag }}</span>
              </div>
              <div class="pc-foot">
                <span class="pf-item">
                  <svg viewBox="0 0 24 24"><path d="M2.5 12S6 5.8 12 5.8 21.5 12 21.5 12 18 18.2 12 18.2 2.5 12 2.5 12Z" fill="none" stroke="currentColor" stroke-width="1.7"/><circle cx="12" cy="12" r="3" fill="none" stroke="currentColor" stroke-width="1.7"/></svg>
                  {{ p.viewText }}
                </span>
                <span class="pf-item">
                  <svg viewBox="0 0 24 24"><path d="M4 5.5h16v11a2 2 0 0 1-2 2H9l-5 3.5v-16.5z" fill="none" stroke="currentColor" stroke-width="1.7" stroke-linejoin="round"/></svg>
                  {{ p.commentCount || 0 }}
                </span>
                <span class="pf-item pf-like" :class="{ liked: p.liked }" @click.stop="onLike(p)">
                  <svg viewBox="0 0 24 24"><path d="M12 20.5s-7-4.6-9-8.6C1.6 9 3 5.8 6.2 5.8c1.9 0 3.2 1.1 3.8 2 .6-.9 1.9-2 3.8-2 3.2 0 4.6 3.2 3.2 6.1-2 4-9 8.6-9 8.6Z" :fill="p.liked ? 'currentColor' : 'none'" stroke="currentColor" stroke-width="1.7" stroke-linejoin="round"/></svg>
                  {{ p.likeCount || 0 }}
                </span>
              </div>
            </div>
          </div>
          <div v-if="!loading && list.length === 0" class="post-empty">
            <div class="pe-icon">
              <svg viewBox="0 0 24 24"><circle cx="11" cy="11" r="6.5" fill="none" stroke="#b8c0e8" stroke-width="1.8"/><path d="M16 16l4.5 4.5" stroke="#b8c0e8" stroke-width="1.8" stroke-linecap="round"/></svg>
            </div>
            <p>暂无帖子，快来发布第一条分享吧</p>
          </div>
        </div>

        <el-pagination
          v-if="total > size"
          class="pagination"
          background
          layout="prev, pager, next"
          :total="total"
          :page-size="size"
          :current-page="page"
          @current-change="onPageChange"
        />
      </div>

      <!-- 右侧边栏 -->
      <aside class="main-side">
        <!-- 热门话题 -->
        <section class="side-card">
          <div class="sc-head">
            <span class="sc-title">
              <span class="sc-ic hot" v-html="IC.fire"></span>
              热门话题
            </span>
            <a class="sc-more" @click="onMoreTopics">查看更多 &gt;</a>
          </div>
          <ul class="topic-list">
            <li v-for="(t, i) in topics" :key="t.name" @click="searchTopic(t)">
              <span class="tp-rank" :class="{ top: i < 3 }">{{ i + 1 }}</span>
              <span class="tp-name">{{ t.name }}</span>
              <span class="tp-hot">
                <span v-html="IC.fire"></span>{{ t.hot }}
              </span>
            </li>
          </ul>
        </section>

        <!-- 最近活跃用户 -->
        <section class="side-card">
          <div class="sc-head">
            <span class="sc-title">
              <span class="sc-ic user" v-html="IC.users"></span>
              最近活跃用户
            </span>
            <a class="sc-more" @click="onMoreUsers">查看更多 &gt;</a>
          </div>
          <ul class="user-list">
            <li v-for="u in activeUsers" :key="u.name">
              <img :src="u.avatar" class="ul-avatar" :alt="u.name" />
              <div class="ul-info">
                <b>{{ u.name }}</b>
                <small>{{ u.level }}</small>
              </div>
              <button
                class="ul-follow"
                :class="{ followed: u.followed }"
                @click="onFollow(u)"
              >{{ u.followed ? '已关注' : '+ 关注' }}</button>
            </li>
          </ul>
        </section>

        <!-- 加入社区 -->
        <section class="join-card">
          <div class="join-left">
            <b class="join-title">加入宠知社区</b>
            <p class="join-sub">和更多铲屎官一起交流</p>
            <el-button class="join-btn" round @click="goPublish">立即加入
              <svg viewBox="0 0 24 24" class="btn-arrow"><path d="M5 12h13m0 0-5.5-5.5M18 12l-5.5 5.5" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"/></svg>
            </el-button>
          </div>
          <img :src="IMG.joinPets" class="join-img" alt="" />
          <span class="join-paw" v-html="IC.paw"></span>
        </section>
      </aside>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { listPost, likePost, getTopics, getActiveUsers, toggleFollow } from '@/api/community'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const isLogin = ref(!!userStore.token)

/* ========== AI 图片资源 ========== */
const IMG = {
  hero: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=' +
    encodeURIComponent('Golden retriever dog and orange tabby cat sitting together in a sunny flower meadow with green grass, blue sky with soft clouds, cheerful spring atmosphere, professional pet photography') +
    '&image_size=landscape_4_3',
  joinPets: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=' +
    encodeURIComponent('Cute cartoon illustration of a corgi dog and two cats sitting together waving paws, flat style, soft pastel colors, transparent-looking white background') +
    '&image_size=square',
  kitten: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=' +
    encodeURIComponent('Adorable small kitten eating cat food from a bowl indoors, soft warm lighting, cozy home, professional pet photography') +
    '&image_size=landscape_4_3',
  beachDog: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=' +
    encodeURIComponent('Golden retriever dog happily running on sandy beach with ocean waves and blue sky, joyful summer day, professional pet photography') +
    '&image_size=landscape_4_3',
  sofaCat: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=' +
    encodeURIComponent('Tabby cat scratching on a beige sofa in a bright living room, curious expression, professional pet photography') +
    '&image_size=landscape_4_3',
  vaccineDog: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=' +
    encodeURIComponent('Veterinarian in white coat giving vaccine injection to a small cute dog at modern pet clinic, gentle care scene, professional photography') +
    '&image_size=landscape_4_3',
  avatarCat: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=' +
    encodeURIComponent('Cute orange tabby cat face close-up portrait, big round eyes, soft cream background, professional pet photography, square avatar') +
    '&image_size=square_hd',
  avatarDog: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=' +
    encodeURIComponent('Happy golden retriever dog head portrait with tongue out smiling, soft cream studio background, professional pet photography, square avatar') +
    '&image_size=square_hd',
  avatarCorgi: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=' +
    encodeURIComponent('Smiling corgi dog face portrait with perky ears, soft light blue background, professional pet photography, square avatar') +
    '&image_size=square_hd',
  avatarGrey: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=' +
    encodeURIComponent('Cute grey and white british shorthair cat face portrait, amber eyes, soft neutral background, professional pet photography, square avatar') +
    '&image_size=square_hd'
}
const defaultAvatars = [IMG.avatarCat, IMG.avatarDog, IMG.avatarCorgi, IMG.avatarGrey]

/* ========== 内联 SVG 图标 ========== */
const IC = {
  paw: '<svg viewBox="0 0 24 24"><path fill="currentColor" d="M12 14.5c3.2 0 6 2.4 6 5 0 1-1.8 1.5-6 1.5s-6-.5-6-1.5c0-2.6 2.8-5 6-5zM7.2 11.6a2.1 2.1 0 1 1 0-4.2 2.1 2.1 0 0 1 0 4.2zm9.6 0a2.1 2.1 0 1 1 0-4.2 2.1 2.1 0 0 1 0 4.2zM4.6 8.4a1.7 1.7 0 1 1 0-3.4 1.7 1.7 0 0 1 0 3.4zm14.8 0a1.7 1.7 0 1 1 0-3.4 1.7 1.7 0 0 1 0 3.4z"/></svg>',
  heart: '<svg viewBox="0 0 24 24"><path fill="#ff8fb2" d="M12 20.5s-7-4.6-9-8.6C1.6 9 3 5.8 6.2 5.8c1.9 0 3.2 1.1 3.8 2 .6-.9 1.9-2 3.8-2 3.2 0 4.6 3.2 3.2 6.1-2 4-9 8.6-9 8.6Z"/></svg>',
  fire: '<svg viewBox="0 0 24 24" style="width:13px;height:13px;vertical-align:-1px"><path fill="#ff7a45" d="M12 22c4.4 0 7.5-3 7.5-7.2 0-3.1-1.9-5.3-3.5-7-.3 1.6-1.2 2.8-2.5 3.2.5-2.9-.9-6.6-4-8.5.3 2.8-1 4.3-2.4 6C5.3 10.4 4.5 12 4.5 14.8 4.5 19 7.6 22 12 22z"/></svg>',
  users: '<svg viewBox="0 0 24 24"><circle cx="9" cy="8.5" r="3.2" fill="#fff"/><path d="M3.2 19c.6-3.2 3-5 5.8-5s5.2 1.8 5.8 5" fill="none" stroke="#fff" stroke-width="1.8" stroke-linecap="round"/><circle cx="17" cy="9.5" r="2.5" fill="#fff" opacity=".85"/><path d="M15.5 14.6c2.6.2 4.5 1.7 5 4.4" fill="none" stroke="#fff" stroke-width="1.6" stroke-linecap="round" opacity=".85"/></svg>',
  topic: '<svg viewBox="0 0 24 24"><path d="M10 4L8.5 20M16 4l-1.5 16M4.5 8.5h15M3.5 15.5h15" fill="none" stroke="#fff" stroke-width="1.9" stroke-linecap="round"/></svg>',
  exp: '<svg viewBox="0 0 24 24"><path d="M11.5 5.2c-2-1.2-4.5-1.5-7-.7v13c2.5-.8 5-.5 7 .7m0-13c2-1.2 4.5-1.5 7-.7v13c-2.5-.8-5-.5-7 .7m0-13v13" fill="none" stroke="#fff" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"/></svg>',
  qa: '<svg viewBox="0 0 24 24"><circle cx="12" cy="12" r="9" fill="none" stroke="#fff" stroke-width="1.8"/><path d="M9.6 9.4a2.5 2.5 0 1 1 3.4 2.4c-.8.3-1 .9-1 1.7v.3" fill="none" stroke="#fff" stroke-width="1.8" stroke-linecap="round"/><circle cx="12" cy="17" r="1.2" fill="#fff"/></svg>',
  share: '<svg viewBox="0 0 24 24"><rect x="4" y="6.5" width="16" height="13" rx="2.4" fill="none" stroke="#fff" stroke-width="1.8"/><path d="M4 11l4.2-4.2 3 3 2.6-2.6L20 13" fill="none" stroke="#fff" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"/><circle cx="8.6" cy="9.6" r="1.3" fill="#fff"/></svg>',
  daily: '<svg viewBox="0 0 24 24"><rect x="4" y="5" width="16" height="15" rx="2.4" fill="none" stroke="#fff" stroke-width="1.8"/><path d="M4 9.5h16M8 3.5V7M16 3.5V7" stroke="#fff" stroke-width="1.8" stroke-linecap="round"/><path d="M8 13.5h4M8 16.5h6" stroke="#fff" stroke-width="1.7" stroke-linecap="round"/></svg>',
  gift: '<svg viewBox="0 0 24 24"><rect x="4.5" y="9" width="15" height="11" rx="2" fill="none" stroke="#fff" stroke-width="1.8"/><path d="M12 9v11M4.5 13h15M12 9s-4 .2-5-1.6C6.2 5.9 7.8 4 9.4 4.6 11 5.2 12 9 12 9zm0 0s4 .2 5-1.6C17.8 5.9 16.2 4 14.6 4.6 13 5.2 12 9 12 9z" fill="none" stroke="#fff" stroke-width="1.7" stroke-linejoin="round"/></svg>',
  grid: '<svg viewBox="0 0 24 24"><rect x="4" y="4" width="7" height="7" rx="1.8" fill="#fff"/><rect x="13" y="4" width="7" height="7" rx="1.8" fill="#fff" opacity=".85"/><rect x="4" y="13" width="7" height="7" rx="1.8" fill="#fff" opacity=".85"/><rect x="13" y="13" width="7" height="7" rx="1.8" fill="#fff"/></svg>'
}

/* ========== 分类图标行 ========== */
const categories = [
  { name: '热门话题', icon: IC.topic, bg: 'linear-gradient(135deg,#ff9a7a,#ff6b6b)', action: 'topic' },
  { name: '养宠经验', icon: IC.exp, bg: 'linear-gradient(135deg,#7c8df6,#6d5ce0)', action: 'cat', value: '' },
  { name: '宠物问答', icon: IC.qa, bg: 'linear-gradient(135deg,#b08bff,#8b5cf6)', action: 'cat', value: '问答' },
  { name: '萌宠分享', icon: IC.share, bg: 'linear-gradient(135deg,#ffb36b,#ff8a3d)', action: 'cat', value: '晒宠' },
  { name: '日常记录', icon: IC.daily, bg: 'linear-gradient(135deg,#5ce0b0,#12b886)', action: 'cat', value: '日常' },
  { name: '活动专区', icon: IC.gift, bg: 'linear-gradient(135deg,#ff8fb2,#f0528f)', action: 'soon' },
  { name: '全部版块', icon: IC.grid, bg: 'linear-gradient(135deg,#7fb0ff,#4f7cf0)', action: 'all' }
]
const activeCat = ref('')

const tabs = [
  { key: 'latest', label: '最新发布' },
  { key: 'hot', label: '热门推荐' },
  { key: 'follow', label: '关注动态' }
]
const activeTab = ref('latest')
const sortCat = ref('')

/* ========== 演示兜底数据 ========== */
const demoList = [
  {
    _key: 'd1', nickname: '小橙子喵', avatar: IMG.avatarCat, timeAgo: '2小时前',
    title: '新手养猫必看！幼猫喂养的5个注意事项',
    content: '刚把小猫接回家，真的有很多不懂的地方，整理了一些经验分享给大家，希望对新手铲屎官有帮助～',
    coverImage: IMG.kitten, tags: ['幼猫喂养', '新手必看', '养猫经验'],
    viewText: '1.2k', commentCount: 56, likeCount: 328, liked: false
  },
  {
    _key: 'd2', nickname: '金毛多多', avatar: IMG.avatarDog, timeAgo: '5小时前',
    title: '带狗狗去海边玩是一种什么体验？',
    content: '上周带我家金毛去海边玩了，第一次见到大海兴奋得不行，分享一下旅行攻略和注意事项～',
    coverImage: IMG.beachDog, tags: ['狗狗旅行', '金毛', '旅行攻略'],
    viewText: '89', commentCount: 89, likeCount: 612, liked: false
  },
  {
    _key: 'd3', nickname: '喵星人小分队', avatar: IMG.avatarGrey, timeAgo: '8小时前',
    title: '猫咪为什么总是喜欢抓沙发？',
    content: '家里的猫总爱抓沙发，后来才知道是有这些原因，大家可以试试这些方法来引导～',
    coverImage: IMG.sofaCat, tags: ['猫咪行为', '养猫知识', '家居保护'],
    viewText: '856', commentCount: 32, likeCount: 201, liked: false
  },
  {
    _key: 'd4', nickname: '宠物小百科', avatar: IMG.avatarCorgi, timeAgo: '12小时前',
    title: '狗狗疫苗接种全攻略，收藏备用！',
    content: '疫苗对狗狗的健康非常重要，下面整理了常见的疫苗类型和接种时间，包含详细注意事项～',
    coverImage: IMG.vaccineDog, tags: ['疫苗接种', '狗狗健康', '宠物医疗'],
    viewText: '1.8k', commentCount: 67, likeCount: 423, liked: false
  }
]

const list = ref([...demoList])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const loading = ref(false)

/* ========== 真实 API 渐进增强 ========== */
async function loadList(p) {
  if (p) page.value = p
  loading.value = true
  try {
    const sort = activeTab.value === 'hot' ? 'hot' : 'latest'
    const feed = activeTab.value === 'follow' ? 'follow' : 'all'
    const res = await listPost({ page: page.value, size: size.value, category: sortCat.value, sort, feed })
    const arr = res.data?.list || []
    if (arr.length) {
      list.value = arr.map((item, i) => ({
        ...item,
        _key: item.id || ('demo-' + i),
        nickname: item.nickname || '宠友' + String(item.userId || i + 1).padStart(3, '0'),
        avatar: item.avatar || defaultAvatars[i % 4],
        timeAgo: item.createTime ? timeAgo(item.createTime) : '刚刚',
        coverImage: item.coverImage || [IMG.kitten, IMG.beachDog, IMG.sofaCat, IMG.vaccineDog][i % 4],
        tags: item.tags || [item.category || '日常'],
        viewText: item.viewCount != null ? fmtNum(item.viewCount) : '—',
        liked: item.liked || false
      }))
      total.value = res.data?.total || 0
    } else {
      list.value = page.value === 1 && !sortCat.value ? [...demoList] : []
      total.value = list.value.length
    }
  } catch (e) {
    list.value = page.value === 1 && !sortCat.value ? [...demoList] : []
    total.value = list.value.length
  } finally {
    loading.value = false
  }
}

function fmtNum(n) {
  return n >= 1000 ? (n / 1000).toFixed(1) + 'k' : String(n)
}
function timeAgo(t) {
  if (!t) return '刚刚'
  const diff = Date.now() - new Date(String(t).replace(' ', 'T')).getTime()
  if (isNaN(diff) || diff < 0) return '刚刚'
  const h = Math.floor(diff / 3600000)
  if (h < 1) return '刚刚'
  if (h < 24) return h + '小时前'
  return Math.floor(h / 24) + '天前'
}

/* ========== 交互 ========== */
function onCategory(c) {
  if (c.action === 'soon') { ElMessage.info('活动专区即将上线，敬请期待'); return }
  if (c.action === 'topic') {
    document.querySelector('.topic-list')?.scrollIntoView({ behavior: 'smooth', block: 'center' })
    return
  }
  activeCat.value = c.name
  sortCat.value = c.action === 'all' ? '' : (c.value ?? '')
  activeTab.value = 'latest'
  loadList(1)
}

function onTab(key) {
  activeTab.value = key
  if (key === 'follow' && !isLogin.value) { ElMessage.warning('请先登录后查看关注动态'); activeTab.value = 'latest'; return }
  loadList(1)
}

async function onLike(p) {
  if (!isLogin.value) { ElMessage.warning('请先登录后再点赞'); return }
  if (p.liked) { ElMessage.info('已经点过赞啦'); return }
  p.liked = true
  p.likeCount = (p.likeCount || 0) + 1
  if (p.id) {
    try { await likePost(p.id) } catch (e) { /* 演示环境失败保持本地态 */ }
  }
}

/* ========== 右侧边栏数据（演示兜底 + API 渐进增强） ========== */
const topics = ref([
  { name: '新手养猫必看指南', hot: '12.8w' },
  { name: '狗狗能吃哪些水果', hot: '9.6w' },
  { name: '宠物驱虫怎么做', hot: '7.9w' },
  { name: '猫咪绝育的好处', hot: '5.9w' },
  { name: '养宠日常分享', hot: '4.2w' }
])
const activeUsers = ref([
  { name: '猫咪小天使', level: 'Lv.5', avatar: IMG.avatarCat, followed: false },
  { name: '金毛多多', level: 'Lv.3', avatar: IMG.avatarDog, followed: false },
  { name: '宠物小百科', level: 'Lv.1', avatar: IMG.avatarCorgi, followed: false },
  { name: '喵星人小分队', level: 'Lv.2', avatar: IMG.avatarGrey, followed: false }
])

async function loadTopics() {
  try {
    const res = await getTopics(5)
    const arr = res.data || []
    if (arr.length) {
      topics.value = arr.map(t => ({
        name: t.name,
        hot: t.hot != null ? fmtHot(t.hot) : '—'
      }))
    }
  } catch (e) { /* 失败保留演示数据 */ }
}

async function loadActiveUsers() {
  try {
    const res = await getActiveUsers(4)
    const arr = res.data || []
    if (arr.length) {
      activeUsers.value = arr.map((u, i) => ({
        name: u.nickname || '宠友' + String(i + 1).padStart(3, '0'),
        level: `Lv.${u.level ?? 1}`,
        avatar: u.avatar || defaultAvatars[i % 4],
        followed: u.followed || false,
        userId: u.userId
      }))
    }
  } catch (e) { /* 失败保留演示数据 */ }
}

function fmtHot(n) {
  if (n >= 10000) return (n / 10000).toFixed(1) + 'w'
  return String(n)
}

function onMoreTopics() { ElMessage.info('话题广场即将上线，敬请期待') }
function onMoreUsers() { ElMessage.info('用户榜单即将上线，敬请期待') }
function searchTopic(t) { ElMessage.info(`话题「${t.name}」聚合页即将上线`) }

async function onFollow(u) {
  if (!isLogin.value) { ElMessage.warning('请先登录后再关注'); return }
  if (!u.userId) { ElMessage.info('演示用户，暂不支持关注'); return }
  try {
    const res = await toggleFollow(u.userId)
    const followed = res.data ?? !u.followed
    u.followed = followed
    ElMessage.success(u.followed ? `已关注 ${u.name}` : `已取消关注 ${u.name}`)
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || '操作失败，请稍后重试')
  }
}

function onPageChange(p) { loadList(p) }
function goDetail(id) {
  if (id) router.push(`/community/${id}`)
  else ElMessage.info('演示帖子，发布真实帖子后可查看详情')
}
function goPublish() {
  if (!isLogin.value) { ElMessage.warning('请先登录'); router.push('/login'); return }
  router.push('/community/publish')
}

onMounted(() => {
  loadList(1)
  loadTopics()
  loadActiveUsers()
})
</script>

<style scoped>
.community-page {
  min-height: 100%;
  background: #f5f7fb;
  padding: 18px clamp(16px, 2vw, 32px) 40px;
}

/* ================= Hero ================= */
.hero {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
  border-radius: 22px;
  padding: 30px 40px;
  min-height: 300px;
  background-color: #dfe8ff;
  background-size: cover;
  background-position: center right;
  overflow: hidden;
  margin-bottom: 20px;
}
/* 左侧浅色遮罩：保证文字可读，右侧完整透出背景图 */
.hero::before {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(90deg,
    rgba(238, 243, 255, .97) 0%,
    rgba(238, 243, 255, .9) 32%,
    rgba(238, 243, 255, .45) 58%,
    rgba(238, 243, 255, .05) 80%);
}
.hero-left { position: relative; z-index: 2; flex-shrink: 0; }
.hero-title { display: flex; flex-direction: column; gap: 4px; margin-bottom: 12px; }
.ht-line {
  font-size: clamp(28px, 3vw, 38px);
  font-weight: 800;
  letter-spacing: 2px;
  background: linear-gradient(120deg, #6d5ce0, #8b5cf6);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}
.ht-l2 { margin-left: 46px; }
.hero-sub {
  margin: 0 0 20px;
  color: #5b6480;
  font-size: 14px;
  letter-spacing: .5px;
}
.hero-btn {
  height: 44px;
  padding: 0 26px;
  border: none;
  color: #fff;
  font-size: 15px;
  font-weight: 600;
  letter-spacing: 1px;
  background: linear-gradient(135deg, #7c8df6, #6d5ce0);
  box-shadow: 0 10px 22px rgba(109, 92, 224, .35);
  display: inline-flex;
  align-items: center;
  gap: 6px;
}
.hero-btn:hover { opacity: .92; }
.btn-arrow { width: 16px; height: 16px; }
.hero-heart { position: absolute; top: 22px; right: 42%; width: 22px; opacity: .9; z-index: 1; }
.hero-paw { position: absolute; width: 26px; color: #b8c4f5; }
.hero-paw.p1 { left: 38%; bottom: 16px; transform: rotate(-18deg); opacity: .5; }
.hero-paw.p2 { right: 30px; top: 20px; transform: rotate(20deg); opacity: .4; }

/* ================= 分类图标行 ================= */
.cat-row {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  background: #fff;
  border: 1px solid #edf0f8;
  border-radius: 16px;
  box-shadow: 0 10px 28px rgba(94, 110, 190, .07);
  padding: 18px clamp(18px, 4vw, 60px);
  margin-bottom: 20px;
}
.cat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 9px;
  cursor: pointer;
  transition: transform .2s;
}
.cat-item:hover { transform: translateY(-3px); }
.cat-icon {
  width: 58px;
  height: 58px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8px 18px rgba(94, 110, 190, .16);
}
.cat-icon svg { width: 26px; height: 26px; }
.cat-name { font-size: 13px; color: #3a4560; font-weight: 500; }
.cat-item.on .cat-name { color: #6d5ce0; font-weight: 700; }

/* ================= 主区布局 ================= */
.main-grid {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 320px;
  gap: 20px;
  align-items: start;
}

/* ---- 左侧 ---- */
.feed-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 14px;
}
.feed-tabs { display: flex; gap: 26px; }
.feed-tab {
  position: relative;
  font-size: 16px;
  font-weight: 600;
  color: #7a8499;
  cursor: pointer;
  padding-bottom: 8px;
  transition: color .2s;
}
.feed-tab.on { color: #202a4a; }
.feed-tab.on::after {
  content: '';
  position: absolute;
  left: 50%;
  bottom: 0;
  transform: translateX(-50%);
  width: 22px;
  height: 3px;
  border-radius: 2px;
  background: linear-gradient(90deg, #7c8df6, #6d5ce0);
}
.feed-select { width: 110px; }

.post-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
  min-height: 200px;
}
.post-card {
  background: #fff;
  border: 1px solid #edf0f8;
  border-radius: 16px;
  overflow: hidden;
  cursor: pointer;
  box-shadow: 0 10px 28px rgba(94, 110, 190, .07);
  transition: transform .2s, box-shadow .2s;
}
.post-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 16px 36px rgba(94, 110, 190, .12);
}
.post-card.featured {
  grid-column: 1 / -1;
  display: grid;
  grid-template-columns: 300px minmax(0, 1fr);
}
.post-card.featured .pc-cover { height: 100%; min-height: 220px; }
.pc-cover { height: 170px; overflow: hidden; }
.pc-cover img { width: 100%; height: 100%; object-fit: cover; transition: transform .35s; display: block; }
.post-card:hover .pc-cover img { transform: scale(1.05); }
.pc-body { padding: 16px 18px; min-width: 0; }
.pc-author { display: flex; align-items: center; gap: 10px; margin-bottom: 10px; }
.pc-avatar { width: 36px; height: 36px; border-radius: 50%; object-fit: cover; flex-shrink: 0; }
.pc-author-info { display: flex; flex-direction: column; line-height: 1.3; min-width: 0; }
.pc-author-info b { font-size: 13.5px; color: #2b3350; }
.pc-author-info small { font-size: 11.5px; color: #a0a8c0; }
.pc-more { margin-left: auto; color: #b0b7cc; font-size: 18px; letter-spacing: 2px; padding: 0 4px; }
.pc-title {
  margin: 0 0 8px;
  font-size: 16.5px;
  color: #202a4a;
  line-height: 1.45;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.post-card.featured .pc-title { font-size: 19px; -webkit-line-clamp: 2; }
.pc-desc {
  margin: 0 0 10px;
  font-size: 13px;
  color: #7a8499;
  line-height: 1.65;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.pc-tags { display: flex; flex-wrap: wrap; gap: 8px; margin-bottom: 12px; }
.pc-tag { font-size: 12px; color: #6d5ce0; background: #f1eeff; border-radius: 20px; padding: 3px 10px; }
.pc-foot { display: flex; gap: 20px; color: #a0a8c0; font-size: 12.5px; }
.pf-item { display: inline-flex; align-items: center; gap: 5px; }
.pf-item svg { width: 15px; height: 15px; }
.pf-like { cursor: pointer; transition: color .2s; }
.pf-like:hover { color: #f0528f; }
.pf-like.liked { color: #f0528f; }

.post-empty {
  grid-column: 1 / -1;
  text-align: center;
  padding: 60px 0;
  color: #a0a8c0;
}
.pe-icon {
  width: 72px;
  height: 72px;
  margin: 0 auto 14px;
  border-radius: 50%;
  background: #eef1fb;
  display: flex;
  align-items: center;
  justify-content: center;
}
.pe-icon svg { width: 34px; height: 34px; }
.pagination { margin-top: 20px; justify-content: center; display: flex; }

/* ---- 右侧 ---- */
.main-side { display: flex; flex-direction: column; gap: 16px; min-width: 0; }
.side-card {
  background: #fff;
  border: 1px solid #edf0f8;
  border-radius: 16px;
  box-shadow: 0 10px 28px rgba(94, 110, 190, .07);
  padding: 18px;
}
.sc-head { display: flex; align-items: center; justify-content: space-between; margin-bottom: 12px; }
.sc-title { display: inline-flex; align-items: center; gap: 8px; font-size: 15.5px; font-weight: 700; color: #202a4a; }
.sc-ic {
  width: 26px;
  height: 26px;
  border-radius: 8px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}
.sc-ic svg { width: 16px; height: 16px; }
.sc-ic.hot { background: linear-gradient(135deg, #ff9a7a, #ff6b6b); }
.sc-ic.hot :deep(svg) path { fill: #fff; }
.sc-ic.user { background: linear-gradient(135deg, #7c8df6, #6d5ce0); }
.sc-more { font-size: 12px; color: #a0a8c0; cursor: pointer; }
.sc-more:hover { color: #6d5ce0; }

.topic-list { list-style: none; margin: 0; padding: 0; }
.topic-list li {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 9px 0;
  cursor: pointer;
  border-bottom: 1px dashed #f0f2f9;
}
.topic-list li:last-child { border-bottom: none; }
.tp-rank {
  width: 20px;
  height: 20px;
  border-radius: 6px;
  background: #f0f2f9;
  color: #8a93b2;
  font-size: 12px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.tp-rank.top { background: linear-gradient(135deg, #ff9a7a, #ff6b6b); color: #fff; }
.tp-name {
  flex: 1;
  min-width: 0;
  font-size: 13.5px;
  color: #3a4560;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.topic-list li:hover .tp-name { color: #6d5ce0; }
.tp-hot { font-size: 11.5px; color: #b0b7cc; display: inline-flex; align-items: center; gap: 3px; }

.user-list { list-style: none; margin: 0; padding: 0; }
.user-list li {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 9px 0;
  border-bottom: 1px dashed #f0f2f9;
}
.user-list li:last-child { border-bottom: none; }
.ul-avatar { width: 38px; height: 38px; border-radius: 50%; object-fit: cover; flex-shrink: 0; }
.ul-info { flex: 1; min-width: 0; display: flex; flex-direction: column; line-height: 1.35; }
.ul-info b { font-size: 13.5px; color: #2b3350; }
.ul-info small { font-size: 11px; color: #b088f9; font-weight: 600; }
.ul-follow {
  border: none;
  background: #f1eeff;
  color: #6d5ce0;
  font-size: 12px;
  padding: 5px 12px;
  border-radius: 20px;
  cursor: pointer;
  transition: all .2s;
  flex-shrink: 0;
}
.ul-follow:hover { background: #e6e0ff; }
.ul-follow.followed { background: #f0f2f9; color: #a0a8c0; }

/* 加入社区 */
.join-card {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  border-radius: 16px;
  padding: 20px;
  background: linear-gradient(130deg, #7c8df6, #6d5ce0 70%, #5b48d6);
  overflow: hidden;
  color: #fff;
}
.join-left { position: relative; z-index: 2; }
.join-title { font-size: 17px; font-weight: 800; letter-spacing: 1px; }
.join-sub { margin: 6px 0 14px; font-size: 12px; opacity: .88; }
.join-btn {
  height: 36px;
  padding: 0 18px;
  border: none;
  border-radius: 20px;
  background: #fff;
  color: #6d5ce0;
  font-size: 13px;
  font-weight: 700;
  display: inline-flex;
  align-items: center;
  gap: 5px;
  box-shadow: 0 6px 16px rgba(0, 0, 0, .15);
}
.join-btn:hover { background: #f6f4ff; color: #6d5ce0; }
.join-img {
  width: 108px;
  border-radius: 12px;
  position: relative;
  z-index: 1;
  flex-shrink: 0;
}
.join-paw { position: absolute; right: 14px; top: 12px; width: 22px; color: rgba(255, 255, 255, .35); }

/* ================= 响应式 ================= */
@media (max-width: 1100px) {
  .main-grid { grid-template-columns: minmax(0, 1fr); }
  .main-side { flex-direction: row; flex-wrap: wrap; }
  .main-side > * { flex: 1 1 280px; }
}
@media (max-width: 860px) {
  .post-grid { grid-template-columns: minmax(0, 1fr); }
  .post-card.featured { grid-template-columns: minmax(0, 1fr); }
  .post-card.featured .pc-cover { min-height: 180px; }
  .hero { flex-direction: column; padding: 26px 22px; min-height: 240px; }
  .ht-l2 { margin-left: 0; }
  .cat-row { flex-wrap: wrap; justify-content: center; }
}
</style>
