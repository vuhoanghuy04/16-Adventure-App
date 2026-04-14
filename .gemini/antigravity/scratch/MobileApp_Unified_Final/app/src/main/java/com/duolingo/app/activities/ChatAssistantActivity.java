package com.duolingo.app.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.duolingo.app.R;
import com.duolingo.app.adapter.ChatAdapter;
import com.duolingo.app.models.ChatMessage;
import com.duolingo.app.utils.GroqApiClient;
import com.duolingo.app.utils.MockAiEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ChatAssistantActivity extends AppCompatActivity {

    private RecyclerView rvChatMessages;
    private EditText etMessageInput;
    private ImageButton btnSendMessage;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> messageList;
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_assistant);

        Toolbar toolbar = findViewById(R.id.toolbar_chat);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        rvChatMessages = findViewById(R.id.rv_chat_messages);
        etMessageInput = findViewById(R.id.et_message_input);
        btnSendMessage = findViewById(R.id.btn_send_message);

        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(messageList);
        
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        rvChatMessages.setLayoutManager(layoutManager);
        rvChatMessages.setAdapter(chatAdapter);

        // Tin nhắn mở đầu của DuDu
        addBotMessage("Xin chào! Mình là DuDu, trợ lý AI học tập của bạn.\nBạn có thể bảo mình dịch từ, dịch câu, hoặc hỏi bất kỳ kiến thức ngữ pháp nào nhé!");

        btnSendMessage.setOnClickListener(v -> handleSendMessage());
    }

    private void handleSendMessage() {
        String input = etMessageInput.getText().toString().trim();
        if (TextUtils.isEmpty(input)) return;

        // Thêm tin nhắn của User
        messageList.add(new ChatMessage(input, true));
        chatAdapter.notifyItemInserted(messageList.size() - 1);
        rvChatMessages.smoothScrollToPosition(messageList.size() - 1);
        etMessageInput.setText("");

        // Disable nút gửi khi AI đang trả lời
        btnSendMessage.setEnabled(false);

        // Gọi OpenRouter API (Gemini Free)
        GroqApiClient apiClient = new GroqApiClient();
        apiClient.generateContent(input, new GroqApiClient.GroqCallback() {
            @Override
            public void onSuccess(String responseText) {
                btnSendMessage.setEnabled(true);
                addBotMessage(responseText);
            }

            @Override
            public void onError(String errorMessage) {
                btnSendMessage.setEnabled(true);
                // System fallback offline
                generateMockBotResponse(input);
            }
        });
    }

    private void generateMockBotResponse(String query) {
        String response = MockAiEngine.getResponse(this, query);
        addBotMessage(response);
    }

    private void addBotMessage(String text) {
        messageList.add(new ChatMessage(text, false));
        chatAdapter.notifyItemInserted(messageList.size() - 1);
        rvChatMessages.smoothScrollToPosition(messageList.size() - 1);
    }
}
