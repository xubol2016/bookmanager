package top.xubol.bookmanager.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;

/**
 * 图书实体类
 */
@Data
@TableName("books")
public class Book implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
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
     * ISBN编码
     */
    private String isbn;

    /**
     * 封面图片地址
     */
    private String coverUrl;

    /**
     * 关联分类ID
     */
    private Long categoryId;

    /**
     * 当前库存数量
     */
    private Integer stock;

    /**
     * 存放位置 (如A区1架)
     */
    private String location;

    /**
     * 图书简介
     */
    private String description;

    /**
     * 状态: 1-上架, 0-下架
     */
    private Integer status;

    /**
     * 逻辑删除: 1-已删除, 0-未删除 (Comment kept for reference but field removed to match DB)
     */
    // private Integer deleted;
}
