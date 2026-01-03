package top.xubol.bookmanager.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class BorrowQueryDTO {
    
    private Integer page = 1;
    
    private Integer size = 10;

    /**
     * 用户ID (管理员查询指定用户)
     */
    private Long userId;

    /**
     * 图书ID
     */
    private Long bookId;

    /**
     * 状态: 1-借阅中, 2-已归还, 3-已超期
     */
    private Integer status;

    /**
     * 用户名 (关联查询用)
     */
    private String username;
    
    /**
     * 图书名
     */
    private String bookTitle;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
}
