package top.xubol.bookmanager.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import top.xubol.bookmanager.service.BorrowService;

@Component
public class OverdueTask {

    @Autowired
    private BorrowService borrowService;

    /**
     * 每天凌晨执行一次，检查超期
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void checkOverdue() {
        borrowService.checkOverdue();
    }
}
