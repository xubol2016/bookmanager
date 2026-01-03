package top.xubol.bookmanager.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 借阅记录实体类
 */
@Data
@TableName("borrow_records")
public class BorrowRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long bookId;

    private Date borrowTime;

    private Date dueTime;

    private Date returnTime;

    /**
     * 状态: 1-借阅中, 2-已归还, 3-已超期
     */
    private Integer status;

    private String remark;
}
