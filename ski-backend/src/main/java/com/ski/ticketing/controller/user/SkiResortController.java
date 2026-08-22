package com.ski.ticketing.controller.user;

import com.ski.ticketing.pojo.entity.SkiResort;
import com.ski.ticketing.result.Result;
import com.ski.ticketing.service.SkiResortService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("userSkiResortController")
@RequestMapping("/user/resort")
public class SkiResortController {
    @Autowired
    private SkiResortService service;

    @GetMapping("/list")
    public Result<List<SkiResort>> list() { return Result.success(service.getAll()); }

    @GetMapping("/{id}")
    public Result<SkiResort> getById(@PathVariable Long id) { return Result.success(service.getById(id)); }
}
