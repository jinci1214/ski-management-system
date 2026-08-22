package com.ski.ticketing.service;

import com.ski.ticketing.pojo.entity.SkiRun;
import com.ski.ticketing.pojo.vo.PageResultVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public interface SkiRunService {

    SkiRun getById(Long id);

    List<SkiRun> getAll();

    List<SkiRun> getByLevel(String level);

    PageResultVO<SkiRun> getByPage(int pageNum, int pageSize);

    SkiRun create(Integer resortId, String name, String level, BigDecimal length, BigDecimal height, MultipartFile image) throws IOException;

    SkiRun update(Long id, Integer resortId, String name, String level, BigDecimal length, BigDecimal height, MultipartFile image) throws IOException;

    void delete(Long id);
}
