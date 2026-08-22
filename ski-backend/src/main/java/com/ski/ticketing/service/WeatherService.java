package com.ski.ticketing.service;

import com.ski.ticketing.pojo.dto.WeatherCreateDTO;
import com.ski.ticketing.pojo.entity.Weather;
import com.ski.ticketing.pojo.vo.PageResultVO;

import java.util.List;

public interface WeatherService {

    Weather getById(Long id);

    List<Weather> getAll();

    PageResultVO<Weather> getByPage(int pageNum, int pageSize);

    List<Weather> getByWeather(String weather);

    Weather create(WeatherCreateDTO dto);

    Weather update(Long id, WeatherCreateDTO dto);

    void delete(Long id);
}