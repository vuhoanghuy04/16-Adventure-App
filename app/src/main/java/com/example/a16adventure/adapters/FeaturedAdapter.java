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

        // --- SỰ KIỆN CLICK VÀO BÀI VIẾT NỔI BẬT ---
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Gọi chuyến xe Intent để đi sang trang DetailActivity
                Intent intent = new Intent(v.getContext(), DetailActivity.class);

                // Gửi gắm dữ liệu lên xe
                intent.putExtra("TITLE", article.getTitle());
                intent.putExtra("CATEGORY", article.getCategory());
                intent.putExtra("TIME_VIEWS", article.getTimeAndViews());

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