<template>
  <div class="admin-login-page">
    <div class="login-card">
      <div class="brand">
        <span class="logo">
          <svg viewBox="0 0 48 48">
            <ellipse cx="24" cy="30" rx="11" ry="9" />
            <circle cx="12" cy="20" r="4.6" />
            <circle cx="21" cy="13.5" r="4.6" />
            <circle cx="27" cy="13.5" r="4.6" />
            <circle cx="36" cy="20" r="4.6" />
          </svg>
        </span>
        <div class="title">宠知AI</div>
        <div class="subtitle">管理后台</div>
      </div>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-position="top"
        @keyup.enter="onSubmit"
      >
        <el-form-item label="管理员账号" prop="account">
          <el-input
            v-model="form.account"
            placeholder="请输入管理员账号"
            :prefix-icon="User"
            size="large"
          />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            :prefix-icon="Lock"
            size="large"
            show-password
          />
        </el-form-item>
        <el-button
          type="primary"
          size="large"
          class="submit-btn"
          :loading="loading"
          @click="onSubmit"
        >登录管理后台</el-button>
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
import { User, Lock } from '@element-plus/icons-vue'
import { adminLogin } from '@/api/admin'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  account: 'admin',
  password: '123456'
})

const rules = {
  account: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function onSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      const res = await adminLogin({ ...form })
      userStore.setLogin(res.data)
      ElMessage.success('登录成功')
      const redirect = route.query.redirect || '/admin/dashboard'
      router.push(redirect)
    } finally {
      loading.value = false
    }
  })
}
</script>

<style scoped>
.admin-login-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #1f2d3d 0%, #2c3e50 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}
.login-card {
  width: 420px;
  background: #fff;
  border-radius: 12px;
  padding: 40px 36px 32px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
}
.brand {
  text-align: center;
  margin-bottom: 28px;
}
.logo {
  width: 56px;
  height: 56px;
  margin: 0 auto;
  border-radius: 14px;
  background: linear-gradient(135deg, #6d7bf0, #6a5bd8);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 6px 14px rgba(106, 91, 216, 0.3);
}
.logo svg {
  width: 32px;
  height: 32px;
  fill: #fff;
}
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
.back-link {
  text-align: center;
  margin-top: 18px;
  font-size: 13px;
}
.back-link a {
  color: #606266;
  text-decoration: none;
}
.back-link a:hover {
  color: #409eff;
}
</style>
