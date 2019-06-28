package com.nik.weather.service;

import com.nik.weather.data.payload.WeatherDto;
import com.nik.weather.data.vo.Weather;
import com.nik.weather.entity.CityEntity;
import com.nik.weather.entity.WeatherEntity;
import com.nik.weather.repository.CityRepository;
import com.nik.weather.repository.WeatherRepository;
import com.nik.weather.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class WeatherService {

    @Autowired
    private WeatherRepository weatherRepository;

    @Autowired
    private CityRepository cityRepository;

    public WeatherDto getWeatherByCity(String city) {
        List<WeatherEntity> we = weatherRepository.findWeatherByName(city);
        WeatherDto dto = new WeatherDto();
        if (we != null) {
            return dto.createFrom(we.get(0));
        }
        return dto;
    }

    public WeatherDto add(Weather weather) {
        WeatherDto res = new WeatherDto();
        if (weather.getCity() != null) {
            if (weather.getCity().equals("karaj")) {
                List<WeatherEntity> wEn = weatherRepository.findWeatherLastDate(weather.getCity());
                if (wEn == null || wEn.size() == 0 || DateUtil.isDateBeforeToday(wEn.get(0).getDate())) {
                    WeatherEntity en = weatherRepository.save(buildEntity(weather));
                    res.createFrom(en);
                }
            }
        }
        return res;
    }

    private WeatherEntity buildEntity(Weather w) {
        WeatherEntity en = new WeatherEntity();
        List<CityEntity> city = cityRepository.findCityByName(w.getCity().toLowerCase());
        if (city != null && city.size() > 0) {
            en.setCity(city.get(0));
        }
        en.setDate(new Date());
        en.setMinTemp(w.getMinTemp());
        en.setMaxTemp(w.getMaxTemp());
        en.setStatus(w.getStatus());
        return en;
    }
}
