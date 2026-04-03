package com.example.a16adventure.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView chatRecyclerView;
    private ChatAdapter chatAdapter;
    private List<Message> messageList;
    private EditText edtMessage;
    private TextView btnSend;
    private ImageButton btnBack;

    private GenerativeModelFutures model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initViews();
        setupChat();
        setupGemini();

        btnSend.setOnClickListener(v -> sendMessage());
        btnBack.setOnClickListener(v -> finish());
    }

    private void initViews() {
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        edtMessage = findViewById(R.id.edtMessage);
        btnSend = findViewById(R.id.btnSend);
        btnBack = findViewById(R.id.btnBack);
    }

    private void setupChat() {
        messageList = new ArrayList<>();
        messageList.add(new Message("model", "Chào bạn! Tôi là trợ lý ảo của 16 Adventure. Tôi có thể giúp gì cho bạn về du lịch Hải Phòng?"));
        
        chatAdapter = new ChatAdapter(messageList);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);
    }

    private void setupGemini() {
        // Sử dụng model gemini-1.5-flash-001 kèm API Key mới của bạn
        String apiKey = "AIzaSyCBziMGwz6k1sQeyNtS34JPwItNwJ96hQ4";
        GenerativeModel gm = new GenerativeModel("gemini-1.5-flash-001", apiKey);
        model = GenerativeModelFutures.from(gm);
    }

    private void sendMessage() {
        String query = edtMessage.getText().toString().trim();
        if (query.isEmpty()) return;

        // 1. Hiển thị tin nhắn người dùng
        messageList.add(new Message("user", query));
        int userPos = messageList.size() - 1;
        chatAdapter.notifyItemInserted(userPos);
        chatRecyclerView.scrollToPosition(userPos);
        edtMessage.setText("");

        // 2. Hiển thị trạng thái đang xử lý
        messageList.add(new Message("model", "..."));
        int aiLoadingPos = messageList.size() - 1;
        chatAdapter.notifyItemInserted(aiLoadingPos);
        chatRecyclerView.scrollToPosition(aiLoadingPos);

        Content content = new Content.Builder()
                .addText(query)
                .build();

        Executor executor = Executors.newSingleThreadExecutor();
        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String aiText = result.getText();
                runOnUiThread(() -> {
                    if (aiText != null && !aiText.isEmpty()) {
                        messageList.get(aiLoadingPos).setContent(aiText);
                    } else {
                        messageList.get(aiLoadingPos).setContent("AI không phản hồi dữ liệu.");
                    }
                    chatAdapter.notifyItemChanged(aiLoadingPos);
                    chatRecyclerView.scrollToPosition(aiLoadingPos);
                });
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("GeminiError", "LỖI CHI TIẾT: ", t);
                runOnUiThread(() -> {
                    messageList.get(aiLoadingPos).setContent("Lỗi: " + t.getMessage());
                    chatAdapter.notifyItemChanged(aiLoadingPos);
                });
            }
        }, executor);
    }
}