package com.ski.ticketing.controller.user;

import com.ski.ticketing.pojo.entity.Coach;
import com.ski.ticketing.result.Result;
import com.ski.ticketing.service.CoachService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("userCoachController")
@RequestMapping("/user/coach")
public class CoachController {
    @Autowired
    private CoachService service;

    @GetMapping("/list")
    public Result<List<Coach>> list() { return Result.success(service.getAll()); }

    @GetMapping("/{id}")
    public Result<Coach> getById(@PathVariable Long id) { return Result.success(service.getById(id)); }
}
