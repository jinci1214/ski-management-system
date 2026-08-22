package com.ski.ticketing.controller.user;

import com.ski.ticketing.pojo.entity.Orders;
import com.ski.ticketing.pojo.entity.Ticket;
import com.ski.ticketing.pojo.dto.TicketBundleOrderDTO;
import com.ski.ticketing.result.Result;
import com.ski.ticketing.service.TicketService;
import com.ski.ticketing.service.VisitorOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.security.core.Authentication;
import jakarta.validation.Valid;

@RestController("userTicketController")
@RequestMapping("/user/ticket")
@Slf4j
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private VisitorOrderService visitorOrderService;

    @GetMapping("/list")
    public Result<List<Ticket>> list() {
        List<Ticket> list = ticketService.getAll();
        return Result.success(list);
    }

    @PostMapping("/buy/{ticketId}")
    public Result<Orders> buyTicket(@PathVariable Long ticketId, Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        log.info("用户{}购买雪票{}", userId, ticketId);
        return Result.success(visitorOrderService.buyTicket(userId, ticketId));
    }

    @PostMapping("/buy-with-equipment")
    public Result<List<Orders>> buyTicketWithEquipment(@Valid @RequestBody TicketBundleOrderDTO dto,
                                                        Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        log.info("用户{}购买雪票{}并加购{}件雪具", userId, dto.getTicketId(), dto.getEquipmentIds().size());
        return Result.success(visitorOrderService.buyTicketWithEquipment(userId, dto.getTicketId(), dto.getEquipmentIds()));
    }
}
