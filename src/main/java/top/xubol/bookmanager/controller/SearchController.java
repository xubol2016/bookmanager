package top.xubol.bookmanager.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.xubol.bookmanager.common.Result;
import top.xubol.bookmanager.dto.AdvancedSearchDTO;
import top.xubol.bookmanager.entity.Book;
import top.xubol.bookmanager.service.SearchService;
import top.xubol.bookmanager.vo.SearchResultVO;

import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    @Autowired
    private SearchService searchService;

    /**
     * 基础图书搜索（关键词匹配书名、作者、ISBN、出版社）
     */
    @GetMapping("/books")
    public Result<Page<Book>> searchBooks(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        return searchService.searchBooks(keyword, page, size);
    }

    /**
     * 高级图书搜索
     */
    @PostMapping("/books/advanced")
    public Result<Page<Book>> advancedSearch(@RequestBody AdvancedSearchDTO searchDTO) {
        return searchService.advancedSearch(searchDTO);
    }

    /**
     * 获取搜索建议（自动补全）
     */
    @GetMapping("/suggest")
    public Result<List<String>> getSearchSuggestions(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "10") int limit) {
        return searchService.getSearchSuggestions(keyword, limit);
    }

    /**
     * 全局搜索（同时搜索图书和公告）
     */
    @GetMapping("/global")
    public Result<SearchResultVO> globalSearch(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "5") int limit) {
        return searchService.globalSearch(keyword, limit);
    }
}
