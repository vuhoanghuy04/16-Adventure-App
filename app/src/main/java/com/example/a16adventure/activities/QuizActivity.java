package com.example.a16adventure.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder; // Thêm import mới
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat; // Thêm import mới

import com.example.a16adventure.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class QuizActivity extends AppCompatActivity {

    private TextView tvScore, tvQuestion;
    private ImageView btnBackQuiz, ivQuizImage;
    private MaterialButton btnOption1, btnOption2, btnOption3;
    private ProgressBar pbCountdown;

    private int score = 0;
    private String correctAnswer = "";
    private boolean isClickable = false;

    private DatabaseReference userScoreRef;
    private FirebaseUser currentUser;

    private List<JSONObject> allQuizzes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Làm mờ thanh status bar để nền liquid tràn lên trên
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_quiz);

        initViews();
        setupFirebase();
        loadQuizBankFromAssets();
    }

    private void setupFirebase() {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userScoreRef = FirebaseDatabase.getInstance().getReference("users")
                    .child(currentUser.getUid()).child("quiz");
            loadScoreFromFirebase();
        }
    }

    private void loadScoreFromFirebase() {
        userScoreRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int lastResetWeek = snapshot.child("lastResetWeek").getValue(Integer.class) != null ? 
                            snapshot.child("lastResetWeek").getValue(Integer.class) : -1;
                    
                    Calendar calendar = Calendar.getInstance(Locale.getDefault());
                    int currentWeek = calendar.get(Calendar.WEEK_OF_YEAR);

                    if (currentWeek != lastResetWeek) {
                        // Reset điểm mỗi tuần
                        score = 0;
                        updateScoreToFirebase(currentWeek);
                    } else {
                        score = snapshot.child("score").getValue(Integer.class) != null ? 
                                snapshot.child("score").getValue(Integer.class) : 0;
                    }
                    tvScore.setText(String.valueOf(score));
                } else {
                    // Lần đầu chơi
                    score = 0;
                    tvScore.setText("0");
                    updateScoreToFirebase(Calendar.getInstance().get(Calendar.WEEK_OF_YEAR));
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }

    private void updateScoreToFirebase(int week) {
        if (userScoreRef != null) {
            userScoreRef.child("score").setValue(score);
            userScoreRef.child("lastResetWeek").setValue(week);
        }
    }

    private void initViews() {
        tvScore = findViewById(R.id.tvScore);
        tvQuestion = findViewById(R.id.tvQuestion);
        btnBackQuiz = findViewById(R.id.btnBackQuiz);
        ivQuizImage = findViewById(R.id.ivQuizImage);
        btnOption1 = findViewById(R.id.btnOption1);
        btnOption2 = findViewById(R.id.btnOption2);
        btnOption3 = findViewById(R.id.btnOption3);
        pbCountdown = findViewById(R.id.pbCountdown);

        btnBackQuiz.setOnClickListener(v -> finish());

        View.OnClickListener answerListener = v -> {
            if (!isClickable) return;
            animateButtonClick(v); // Thêm hiệu ứng Liquid co giãn khi bấm
            checkAnswer((MaterialButton) v);
        };
        btnOption1.setOnClickListener(answerListener);
        btnOption2.setOnClickListener(answerListener);
        btnOption3.setOnClickListener(answerListener);
    }

    private void loadQuizBankFromAssets() {
        try {
            InputStream is = getAssets().open("quizzes.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            String jsonString = new String(buffer, StandardCharsets.UTF_8);
            JSONArray jsonArray = new JSONArray(jsonString);
            for (int i = 0; i < jsonArray.length(); i++) {
                allQuizzes.add(jsonArray.getJSONObject(i));
            }

            if (!allQuizzes.isEmpty()) {
                loadNextQuestion();
            } else {
                tvQuestion.setText("File JSON không có câu hỏi nào!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            tvQuestion.setText("Lỗi đọc file JSON: " + e.getMessage());
        }
    }

    private void loadNextQuestion() {
        if (allQuizzes.isEmpty()) return;

        isClickable = false;
        resetButtonColors(); // Reset về trạng thái kính mờ
        pbCountdown.setVisibility(View.INVISIBLE);

        tvQuestion.setText("Đang tải dữ liệu hình ảnh...");
        btnOption1.setText("...");
        btnOption2.setText("...");
        btnOption3.setText("...");

        try {
            int randomIndex = new Random().nextInt(allQuizzes.size());
            JSONObject randomQuiz = allQuizzes.get(randomIndex);

            String question = randomQuiz.getString("question");
            String imageUrl = randomQuiz.getString("imageUrl");
            correctAnswer = randomQuiz.getString("correctAnswer");

            JSONArray optionsArray = randomQuiz.getJSONArray("options");
            List<String> options = new ArrayList<>();
            for (int i = 0; i < optionsArray.length(); i++) {
                options.add(optionsArray.getString(i));
            }
            Collections.shuffle(options);

            Glide.with(this)
                    .load(imageUrl)
                    .centerCrop()
                    .placeholder(android.R.drawable.ic_popup_sync)
                    .error(android.R.drawable.stat_notify_error)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            showQuestionData(question, options);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            showQuestionData(question, options);
                            return false;
                        }
                    })
                    .into(ivQuizImage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showQuestionData(String question, List<String> options) {
        runOnUiThread(() -> {
            tvQuestion.setText(question);
            btnOption1.setText(options.get(0));
            btnOption2.setText(options.get(1));
            btnOption3.setText(options.get(2));
            isClickable = true;
        });
    }

    private void checkAnswer(MaterialButton clickedBtn) {
        isClickable = false;
        String selectedAnswer = clickedBtn.getText().toString();
        String message;
        int popupColor;

        // Định nghĩa bảng màu Liquid
        int colorSuccess = Color.parseColor("#4CAF50"); // Xanh lá
        int colorError = Color.parseColor("#F44336");   // Đỏ

        if (selectedAnswer.equals(correctAnswer)) {
            // ĐÚNG: Đổi màu nút sang xanh
            clickedBtn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(colorSuccess));
            score += 10;
            tvScore.setText(String.valueOf(score));
            
            // Cập nhật lên Firebase
            int currentWeek = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);
            updateScoreToFirebase(currentWeek);

            message = "Tuyệt vời! +10đ";
            popupColor = colorSuccess;
        } else {
            // SAI: Đổi màu nút bấm sang đỏ
            clickedBtn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(colorError));
            message = "Sai rồi! Cố gắng câu sau nhé.";
            popupColor = colorError;

            // TỰ ĐỘNG BÔI XANH ĐÁP ÁN ĐÚNG
            if (btnOption1.getText().toString().equals(correctAnswer)) {
                btnOption1.setBackgroundTintList(android.content.res.ColorStateList.valueOf(colorSuccess));
            } else if (btnOption2.getText().toString().equals(correctAnswer)) {
                btnOption2.setBackgroundTintList(android.content.res.ColorStateList.valueOf(colorSuccess));
            } else if (btnOption3.getText().toString().equals(correctAnswer)) {
                btnOption3.setBackgroundTintList(android.content.res.ColorStateList.valueOf(colorSuccess));
            }
        }

        // Hiển thị Popup và Thanh đếm ngược
        showStyledSnackbar(message, popupColor);

        pbCountdown.setVisibility(View.VISIBLE);
        ObjectAnimator animation = ObjectAnimator.ofInt(pbCountdown, "progress", 1000, 0);
        animation.setDuration(2500);
        animation.start();

        new Handler(Looper.getMainLooper()).postDelayed(this::loadNextQuestion, 2500);
    }

    // HIỆU ỨNG LIQUID: Co giãn nhẹ khi bấm nút
    private void animateButtonClick(View v) {
        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 0.92f, 1f);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 0.92f, 1f);
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(v, pvhX, pvhY);
        animator.setDuration(150);
        animator.start();
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    // Thiết kế Popup "viên nổi" Glass mượt mà
    private void showStyledSnackbar(String message, int backgroundColor) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, 2500);
        View view = snackbar.getView();
        TextView textView = view.findViewById(com.google.android.material.R.id.snackbar_text);

        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textView.setPadding(dpToPx(16), dpToPx(8), dpToPx(16), dpToPx(8));
        textView.setTextColor(Color.WHITE);

        // Nền Popup là kính bo góc lớn
        GradientDrawable background = new GradientDrawable();
        background.setShape(GradientDrawable.RECTANGLE);
        background.setCornerRadius(dpToPx(28)); // Bo góc siêu lớn
        background.setColor(backgroundColor);
        view.setBackground(background);

        if (view.getLayoutParams() instanceof FrameLayout.LayoutParams) {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
            params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
            // Margin cách đều các cạnh tạo cảm giác lơ lửng
            params.setMargins(dpToPx(24), 0, dpToPx(24), dpToPx(70));
            view.setLayoutParams(params);
        }

        view.setElevation(dpToPx(12)); // Bóng đổ lớn hơn

        snackbar.show();
    }

    // RESET NÚT VỀ TRẠNG THÁI KÍNH MỜ MẶC ĐỊNH
    private void resetButtonColors() {
        // Màu trắng trong suốt 40% (giống trong file xml glass_white_fill)
        int glassColor = Color.parseColor("#66FFFFFF");

        // Reset màu nền và xóa bỏ các tint màu cũ (Xanh/Đỏ) của câu trước
        btnOption1.setBackgroundTintList(android.content.res.ColorStateList.valueOf(glassColor));
        btnOption2.setBackgroundTintList(android.content.res.ColorStateList.valueOf(glassColor));
        btnOption3.setBackgroundTintList(android.content.res.ColorStateList.valueOf(glassColor));

        // Đảm bảo nút luôn sử dụng drawable bo góc của Liquid Glass
        btnOption1.setBackgroundResource(R.drawable.btn_glass_option);
        btnOption2.setBackgroundResource(R.drawable.btn_glass_option);
        btnOption3.setBackgroundResource(R.drawable.btn_glass_option);
    }
}