<template>
  <div class="admin-user-page">
    <!-- 顶部 Banner -->
    <div class="hero-banner" :style="{ '--hero-img': `url(${heroImg})` }">
      <div class="h-left">
        <span class="h-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><circle cx="9" cy="8" r="3.5"/><path d="M2.5 19c0-3.3 2.9-5.5 6.5-5.5s6.5 2.2 6.5 5.5"/><circle cx="17" cy="9" r="2.6"/><path d="M15.5 14.2c3.4.3 6 2.3 6 4.8"/></svg>
        </span>
        <div>
          <h2>用户管理</h2>
          <p>管理平台用户信息，查看用户行为数据，保障平台安全运营</p>
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
          <div class="s-cmp">{{ c.cmp }}</div>
        </div>
      </div>
    </div>

    <!-- 筛选栏 -->
    <div class="filter-bar">
      <el-input
        v-model="filter.keyword"
        placeholder="请输入用户名/手机号/ID"
        clearable
        class="f-search"
        @keyup.enter="loadList(1)"
      >
        <template #prefix>
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" style="width:15px;height:15px"><circle cx="11" cy="11" r="7"/><path d="M21 21l-4.3-4.3"/></svg>
        </template>
      </el-input>
      <div class="f-item">
        <span class="f-label">用户角色：</span>
        <el-select v-model="filter.role" placeholder="全部" clearable style="width:130px">
          <el-option label="普通用户" value="user" />
          <el-option label="VIP用户" value="vip" />
        </el-select>
      </div>
      <div class="f-item">
        <span class="f-label">注册时间：</span>
        <el-date-picker
          v-model="filter.date"
          type="date"
          placeholder="请选择注册时间"
          value-format="YYYY-MM-DD"
          style="width:170px"
        />
      </div>
      <div class="f-item">
        <span class="f-label">状态：</span>
        <el-select v-model="filter.status" placeholder="全部" clearable style="width:110px">
          <el-option label="正常" :value="1" />
          <el-option label="禁用" :value="0" />
        </el-select>
      </div>
      <el-button type="primary" class="f-query" @click="loadList(1)">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" style="width:14px;height:14px"><circle cx="11" cy="11" r="7"/><path d="M21 21l-4.3-4.3"/></svg>
        查询
      </el-button>
      <el-button class="f-reset" @click="resetFilter">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" style="width:14px;height:14px"><path d="M21 12a9 9 0 1 1-2.64-6.36M21 3v6h-6"/></svg>
        重置
      </el-button>
    </div>

    <!-- 用户表格 -->
    <div class="table-card">
      <el-table :data="list" v-loading="loading" @selection-change="sel => selected = sel">
        <el-table-column type="selection" width="42" />
        <el-table-column label="用户信息" min-width="170">
          <template #default="{ row }">
            <div class="u-info">
              <el-avatar :size="36" :src="row.avatar">
                {{ (row.nickname || row.account || 'U').charAt(0).toUpperCase() }}
              </el-avatar>
              <span class="u-nick">{{ row.nickname || '—' }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="用户名" prop="account" min-width="110" show-overflow-tooltip />
        <el-table-column label="手机号" width="130">
          <template #default="{ row }">
            <span v-if="row.phone">{{ maskPhone(row.phone) }}</span>
            <span v-else class="text-muted">—</span>
          </template>
        </el-table-column>
        <el-table-column label="角色" width="110">
          <template #default="{ row }">
            <el-tag v-if="row.role === 'vip'" size="small" class="tag-vip">VIP用户</el-tag>
            <el-tag v-else size="small" class="tag-user">普通用户</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="注册时间" width="150">
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <a class="op-link" @click="openView(row)">查看</a>
            <a class="op-link" @click="openEdit(row)">编辑</a>
            <a class="op-link danger" @click="onDelete(row)">删除</a>
          </template>
        </el-table-column>
        <template #empty>
          <el-empty description="暂无用户" />
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

    <!-- 查看弹窗 -->
    <el-dialog v-model="viewVisible" title="用户详情" width="480px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="头像">
          <el-avatar :size="44" :src="viewData.avatar">
            {{ (viewData.nickname || viewData.account || 'U').charAt(0).toUpperCase() }}
          </el-avatar>
        </el-descriptions-item>
        <el-descriptions-item label="用户ID">{{ viewData.id }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ viewData.account }}</el-descriptions-item>
        <el-descriptions-item label="昵称">{{ viewData.nickname || '—' }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ viewData.phone ? maskPhone(viewData.phone) : '—' }}</el-descriptions-item>
        <el-descriptions-item label="角色">
          <el-tag v-if="viewData.role === 'vip'" size="small" class="tag-vip">VIP用户</el-tag>
          <el-tag v-else size="small" class="tag-user">普通用户</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="viewData.status === 1 ? 'success' : 'danger'" size="small">
            {{ viewData.status === 1 ? '正常' : '禁用' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="注册时间">{{ formatTime(viewData.createTime) }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="viewVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 编辑弹窗 -->
    <el-dialog v-model="editVisible" title="编辑用户" width="460px">
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="用户名">
          <el-input :model-value="editForm.account" disabled />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="editForm.nickname" placeholder="请输入昵称" maxlength="50" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="editForm.phone" placeholder="请输入手机号" maxlength="11" />
        </el-form-item>
        <el-form-item label="角色">
          <el-radio-group v-model="editForm.role">
            <el-radio value="user">普通用户</el-radio>
            <el-radio value="vip">VIP用户</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submitEdit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  pageUsers, updateUserStatus, getUserStats,
  getUserDetail, updateUser, deleteUser
} from '@/api/admin'

const heroImg = 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=' +
  encodeURIComponent('golden retriever dog and tabby cat sitting side by side looking at camera, soft light blue studio background, professional pet photography, warm friendly') +
  '&image_size=landscape_4_3'

/* ===== 图标 ===== */
const ICON = {
  users: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><circle cx="9" cy="8" r="3.5"/><path d="M2.5 19c0-3.3 2.9-5.5 6.5-5.5s6.5 2.2 6.5 5.5"/><circle cx="17" cy="9" r="2.6"/><path d="M15.5 14.2c3.4.3 6 2.3 6 4.8"/></svg>',
  active: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 20s-7.5-4.6-9.5-9C1 7.5 3.5 4 7 4c2.1 0 3.7 1.2 5 3 1.3-1.8 2.9-3 5-3 3.5 0 6 3.5 4.5 7-2 4.4-9.5 9-9.5 9z"/></svg>',
  today: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><circle cx="12" cy="8" r="4"/><path d="M4 20c0-4 3.6-6.5 8-6.5s8 2.5 8 6.5"/><path d="M17 5l1-2M19 8l2-1" /></svg>',
  month: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><rect x="3" y="5" width="18" height="16" rx="2"/><path d="M8 3v4M16 3v4M3 10h18"/></svg>'
}

/* ===== 统计卡（演示兜底） ===== */
const stats = ref({
  totalCount: 12846, activeCount: 8732, todayNewCount: 156, monthNewCount: 2482,
  totalGrowth: 12.5, activeGrowth: 8.7, todayGrowth: 21.3, monthGrowth: 15.6
})

const statCards = ref(buildStatCards(stats.value))

function buildStatCards(s) {
  return [
    { key: 'total', label: '总用户数', value: s.totalCount, growth: s.totalGrowth ?? 0, cmp: '较上月', icon: ICON.users, bg: '#e8f1ff', color: '#3b7cff' },
    { key: 'active', label: '活跃用户数', value: s.activeCount, growth: s.activeGrowth ?? 0, cmp: '较上月', icon: ICON.active, bg: '#e6f9f0', color: '#12b886' },
    { key: 'today', label: '今日新增用户', value: s.todayNewCount, growth: s.todayGrowth ?? 0, cmp: '较昨日', icon: ICON.today, bg: '#f1ebff', color: '#8b5cf6' },
    { key: 'month', label: '本月新增用户', value: s.monthNewCount, growth: s.monthGrowth ?? 0, cmp: '较上月', icon: ICON.month, bg: '#ffeef5', color: '#f76c9b' }
  ]
}

/* ===== 列表 ===== */
const list = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const loading = ref(false)
const selected = ref([])
const filter = reactive({ keyword: '', role: '', date: '', status: null })

function formatTime(t) {
  if (!t) return ''
  return String(t).replace('T', ' ').substring(0, 16)
}
function maskPhone(p) {
  const s = String(p || '')
  return s.length === 11 ? s.substring(0, 3) + '****' + s.substring(7) : s
}
function fmtNum(n) {
  return Number(n || 0).toLocaleString('en-US')
}

async function loadList(p = 1) {
  page.value = p
  loading.value = true
  try {
    const res = await pageUsers({
      page: page.value,
      size: size.value,
      keyword: filter.keyword || undefined,
      role: filter.role || undefined,
      date: filter.date || undefined,
      status: filter.status
    })
    list.value = res.data.list || []
    total.value = res.data.total || 0
  } catch (e) {
    /* 接口失败保留空表 */
  } finally {
    loading.value = false
  }
}

async function loadStats() {
  try {
    const res = await getUserStats()
    stats.value = { ...stats.value, ...(res.data || {}) }
    statCards.value = buildStatCards(stats.value)
  } catch (e) {
    /* 保留演示数据 */
  }
}

function resetFilter() {
  filter.keyword = ''
  filter.role = ''
  filter.date = ''
  filter.status = null
  loadList(1)
}

/* ===== 查看 ===== */
const viewVisible = ref(false)
const viewData = ref({})
async function openView(row) {
  viewData.value = row
  viewVisible.value = true
  try {
    const res = await getUserDetail(row.id)
    viewData.value = res.data || row
  } catch (e) {}
}

/* ===== 编辑 ===== */
const editVisible = ref(false)
const saving = ref(false)
const editForm = reactive({ id: null, account: '', nickname: '', phone: '', role: 'user' })

function openEdit(row) {
  editForm.id = row.id
  editForm.account = row.account
  editForm.nickname = row.nickname || ''
  editForm.phone = row.phone || ''
  editForm.role = row.role || 'user'
  editVisible.value = true
}

async function submitEdit() {
  if (editForm.phone && !/^1\d{10}$/.test(editForm.phone)) {
    ElMessage.warning('请输入正确的 11 位手机号')
    return
  }
  saving.value = true
  try {
    await updateUser(editForm.id, {
      nickname: editForm.nickname,
      phone: editForm.phone,
      role: editForm.role
    })
    ElMessage.success('保存成功')
    editVisible.value = false
    loadList(page.value)
  } catch (e) {} finally {
    saving.value = false
  }
}

/* ===== 删除 ===== */
async function onDelete(row) {
  try {
    await ElMessageBox.confirm(`确认删除用户「${row.nickname || row.account}」吗？删除后不可恢复。`, '提示', {
      type: 'warning',
      confirmButtonText: '删除',
      confirmButtonClass: 'el-button--danger'
    })
  } catch {
    return
  }
  await deleteUser(row.id)
  ElMessage.success('删除成功')
  loadList(page.value)
}

onMounted(() => {
  loadList(1)
  loadStats()
})
</script>

<style scoped>
.admin-user-page {
  padding: 4px;
}

/* ===== 顶部 Banner ===== */
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
.h-left {
  display: flex;
  align-items: center;
  gap: 16px;
}
.h-icon {
  width: 56px;
  height: 56px;
  border-radius: 15px;
  background: #3b7cff;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 8px 18px rgba(59, 124, 255, .3);
}
.h-icon svg { width: 30px; height: 30px; }
.h-left h2 {
  margin: 0 0 6px;
  font-size: 22px;
  color: #2c3e50;
}
.h-left p {
  margin: 0;
  font-size: 13px;
  color: #7a8699;
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
  padding: 18px 20px;
  display: flex;
  gap: 14px;
  align-items: center;
}
.s-icon {
  width: 48px;
  height: 48px;
  border-radius: 13px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.s-icon :deep(svg) { width: 24px; height: 24px; }
.s-label {
  color: #909399;
  font-size: 13px;
  margin-bottom: 3px;
}
.s-value {
  font-size: 24px;
  font-weight: 700;
  color: #2c3e50;
  line-height: 1.2;
  font-variant-numeric: tabular-nums;
  display: flex;
  align-items: baseline;
  gap: 8px;
}
.s-up {
  font-size: 12px;
  color: #67c23a;
  font-weight: 600;
}
.s-cmp {
  margin-top: 3px;
  font-size: 12px;
  color: #b0b6c5;
}

/* ===== 筛选栏 ===== */
.filter-bar {
  background: #fff;
  border: 1px solid #edf0f8;
  border-radius: 14px;
  padding: 14px 16px;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 14px;
  flex-wrap: wrap;
}
.f-search {
  width: 240px;
}
.f-item {
  display: flex;
  align-items: center;
}
.f-label {
  font-size: 13px;
  color: #606266;
  white-space: nowrap;
}
.f-query, .f-reset {
  display: inline-flex;
  align-items: center;
  gap: 5px;
}

/* ===== 表格 ===== */
.table-card {
  background: #fff;
  border: 1px solid #edf0f8;
  border-radius: 14px;
  padding: 8px 16px 16px;
}
.table-card :deep(.el-table__header th) {
  background: #f7f9fd;
  color: #7a8699;
  font-weight: 600;
}
.u-info {
  display: flex;
  align-items: center;
  gap: 10px;
}
.u-nick {
  font-size: 13px;
  color: #2c3e50;
  font-weight: 500;
}
.text-muted { color: #c0c4cc; font-size: 13px; }
.tag-user {
  background: #ecf5ff;
  border-color: #d9ecff;
  color: #409eff;
}
.tag-vip {
  background: #fdf6ec;
  border-color: #faecd8;
  color: #e6a23c;
}
.op-link {
  color: #3b7cff;
  font-size: 13px;
  cursor: pointer;
  margin-right: 14px;
}
.op-link:last-child { margin-right: 0; }
.op-link:hover { text-decoration: underline; }
.op-link.danger { color: #f56c6c; }

/* ===== 分页 ===== */
.pager-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 14px;
  flex-wrap: wrap;
  gap: 10px;
}
.pager-text {
  font-size: 13px;
  color: #909399;
}

/* 响应式 */
@media (max-width: 1280px) {
  .stat-grid { grid-template-columns: repeat(2, minmax(0, 1fr)); }
}
@media (max-width: 760px) {
  .hero-banner { flex-direction: column; align-items: flex-start; }
  .stat-grid { grid-template-columns: 1fr; }
}
</style>
