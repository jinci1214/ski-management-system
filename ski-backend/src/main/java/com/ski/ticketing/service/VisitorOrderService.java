package com.ski.ticketing.service;

import com.ski.ticketing.contant.OrderStatus;
import com.ski.ticketing.contant.OrderType;
import com.ski.ticketing.mapper.CourseMapper;
import com.ski.ticketing.mapper.EquipmentMapper;
import com.ski.ticketing.mapper.OrdersMapper;
import com.ski.ticketing.mapper.TicketMapper;
import com.ski.ticketing.pojo.entity.Courses;
import com.ski.ticketing.pojo.entity.Equipment;
import com.ski.ticketing.pojo.entity.Orders;
import com.ski.ticketing.pojo.entity.Ticket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 游客下单服务。库存扣减和订单写入在同一事务中完成。
 */
@Service
public class VisitorOrderService {
    @Autowired
    private TicketMapper ticketMapper;
    @Autowired
    private EquipmentMapper equipmentMapper;
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private OrdersMapper ordersMapper;

    @Transactional
    public Orders buyTicket(Long userId, Long ticketId) {
        Ticket ticket = ticketMapper.getById(ticketId);
        if (ticket == null) throw new IllegalArgumentException("雪票不存在");
        if (ticket.getStatus() == 0) throw new IllegalArgumentException("雪票已禁用，无法购买");

        Orders order = newOrder(userId, OrderType.TICKET, ticket.getPrice());
        order.setTicketId(ticketId.intValue());
        ordersMapper.insert(order);
        return order;
    }

    @Transactional
    public Orders rentEquipment(Long userId, Long equipmentId) {
        Equipment equipment = equipmentMapper.getById(equipmentId);
        if (equipment == null) throw new IllegalArgumentException("雪具不存在");
        if (equipmentMapper.decreaseRemaining(equipmentId) == 0) {
            throw new IllegalArgumentException("雪具已租完，无法租借");
        }

        Orders order = newOrder(userId, OrderType.EQUIPMENT, equipment.getPrice());
        order.setEquipmentId(equipmentId.intValue());
        ordersMapper.insert(order);
        return order;
    }

    /**
     * 雪票和加购雪具同时下单。任意雪具库存不足时，整个事务都会回滚。
     */
    @Transactional
    public List<Orders> buyTicketWithEquipment(Long userId, Long ticketId, List<Long> equipmentIds) {
        List<Orders> orders = new ArrayList<>();
        orders.add(buyTicket(userId, ticketId));
        if (equipmentIds != null) {
            for (Long equipmentId : equipmentIds.stream().distinct().toList()) {
                orders.add(rentEquipment(userId, equipmentId));
            }
        }
        return orders;
    }

    @Transactional
    public Orders joinCourse(Long userId, Long courseId) {
        Courses course = courseMapper.getById(courseId);
        if (course == null) throw new IllegalArgumentException("课程不存在");
        if (courseMapper.decreaseNumber(courseId) == 0) {
            throw new IllegalArgumentException("课程已满，无法加入");
        }

        Orders order = newOrder(userId, OrderType.COURSE, course.getPrice());
        order.setCourseId(courseId.intValue());
        ordersMapper.insert(order);
        return order;
    }

    private Orders newOrder(Long userId, String orderType, java.math.BigDecimal amount) {
        Orders order = new Orders();
        order.setOrderId(UUID.randomUUID().toString().replace("-", ""));
        order.setUserId(userId.intValue());
        order.setOrderType(orderType);
        order.setAmount(amount);
        order.setStatus(OrderStatus.PENDING);
        order.setCreateTime(LocalDateTime.now());
        return order;
    }
}
