package com.nik.weather.data.initialization;

import com.nik.weather.conn.WeatherUtil;
import com.nik.weather.data.payload.Forecasts;
import com.nik.weather.data.vo.Weather;
import com.nik.weather.service.CityService;
import com.nik.weather.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class CommandLineAppStartupRunner implements CommandLineRunner {

    @Autowired
    private CityService cityService;

    @Autowired
    private WeatherService weatherService;

    @Override
    public void run(String... args) {
        List<String> cities = new ArrayList<>();
        cities.add("ahvaz");
        cities.add("rasht");
        cities.add("shiraz");
        cities.add("tabriz");
        cities.add("tehran");
        cities.add("karaj");

        for (String city : cities) {
            cityService.add(city);
            try {
                Forecasts f = WeatherUtil.getWeather(new Date().getTime() / 1000, city, "fa");
                Weather w = new Weather();
                w.setCity(city);
                if (f.text != null && f.low != null && f.high != null) {
                    w.setStatus(f.text);
                    w.setMinTemp(f.low.intValue());
                    w.setMaxTemp(f.high.intValue());
                    weatherService.add(w);
                }
            }catch (Exception e){
               e.printStackTrace();
            }
        }
    }
}
