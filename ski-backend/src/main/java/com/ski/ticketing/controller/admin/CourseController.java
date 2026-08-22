package com.ski.ticketing.controller.admin;

import com.ski.ticketing.pojo.entity.Courses;
import com.ski.ticketing.pojo.vo.PageResultVO;
import com.ski.ticketing.result.Result;
import com.ski.ticketing.service.CourseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/course")
@Slf4j
public class CourseController {

    @Autowired
    private CourseService courseService;

    @GetMapping("/{id}")
    public Result<Courses> getById(@PathVariable Long id) {
        Courses course = courseService.getById(id);
        return Result.success(course);
    }

    @GetMapping("/ski-run/{skiRunId}")
    public Result<List<Courses>> getBySkiRunId(@PathVariable Long skiRunId) {
        List<Courses> list = courseService.getBySkiRunId(skiRunId);
        return Result.success(list);
    }

    @GetMapping("/level/{level}")
    public Result<List<Courses>> getByLevel(@PathVariable String level) {
        List<Courses> list = courseService.getByLevel(level);
        return Result.success(list);
    }

    @GetMapping
    public Result<List<Courses>> getAll() {
        List<Courses> list = courseService.getAll();
        return Result.success(list);
    }

    @GetMapping("/page")
    public Result<PageResultVO<Courses>> getByPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageResultVO<Courses> result = courseService.getByPage(pageNum, pageSize);
        return Result.success(result);
    }

    @PostMapping
    public Result<Courses> create(@RequestBody Courses course) {
        Courses created = courseService.create(course);
        return Result.success(created);
    }

    @PutMapping("/{id}")
    public Result<Courses> update(@PathVariable Long id, @RequestBody Courses course) {
        Courses updated = courseService.update(id, course);
        return Result.success(updated);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        courseService.delete(id);
        return Result.success();
    }
}