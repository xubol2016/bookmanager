package top.xubol.bookmanager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.xubol.bookmanager.common.Result;
import top.xubol.bookmanager.dto.CategoryDTO;
import top.xubol.bookmanager.entity.Category;
import top.xubol.bookmanager.vo.CategoryTreeVO;

import java.util.List;
import java.util.Map;

public interface CategoryService extends IService<Category> {

    /**
     * 获取分类树
     */
    Result<List<CategoryTreeVO>> getCategoryTree();

    /**
     * 获取所有分类列表 (平铺)
     */
    Result<List<Category>> getCategoryList();

    /**
     * 新增分类
     */
    Result<String> addCategory(CategoryDTO categoryDTO);

    /**
     * 修改分类
     */
    Result<String> updateCategory(Long id, CategoryDTO categoryDTO);

    /**
     * 删除分类
     */
    Result<String> deleteCategory(Long id);

    /**
     * 获取分类统计
     */
    Result<Map<String, Object>> getCategoryStats(Long id);
}
