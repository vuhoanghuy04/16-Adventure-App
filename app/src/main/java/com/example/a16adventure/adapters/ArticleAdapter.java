package com.example.a16adventure.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide; // Thư viện tải ảnh thần thánh
import com.example.a16adventure.R;
import com.example.a16adventure.activities.ArticleDetailActivity;
import com.example.a16adventure.models.Article;
import com.example.a16adventure.util.AppConstants;

import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder> {

    private List<Article> articleList;

    public ArticleAdapter(List<Article> articleList) {
        this.articleList = articleList;
    }

    public void updateData(List<Article> newList) {
        this.articleList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_article, parent, false);
        return new ArticleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        Article article = articleList.get(position);

        // 1. Gắn chữ vào đúng ID thiết kế của Huy
        if (holder.tvArticleTitle != null) holder.tvArticleTitle.setText(article.getTitle());
        if (holder.tvCategory != null) holder.tvCategory.setText(article.getCategory());
        if (holder.tvTime != null) holder.tvTime.setText(article.getTimeAndViews());

        // 2. Tải ảnh vào ô imgThumbnail
        if (article.getImageUrl() != null && !article.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(article.getImageUrl())
                    .placeholder(R.drawable.bg_search_bar) // Ảnh chờ tải
                    .into(holder.imgThumbnail);
        }

        // 3. Sự kiện Click: Đóng gói toàn bộ đồ đạc gửi sang trang Chi tiết
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ArticleDetailActivity.class);
                intent.putExtra(AppConstants.Extras.ARTICLE_TITLE, article.getTitle());
                intent.putExtra(AppConstants.Extras.ARTICLE_CATEGORY, article.getCategory());
                intent.putExtra(AppConstants.Extras.ARTICLE_TIME_VIEWS, article.getTimeAndViews());
                intent.putExtra(AppConstants.Extras.ARTICLE_CONTENT, article.getContent());
                intent.putExtra(AppConstants.Extras.IMAGE_URL, article.getImageUrl());
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

    public static class ArticleViewHolder extends RecyclerView.ViewHolder {
        ImageView imgThumbnail;
        TextView tvCategory, tvArticleTitle, tvTime;

        public ArticleViewHolder(@NonNull View itemView) {
            super(itemView);
            // KẾT NỐI ĐÚNG VỚI CÁC ID TRONG FILE item_article.xml
            imgThumbnail = itemView.findViewById(R.id.imgThumbnail);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvArticleTitle = itemView.findViewById(R.id.tvArticleTitle);
            tvTime = itemView.findViewById(R.id.tvTime);
        }
    }
}
