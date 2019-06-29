package com.nik.weather.data.initialization;

import com.nik.weather.exception.InvalidParameterException;
import com.nik.weather.exception.YahooWeatherServiceException;
import com.nik.weather.server.WeatherDataGenerator;
import com.nik.weather.service.CityService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CommandLineAppStartupRunner implements CommandLineRunner {

    private final CityService cityService;

    private final WeatherDataGenerator generator;

    public CommandLineAppStartupRunner(CityService cityService, WeatherDataGenerator generator) {
        this.cityService = cityService;
        this.generator = generator;
    }

    @Override
    public void run(String... args) throws InvalidParameterException, YahooWeatherServiceException {
        List<String> cities = new ArrayList<>();
        cities.add("ahvaz");
        cities.add("rasht");
        cities.add("shiraz");
        cities.add("tabriz");
        cities.add("tehran");
        cities.add("karaj");

        for (String city : cities) {
            cityService.add(city);
            generator.generate(city, "fa");
        }
    }
}
