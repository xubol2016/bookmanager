package top.xubol.bookmanager.vo;

import lombok.Data;
import top.xubol.bookmanager.entity.Announcement;
import top.xubol.bookmanager.entity.Book;

import java.util.List;

/**
 * 全局搜索结果VO
 */
@Data
public class SearchResultVO {

    /**
     * 图书搜索结果
     */
    private List<Book> books;

    /**
     * 公告搜索结果
     */
    private List<Announcement> announcements;

    /**
     * 图书总数
     */
    private Long bookTotal;

    /**
     * 公告总数
     */
    private Long announcementTotal;
}
