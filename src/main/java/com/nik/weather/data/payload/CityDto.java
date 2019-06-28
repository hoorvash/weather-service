package com.nik.weather.data.payload;

import com.nik.weather.entity.CityEntity;

public class CityDto {

    private long id;
    private String name;

    public CityDto createFrom(CityEntity city){
        CityDto c = new CityDto();
        c.id = city.getId();
        c.name = city.getName();
        return c;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
