package com.ski.ticketing.service.Impl;

import com.ski.ticketing.pojo.dto.WeatherCreateDTO;
import com.ski.ticketing.pojo.entity.Weather;
import com.ski.ticketing.pojo.vo.PageResultVO;
import com.ski.ticketing.service.WeatherService;
import com.ski.ticketing.mapper.WeatherMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WeatherServiceImpl implements WeatherService {

    @Autowired
    private WeatherMapper weatherMapper;

    @Override
    public Weather getById(Long id) {
        return weatherMapper.getById(id);
    }

    @Override
    public List<Weather> getAll() {
        return weatherMapper.getAll();
    }

    @Override
    public PageResultVO<Weather> getByPage(int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<Weather> list = weatherMapper.getByPage(offset, pageSize);
        long total = weatherMapper.count();

        PageResultVO<Weather> result = new PageResultVO<>();
        result.setData(list);
        result.setTotal(total);
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);

        return result;
    }

    @Override
    public List<Weather> getByWeather(String weather) {
        return weatherMapper.getByWeather(weather);
    }

    @Override
    public Weather create(WeatherCreateDTO dto) {
        Weather weather = new Weather();
        weather.setResortId(dto.getResortId());
        weather.setDate(dto.getDate());
        weather.setTemperatureMin(dto.getTemperatureMin());
        weather.setTemperatureMax(dto.getTemperatureMax());
        weather.setWeather(dto.getWeather());
        weather.setWind(dto.getWind());
        weather.setSnow(dto.getSnow());

        weatherMapper.insert(weather);
        return weather;
    }

    @Override
    public Weather update(Long id, WeatherCreateDTO dto) {
        Weather weather = new Weather();
        weather.setId(id.intValue());
        weather.setResortId(dto.getResortId());
        weather.setDate(dto.getDate());
        weather.setTemperatureMin(dto.getTemperatureMin());
        weather.setTemperatureMax(dto.getTemperatureMax());
        weather.setWeather(dto.getWeather());
        weather.setWind(dto.getWind());
        weather.setSnow(dto.getSnow());

        weatherMapper.update(weather);
        return weatherMapper.getById(id);
    }

    @Override
    public void delete(Long id) {
        weatherMapper.delete(id);
    }
}
