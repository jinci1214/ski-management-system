package com.ski.ticketing.service.Impl;

import com.ski.ticketing.mapper.FeedbackMapper;
import com.ski.ticketing.pojo.entity.Feedback;
import com.ski.ticketing.pojo.vo.PageResultVO;
import com.ski.ticketing.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedbackServiceImpl implements FeedbackService {
    @Autowired
    private FeedbackMapper mapper;

    public Feedback getById(Long id) { return mapper.getById(id); }
    public List<Feedback> getByUserId(Long userId) { return mapper.getByUserId(userId); }
    public PageResultVO<Feedback> getByPage(int pageNum, int pageSize) {
        PageResultVO<Feedback> result = new PageResultVO<>();
        result.setData(mapper.getByPage((pageNum - 1) * pageSize, pageSize));
        result.setTotal(mapper.count());
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        return result;
    }
    public Feedback create(Feedback feedback) { mapper.insert(feedback); return feedback; }
    public Feedback reply(Long id, String reply) {
        Feedback feedback = new Feedback();
        feedback.setId(id.intValue());
        feedback.setReply(reply);
        mapper.reply(feedback);
        return mapper.getById(id);
    }
    public void markRepliesRead(Long userId) { mapper.markRepliesRead(userId); }
    public void delete(Long id) { mapper.delete(id); }
}
