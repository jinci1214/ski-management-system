package com.ski.ticketing.controller.user;

import com.ski.ticketing.pojo.dto.OrderBatchPaymentDTO;
import com.ski.ticketing.pojo.vo.OrderDetailVO;
import com.ski.ticketing.result.Result;
import com.ski.ticketing.service.UserOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import jakarta.validation.Valid;

@RestController("userOrdersController")
@RequestMapping("/user/orders")
public class OrdersController {
    @Autowired
    private UserOrderService userOrderService;

    @GetMapping("/my")
    public Result<List<OrderDetailVO>> getMyOrders(Authentication authentication) {
        return Result.success(userOrderService.getMyOrders(currentUserId(authentication)));
    }

    @GetMapping("/{id}")
    public Result<OrderDetailVO> getMyOrder(@PathVariable Long id, Authentication authentication) {
        return Result.success(userOrderService.getMyOrder(currentUserId(authentication), id));
    }

    @PutMapping("/{id}/pay")
    public Result<OrderDetailVO> pay(@PathVariable Long id,
                                     @RequestParam(defaultValue = "微信") String paymentMethod,
                                     Authentication authentication) {
        return Result.success(userOrderService.pay(currentUserId(authentication), id, paymentMethod));
    }

    @PutMapping("/pay-batch")
    public Result<List<OrderDetailVO>> payBatch(@Valid @RequestBody OrderBatchPaymentDTO dto,
                                                  Authentication authentication) {
        return Result.success(userOrderService.payBatch(currentUserId(authentication), dto.getOrderIds(), dto.getPaymentMethod()));
    }

    @PutMapping("/{id}/cancel")
    public Result<OrderDetailVO> cancel(@PathVariable Long id,
                                        @RequestParam(required = false) String cancelReason,
                                        Authentication authentication) {
        return Result.success(userOrderService.cancel(currentUserId(authentication), id, cancelReason));
    }

    @PutMapping("/{id}/return")
    public Result<OrderDetailVO> returnEquipment(@PathVariable Long id, Authentication authentication) {
        return Result.success(userOrderService.returnEquipment(currentUserId(authentication), id));
    }

    private Long currentUserId(Authentication authentication) {
        return (Long) authentication.getPrincipal();
    }
}
