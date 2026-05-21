package com.example.a16adventure.models;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class MonumentRepository {
    private static final String TAG = "MonumentRepository";

    private MonumentRepository() {}

    public static List<Monument> loadFromAssets(Context context) {
        List<Monument> monuments = new ArrayList<>();
        try (InputStream is = context.getAssets().open("monuments.json")) {
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);

            String jsonString = new String(buffer, StandardCharsets.UTF_8);
            JSONArray jsonArray = new JSONArray(jsonString);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                monuments.add(new Monument(
                        obj.getString("id"),
                        obj.getString("name"),
                        obj.getString("district"),
                        obj.getString("description"),
                        obj.getString("imageUrl"),
                        obj.getDouble("lat"),
                        obj.getDouble("lng")
                ));
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to load monuments from assets", e);
        }
        return monuments;
    }
}
