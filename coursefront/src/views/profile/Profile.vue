<template>
  <div class="profile-container">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card class="box-card">
          <template #header>
            <div class="clearfix">
              <span>个人信息</span>
            </div>
          </template>
          <div class="text item">
            <p>用户名: {{ userInfo.username }}</p>
            <p>真实姓名: {{ userInfo.realName }}</p>
            <p>手机号: {{ userInfo.phone }}</p>
            <p>角色: {{ userInfo.role === 1 ? '管理员' : '普通用户' }}</p>
            <p>注册时间: {{ formatTime(userInfo.createdTime) }}</p>
          </div>
        </el-card>
      </el-col>
      <el-col :span="16">
        <el-card class="box-card">
          <template #header>
            <div class="clearfix">
              <span>编辑资料</span>
            </div>
          </template>
          <el-tabs v-model="activeTab">
             <el-tab-pane label="基本信息" name="info">
                 <el-form :model="form" label-width="80px">
                     <el-form-item label="真实姓名">
                         <el-input v-model="form.realName" />
                     </el-form-item>
                     <el-form-item label="手机号">
                         <el-input v-model="form.phone" />
                     </el-form-item>
                     <el-form-item>
                         <el-button type="primary" @click="updateInfo">保存修改</el-button>
                     </el-form-item>
                 </el-form>
             </el-tab-pane>
             <el-tab-pane label="修改密码" name="password">
                 <el-form :model="pwdForm" :rules="pwdRules" ref="pwdFormRef" label-width="80px">
                     <el-form-item label="新密码" prop="password">
                         <el-input v-model="pwdForm.password" type="password" show-password />
                     </el-form-item>
                     <el-form-item label="确认密码" prop="confirmPassword">
                         <el-input v-model="pwdForm.confirmPassword" type="password" show-password />
                     </el-form-item>
                     <el-form-item>
                         <el-button type="primary" @click="updatePassword">修改密码</el-button>
                     </el-form-item>
                 </el-form>
             </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const userInfo = ref({})
const activeTab = ref('info')

const form = reactive({
    realName: '',
    phone: ''
})

const pwdForm = reactive({
    password: '',
    confirmPassword: ''
})

const pwdFormRef = ref(null)

const validatePass2 = (rule, value, callback) => {
    if (value === '') {
        callback(new Error('请再次输入密码'))
    } else if (value !== pwdForm.password) {
        callback(new Error('两次输入密码不一致!'))
    } else {
        callback()
    }
}

const pwdRules = {
    password: [{ required: true, message: '请输入新密码', trigger: 'blur' }, { min: 6, message: '至少6位', trigger: 'blur' }],
    confirmPassword: [{ validator: validatePass2, trigger: 'blur' }]
}

onMounted(async () => {
    await fetchProfile()
})

const fetchProfile = async () => {
    try {
        const res = await userStore.getProfile()
        userInfo.value = res
        form.realName = res.realName
        form.phone = res.phone
    } catch (e) {
        // error
    }
}

const updateInfo = async () => {
    try {
        await request.put('/users/profile/update', form)
        ElMessage.success('信息更新成功')
        await fetchProfile()
    } catch (e) {
        // error
    }
}

const updatePassword = async () => {
    if (!pwdFormRef.value) return
    await pwdFormRef.value.validate(async (valid) => {
        if (valid) {
            try {
                await request.put('/users/profile/update', { password: pwdForm.password })
                ElMessage.success('密码修改成功')
                pwdForm.password = ''
                pwdForm.confirmPassword = ''
            } catch (e) {
                // error
            }
        }
    })
}

const formatTime = (time) => {
    if (!time) return ''
    return new Date(time).toLocaleString()
}
</script>

<style scoped>
.text {
    font-size: 14px;
}

.item {
    margin-bottom: 18px;
}
.item p {
    line-height: 2;
}
</style>
