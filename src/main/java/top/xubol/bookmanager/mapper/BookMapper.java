package top.xubol.bookmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.xubol.bookmanager.entity.Book;
import top.xubol.bookmanager.vo.HotBookVO;

import java.util.List;
import java.util.Map;

@Mapper
public interface BookMapper extends BaseMapper<Book> {

    /**
     * 查询热门图书（按借阅次数排序）
     */
    List<HotBookVO> selectHotBooks(@Param("limit") int limit);

    /**
     * 查询各分类图书数量统计
     */
    List<Map<String, Object>> selectCategoryStats();

    /**
     * 查询最近N天的借阅趋势
     */
    List<Map<String, Object>> selectBorrowTrend(@Param("days") int days);
}
