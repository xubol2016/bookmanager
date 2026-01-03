package top.xubol.bookmanager.service;

import top.xubol.bookmanager.common.Result;
import top.xubol.bookmanager.vo.NavigationMenuVO;

import java.util.List;

/**
 * 导航菜单服务接口
 */
public interface NavigationService {

    /**
     * 根据用户角色获取导航菜单
     * @param role 用户角色 (0-普通用户, 1-管理员)
     * @return 导航菜单列表
     */
    Result<List<NavigationMenuVO>> getMenusByRole(Integer role);

    /**
     * 获取快捷入口
     * @param role 用户角色
     * @return 快捷入口列表
     */
    Result<List<NavigationMenuVO>> getQuickAccess(Integer role);
}
