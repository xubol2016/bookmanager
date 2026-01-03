package top.xubol.bookmanager.vo;

import lombok.Data;

/**
 * 热门图书VO
 */
@Data
public class HotBookVO {

    /**
     * 图书ID
     */
    private Long id;

    /**
     * 图书名称
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
     * 封面图片地址
     */
    private String coverUrl;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 借阅次数
     */
    private Long borrowCount;

    /**
     * 当前库存
     */
    private Integer stock;

    /**
     * 图书简介
     */
    private String description;
}
