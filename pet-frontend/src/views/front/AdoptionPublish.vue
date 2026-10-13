<template>
  <div class="publish-page">
    <div class="page-shell">
      <div class="form-card">
        <h2>发布领养寄养信息</h2>
        <el-form :model="form" label-width="100px" @submit.prevent>
          <el-form-item label="类型">
            <el-radio-group v-model="form.type">
              <el-radio label="adoption">领养</el-radio>
              <el-radio label="foster">寄养</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="宠物名称">
            <el-input v-model="form.petName" placeholder="请输入宠物名称" />
          </el-form-item>
          <el-form-item label="品种">
            <el-input v-model="form.breed" placeholder="如：金毛、布偶猫" />
          </el-form-item>
          <el-form-item label="宠物类型">
            <el-select v-model="form.petType" placeholder="请选择">
              <el-option label="狗" value="狗" />
              <el-option label="猫" value="猫" />
            </el-select>
          </el-form-item>
          <el-form-item label="年龄">
            <el-input v-model="form.age" placeholder="如：2岁、8个月" />
          </el-form-item>
          <el-form-item label="性别">
            <el-radio-group v-model="form.gender">
              <el-radio label="公">公</el-radio>
              <el-radio label="母">母</el-radio>
              <el-radio label="未知">未知</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="照片">
            <el-input v-model="form.photoUrl" placeholder="照片URL（可留空）" />
          </el-form-item>
          <el-form-item label="描述">
            <el-input v-model="form.description" type="textarea" :rows="4" placeholder="详细描述宠物情况" />
          </el-form-item>
          <el-form-item label="联系方式">
            <el-input v-model="form.contact" placeholder="手机号或微信" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="submit">发布</el-button>
            <el-button @click="$router.back()">取消</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { saveAdoption } from '@/api/adoption'

const router = useRouter()
const form = reactive({
  type: 'adoption',
  petName: '',
  breed: '',
  petType: '',
  age: '',
  gender: '',
  photoUrl: '',
  description: '',
  contact: ''
})

async function submit() {
  if (!form.petName) {
    ElMessage.warning('请输入宠物名称')
    return
  }
  try {
    await saveAdoption(form)
    ElMessage.success('发布成功，等待管理员审核')
    router.push('/adoption')
  } catch (e) {}
}
</script>

<style scoped>
.publish-page { min-height: 100%; background: #f5f7fa; }
.page-shell { padding: 18px clamp(16px, 2vw, 32px) 40px; max-width: 700px; margin: 0 auto; }
.form-card { background: #fff; border-radius: 16px; padding: 30px; margin-top: 12px; }
.form-card h2 { margin: 0 0 24px; }
</style>
