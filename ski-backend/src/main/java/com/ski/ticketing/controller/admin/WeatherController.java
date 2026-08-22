package com.ski.ticketing.controller.admin;

import com.ski.ticketing.pojo.dto.WeatherCreateDTO;
import com.ski.ticketing.pojo.entity.Weather;
import com.ski.ticketing.pojo.vo.PageResultVO;
import com.ski.ticketing.result.Result;
import com.ski.ticketing.service.WeatherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/weather")
@Slf4j
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    @GetMapping("/{id}")
    public Result<Weather> getById(@PathVariable Long id) {
        Weather weather = weatherService.getById(id);
        return Result.success(weather);
    }

    @GetMapping("/page")
    public Result<PageResultVO<Weather>> getByPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageResultVO<Weather> result = weatherService.getByPage(pageNum, pageSize);
        return Result.success(result);
    }

    @GetMapping("/condition/{weather}")
    public Result<List<Weather>> getByWeather(@PathVariable String weather) {
        List<Weather> list = weatherService.getByWeather(weather);
        return Result.success(list);
    }

    @PostMapping
    public Result<Weather> create(@RequestBody WeatherCreateDTO dto) {
        Weather created = weatherService.create(dto);
        return Result.success(created);
    }

    @PutMapping("/{id}")
    public Result<Weather> update(@PathVariable Long id, @RequestBody WeatherCreateDTO dto) {
        Weather updated = weatherService.update(id, dto);
        return Result.success(updated);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        weatherService.delete(id);
        return Result.success();
    }
}