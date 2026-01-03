package top.xubol.bookmanager.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import top.xubol.bookmanager.common.Result;
import top.xubol.bookmanager.dto.AdvancedSearchDTO;
import top.xubol.bookmanager.entity.Book;
import top.xubol.bookmanager.vo.SearchResultVO;

import java.util.List;

/**
 * 搜索服务接口
 */
public interface SearchService {

    /**
     * 基础图书搜索（关键词匹配书名、作者、ISBN）
     */
    Result<Page<Book>> searchBooks(String keyword, int page, int size);

    /**
     * 高级图书搜索
     */
    Result<Page<Book>> advancedSearch(AdvancedSearchDTO searchDTO);

    /**
     * 获取搜索建议（自动补全）
     */
    Result<List<String>> getSearchSuggestions(String keyword, int limit);

    /**
     * 全局搜索（同时搜索图书和公告）
     */
    Result<SearchResultVO> globalSearch(String keyword, int limit);
}
