package com.ski.ticketing.mapper;

import com.ski.ticketing.pojo.entity.Announcements;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface AnnouncementsMapper {

    Announcements getById(Long id);

    List<Announcements> getAll();

    List<Announcements> searchByCategory(String keyword);

    List<Announcements> searchByTitle(String keyword);

    List<Announcements> searchByContent(String keyword);

    List<Announcements> getByPublishDate(String date);

    List<Announcements> getByPage(int offset, int pageSize);

    long count();

    int insert(Announcements announcements);

    int update(Announcements announcements);

    int delete(Long id);
}
