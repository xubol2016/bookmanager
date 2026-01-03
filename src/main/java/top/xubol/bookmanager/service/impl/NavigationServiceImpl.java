package top.xubol.bookmanager.service.impl;

import org.springframework.stereotype.Service;
import top.xubol.bookmanager.common.Result;
import top.xubol.bookmanager.service.NavigationService;
import top.xubol.bookmanager.vo.NavigationMenuVO;

import java.util.ArrayList;
import java.util.List;

@Service
public class NavigationServiceImpl implements NavigationService {

    @Override
    public Result<List<NavigationMenuVO>> getMenusByRole(Integer role) {
        List<NavigationMenuVO> menus = new ArrayList<>();

        // 公共菜单 - 所有用户可见
        menus.add(createMenu("首页", "/dashboard", "HomeFilled", 1, null));
        menus.add(createMenu("图书浏览", "/books", "Reading", 2, null));
        menus.add(createMenu("公告信息", "/announcements", "Bell", 3, null));

        // 用户菜单
        List<NavigationMenuVO> userSubMenus = new ArrayList<>();
        userSubMenus.add(createMenu("我的借阅", "/my-borrow", "Document", 1, null));
        userSubMenus.add(createMenu("个人中心", "/profile", "User", 2, null));
        menus.add(createMenu("个人服务", "/user", "UserFilled", 4, userSubMenus));

        // 管理员菜单 - 仅管理员可见
        if (role != null && role == 1) {
            List<NavigationMenuVO> adminSubMenus = new ArrayList<>();
            adminSubMenus.add(createMenu("图书管理", "/admin/books", "Notebook", 1, null));
            adminSubMenus.add(createMenu("分类管理", "/admin/categories", "FolderOpened", 2, null));
            adminSubMenus.add(createMenu("借阅管理", "/admin/borrows", "List", 3, null));
            adminSubMenus.add(createMenu("超期管理", "/admin/overdue", "Warning", 4, null));
            adminSubMenus.add(createMenu("公告管理", "/admin/announcements", "EditPen", 5, null));
            menus.add(createMenu("系统管理", "/admin", "Setting", 5, adminSubMenus));

            List<NavigationMenuVO> statsSubMenus = new ArrayList<>();
            statsSubMenus.add(createMenu("借阅统计", "/admin/borrow-stats", "DataAnalysis", 1, null));
            statsSubMenus.add(createMenu("分类统计", "/admin/category-stats", "PieChart", 2, null));
            menus.add(createMenu("数据统计", "/stats", "TrendCharts", 6, statsSubMenus));

            List<NavigationMenuVO> userMgmtSubMenus = new ArrayList<>();
            userMgmtSubMenus.add(createMenu("用户列表", "/admin/users", "Coordinate", 1, null));
            menus.add(createMenu("用户管理", "/admin/user-mgmt", "Avatar", 7, userMgmtSubMenus));
        }

        return Result.success(menus);
    }

    @Override
    public Result<List<NavigationMenuVO>> getQuickAccess(Integer role) {
        List<NavigationMenuVO> quickAccess = new ArrayList<>();

        // 公共快捷入口
        quickAccess.add(createMenu("搜索图书", "/search", "Search", 1, null));
        quickAccess.add(createMenu("我的借阅", "/my-borrow", "Document", 2, null));
        quickAccess.add(createMenu("热门图书", "/books?sort=hot", "Star", 3, null));

        // 管理员快捷入口
        if (role != null && role == 1) {
            quickAccess.add(createMenu("新增图书", "/admin/books/add", "Plus", 4, null));
            quickAccess.add(createMenu("借阅审批", "/admin/borrows", "Check", 5, null));
            quickAccess.add(createMenu("发布公告", "/admin/announcements/add", "EditPen", 6, null));
        }

        return Result.success(quickAccess);
    }

    /**
     * 创建菜单项
     */
    private NavigationMenuVO createMenu(String name, String path, String icon, Integer sortOrder, List<NavigationMenuVO> children) {
        NavigationMenuVO menu = new NavigationMenuVO();
        menu.setName(name);
        menu.setPath(path);
        menu.setIcon(icon);
        menu.setSortOrder(sortOrder);
        menu.setChildren(children);
        return menu;
    }
}
