<template>
  <div class="adoption-page">
    <div class="adoption-layout">
      <!-- 左侧主区 -->
      <div class="adoption-main">
        <!-- Hero Banner -->
        <section class="hero-banner">
          <div class="hero-text">
            <h2 class="hero-title">用领养代替购买<br />让每一个生命都有家</h2>
            <p class="hero-sub">领养不只是带回一只宠物　更是给TA一个温暖的未来</p>
            <el-button class="hero-btn" type="primary" round @click="scrollToPets">查看领养信息 →</el-button>
          </div>
          <img :src="IMG.heroPets" class="hero-img" alt="pets" />
        </section>

        <!-- 分类导航 -->
        <section class="category-nav">
          <div
            v-for="c in categories"
            :key="c.name"
            class="cat-item"
            @click="onCategoryClick(c)"
          >
            <div class="cat-icon" :style="{ background: c.bg }">
              <span class="cat-emoji">{{ c.icon }}</span>
            </div>
            <span class="cat-name">{{ c.name }}</span>
          </div>
        </section>

        <!-- Tab + 搜索 -->
        <section class="pets-section" id="pets">
          <div class="section-head">
            <div class="tabs">
              <span
                v-for="t in tabs"
                :key="t"
                class="tab"
                :class="{ active: activeTab === t }"
                @click="activeTab = t; onTabChange(t)"
              >{{ t }}</span>
            </div>
            <div class="search-box">
              <el-input
                v-model="keyword"
                placeholder="搜索你想领养的宠物..."
                clearable
                @keyup.enter="loadList(1)"
              >
                <template #prefix><el-icon><Search /></el-icon></template>
              </el-input>
            </div>
          </div>

          <!-- 筛选条 -->
          <div class="filter-bar">
            <el-select v-model="filterArea" placeholder="地区" clearable size="default" @change="loadList(1)">
              <el-option label="全部地区" value="" />
              <el-option v-for="a in areaList" :key="a" :label="a" :value="a" />
            </el-select>
            <el-select v-model="filterType" placeholder="宠物类型" clearable @change="loadList(1)">
              <el-option label="全部" value="" />
              <el-option label="狗狗" value="狗" />
              <el-option label="猫咪" value="猫" />
            </el-select>
            <el-select v-model="filterAge" placeholder="年龄" clearable @change="loadList(1)">
              <el-option label="全部" value="" />
              <el-option v-for="a in ageList" :key="a" :label="a" :value="a" />
            </el-select>
            <el-select v-model="filterGender" placeholder="性别" clearable @change="loadList(1)">
              <el-option label="全部" value="" />
              <el-option label="男生" value="公" />
              <el-option label="女生" value="母" />
            </el-select>
            <el-select v-model="filterSize" placeholder="体型" clearable @change="loadList(1)">
              <el-option label="全部" value="" />
              <el-option v-for="s in sizeList" :key="s" :label="s" :value="s" />
            </el-select>
            <el-select v-model="filterSteril" placeholder="是否已绝育" clearable @change="loadList(1)">
              <el-option label="全部" value="" />
              <el-option label="已绝育" value="是" />
              <el-option label="未绝育" value="否" />
            </el-select>
          </div>

          <!-- 宠物卡片网格 -->
          <div class="pet-grid" v-if="activeTab === '待领养'" v-loading="loading">
            <div
              v-for="p in list"
              :key="p.id"
              class="pet-card"
              @click="goDetail(p.id)"
            >
              <div class="pet-img-box">
                <span class="pet-tag">待领养</span>
                <span class="heart" @click.stop="toggleFav(p)">
                  <el-icon :color="p.fav ? '#f56c6c' : '#fff'"><Star /></el-icon>
                </span>
                <img :src="p.photoUrl" :alt="p.petName" class="pet-img" />
              </div>
              <div class="pet-body">
                <h3 class="pet-name">{{ p.petName }}</h3>
                <div class="pet-tags">
                  <span class="ptag">{{ p.petType || '未知' }}</span>
                  <span class="ptag">{{ p.age || '未知' }}</span>
                  <span class="ptag">{{ p.gender || '未知' }}</span>
                  <span class="ptag">{{ p.breed || '未知品种' }}</span>
                </div>
                <p class="pet-desc">{{ p.description }}</p>
                <div class="pet-foot">
                  <span class="pet-loc"><el-icon><Location /></el-icon> {{ p.location || '待补充' }}</span>
                  <el-button type="primary" size="small" round class="apply-btn" @click.stop="goDetail(p.id)">申请领养</el-button>
                </div>
              </div>
            </div>
            <el-empty v-if="!loading && list.length === 0" description="暂无待领养宠物" />
          </div>

          <!-- 寄养服务 -->
          <div v-else-if="activeTab === '寄养服务'" class="foster-service-content">
            <div class="service-intro">
              <h3>专业寄养服务</h3>
              <p>让出行更安心，专业团队为您的爱宠提供贴心照护</p>
            </div>
            <div class="service-list">
              <div class="service-card" v-for="s in fosterServices" :key="s.title">
                <div class="svc-icon" :style="{ background: s.bg }">{{ s.icon }}</div>
                <div class="svc-info">
                  <h4>{{ s.title }}</h4>
                  <p>{{ s.desc }}</p>
                  <el-button type="primary" size="small" round @click="goPublish">立即预约</el-button>
                </div>
              </div>
            </div>
          </div>

          <!-- 公益救助 -->
          <div v-else-if="activeTab === '公益救助'" class="charity-content-wrap">
            <div class="service-intro">
              <h3>爱心公益救助</h3>
              <p>用行动帮助更多流浪毛孩子，让爱传递</p>
            </div>
            <div class="charity-list">
              <div class="charity-card" v-for="c in charityList" :key="c.title">
                <div class="cc-icon">{{ c.icon }}</div>
                <h4>{{ c.title }}</h4>
                <p>{{ c.desc }}</p>
                <el-button type="danger" size="small" round @click="goPublish">参与救助</el-button>
              </div>
            </div>
          </div>

          <!-- 领养故事 -->
          <div v-else-if="activeTab === '领养故事'" class="stories-content">
            <div class="service-intro">
              <h3>领养故事</h3>
              <p>每一个领养都是一段温暖的相遇</p>
            </div>
            <div class="story-list">
              <div class="story-card" v-for="s in storyList" :key="s.title">
                <img :src="s.img" :alt="s.title" class="story-img" />
                <div class="story-body">
                  <h4>{{ s.title }}</h4>
                  <p>{{ s.content }}</p>
                  <span class="story-date">{{ s.date }}</span>
                </div>
              </div>
            </div>
          </div>
        </section>
      </div>

      <!-- 右侧边栏 -->
      <aside class="adoption-sidebar">
        <!-- 专业寄养服务 -->
        <section class="foster-card">
          <div class="foster-text">
            <h3 class="foster-title">专业寄养服务</h3>
            <p class="foster-sub">让出行更安心</p>
            <div class="foster-tags">
              <span>✓ 安全环境</span>
              <span>✓ 专业照顾</span>
              <span>✓ 实时反馈</span>
            </div>
            <el-button class="foster-btn" round @click="goPublish">立即预约 →</el-button>
          </div>
          <img :src="IMG.fosterDog" class="foster-img" alt="" />
        </section>

        <!-- 寄养服务选项 -->
        <section class="sidebar-card">
          <div class="foster-grid">
            <div class="foster-item" @click="activeTab = '寄养服务'">
              <div class="fi-icon" style="background:#eef3ff;"><el-icon color="#6d5ce0"><HomeFilled /></el-icon></div>
              <div class="fi-text">
                <div class="fi-title">家庭式寄养</div>
                <div class="fi-sub">像在家一样温暖</div>
              </div>
            </div>
            <div class="foster-item">
              <div class="fi-icon" style="background:#fff3e0;"><el-icon color="#ff8a3d"><OfficeBuilding /></el-icon></div>
              <div class="fi-text">
                <div class="fi-title">专业宠物机构</div>
                <div class="fi-sub">专业团队照护</div>
              </div>
            </div>
            <div class="foster-item">
              <div class="fi-icon" style="background:#e8f5e9;"><el-icon color="#5ce0b0"><User /></el-icon></div>
              <div class="fi-text">
                <div class="fi-title">上门喂养</div>
                <div class="fi-sub">贴心到家服务</div>
              </div>
            </div>
            <div class="foster-item">
              <div class="fi-icon" style="background:#fce4ec;"><el-icon color="#e05c9c"><Calendar /></el-icon></div>
              <div class="fi-text">
                <div class="fi-title">短期/长期寄养</div>
                <div class="fi-sub">灵活时间选择</div>
              </div>
            </div>
          </div>
        </section>

        <!-- 领养须知 -->
        <section class="sidebar-card notice-card" @click="goPublish">
          <div class="notice-icon"><el-icon :size="24" color="#6d5ce0"><Reading /></el-icon></div>
          <div class="notice-text">
            <div class="notice-title">领养须知</div>
            <div class="notice-sub">了解领养条件与流程，给TA一个负责任的家</div>
          </div>
          <el-icon class="notice-arrow"><ArrowRight /></el-icon>
        </section>

        <!-- 爱心公益 -->
        <section class="sidebar-card charity-card">
          <div class="charity-content">
            <div class="charity-icon">❤️</div>
            <div class="charity-text">
              <div class="charity-title">爱心公益</div>
              <div class="charity-sub">用行动帮助更多流浪毛孩子</div>
            </div>
            <el-button type="danger" round class="charity-btn" @click="goPublish">立即参与 →</el-button>
          </div>
          <img :src="IMG.heroPets" class="charity-img" alt="" />
        </section>
      </aside>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search, Star, Location, HomeFilled, OfficeBuilding, User, Calendar, Reading, ArrowRight } from '@element-plus/icons-vue'
import { listAdoption, toggleFavorite, myFavoriteIds } from '@/api/adoption'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const isLogin = ref(!!userStore.token)

/* ========== 图片资源 ========== */
const IMG = {
  heroPets: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=' +
    encodeURIComponent('Adorable golden retriever dog and grey tabby white cat sitting close together, bright modern living room with green plants and big window, soft natural sunlight, warm bokeh background, professional pet photography') +
    '&image_size=landscape_16_9',
  fosterDog: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=' +
    encodeURIComponent('Happy golden retriever dog lying down relaxed at home, warm cozy living room background, professional pet photography') +
    '&image_size=portrait_4_3',
  dog1: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=' +
    encodeURIComponent('Cute golden retriever puppy portrait smiling with tongue out, outdoor park background, professional pet photography') +
    '&image_size=square_hd',
  cat1: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=' +
    encodeURIComponent('Adorable british shorthair kitten portrait, big eyes, soft indoor background, professional pet photography') +
    '&image_size=square_hd',
  dog2: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=' +
    encodeURIComponent('Happy corgi dog portrait smiling outdoors, green grass background, professional pet photography') +
    '&image_size=square_hd',
  cat2: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=' +
    encodeURIComponent('Beautiful ragdoll cat portrait blue eyes, soft background, professional pet photography') +
    '&image_size=square_hd'
}

/* ========== 分类导航 ========== */
const categories = [
  { name: '我要领养', icon: '🐾', bg: 'linear-gradient(135deg,#fff3e0,#ffe0b2)' },
  { name: '我要寄养', icon: '🏠', bg: 'linear-gradient(135deg,#e3f2fd,#bbdefb)' },
  { name: '领养流程', icon: '📋', bg: 'linear-gradient(135deg,#fce4ec,#f8bbd0)' },
  { name: '寄养流程', icon: '✅', bg: 'linear-gradient(135deg,#e0f2f1,#b2dfdb)' },
  { name: '爱心机构', icon: '💚', bg: 'linear-gradient(135deg,#f3e5f5,#e1bee7)' },
  { name: '宠物救助', icon: '🌱', bg: 'linear-gradient(135deg,#e8f5e9,#c8e6c9)' }
]

const tabs = ['待领养', '寄养服务', '公益救助', '领养故事']
const activeTab = ref('待领养')

/* ========== 寄养服务数据 ========== */
const fosterServices = [
  { icon: '🏠', title: '家庭式寄养', desc: '像在家一样温暖，专业寄养家庭一对一照护', bg: '#eef3ff' },
  { icon: '🏢', title: '专业宠物机构', desc: '正规宠物机构，24小时监控，专业团队照护', bg: '#fff3e0' },
  { icon: '👤', title: '上门喂养', desc: '工作人员上门喂养、遛弯，贴心到家服务', bg: '#e8f5e9' },
  { icon: '📅', title: '短期/长期寄养', desc: '灵活时间选择，支持短期出差和长期寄养', bg: '#fce4ec' }
]

/* ========== 公益救助数据 ========== */
const charityList = [
  { icon: '🏥', title: '流浪动物救助', desc: '救助受伤、生病的流浪动物，提供医疗救治' },
  { icon: '🍖', title: '爱心投喂', desc: '为流浪动物提供食物和水源，帮助它们度过寒冬' },
  { icon: '🏠', title: '临时寄养', desc: '为救助的流浪动物提供临时寄养，等待领养' },
  { icon: '💰', title: '爱心捐赠', desc: '捐赠物资或资金，支持流浪动物救助工作' }
]

/* ========== 领养故事数据 ========== */
const storyList = [
  { title: '小黑的新家', content: '小黑是一只被遗弃的黑色拉布拉多，在救助站待了3个月后，终于遇到了它的新主人。现在的小黑每天都在公园里快乐奔跑。', date: '2024-03-15', img: IMG.dog1 },
  { title: '橘座的逆袭', content: '胖乎乎的橘猫橘座曾经流浪街头，被救助后减肥成功，现在是家里的大王，每天享受着铲屎官的膜拜。', date: '2024-02-28', img: IMG.cat1 },
  { title: '小白的春天', content: '小白是一只白色田园犬，被前主人虐待后获救。经过耐心的康复训练，它重新信任了人类，找到了温暖的家。', date: '2024-01-20', img: IMG.dog2 }
]

/* ========== 筛选 ========== */
const keyword = ref('')
const filterArea = ref('')
const filterType = ref('')
const filterAge = ref('')
const filterGender = ref('')
const filterSize = ref('')
const filterSteril = ref('')

const areaList = ['北京市', '广州市', '深圳市', '杭州市', '成都市', '上海市']
const ageList = ['幼年', '1岁', '2岁', '3岁', '成年', '老年']
const sizeList = ['小型', '中型', '大型']

/* ========== 列表数据 ========== */
const list = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(8)
const loading = ref(false)
const favIds = ref(new Set())

const demoList = [
  { id: 201, petName: '豆豆', petType: '狗狗', age: '1岁', gender: '男生', breed: '金毛寻回犬', photoUrl: IMG.dog1, description: '性格温顺，活泼亲人，喜欢和人互动，希望能找到一个温暖的家。', location: '广州市 天河区', fav: false },
  { id: 202, petName: '奶糖', petType: '猫咪', age: '3个月', gender: '女生', breed: '英短', photoUrl: IMG.cat1, description: '活泼好动，已完成疫苗接种，健康可爱，适合有耐心的家庭。', location: '深圳市 南山区', fav: false },
  { id: 203, petName: '球球', petType: '狗狗', age: '2岁', gender: '女生', breed: '柯基', photoUrl: IMG.dog2, description: '性格开朗，亲人友好，喜欢散步和玩耍，希望寻找爱她的主人。', location: '杭州市 西湖区', fav: false },
  { id: 204, petName: '布丁', petType: '猫咪', age: '1岁', gender: '男生', breed: '布偶猫', photoUrl: IMG.cat2, description: '温顺安静，喜欢被摸摸，已绝育，期待一个有爱的家庭。', location: '成都市 武侯区', fav: false }
]

async function loadList(p = page.value) {
  page.value = p
  loading.value = true
  try {
    const res = await listAdoption({
      page: p, size: size.value, type: 'adoption',
      petType: filterType.value || undefined,
      age: filterAge.value || undefined,
      gender: filterGender.value || undefined,
      area: filterArea.value || undefined,
      sizeType: filterSize.value || undefined,
      steril: filterSteril.value || undefined,
      keyword: keyword.value || undefined
    })
    const arr = res.data?.list || []
    if (arr.length) {
      list.value = arr.map((item, i) => ({
        ...item,
        photoUrl: item.photoUrl || [IMG.dog1, IMG.cat1, IMG.dog2, IMG.cat2][i % 4],
        location: item.area || item.contact || '待补充',
        fav: favIds.value.has(item.id)
      }))
      total.value = res.data?.total || 0
    } else {
      list.value = demoList
    }
  } catch (e) {
    list.value = demoList
  } finally {
    loading.value = false
  }
}

function onTabChange(t) {
  if (t === '寄养服务') {
    router.push('/adoption/publish')
  }
}

function onCategoryClick(c) {
  if (c.name === '我要领养') { activeTab.value = '待领养'; loadList(1) }
  else if (c.name === '我要寄养') { goPublish() }
  else if (c.name === '领养流程') { activeTab.value = '领养故事'; ElMessage.info('请查看下方领养故事了解领养流程') }
  else if (c.name === '寄养流程') { activeTab.value = '寄养服务'; ElMessage.info('请查看下方寄养服务了解寄养流程') }
  else if (c.name === '爱心机构' || c.name === '宠物救助') { activeTab.value = '公益救助'; ElMessage.info('请查看下方公益救助板块') }
  else { ElMessage.info('功能开发中') }
}

function toggleFav(p) {
  if (!isLogin.value) {
    ElMessage.warning('请先登录后再收藏')
    router.push('/login')
    return
  }
  toggleFavorite(p.id).then(res => {
    const favorited = res.data
    p.fav = favorited
    if (favorited) {
      favIds.value.add(p.id)
      ElMessage.success('已收藏')
    } else {
      favIds.value.delete(p.id)
      ElMessage.info('已取消收藏')
    }
  }).catch(() => {
    ElMessage.error('操作失败')
  })
}

async function loadFavIds() {
  if (!isLogin.value) return
  try {
    const res = await myFavoriteIds()
    favIds.value = new Set(res.data || [])
    list.value.forEach(item => { item.fav = favIds.value.has(item.id) })
  } catch (e) {}
}

function goDetail(id) {
  if (id > 200) return
  router.push(`/adoption/${id}`)
}

function goPublish() {
  if (!isLogin.value) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  router.push('/adoption/publish')
}

function scrollToPets() {
  document.getElementById('pets')?.scrollIntoView({ behavior: 'smooth' })
}

onMounted(() => {
  loadList(1)
  loadFavIds()
})
</script>

<style scoped>
/* ========== 新增Tab内容样式 ========== */
.service-intro { margin-bottom: 20px; }
.service-intro h3 { margin: 0 0 6px; font-size: 22px; color: #202a4a; }
.service-intro p { margin: 0; color: #7a8499; font-size: 14px; }

.foster-service-content .service-list {
  display: grid; grid-template-columns: repeat(2, 1fr); gap: 16px;
}
.foster-service-content .service-card {
  display: flex; gap: 14px; align-items: center;
  background: #fff; border-radius: 14px; padding: 18px;
  box-shadow: 0 2px 10px rgba(0,0,0,.05);
}
.foster-service-content .svc-icon {
  width: 52px; height: 52px; border-radius: 14px;
  display: flex; align-items: center; justify-content: center;
  font-size: 26px; flex-shrink: 0;
}
.foster-service-content .svc-info h4 { margin: 0 0 4px; font-size: 16px; }
.foster-service-content .svc-info p { margin: 0 0 10px; font-size: 13px; color: #7a8499; }

.charity-content-wrap .charity-list {
  display: grid; grid-template-columns: repeat(2, 1fr); gap: 16px;
}
.charity-content-wrap .charity-card {
  background: #fff; border-radius: 14px; padding: 22px; text-align: center;
  box-shadow: 0 2px 10px rgba(0,0,0,.05);
}
.charity-content-wrap .cc-icon { font-size: 36px; margin-bottom: 10px; }
.charity-content-wrap .charity-card h4 { margin: 0 0 8px; font-size: 16px; }
.charity-content-wrap .charity-card p { margin: 0 0 14px; font-size: 13px; color: #7a8499; }

.stories-content .story-list { display: flex; flex-direction: column; gap: 16px; }
.stories-content .story-card {
  display: flex; gap: 16px; background: #fff; border-radius: 14px;
  padding: 16px; box-shadow: 0 2px 10px rgba(0,0,0,.05);
}
.stories-content .story-img { width: 120px; height: 90px; border-radius: 10px; object-fit: cover; flex-shrink: 0; }
.stories-content .story-body h4 { margin: 0 0 6px; font-size: 16px; }
.stories-content .story-body p { margin: 0 0 8px; font-size: 13px; color: #5b6480; line-height: 1.6; }
.stories-content .story-date { font-size: 12px; color: #a0a8c0; }

.adoption-page { min-height: 100%; background: #f5f7fb; }
.adoption-layout {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 340px;
  gap: 20px;
  padding: 18px clamp(16px, 2vw, 32px) 40px;
  width: 100%;
  max-width: none;
  margin: 0;
}
.adoption-main { min-width: 0; }

/* Hero */
.hero-banner {
  position: relative;
  display: flex; align-items: center; justify-content: space-between;
  background: linear-gradient(125deg, #eef3ff, #e6ecff, #dfe8ff);
  border-radius: 22px; padding: 32px 44px; overflow: hidden; margin-bottom: 18px;
}
.hero-title {
  font-size: 34px; font-weight: 800; color: #2c3e7b; line-height: 1.3; margin: 0 0 10px;
  font-family: 'STKaiti', 'KaiTi', serif;
}
.hero-sub { color: #6b7aa8; font-size: 14px; margin: 0 0 20px; }
.hero-btn {
  height: 42px; padding: 0 26px; font-size: 14px;
  background: linear-gradient(135deg, #7c8df6, #6d5ce0); border: none;
  box-shadow: 0 6px 18px rgba(109, 92, 224, .35);
}
.hero-img { width: 320px; height: 200px; object-fit: cover; border-radius: 18px; }

/* 分类导航 */
.category-nav {
  display: grid; grid-template-columns: repeat(6, 1fr); gap: 10px;
  background: #fff; border-radius: 16px; padding: 20px 16px; margin-bottom: 18px;
  box-shadow: 0 2px 10px rgba(94, 110, 190, .05);
}
.cat-item { display: flex; flex-direction: column; align-items: center; gap: 8px; cursor: pointer; }
.cat-icon { width: 52px; height: 52px; border-radius: 50%; display: flex; align-items: center; justify-content: center; }
.cat-emoji { font-size: 24px; }
.cat-name { font-size: 13px; color: #4a5578; font-weight: 500; }

/* 宠物区 */
.pets-section { background: #fff; border-radius: 16px; padding: 18px; }
.section-head { display: flex; align-items: center; justify-content: space-between; gap: 16px; margin-bottom: 16px; }
.tabs { display: flex; gap: 22px; }
.tab {
  font-size: 16px; color: #8a93ab; cursor: pointer; padding-bottom: 6px;
  border-bottom: 2px solid transparent; transition: all .2s; font-weight: 500;
}
.tab.active { color: #6d5ce0; border-bottom-color: #6d5ce0; font-weight: 600; }
.search-box { width: 260px; }

.filter-bar { display: flex; gap: 10px; flex-wrap: wrap; margin-bottom: 18px; }
.filter-bar .el-select { width: 120px; }

.pet-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 14px; }
.pet-card {
  background: #fff; border: 1px solid #edf0f8; border-radius: 14px;
  overflow: hidden; cursor: pointer; transition: all .25s;
}
.pet-card:hover { transform: translateY(-4px); box-shadow: 0 10px 28px rgba(94, 110, 190, .12); }
.pet-img-box { position: relative; height: 180px; background: #f7f8fc; }
.pet-img { width: 100%; height: 100%; object-fit: cover; }
.pet-tag {
  position: absolute; top: 10px; left: 10px; padding: 3px 10px; border-radius: 6px;
  font-size: 12px; color: #fff; background: #ff8a3d; font-weight: 600;
}
.heart {
  position: absolute; top: 10px; right: 10px; width: 28px; height: 28px;
  border-radius: 50%; background: rgba(0,0,0,.25); display: flex; align-items: center; justify-content: center;
}
.pet-body { padding: 12px; }
.pet-name { margin: 0 0 8px; font-size: 16px; font-weight: 600; color: #303133; }
.pet-tags { display: flex; flex-wrap: wrap; gap: 6px; margin-bottom: 8px; }
.ptag {
  padding: 1px 8px; border-radius: 4px; font-size: 11px;
  background: #f2f4fa; color: #6b7aa8;
}
.pet-desc {
  font-size: 12px; color: #909399; margin: 0 0 10px; line-height: 1.5;
  display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden;
}
.pet-foot { display: flex; align-items: center; justify-content: space-between; }
.pet-loc { font-size: 12px; color: #909399; display: flex; align-items: center; gap: 2px; }
.apply-btn {
  background: linear-gradient(135deg, #7c8df6, #6d5ce0); border: none;
  height: 30px; padding: 0 16px;
}

/* 右侧边栏 */
.foster-card {
  position: relative;
  background: linear-gradient(135deg, #fff5e6, #ffe8cc);
  border-radius: 16px; padding: 20px; margin-bottom: 16px;
  display: flex; justify-content: space-between; align-items: center; overflow: hidden;
}
.foster-title { font-size: 18px; font-weight: 700; color: #d4880f; margin: 0 0 4px; }
.foster-sub { font-size: 14px; color: #b8860b; margin: 0 0 12px; }
.foster-tags { display: flex; flex-wrap: wrap; gap: 8px; margin-bottom: 16px; }
.foster-tags span { font-size: 12px; color: #d4880f; }
.foster-btn {
  background: #ff8a3d; border: none; color: #fff; height: 36px; padding: 0 20px;
}
.foster-img { width: 110px; height: 130px; object-fit: cover; border-radius: 12px; }

.sidebar-card {
  background: #fff; border-radius: 16px; padding: 16px; margin-bottom: 16px;
  box-shadow: 0 2px 10px rgba(94, 110, 190, .05);
}

.foster-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; }
.foster-item { display: flex; align-items: center; gap: 10px; cursor: pointer; }
.fi-icon {
  width: 38px; height: 38px; border-radius: 10px; display: flex; align-items: center; justify-content: center; flex-shrink: 0;
}
.fi-title { font-size: 13px; font-weight: 600; color: #303133; }
.fi-sub { font-size: 11px; color: #909399; }

.notice-card { display: flex; align-items: center; gap: 12px; cursor: pointer; }
.notice-icon {
  width: 44px; height: 44px; border-radius: 10px; background: #eef3ff;
  display: flex; align-items: center; justify-content: center; flex-shrink: 0;
}
.notice-text { flex: 1; min-width: 0; }
.notice-title { font-size: 14px; font-weight: 600; color: #303133; }
.notice-sub { font-size: 12px; color: #909399; }
.notice-arrow { color: #c0c4cc; }

.charity-card {
  position: relative;
  background: linear-gradient(135deg, #fff0f3, #ffe0e9);
  overflow: hidden;
}
.charity-content { position: relative; z-index: 2; }
.charity-icon { font-size: 28px; margin-bottom: 8px; }
.charity-title { font-size: 16px; font-weight: 700; color: #e05c7a; }
.charity-sub { font-size: 12px; color: #c0607a; margin: 4px 0 14px; }
.charity-btn { background: #ff6b8a; border: none; height: 36px; padding: 0 20px; }
.charity-img {
  position: absolute; right: 0; bottom: 0; width: 120px; height: 100px;
  object-fit: cover; border-radius: 12px 0 16px 0; opacity: .8;
}

/* 响应式 */
@media (max-width: 1280px) {
  .adoption-layout { grid-template-columns: minmax(0, 1fr) 300px; }
  .pet-grid { grid-template-columns: repeat(3, 1fr); }
  .hero-img { width: 260px; }
}
@media (max-width: 1024px) {
  .adoption-layout { grid-template-columns: 1fr; }
  .pet-grid { grid-template-columns: repeat(3, 1fr); }
}
@media (max-width: 768px) {
  .pet-grid { grid-template-columns: repeat(2, 1fr); }
  .category-nav { grid-template-columns: repeat(3, 1fr); }
  .hero-banner { flex-direction: column; text-align: center; padding: 24px; }
  .hero-img { width: 100%; height: 180px; margin-top: 16px; }
  .filter-bar .el-select { width: calc(50% - 5px); }
}
</style>
