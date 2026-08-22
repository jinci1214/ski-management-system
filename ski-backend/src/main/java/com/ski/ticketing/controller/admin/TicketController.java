package com.ski.ticketing.controller.admin;

import com.ski.ticketing.pojo.dto.TicketCreateDTO;
import com.ski.ticketing.pojo.entity.Ticket;
import com.ski.ticketing.pojo.vo.PageResultVO;
import com.ski.ticketing.result.Result;
import com.ski.ticketing.service.TicketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/ticket")
@Slf4j
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @GetMapping("/{id}")
    public Result<Ticket> getById(@PathVariable Long id) {
        Ticket ticket = ticketService.getById(id);
        return Result.success(ticket);
    }

    @GetMapping("/page")
    public Result<PageResultVO<Ticket>> getByPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageResultVO<Ticket> result = ticketService.getByPage(pageNum, pageSize);
        return Result.success(result);
    }

    @GetMapping("/status/{status}")
    public Result<List<Ticket>> getByStatus(@PathVariable Integer status) {
        List<Ticket> list = ticketService.getByStatus(status);
        return Result.success(list);
    }

    @GetMapping("/ski-run/{skiRunId}")
    public Result<List<Ticket>> getBySkiRunId(@PathVariable Long skiRunId) {
        List<Ticket> list = ticketService.getBySkiRunId(skiRunId);
        return Result.success(list);
    }

    @PostMapping
    public Result<Ticket> create(@RequestBody TicketCreateDTO dto) {
        Ticket created = ticketService.create(dto);
        return Result.success(created);
    }

    @PutMapping("/{id}")
    public Result<Ticket> update(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam Integer skiRunId,
            @RequestParam Integer hour,
            @RequestParam(required = false) String description) {
        Ticket updated = ticketService.update(id, name, skiRunId, hour, description);
        return Result.success(updated);
    }

    @PutMapping("/{id}/status")
    public Result<Ticket> updateStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        Ticket updated = ticketService.updateStatus(id, status);
        return Result.success(updated);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        ticketService.delete(id);
        return Result.success();
    }
}