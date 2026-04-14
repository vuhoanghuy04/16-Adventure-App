package com.example.a16adventure.data.repository;

import android.content.Context;

import com.example.a16adventure.domain.repository.QuizRepository;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class AssetQuizRepository implements QuizRepository {
    private final Context context;

    public AssetQuizRepository(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    public List<JSONObject> getAllQuizzes() throws Exception {
        List<JSONObject> quizzes = new ArrayList<>();

        InputStream is = context.getAssets().open("quizzes.json");
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();

        String jsonString = new String(buffer, StandardCharsets.UTF_8);
        JSONArray jsonArray = new JSONArray(jsonString);
        for (int i = 0; i < jsonArray.length(); i++) {
            quizzes.add(jsonArray.getJSONObject(i));
        }

        return quizzes;
    }
}
