package com.ski.ticketing.service;

import com.ski.ticketing.pojo.entity.Orders;
import com.ski.ticketing.pojo.vo.PageResultVO;

import java.util.List;
import java.util.Map;

public interface OrdersService {

    Orders getById(Long id);

    List<Orders> search(Map<String, Object> params);

    PageResultVO<Orders> searchByPage(Map<String, Object> params, int pageNum, int pageSize);

    Orders create(Orders order);

    Orders updateStatus(Long id, String status, String cancelReason);
}