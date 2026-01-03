import axios from 'axios'
import { ElMessage } from 'element-plus'

const service = axios.create({
    baseURL: '/api', // Vite proxy handles this
    timeout: 5000
})

// Request interceptor
service.interceptors.request.use(
    (config) => {
        const userStore = localStorage.getItem('user')
        if (userStore) {
            try {
                const token = JSON.parse(userStore).token
                if (token) {
                    config.headers['Authorization'] = `Bearer ${token}`
                }
            } catch (e) {
                console.error('Failed to parse user token', e)
            }
        }
        return config
    },
    (error) => {
        return Promise.reject(error)
    }
)

// Response interceptor
service.interceptors.response.use(
    (response) => {
        const res = response.data
        if (res.code !== 200) {
            ElMessage({
                message: res.message || 'Error',
                type: 'error',
                duration: 5 * 1000
            })
            // Handle auth errors
            if (res.code === 401 || res.code === 403) {
                // To do: redirect to login or clear token
            }
            return Promise.reject(new Error(res.message || 'Error'))
        } else {
            return res.data
        }
    },
    (error) => {
        console.error('err' + error)
        let message = error.message
        if (error.response && error.response.data) {
            message = error.response.data.message || error.message
            // Handle 401 specifically if backend returns standard 401 status code
            if (error.response.status === 401) {
                message = 'Unauthorized or Token Expired'
                // clear token logic could go here
            }
        }
        ElMessage({
            message: message,
            type: 'error',
            duration: 5 * 1000
        })
        return Promise.reject(error)
    }
)

export default service
