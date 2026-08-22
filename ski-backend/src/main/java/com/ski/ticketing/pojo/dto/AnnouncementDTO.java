package com.ski.ticketing.pojo.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementDTO {

    private Integer id;
    private String title;
    private String content;
    private String category;
    private LocalDateTime publishTime;
}