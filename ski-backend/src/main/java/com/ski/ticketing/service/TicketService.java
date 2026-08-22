package com.ski.ticketing.service;

import com.ski.ticketing.pojo.dto.TicketCreateDTO;
import com.ski.ticketing.pojo.entity.Ticket;
import com.ski.ticketing.pojo.vo.PageResultVO;

import java.util.List;

public interface TicketService {

    Ticket getById(Long id);

    List<Ticket> getAll();

    PageResultVO<Ticket> getByPage(int pageNum, int pageSize);

    List<Ticket> getByStatus(Integer status);

    List<Ticket> getBySkiRunId(Long skiRunId);

    Ticket create(TicketCreateDTO dto);

    Ticket update(Long id, String name, Integer skiRunId, Integer hour, String description);

    Ticket updateStatus(Long id, Integer status);

    void delete(Long id);
}