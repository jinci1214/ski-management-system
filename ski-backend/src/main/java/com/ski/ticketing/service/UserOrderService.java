package com.ski.ticketing.service;

import com.ski.ticketing.contant.OrderStatus;
import com.ski.ticketing.contant.OrderType;
import com.ski.ticketing.mapper.CourseMapper;
import com.ski.ticketing.mapper.EquipmentMapper;
import com.ski.ticketing.mapper.OrdersMapper;
import com.ski.ticketing.pojo.entity.Orders;
import com.ski.ticketing.pojo.vo.OrderDetailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 游客个人订单业务。
 */
@Service
public class UserOrderService {
    private static final Set<String> PAYMENT_METHODS = Set.of("微信", "支付宝", "现金");

    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private EquipmentMapper equipmentMapper;
    @Autowired
    private CourseMapper courseMapper;

    public List<OrderDetailVO> getMyOrders(Long userId) {
        return ordersMapper.getUserOrderDetails(userId);
    }

    public OrderDetailVO getMyOrder(Long userId, Long orderId) {
        OrderDetailVO order = ordersMapper.getUserOrderDetail(orderId, userId);
        if (order == null) throw new IllegalArgumentException("订单不存在");
        return order;
    }

    @Transactional
    public OrderDetailVO pay(Long userId, Long orderId, String paymentMethod) {
        validatePaymentMethod(paymentMethod);
        return payOne(userId, orderId, paymentMethod);
    }

    /** 同一次购买的雪票、雪具订单统一支付，任一订单异常时全部回滚。 */
    @Transactional
    public List<OrderDetailVO> payBatch(Long userId, List<Long> orderIds, String paymentMethod) {
        validatePaymentMethod(paymentMethod);
        return orderIds.stream().distinct()
                .map(orderId -> payOne(userId, orderId, paymentMethod))
                .toList();
    }

    private OrderDetailVO payOne(Long userId, Long orderId, String paymentMethod) {
        Orders order = requireOrder(userId, orderId);
        if (!OrderStatus.PENDING.equals(order.getStatus())) {
            throw new IllegalArgumentException("只有待支付订单可以支付");
        }

        Map<String, Object> params = new HashMap<>();
        params.put("id", orderId);
        params.put("userId", userId);
        params.put("paymentMethod", paymentMethod);
        if (ordersMapper.pay(params) == 0) {
            throw new IllegalArgumentException("订单状态已变化，请刷新后重试");
        }
        return ordersMapper.getUserOrderDetail(orderId, userId);
    }

    private void validatePaymentMethod(String paymentMethod) {
        if (!PAYMENT_METHODS.contains(paymentMethod)) {
            throw new IllegalArgumentException("支付方式只能是微信、支付宝或现金");
        }
    }

    @Transactional
    public OrderDetailVO cancel(Long userId, Long orderId, String cancelReason) {
        Orders order = requireOrder(userId, orderId);
        if (!OrderStatus.PENDING.equals(order.getStatus()) && !OrderStatus.PAID.equals(order.getStatus())) {
            throw new IllegalArgumentException("当前订单状态不能取消");
        }

        String newStatus = OrderStatus.PAID.equals(order.getStatus())
                ? OrderStatus.REFUNDED : OrderStatus.CANCELLED;
        Map<String, Object> params = new HashMap<>();
        params.put("id", orderId);
        params.put("userId", userId);
        params.put("oldStatus", order.getStatus());
        params.put("newStatus", newStatus);
        params.put("cancelReason", normalizeReason(cancelReason));

        if (ordersMapper.cancel(params) == 0) {
            throw new IllegalArgumentException("订单状态已变化，请刷新后重试");
        }
        restoreReservedResource(order);
        return ordersMapper.getUserOrderDetail(orderId, userId);
    }

    @Transactional
    public OrderDetailVO returnEquipment(Long userId, Long orderId) {
        Orders order = requireOrder(userId, orderId);
        if (!OrderType.EQUIPMENT.equals(order.getOrderType())) {
            throw new IllegalArgumentException("该订单不是雪具租赁订单");
        }
        if (!OrderStatus.PAID.equals(order.getStatus())) {
            throw new IllegalArgumentException("只有已支付的雪具订单可以归还");
        }

        Map<String, Object> params = new HashMap<>();
        params.put("id", orderId);
        params.put("userId", userId);
        if (ordersMapper.completeEquipmentReturn(params) == 0) {
            throw new IllegalArgumentException("订单状态已变化，请刷新后重试");
        }
        equipmentMapper.increaseRemaining(order.getEquipmentId().longValue());
        return ordersMapper.getUserOrderDetail(orderId, userId);
    }

    private Orders requireOrder(Long userId, Long orderId) {
        Orders order = ordersMapper.getByIdAndUserId(orderId, userId);
        if (order == null) throw new IllegalArgumentException("订单不存在或不属于当前用户");
        return order;
    }

    private void restoreReservedResource(Orders order) {
        if (OrderType.EQUIPMENT.equals(order.getOrderType()) && order.getEquipmentId() != null) {
            equipmentMapper.increaseRemaining(order.getEquipmentId().longValue());
        } else if (OrderType.COURSE.equals(order.getOrderType()) && order.getCourseId() != null) {
            courseMapper.increaseNumber(order.getCourseId().longValue());
        }
    }

    private String normalizeReason(String reason) {
        if (reason == null || reason.isBlank()) return "用户主动取消";
        String trimmed = reason.trim();
        return trimmed.length() > 200 ? trimmed.substring(0, 200) : trimmed;
    }
}
