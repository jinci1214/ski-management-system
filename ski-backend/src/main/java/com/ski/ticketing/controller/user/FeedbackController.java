package com.ski.ticketing.controller.user;

import com.ski.ticketing.pojo.entity.Feedback;
import com.ski.ticketing.result.Result;
import com.ski.ticketing.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("userFeedbackController")
@RequestMapping("/user/feedback")
public class FeedbackController {
    @Autowired
    private FeedbackService service;

    @PostMapping
    public Result<Feedback> create(Authentication authentication, @RequestBody Feedback feedback) {
        feedback.setUserId(((Long) authentication.getPrincipal()).intValue());
        return Result.success(service.create(feedback));
    }

    @GetMapping("/my")
    public Result<List<Feedback>> myFeedback(Authentication authentication) {
        return Result.success(service.getByUserId((Long) authentication.getPrincipal()));
    }

    @PutMapping("/my/read")
    public Result<Void> markRepliesRead(Authentication authentication) {
        service.markRepliesRead((Long) authentication.getPrincipal());
        return Result.success();
    }
}
