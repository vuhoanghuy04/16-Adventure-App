package com.duolingo.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.duolingo.app.R;
import com.duolingo.app.adapter.StudyModuleAdapter;
import com.duolingo.app.models.GrammarQuestion;
import com.duolingo.app.models.ListeningQuestion;
import com.duolingo.app.models.PronunciationQuestion;
import com.duolingo.app.models.StudyModule;
import com.duolingo.app.models.User;
import com.duolingo.app.models.VocabularyItem;
import com.duolingo.app.persistence.CSVHelper;
import com.duolingo.app.persistence.VocaVerseDatabase;
import com.duolingo.app.utils.NavigationHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.duolingo.app.models.PronunciationQuestion;
import com.duolingo.app.models.StudyModule;
import com.duolingo.app.models.User;
import com.duolingo.app.persistence.CSVHelper;
import com.duolingo.app.persistence.VocaVerseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvStudyModules;
    private List<StudyModule> moduleList;

    private TextView textGreeting, textUserName, tvStreakCount;

    // Khai báo lại cầu nối Database
    private VocaVerseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // 1. Ánh xạ View
        rvStudyModules = findViewById(R.id.rvStudyModules);
        textGreeting = findViewById(R.id.text_greeting);
        
        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        NavigationHelper.setup(this, nav, R.id.nav_study);
        textUserName = findViewById(R.id.text_user_name);
        tvStreakCount = findViewById(R.id.tv_streak_count);

        // 2. Khởi tạo Database
        database = VocaVerseDatabase.getDatabase(this);

        // 3. Tự động chào theo giờ & Móc tên từ Database lên!
        updateGreeting();
        loadUserData();

        // 4. Khởi tạo dữ liệu lưới
        initData();

        // 5. Thiết lập hiển thị
        setupRecyclerView();

        initializeLessonData();
        initializeGrammarData();

        loadGrammarQuestionsFromCSV();

        loadListeningQuestionsFromCSV();

        loadPronunciationQuestionsFromCSV();
    }

    private void loadUserData() {
        // Đọc ID người dùng từ phiên đăng nhập (Mặc định là -1 nếu chưa ai đăng nhập)
        int userId = getSharedPreferences("VocaVersePrefs", MODE_PRIVATE).getInt("current_user_id", -1);

        if (userId != -1) {
            // Có người đăng nhập -> Xuống Database tìm tên
            VocaVerseDatabase.databaseWriteExecutor.execute(() -> {
                User user = database.userDao().getUserById(userId);
                if (user != null) {
                    // Update UI bắt buộc phải chạy trên Main Thread
                    runOnUiThread(() -> textUserName.setText(user.getFullName() + " 👋"));
                }
            });
            calculateStreak(userId);
            loadRecentLesson(userId);
        } else {
            // Chưa có ai đăng nhập (Khách vãng lai)
            textUserName.setText("Người dùng 👋");
            tvStreakCount.setText("0");
            loadRecentLesson(-1); // Default fallbacks
        }
    }

    private void calculateStreak(int userId) {
        VocaVerseDatabase.databaseWriteExecutor.execute(() -> {
            java.util.List<com.duolingo.app.models.GameHistory> history = database.progressDao().getAllHistory(userId);
            int streak = 0;
            if (history != null && !history.isEmpty()) {
                Calendar currentCal = Calendar.getInstance();
                currentCal.set(Calendar.HOUR_OF_DAY, 0);
                currentCal.set(Calendar.MINUTE, 0);
                currentCal.set(Calendar.SECOND, 0);
                currentCal.set(Calendar.MILLISECOND, 0);
                
                java.util.Set<Long> playDays = new java.util.HashSet<>();
                for (com.duolingo.app.models.GameHistory h : history) {
                    Calendar c = Calendar.getInstance();
                    c.setTimeInMillis(h.PlayedAt);
                    c.set(Calendar.HOUR_OF_DAY, 0);
                    c.set(Calendar.MINUTE, 0);
                    c.set(Calendar.SECOND, 0);
                    c.set(Calendar.MILLISECOND, 0);
                    playDays.add(c.getTimeInMillis());
                }

                long checkDay = currentCal.getTimeInMillis();
                if (playDays.contains(checkDay)) {
                    streak = 1;
                    checkDay -= 86400000L; // Trừ đi 1 ngày
                    while(playDays.contains(checkDay)) {
                        streak++;
                        checkDay -= 86400000L;
                    }
                } else {
                    checkDay -= 86400000L; // Kiểm tra xem hôm qua có học không
                    if (playDays.contains(checkDay)) {
                        streak = 1;
                        checkDay -= 86400000L;
                        while(playDays.contains(checkDay)) {
                            streak++;
                            checkDay -= 86400000L;
                        }
                    } else {
                        streak = 0; // Đứt chuỗi
                    }
                }
            }
            
            final int finalStreak = streak;
            runOnUiThread(() -> tvStreakCount.setText(String.valueOf(finalStreak)));
        });
    }

    private void loadRecentLesson(int userId) {
        TextView tvTitle = findViewById(R.id.tv_continue_title);
        TextView tvSubtitle = findViewById(R.id.tv_continue_subtitle);
        TextView tvPercent = findViewById(R.id.tv_continue_progress);
        android.widget.ProgressBar pb = findViewById(R.id.pb_continue_progress);
        android.widget.ImageView ivIcon = findViewById(R.id.iv_continue_icon);
        androidx.cardview.widget.CardView cvBg = findViewById(R.id.cv_continue_icon_bg);
        com.google.android.material.card.MaterialCardView cardContinue = findViewById(R.id.card_continue_learning);

        if (userId == -1) {
            runOnUiThread(() -> {
                tvTitle.setText("Chưa học bài nào");
                tvSubtitle.setText("Điểm: 0");
                tvPercent.setText("0%");
                pb.setProgress(0);
            });
            return;
        }

        VocaVerseDatabase.databaseWriteExecutor.execute(() -> {
            java.util.List<com.duolingo.app.models.GameHistory> history = database.progressDao().getAllHistory(userId);
            if (history != null && !history.isEmpty()) {
                com.duolingo.app.models.GameHistory recent = history.get(0);
                
                runOnUiThread(() -> {
                    String gameType = recent.GameType;
                    int score = recent.Score;
                    
                    tvTitle.setText(gameType);
                    tvSubtitle.setText("Lần học gần nhất • Điểm: " + score);

                    // Trực quan phần trăm theo điểm giả định (ví dụ tối đa là 15-20, mock thành 100%)
                    // Hoặc điểm nhân 10/20 v.v.. (Ghép từ, Lật thẻ hay cho 5-20 điểm)
                    int percent = Math.min(100, score * 10);
                    if (percent == 0 && score > 0) percent = 100; // Nếu điểm nhỏ mà ko tính ra percent
                    if (percent == 0) percent = 5; // Có chơi là có tiến độ

                    tvPercent.setText(percent + "%");
                    pb.setProgress(percent);

                    // Đổi màu icon theo chủ đề
                    if (gameType.contains("từ") || gameType.contains("thẻ")) {
                        ivIcon.setImageResource(R.drawable.ic_book);
                        ivIcon.setColorFilter(android.graphics.Color.parseColor("#4CAF50")); // Green
                        cvBg.setCardBackgroundColor(android.graphics.Color.parseColor("#E8F5E9"));
                    } else if (gameType.contains("rắc") || gameType.contains("ắp")) { // Trắc nghiệm, Sắp xếp
                        ivIcon.setImageResource(R.drawable.ic_pen);
                        ivIcon.setColorFilter(android.graphics.Color.parseColor("#2196F3")); // Blue
                        cvBg.setCardBackgroundColor(android.graphics.Color.parseColor("#E3F2FD"));
                    }

                    // Click vào thẻ tiếp tục học sẽ mở ra màn tương ứng 
                    cardContinue.setOnClickListener(v -> {
                        if (gameType.contains("từ") || gameType.contains("thẻ")) {
                            startActivity(new Intent(MainActivity.this, LessonSelectionActivity.class));
                        } else {
                            startActivity(new Intent(MainActivity.this, GrammarListActivity.class));
                        }
                    });
                });
            } else {
                runOnUiThread(() -> {
                    tvTitle.setText("Khởi động khóa đầu tiên!");
                    tvSubtitle.setText("Hãy chơi 1 bài để lưu tiến độ");
                    tvPercent.setText("0%");
                    pb.setProgress(0);
                });
            }
        });
    }

    private void updateGreeting() {
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
        String greeting;

        if (timeOfDay >= 0 && timeOfDay < 12) greeting = "Chào buổi sáng,";
        else if (timeOfDay >= 12 && timeOfDay < 16) greeting = "Chào buổi trưa,";
        else if (timeOfDay >= 16 && timeOfDay < 21) greeting = "Chào buổi chiều,";
        else greeting = "Chào buổi tối,";

        textGreeting.setText(greeting);
    }

    private void initData() {
        moduleList = new ArrayList<>();
        moduleList.add(new StudyModule("vocab", R.drawable.ic_book, "Từ vựng", "Flashcard", R.color.vocab_main, R.color.vocab_bg));
        moduleList.add(new StudyModule("grammar", R.drawable.ic_pen, "Ngữ pháp", "Điền từ & Sắp xếp", R.color.grammar_main, R.color.grammar_bg));
        moduleList.add(new StudyModule("listening", R.drawable.ic_headphone, "Nghe hiểu", "Trắc nghiệm", R.color.listening_main, R.color.listening_bg));
        moduleList.add(new StudyModule("pronunciation", R.drawable.ic_mic, "Phát âm", "AI đánh giá", R.color.pronunciation_main, R.color.pronunciation_bg));
    }

    private void setupRecyclerView() {
        StudyModuleAdapter adapter = new StudyModuleAdapter(moduleList, module -> {
            // Giả sử id của module ngữ pháp là "grammar"
            if (module.getId().equals("grammar")) {
                Intent intent = new Intent(MainActivity.this, GrammarListActivity.class);
                startActivity(intent);
            }
            else if (module.getId().equals("vocab")) {
                Intent intent = new Intent(MainActivity.this, LessonSelectionActivity.class);
                startActivity(intent);
            }
            else if (module.getId().equals("listening")) {
                Intent intent = new Intent(MainActivity.this, ListeningListActivity.class);
                startActivity(intent);
            }
            else if (module.getId().equals("pronunciation")) {
                Intent intent = new Intent(MainActivity.this, PronunciationListActivity.class);
                startActivity(intent);
            }
        });

        rvStudyModules.setLayoutManager(new GridLayoutManager(this, 2));
        rvStudyModules.setAdapter(adapter);
    }

    private void updateModuleCounts() {
        int vocabCount = com.duolingo.app.utils.ProgressHelper.getCompletedVocabCount(this);
        int grammarCount = com.duolingo.app.utils.ProgressHelper.getCompletedGrammarCount(this);
        
        moduleList.get(0).setDescription("Đã hoàn thành: " + vocabCount + " bài");
        moduleList.get(1).setDescription("Đã hoàn thành: " + grammarCount + " bài");
        
        if (rvStudyModules.getAdapter() != null) {
            rvStudyModules.getAdapter().notifyDataSetChanged();
        }
    }

    private void initializeGrammarData() {
        VocaVerseDatabase.databaseWriteExecutor.execute(() -> {
            if (database.grammarDao().getCount() == 0) {
                List<GrammarQuestion> questions = CSVHelper.readGrammarFromCSV(this, "grammar_questions.csv");
                database.grammarDao().insertAll(questions);
            }
        });
    }

    private void loadGrammarQuestionsFromCSV() {
        VocaVerseDatabase db = VocaVerseDatabase.getDatabase(this);
        VocaVerseDatabase.databaseWriteExecutor.execute(() -> {

            // Dùng getCount() để đếm xem kho có trống không
            if (db.grammarDao().getCount() == 0) {
                try {
                    java.io.InputStream is = getAssets().open("grammar_questions.csv");
                    java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(is));
                    String line;
                    reader.readLine(); // Bỏ qua dòng tiêu đề

                    List<GrammarQuestion> bulkInsertList = new ArrayList<>();

                    while ((line = reader.readLine()) != null) {
                        // Regex này giúp tách dấu phẩy chuẩn hơn (phòng trường hợp trong lý thuyết có dấu phẩy)
                        String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

                        if (parts.length >= 12) {
                            GrammarQuestion q = new GrammarQuestion(
                                    parts[0].replace("\"", "").trim(),
                                    parts[1].replace("\"", "").trim(),
                                    parts[2].replace("\"", "").trim(),
                                    parts[3].replace("\"", "").trim(),
                                    parts[4].replace("\"", "").trim(),
                                    parts[5].replace("\"", "").trim(),
                                    parts[6].replace("\"", "").trim(),
                                    parts[7].replace("\"", "").trim(),
                                    parts[8].replace("\"", "").trim(),
                                    parts[9].replace("\"", "").trim(),
                                    parts[10].replace("\"", "").trim(),
                                    parts[11].replace("\"", "").trim()
                            );
                            bulkInsertList.add(q);
                        }
                    }
                    reader.close();

                    // Nhồi một phát tất cả dữ liệu vào Database
                    db.grammarDao().insertAll(bulkInsertList);

                    // Báo cáo thành công ra màn hình
                    runOnUiThread(() -> android.widget.Toast.makeText(MainActivity.this,
                            "Đã nạp xong " + bulkInsertList.size() + " câu Ngữ pháp!",
                            android.widget.Toast.LENGTH_LONG).show());

                } catch (Exception e) {
                    // Nếu lỗi, in thẳng ra Logcat dòng màu đỏ để bắt bệnh
                    e.printStackTrace();
                    runOnUiThread(() -> android.widget.Toast.makeText(MainActivity.this,
                            "Lỗi đọc file CSV: " + e.getMessage(),
                            android.widget.Toast.LENGTH_LONG).show());
                }
            }
        });
    }
    private void loadListeningQuestionsFromCSV() {
        VocaVerseDatabase db = VocaVerseDatabase.getDatabase(this);
        VocaVerseDatabase.databaseWriteExecutor.execute(() -> {
            if (db.listeningDao().getListeningCount() == 0) {
                try {
                    java.io.InputStream is = getAssets().open("listening_questions.csv");
                    java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(is));
                    String line;
                    reader.readLine(); // Bỏ qua tiêu đề
                    List<ListeningQuestion> bulkInsertList = new ArrayList<>();
                    int rowCount = 1;

                    while ((line = reader.readLine()) != null) {
                        rowCount++;
                        String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

                        if (parts.length >= 27) {
                            ListeningQuestion q = new ListeningQuestion();
                            // FIX LỖI BOM: Xóa bỏ các ký tự tàng hình UTF-8 ở đầu file
                            q.level = parts[0].replace("\"", "").replace("\uFEFF", "").trim();
                            q.audioFile = parts[1].replace("\"", "").trim();
                            q.transcript = parts[2].replace("\"", "").trim();

                            q.q1Text = parts[3].replace("\"", "").trim(); q.q1A = parts[4].replace("\"", "").trim(); q.q1B = parts[5].replace("\"", "").trim(); q.q1C = parts[6].replace("\"", "").trim(); q.q1D = parts[7].replace("\"", "").trim(); q.q1Correct = parts[8].replace("\"", "").trim();
                            q.q2Text = parts[9].replace("\"", "").trim(); q.q2A = parts[10].replace("\"", "").trim(); q.q2B = parts[11].replace("\"", "").trim(); q.q2C = parts[12].replace("\"", "").trim(); q.q2D = parts[13].replace("\"", "").trim(); q.q2Correct = parts[14].replace("\"", "").trim();
                            q.q3Text = parts[15].replace("\"", "").trim(); q.q3A = parts[16].replace("\"", "").trim(); q.q3B = parts[17].replace("\"", "").trim(); q.q3C = parts[18].replace("\"", "").trim(); q.q3D = parts[19].replace("\"", "").trim(); q.q3Correct = parts[20].replace("\"", "").trim();
                            q.q4Text = parts[21].replace("\"", "").trim(); q.q4A = parts[22].replace("\"", "").trim(); q.q4B = parts[23].replace("\"", "").trim(); q.q4C = parts[24].replace("\"", "").trim(); q.q4D = parts[25].replace("\"", "").trim(); q.q4Correct = parts[26].replace("\"", "").trim();

                            bulkInsertList.add(q);
                        } else {
                            // In ra log đỏ để báo lỗi thiếu cột
                            android.util.Log.e("CSV_ERROR", "Dòng " + rowCount + " bị thiếu cột! Chỉ có " + parts.length + " cột.");
                        }
                    }
                    reader.close();

                    if (!bulkInsertList.isEmpty()) {
                        db.listeningDao().insertAll(bulkInsertList);
                        runOnUiThread(() -> android.widget.Toast.makeText(MainActivity.this, "Đã nạp " + bulkInsertList.size() + " bài Nghe hiểu!", android.widget.Toast.LENGTH_LONG).show());
                    } else {
                        runOnUiThread(() -> android.widget.Toast.makeText(MainActivity.this, "File CSV trống hoặc sai định dạng!", android.widget.Toast.LENGTH_LONG).show());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> android.widget.Toast.makeText(MainActivity.this, "Lỗi đọc file: " + e.getMessage(), android.widget.Toast.LENGTH_LONG).show());
                }
            }
        });
    }
    private void loadPronunciationQuestionsFromCSV() {
        VocaVerseDatabase db = VocaVerseDatabase.getDatabase(this);
        VocaVerseDatabase.databaseWriteExecutor.execute(() -> {
            // Kiểm tra xem kho đã có dữ liệu chưa
            if (db.pronunciationDao().getCount() == 0) {
                try {
                    java.io.InputStream is = getAssets().open("pronunciation_questions.csv");
                    java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(is));
                    String line;
                    reader.readLine(); // Bỏ qua dòng tiêu đề

                    List<PronunciationQuestion> bulkList = new ArrayList<>();

                    while ((line = reader.readLine()) != null) {
                        // Regex thần thánh để tách dấu phẩy trong ngoặc kép
                        String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

                        if (parts.length >= 2) {
                            String lessonId = parts[0].replace("\"", "").trim();
                            String transcript = parts[1].replace("\"", "").trim();

                            bulkList.add(new PronunciationQuestion(lessonId, transcript));
                        }
                    }
                    reader.close();

                    if (!bulkList.isEmpty()) {
                        db.pronunciationDao().insertAll(bulkList);
                        runOnUiThread(() -> Toast.makeText(MainActivity.this,
                                "Nạp xong " + bulkList.size() + " câu luyện phát âm!", Toast.LENGTH_SHORT).show());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initializeLessonData() {
        VocaVerseDatabase.databaseWriteExecutor.execute(() -> {
            List<VocabularyItem> existing = database.vocabularyDao().getVocabularyByLessonSync(1, "Tiếng Anh");
            if (existing == null || existing.isEmpty()) {
                List<VocabularyItem> allItems = CSVHelper.readVocabularyFromCSV(this, "english_lessons.csv", "Tiếng Anh");
                if (allItems != null && !allItems.isEmpty()) {
                    database.vocabularyDao().insertAll(allItems);
                }
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        if (nav != null) {
            NavigationHelper.setup(this, nav, R.id.nav_study);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        if (nav != null) {
            nav.setSelectedItemId(R.id.nav_study);
        }
        if (moduleList != null && !moduleList.isEmpty()) {
            updateModuleCounts();
        }
    }
}