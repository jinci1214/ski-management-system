package com.ski.ticketing.controller.admin;

import com.ski.ticketing.pojo.entity.Equipment;
import com.ski.ticketing.pojo.vo.PageResultVO;
import com.ski.ticketing.result.Result;
import com.ski.ticketing.service.EquipmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/equipment")
@Slf4j
public class EquipmentController {

    @Autowired
    private EquipmentService equipmentService;

    @GetMapping("/{id}")
    public Result<Equipment> getById(@PathVariable Long id) {
        Equipment equipment = equipmentService.getById(id);
        return Result.success(equipment);
    }

    @GetMapping
    public Result<List<Equipment>> getAll() {
        List<Equipment> list = equipmentService.getAll();
        return Result.success(list);
    }

    @GetMapping("/page")
    public Result<PageResultVO<Equipment>> getByPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageResultVO<Equipment> result = equipmentService.getByPage(pageNum, pageSize);
        return Result.success(result);
    }

    @PostMapping
    public Result<Equipment> create(@RequestBody Equipment equipment) {
        Equipment created = equipmentService.create(equipment);
        return Result.success(created);
    }

    @PutMapping("/{id}")
    public Result<Equipment> update(@PathVariable Long id, @RequestBody Equipment equipment) {
        Equipment updated = equipmentService.update(id, equipment);
        return Result.success(updated);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        equipmentService.delete(id);
        return Result.success();
    }
}