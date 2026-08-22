package com.ski.ticketing.controller.admin;

import com.ski.ticketing.pojo.entity.MaintenanceRecord;
import com.ski.ticketing.pojo.vo.PageResultVO;
import com.ski.ticketing.result.Result;
import com.ski.ticketing.service.MaintenanceRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/maintenance")
public class MaintenanceRecordController {
    @Autowired
    private MaintenanceRecordService service;

    @GetMapping("/{id}")
    public Result<MaintenanceRecord> getById(@PathVariable Long id) { return Result.success(service.getById(id)); }

    @GetMapping("/page")
    public Result<PageResultVO<MaintenanceRecord>> page(@RequestParam(defaultValue = "1") int pageNum,
                                                        @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(service.getByPage(pageNum, pageSize));
    }

    @PostMapping
    public Result<MaintenanceRecord> create(@RequestBody MaintenanceRecord record) {
        return Result.success(service.create(record));
    }

    @PutMapping("/{id}")
    public Result<MaintenanceRecord> update(@PathVariable Long id, @RequestBody MaintenanceRecord record) {
        return Result.success(service.update(id, record));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) { service.delete(id); return Result.success(); }
}
