package com.xjs.apitools.service.impl;

import com.xjs.apitools.domain.*;
import com.xjs.apitools.factory.ApiToolsFactory;
import com.xjs.apitools.factory.impl.*;
import com.xjs.apitools.service.ApiToolsService;
import com.xjs.exception.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * api工具服务实现
 *
 * @author xiejs
 * @since 2022-01-18
 */
@Service
public class ApiToolsServiceImpl implements ApiToolsService {

    private ApiToolsFactory<ApiHoliday, Object> holidayFactory;
    private ApiToolsFactory<ApiMobileBelong, RequestBody> mobileBelongFactory;
    private ApiToolsFactory<ApiNowWeather, RequestBody> nowWeatherFactory;
    private ApiToolsFactory<ApiForecastWeather,RequestBody> forecastWeatherFactory;
    private ApiToolsFactory<ApiGarbageSorting,RequestBody> garbageSortingFactory;

    @Autowired
    public void setHolidayFactory(RollHolidayFactory rollHolidayFactory) {
        this.holidayFactory = rollHolidayFactory;
    }

    @Autowired
    public void setMobileBelongFactory(RollMobileBelongFactory rollMobileBelongFactory) {
        this.mobileBelongFactory = rollMobileBelongFactory;
    }

    @Autowired
    public void setNowWeatherFactory(RollNowWeatherFactory rollNowWeatherFactory) {
        this.nowWeatherFactory = rollNowWeatherFactory;
    }

    @Autowired
    public void setForecastWeatherFactory(RollForecastWeatherFactory rollForecastWeatherFactory) {
        this.forecastWeatherFactory = rollForecastWeatherFactory;
    }

    @Autowired
    public void setGarbageSortingFactory(RollGarbageSortingFactory rollGarbageSortingFactory) {
        this.garbageSortingFactory = rollGarbageSortingFactory;
    }


    @Override
    public List<ApiHoliday> getApiHolidayList() {
        List<ApiHoliday> apiHolidayList = holidayFactory.apiDataList();
        Optional.ofNullable(apiHolidayList).orElseThrow(ApiException::new);
        List<ApiHoliday> collect = apiHolidayList.stream().map(holidayFactory -> {
            if (holidayFactory.getResidueDays() >= 0) {
                return holidayFactory;
            } else {
                return null;
            }
        }).collect(Collectors.toList());
        collect.removeIf(Objects::isNull);
        return collect;
    }

    @Override
    public ApiMobileBelong getApiMobileBelong(String mobile) {
        RequestBody requestBody = new RequestBody();
        requestBody.setMobile(mobile);
        return Optional.ofNullable(mobileBelongFactory.apiData(requestBody))
                .orElseThrow(ApiException::new);
    }

    @Override
    public ApiNowWeather getNowWeather(String city) {
        RequestBody requestBody = new RequestBody();
        requestBody.setCity(city);
        return Optional.ofNullable(nowWeatherFactory.apiData(requestBody))
                .orElseThrow(ApiException::new);
    }

    @Override
    public ApiForecastWeather getForecastWeather(String city) {
        RequestBody requestBody = new RequestBody();
        requestBody.setCity(city);
        return Optional.ofNullable(forecastWeatherFactory.apiData(requestBody))
                .orElseThrow(ApiException::new);
    }

    @Override
    public ApiGarbageSorting getGarbageSorting(String name) {
        RequestBody requestBody = new RequestBody();
        requestBody.setName(name);
        return Optional.ofNullable(garbageSortingFactory.apiData(requestBody))
                .orElseThrow(ApiException::new);
    }
}
