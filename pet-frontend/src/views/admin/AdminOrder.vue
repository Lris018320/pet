<template>
  <div class="admin-page">
    <h2>订单管理</h2>
    <div class="filter">
      <el-select v-model="status" placeholder="状态" clearable @change="loadList(1)" style="width:140px">
        <el-option label="待付款" value="pending" />
        <el-option label="已付款" value="paid" />
        <el-option label="已发货" value="shipped" />
        <el-option label="已完成" value="completed" />
        <el-option label="已取消" value="cancelled" />
      </el-select>
    </div>
    <el-table :data="list" v-loading="loading" stripe>
      <el-table-column prop="orderNo" label="订单号" width="180" />
      <el-table-column prop="userId" label="用户ID" width="80" />
      <el-table-column prop="totalAmount" label="金额" width="100" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusTag(row.status)">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="receiverName" label="收件人" width="100" />
      <el-table-column prop="receiverPhone" label="电话" width="130" />
      <el-table-column prop="createTime" label="下单时间" width="170" />
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="viewDetail(row.id)">详情</el-button>
          <el-button v-if="row.status === 'paid'" size="small" type="primary" @click="ship(row.id)">发货</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pager">
      <el-pagination
        background
        layout="total, prev, pager, next"
        :current-page="page"
        :page-size="size"
        :total="total"
        @current-change="loadList"
      />
    </div>

    <!-- 订单详情弹窗 -->
    <el-dialog v-model="detailVisible" title="订单详情" width="640px">
      <div v-loading="detailLoading">
        <el-descriptions :column="2" border class="detail-desc">
          <el-descriptions-item label="订单号">{{ detail.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="statusTag(detail.status)">{{ statusText(detail.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="总金额">¥{{ detail.totalAmount }}</el-descriptions-item>
          <el-descriptions-item label="下单时间">{{ detail.createTime }}</el-descriptions-item>
          <el-descriptions-item label="收件人">{{ detail.receiverName }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ detail.receiverPhone }}</el-descriptions-item>
          <el-descriptions-item label="收货地址" :span="2">{{ detail.receiverAddress }}</el-descriptions-item>
        </el-descriptions>

        <h4 class="items-title">商品明细</h4>
        <el-table :data="items" size="small" border>
          <el-table-column prop="productName" label="商品" min-width="160" show-overflow-tooltip />
          <el-table-column prop="price" label="单价" width="100" />
          <el-table-column prop="quantity" label="数量" width="70" />
          <el-table-column prop="subtotal" label="小计" width="110" />
        </el-table>
      </div>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { adminPageOrder, adminShipOrder, adminGetOrder, adminGetOrderItems } from '@/api/mall'

const list = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const status = ref('')
const loading = ref(false)

const detailVisible = ref(false)
const detailLoading = ref(false)
const detail = ref({})
const items = ref([])

const statusMap = { pending: '待付款', paid: '已付款', shipped: '已发货', completed: '已完成', cancelled: '已取消' }
function statusText(s) { return statusMap[s] || s }
function statusTag(s) {
  return { pending: 'warning', paid: 'primary', shipped: 'success', completed: 'info', cancelled: 'info' }[s] || ''
}

async function loadList(p = 1) {
  page.value = p
  loading.value = true
  try {
    const res = await adminPageOrder({ page: page.value, size: size.value, status: status.value || undefined })
    list.value = res.data.list || []
    total.value = res.data.total || 0
  } catch (e) {} finally { loading.value = false }
}

async function viewDetail(id) {
  detailVisible.value = true
  detailLoading.value = true
  detail.value = {}
  items.value = []
  try {
    const [d, it] = await Promise.all([adminGetOrder(id), adminGetOrderItems(id)])
    detail.value = d.data || {}
    items.value = it.data || []
  } catch (e) {} finally { detailLoading.value = false }
}

async function ship(id) {
  await adminShipOrder(id)
  ElMessage.success('发货成功'); loadList(page.value)
}

onMounted(() => loadList(1))
</script>

<style scoped>
.admin-page h2 { margin: 0 0 16px; }
.filter { margin-bottom: 16px; }
.pager { margin-top: 16px; text-align: right; }
.detail-desc { margin-bottom: 16px; }
.items-title { margin: 0 0 10px; font-size: 14px; color: #2c3e50; }
</style>
