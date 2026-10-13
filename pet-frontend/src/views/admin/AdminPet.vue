<template>
  <div class="admin-pet-page">
    <!-- 顶部 Banner -->
    <div class="hero-banner" :style="{ '--hero-img': `url(${heroImg})` }">
      <div class="h-left">
        <span class="h-icon">
          <svg viewBox="0 0 24 24" fill="currentColor"><path d="M12 13.5c-2.9 0-5.5 2.3-5.5 4.6 0 1.5 1.1 2.4 2.6 2.4 1.1 0 1.9-.5 2.9-.5s1.8.5 2.9.5c1.5 0 2.6-.9 2.6-2.4 0-2.3-2.6-4.6-5.5-4.6zM6.4 8.2c-1.2.3-2 1.6-1.8 2.9.2 1.2 1.3 2 2.5 1.8 1.2-.3 2-1.6 1.8-2.9-.2-1.2-1.3-2-2.5-1.8zm11.2 0c-1.2-.2-2.3.6-2.5 1.8-.2 1.3.6 2.6 1.8 2.9 1.2.2 2.3-.6 2.5-1.8.2-1.3-.6-2.6-1.8-2.9zM9.3 3.6c-1.1.2-1.9 1.4-1.7 2.7.2 1.2 1.2 2.1 2.4 1.9 1.1-.2 1.9-1.4 1.7-2.7-.2-1.2-1.3-2.1-2.4-1.9zm5.4 0c-1.1-.2-2.2.7-2.4 1.9-.2 1.3.6 2.5 1.7 2.7 1.2.2 2.2-.7 2.4-1.9.2-1.3-.6-2.5-1.7-2.7z"/></svg>
        </span>
        <div>
          <h2>宠物管理</h2>
          <p>科学管理每一只宠物，让它们拥有更好的生活</p>
        </div>
      </div>

    </div>

    <!-- 统计卡 -->
    <div class="stat-grid">
      <div class="stat-card" v-for="c in statCards" :key="c.key">
        <div class="s-icon" :style="{ background: c.bg, color: c.color }" v-html="c.icon"></div>
        <div class="s-body">
          <div class="s-label">{{ c.label }}</div>
          <div class="s-value">
            {{ fmtNum(c.value) }}
            <span class="s-up" v-if="c.growth !== null">{{ c.growth >= 0 ? '↑' : '↓' }} {{ Math.abs(c.growth).toFixed(1) }}%</span>
          </div>
          <div class="s-cmp">较上月</div>
        </div>
      </div>
    </div>

    <div class="main-layout">
      <!-- 左侧主区域 -->
      <div class="main-col">
        <!-- 筛选栏 -->
        <div class="filter-bar">
          <el-input
            v-model="filter.keyword"
            placeholder="请输入宠物名称、品种或ID"
            clearable
            class="f-search"
            @keyup.enter="loadList(1)"
          >
            <template #prefix>
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" style="width:15px;height:15px"><circle cx="11" cy="11" r="7"/><path d="M21 21l-4.3-4.3"/></svg>
            </template>
          </el-input>
          <div class="f-item">
            <span class="f-label">宠物类型：</span>
            <el-select v-model="filter.petType" placeholder="全部" clearable style="width:110px">
              <el-option label="犬类" value="犬" />
              <el-option label="猫类" value="猫" />
            </el-select>
          </div>
          <div class="f-item">
            <span class="f-label">状态：</span>
            <el-select v-model="filter.healthStatus" placeholder="全部" clearable style="width:110px">
              <el-option label="良好" value="良好" />
              <el-option label="轻微异常" value="轻微异常" />
              <el-option label="异常" value="异常" />
            </el-select>
          </div>
          <div class="f-item">
            <span class="f-label">品种：</span>
            <el-select v-model="filter.breed" placeholder="全部" clearable style="width:130px">
              <el-option label="金毛寻回犬" value="金毛寻回犬" />
              <el-option label="布偶猫" value="布偶猫" />
              <el-option label="柯基犬" value="柯基犬" />
              <el-option label="英短" value="英短" />
              <el-option label="萨摩耶" value="萨摩耶" />
              <el-option label="橘猫" value="橘猫" />
            </el-select>
          </div>
          <el-button type="primary" class="f-add" @click="openDialog()">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.4" stroke-linecap="round" style="width:14px;height:14px"><path d="M12 5v14M5 12h14"/></svg>
            添加宠物
          </el-button>
          <el-button class="f-batch" @click="batchAction">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" style="width:14px;height:14px"><rect x="3" y="3" width="7" height="7" rx="1"/><rect x="14" y="3" width="7" height="7" rx="1"/><rect x="3" y="14" width="7" height="7" rx="1"/><rect x="14" y="14" width="7" height="7" rx="1"/></svg>
            批量操作
          </el-button>
        </div>

        <!-- 表格 -->
        <div class="table-card">
          <el-table :data="list" v-loading="loading" @selection-change="sel => selected = sel">
            <el-table-column type="selection" width="42" />
            <el-table-column label="宠物信息" min-width="170">
              <template #default="{ row }">
                <div class="p-info">
                  <el-avatar :size="36" :src="row.photoUrl" shape="square" class="p-avatar">
                    {{ (row.petName || 'P').charAt(0) }}
                  </el-avatar>
                  <div>
                    <div class="p-name">
                      {{ row.petName }}
                      <span class="p-type-tag" :class="row.petType === '猫' ? 'cat' : 'dog'">{{ row.petType === '猫' ? '猫' : '犬' }}</span>
                    </div>
                    <div class="p-code">ID: {{ row.petCode }}</div>
                  </div>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="品种" prop="breed" min-width="110" />
            <el-table-column label="年龄" prop="age" width="100" />
            <el-table-column label="性别" width="80">
              <template #default="{ row }">
                <span class="gender" :class="row.gender === '公' ? 'male' : 'female'">
                  {{ row.gender === '公' ? '♂ 雄性' : row.gender === '母' ? '♀ 雌性' : '—' }}
                </span>
              </template>
            </el-table-column>
            <el-table-column label="状态" width="90">
              <template #default="{ row }">
                <el-tag size="small" class="tag-health">健康</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="健康状况" width="100">
              <template #default="{ row }">
                <el-tag size="small" :type="healthTag(row.healthStatus)">{{ row.healthStatus || '良好' }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="绑定用户" prop="bindUserName" width="110" />
            <el-table-column label="操作" width="140" fixed="right">
              <template #default="{ row }">
                <a class="op-link" @click="openDetail(row)">查看</a>
                <a class="op-link" @click="openDialog(row)">编辑</a>
                <a class="op-link danger" @click="onDelete(row)">删除</a>
              </template>
            </el-table-column>
            <template #empty>
              <el-empty description="暂无宠物" />
            </template>
          </el-table>
          <div class="pager-row">
            <span class="pager-text">共 {{ fmtNum(total) }} 条记录，当前第 {{ page }} / {{ Math.max(Math.ceil(total / size), 1) }} 页</span>
            <el-pagination
              background
              layout="prev, pager, next"
              :current-page="page"
              :page-size="size"
              :total="total"
              @current-change="loadList"
            />
          </div>
        </div>
      </div>

      <!-- 右侧详情面板 -->
      <div class="side-col" v-if="currentPet">
        <!-- 宠物卡片 -->
        <div class="side-card">
          <div class="pet-card-head">
            <img class="pet-photo" :src="currentPet.photoUrl" :alt="currentPet.petName" />
            <div class="pet-head-info">
              <div class="pet-name-row">
                {{ currentPet.petName }}
                <span class="p-type-tag" :class="currentPet.petType === '猫' ? 'cat' : 'dog'">{{ currentPet.petType === '猫' ? '猫' : '犬' }}</span>
                <a class="edit-link" @click="openDialog(currentPet)">编辑</a>
              </div>
              <div class="pet-code">ID: {{ currentPet.petCode }}</div>
              <el-tag size="small" :type="healthTag(currentPet.healthStatus)" class="health-tag">{{ currentPet.healthStatus || '良好' }}</el-tag>
            </div>
          </div>
          <ul class="info-list">
            <li><span class="i-label">品种</span><span class="i-val">{{ currentPet.breed }}</span></li>
            <li><span class="i-label">年龄</span><span class="i-val">{{ currentPet.age }}</span></li>
            <li><span class="i-label">性别</span><span class="i-val">{{ currentPet.gender === '公' ? '雄性' : currentPet.gender === '母' ? '雌性' : '—' }}</span></li>
            <li><span class="i-label">体重</span><span class="i-val">{{ currentPet.weight || '—' }}</span></li>
            <li><span class="i-label">疫苗</span><span class="i-val vaccine">{{ currentPet.vaccineStatus || '—' }}</span></li>
            <li><span class="i-label">绝育</span><span class="i-val">{{ currentPet.sterilized || '—' }}</span></li>
            <li><span class="i-label">绑定用户</span><span class="i-val link" @click="goUser(currentPet.userId)">{{ currentPet.bindUserName }} (ID: U{{ String(currentPet.userId).padStart(4,'0') }})</span></li>
          </ul>
        </div>

        <!-- 健康记录 -->
        <div class="side-card">
          <div class="card-head">
            <h3>健康记录</h3>
            <a class="more-link" @click="openHealthDialog">+ 添加</a>
          </div>
          <ul class="record-list">
            <li v-for="r in healthRecords" :key="r.id">
              <span class="r-icon" :class="'type-' + r.type">
                <svg v-if="r.type === '疫苗'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><path d="M12 2l2 4h-4l2-4zM5 8h14l-1.5 12h-11L5 8z"/></svg>
                <svg v-else-if="r.type === '驱虫'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><circle cx="12" cy="12" r="9"/><path d="M12 3v18M3 12h18"/></svg>
                <svg v-else-if="r.type === '就诊'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><path d="M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5"/></svg>
                <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><path d="M20 12h-4l-2 6-4-12-2 6H4"/></svg>
              </span>
              <div class="r-body">
                <div class="r-title">{{ r.title }}</div>
                <div class="r-date">{{ r.recordDate }}</div>
              </div>
              <div class="r-desc">{{ r.description || '—' }}</div>
              <a class="r-del" @click="onDeleteHealthRecord(r.id)">删除</a>
            </li>
            <li v-if="!healthRecords.length" class="empty-record">暂无健康记录</li>
          </ul>
        </div>

        <!-- 最近动态 -->
        <div class="side-card">
          <div class="card-head">
            <h3>最近动态</h3>
            <a class="more-link" @click="goActivity">查看更多 ></a>
          </div>
          <ul class="feed-list">
            <li v-for="(f, i) in recentFeed" :key="i">
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
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="editForm.id ? '编辑宠物' : '添加宠物'" width="520px">
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="宠物名">
          <el-input v-model="editForm.petName" placeholder="请输入宠物名称" />
        </el-form-item>
        <el-form-item v-if="!editForm.id" label="绑定用户">
          <el-select v-model="editForm.userId" placeholder="请选择绑定用户" filterable style="width:100%">
            <el-option v-for="u in userOptions" :key="u.value" :label="u.label" :value="u.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="类型">
          <el-radio-group v-model="editForm.petType">
            <el-radio value="犬">犬</el-radio>
            <el-radio value="猫">猫</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="品种">
          <el-input v-model="editForm.breed" placeholder="如：金毛寻回犬" />
        </el-form-item>
        <el-form-item label="年龄">
          <el-input v-model="editForm.age" placeholder="如：2岁3个月" />
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="editForm.gender">
            <el-radio value="公">雄性</el-radio>
            <el-radio value="母">雌性</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="体重">
          <el-input v-model="editForm.weight" placeholder="如：32.5kg" />
        </el-form-item>
        <el-form-item label="疫苗">
          <el-input v-model="editForm.vaccineStatus" placeholder="如：已接种(有效期至 2026-12-01)" />
        </el-form-item>
        <el-form-item label="绝育">
          <el-radio-group v-model="editForm.sterilized">
            <el-radio value="是">是</el-radio>
            <el-radio value="否">否</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="健康状况">
          <el-select v-model="editForm.healthStatus" style="width:100%">
            <el-option label="良好" value="良好" />
            <el-option label="轻微异常" value="轻微异常" />
            <el-option label="异常" value="异常" />
          </el-select>
        </el-form-item>
        <el-form-item label="照片URL">
          <el-input v-model="editForm.photoUrl" placeholder="请输入照片地址" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>

    <!-- 健康记录弹窗 -->
    <el-dialog v-model="healthDialogVisible" title="添加健康记录" width="440px">
      <el-form :model="healthForm" label-width="80px">
        <el-form-item label="类型">
          <el-select v-model="healthForm.type" style="width:100%">
            <el-option label="体检" value="体检" />
            <el-option label="疫苗" value="疫苗" />
            <el-option label="驱虫" value="驱虫" />
            <el-option label="就诊" value="就诊" />
          </el-select>
        </el-form-item>
        <el-form-item label="标题">
          <el-input v-model="healthForm.title" placeholder="如：年度体检 / 狂犬疫苗" />
        </el-form-item>
        <el-form-item label="发生日期">
          <el-date-picker v-model="healthForm.recordDate" type="date" value-format="YYYY-MM-DD" style="width:100%" />
        </el-form-item>
        <el-form-item label="结果描述">
          <el-input v-model="healthForm.description" type="textarea" :rows="2" placeholder="如：体检正常 / 皮肤过敏" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="healthDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="healthSaving" @click="submitHealthRecord">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import {
  getPetStats, pagePets, getPetDetail, savePet, updatePet,
  deletePet, batchDeletePet, getPetHealthRecords, addPetHealthRecord,
  deletePetHealthRecord, getPetActivities, pageUsers
} from '@/api/admin'

const router = useRouter()

const heroImg = 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=' +
  encodeURIComponent('golden retriever dog and tabby cat sitting together on soft blue background, professional pet photography, warm friendly') +
  '&image_size=landscape_16_9'

/* ===== 图标 ===== */
const ICON = {
  paw: '<svg viewBox="0 0 24 24" fill="currentColor"><path d="M12 13.5c-2.9 0-5.5 2.3-5.5 4.6 0 1.5 1.1 2.4 2.6 2.4 1.1 0 1.9-.5 2.9-.5s1.8.5 2.9.5c1.5 0 2.6-.9 2.6-2.4 0-2.3-2.6-4.6-5.5-4.6zM6.4 8.2c-1.2.3-2 1.6-1.8 2.9.2 1.2 1.3 2 2.5 1.8 1.2-.3 2-1.6 1.8-2.9-.2-1.2-1.3-2-2.5-1.8zm11.2 0c-1.2-.2-2.3.6-2.5 1.8-.2 1.3.6 2.6 1.8 2.9 1.2.2 2.3-.6 2.5-1.8.2-1.3-.6-2.6-1.8-2.9zM9.3 3.6c-1.1.2-1.9 1.4-1.7 2.7.2 1.2 1.2 2.1 2.4 1.9 1.1-.2 1.9-1.4 1.7-2.7-.2-1.2-1.3-2.1-2.4-1.9zm5.4 0c-1.1-.2-2.2.7-2.4 1.9-.2 1.3.6 2.5 1.7 2.7 1.2.2 2.2-.7 2.4-1.9.2-1.3-.6-2.5-1.7-2.7z"/></svg>',
  dog: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><circle cx="12" cy="12" r="9"/><path d="M8 12s1.5-2 4-2 4 2 4 2M9 9h.01M15 9h.01M9 16h6"/></svg>',
  cat: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><path d="M12 3L4 7v10l8 4 8-4V7l-8-4z"/><path d="M12 12l4-2M12 12l-4-2M12 12v6"/></svg>',
  alert: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><path d="M12 9v4M12 17h.01M10.3 3.86L1.82 18a2 2 0 001.71 3h16.94a2 2 0 001.71-3L13.7 3.86a2 2 0 00-3.4 0z"/></svg>'
}

/* ===== 统计卡 ===== */
const stats = ref({
  total: 8732, dogCount: 5216, catCount: 3421, abnormal: 95,
  totalGrowth: 8.7, dogGrowth: 6.3, catGrowth: 12.5, abnormalGrowth: 15.2
})

const statCards = ref(buildStatCards(stats.value))

function buildStatCards(s) {
  return [
    { key: 'total', label: '宠物总数', value: s.total, growth: s.totalGrowth ?? 0, icon: ICON.paw, bg: '#e8f1ff', color: '#3b7cff' },
    { key: 'dog', label: '犬类数量', value: s.dogCount, growth: s.dogGrowth ?? 0, icon: ICON.dog, bg: '#fff4e5', color: '#f5a623' },
    { key: 'cat', label: '猫类数量', value: s.catCount, growth: s.catGrowth ?? 0, icon: ICON.cat, bg: '#f1ebff', color: '#8b5cf6' },
    { key: 'abnormal', label: '健康异常', value: s.abnormal, growth: s.abnormalGrowth ?? 0, icon: ICON.alert, bg: '#ffeef5', color: '#f76c9b' }
  ]
}

/* ===== 列表 ===== */
const list = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const loading = ref(false)
const selected = ref([])
const filter = reactive({ keyword: '', petType: '', healthStatus: '', breed: '' })

function fmtNum(n) { return Number(n || 0).toLocaleString('en-US') }
function healthTag(s) {
  return { '良好': 'success', '轻微异常': 'warning', '异常': 'danger' }[s] || 'success'
}

async function loadList(p = 1) {
  page.value = p
  loading.value = true
  try {
    const res = await pagePets({
      page: page.value,
      size: size.value,
      keyword: filter.keyword || undefined,
      petType: filter.petType || undefined,
      healthStatus: filter.healthStatus || undefined,
      breed: filter.breed || undefined
    })
    list.value = res.data.list || []
    total.value = res.data.total || 0
    if (list.value.length && !currentPet.value) {
      selectPet(list.value[0])
    }
  } catch (e) {} finally { loading.value = false }
}

async function loadStats() {
  try {
    const res = await getPetStats()
    stats.value = { ...stats.value, ...(res.data || {}) }
    statCards.value = buildStatCards(stats.value)
  } catch (e) {}
}

/* ===== 右侧详情面板 ===== */
const currentPet = ref(null)
const healthRecords = ref([])
const recentFeed = ref([
  { title: '豆豆', sub: '完成了疫苗接种', time: '2025-03-12 10:24', icon: ICON.paw, bg: '#e8f1ff', color: '#3b7cff' },
  { title: '小橘子', sub: '发布了新的动态：豆豆今天很开心！', time: '2025-03-10 16:32', icon: ICON.paw, bg: '#e6f9f0', color: '#12b886' }
])

async function selectPet(pet) {
  currentPet.value = pet
  try {
    const [hRes, aRes] = await Promise.all([
      getPetHealthRecords(pet.id),
      getPetActivities(pet.id)
    ])
    healthRecords.value = hRes.data || []
    recentFeed.value = (aRes.data || []).map(a => ({
      title: a.title,
      sub: a.sub,
      time: String(a.time || '').substring(0, 16),
      icon: ICON.paw,
      bg: a.type === '疫苗' ? '#e8f1ff' : a.type === '驱虫' ? '#e6f9f0' : a.type === '就诊' ? '#ffeef5' : '#fff4e5',
      color: a.type === '疫苗' ? '#3b7cff' : a.type === '驱虫' ? '#12b886' : a.type === '就诊' ? '#f76c9b' : '#f5a623'
    }))
  } catch (e) { healthRecords.value = [] }
}

function openDetail(row) {
  selectPet(row)
}

/* ===== 弹窗 ===== */
const dialogVisible = ref(false)
const saving = ref(false)
const userOptions = ref([])
const editForm = reactive({
  id: null, petName: '', petType: '犬', breed: '', age: '', gender: '公',
  weight: '', vaccineStatus: '', sterilized: '否', healthStatus: '良好', photoUrl: '',
  userId: null
})

async function loadUserOptions() {
  if (userOptions.value.length) return
  try {
    const res = await pageUsers({ page: 1, size: 100, status: 1 })
    userOptions.value = (res.data.list || []).map(u => ({
      value: u.id,
      label: `${u.nickname || u.account} (ID:${u.id})`
    }))
  } catch (e) {}
}

function openDialog(row) {
  loadUserOptions()
  if (row) {
    Object.assign(editForm, {
      id: row.id, petName: row.petName, petType: row.petType, breed: row.breed,
      age: row.age, gender: row.gender, weight: row.weight || '',
      vaccineStatus: row.vaccineStatus || '', sterilized: row.sterilized || '否',
      healthStatus: row.healthStatus || '良好', photoUrl: row.photoUrl || '',
      userId: row.userId
    })
  } else {
    Object.assign(editForm, {
      id: null, petName: '', petType: '犬', breed: '', age: '', gender: '公',
      weight: '', vaccineStatus: '', sterilized: '否', healthStatus: '良好', photoUrl: '',
      userId: null
    })
  }
  dialogVisible.value = true
}

async function submitForm() {
  if (!editForm.petName) { ElMessage.warning('请输入宠物名称'); return }
  if (!editForm.id && !editForm.userId) { ElMessage.warning('请选择绑定用户'); return }
  saving.value = true
  try {
    if (editForm.id) {
      await updatePet(editForm.id, editForm)
    } else {
      await savePet(editForm)
    }
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadList(page.value)
    if (currentPet.value?.id === editForm.id) selectPet(editForm)
  } catch (e) {} finally { saving.value = false }
}

/* ===== 健康记录弹窗 ===== */
const healthDialogVisible = ref(false)
const healthSaving = ref(false)
const healthForm = reactive({
  petId: null, type: '体检', title: '', description: '', recordDate: ''
})

function openHealthDialog() {
  if (!currentPet.value) {
    ElMessage.warning('请先选择宠物')
    return
  }
  Object.assign(healthForm, { petId: currentPet.value.id, type: '体检', title: '', description: '', recordDate: '' })
  healthDialogVisible.value = true
}

async function submitHealthRecord() {
  if (!healthForm.title) { ElMessage.warning('请输入记录标题'); return }
  if (!healthForm.recordDate) { ElMessage.warning('请选择发生日期'); return }
  healthSaving.value = true
  try {
    await addPetHealthRecord(healthForm.petId, healthForm)
    ElMessage.success('添加成功')
    healthDialogVisible.value = false
    selectPet(currentPet.value)
  } catch (e) {} finally { healthSaving.value = false }
}

async function onDeleteHealthRecord(recordId) {
  try {
    await ElMessageBox.confirm('确认删除这条健康记录吗？', '提示', { type: 'warning' })
  } catch { return }
  await deletePetHealthRecord(recordId)
  ElMessage.success('删除成功')
  selectPet(currentPet.value)
}

async function onDelete(row) {
  try {
    await ElMessageBox.confirm(`确认删除宠物「${row.petName}」吗？`, '提示', { type: 'warning' })
  } catch { return }
  await deletePet(row.id)
  ElMessage.success('删除成功')
  loadList(page.value)
  if (currentPet.value?.id === row.id) currentPet.value = null
}

async function batchAction() {
  if (!selected.value.length) {
    ElMessage.warning('请先选择宠物')
    return
  }
  try {
    await ElMessageBox.confirm(`确认批量删除选中的 ${selected.value.length} 只宠物吗？`, '提示', { type: 'warning' })
  } catch { return }
  await batchDeletePet(selected.value.map(p => p.id))
  ElMessage.success('批量删除成功')
  loadList(1)
  currentPet.value = null
}

function goUser(userId) {
  router.push(`/admin/user`)
}
function goHealth() {
  ElMessage.info('跳转到健康记录页')
}
function goActivity() {
  ElMessage.info('跳转到动态页')
}

onMounted(() => {
  loadList(1)
  loadStats()
})
</script>

<style scoped>
.admin-pet-page { padding: 4px; }

/* ===== Banner ===== */
.hero-banner {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  background-image:
    linear-gradient(90deg, #e7efff 40%, rgba(231, 239, 255, 0) 75%),
    var(--hero-img),
    linear-gradient(115deg, #e3edff 0%, #eaf2ff 55%, #f0f6ff 100%);
  background-size: 100% 100%, auto 100%, 100% 100%;
  background-position: center, right center, center;
  background-repeat: no-repeat;
  border-radius: 16px;
  padding: 24px 28px;
  margin-bottom: 18px;
  overflow: hidden;
}
.h-left { display: flex; align-items: center; gap: 16px; }
.h-icon {
  width: 56px; height: 56px; border-radius: 15px;
  background: #3b7cff; color: #fff;
  display: flex; align-items: center; justify-content: center; flex-shrink: 0;
  box-shadow: 0 8px 18px rgba(59, 124, 255, .3);
}
.h-icon svg { width: 30px; height: 30px; }
.h-left h2 { margin: 0 0 6px; font-size: 22px; color: #2c3e50; }
.h-left p { margin: 0; font-size: 13px; color: #7a8699; }

/* ===== 统计卡 ===== */
.stat-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
  margin-bottom: 18px;
}
.stat-card {
  background: #fff; border: 1px solid #edf0f8; border-radius: 16px;
  box-shadow: 0 10px 28px rgba(94, 110, 190, .07);
  padding: 18px 20px; display: flex; gap: 14px; align-items: center;
}
.s-icon {
  width: 48px; height: 48px; border-radius: 13px;
  display: flex; align-items: center; justify-content: center; flex-shrink: 0;
}
.s-icon :deep(svg) { width: 24px; height: 24px; }
.s-label { color: #909399; font-size: 13px; margin-bottom: 3px; }
.s-value {
  font-size: 24px; font-weight: 700; color: #2c3e50; line-height: 1.2;
  font-variant-numeric: tabular-nums; display: flex; align-items: baseline; gap: 8px;
}
.s-up { font-size: 12px; color: #67c23a; font-weight: 600; }
.s-cmp { margin-top: 3px; font-size: 12px; color: #b0b6c5; }

/* ===== 主布局 ===== */
.main-layout {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 320px;
  gap: 16px;
  align-items: start;
}

/* ===== 筛选栏 ===== */
.filter-bar {
  background: #fff; border: 1px solid #edf0f8; border-radius: 14px;
  padding: 14px 16px; margin-bottom: 16px;
  display: flex; align-items: center; gap: 14px; flex-wrap: wrap;
}
.f-search { width: 240px; }
.f-item { display: flex; align-items: center; }
.f-label { font-size: 13px; color: #606266; white-space: nowrap; }
.f-add, .f-batch { display: inline-flex; align-items: center; gap: 5px; }

/* ===== 表格 ===== */
.table-card {
  background: #fff; border: 1px solid #edf0f8; border-radius: 14px;
  padding: 8px 16px 16px;
}
.table-card :deep(.el-table__header th) {
  background: #f7f9fd; color: #7a8699; font-weight: 600;
}
.p-info { display: flex; align-items: center; gap: 10px; }
.p-avatar { border-radius: 8px; }
.p-name { font-size: 13px; color: #2c3e50; font-weight: 500; }
.p-code { font-size: 12px; color: #a0a8bb; margin-top: 2px; }
.p-type-tag {
  display: inline-block; font-size: 11px; padding: 1px 6px; border-radius: 4px; margin-left: 6px;
}
.p-type-tag.dog { background: #fff4e5; color: #e6a23c; }
.p-type-tag.cat { background: #f1ebff; color: #8b5cf6; }
.gender.male { color: #3b7cff; }
.gender.female { color: #f76c9b; }
.tag-health { background: #e6f9f0; border-color: #d0f0e0; color: #12b886; }
.op-link { color: #3b7cff; font-size: 13px; cursor: pointer; margin-right: 12px; }
.op-link:hover { text-decoration: underline; }
.op-link.danger { color: #f56c6c; }
.pager-row {
  display: flex; align-items: center; justify-content: space-between;
  margin-top: 14px; flex-wrap: wrap; gap: 10px;
}
.pager-text { font-size: 13px; color: #909399; }

/* ===== 右侧面板 ===== */
.side-card {
  background: #fff; border: 1px solid #edf0f8; border-radius: 14px;
  padding: 16px; margin-bottom: 16px;
}
.pet-card-head {
  display: flex; gap: 12px; margin-bottom: 14px;
}
.pet-photo {
  width: 80px; height: 80px; object-fit: cover; border-radius: 12px; flex-shrink: 0;
}
.pet-head-info { flex: 1; }
.pet-name-row {
  font-size: 16px; font-weight: 600; color: #2c3e50;
  display: flex; align-items: center; gap: 6px;
}
.edit-link { font-size: 12px; color: #3b7cff; cursor: pointer; margin-left: auto; font-weight: 400; }
.edit-link:hover { text-decoration: underline; }
.pet-code { font-size: 12px; color: #a0a8bb; margin: 4px 0 8px; }
.info-list {
  list-style: none; margin: 0; padding: 0;
}
.info-list li {
  display: flex; justify-content: space-between; padding: 8px 0; font-size: 13px;
  border-bottom: 1px dashed #eef1f8;
}
.info-list li:last-child { border-bottom: none; }
.i-label { color: #909399; }
.i-val { color: #2c3e50; font-weight: 500; }
.i-val.vaccine { color: #12b886; font-size: 12px; }
.i-val.link { color: #3b7cff; cursor: pointer; }
.i-val.link:hover { text-decoration: underline; }
.card-head {
  display: flex; align-items: center; justify-content: space-between; margin-bottom: 12px;
}
.card-head h3 { margin: 0; font-size: 15px; color: #2c3e50; font-weight: 600; }
.more-link { font-size: 12px; color: #3b7cff; cursor: pointer; }
.more-link:hover { text-decoration: underline; }
.record-list { list-style: none; margin: 0; padding: 0; }
.record-list li {
  display: flex; align-items: center; gap: 10px; padding: 9px 0;
}
.record-list li + li { border-top: 1px dashed #eef1f8; }
.r-icon {
  width: 34px; height: 34px; border-radius: 10px;
  display: flex; align-items: center; justify-content: center; flex-shrink: 0;
}
.r-icon svg { width: 17px; height: 17px; }
.r-icon.type-疫苗 { background: #e8f1ff; color: #3b7cff; }
.r-icon.type-驱虫 { background: #e6f9f0; color: #12b886; }
.r-icon.type-就诊 { background: #ffeef5; color: #f76c9b; }
.r-icon { background: #fff4e5; color: #f5a623; }
.r-body { flex: 1; min-width: 0; }
.r-title { font-size: 13px; color: #2c3e50; font-weight: 500; }
.r-date { font-size: 12px; color: #a0a8bb; }
.r-desc { font-size: 12px; color: #7a8699; }
.r-del { font-size: 12px; color: #f56c6c; cursor: pointer; flex-shrink: 0; }
.r-del:hover { text-decoration: underline; }
.empty-record { font-size: 13px; color: #b0b6c5; text-align: center; padding: 16px; }
.feed-list { list-style: none; margin: 0; padding: 0; }
.feed-list li {
  display: flex; align-items: center; gap: 10px; padding: 9px 0;
}
.feed-list li + li { border-top: 1px dashed #eef1f8; }
.f-icon {
  width: 34px; height: 34px; border-radius: 10px;
  display: flex; align-items: center; justify-content: center; flex-shrink: 0;
}
.f-icon :deep(svg) { width: 17px; height: 17px; }
.f-body { flex: 1; min-width: 0; }
.f-title { font-size: 13px; color: #2c3e50; font-weight: 500; }
.f-sub {
  font-size: 12px; color: #a0a8bb;
  white-space: nowrap; overflow: hidden; text-overflow: ellipsis;
}
.f-time { font-size: 12px; color: #b0b6c5; flex-shrink: 0; }

/* 响应式 */
@media (max-width: 1400px) {
  .main-layout { grid-template-columns: 1fr; }
  .side-col { display: none; }
}
@media (max-width: 760px) {
  .hero-banner { flex-direction: column; align-items: flex-start; }
  .stat-grid { grid-template-columns: 1fr; }
}
</style>
