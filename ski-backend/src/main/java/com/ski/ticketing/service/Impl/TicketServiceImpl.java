package com.ski.ticketing.service.Impl;

import com.ski.ticketing.pojo.dto.TicketCreateDTO;
import com.ski.ticketing.pojo.entity.Ticket;
import com.ski.ticketing.pojo.vo.PageResultVO;
import com.ski.ticketing.service.TicketService;
import com.ski.ticketing.mapper.TicketMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TicketServiceImpl implements TicketService {

    @Autowired
    private TicketMapper ticketMapper;

    @Override
    public Ticket getById(Long id) {
        return ticketMapper.getById(id);
    }

    @Override
    public List<Ticket> getAll() {
        return ticketMapper.getAll();
    }

    @Override
    public PageResultVO<Ticket> getByPage(int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<Ticket> list = ticketMapper.getByPage(offset, pageSize);
        long total = ticketMapper.count();

        PageResultVO<Ticket> result = new PageResultVO<>();
        result.setData(list);
        result.setTotal(total);
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);

        return result;
    }

    @Override
    public List<Ticket> getByStatus(Integer status) {
        return ticketMapper.getByStatus(status);
    }

    @Override
    public List<Ticket> getBySkiRunId(Long skiRunId) {
        return ticketMapper.getBySkiRunId(skiRunId);
    }

    @Override
    public Ticket create(TicketCreateDTO dto) {
        Ticket ticket = new Ticket();
        ticket.setName(dto.getName());
        ticket.setSkiRunId(dto.getSkiRunId());
        ticket.setPrice(dto.getPrice());
        ticket.setHour(dto.getHour());
        ticket.setDescription(dto.getDescription());
        ticket.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);

        ticketMapper.insert(ticket);
        return ticket;
    }

    @Override
    public Ticket update(Long id, String name, Integer skiRunId, Integer hour, String description) {
        Ticket ticket = new Ticket();
        ticket.setId(id.intValue());
        ticket.setName(name);
        ticket.setSkiRunId(skiRunId);
        ticket.setHour(hour);
        ticket.setDescription(description);

        ticketMapper.update(ticket);
        return ticketMapper.getById(id);
    }

    @Override
    public Ticket updateStatus(Long id, Integer status) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("status", status);

        ticketMapper.updateStatus(params);
        return ticketMapper.getById(id);
    }

    @Override
    public void delete(Long id) {
        ticketMapper.delete(id);
    }
}