package com.ski.ticketing.mapper;

import com.ski.ticketing.pojo.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.ski.ticketing.pojo.vo.OrderDetailVO;

import java.util.List;
import java.util.Map;

@Mapper
public interface OrdersMapper {

    Orders getById(Long id);

    Orders getByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

    List<OrderDetailVO> getUserOrderDetails(Long userId);

    OrderDetailVO getUserOrderDetail(@Param("id") Long id, @Param("userId") Long userId);

    List<Orders> search(Map<String, Object> params);

    List<Orders> searchByPage(Map<String, Object> params);

    long count(Map<String, Object> params);

    int insert(Orders order);

    int updateStatus(Map<String, Object> params);

    int pay(Map<String, Object> params);

    int cancel(Map<String, Object> params);

    int completeEquipmentReturn(Map<String, Object> params);
}
