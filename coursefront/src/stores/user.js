import { defineStore } from 'pinia'
import { ref } from 'vue'
import request from '@/utils/request'

export const useUserStore = defineStore('user', () => {
    const token = ref('')
    const userInfo = ref({})
    const role = ref(0) // 0: user, 1: admin

    // Persist state (simple version, normally use plugin)
    // For simplicity, we just init from localStorage if valid
    // But defineStore + persist plugin is better.
    // Here we manually save in login action.

    function setToken(newToken) {
        token.value = newToken
    }

    function setUserInfo(info) {
        userInfo.value = info
        role.value = info.role
    }

    const login = async (form) => {
        const data = await request.post('/users/login', form)
        token.value = data // Currently backend returns string token or data object? 
        // Check UserServiceImpl: Result.success(token, "登录成功") -> data is String token
        // We should also get user info or decode token to get role, 
        // but backend 'login' only returns token string.
        // So we need to fetch profile after login.
        return data
    }

    const getProfile = async () => {
        const data = await request.get('/users/profile')
        setUserInfo(data)
        return data
    }

    const register = async (form) => {
        return await request.post('/users/register', form)
    }

    const logout = () => {
        token.value = ''
        userInfo.value = {}
        role.value = 0
        localStorage.removeItem('user')
    }

    return { token, userInfo, role, login, getProfile, register, logout, setToken }
}, {
    persist: true // Need pinia-plugin-persistedstate if we want auto persist, 
    // or we manually handle localStorage in actions/components.
    // I'll create a simple persist mechanism in main.js or separate file if requested,
    // or just assume Pinia persists not strictly required if I handle it manually.
    // Actually, better to just use localStorage manually for simplicity without extra plugins for now.
})
