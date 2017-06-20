package com.google.firebase.udacity.friendlychat.adaptor;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.udacity.friendlychat.R;
import com.google.firebase.udacity.friendlychat.model.FriendlyMessage;
import com.google.firebase.udacity.friendlychat.model.User;

import java.util.List;

/**
 * Created by DMI on 20-06-2017.
 */

public class MessageRecyclerViewAdaptor extends RecyclerView.Adapter<MessageRecyclerViewAdaptor.MyViewHolder> {

    private List<FriendlyMessage> messageList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView username;
        public ImageView photoImageView;

        public MyViewHolder(View view) {
            super(view);
            username = (TextView) view.findViewById(R.id.messageTextView);
            photoImageView = (ImageView) view.findViewById(R.id.photoImageView);
        }
    }


    public  MessageRecyclerViewAdaptor (List<FriendlyMessage> messageList) {
        this.messageList = messageList;
    }

    @Override
    public MessageRecyclerViewAdaptor.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);

        return new MessageRecyclerViewAdaptor .MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MessageRecyclerViewAdaptor .MyViewHolder holder, int position) {
        FriendlyMessage friendlyMessage= messageList.get(position);
        holder.username.setText(friendlyMessage.getText());

        FriendlyMessage message = messageList.get(position);

        boolean isPhoto = message.getPhotoUrl() != null;


        if (isPhoto) {
            holder.username.setVisibility(View.GONE);
            holder.photoImageView.setVisibility(View.VISIBLE);
            Glide.with(holder.photoImageView.getContext())
                    .load(message.getPhotoUrl())
                    .into(holder.photoImageView);
        } else {
            holder.username.setVisibility(View.VISIBLE);
            holder.photoImageView.setVisibility(View.GONE);
            holder.username.setText(message.getText());
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public void clear(){
        messageList.clear();
        notifyDataSetChanged();
    }
}
