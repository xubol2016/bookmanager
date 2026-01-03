package top.xubol.bookmanager.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 公告DTO
 */
@Data
public class AnnouncementDTO {

    /**
     * 公告ID (更新时使用)
     */
    private Long id;

    /**
     * 公告标题
     */
    private String title;

    /**
     * 公告内容
     */
    private String content;

    /**
     * 是否置顶: 1-是, 0-否
     */
    private Integer isTop;

    /**
     * 定时发布时间 (为空则立即发布)
     */
    private LocalDateTime scheduledPublishTime;

    /**
     * 过期时间 (为空则永不过期)
     */
    private LocalDateTime expireTime;

    /**
     * 发布状态: 0-草稿, 1-已发布, 2-已过期, 3-待发布(定时)
     */
    private Integer status;
}
