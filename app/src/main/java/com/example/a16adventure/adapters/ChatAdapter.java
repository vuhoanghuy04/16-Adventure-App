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
        return "user".equals(message.getRole()) ? VIEW_TYPE_USER : VIEW_TYPE_AI;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_USER) {
            return new UserViewHolder(inflater.inflate(R.layout.item_message_user, parent, false));
        } else {
            return new AiViewHolder(inflater.inflate(R.layout.item_message_ai, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messageList.get(position);
        if (holder instanceof UserViewHolder) {
            UserViewHolder userHolder = (UserViewHolder) holder;
            userHolder.txtUserMessage.setText(message.getContent());
            
            // Hiển thị ảnh của User nếu có
            if (message.getImageUrl() != null && !message.getImageUrl().isEmpty()) {
                userHolder.cardUserImage.setVisibility(View.VISIBLE);
                Glide.with(userHolder.itemView.getContext())
                        .load(message.getImageUrl())
                        .into(userHolder.imgUserMessage);
            } else {
                userHolder.cardUserImage.setVisibility(View.GONE);
                Glide.with(userHolder.itemView.getContext()).clear(userHolder.imgUserMessage);
            }
        } else if (holder instanceof AiViewHolder) {
            AiViewHolder aiHolder = (AiViewHolder) holder;
            aiHolder.txtAiMessage.setText(message.getContent());
            
            // Hiển thị ảnh của AI nếu có
            if (message.getImageUrl() != null && !message.getImageUrl().isEmpty()) {
                aiHolder.imgAiResponse.setVisibility(View.VISIBLE);
                Glide.with(aiHolder.itemView.getContext())
                        .load(message.getImageUrl())
                        .into(aiHolder.imgAiResponse);
            } else {
                aiHolder.imgAiResponse.setVisibility(View.GONE);
                Glide.with(aiHolder.itemView.getContext()).clear(aiHolder.imgAiResponse);
            }
        }
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder instanceof AiViewHolder) {
            Glide.with(holder.itemView.getContext()).clear(((AiViewHolder) holder).imgAiResponse);
        } else if (holder instanceof UserViewHolder) {
            Glide.with(holder.itemView.getContext()).clear(((UserViewHolder) holder).imgUserMessage);
        }
    }

    @Override
    public int getItemCount() {
        return messageList != null ? messageList.size() : 0;
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        final TextView txtUserMessage;
        final ImageView imgUserMessage;
        final View cardUserImage;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUserMessage = itemView.findViewById(R.id.txtUserMessage);
            imgUserMessage = itemView.findViewById(R.id.imgUserMessage);
            cardUserImage = itemView.findViewById(R.id.cardUserImage);
        }
    }

    static class AiViewHolder extends RecyclerView.ViewHolder {
        final TextView txtAiMessage;
        final ImageView imgAiResponse;

        AiViewHolder(@NonNull View itemView) {
            super(itemView);
            txtAiMessage = itemView.findViewById(R.id.txtAiMessage);
            imgAiResponse = itemView.findViewById(R.id.imgAiResponse);
        }
    }
}
