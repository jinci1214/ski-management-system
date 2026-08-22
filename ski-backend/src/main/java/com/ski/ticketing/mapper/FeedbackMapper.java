package com.ski.ticketing.mapper;

import com.ski.ticketing.pojo.entity.Feedback;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FeedbackMapper {
    Feedback getById(Long id);
    List<Feedback> getAll();
    List<Feedback> getByUserId(Long userId);
    List<Feedback> getByPage(@Param("offset") int offset, @Param("pageSize") int pageSize);
    long count();
    int insert(Feedback feedback);
    int reply(Feedback feedback);
    int markRepliesRead(Long userId);
    int delete(Long id);
}
