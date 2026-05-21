package com.example.a16adventure.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide; // Import thêm Glide để tải ảnh
import com.example.a16adventure.R;
import com.example.a16adventure.activities.ArticleDetailActivity;
import com.example.a16adventure.models.Article;
import com.example.a16adventure.util.AppConstants;

import java.util.List;

public class FeaturedAdapter extends RecyclerView.Adapter<FeaturedAdapter.FeaturedViewHolder> {

    private List<Article> articleList;

    public FeaturedAdapter(List<Article> articleList) {
        this.articleList = articleList;
    }

    @NonNull
    @Override
    public FeaturedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_featured, parent, false);
        return new FeaturedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeaturedViewHolder holder, int position) {
        Article article = articleList.get(position);

        holder.tvFeaturedTitle.setText(article.getTitle());
        holder.tvFeaturedCategory.setText(article.getCategory());
        holder.tvFeaturedTime.setText(article.getTimeAndViews());

        // --- TẢI ẢNH ĐẠI DIỆN TỪ FIREBASE LÊN THẺ NỔI BẬT ---
        if (article.getImageUrl() != null && !article.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(article.getImageUrl())
                    .placeholder(R.drawable.bg_search_bar) // Ảnh hiện tạm lúc chờ load mạng
                    .into(holder.imgFeatured);
        }

        // --- SỰ KIỆN CLICK VÀO BÀI VIẾT NỔI BẬT ---
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Gọi chuyến xe Intent để đi sang trang ArticleDetailActivity
                Intent intent = new Intent(v.getContext(), ArticleDetailActivity.class);

                // Gửi gắm toàn bộ dữ liệu lên xe
                intent.putExtra(AppConstants.Extras.ARTICLE_TITLE, article.getTitle());
                intent.putExtra(AppConstants.Extras.ARTICLE_CATEGORY, article.getCategory());
                intent.putExtra(AppConstants.Extras.ARTICLE_TIME_VIEWS, article.getTimeAndViews());
                intent.putExtra(AppConstants.Extras.ARTICLE_CONTENT, article.getContent());     // Bổ sung Nội dung
                intent.putExtra(AppConstants.Extras.IMAGE_URL, article.getImageUrl());  // Bổ sung Link ảnh

                // Khởi hành!
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (articleList != null) {
            return articleList.size();
        }
        return 0;
    }

    public static class FeaturedViewHolder extends RecyclerView.ViewHolder {
        ImageView imgFeatured;
        TextView tvFeaturedCategory;
        TextView tvFeaturedTitle;
        TextView tvFeaturedTime;

        public FeaturedViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFeatured = itemView.findViewById(R.id.imgFeatured);
            tvFeaturedCategory = itemView.findViewById(R.id.tvFeaturedCategory);
            tvFeaturedTitle = itemView.findViewById(R.id.tvFeaturedTitle);
            tvFeaturedTime = itemView.findViewById(R.id.tvFeaturedTime);
        }
    }
}
