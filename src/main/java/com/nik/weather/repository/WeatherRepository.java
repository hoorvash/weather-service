package com.nik.weather.repository;

import com.nik.weather.entity.WeatherEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WeatherRepository extends JpaRepository<WeatherEntity, Long> {

    @Query("SELECT w FROM WeatherEntity w WHERE w.city.name = :city order by w.date DESC ")
    List<WeatherEntity> findWeatherByName(@Param("city") String city);

    @Query("SELECT w FROM WeatherEntity w WHERE w.city.name = :city order by w.date DESC ")
    List<WeatherEntity> findWeatherLastDate(@Param("city") String city);
}
