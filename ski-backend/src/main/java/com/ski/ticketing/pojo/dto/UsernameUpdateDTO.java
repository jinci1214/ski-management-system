package com.ski.ticketing.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UsernameUpdateDTO {

    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = "^[A-Za-z0-9_]{3,20}$", message = "用户名须为3—20位字母、数字或下划线")
    private String username;
}
