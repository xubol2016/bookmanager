package top.xubol.bookmanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.xubol.bookmanager.common.Result;
import top.xubol.bookmanager.dto.CategoryDTO;
import top.xubol.bookmanager.entity.Book;
import top.xubol.bookmanager.entity.Category;
import top.xubol.bookmanager.mapper.CategoryMapper;
import top.xubol.bookmanager.service.BookService;
import top.xubol.bookmanager.service.CategoryService;
import top.xubol.bookmanager.vo.CategoryTreeVO;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private BookService bookService;

    @Override
    public Result<List<CategoryTreeVO>> getCategoryTree() {
        List<Category> allCategories = this.list(new LambdaQueryWrapper<Category>().orderByAsc(Category::getSortOrder));
        List<CategoryTreeVO> tree = buildTree(allCategories, 0L);
        return Result.success(tree);
    }

    @Override
    public Result<List<Category>> getCategoryList() {
        return Result.success(this.list(new LambdaQueryWrapper<Category>().orderByAsc(Category::getSortOrder)));
    }

    @Override
    public Result<String> addCategory(CategoryDTO categoryDTO) {
        // 校验名称唯一性
        if (checkNameExists(categoryDTO.getName(), null)) {
            return Result.failed("分类名称已存在");
        }

        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);
        
        // 计算level
        if (category.getParentId() == null || category.getParentId() == 0) {
            category.setLevel(1);
            category.setParentId(0L);
        } else {
            Category parent = this.getById(category.getParentId());
            if (parent == null) {
                return Result.failed("父分类不存在");
            }
            category.setLevel(parent.getLevel() + 1);
        }

        this.save(category);
        return Result.success("添加成功");
    }

    @Override
    public Result<String> updateCategory(Long id, CategoryDTO categoryDTO) {
        Category category = this.getById(id);
        if (category == null) {
            return Result.failed("分类不存在");
        }

        if (checkNameExists(categoryDTO.getName(), id)) {
            return Result.failed("分类名称已存在");
        }

        BeanUtils.copyProperties(categoryDTO, category);
        
        // 如果修改了parentId，需要重新计算level
        if (category.getParentId() == 0) {
             category.setLevel(1);
        } else {
             Category parent = this.getById(category.getParentId());
             if (parent != null) {
                 category.setLevel(parent.getLevel() + 1);
             }
        }
        
        this.updateById(category);
        return Result.success("修改成功");
    }

    @Override
    public Result<String> deleteCategory(Long id) {
        // 1. 检查是否有子分类
        long childCount = this.count(new LambdaQueryWrapper<Category>().eq(Category::getParentId, id));
        if (childCount > 0) {
            return Result.failed("存在子分类，无法删除");
        }

        // 2. 检查是否有图书关联
        long bookCount = bookService.count(new LambdaQueryWrapper<Book>().eq(Book::getCategoryId, id));
        if (bookCount > 0) {
            return Result.failed("该分类下有图书，无法删除");
        }

        this.removeById(id);
        return Result.success("删除成功");
    }

    @Override
    public Result<Map<String, Object>> getCategoryStats(Long id) {
        Map<String, Object> stats = new HashMap<>();
        // 统计该分类下图书总数
        long count = bookService.count(new LambdaQueryWrapper<Book>().eq(Book::getCategoryId, id));
        stats.put("bookCount", count);
        
        Category category = this.getById(id);
        stats.put("categoryName", category != null ? category.getName() : "Unknown");
        
        return Result.success(stats);
    }

    private boolean checkNameExists(String name, Long excludeId) {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getName, name);
        if (excludeId != null) {
            wrapper.ne(Category::getId, excludeId);
        }
        return this.count(wrapper) > 0;
    }

    private List<CategoryTreeVO> buildTree(List<Category> all, Long parentId) {
        List<CategoryTreeVO> tree = new ArrayList<>();
        for (Category category : all) {
            if (category.getParentId().equals(parentId)) {
                CategoryTreeVO vo = new CategoryTreeVO();
                BeanUtils.copyProperties(category, vo);
                vo.setChildren(buildTree(all, category.getId()));
                tree.add(vo);
            }
        }
        return tree;
    }
}
