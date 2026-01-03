package top.xubol.bookmanager.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.xubol.bookmanager.common.Result;
import top.xubol.bookmanager.service.NavigationService;
import top.xubol.bookmanager.util.JwtUtils;
import top.xubol.bookmanager.vo.NavigationMenuVO;

import java.util.List;

/**
 * 导航菜单控制器
 */
@RestController
@RequestMapping("/api/navigation")
public class NavigationController {

    @Autowired
    private NavigationService navigationService;

    @Autowired
    private HttpServletRequest request;

    /**
     * 获取当前用户的导航菜单
     */
    @GetMapping("/menus")
    public Result<List<NavigationMenuVO>> getMenus() {
        Integer role = getCurrentUserRole();
        return navigationService.getMenusByRole(role);
    }

    /**
     * 获取当前用户的快捷入口
     */
    @GetMapping("/quick-access")
    public Result<List<NavigationMenuVO>> getQuickAccess() {
        Integer role = getCurrentUserRole();
        return navigationService.getQuickAccess(role);
    }

    /**
     * 从请求头中获取当前用户角色
     */
    private Integer getCurrentUserRole() {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            try {
                return jwtUtils.extractRole(token);
            } catch (Exception e) {
                return 0; // 默认普通用户
            }
        }
        return 0;
    }

    @Autowired
    private JwtUtils jwtUtils;
}
