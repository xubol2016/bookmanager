package top.xubol.bookmanager.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import top.xubol.bookmanager.common.Result;
import top.xubol.bookmanager.dto.BorrowApplyDTO;
import top.xubol.bookmanager.dto.BorrowQueryDTO;
import top.xubol.bookmanager.entity.BorrowRecord;

public interface BorrowService extends IService<BorrowRecord> {

    /**
     * 申请借阅
     */
    Result<String> applyBorrow(Long userId, BorrowApplyDTO applyDTO);

    /**
     * 归还图书
     */
    Result<String> returnBook(Long userId, Long recordId);

    /**
     * 获取我的借阅记录
     */
    Result<Page<BorrowRecord>> getMyRecords(Long userId, BorrowQueryDTO queryDTO);

    /**
     * (管理员) 获取借阅记录列表
     */
    Result<Page<BorrowRecord>> getRecordPage(BorrowQueryDTO queryDTO);

    /**
     * (管理员) 检查并处理超期
     */
    void checkOverdue();
}
