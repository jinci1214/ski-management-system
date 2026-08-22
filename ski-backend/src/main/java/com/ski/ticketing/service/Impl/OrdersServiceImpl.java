package com.ski.ticketing.service.Impl;

import com.ski.ticketing.pojo.entity.Orders;
import com.ski.ticketing.pojo.vo.PageResultVO;
import com.ski.ticketing.service.OrdersService;
import com.ski.ticketing.mapper.OrdersMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrdersServiceImpl implements OrdersService {

    @Autowired
    private OrdersMapper ordersMapper;

    @Override
    public Orders getById(Long id) {
        return ordersMapper.getById(id);
    }

    @Override
    public List<Orders> search(Map<String, Object> params) {
        return ordersMapper.search(params);
    }

    @Override
    public PageResultVO<Orders> searchByPage(Map<String, Object> params, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        params.put("offset", offset);
        params.put("pageSize", pageSize);

        List<Orders> list = ordersMapper.searchByPage(params);
        long total = ordersMapper.count(params);

        PageResultVO<Orders> result = new PageResultVO<>();
        result.setData(list);
        result.setTotal(total);
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);

        return result;
    }

    @Override
    public Orders create(Orders order) {
        order.setCreateTime(LocalDateTime.now());
        ordersMapper.insert(order);
        return order;
    }

    @Override
    public Orders updateStatus(Long id, String status, String cancelReason) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("status", status);

        if (cancelReason != null && !cancelReason.isEmpty()) {
            params.put("cancelReason", cancelReason);
        }

        if ("已退款".equals(status) || "已取消".equals(status)) {
            params.put("endTime", LocalDateTime.now());
        }

        ordersMapper.updateStatus(params);
        return ordersMapper.getById(id);
    }
}