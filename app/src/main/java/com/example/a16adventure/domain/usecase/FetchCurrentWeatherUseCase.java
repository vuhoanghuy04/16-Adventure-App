package com.example.a16adventure.domain.usecase;

import com.example.a16adventure.domain.repository.WeatherRepository;

public class FetchCurrentWeatherUseCase {
    private final WeatherRepository weatherRepository;

    public FetchCurrentWeatherUseCase(WeatherRepository weatherRepository) {
        this.weatherRepository = weatherRepository;
    }

    public void execute(WeatherRepository.WeatherCallback callback) {
        weatherRepository.fetchCurrentWeather(callback);
    }
}
