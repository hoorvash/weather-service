package com.nik.weather.server;

import com.nik.weather.exception.InvalidParameterException;
import com.nik.weather.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WeatherDataGenerator {

    private final WeatherService weatherService;

    @Autowired
    public WeatherDataGenerator(WeatherService weatherService){
        this.weatherService = weatherService;
    }

    public void generate(String city, String region) throws InvalidParameterException {
        weatherService.getWeatherByCity(city, region, false);
    }
}
