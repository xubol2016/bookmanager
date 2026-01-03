package top.xubol.bookmanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import top.xubol.bookmanager.common.Result;
import top.xubol.bookmanager.dto.AnnouncementDTO;
import top.xubol.bookmanager.entity.Announcement;
import top.xubol.bookmanager.mapper.AnnouncementMapper;
import top.xubol.bookmanager.service.AnnouncementService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, Announcement> implements AnnouncementService {

    @Override
    public Result<List<Announcement>> getTopAnnouncements(int limit) {
        // 只查询已发布的公告（publish_time <= 当前时间）
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        wrapper.le(Announcement::getPublishTime, LocalDateTime.now())
               .orderByDesc(Announcement::getIsTop)
               .orderByDesc(Announcement::getPublishTime)
               .last("LIMIT " + limit);
        return Result.success(this.list(wrapper));
    }

    @Override
    public Result<Page<Announcement>> getAnnouncementPage(int page, int size, String keyword) {
        // 管理员查询所有公告（包含待发布的）
        Page<Announcement> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Announcement::getTitle, keyword)
                   .or()
                   .like(Announcement::getContent, keyword);
        }
        wrapper.orderByDesc(Announcement::getIsTop)
               .orderByDesc(Announcement::getPublishTime);
        return Result.success(this.page(pageParam, wrapper));
    }

    @Override
    public Result<Page<Announcement>> getPublishedAnnouncementPage(int page, int size, String keyword) {
        // 普通用户只查询已发布的公告
        Page<Announcement> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        wrapper.le(Announcement::getPublishTime, LocalDateTime.now());
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w
                    .like(Announcement::getTitle, keyword)
                    .or()
                    .like(Announcement::getContent, keyword)
            );
        }
        wrapper.orderByDesc(Announcement::getIsTop)
               .orderByDesc(Announcement::getPublishTime);
        return Result.success(this.page(pageParam, wrapper));
    }

    @Override
    public Result<String> publishAnnouncement(Announcement announcement) {
        if (announcement.getPublishTime() == null) {
            announcement.setPublishTime(LocalDateTime.now());
        }
        if (announcement.getIsTop() == null) {
            announcement.setIsTop(0);
        }
        this.save(announcement);

        if (announcement.getPublishTime().isAfter(LocalDateTime.now())) {
            return Result.success("公告已设置定时发布，发布时间：" + announcement.getPublishTime());
        }
        return Result.success("发布成功");
    }

    @Override
    public Result<String> scheduleAnnouncement(AnnouncementDTO dto) {
        Announcement announcement = new Announcement();
        announcement.setTitle(dto.getTitle());
        announcement.setContent(dto.getContent());
        announcement.setIsTop(dto.getIsTop() != null ? dto.getIsTop() : 0);

        // 设置定时发布时间
        if (dto.getScheduledPublishTime() != null) {
            announcement.setPublishTime(dto.getScheduledPublishTime());
        } else {
            announcement.setPublishTime(LocalDateTime.now());
        }

        this.save(announcement);

        if (announcement.getPublishTime().isAfter(LocalDateTime.now())) {
            return Result.success("公告已设置定时发布，发布时间：" + announcement.getPublishTime());
        }
        return Result.success("发布成功");
    }

    @Override
    public Result<List<Announcement>> getPendingAnnouncements() {
        // 查询待发布的公告（publish_time > 当前时间）
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        wrapper.gt(Announcement::getPublishTime, LocalDateTime.now())
               .orderByAsc(Announcement::getPublishTime);
        return Result.success(this.list(wrapper));
    }

    @Override
    public Result<Map<String, Object>> getAnnouncementStats() {
        Map<String, Object> stats = new HashMap<>();

        LocalDateTime now = LocalDateTime.now();

        // 公告总数
        long totalCount = this.count();
        stats.put("totalCount", totalCount);

        // 已发布数量
        long publishedCount = this.count(new LambdaQueryWrapper<Announcement>()
                .le(Announcement::getPublishTime, now));
        stats.put("publishedCount", publishedCount);

        // 待发布数量
        long pendingCount = this.count(new LambdaQueryWrapper<Announcement>()
                .gt(Announcement::getPublishTime, now));
        stats.put("pendingCount", pendingCount);

        // 置顶数量
        long topCount = this.count(new LambdaQueryWrapper<Announcement>()
                .eq(Announcement::getIsTop, 1));
        stats.put("topCount", topCount);

        return Result.success(stats);
    }

    @Override
    public Result<Announcement> getAnnouncementById(Long id) {
        Announcement announcement = this.getById(id);
        if (announcement == null) {
            return Result.failed("公告不存在");
        }
        return Result.success(announcement);
    }

    @Override
    public Result<String> toggleTop(Long id, Integer isTop) {
        Announcement announcement = this.getById(id);
        if (announcement == null) {
            return Result.failed("公告不存在");
        }
        announcement.setIsTop(isTop);
        this.updateById(announcement);
        return Result.success(isTop == 1 ? "置顶成功" : "取消置顶成功");
    }
}
