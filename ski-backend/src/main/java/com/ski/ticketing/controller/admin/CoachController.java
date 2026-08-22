package com.ski.ticketing.controller.admin;

import com.ski.ticketing.pojo.entity.Coach;
import com.ski.ticketing.pojo.vo.PageResultVO;
import com.ski.ticketing.result.Result;
import com.ski.ticketing.service.CoachService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("adminCoachController")
@RequestMapping("/admin/coach")
public class CoachController {
    @Autowired
    private CoachService service;

    @GetMapping("/{id}")
    public Result<Coach> getById(@PathVariable Long id) { return Result.success(service.getById(id)); }

    @GetMapping("/page")
    public Result<PageResultVO<Coach>> page(@RequestParam(defaultValue = "1") int pageNum,
                                            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(service.getByPage(pageNum, pageSize));
    }

    @PostMapping
    public Result<Coach> create(@RequestBody Coach coach) { return Result.success(service.create(coach)); }

    @PutMapping("/{id}")
    public Result<Coach> update(@PathVariable Long id, @RequestBody Coach coach) {
        return Result.success(service.update(id, coach));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) { service.delete(id); return Result.success(); }
}
