package com.ski.ticketing.mapper;

import com.ski.ticketing.pojo.entity.Weather;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface WeatherMapper {

    Weather getById(Long id);

    List<Weather> getAll();

    List<Weather> getByPage(int offset, int pageSize);

    long count();

    List<Weather> getByWeather(String weather);

    int insert(Weather weather);

    int update(Weather weather);

    int delete(Long id);
}