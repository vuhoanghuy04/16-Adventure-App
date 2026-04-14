package com.example.a16adventure.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.a16adventure.R;
import com.example.a16adventure.activities.ArticleDetailActivity;
import com.example.a16adventure.models.Article;

import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder> {

    private List<Article> articleList;

    public ArticleAdapter(List<Article> articleList) {
        this.articleList = articleList;
    }

    // Hàm cập nhật dữ liệu khi tìm kiếm/lọc
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

        holder.tvTitle.setText(article.getTitle());
        holder.tvCategory.setText("• " + article.getCategory());
        holder.tvTime.setText(article.getTimeAndViews());

        // --- SỰ KIỆN CLICK VÀO BÀI VIẾT ---
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ArticleDetailActivity.class);

                // Đóng gói toàn bộ dữ liệu gửi đi
                intent.putExtra("TITLE", article.getTitle());
                intent.putExtra("CATEGORY", article.getCategory());
                intent.putExtra("TIME_VIEWS", article.getTimeAndViews());
                intent.putExtra("CONTENT", article.getContent());
                intent.putExtra("IMAGE_URL", article.getImageUrl());

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
        TextView tvTitle, tvCategory, tvTime;

        public ArticleViewHolder(@NonNull View itemView) {
            super(itemView);
            // Lưu ý: ID có thể khác một chút tùy file XML của bạn, nếu bị đỏ thì sửa lại cho khớp với item_article.xml nhé
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvTime = itemView.findViewById(R.id.tvTime);
        }
    }
}