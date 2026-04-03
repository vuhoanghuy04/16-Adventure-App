package com.example.a16adventure.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.a16adventure.R;
import com.example.a16adventure.models.Message;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_USER = 1;
    private static final int VIEW_TYPE_AI = 2;

    private List<Message> messageList;

    public ChatAdapter(List<Message> messageList) {
        this.messageList = messageList;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);
        if ("user".equals(message.getRole())) {
            return VIEW_TYPE_USER;
        } else {
            return VIEW_TYPE_AI;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_USER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_user, parent, false);
            return new UserViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_ai, parent, false);
            return new AiViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messageList.get(position);
        if (holder instanceof UserViewHolder) {
            ((UserViewHolder) holder).txtUserMessage.setText(message.getContent());
        } else if (holder instanceof AiViewHolder) {
            AiViewHolder aiHolder = (AiViewHolder) holder;
            aiHolder.txtAiMessage.setText(message.getContent());
            
            if (message.getImageUrl() != null && !message.getImageUrl().isEmpty()) {
                aiHolder.imgAiResponse.setVisibility(View.VISIBLE);
                Glide.with(aiHolder.itemView.getContext())
                        .load(message.getImageUrl())
                        .into(aiHolder.imgAiResponse);
            } else {
                aiHolder.imgAiResponse.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView txtUserMessage;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUserMessage = itemView.findViewById(R.id.txtUserMessage);
        }
    }

    static class AiViewHolder extends RecyclerView.ViewHolder {
        TextView txtAiMessage;
        ImageView imgAiResponse;

        AiViewHolder(@NonNull View itemView) {
            super(itemView);
            txtAiMessage = itemView.findViewById(R.id.txtAiMessage);
            imgAiResponse = itemView.findViewById(R.id.imgAiResponse);
        }
    }
}
