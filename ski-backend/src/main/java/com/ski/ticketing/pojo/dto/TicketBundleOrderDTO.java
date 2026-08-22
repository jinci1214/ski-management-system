package com.ski.ticketing.pojo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TicketBundleOrderDTO {

    @NotNull(message = "雪票不能为空")
    private Long ticketId;

    private List<Long> equipmentIds = new ArrayList<>();
}
