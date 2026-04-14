package com.example.a16adventure.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a16adventure.R;
import com.example.a16adventure.adapters.ChatAdapter;
import com.example.a16adventure.models.Message;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ChatActivity extends AppCompatActivity {

    private static final int SPEECH_REQUEST_CODE = 100;
    private static final int PICK_IMAGE_REQUEST_CODE = 101;

    private RecyclerView chatRecyclerView;
    private ChatAdapter chatAdapter;
    private List<Message> messageList;
    private EditText edtMessage;
    private TextView btnSend;
    private ImageButton btnBack, btnMic, btnCamera;

    private GenerativeModelFutures model;
    private final Executor chatExecutor = Executors.newSingleThreadExecutor();
    
    private static final String PREFS_NAME = "ChatPrefs";
    private static final String KEY_MESSAGES = "saved_messages";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initViews();
        loadChatHistory();
        setupGemini();

        btnSend.setOnClickListener(v -> sendMessage(null));
        btnBack.setOnClickListener(v -> finish());
        
        btnMic.setOnClickListener(v -> startSpeechToText());
        btnCamera.setOnClickListener(v -> openGallery());
    }

    private void initViews() {
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        edtMessage = findViewById(R.id.edtMessage);
        btnSend = findViewById(R.id.btnSend);
        btnBack = findViewById(R.id.btnBack);
        btnMic = findViewById(R.id.btnMic);
        btnCamera = findViewById(R.id.btnCamera);
    }

    private void startSpeechToText() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Đang nghe...");
        try {
            startActivityForResult(intent, SPEECH_REQUEST_CODE);
        } catch (Exception e) {
            Toast.makeText(this, "Thiết bị không hỗ trợ nhận diện giọng nói", Toast.LENGTH_SHORT).show();
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == SPEECH_REQUEST_CODE) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if (result != null && !result.isEmpty()) {
                    edtMessage.setText(result.get(0));
                }
            } else if (requestCode == PICK_IMAGE_REQUEST_CODE) {
                Uri imageUri = data.getData();
                if (imageUri != null) {
                    processImageMessage(imageUri);
                }
            }
        }
    }

    private void processImageMessage(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            sendMessage(bitmap);
        } catch (Exception e) {
            Log.e("ChatActivity", "Lỗi xử lý ảnh", e);
        }
    }

    private void loadChatHistory() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String json = prefs.getString(KEY_MESSAGES, null);
        Gson gson = new Gson();
        
        if (json != null) {
            Type type = new TypeToken<ArrayList<Message>>() {}.getType();
            messageList = gson.fromJson(json, type);
        } else {
            messageList = new ArrayList<>();
            messageList.add(new Message("model", "Chào bạn! Tôi là trợ lý ảo của 16 Adventure. Tôi có thể giúp gì cho bạn về du lịch Hải Phòng?"));
        }
        
        chatAdapter = new ChatAdapter(messageList);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);
        chatRecyclerView.scrollToPosition(messageList.size() - 1);
    }

    private void saveChatHistory() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(messageList);
        editor.putString(KEY_MESSAGES, json);
        editor.apply();
    }

    private void setupGemini() {
        String apiKey = "AIzaSyCBziMGwz6k1sQeyNtS34JPwItNwJ96hQ4";
        GenerativeModel gm = new GenerativeModel("gemini-2.5-flash", apiKey);
        model = GenerativeModelFutures.from(gm);
    }

    private void sendMessage(@Nullable Bitmap bitmap) {
        String query = edtMessage.getText().toString().trim();
        if (query.isEmpty() && bitmap == null) return;

        // Thêm tin nhắn user
        messageList.add(new Message("user", query));
        chatAdapter.notifyItemInserted(messageList.size() - 1);
        chatRecyclerView.scrollToPosition(messageList.size() - 1);
        edtMessage.setText("");

        // Thêm tin nhắn chờ của AI
        messageList.add(new Message("model", "..."));
        int aiLoadingPos = messageList.size() - 1;
        chatAdapter.notifyItemInserted(aiLoadingPos);
        chatRecyclerView.scrollToPosition(aiLoadingPos);

        // Xây dựng nội dung gửi đi
        Content.Builder contentBuilder = new Content.Builder();
        contentBuilder.setRole("user");
        if (!query.isEmpty()) contentBuilder.addText(query);
        if (bitmap != null) contentBuilder.addImage(bitmap);
        
        Content content = contentBuilder.build();

        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String rawAiText = result.getText();
                runOnUiThread(() -> {
                    if (rawAiText != null && !rawAiText.isEmpty()) {
                        String cleanAiText = rawAiText.replace("*", "");
                        messageList.get(aiLoadingPos).setContent(cleanAiText);
                    } else {
                        messageList.get(aiLoadingPos).setContent("AI không phản hồi dữ liệu.");
                    }
                    chatAdapter.notifyItemChanged(aiLoadingPos);
                    chatRecyclerView.scrollToPosition(aiLoadingPos);
                    saveChatHistory();
                });
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("GeminiError", "LỖI: ", t);
                runOnUiThread(() -> {
                    messageList.get(aiLoadingPos).setContent("Lỗi kết nối: " + t.getMessage());
                    chatAdapter.notifyItemChanged(aiLoadingPos);
                    saveChatHistory();
                });
            }
        }, chatExecutor);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveChatHistory();
    }
}