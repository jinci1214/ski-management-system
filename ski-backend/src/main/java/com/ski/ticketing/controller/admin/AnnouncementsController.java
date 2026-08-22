package com.ski.ticketing.controller.admin;

import com.ski.ticketing.pojo.entity.Announcements;
import com.ski.ticketing.pojo.vo.PageResultVO;
import com.ski.ticketing.result.Result;
import com.ski.ticketing.service.AnnouncementsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/announcement")
@Slf4j
public class AnnouncementsController {

    @Autowired
    private AnnouncementsService announcementsService;

    /*根据id查公告*/
    @GetMapping("/{id}")
    public Result<Announcements> getById(@PathVariable Long id) {
        Announcements announcement = announcementsService.getById(id);
        return Result.success(announcement);
    }

    /*根据分类查公告，可模糊搜索*/
    @GetMapping("/search/category")
    public Result<List<Announcements>> searchByCategory(@RequestParam String keyword) {
        List<Announcements> list = announcementsService.searchByCategory(keyword);
        return Result.success(list);
    }

    /*根据标题查公告，可模糊搜索*/
    @GetMapping("/search/title")
    public Result<List<Announcements>> searchByTitle(@RequestParam String keyword) {
        List<Announcements> list = announcementsService.searchByTitle(keyword);
        return Result.success(list);
    }
    /*根据内容查公告，可模糊搜索*/
    @GetMapping("/search/content")
    public Result<List<Announcements>> searchByContent(@RequestParam String keyword) {
        List<Announcements> list = announcementsService.searchByContent(keyword);
        return Result.success(list);
    }

    /*根据月份或日期，查公告*/
    @GetMapping("/date")
    public Result<List<Announcements>> getByPublishDate(@RequestParam String date) {
        List<Announcements> list = announcementsService.getByPublishDate(date);
        return Result.success(list);
    }

    /*根据页码查*/
    @GetMapping("/page")
    public Result<PageResultVO<Announcements>> getByPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageResultVO<Announcements> result = announcementsService.getByPage(pageNum, pageSize);
        return Result.success(result);
    }

    /*创建公告*/
    @PostMapping
    public Result<Announcements> create(@RequestBody Announcements announcements) {
        Announcements created = announcementsService.create(announcements);
        return Result.success(created);
    }

    /*根据id修改*/
    @PutMapping("/{id}")
    public Result<Announcements> update(@PathVariable Long id, @RequestBody Announcements announcements) {
        Announcements updated = announcementsService.update(id, announcements);
        return Result.success(updated);
    }

    /*根据id删除*/
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        announcementsService.delete(id);
        return Result.success();
    }
}
