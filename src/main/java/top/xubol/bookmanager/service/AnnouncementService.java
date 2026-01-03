package top.xubol.bookmanager.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import top.xubol.bookmanager.common.Result;
import top.xubol.bookmanager.dto.AnnouncementDTO;
import top.xubol.bookmanager.entity.Announcement;

import java.util.List;
import java.util.Map;

public interface AnnouncementService extends IService<Announcement> {

    /**
     * 获取置顶/最新公告（只返回已发布的公告）
     */
    Result<List<Announcement>> getTopAnnouncements(int limit);

    /**
     * 分页查询公告 (管理员，包含所有状态)
     */
    Result<Page<Announcement>> getAnnouncementPage(int page, int size, String keyword);

    /**
     * 分页查询已发布的公告（普通用户）
     */
    Result<Page<Announcement>> getPublishedAnnouncementPage(int page, int size, String keyword);

    /**
     * 发布公告（支持立即发布或定时发布）
     */
    Result<String> publishAnnouncement(Announcement announcement);

    /**
     * 定时发布公告
     * @param announcement 公告内容
     * @return 发布结果
     */
    Result<String> scheduleAnnouncement(AnnouncementDTO announcement);

    /**
     * 获取待发布的公告列表（定时发布）
     */
    Result<List<Announcement>> getPendingAnnouncements();

    /**
     * 获取公告统计数据
     */
    Result<Map<String, Object>> getAnnouncementStats();

    /**
     * 获取公告详情
     */
    Result<Announcement> getAnnouncementById(Long id);

    /**
     * 设置/取消置顶
     */
    Result<String> toggleTop(Long id, Integer isTop);
}
