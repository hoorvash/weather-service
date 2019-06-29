package com.nik.weather.service;

import com.nik.weather.entity.CityEntity;
import com.nik.weather.repository.CityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class CityService {

    private final CityRepository cityRepository;

    public CityService(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    public void add(String cityName) {
        Collection<CityEntity> oldEn = cityRepository.findCityByName(cityName.toLowerCase());
        if (oldEn == null || oldEn.size() == 0) {
            CityEntity en = new CityEntity();
            en.setName(cityName.toLowerCase());
            cityRepository.save(en);
        }
    }
}
