package com.nik.weather.controller;

import com.nik.weather.data.payload.WeatherDto;
import com.nik.weather.exception.InvalidParameterException;
import com.nik.weather.service.WeatherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("")
@Api(value = "Weather Report API")
public class CityWeatherController {

    private final WeatherService weatherService;

    public CityWeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @ApiOperation(value = "View weather report based on city name if the city exists")
    @PostMapping("/weather")
    public WeatherDto getWeatherByCity(@ApiParam(value = "Iran's city name which already exists" +
            " in database e.g. tehran, rasht , ...",
            required = true) @RequestParam String city) throws InvalidParameterException {
        return weatherService.getWeatherByCity(city, null, false);
    }

    @ApiOperation(value = "View weather report based on city name and if the city is not in the database it creates it")
    @PostMapping("/admin/weather")
    public WeatherDto getWeatherByCityByAdmin(@ApiParam(value = "A Valid City name of Iran",
            required = true) @RequestParam String city) throws InvalidParameterException {
        return weatherService.getWeatherByCity(city, null, true);
    }

}
