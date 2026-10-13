<template>
  <div class="merchant-page">
    <h2>订单管理</h2>
    <div class="filter">
      <el-select v-model="status" placeholder="状态" clearable @change="loadList" style="width:140px">
        <el-option label="待付款" value="pending" />
        <el-option label="已付款" value="paid" />
        <el-option label="已发货" value="shipped" />
        <el-option label="已完成" value="completed" />
        <el-option label="已取消" value="cancelled" />
      </el-select>
    </div>
    <el-table :data="list" v-loading="loading" stripe>
      <el-table-column prop="orderNo" label="订单号" width="180" />
      <el-table-column prop="totalAmount" label="金额" width="100" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusTag(row.status)">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="receiverName" label="收件人" width="100" />
      <el-table-column prop="receiverPhone" label="电话" width="130" />
      <el-table-column prop="receiverAddress" label="地址" show-overflow-tooltip />
      <el-table-column prop="createTime" label="下单时间" width="170" />
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button size="small" @click="viewDetail(row.id)">明细</el-button>
          <el-button v-if="row.status === 'paid'" size="small" type="primary" @click="ship(row.id)">发货</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="showDetail" title="订单明细" width="600px">
      <div class="order-info" v-if="detail">
        <p><b>订单号：</b>{{ detail.orderNo }}</p>
        <p><b>收件人：</b>{{ detail.receiverName }} {{ detail.receiverPhone }}</p>
        <p><b>地址：</b>{{ detail.receiverAddress }}</p>
      </div>
      <el-table :data="items" style="margin-top:12px">
        <el-table-column prop="productName" label="商品" />
        <el-table-column prop="price" label="单价" width="100" />
        <el-table-column prop="quantity" label="数量" width="80" />
        <el-table-column prop="subtotal" label="小计" width="120" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { pageMerchantOrders, getMerchantOrderItems, shipMerchantOrder } from '@/api/merchant'

const list = ref([])
const status = ref('')
const loading = ref(false)
const showDetail = ref(false)
const detail = ref(null)
const items = ref([])

const statusMap = { pending: '待付款', paid: '已付款', shipped: '已发货', completed: '已完成', cancelled: '已取消' }
function statusText(s) { return statusMap[s] || s }
function statusTag(s) {
  return { pending: 'warning', paid: 'primary', shipped: 'success', completed: 'info', cancelled: 'info' }[s] || ''
}

async function loadList() {
  loading.value = true
  try {
    const res = await pageMerchantOrders({ page: 1, size: 50, status: status.value })
    list.value = res.data.list || []
  } catch (e) {} finally { loading.value = false }
}

async function viewDetail(id) {
  try {
    const [orderRes, itemsRes] = await Promise.all([
      // 详情接口已有权限校验，此处用列表数据即可
      Promise.resolve({ data: list.value.find(o => o.id === id) }),
      getMerchantOrderItems(id)
    ])
    detail.value = orderRes.data
    items.value = itemsRes.data || []
    showDetail.value = true
  } catch (e) {}
}

async function ship(id) {
  try {
    await shipMerchantOrder(id)
    ElMessage.success('发货成功'); loadList()
  } catch (e) {}
}

onMounted(loadList)
</script>

<style scoped>
.merchant-page h2 { margin: 0 0 16px; }
.filter { margin-bottom: 16px; }
.order-info p { margin: 4px 0; color: #606266; }
</style>
