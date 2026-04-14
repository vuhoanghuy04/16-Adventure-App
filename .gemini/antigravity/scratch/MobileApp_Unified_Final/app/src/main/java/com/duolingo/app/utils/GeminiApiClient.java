package com.duolingo.app.utils;

import android.os.Handler;
import android.os.Looper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GeminiApiClient {

    public interface GeminiCallback {
        void onSuccess(String responseText);
        void onError(String errorMessage);
    }

    private static final String API_KEY = "AIzaSyB9yaqEVzp5TuQyGL-RVzlBNAFweifLov0";
    private static final String ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + API_KEY;
    private static final String SYSTEM_INSTRUCTION = "Bạn là DuDu, một trợ lý học tập tiếng Anh thông minh và nhiệt tình. Bạn luôn trả lời ngắn gọn, vui vẻ, dùng emoji và giải thích ngữ pháp/từ vựng thật dễ hiểu.";

    private OkHttpClient client;
    private Handler mainHandler;

    public GeminiApiClient() {
        this.client = new OkHttpClient();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    public void generateContent(String userMessage, GeminiCallback callback) {
        try {
            JSONObject jsonBody = new JSONObject();

            // System Instruction
            JSONObject sysInstruction = new JSONObject();
            JSONArray sysParts = new JSONArray();
            sysParts.put(new JSONObject().put("text", SYSTEM_INSTRUCTION));
            sysInstruction.put("parts", sysParts);
            jsonBody.put("systemInstruction", sysInstruction);

            // Contents
            JSONArray contents = new JSONArray();
            JSONObject contentObj = new JSONObject();
            JSONArray contentParts = new JSONArray();
            contentParts.put(new JSONObject().put("text", userMessage));
            contentObj.put("parts", contentParts);
            contents.put(contentObj);
            jsonBody.put("contents", contents);

            RequestBody requestBody = RequestBody.create(
                    jsonBody.toString(),
                    MediaType.get("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                    .url(ENDPOINT)
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    mainHandler.post(() -> callback.onError("Lỗi kết nối mạng: " + e.getMessage()));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String resBody = response.body() != null ? response.body().string() : "";
                    if (response.isSuccessful()) {
                        try {
                            JSONObject jsonObject = new JSONObject(resBody);
                            JSONArray candidates = jsonObject.optJSONArray("candidates");
                            if (candidates != null && candidates.length() > 0) {
                                JSONObject firstCandidate = candidates.getJSONObject(0);
                                JSONObject content = firstCandidate.optJSONObject("content");
                                if (content != null) {
                                    JSONArray parts = content.optJSONArray("parts");
                                    if (parts != null && parts.length() > 0) {
                                        JSONObject part = parts.getJSONObject(0);
                                        String text = part.optString("text", "");
                                        mainHandler.post(() -> callback.onSuccess(text.trim()));
                                        return;
                                    }
                                }
                            }
                            mainHandler.post(() -> callback.onError("Không thể đọc phản hồi từ AI."));
                        } catch (Exception e) {
                            mainHandler.post(() -> callback.onError("Lỗi xử lý JSON: " + e.getMessage()));
                        }
                    } else {
                        mainHandler.post(() -> callback.onError("Lỗi API: " + response.code() + " - " + resBody));
                    }
                }
            });
        } catch (Exception e) {
            callback.onError("Lỗi chuẩn bị dữ liệu: " + e.getMessage());
        }
    }
}
