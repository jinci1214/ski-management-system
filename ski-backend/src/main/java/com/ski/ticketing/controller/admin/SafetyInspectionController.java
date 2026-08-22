package com.ski.ticketing.controller.admin;

import com.ski.ticketing.pojo.entity.SafetyInspection;
import com.ski.ticketing.pojo.vo.PageResultVO;
import com.ski.ticketing.result.Result;
import com.ski.ticketing.service.SafetyInspectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/safety-inspection")
public class SafetyInspectionController {
    @Autowired
    private SafetyInspectionService service;

    @GetMapping("/{id}")
    public Result<SafetyInspection> getById(@PathVariable Long id) { return Result.success(service.getById(id)); }

    @GetMapping("/page")
    public Result<PageResultVO<SafetyInspection>> page(@RequestParam(defaultValue = "1") int pageNum,
                                                       @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(service.getByPage(pageNum, pageSize));
    }

    @PostMapping
    public Result<SafetyInspection> create(@RequestBody SafetyInspection inspection) {
        return Result.success(service.create(inspection));
    }

    @PutMapping("/{id}")
    public Result<SafetyInspection> update(@PathVariable Long id, @RequestBody SafetyInspection inspection) {
        return Result.success(service.update(id, inspection));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) { service.delete(id); return Result.success(); }
}
