package com.example.a16adventure.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import com.example.a16adventure.R;
import com.example.a16adventure.adapters.ArticleAdapter;
import com.example.a16adventure.adapters.FeaturedAdapter;
import com.example.a16adventure.models.Article;

import java.util.ArrayList;
import java.util.List;

public class ExploreActivity extends BaseActivity {

    // Khai báo các biến danh sách
    private RecyclerView rvAllArticles;
    private RecyclerView rvFeaturedArticles;
    private ArticleAdapter articleAdapter;
    private FeaturedAdapter featuredAdapter;
    private List<Article> articleList;
    private List<Article> featuredList;

    // Khai báo các nút Danh mục và Ô tìm kiếm
    private ChipGroup chipGroupCategories;
    private Chip chipAll, chipCustoms, chipFood, chipHistory;
    private EditText edtSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        // Kích hoạt thanh điều hướng
        setupBottomNavigation(R.id.bottomNavigation, R.id.nav_explore);

        // --- 0. ÁNH XẠ GIAO DIỆN ---
        chipGroupCategories = findViewById(R.id.chipGroupCategories);
        chipAll = findViewById(R.id.chipAll);
        chipCustoms = findViewById(R.id.chipCustoms);
        chipFood = findViewById(R.id.chipFood);
        chipHistory = findViewById(R.id.chipHistory);
        edtSearch = findViewById(R.id.edtSearch);

        // --- 1. TẠO DỮ LIỆU GIẢ ---
        articleList = new ArrayList<>();
        articleList.add(new Article("Lịch sử hình thành thành phố Cảng", "Lịch sử", "🕒 8 phút   👁 3.420", 0));
        articleList.add(new Article("Phong tục cưới hỏi truyền thống Hải Phòng", "Phong tục", "🕒 5 phút   👁 2.210", 0));
        articleList.add(new Article("Ẩm thực đường phố Hải Phòng - Thiên đường vị giác", "Ẩm thực", "🕒 10 phút  👁 8.120", 0));

        featuredList = new ArrayList<>();
        featuredList.add(new Article("Lịch sử hình thành thành phố Cảng", "Lịch sử", "🕒 8 phút", 0));
        featuredList.add(new Article("Tết Nguyên Đán ở Hải Phòng xưa và nay", "Phong tục", "🕒 6 phút", 0));
        featuredList.add(new Article("Lễ hội Chọi Trâu Đồ Sơn", "Lễ hội", "🕒 12 phút", 0));

        // Đếm tự động số lượng bài viết
        countAndUpdateChips();

        // --- 2. CÀI ĐẶT DANH SÁCH ---
        rvAllArticles = findViewById(R.id.rvAllArticles);
        articleAdapter = new ArticleAdapter(articleList);
        rvAllArticles.setAdapter(articleAdapter);
        rvAllArticles.setLayoutManager(new LinearLayoutManager(this));

        rvFeaturedArticles = findViewById(R.id.rvFeaturedArticles);
        featuredAdapter = new FeaturedAdapter(featuredList);
        rvFeaturedArticles.setAdapter(featuredAdapter);
        rvFeaturedArticles.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // --- 3. TÍNH NĂNG LỌC KHI BẤM NÚT DANH MỤC ---
        chipGroupCategories.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                List<Article> filteredList = new ArrayList<>();

                if (checkedId == R.id.chipAll || checkedId == -1) {
                    filteredList.addAll(articleList);
                } else if (checkedId == R.id.chipCustoms) {
                    for (Article a : articleList) {
                        if (a.getCategory().equals("Phong tục")) filteredList.add(a);
                    }
                } else if (checkedId == R.id.chipFood) {
                    for (Article a : articleList) {
                        if (a.getCategory().equals("Ẩm thực")) filteredList.add(a);
                    }
                } else if (checkedId == R.id.chipHistory) {
                    for (Article a : articleList) {
                        if (a.getCategory().equals("Lịch sử")) filteredList.add(a);
                    }
                }

                articleAdapter.updateData(filteredList);
            }
        });

        // --- 4. TÍNH NĂNG TÌM KIẾM THEO CHỮ ---
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String keyword = s.toString().toLowerCase().trim();
                List<Article> searchList = new ArrayList<>();

                for (Article article : articleList) {
                    if (article.getTitle().toLowerCase().contains(keyword)) {
                        searchList.add(article);
                    }
                }

                articleAdapter.updateData(searchList);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    // Hàm đếm số lượng bài viết
    private void countAndUpdateChips() {
        int countCustoms = 0;
        int countFood = 0;
        int countHistory = 0;

        for (Article article : articleList) {
            if (article.getCategory().equals("Phong tục")) countCustoms++;
            else if (article.getCategory().equals("Ẩm thực")) countFood++;
            else if (article.getCategory().equals("Lịch sử")) countHistory++;
        }

        chipAll.setText("Tất cả (" + articleList.size() + ")");
        chipCustoms.setText("🔴 Phong tục (" + countCustoms + ")");
        chipFood.setText("🟡 Ẩm thực (" + countFood + ")");
        chipHistory.setText("🔵 Lịch sử (" + countHistory + ")");
    }
}