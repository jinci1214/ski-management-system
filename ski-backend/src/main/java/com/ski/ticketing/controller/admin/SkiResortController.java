package com.ski.ticketing.controller.admin;

import com.ski.ticketing.pojo.entity.SkiResort;
import com.ski.ticketing.pojo.vo.PageResultVO;
import com.ski.ticketing.result.Result;
import com.ski.ticketing.service.SkiResortService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("adminSkiResortController")
@RequestMapping("/admin/resort")
public class SkiResortController {
    @Autowired
    private SkiResortService service;

    @GetMapping("/{id}")
    public Result<SkiResort> getById(@PathVariable Long id) { return Result.success(service.getById(id)); }

    @GetMapping("/page")
    public Result<PageResultVO<SkiResort>> page(@RequestParam(defaultValue = "1") int pageNum,
                                                @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(service.getByPage(pageNum, pageSize));
    }

    @PostMapping
    public Result<SkiResort> create(@RequestBody SkiResort resort) { return Result.success(service.create(resort)); }

    @PutMapping("/{id}")
    public Result<SkiResort> update(@PathVariable Long id, @RequestBody SkiResort resort) {
        return Result.success(service.update(id, resort));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) { service.delete(id); return Result.success(); }
}
