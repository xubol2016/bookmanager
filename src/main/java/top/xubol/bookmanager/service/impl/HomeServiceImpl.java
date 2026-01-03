package top.xubol.bookmanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.xubol.bookmanager.common.Result;
import top.xubol.bookmanager.entity.Book;
import top.xubol.bookmanager.entity.BorrowRecord;
import top.xubol.bookmanager.mapper.BookMapper;
import top.xubol.bookmanager.mapper.BorrowRecordMapper;
import top.xubol.bookmanager.service.BookService;
import top.xubol.bookmanager.service.BorrowService;
import top.xubol.bookmanager.service.HomeService;
import top.xubol.bookmanager.service.UserService;
import top.xubol.bookmanager.vo.HomeStatsVO;
import top.xubol.bookmanager.vo.HotBookVO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HomeServiceImpl implements HomeService {

    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService;

    @Autowired
    private BorrowService borrowService;

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private BorrowRecordMapper borrowRecordMapper;

    @Override
    public Result<Map<String, Object>> getHomeStats() {
        Map<String, Object> stats = new HashMap<>();

        // 1. 图书总数
        long bookCount = bookService.count();
        stats.put("bookCount", bookCount);

        // 2. 用户总数
        long userCount = userService.count();
        stats.put("userCount", userCount);

        // 3. 当前借出数量 (Status = 1 or 3)
        long borrowCount = borrowService.count(new LambdaQueryWrapper<BorrowRecord>()
                .in(BorrowRecord::getStatus, 1, 3));
        stats.put("borrowCount", borrowCount);

        // 4. 超期数量
        Long overdueCount = borrowRecordMapper.countOverdue();
        stats.put("overdueCount", overdueCount != null ? overdueCount : 0L);

        // 5. 今日借阅数量
        Long todayBorrowCount = borrowRecordMapper.countTodayBorrow();
        stats.put("todayBorrowCount", todayBorrowCount != null ? todayBorrowCount : 0L);

        // 6. 今日归还数量
        Long todayReturnCount = borrowRecordMapper.countTodayReturn();
        stats.put("todayReturnCount", todayReturnCount != null ? todayReturnCount : 0L);

        return Result.success(stats);
    }

    @Override
    public Result<HomeStatsVO> getFullHomeStats() {
        HomeStatsVO statsVO = new HomeStatsVO();

        // 基础统计
        statsVO.setBookCount(bookService.count());
        statsVO.setUserCount(userService.count());
        statsVO.setBorrowCount(borrowService.count(new LambdaQueryWrapper<BorrowRecord>()
                .in(BorrowRecord::getStatus, 1, 3)));

        // 超期统计
        Long overdueCount = borrowRecordMapper.countOverdue();
        statsVO.setOverdueCount(overdueCount != null ? overdueCount : 0L);

        // 今日借阅/归还统计
        Long todayBorrowCount = borrowRecordMapper.countTodayBorrow();
        statsVO.setTodayBorrowCount(todayBorrowCount != null ? todayBorrowCount : 0L);

        Long todayReturnCount = borrowRecordMapper.countTodayReturn();
        statsVO.setTodayReturnCount(todayReturnCount != null ? todayReturnCount : 0L);

        // 分类统计
        statsVO.setCategoryStats(bookMapper.selectCategoryStats());

        // 近7天借阅趋势
        statsVO.setBorrowTrend(bookMapper.selectBorrowTrend(7));

        return Result.success(statsVO);
    }

    @Override
    public Result<List<Book>> getRecommendations() {
        // 推荐算法：最新上架的图书
        List<Book> books = bookService.list(new LambdaQueryWrapper<Book>()
                .eq(Book::getStatus, 1)
                .orderByDesc(Book::getId)
                .last("LIMIT 8"));
        return Result.success(books);
    }

    @Override
    public Result<List<HotBookVO>> getHotBooks(int limit) {
        // 热门图书：按借阅量排序
        List<HotBookVO> hotBooks = bookMapper.selectHotBooks(limit);
        return Result.success(hotBooks);
    }

    @Override
    public Result<List<Map<String, Object>>> getCategoryStats() {
        return Result.success(bookMapper.selectCategoryStats());
    }

    @Override
    public Result<List<Map<String, Object>>> getBorrowTrend(int days) {
        return Result.success(bookMapper.selectBorrowTrend(days));
    }
}
