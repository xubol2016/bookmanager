package top.xubol.bookmanager.vo;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 导航菜单VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NavigationMenuVO {

    /**
     * 菜单名称
     */
    private String name;

    /**
     * 菜单路径
     */
    private String path;

    /**
     * 菜单图标
     */
    private String icon;

    /**
     * 排序优先级
     */
    private Integer sortOrder;

    /**
     * 子菜单
     */
    private List<NavigationMenuVO> children;
}
