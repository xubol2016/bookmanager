<template>
  <div class="login-container">
    <div class="login-card">
      <div class="title">
        <h2>图书管理系统</h2>
        <p>欢迎登录</p>
      </div>
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        label-position="top"
        size="large"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入用户名" prefix-icon="User" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="loginForm.password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            type="password"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-form-item>
          <div class="flex-row">
            <el-checkbox v-model="rememberMe">记住我</el-checkbox>
            <el-link type="primary" :underline="false" @click="handleForgotPassword">忘记密码?</el-link>
          </div>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" class="w-100" @click="handleLogin">登录</el-button>
        </el-form-item>
        <div class="footer">
          <span>还没有账号? </span>
          <el-link type="primary" @click="$router.push('/register')">立即注册</el-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const loginFormRef = ref(null)
const loading = ref(false)
const rememberMe = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const loginRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await userStore.login(loginForm)
        ElMessage.success('登录成功')
        // Load profile to get role
        await userStore.getProfile()
        router.push('/')
      } catch (error) {
        // Error handled in interceptor or here
      } finally {
        loading.value = false
      }
    }
  })
}

const handleForgotPassword = () => {
  ElMessage.info('请联系系统管理员重置密码')
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

.login-card {
  width: 400px;
  padding: 40px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
}

.title {
  text-align: center;
  margin-bottom: 30px;
}

.title h2 {
  margin: 0;
  color: #303133;
}

.title p {
  margin: 10px 0 0;
  color: #909399;
}

.w-100 {
  width: 100%;
}

.flex-row {
  display: flex;
  justify-content: space-between;
  width: 100%;
}

.footer {
  text-align: center;
  margin-top: 10px;
  font-size: 14px;
  color: #606266;
}
</style>
