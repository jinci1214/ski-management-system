package com.ski.ticketing.controller.user;

import com.ski.ticketing.pojo.entity.Weather;
import com.ski.ticketing.result.Result;
import com.ski.ticketing.service.WeatherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("userWeatherController")
@RequestMapping("/user/weather")
@Slf4j
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    @GetMapping("/list")
    public Result<List<Weather>> list() {
        List<Weather> list = weatherService.getAll();
        return Result.success(list);
    }

    @GetMapping("/condition/{weather}")
    public Result<List<Weather>> getByWeather(@PathVariable String weather) {
        List<Weather> list = weatherService.getByWeather(weather);
        return Result.success(list);
    }
}