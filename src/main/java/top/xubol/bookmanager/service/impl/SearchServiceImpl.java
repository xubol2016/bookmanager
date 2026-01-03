package top.xubol.bookmanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import top.xubol.bookmanager.common.Result;
import top.xubol.bookmanager.dto.AdvancedSearchDTO;
import top.xubol.bookmanager.entity.Announcement;
import top.xubol.bookmanager.entity.Book;
import top.xubol.bookmanager.service.AnnouncementService;
import top.xubol.bookmanager.service.BookService;
import top.xubol.bookmanager.service.SearchService;
import top.xubol.bookmanager.vo.SearchResultVO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private BookService bookService;

    @Autowired
    private AnnouncementService announcementService;

    @Override
    public Result<Page<Book>> searchBooks(String keyword, int page, int size) {
        Page<Book> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w
                    .like(Book::getTitle, keyword)
                    .or()
                    .like(Book::getAuthor, keyword)
                    .or()
                    .like(Book::getIsbn, keyword)
                    .or()
                    .like(Book::getPublisher, keyword)
            );
        }
        wrapper.eq(Book::getStatus, 1);
        wrapper.orderByDesc(Book::getId);

        return Result.success(bookService.page(pageParam, wrapper));
    }

    @Override
    public Result<Page<Book>> advancedSearch(AdvancedSearchDTO searchDTO) {
        Page<Book> pageParam = new Page<>(searchDTO.getPage(), searchDTO.getSize());
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();

        // 只搜索上架图书
        wrapper.eq(Book::getStatus, 1);

        // 综合关键词搜索
        if (StringUtils.hasText(searchDTO.getKeyword())) {
            wrapper.and(w -> w
                    .like(Book::getTitle, searchDTO.getKeyword())
                    .or()
                    .like(Book::getAuthor, searchDTO.getKeyword())
                    .or()
                    .like(Book::getIsbn, searchDTO.getKeyword())
            );
        }

        // 书名精确搜索
        if (StringUtils.hasText(searchDTO.getTitle())) {
            wrapper.like(Book::getTitle, searchDTO.getTitle());
        }

        // 作者搜索
        if (StringUtils.hasText(searchDTO.getAuthor())) {
            wrapper.like(Book::getAuthor, searchDTO.getAuthor());
        }

        // 出版社搜索
        if (StringUtils.hasText(searchDTO.getPublisher())) {
            wrapper.like(Book::getPublisher, searchDTO.getPublisher());
        }

        // ISBN搜索
        if (StringUtils.hasText(searchDTO.getIsbn())) {
            wrapper.like(Book::getIsbn, searchDTO.getIsbn());
        }

        // 分类筛选
        if (searchDTO.getCategoryId() != null) {
            wrapper.eq(Book::getCategoryId, searchDTO.getCategoryId());
        }

        // 只显示有库存
        if (Boolean.TRUE.equals(searchDTO.getInStock())) {
            wrapper.gt(Book::getStock, 0);
        }

        // 排序处理
        if (StringUtils.hasText(searchDTO.getSortBy())) {
            boolean isAsc = "asc".equalsIgnoreCase(searchDTO.getSortOrder());
            switch (searchDTO.getSortBy()) {
                case "title":
                    if (isAsc) {
                        wrapper.orderByAsc(Book::getTitle);
                    } else {
                        wrapper.orderByDesc(Book::getTitle);
                    }
                    break;
                case "author":
                    if (isAsc) {
                        wrapper.orderByAsc(Book::getAuthor);
                    } else {
                        wrapper.orderByDesc(Book::getAuthor);
                    }
                    break;
                default:
                    wrapper.orderByDesc(Book::getId);
            }
        } else {
            wrapper.orderByDesc(Book::getId);
        }

        return Result.success(bookService.page(pageParam, wrapper));
    }

    @Override
    public Result<List<String>> getSearchSuggestions(String keyword, int limit) {
        if (!StringUtils.hasText(keyword)) {
            return Result.success(List.of());
        }

        Set<String> suggestions = new HashSet<>();

        // 从书名中获取建议
        List<Book> titleMatches = bookService.list(new LambdaQueryWrapper<Book>()
                .like(Book::getTitle, keyword)
                .eq(Book::getStatus, 1)
                .select(Book::getTitle)
                .last("LIMIT " + limit));
        titleMatches.forEach(b -> suggestions.add(b.getTitle()));

        // 从作者中获取建议
        if (suggestions.size() < limit) {
            List<Book> authorMatches = bookService.list(new LambdaQueryWrapper<Book>()
                    .like(Book::getAuthor, keyword)
                    .eq(Book::getStatus, 1)
                    .select(Book::getAuthor)
                    .last("LIMIT " + (limit - suggestions.size())));
            authorMatches.forEach(b -> suggestions.add(b.getAuthor()));
        }

        // 从出版社中获取建议
        if (suggestions.size() < limit) {
            List<Book> publisherMatches = bookService.list(new LambdaQueryWrapper<Book>()
                    .like(Book::getPublisher, keyword)
                    .eq(Book::getStatus, 1)
                    .select(Book::getPublisher)
                    .last("LIMIT " + (limit - suggestions.size())));
            publisherMatches.forEach(b -> {
                if (b.getPublisher() != null) {
                    suggestions.add(b.getPublisher());
                }
            });
        }

        return Result.success(new ArrayList<>(suggestions).subList(0, Math.min(suggestions.size(), limit)));
    }

    @Override
    public Result<SearchResultVO> globalSearch(String keyword, int limit) {
        SearchResultVO result = new SearchResultVO();

        if (!StringUtils.hasText(keyword)) {
            result.setBooks(List.of());
            result.setAnnouncements(List.of());
            result.setBookTotal(0L);
            result.setAnnouncementTotal(0L);
            return Result.success(result);
        }

        // 搜索图书
        LambdaQueryWrapper<Book> bookWrapper = new LambdaQueryWrapper<>();
        bookWrapper.and(w -> w
                .like(Book::getTitle, keyword)
                .or()
                .like(Book::getAuthor, keyword)
                .or()
                .like(Book::getIsbn, keyword)
        );
        bookWrapper.eq(Book::getStatus, 1);

        long bookTotal = bookService.count(bookWrapper);
        bookWrapper.last("LIMIT " + limit);
        List<Book> books = bookService.list(bookWrapper);

        // 搜索公告（只搜索已发布的，即publish_time <= 当前时间）
        LambdaQueryWrapper<Announcement> announcementWrapper = new LambdaQueryWrapper<>();
        announcementWrapper.and(w -> w
                .like(Announcement::getTitle, keyword)
                .or()
                .like(Announcement::getContent, keyword)
        );
        announcementWrapper.le(Announcement::getPublishTime, LocalDateTime.now());

        long announcementTotal = announcementService.count(announcementWrapper);
        announcementWrapper.orderByDesc(Announcement::getPublishTime);
        announcementWrapper.last("LIMIT " + limit);
        List<Announcement> announcements = announcementService.list(announcementWrapper);

        result.setBooks(books);
        result.setAnnouncements(announcements);
        result.setBookTotal(bookTotal);
        result.setAnnouncementTotal(announcementTotal);

        return Result.success(result);
    }
}
