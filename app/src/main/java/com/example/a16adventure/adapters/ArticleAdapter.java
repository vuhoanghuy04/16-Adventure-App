package com.example.a16adventure.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a16adventure.R;
import com.example.a16adventure.activities.DetailActivity;
import com.example.a16adventure.models.Article;

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

        holder.tvArticleTitle.setText(article.getTitle());
        holder.tvCategory.setText("• " + article.getCategory());
        holder.tvTime.setText(article.getTimeAndViews());

        // --- SỰ KIỆN CLICK VÀO BÀI VIẾT ---
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 1. Gọi chuyến xe Intent để đi sang trang DetailActivity
                Intent intent = new Intent(v.getContext(), DetailActivity.class);

                // 2. Gửi gắm dữ liệu lên xe
                intent.putExtra("TITLE", article.getTitle());
                intent.putExtra("CATEGORY", article.getCategory());
                intent.putExtra("TIME_VIEWS", article.getTimeAndViews());

                // 3. Khởi hành!
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
        TextView tvCategory;
        TextView tvArticleTitle;
        TextView tvTime;

        public ArticleViewHolder(@NonNull View itemView) {
            super(itemView);
            imgThumbnail = itemView.findViewById(R.id.imgThumbnail);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvArticleTitle = itemView.findViewById(R.id.tvArticleTitle);
            tvTime = itemView.findViewById(R.id.tvTime);
        }
    }
}