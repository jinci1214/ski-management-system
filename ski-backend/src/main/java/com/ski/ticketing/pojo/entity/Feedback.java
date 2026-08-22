package com.ski.ticketing.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 游客评价与意见反馈。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Feedback {
    private Integer id;
    private Integer userId;
    private String type;
    private String content;
    /** 1-5 分 */
    private Integer rating;
    private String reply;
    /** 待处理、已回复 */
    private String status;
    /** 管理回复是否已被游客查看 */
    private Boolean replyRead;
    private LocalDateTime createTime;
    private LocalDateTime replyTime;
}
