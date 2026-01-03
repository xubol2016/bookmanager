package top.xubol.bookmanager.service;

import top.xubol.bookmanager.common.Result;
import top.xubol.bookmanager.entity.Book;
import top.xubol.bookmanager.vo.HomeStatsVO;
import top.xubol.bookmanager.vo.HotBookVO;

import java.util.List;
import java.util.Map;

public interface HomeService {

    /**
     * 获取首页统计数据
     */
    Result<Map<String, Object>> getHomeStats();

    /**
     * 获取完整首页统计数据（包含分类统计和借阅趋势）
     */
    Result<HomeStatsVO> getFullHomeStats();

    /**
     * 获取推荐图书（最新上架）
     */
    Result<List<Book>> getRecommendations();

    /**
     * 获取热门图书（按借阅量排序）
     */
    Result<List<HotBookVO>> getHotBooks(int limit);

    /**
     * 获取分类统计数据
     */
    Result<List<Map<String, Object>>> getCategoryStats();

    /**
     * 获取借阅趋势数据
     */
    Result<List<Map<String, Object>>> getBorrowTrend(int days);
}
