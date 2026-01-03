package top.xubol.bookmanager.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 首页统计数据VO
 */
@Data
public class HomeStatsVO {

    /**
     * 图书总数
     */
    private Long bookCount;

    /**
     * 用户总数
     */
    private Long userCount;

    /**
     * 当前借出数量
     */
    private Long borrowCount;

    /**
     * 超期借阅数量
     */
    private Long overdueCount;

    /**
     * 今日借阅数量
     */
    private Long todayBorrowCount;

    /**
     * 今日归还数量
     */
    private Long todayReturnCount;

    /**
     * 分类统计数据 (分类名称 -> 图书数量)
     */
    private List<Map<String, Object>> categoryStats;

    /**
     * 近7天借阅趋势 (日期 -> 借阅量)
     */
    private List<Map<String, Object>> borrowTrend;
}
