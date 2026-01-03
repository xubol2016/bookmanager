package top.xubol.bookmanager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.xubol.bookmanager.common.Result;
import top.xubol.bookmanager.entity.Book;
import top.xubol.bookmanager.service.HomeService;
import top.xubol.bookmanager.vo.HomeStatsVO;
import top.xubol.bookmanager.vo.HotBookVO;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/home")
public class HomeController {

    @Autowired
    private HomeService homeService;

    /**
     * 获取首页基础统计数据
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getHomeStats() {
        return homeService.getHomeStats();
    }

    /**
     * 获取完整首页统计数据（包含分类统计和借阅趋势）
     */
    @GetMapping("/stats/full")
    public Result<HomeStatsVO> getFullHomeStats() {
        return homeService.getFullHomeStats();
    }

    /**
     * 获取推荐图书（最新上架）
     */
    @GetMapping("/recommend")
    public Result<List<Book>> getRecommendations() {
        return homeService.getRecommendations();
    }

    /**
     * 获取热门图书（按借阅量排序）
     */
    @GetMapping("/hot")
    public Result<List<HotBookVO>> getHotBooks(@RequestParam(defaultValue = "10") int limit) {
        return homeService.getHotBooks(limit);
    }

    /**
     * 获取分类统计数据
     */
    @GetMapping("/category-stats")
    public Result<List<Map<String, Object>>> getCategoryStats() {
        return homeService.getCategoryStats();
    }

    /**
     * 获取借阅趋势数据
     */
    @GetMapping("/borrow-trend")
    public Result<List<Map<String, Object>>> getBorrowTrend(@RequestParam(defaultValue = "7") int days) {
        return homeService.getBorrowTrend(days);
    }
}
