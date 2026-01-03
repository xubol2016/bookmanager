package top.xubol.bookmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.xubol.bookmanager.entity.BorrowRecord;

@Mapper
public interface BorrowRecordMapper extends BaseMapper<BorrowRecord> {

    /**
     * 统计今日借阅数量
     */
    Long countTodayBorrow();

    /**
     * 统计今日归还数量
     */
    Long countTodayReturn();

    /**
     * 统计超期未还数量
     */
    Long countOverdue();
}
