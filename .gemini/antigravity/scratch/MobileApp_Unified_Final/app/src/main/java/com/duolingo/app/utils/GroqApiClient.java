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

public class GroqApiClient {

    public interface GroqCallback {
        void onSuccess(String responseText);
        void onError(String errorMessage);
    }

    // ĐIỀN OPENROUTER API KEY TỪ openrouter.ai/keys VÀO ĐÂY
    private static final String API_KEY = "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"; 
    private static final String ENDPOINT = "https://openrouter.ai/api/v1/chat/completions";
    private static final String SYSTEM_INSTRUCTION = "Bạn là DuDu, một trợ lý học tập tiếng Anh thông minh và nhiệt tình trong ứng dụng VocaVerse. Bạn luôn trả lời ngắn gọn ngữ pháp tiếng Anh, vui vẻ, dùng emoji và giải thích thật dễ hiểu. Trả lời bằng tiếng Việt.";

    private OkHttpClient client;
    private Handler mainHandler;

    public GroqApiClient() {
        this.client = new OkHttpClient();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    public void generateContent(String userMessage, GroqCallback callback) {
        if (API_KEY.equals("YOUR_OPENROUTER_API_KEY")) {
            mainHandler.post(() -> callback.onError("Vui lòng lấy OpenRouter API Key điền vào GroqApiClient.java!"));
            return;
        }

        try {
            JSONObject jsonBody = new JSONObject();
            // Dùng mô hình Llama 3.3 Miễn phí trên OpenRouter
            jsonBody.put("model", "meta-llama/llama-3.3-70b-instruct:free");

            JSONArray messages = new JSONArray();
            
            // System message
            JSONObject sysMsg = new JSONObject();
            sysMsg.put("role", "system");
            sysMsg.put("content", SYSTEM_INSTRUCTION);
            messages.put(sysMsg);

            // User message
            JSONObject userMsg = new JSONObject();
            userMsg.put("role", "user");
            userMsg.put("content", userMessage);
            messages.put(userMsg);

            jsonBody.put("messages", messages);

            RequestBody requestBody = RequestBody.create(
                    jsonBody.toString(),
                    MediaType.get("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                    .url(ENDPOINT)
                    .addHeader("Authorization", "Bearer " + API_KEY)
                    .addHeader("HTTP-Referer", "http://localhost")
                    .addHeader("X-Title", "VocaVerse")
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    mainHandler.post(() -> callback.onError("Lỗi kết nối mạng Groq: " + e.getMessage()));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String resBody = response.body() != null ? response.body().string() : "";
                    if (response.isSuccessful()) {
                        try {
                            JSONObject jsonObject = new JSONObject(resBody);
                            JSONArray choices = jsonObject.optJSONArray("choices");
                            if (choices != null && choices.length() > 0) {
                                JSONObject firstChoice = choices.getJSONObject(0);
                                JSONObject message = firstChoice.optJSONObject("message");
                                if (message != null) {
                                    String text = message.optString("content", "");
                                    mainHandler.post(() -> callback.onSuccess(text.trim()));
                                    return;
                                }
                            }
                            mainHandler.post(() -> callback.onError("Không thể đọc phản hồi từ Llama3."));
                        } catch (Exception e) {
                            mainHandler.post(() -> callback.onError("Lỗi xử lý JSON: " + e.getMessage()));
                        }
                    } else {
                        mainHandler.post(() -> callback.onError("Lỗi API Groq: " + response.code() + " - " + resBody));
                    }
                }
            });
        } catch (Exception e) {
            callback.onError("Lỗi chuẩn bị dữ liệu: " + e.getMessage());
        }
    }
}
