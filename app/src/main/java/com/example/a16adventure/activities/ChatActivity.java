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
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a16adventure.BuildConfig;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView chatRecyclerView;
    private ChatAdapter chatAdapter;
    private List<Message> messageList;
    private EditText edtMessage;
    private TextView btnSend;
    private ImageButton btnBack, btnMic, btnCamera, btnMore;
    
    private View layoutImagePreview;
    private ImageView imgPreview;
    private ImageButton btnRemoveImage;
    private Bitmap selectedBitmap = null;

    private GenerativeModelFutures model;
    private final Executor chatExecutor = Executors.newSingleThreadExecutor();
    
    private FirebaseAuth mAuth;
    private String currentUserId = "guest";

    private final ActivityResultLauncher<Intent> speechRecognizerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                Intent data = result.getData();
                if (result.getResultCode() == RESULT_OK && data != null) {
                    ArrayList<String> textResult = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if (textResult != null && !textResult.isEmpty()) {
                        edtMessage.setText(textResult.get(0));
                    }
                }
            });

    private final ActivityResultLauncher<Intent> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                Intent data = result.getData();
                if (result.getResultCode() == RESULT_OK && data != null) {
                    Uri imageUri = data.getData();
                    if (imageUri != null) {
                        showImagePreview(imageUri);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) currentUserId = user.getUid();

        initViews();
        loadChatHistory();
        setupGemini();

        btnSend.setOnClickListener(v -> sendMessage());
        btnBack.setOnClickListener(v -> finish());
        btnMic.setOnClickListener(v -> startSpeechToText());
        btnCamera.setOnClickListener(v -> {
            if (mAuth.getCurrentUser() == null) {
                Toast.makeText(this, "Vui lòng đăng nhập để gửi ảnh!", Toast.LENGTH_SHORT).show();
                return;
            }
            openGallery();
        });
        btnRemoveImage.setOnClickListener(v -> removeSelectedImage());
        
        // Nút tròn (More) bên phải phía trên
        btnMore.setOnClickListener(this::showPopupMenu);
    }

    private void initViews() {
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        edtMessage = findViewById(R.id.edtMessage);
        btnSend = findViewById(R.id.btnSend);
        btnBack = findViewById(R.id.btnBack);
        btnMic = findViewById(R.id.btnMic);
        btnCamera = findViewById(R.id.btnCamera);
        btnMore = findViewById(R.id.btnMore);
        
        layoutImagePreview = findViewById(R.id.layoutImagePreview);
        imgPreview = findViewById(R.id.imgPreview);
        btnRemoveImage = findViewById(R.id.btnRemoveImage);
    }

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenu().add("Cuộc trò chuyện mới");
        
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getTitle().equals("Cuộc trò chuyện mới")) {
                startNewChat();
                return true;
            }
            return false;
        });
        popupMenu.show();
    }

    private void startNewChat() {
        // 1. Xóa danh sách hiện tại
        messageList.clear();
        messageList.add(new Message("model", "Chào bạn! Một cuộc trò chuyện mới đã bắt đầu. Tôi có thể giúp gì cho bạn?"));
        
        // 2. Cập nhật giao diện
        chatAdapter.notifyDataSetChanged();
        
        // 3. Xóa lịch sử trong SharedPreferences
        saveChatHistory();
        
        Toast.makeText(this, "Đã bắt đầu cuộc trò chuyện mới", Toast.LENGTH_SHORT).show();
    }

    private void startSpeechToText() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Đang nghe...");
        try {
            speechRecognizerLauncher.launch(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Thiết bị không hỗ trợ nhận diện giọng nói", Toast.LENGTH_SHORT).show();
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }

    private void removeSelectedImage() {
        selectedBitmap = null;
        layoutImagePreview.setVisibility(View.GONE);
    }

    private void showImagePreview(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            selectedBitmap = BitmapFactory.decodeStream(inputStream);
            if (selectedBitmap != null) {
                imgPreview.setImageBitmap(selectedBitmap);
                layoutImagePreview.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            Log.e("ChatActivity", "Lỗi preview ảnh", e);
        }
    }

    private void loadChatHistory() {
        SharedPreferences prefs = getSharedPreferences("ChatHistory_" + currentUserId, MODE_PRIVATE);
        String json = prefs.getString("messages", null);
        Gson gson = new Gson();
        
        if (json != null) {
            Type type = new TypeToken<ArrayList<Message>>() {}.getType();
            messageList = gson.fromJson(json, type);
        } else {
            messageList = new ArrayList<>();
            messageList.add(new Message("model", "Chào bạn! Tôi có thể giúp gì cho bạn về du lịch Hải Phòng?"));
        }
        
        chatAdapter = new ChatAdapter(messageList);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);
        chatRecyclerView.scrollToPosition(messageList.size() - 1);
    }

    private void saveChatHistory() {
        SharedPreferences prefs = getSharedPreferences("ChatHistory_" + currentUserId, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(messageList);
        editor.putString("messages", json);
        editor.apply();
    }

    private void setupGemini() {
        String apiKey = BuildConfig.GEMINI_API_KEY;
        if (apiKey == null || apiKey.trim().isEmpty()) {
            Toast.makeText(this, "Thiếu cấu hình GEMINI_API_KEY", Toast.LENGTH_LONG).show();
            return;
        }
        GenerativeModel gm = new GenerativeModel("gemini-2.5-flash-lite", apiKey);
        model = GenerativeModelFutures.from(gm);
    }

    private void sendMessage() {
        String query = edtMessage.getText().toString().trim();
        if (query.isEmpty() && selectedBitmap == null) return;

        final String userMsgContent = query.isEmpty() && selectedBitmap != null ? "[Hình ảnh]" : query;
        
        if (selectedBitmap != null) {
            if (mAuth.getCurrentUser() == null) {
                Toast.makeText(this, "Hãy đăng nhập để có thể gửi ảnh!", Toast.LENGTH_SHORT).show();
                return;
            }
            uploadImageToFirebase(selectedBitmap, userMsgContent);
        } else {
            executeSendMessage(userMsgContent, null, null);
        }
    }

    private void uploadImageToFirebase(Bitmap bitmap, String text) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("users/" + currentUserId + "/chat_images/" + UUID.randomUUID().toString() + ".jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] data = baos.toByteArray();

        storageRef.putBytes(data).addOnSuccessListener(taskSnapshot -> {
            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                executeSendMessage(text, uri.toString(), bitmap);
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Lỗi tải ảnh", Toast.LENGTH_SHORT).show();
            executeSendMessage(text, null, bitmap);
        });
    }

    private void executeSendMessage(String text, String imageUrl, Bitmap bitmapForAi) {
        Message userMessage = new Message("user", text);
        if (imageUrl != null) userMessage.setImageUrl(imageUrl);
        
        messageList.add(userMessage);
        chatAdapter.notifyItemInserted(messageList.size() - 1);
        chatRecyclerView.scrollToPosition(messageList.size() - 1);
        
        edtMessage.setText("");
        Bitmap bitmapToSend = selectedBitmap;
        removeSelectedImage();

        messageList.add(new Message("model", "..."));
        int aiLoadingPos = messageList.size() - 1;
        chatAdapter.notifyItemChanged(aiLoadingPos);
        chatRecyclerView.scrollToPosition(aiLoadingPos);

        Content.Builder contentBuilder = new Content.Builder();
        contentBuilder.setRole("user");
        if (!text.equals("[Hình ảnh]")) contentBuilder.addText(text);
        if (bitmapToSend != null) contentBuilder.addImage(bitmapToSend);
        
        Content content = contentBuilder.build();
        if (model == null) {
            messageList.get(aiLoadingPos).setContent("Thiếu cấu hình Gemini API key.");
            chatAdapter.notifyItemChanged(aiLoadingPos);
            return;
        }
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
                        messageList.get(aiLoadingPos).setContent("AI không phản hồi.");
                    }
                    chatAdapter.notifyItemChanged(aiLoadingPos);
                    chatRecyclerView.scrollToPosition(aiLoadingPos);
                    saveChatHistory();
                });
            }

            @Override
            public void onFailure(Throwable t) {
                runOnUiThread(() -> {
                    messageList.get(aiLoadingPos).setContent("Lỗi: " + t.getMessage());
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
