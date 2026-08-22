package com.ski.ticketing.controller.user;

import com.ski.ticketing.pojo.entity.SkiRun;
import com.ski.ticketing.result.Result;
import com.ski.ticketing.service.SkiRunService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("userSkiRunController")
@RequestMapping("/user/ski-run")
@Slf4j
public class SkiRunController {

    @Autowired
    private SkiRunService skiRunService;

    @GetMapping("/list")
    public Result<List<SkiRun>> list() {
        List<SkiRun> list = skiRunService.getAll();
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<SkiRun> getById(@PathVariable Long id) {
        SkiRun skiRun = skiRunService.getById(id);
        return Result.success(skiRun);
    }
}