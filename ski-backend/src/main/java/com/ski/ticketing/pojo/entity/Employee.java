package com.ski.ticketing.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
    /**
     * 主键 ID
     */
    private Integer id;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 加密密码
     */
    private String password;
    
    /**
     * 真实姓名
     */
    private String name;
    
    /**
     * 联系电话
     */
    private String phone;
    
    /**
     * 职位
     */
    private String position;
    
    /**
     * 状态 (0-禁用/1-启用)
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
