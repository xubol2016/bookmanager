package top.xubol.bookmanager.dto;

import lombok.Data;

@Data
public class BorrowApplyDTO {
    /**
     * 图书ID
     */
    private Long bookId;

    /**
     * 借阅天数 (可选, 默认30)
     */
    private Integer days;
}
