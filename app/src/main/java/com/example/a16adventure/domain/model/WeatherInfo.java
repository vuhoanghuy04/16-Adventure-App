package com.example.a16adventure.domain.model;

public class WeatherInfo {
    private final double temperature;
    private final int weatherCode;

    public WeatherInfo(double temperature, int weatherCode) {
        this.temperature = temperature;
        this.weatherCode = weatherCode;
    }

    public double getTemperature() {
        return temperature;
    }

    public int getWeatherCode() {
        return weatherCode;
    }
}
