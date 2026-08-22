package com.ski.ticketing.service.Impl;

import com.ski.ticketing.pojo.entity.Announcements;
import com.ski.ticketing.pojo.vo.PageResultVO;
import com.ski.ticketing.service.AnnouncementsService;
import com.ski.ticketing.mapper.AnnouncementsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AnnouncementsServiceImpl implements AnnouncementsService {

    @Autowired
    private AnnouncementsMapper announcementsMapper;

    @Override
    public Announcements getById(Long id) {
        return announcementsMapper.getById(id);
    }

    @Override
    public List<Announcements> getAll() {
        return announcementsMapper.getAll();
    }

    @Override
    public List<Announcements> searchByCategory(String keyword) {
        return announcementsMapper.searchByCategory(keyword);
    }

    @Override
    public List<Announcements> searchByTitle(String keyword) {
        return announcementsMapper.searchByTitle(keyword);
    }

    @Override
    public List<Announcements> searchByContent(String keyword) {
        return announcementsMapper.searchByContent(keyword);
    }

    @Override
    public List<Announcements> getByPublishDate(String date) {
        return announcementsMapper.getByPublishDate(date);
    }

    @Override
    public PageResultVO<Announcements> getByPage(int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<Announcements> list = announcementsMapper.getByPage(offset, pageSize);
        long total = announcementsMapper.count();

        PageResultVO<Announcements> result = new PageResultVO<>();
        result.setData(list);
        result.setTotal(total);
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);

        return result;
    }

    @Override
    public Announcements create(Announcements announcements) {
        announcements.setPublishTime(LocalDateTime.now());
        announcementsMapper.insert(announcements);
        return announcements;
    }

    @Override
    public Announcements update(Long id, Announcements announcements) {
        announcements.setId(id.intValue());
        announcementsMapper.update(announcements);
        return announcementsMapper.getById(id);
    }

    @Override
    public void delete(Long id) {
        announcementsMapper.delete(id);
    }
}