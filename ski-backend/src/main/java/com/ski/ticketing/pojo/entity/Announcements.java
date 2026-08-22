package com.ski.ticketing.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
/*
* 公告*/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Announcements {
    /**
     * 主键 ID
     */
    private Integer id;
    
    /**
     * 公告标题
     */
    private String title;
    
    /**
     * 公告内容
     */
    private String content;
    
    /**
     * 分类 (雪道更新/雪票更新/开放时间/天气/课程更新/宣传)
     */
    private String category;
    
    /**
     * 发布时间
     */
    private LocalDateTime publishTime;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
