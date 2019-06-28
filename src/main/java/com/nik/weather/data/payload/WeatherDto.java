package com.nik.weather.data.payload;

import com.nik.weather.entity.WeatherEntity;

public class WeatherDto {

    private long id;
    private String city;
    private String timestamp;
    private int minTemp;
    private int maxTemp;
    private String status;

    public WeatherDto createFrom(WeatherEntity weather){
        WeatherDto w = new WeatherDto();
        w.id = weather.getId();
        w.city = weather.getCity().getName();
        w.minTemp = weather.getMinTemp();
        w.maxTemp = weather.getMaxTemp();
        w.timestamp = String.valueOf(weather.getDate().getTime());
        w.status = weather.getStatus();
        return w;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getMinTemp() {
        return minTemp;
    }

    public void setMinTemp(int minTemp) {
        this.minTemp = minTemp;
    }

    public int getMaxTemp() {
        return maxTemp;
    }

    public void setMaxTemp(int maxTemp) {
        this.maxTemp = maxTemp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
