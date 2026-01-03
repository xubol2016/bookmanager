import request from '@/utils/request'

// 获取导航菜单
export function getMenus() {
    return request({
        url: '/navigation/menus',
        method: 'get'
    })
}

// 获取快捷入口
export function getQuickAccess() {
    return request({
        url: '/navigation/quick-access',
        method: 'get'
    })
}
