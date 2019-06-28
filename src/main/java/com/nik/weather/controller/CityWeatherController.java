package com.nik.weather.controller;

import com.nik.weather.data.payload.CityDto;
import com.nik.weather.data.payload.WeatherDto;
import com.nik.weather.data.vo.Weather;
import com.nik.weather.service.CityService;
import com.nik.weather.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("")
public class CityWeatherController {

    @Autowired
    private WeatherService weatherService;

    @Autowired
    private CityService cityService;

    @PostMapping("/weather")
    public WeatherDto getWeatherByCity(@RequestParam String city) {
        return weatherService.getWeatherByCity(city);
    }

    @PostMapping("/city/create/{name}")
    public CityDto addCity(@PathVariable("name") String city) {
        return cityService.add(city);
    }

    @RequestMapping(path = "/weather/create", method = RequestMethod.POST)
    public WeatherDto addWeather( Weather weather){
        return weatherService.add(weather);
    }

    @GetMapping("/city/get/{name}")
    public CityDto getCity(@PathVariable("name") String city) {
        return cityService.getCityByName(city);
    }
}
