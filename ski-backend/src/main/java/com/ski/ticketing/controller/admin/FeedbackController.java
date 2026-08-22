package com.ski.ticketing.controller.admin;

import com.ski.ticketing.pojo.entity.Feedback;
import com.ski.ticketing.pojo.vo.PageResultVO;
import com.ski.ticketing.result.Result;
import com.ski.ticketing.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("adminFeedbackController")
@RequestMapping("/admin/feedback")
public class FeedbackController {
    @Autowired
    private FeedbackService service;

    @GetMapping("/{id}")
    public Result<Feedback> getById(@PathVariable Long id) { return Result.success(service.getById(id)); }

    @GetMapping("/page")
    public Result<PageResultVO<Feedback>> page(@RequestParam(defaultValue = "1") int pageNum,
                                               @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(service.getByPage(pageNum, pageSize));
    }

    @PutMapping("/{id}/reply")
    public Result<Feedback> reply(@PathVariable Long id, @RequestParam String reply) {
        return Result.success(service.reply(id, reply));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) { service.delete(id); return Result.success(); }
}
