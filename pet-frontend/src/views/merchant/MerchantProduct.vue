<template>
  <div class="merchant-page">
    <div class="page-head">
      <h2>商品管理</h2>
      <el-button type="primary" @click="openDialog()">新增商品</el-button>
    </div>
    <el-table :data="list" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column label="封面" width="80">
        <template #default="{ row }">
          <img v-if="row.coverImage" :src="row.coverImage" style="width:50px;height:50px;object-fit:cover;border-radius:4px" />
        </template>
      </el-table-column>
      <el-table-column prop="name" label="名称" />
      <el-table-column prop="category" label="分类" width="90" />
      <el-table-column prop="price" label="价格" width="100" />
      <el-table-column prop="stock" label="库存" width="80" />
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '上架' : '下架' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button size="small" @click="openDialog(row)">编辑</el-button>
          <el-button size="small" @click="toggleStatus(row)">{{ row.status === 1 ? '下架' : '上架' }}</el-button>
          <el-button size="small" type="danger" @click="remove(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="showDialog" :title="form.id ? '编辑商品' : '新增商品'" width="600px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="分类">
          <el-select v-model="form.category">
            <el-option label="食品" value="食品" /><el-option label="玩具" value="玩具" />
            <el-option label="用品" value="用品" /><el-option label="医药" value="医药" />
          </el-select>
        </el-form-item>
        <el-form-item label="价格"><el-input-number v-model="form.price" :min="0" :precision="2" /></el-form-item>
        <el-form-item label="库存"><el-input-number v-model="form.stock" :min="0" /></el-form-item>
        <el-form-item label="封面图"><el-input v-model="form.coverImage" placeholder="图片URL" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">上架</el-radio><el-radio :label="0">下架</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="submit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  pageMerchantProducts, saveMerchantProduct, updateMerchantProduct,
  deleteMerchantProduct, updateMerchantProductStatus
} from '@/api/merchant'

const list = ref([])
const loading = ref(false)
const showDialog = ref(false)
const form = reactive({ id: null, name: '', category: '', price: 0, stock: 0, coverImage: '', description: '', status: 1 })

async function loadList() {
  loading.value = true
  try {
    const res = await pageMerchantProducts({ page: 1, size: 50 })
    list.value = res.data.list || []
  } catch (e) {} finally { loading.value = false }
}

function openDialog(row) {
  if (row) {
    Object.assign(form, { id: row.id, name: row.name, category: row.category, price: row.price, stock: row.stock, coverImage: row.coverImage, description: row.description, status: row.status })
  } else {
    Object.assign(form, { id: null, name: '', category: '', price: 0, stock: 0, coverImage: '', description: '', status: 1 })
  }
  showDialog.value = true
}

async function submit() {
  if (!form.name) { ElMessage.warning('请输入名称'); return }
  try {
    if (form.id) {
      await updateMerchantProduct(form.id, form)
    } else {
      await saveMerchantProduct(form)
    }
    ElMessage.success('保存成功')
    showDialog.value = false
    loadList()
  } catch (e) {}
}

async function toggleStatus(row) {
  await updateMerchantProductStatus(row.id, row.status === 1 ? 0 : 1)
  ElMessage.success('操作成功'); loadList()
}

async function remove(id) {
  try {
    await ElMessageBox.confirm('确定删除吗？', '提示', { type: 'warning' })
    await deleteMerchantProduct(id)
    ElMessage.success('删除成功'); loadList()
  } catch (e) {}
}

onMounted(loadList)
</script>

<style scoped>
.page-head { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.page-head h2 { margin: 0; }
</style>
