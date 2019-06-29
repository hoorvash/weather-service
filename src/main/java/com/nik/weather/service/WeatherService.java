package com.nik.weather.service;

import com.nik.weather.conn.WeatherUtil;
import com.nik.weather.data.Constants;
import com.nik.weather.data.payload.WeatherDto;
import com.nik.weather.data.vo.Weather;
import com.nik.weather.entity.CityEntity;
import com.nik.weather.entity.WeatherEntity;
import com.nik.weather.exception.InvalidParameterException;
import com.nik.weather.exception.YahooWeatherServiceException;
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

    public WeatherDto getWeatherByCity(String city, String region, boolean isAdmin, boolean isStartup) throws InvalidParameterException, YahooWeatherServiceException {
        WeatherDto dto = new WeatherDto();
        Weather weather;

        if (city == null || city.isEmpty()) {
            throw new InvalidParameterException("Invalid name for city", Constants.Weather.INVALID_NAME_ERROR);
        }

        boolean cityExists = cityExist(city);

        if (isAdmin && !cityExists) { //Admin can add new city and then check weather
            cityService.add(city);
        }

        if (cityExists) {//check if city even exist

            List<WeatherEntity> existedWeathers = weatherRepository.findWeatherLastDate(city);
            if (existedWeathers != null && existedWeathers.size() > 0) {//check weather record is for today
                if (DateUtil.isDateBeforeToday(existedWeathers.get(0).getDate())) {
                    try {
                        weather = WeatherUtil.callYahooWeatherService(city, region, isStartup);
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
            } else { // when there is no record for today
                weather = WeatherUtil.callYahooWeatherService(city, null, isStartup);
                if (weather != null) {
                    WeatherEntity en = weatherRepository.save(buildEntity(weather));
                    return dto.createFrom(en);
                }
            }

        } else {
            throw new InvalidParameterException("City does not exist", Constants.Weather.INVALID_CITY_ERROR);
        }

        return dto;
    }

    public WeatherDto getWeatherByCity(String city, String region) throws InvalidParameterException, YahooWeatherServiceException
    {
        WeatherDto dto = new WeatherDto();

        if (city == null || city.isEmpty()) {
            throw new InvalidParameterException("Invalid name for city", Constants.Weather.INVALID_NAME_ERROR);
        }

        Weather weather;

        if (cityExist(city)) {//check if city even exist
            List<WeatherEntity> existedWeathers = weatherRepository.findWeatherLastDate(city);
            if (existedWeathers != null && existedWeathers.size() > 0) {//check weather record is for today
                if (DateUtil.isDateBeforeToday(existedWeathers.get(0).getDate())) {
                    try {
                        weather = WeatherUtil.callYahooWeatherService(city, region, false);
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
            } else { // when there is no record for today
                weather = WeatherUtil.callYahooWeatherService(city, null, false);
                if (weather != null) {
                    WeatherEntity en = weatherRepository.save(buildEntity(weather));
                    return dto.createFrom(en);
                }
            }

        } else {
            throw new InvalidParameterException("City does not exist", Constants.Weather.INVALID_CITY_ERROR);
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

    private boolean cityExist(String cityName) {
        List<CityEntity> cityEntities = cityRepository.findCityByName(cityName);
        return cityEntities != null && cityEntities.size() != 0;
    }
}
