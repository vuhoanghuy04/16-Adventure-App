package com.duolingo.app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.duolingo.app.R;
import com.duolingo.app.models.ChatMessage;
import com.duolingo.app.utils.MarkdownHelper;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_USER = 1;
    private static final int TYPE_DUDU = 2;

    private List<ChatMessage> chatList;

    public ChatAdapter(List<ChatMessage> chatList) {
        this.chatList = chatList;
    }

    @Override
    public int getItemViewType(int position) {
        if (chatList.get(position).isUser()) {
            return TYPE_USER;
        } else {
            return TYPE_DUDU;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_USER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message_user, parent, false);
            return new UserViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message_dudu, parent, false);
            return new DuDuViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = chatList.get(position);
        if (holder instanceof UserViewHolder) {
            ((UserViewHolder) holder).tvMessage.setText(MarkdownHelper.parse(message.getText()));
            ((UserViewHolder) holder).tvTime.setText(message.getTimeFormatted());
        } else if (holder instanceof DuDuViewHolder) {
            ((DuDuViewHolder) holder).tvMessage.setText(MarkdownHelper.parse(message.getText()));
            ((DuDuViewHolder) holder).tvTime.setText(message.getTimeFormatted());
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage, tvTime;
        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tv_user_message);
            tvTime = itemView.findViewById(R.id.tv_user_time);
        }
    }

    static class DuDuViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage, tvTime;
        DuDuViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tv_bot_message);
            tvTime = itemView.findViewById(R.id.tv_bot_time);
        }
    }
}
