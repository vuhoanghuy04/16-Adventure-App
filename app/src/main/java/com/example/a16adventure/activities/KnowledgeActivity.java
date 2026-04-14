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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Đã cập nhật trỏ về đúng tên file giao diện mới
        setContentView(R.layout.activity_knowledge);

        // Vẫn giữ thanh điều hướng (Nếu trang này là trang con, sau này leader có thể bỏ dòng này đi)
        setupBottomNavigation(R.id.bottomNavigation, R.id.nav_explore);

        // --- 0. ÁNH XẠ ---
        chipGroupCategories = findViewById(R.id.chipGroupCategories);
        chipAll = findViewById(R.id.chipAll);
        chipCustoms = findViewById(R.id.chipCustoms);
        chipFood = findViewById(R.id.chipFood);
        chipHistory = findViewById(R.id.chipHistory);
        edtSearch = findViewById(R.id.edtSearch);

        // --- 1. NẠP DỮ LIỆU THẬT (12 BÀI VIẾT) ---
        articleList = new ArrayList<>();

        // THỂ LOẠI: LỊCH SỬ (4 bài)
        articleList.add(new Article("Lịch sử hình thành thành phố Cảng", "Lịch sử", "🕒 8 phút   👁 3.420", 0));
        articleList.add(new Article("Di tích bến tàu Không Số K15", "Lịch sử", "🕒 6 phút   👁 1.250", 0));
        articleList.add(new Article("Trận chiến trên sông Bạch Đằng lịch sử", "Lịch sử", "🕒 12 phút  👁 5.100", 0));
        articleList.add(new Article("Nữ tướng Lê Chân và công cuộc khai hoang", "Lịch sử", "🕒 9 phút   👁 2.800", 0));

        // THỂ LOẠI: PHONG TỤC (4 bài)
        articleList.add(new Article("Phong tục cưới hỏi truyền thống Hải Phòng", "Phong tục", "🕒 5 phút   👁 2.210", 0));
        articleList.add(new Article("Tết Nguyên Đán ở Hải Phòng xưa và nay", "Phong tục", "🕒 7 phút   👁 3.100", 0));
        articleList.add(new Article("Lễ hội Chọi Trâu Đồ Sơn", "Phong tục", "🕒 10 phút  👁 4.500", 0));
        articleList.add(new Article("Tín ngưỡng thờ Mẫu tại đền Nghè", "Phong tục", "🕒 6 phút   👁 1.800", 0));

        // THỂ LOẠI: ẨM THỰC (4 bài)
        articleList.add(new Article("Ẩm thực đường phố Hải Phòng - Thiên đường vị giác", "Ẩm thực", "🕒 10 phút  👁 8.120", 0));
        articleList.add(new Article("Nghệ thuật ẩm thực hải sản Cát Bà", "Ẩm thực", "🕒 8 phút   👁 6.050", 0));
        articleList.add(new Article("Bánh đa cua - Linh hồn ẩm thực đất Cảng", "Ẩm thực", "🕒 5 phút   👁 9.200", 0));
        articleList.add(new Article("Nem cua bể và cách làm truyền thống", "Ẩm thực", "🕒 7 phút   👁 5.400", 0));

        // Danh sách Nổi bật (Vuốt ngang)
        featuredList = new ArrayList<>();
        featuredList.add(new Article("Lịch sử hình thành thành phố Cảng", "Lịch sử", "🕒 8 phút", 0));
        featuredList.add(new Article("Lễ hội Chọi Trâu Đồ Sơn", "Phong tục", "🕒 10 phút", 0));
        featuredList.add(new Article("Bánh đa cua - Linh hồn ẩm thực đất Cảng", "Ẩm thực", "🕒 5 phút", 0));

        // Đếm tự động số lượng và ghi lên nút
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

        // --- 3. TÍNH NĂNG LỌC ---
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

        // --- 4. TÍNH NĂNG TÌM KIẾM KHÔNG DẤU ---
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String keyword = removeAccents(s.toString().toLowerCase().trim());
                List<Article> searchList = new ArrayList<>();

                for (Article article : articleList) {
                    String titleNoAccent = removeAccents(article.getTitle().toLowerCase());
                    if (titleNoAccent.contains(keyword)) {
                        searchList.add(article);
                    }
                }
                articleAdapter.updateData(searchList);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    // Hàm biến chữ có dấu thành không dấu
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

    // Hàm đếm số lượng
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