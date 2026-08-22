package com.ski.ticketing.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLoginVO {
    private Long id;
    private String userName;
    private String name;
    private String avatar;
    private String token;
}
