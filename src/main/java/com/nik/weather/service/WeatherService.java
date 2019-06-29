package com.nik.weather.service;

import com.nik.weather.conn.WeatherUtil;
import com.nik.weather.data.payload.WeatherDto;
import com.nik.weather.data.vo.Weather;
import com.nik.weather.entity.CityEntity;
import com.nik.weather.entity.WeatherEntity;
import com.nik.weather.repository.CityRepository;
import com.nik.weather.repository.WeatherRepository;
import com.nik.weather.util.DateUtil;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class WeatherService {

    private final WeatherRepository weatherRepository;
    private final CityRepository cityRepository;
    private final CityService cityService;

    public WeatherService(WeatherRepository weatherRepository, CityRepository cityRepository, CityService cityService) {
        this.weatherRepository = weatherRepository;
        this.cityRepository = cityRepository;
        this.cityService = cityService;
    }

    public WeatherDto getWeatherByCity(String city, String region, boolean isAdmin) {
        WeatherDto dto = new WeatherDto();

        if (city == null || city.isEmpty()) {
            return dto;
        }

        if (isAdmin) {
            makeSureCityExist(city);
        }

        List<WeatherEntity> existedWeathers = weatherRepository.findWeatherLastDate(city);
        Weather weather;
        if (existedWeathers != null && existedWeathers.size() > 0) {
            if (DateUtil.isDateBeforeToday(existedWeathers.get(0).getDate())) {
                try {
                    weather = WeatherUtil.callYahooWeatherService(city, region);
                    if (weather != null) {
                        WeatherEntity en = weatherRepository.save(buildEntity(weather));
                        return dto.createFrom(en);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                return dto.createFrom(existedWeathers.get(0));
            }
        } else {
            try {

                weather = WeatherUtil.callYahooWeatherService(city, null);
                if (weather != null) {
                    WeatherEntity en = weatherRepository.save(buildEntity(weather));
                    return dto.createFrom(en);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return dto;
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

    private void makeSureCityExist(String cityName) {
        List<CityEntity> cityEntities = cityRepository.findCityByName(cityName);
        if (cityEntities == null || cityEntities.size() == 0) {
            cityService.add(cityName);
        }
    }
}
