package com.example.a16adventure.data.repository;

import com.example.a16adventure.domain.model.WeatherInfo;
import com.example.a16adventure.domain.repository.WeatherRepository;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class OpenMeteoWeatherRepository implements WeatherRepository {
    private static final String HAI_PHONG_WEATHER_URL =
            "https://api.open-meteo.com/v1/forecast?latitude=20.8648&longitude=106.6835&current_weather=true";

    @Override
    public void fetchCurrentWeather(WeatherCallback callback) {
        new Thread(() -> {
            try {
                URL url = new URL(HAI_PHONG_WEATHER_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();

                JSONObject jsonObject = new JSONObject(result.toString());
                JSONObject current = jsonObject.getJSONObject("current_weather");
                double temp = current.getDouble("temperature");
                int weatherCode = current.getInt("weathercode");

                callback.onSuccess(new WeatherInfo(temp, weatherCode));
            } catch (Exception e) {
                callback.onError(e.getMessage() != null ? e.getMessage() : "Lỗi tải thời tiết");
            }
        }).start();
    }
}
