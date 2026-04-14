package com.example.a16adventure.data.repository;

import android.content.Context;

import com.example.a16adventure.domain.repository.MonumentRepository;
import com.example.a16adventure.models.Monument;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class AssetMonumentRepository implements MonumentRepository {
    private final Context context;

    public AssetMonumentRepository(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    public List<Monument> getAllMonuments() throws Exception {
        List<Monument> monuments = new ArrayList<>();

        InputStream is = context.getAssets().open("monuments.json");
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();

        String jsonString = new String(buffer, StandardCharsets.UTF_8);
        JSONArray jsonArray = new JSONArray(jsonString);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            Monument monument = new Monument(
                    obj.getString("id"),
                    obj.getString("name"),
                    obj.getString("district"),
                    obj.getString("description"),
                    obj.getString("imageUrl"),
                    obj.getDouble("lat"),
                    obj.getDouble("lng")
            );
            monuments.add(monument);
        }

        return monuments;
    }
}
