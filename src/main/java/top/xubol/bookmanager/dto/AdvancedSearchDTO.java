package top.xubol.bookmanager.dto;

import lombok.Data;

/**
 * 高级搜索DTO
 */
@Data
public class AdvancedSearchDTO {

    /**
     * 关键词 (模糊匹配书名、作者、ISBN)
     */
    private String keyword;

    /**
     * 书名
     */
    private String title;

    /**
     * 作者
     */
    private String author;

    /**
     * 出版社
     */
    private String publisher;

    /**
     * ISBN编码
     */
    private String isbn;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 是否只显示有库存的图书
     */
    private Boolean inStock;

    /**
     * 页码
     */
    private Integer page = 1;

    /**
     * 每页数量
     */
    private Integer size = 10;

    /**
     * 排序字段 (title, author, borrowCount)
     */
    private String sortBy;

    /**
     * 排序方向 (asc, desc)
     */
    private String sortOrder = "asc";
}
