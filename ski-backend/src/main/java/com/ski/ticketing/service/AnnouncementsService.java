package com.ski.ticketing.service;

import com.ski.ticketing.pojo.entity.Announcements;
import com.ski.ticketing.pojo.vo.PageResultVO;

import java.time.LocalDateTime;
import java.util.List;

public interface AnnouncementsService {

    Announcements getById(Long id);

    List<Announcements> getAll();

    List<Announcements> searchByCategory(String keyword);

    List<Announcements> searchByTitle(String keyword);

    List<Announcements> searchByContent(String keyword);

    List<Announcements> getByPublishDate(String date);

    PageResultVO<Announcements> getByPage(int pageNum, int pageSize);

    Announcements create(Announcements announcements);

    Announcements update(Long id, Announcements announcements);

    void delete(Long id);
}
