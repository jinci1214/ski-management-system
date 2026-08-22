package com.ski.ticketing.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
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

    /** 头像地址 */
    private String avatar;

    /** 上次修改用户名时间 */
    private LocalDateTime usernameUpdateTime;

    /** 上次修改密码时间 */
    private LocalDateTime passwordUpdateTime;
    
    /**
     * 创建注册时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
