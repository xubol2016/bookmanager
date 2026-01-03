package top.xubol.bookmanager.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.xubol.bookmanager.common.RequireRole;
import top.xubol.bookmanager.common.Result;
import top.xubol.bookmanager.dto.AnnouncementDTO;
import top.xubol.bookmanager.entity.Announcement;
import top.xubol.bookmanager.service.AnnouncementService;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    @Autowired
    private HttpServletRequest request;

    /**
     * 获取首页公告列表（只返回已发布的公告）
     */
    @GetMapping("/announcements/top")
    public Result<List<Announcement>> getTopAnnouncements(@RequestParam(defaultValue = "5") int limit) {
        return announcementService.getTopAnnouncements(limit);
    }

    /**
     * 分页查询已发布的公告（普通用户）
     */
    @GetMapping("/announcements")
    public Result<Page<Announcement>> getPublishedAnnouncements(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        return announcementService.getPublishedAnnouncementPage(page, size, keyword);
    }

    /**
     * 获取公告详情
     */
    @GetMapping("/announcements/{id}")
    public Result<Announcement> getAnnouncementDetail(@PathVariable Long id) {
        return announcementService.getAnnouncementById(id);
    }

    /**
     * 分页查询所有公告（管理员，包含待发布）
     */
    @GetMapping("/admin/announcements")
    @RequireRole("admin")
    public Result<Page<Announcement>> getAnnouncementPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        return announcementService.getAnnouncementPage(page, size, keyword);
    }

    /**
     * 获取待发布的公告列表
     */
    @GetMapping("/admin/announcements/pending")
    @RequireRole("admin")
    public Result<List<Announcement>> getPendingAnnouncements() {
        return announcementService.getPendingAnnouncements();
    }

    /**
     * 获取公告统计数据
     */
    @GetMapping("/admin/announcements/stats")
    @RequireRole("admin")
    public Result<Map<String, Object>> getAnnouncementStats() {
        return announcementService.getAnnouncementStats();
    }

    /**
     * 发布公告（支持立即发布）
     */
    @PostMapping("/admin/announcements")
    @RequireRole("admin")
    public Result<String> addAnnouncement(@RequestBody Announcement announcement) {
        // 设置管理员ID（简化处理，实际应从token中获取用户ID）
        announcement.setAdminId(1L);
        return announcementService.publishAnnouncement(announcement);
    }

    /**
     * 定时发布公告
     */
    @PostMapping("/admin/announcements/schedule")
    @RequireRole("admin")
    public Result<String> scheduleAnnouncement(@RequestBody AnnouncementDTO announcementDTO) {
        return announcementService.scheduleAnnouncement(announcementDTO);
    }

    /**
     * 更新公告
     */
    @PutMapping("/admin/announcements")
    @RequireRole("admin")
    public Result<String> updateAnnouncement(@RequestBody Announcement announcement) {
        if (announcementService.updateById(announcement)) {
            return Result.success("更新成功");
        }
        return Result.failed("更新失败");
    }

    /**
     * 设置/取消置顶
     */
    @PutMapping("/admin/announcements/{id}/top")
    @RequireRole("admin")
    public Result<String> toggleTop(@PathVariable Long id, @RequestParam Integer isTop) {
        return announcementService.toggleTop(id, isTop);
    }

    /**
     * 删除公告
     */
    @DeleteMapping("/admin/announcements/{id}")
    @RequireRole("admin")
    public Result<String> deleteAnnouncement(@PathVariable Long id) {
        if (announcementService.removeById(id)) {
            return Result.success("删除成功");
        }
        return Result.failed("删除失败");
    }
}
