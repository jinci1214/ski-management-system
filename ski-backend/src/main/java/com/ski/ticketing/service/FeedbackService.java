package com.ski.ticketing.service;

import com.ski.ticketing.pojo.entity.Feedback;
import com.ski.ticketing.pojo.vo.PageResultVO;

import java.util.List;

public interface FeedbackService {
    Feedback getById(Long id);
    List<Feedback> getByUserId(Long userId);
    PageResultVO<Feedback> getByPage(int pageNum, int pageSize);
    Feedback create(Feedback feedback);
    Feedback reply(Long id, String reply);
    void markRepliesRead(Long userId);
    void delete(Long id);
}
