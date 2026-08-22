package com.ski.ticketing.service;

import com.ski.ticketing.contant.OrderStatus;
import com.ski.ticketing.mapper.CourseMapper;
import com.ski.ticketing.mapper.EquipmentMapper;
import com.ski.ticketing.pojo.entity.Courses;
import com.ski.ticketing.pojo.entity.Equipment;
import com.ski.ticketing.pojo.entity.Orders;
import com.ski.ticketing.pojo.vo.OrderDetailVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@Transactional
class UserOrderServiceTests {
    @Autowired
    private VisitorOrderService visitorOrderService;
    @Autowired
    private UserOrderService userOrderService;
    @Autowired
    private EquipmentMapper equipmentMapper;
    @Autowired
    private CourseMapper courseMapper;

    @Test
    void equipmentOrderCanBePaidAndReturned() {
        Equipment before = equipmentMapper.getById(1L);

        Orders created = visitorOrderService.rentEquipment(1L, 1L);
        assertEquals(before.getRemaining() - 1, equipmentMapper.getById(1L).getRemaining());

        OrderDetailVO paid = userOrderService.pay(1L, created.getId().longValue(), "微信");
        assertEquals(OrderStatus.PAID, paid.getStatus());

        OrderDetailVO completed = userOrderService.returnEquipment(1L, created.getId().longValue());
        assertEquals(OrderStatus.COMPLETED, completed.getStatus());
        assertEquals(before.getRemaining(), equipmentMapper.getById(1L).getRemaining());
    }

    @Test
    void cancellingCourseOrderRestoresAvailablePlaces() {
        Courses before = courseMapper.getById(1L);

        Orders created = visitorOrderService.joinCourse(1L, 1L);
        assertEquals(before.getNumber() - 1, courseMapper.getById(1L).getNumber());

        OrderDetailVO cancelled = userOrderService.cancel(
                1L, created.getId().longValue(), "临时改变行程");
        assertEquals(OrderStatus.CANCELLED, cancelled.getStatus());
        assertEquals(before.getNumber(), courseMapper.getById(1L).getNumber());
        assertFalse(userOrderService.getMyOrders(1L).isEmpty());
    }
}
