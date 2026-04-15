package com.example.a16adventure.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import com.example.a16adventure.R;
import com.example.a16adventure.adapters.ArticleAdapter;
import com.example.a16adventure.adapters.FeaturedAdapter;
import com.example.a16adventure.models.Article;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class KnowledgeActivity extends BaseActivity {

    private RecyclerView rvAllArticles;
    private RecyclerView rvFeaturedArticles;
    private ArticleAdapter articleAdapter;
    private FeaturedAdapter featuredAdapter;

    private List<Article> articleList;
    private List<Article> featuredList;

    private ChipGroup chipGroupCategories;
    private Chip chipAll, chipCustoms, chipFood, chipHistory;
    private EditText edtSearch;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_knowledge);
        setupBottomNavigation(R.id.bottomNavigation, R.id.nav_explore);

        // Ánh xạ
        chipGroupCategories = findViewById(R.id.chipGroupCategories);
        chipAll = findViewById(R.id.chipAll);
        chipCustoms = findViewById(R.id.chipCustoms);
        chipFood = findViewById(R.id.chipFood);
        chipHistory = findViewById(R.id.chipHistory);
        edtSearch = findViewById(R.id.edtSearch);

        articleList = new ArrayList<>();
        featuredList = new ArrayList<>();

        rvAllArticles = findViewById(R.id.rvAllArticles);
        articleAdapter = new ArticleAdapter(articleList);
        rvAllArticles.setAdapter(articleAdapter);
        rvAllArticles.setLayoutManager(new LinearLayoutManager(this));

        rvFeaturedArticles = findViewById(R.id.rvFeaturedArticles);
        featuredAdapter = new FeaturedAdapter(featuredList);
        rvFeaturedArticles.setAdapter(featuredAdapter);
        rvFeaturedArticles.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Khởi tạo Firebase và gọi hàm tải dữ liệu
        db = FirebaseFirestore.getInstance();
        fetchArticlesFromFirebase();

        // Tính năng lọc
        chipGroupCategories.setOnCheckedChangeListener((group, checkedId) -> {
            List<Article> filteredList = new ArrayList<>();
            if (checkedId == R.id.chipAll || checkedId == -1) {
                filteredList.addAll(articleList);
            } else if (checkedId == R.id.chipCustoms) {
                for (Article a : articleList) if (a.getCategory() != null && a.getCategory().equals("Phong tục")) filteredList.add(a);
            } else if (checkedId == R.id.chipFood) {
                for (Article a : articleList) if (a.getCategory() != null && a.getCategory().equals("Ẩm thực")) filteredList.add(a);
            } else if (checkedId == R.id.chipHistory) {
                for (Article a : articleList) if (a.getCategory() != null && a.getCategory().equals("Lịch sử")) filteredList.add(a);
            }
            articleAdapter.updateData(filteredList);
        });

        // Tính năng tìm kiếm không dấu
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String keyword = removeAccents(s.toString().toLowerCase().trim());
                List<Article> searchList = new ArrayList<>();
                for (Article article : articleList) {
                    if (article.getTitle() != null) {
                        String titleNoAccent = removeAccents(article.getTitle().toLowerCase());
                        if (titleNoAccent.contains(keyword)) searchList.add(article);
                    }
                }
                articleAdapter.updateData(searchList);
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    // --- HÀM TẢI DỮ LIỆU TỪ FIREBASE ---
    private void fetchArticlesFromFirebase() {
        db.collection("articles")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        articleList.clear();
                        featuredList.clear();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Biến dữ liệu mạng thành đối tượng Article
                            Article article = document.toObject(Article.class);
                            articleList.add(article);

                            // Lọc thông minh: Chỉ những bài viết có isFeatured = true mới được vào mục Nổi bật
                            Boolean isFeatured = document.getBoolean("isFeatured");
                            if (isFeatured != null && isFeatured == true) {
                                featuredList.add(article);
                            }
                        }

                        articleAdapter.notifyDataSetChanged();
                        featuredAdapter.notifyDataSetChanged();
                        countAndUpdateChips();
                    } else {
                        Log.e("FIREBASE", "Lỗi lấy dữ liệu", task.getException());
                        Toast.makeText(KnowledgeActivity.this, "Lỗi kết nối mạng!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public static String removeAccents(String str) {
        if (str == null) return "";
        try {
            String temp = Normalizer.normalize(str, Normalizer.Form.NFD);
            Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
            return pattern.matcher(temp).replaceAll("").replace('đ','d').replace('Đ','D');
        } catch (Exception e) {
            return str;
        }
    }

    private void countAndUpdateChips() {
        int countCustoms = 0, countFood = 0, countHistory = 0;
        for (Article article : articleList) {
            if (article.getCategory() != null) {
                if (article.getCategory().equals("Phong tục")) countCustoms++;
                else if (article.getCategory().equals("Ẩm thực")) countFood++;
                else if (article.getCategory().equals("Lịch sử")) countHistory++;
            }
        }
        chipAll.setText("Tất cả (" + articleList.size() + ")");
        chipCustoms.setText("🔴 Phong tục (" + countCustoms + ")");
        chipFood.setText("🟡 Ẩm thực (" + countFood + ")");
        chipHistory.setText("🔵 Lịch sử (" + countHistory + ")");
    }
}