<template>
  <div class="register-container">
    <div class="register-card">
      <div class="title">
        <h2>图书管理系统</h2>
        <p>创建新账号</p>
      </div>
      <el-form
        ref="registerFormRef"
        :model="registerForm"
        :rules="registerRules"
        label-position="top"
        size="large"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="registerForm.username" placeholder="请输入用户名" prefix-icon="User" />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
            <el-input v-model="registerForm.realName" placeholder="请输入真实姓名" prefix-icon="Postcard" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="registerForm.password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            type="password"
            show-password
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
            <el-input
              v-model="registerForm.confirmPassword"
              placeholder="请再次输入密码"
              prefix-icon="Lock"
              type="password"
              show-password
            />
          </el-form-item>
        <el-form-item label="手机号" prop="phone">
            <el-input v-model="registerForm.phone" placeholder="请输入手机号" prefix-icon="Iphone" />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" :loading="loading" class="w-100" @click="handleRegister">注册</el-button>
        </el-form-item>
        <div class="footer">
          <span>已有账号? </span>
          <el-link type="primary" @click="$router.push('/login')">立即登录</el-link>
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

const registerFormRef = ref(null)
const loading = ref(false)

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  realName: '',
  phone: ''
})

const validatePass2 = (rule, value, callback) => {
    if (value === '') {
        callback(new Error('请再次输入密码'))
    } else if (value !== registerForm.password) {
        callback(new Error('两次输入密码不一致!'))
    } else {
        callback()
    }
}

const registerRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }, { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }],
  confirmPassword: [{ validator: validatePass2, trigger: 'blur' }],
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }, { pattern: /^1[3-9]\d{9}$/, message: '目前只支持中国大陆手机号', trigger: 'blur' }]
}

const handleRegister = async () => {
  if (!registerFormRef.value) return
  await registerFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await userStore.register({
            username: registerForm.username,
            password: registerForm.password,
            realName: registerForm.realName,
            phone: registerForm.phone
        })
        ElMessage.success('注册成功，请登录')
        router.push('/login')
      } catch (error) {
        // Error handled in interceptor
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.register-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

.register-card {
  width: 450px;
  padding: 40px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
  max-height: 90vh;
  overflow-y: auto;
}

.title {
  text-align: center;
  margin-bottom: 20px;
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

.footer {
  text-align: center;
  margin-top: 10px;
  font-size: 14px;
  color: #606266;
}
</style>
