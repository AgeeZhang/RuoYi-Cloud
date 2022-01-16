package com.xjs.weather.controller;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.security.annotation.RequiresLogin;
import com.xjs.weather.domain.NowWeather;
import com.xjs.weather.service.WeatherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 天气控制器
 * @author xiejs
 * @since 2022-01-16
 */
@RestController
@RequestMapping("weather")
@Api(tags = "业务模块-天气管理")
@Log4j2
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    @GetMapping
    @ApiOperation("获取天气信息")
    @Log(title = "获取天气")
    @RequiresLogin
    public R<NowWeather> getWeatherApiData() {
        return R.ok(weatherService.saveNowWeather());
    }
}
