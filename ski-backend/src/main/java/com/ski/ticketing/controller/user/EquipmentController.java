package com.ski.ticketing.controller.user;

import com.ski.ticketing.pojo.entity.Equipment;
import com.ski.ticketing.pojo.entity.Orders;
import com.ski.ticketing.result.Result;
import com.ski.ticketing.service.EquipmentService;
import com.ski.ticketing.service.VisitorOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.security.core.Authentication;

@RestController("userEquipmentController")
@RequestMapping("/user/equipment")
@Slf4j
public class EquipmentController {

    @Autowired
    private EquipmentService equipmentService;

    @Autowired
    private VisitorOrderService visitorOrderService;

    @GetMapping("/list")
    public Result<List<Equipment>> list() {
        List<Equipment> list = equipmentService.getAll();
        return Result.success(list);
    }

    @PostMapping("/rent/{equipmentId}")
    public Result<Orders> rentEquipment(@PathVariable Long equipmentId, Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        log.info("用户{}租借雪具{}", userId, equipmentId);
        return Result.success(visitorOrderService.rentEquipment(userId, equipmentId));
    }
}
