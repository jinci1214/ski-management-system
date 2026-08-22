package com.ski.ticketing.controller.user;

import com.ski.ticketing.pojo.entity.Announcements;
import com.ski.ticketing.result.Result;
import com.ski.ticketing.service.AnnouncementsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("userAnnouncementsController")
@RequestMapping("/user/announcement")
@Slf4j
public class AnnouncementsController {

    @Autowired
    private AnnouncementsService announcementsService;

    @GetMapping("/list")
    public Result<List<Announcements>> list() {
        List<Announcements> list = announcementsService.getAll();
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<Announcements> getById(@PathVariable Long id) {
        Announcements announcements = announcementsService.getById(id);
        return Result.success(announcements);
    }
}