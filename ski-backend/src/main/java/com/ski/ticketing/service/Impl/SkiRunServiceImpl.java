package com.ski.ticketing.service.Impl;

import com.ski.ticketing.pojo.entity.SkiRun;
import com.ski.ticketing.pojo.vo.PageResultVO;
import com.ski.ticketing.service.SkiRunService;
import com.ski.ticketing.mapper.SkiRunMapper;
import com.ski.ticketing.utils.AliOSSUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Service
public class SkiRunServiceImpl implements SkiRunService {

    @Autowired
    private SkiRunMapper skiRunMapper;

    @Autowired
    private AliOSSUtil aliOSSUtil;

    @Override
    public SkiRun getById(Long id) {
        return skiRunMapper.getById(id);
    }

    @Override
    public List<SkiRun> getAll() {
        return skiRunMapper.getAll();
    }

    @Override
    public List<SkiRun> getByLevel(String level) {
        return skiRunMapper.getByLevel(level);
    }

    @Override
    public PageResultVO<SkiRun> getByPage(int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<SkiRun> list = skiRunMapper.getByPage(offset, pageSize);
        long total = skiRunMapper.count();

        PageResultVO<SkiRun> result = new PageResultVO<>();
        result.setData(list);
        result.setTotal(total);
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);

        return result;
    }

    @Override
    public SkiRun create(Integer resortId, String name, String level, BigDecimal length, BigDecimal height, MultipartFile image) throws IOException {
        SkiRun skiRun = new SkiRun();
        skiRun.setResortId(resortId);
        skiRun.setName(name);
        skiRun.setLevel(level);
        skiRun.setLength(length);
        skiRun.setHeight(height);

        if (image != null && !image.isEmpty()) {
            String imageUrl = aliOSSUtil.upload(image);
            skiRun.setImage(imageUrl);
        }

        skiRunMapper.insert(skiRun);
        return skiRun;
    }

    @Override
    public SkiRun update(Long id, Integer resortId, String name, String level, BigDecimal length, BigDecimal height, MultipartFile image) throws IOException {
        SkiRun existing = skiRunMapper.getById(id);
        if (existing == null) {
            throw new RuntimeException("雪道不存在");
        }

        SkiRun skiRun = new SkiRun();
        skiRun.setId(id.intValue());
        skiRun.setResortId(resortId);
        skiRun.setName(name);
        skiRun.setLevel(level);
        skiRun.setLength(length);
        skiRun.setHeight(height);

        if (image != null && !image.isEmpty()) {
            String imageUrl = aliOSSUtil.upload(image);
            skiRun.setImage(imageUrl);
        }

        skiRunMapper.update(skiRun);
        return skiRunMapper.getById(id);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        SkiRun skiRun = skiRunMapper.getById(id);
        if (skiRun == null) {
            throw new RuntimeException("雪道不存在");
        }

        skiRunMapper.deleteTicketsBySkiRunId(id);
        skiRunMapper.deleteCoursesBySkiRunId(id);
        skiRunMapper.delete(id);
    }
}
