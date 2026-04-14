package com.example.a16adventure.domain.repository;

import com.example.a16adventure.domain.model.WeatherInfo;

public interface WeatherRepository {
    interface WeatherCallback {
        void onSuccess(WeatherInfo weatherInfo);
        void onError(String message);
    }

    void fetchCurrentWeather(WeatherCallback callback);
}
