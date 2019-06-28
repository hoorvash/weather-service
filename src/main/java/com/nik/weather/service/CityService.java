package com.nik.weather.service;

import com.nik.weather.data.payload.CityDto;
import com.nik.weather.entity.CityEntity;
import com.nik.weather.repository.CityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class CityService {

    @Autowired
    private CityRepository cityRepository;

    public CityDto add(String cityName) {
        CityDto dto = new CityDto();
        Collection<CityEntity> oldEn = cityRepository.findCityByName(cityName.toLowerCase());
        if(oldEn == null || oldEn.size() == 0) {
            CityEntity en = new CityEntity();
            en.setName(cityName.toLowerCase());
            cityRepository.save(en);
        }
        return dto;
    }

    public CityDto getCityByName(String name) {
        CityDto dto = new CityDto();
//        CityEntity en = cityRepository.findCityByName(name.toLowerCase());
//
//        if (en != null) {
//            return dto.createFrom(en);
//        }
        return dto;
    }
}
