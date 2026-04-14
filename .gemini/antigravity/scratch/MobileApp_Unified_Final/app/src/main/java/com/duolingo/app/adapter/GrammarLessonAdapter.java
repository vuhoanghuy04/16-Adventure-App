package com.duolingo.app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.duolingo.app.R;
import com.duolingo.app.models.GrammarLesson;

import java.util.List;

public class GrammarLessonAdapter extends RecyclerView.Adapter<GrammarLessonAdapter.ViewHolder> {

    private List<GrammarLesson> lessonList;
    private OnItemClickListener listener;

    // Interface này là mấu chốt để bắt sự kiện khi em bấm vào 1 dòng
    public interface OnItemClickListener {
        void onItemClick(GrammarLesson lesson);
    }

    // Constructor
    public GrammarLessonAdapter(List<GrammarLesson> lessonList, OnItemClickListener listener) {
        this.lessonList = lessonList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Gọi cái khuôn item_grammar_lesson.xml ra đây
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grammar_lesson, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GrammarLesson lesson = lessonList.get(position);

        holder.tvTitle.setText(lesson.getTitle());
        holder.tvDesc.setText(lesson.getDescription());
        
        if (com.duolingo.app.utils.ProgressHelper.isGrammarCompleted(holder.itemView.getContext(), lesson.getCategoryId())) {
            holder.tvStatus.setVisibility(View.VISIBLE);
            int score = com.duolingo.app.utils.ProgressHelper.getGrammarScore(holder.itemView.getContext(), lesson.getCategoryId());
            int maxScore = com.duolingo.app.utils.ProgressHelper.getGrammarMaxScore(holder.itemView.getContext(), lesson.getCategoryId());
            if (score != -1 && maxScore != -1) {
                holder.tvStatus.setText("✓ Đã hoàn thành (" + score + "/" + maxScore + ")");
            } else {
                holder.tvStatus.setText("✓ Đã hoàn thành");
            }
        } else {
            holder.tvStatus.setVisibility(View.GONE);
        }

        // CHỐT CHẶN Ở ĐÂY: Gắn sự kiện click thẳng vào cái CardView có ID đàng hoàng
        holder.cardLesson.setOnClickListener(v -> listener.onItemClick(lesson));
    }

    @Override
    public int getItemCount() {
        return lessonList.size();
    }

    // Ánh xạ View của từng dòng
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDesc, tvStatus;
        View cardLesson; 

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_lesson_title);
            tvDesc = itemView.findViewById(R.id.tv_lesson_desc);
            tvStatus = itemView.findViewById(R.id.tv_lesson_status);
            cardLesson = itemView.findViewById(R.id.card_lesson); 
        }
    }
}