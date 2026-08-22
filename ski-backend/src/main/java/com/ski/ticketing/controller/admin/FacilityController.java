package com.ski.ticketing.controller.admin;

import com.ski.ticketing.pojo.entity.Facility;
import com.ski.ticketing.pojo.vo.PageResultVO;
import com.ski.ticketing.result.Result;
import com.ski.ticketing.service.FacilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("adminFacilityController")
@RequestMapping("/admin/facility")
public class FacilityController {
    @Autowired
    private FacilityService service;

    @GetMapping("/{id}")
    public Result<Facility> getById(@PathVariable Long id) { return Result.success(service.getById(id)); }

    @GetMapping("/page")
    public Result<PageResultVO<Facility>> page(@RequestParam(defaultValue = "1") int pageNum,
                                               @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(service.getByPage(pageNum, pageSize));
    }

    @PostMapping
    public Result<Facility> create(@RequestBody Facility facility) { return Result.success(service.create(facility)); }

    @PutMapping("/{id}")
    public Result<Facility> update(@PathVariable Long id, @RequestBody Facility facility) {
        return Result.success(service.update(id, facility));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) { service.delete(id); return Result.success(); }
}
