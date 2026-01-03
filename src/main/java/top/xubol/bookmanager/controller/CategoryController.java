package top.xubol.bookmanager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.xubol.bookmanager.common.RequireRole;
import top.xubol.bookmanager.common.Result;
import top.xubol.bookmanager.dto.CategoryDTO;
import top.xubol.bookmanager.entity.Category;
import top.xubol.bookmanager.service.CategoryService;
import top.xubol.bookmanager.vo.CategoryTreeVO;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/categories/tree")
    public Result<List<CategoryTreeVO>> getCategoryTree() {
        return categoryService.getCategoryTree();
    }
    
    @GetMapping("/categories/list")
    public Result<List<Category>> getCategoryList() {
        return categoryService.getCategoryList();
    }

    @PostMapping("/admin/categories")
    @RequireRole("admin")
    public Result<String> addCategory(@RequestBody CategoryDTO categoryDTO) {
        return categoryService.addCategory(categoryDTO);
    }

    @PutMapping("/admin/categories/{id}")
    @RequireRole("admin")
    public Result<String> updateCategory(@PathVariable Long id, @RequestBody CategoryDTO categoryDTO) {
        return categoryService.updateCategory(id, categoryDTO);
    }

    @DeleteMapping("/admin/categories/{id}")
    @RequireRole("admin")
    public Result<String> deleteCategory(@PathVariable Long id) {
        return categoryService.deleteCategory(id);
    }

    @GetMapping("/categories/{id}/stats")
    public Result<Map<String, Object>> getCategoryStats(@PathVariable Long id) {
        return categoryService.getCategoryStats(id);
    }
}
