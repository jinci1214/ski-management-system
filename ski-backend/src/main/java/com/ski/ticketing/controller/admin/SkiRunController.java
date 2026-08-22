package com.ski.ticketing.controller.admin;

import com.ski.ticketing.pojo.entity.SkiRun;
import com.ski.ticketing.pojo.vo.PageResultVO;
import com.ski.ticketing.result.Result;
import com.ski.ticketing.service.SkiRunService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/admin/ski-run")
@Slf4j
public class SkiRunController {

    @Autowired
    private SkiRunService skiRunService;

    @GetMapping("/{id}")
    public Result<SkiRun> getById(@PathVariable Long id) {
        SkiRun skiRun = skiRunService.getById(id);
        return Result.success(skiRun);
    }

    @GetMapping("/level/{level}")
    public Result<List<SkiRun>> getByLevel(@PathVariable String level) {
        List<SkiRun> list = skiRunService.getByLevel(level);
        return Result.success(list);
    }

    @GetMapping("/page")
    public Result<PageResultVO<SkiRun>> getByPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageResultVO<SkiRun> result = skiRunService.getByPage(pageNum, pageSize);
        return Result.success(result);
    }

    @PostMapping
    public Result<SkiRun> create(
            @RequestParam Integer resortId,
            @RequestParam String name,
            @RequestParam String level,
            @RequestParam BigDecimal length,
            @RequestParam BigDecimal height,
            @RequestParam(required = false) MultipartFile image) throws IOException {
        SkiRun created = skiRunService.create(resortId, name, level, length, height, image);
        return Result.success(created);
    }

    @PutMapping("/{id}")
    public Result<SkiRun> update(
            @PathVariable Long id,
            @RequestParam Integer resortId,
            @RequestParam String name,
            @RequestParam String level,
            @RequestParam BigDecimal length,
            @RequestParam BigDecimal height,
            @RequestParam(required = false) MultipartFile image) throws IOException {
        SkiRun updated = skiRunService.update(id, resortId, name, level, length, height, image);
        return Result.success(updated);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        skiRunService.delete(id);
        return Result.success();
    }
}
