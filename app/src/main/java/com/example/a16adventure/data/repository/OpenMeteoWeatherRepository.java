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
            HttpURLConnection conn = null;
            try {
                URL url = new URL(HAI_PHONG_WEATHER_URL);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                StringBuilder result = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                }

                JSONObject jsonObject = new JSONObject(result.toString());
                JSONObject current = jsonObject.getJSONObject("current_weather");
                double temp = current.getDouble("temperature");
                int weatherCode = current.getInt("weathercode");

                callback.onSuccess(new WeatherInfo(temp, weatherCode));
            } catch (Exception e) {
                callback.onError(e.getMessage() != null ? e.getMessage() : "Lỗi tải thời tiết");
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }).start();
    }
}
