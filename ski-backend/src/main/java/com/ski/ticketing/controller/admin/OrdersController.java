package com.ski.ticketing.controller.admin;

import com.ski.ticketing.pojo.entity.Orders;
import com.ski.ticketing.pojo.vo.PageResultVO;
import com.ski.ticketing.result.Result;
import com.ski.ticketing.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/orders")
@Slf4j
public class OrdersController {

    @Autowired
    private OrdersService ordersService;

    @GetMapping("/{id}")
    public Result<Orders> getById(@PathVariable Long id) {
        Orders order = ordersService.getById(id);
        return Result.success(order);
    }

    @GetMapping("/search")
    public Result<List<Orders>> search(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String orderId,
            @RequestParam(required = false) String orderType,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) String status) {

        Map<String, Object> params = new HashMap<>();
        if (id != null)
            params.put("id", id);
        if (orderId != null && !orderId.isEmpty())
            params.put("orderId", orderId);
        if (orderType != null && !orderType.isEmpty())
            params.put("orderType", orderType);
        if (userId != null)
            params.put("userId", userId);
        if (status != null && !status.isEmpty())
            params.put("status", status);

        List<Orders> list = ordersService.search(params);
        return Result.success(list);
    }

    @GetMapping("/page")
    public Result<PageResultVO<Orders>> searchByPage(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String orderId,
            @RequestParam(required = false) String orderType,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {

        Map<String, Object> params = new HashMap<>();
        if (id != null)
            params.put("id", id);
        if (orderId != null && !orderId.isEmpty())
            params.put("orderId", orderId);
        if (orderType != null && !orderType.isEmpty())
            params.put("orderType", orderType);
        if (userId != null)
            params.put("userId", userId);
        if (status != null && !status.isEmpty())
            params.put("status", status);

        PageResultVO<Orders> result = ordersService.searchByPage(params, pageNum, pageSize);
        return Result.success(result);
    }

    @PutMapping("/{id}/status")
    public Result<Orders> updateStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(required = false) String cancelReason) {
        Orders order = ordersService.updateStatus(id, status, cancelReason);
        return Result.success(order);
    }
}