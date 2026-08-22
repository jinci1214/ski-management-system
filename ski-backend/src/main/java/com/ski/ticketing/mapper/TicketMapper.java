package com.ski.ticketing.mapper;

import com.ski.ticketing.pojo.entity.Ticket;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface TicketMapper {

    Ticket getById(Long id);

    List<Ticket> getAll();

    List<Ticket> getByPage(int offset, int pageSize);

    long count();

    List<Ticket> getByStatus(Integer status);

    List<Ticket> getBySkiRunId(Long skiRunId);

    int insert(Ticket ticket);

    int update(Ticket ticket);

    int updateStatus(Map<String, Object> params);

    int delete(Long id);
}