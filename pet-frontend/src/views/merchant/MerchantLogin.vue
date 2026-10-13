<template>
  <div class="merchant-login-page">
    <div class="login-card">
      <div class="brand">
        <span class="logo">🐾</span>
        <div class="title">宠知AI</div>
        <div class="subtitle">商家后台</div>
      </div>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-position="top"
        @keyup.enter="onSubmit"
      >
        <el-form-item label="商家账号" prop="account">
          <el-input v-model="form.account" placeholder="请输入商家账号" :prefix-icon="User" size="large" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" :prefix-icon="Lock" size="large" show-password />
        </el-form-item>
        <template v-if="mode === 'register'">
          <el-form-item label="商家名称" prop="name">
            <el-input v-model="form.name" placeholder="请输入商家/店铺名称" :prefix-icon="Shop" size="large" />
          </el-form-item>
          <el-form-item label="联系电话" prop="phone">
            <el-input v-model="form.phone" placeholder="请输入联系电话" :prefix-icon="Phone" size="large" />
          </el-form-item>
        </template>
        <el-button
          type="primary"
          size="large"
          class="submit-btn"
          :loading="loading"
          @click="onSubmit"
        >{{ mode === 'login' ? '登录商家后台' : '注册商家账号' }}</el-button>
        <div class="switch-link">
          <el-link type="primary" @click="toggleMode">
            {{ mode === 'login' ? '没有账号？立即注册' : '已有账号？返回登录' }}
          </el-link>
        </div>
        <div class="back-link">
          <router-link to="/home">← 返回前台首页</router-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Shop, Phone } from '@element-plus/icons-vue'
import { merchantLogin, merchantRegister } from '@/api/merchant'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(false)
const mode = ref('login')

const form = reactive({
  account: '',
  password: '',
  name: '',
  phone: ''
})

const rules = {
  account: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

function toggleMode() {
  mode.value = mode.value === 'login' ? 'register' : 'login'
}

async function onSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      if (mode.value === 'login') {
        const res = await merchantLogin({ account: form.account, password: form.password })
        userStore.setLogin(res.data)
        ElMessage.success('登录成功')
        const redirect = route.query.redirect || '/merchant/dashboard'
        router.push(redirect)
      } else {
        await merchantRegister({ ...form })
        ElMessage.success('注册成功，请登录')
        mode.value = 'login'
      }
    } finally {
      loading.value = false
    }
  })
}
</script>

<style scoped>
.merchant-login-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #1e3a5f 0%, #2d5a87 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}
.login-card {
  width: 420px;
  background: #fff;
  border-radius: 12px;
  padding: 36px 36px 28px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
}
.brand {
  text-align: center;
  margin-bottom: 24px;
}
.logo { font-size: 48px; }
.title {
  font-size: 22px;
  font-weight: 600;
  color: #2c3e50;
  margin-top: 8px;
}
.subtitle {
  color: #909399;
  font-size: 14px;
  margin-top: 4px;
}
.submit-btn {
  width: 100%;
  margin-top: 8px;
}
.switch-link {
  text-align: center;
  margin-top: 14px;
}
.back-link {
  text-align: center;
  margin-top: 12px;
  font-size: 13px;
}
.back-link a {
  color: #606266;
  text-decoration: none;
}
.back-link a:hover { color: #409eff; }
</style>
