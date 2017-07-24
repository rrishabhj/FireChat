package com.google.firebase.udacity.friendlychat.adaptor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.udacity.friendlychat.ChatActiivty;
import com.google.firebase.udacity.friendlychat.R;
import com.google.firebase.udacity.friendlychat.SongsProfile;
import com.google.firebase.udacity.friendlychat.model.User;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by DMI on 24-07-2017.
 */

public class UsersSongsRecyclerViewAdaptor extends RecyclerView.Adapter<UsersSongsRecyclerViewAdaptor .MyViewHolder> {

    private static final String CHAT_NAME = "user_name";
    private List<User> userList;
    public Context context;
    private String CHAT_USER = "user_id";

    public class MyViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
    public TextView username,tvEmail, tvStatus, tvLikes, tvSongs;
        public ImageView imgMessage;
        public CircleImageView circleImageView;

        public MyViewHolder( View view) {
            super(view);


            username = (TextView) view.findViewById(R.id.tv_user_name);
            tvEmail = (TextView) view.findViewById(R.id.tv_user_email);
            tvStatus = (TextView) view.findViewById(R.id.tv_status);
            tvLikes = (TextView) view.findViewById(R.id.tv_likes);
            tvSongs = (TextView) view.findViewById(R.id.tv_songs);
            imgMessage  = (ImageView) view.findViewById(R.id.img_message);
            circleImageView = (CircleImageView) view.findViewById(R.id.iv_profile);

            imgMessage.setOnClickListener(this);
            circleImageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            Intent intent = null;

            if (view.getId() == circleImageView.getId()){
                intent = new Intent(context, SongsProfile.class);
            }else if (view.getId() == imgMessage.getId()){
                intent = new Intent(context, ChatActiivty.class);
            }
            intent.putExtra(CHAT_USER, userList.get(getAdapterPosition()).getEmail().split("@")[0]);
            intent.putExtra(CHAT_NAME, userList.get(getAdapterPosition()).getName());
            context.startActivity(intent);
        }
    }


    public UsersSongsRecyclerViewAdaptor(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @Override
    public UsersSongsRecyclerViewAdaptor.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user_list, parent, false);

        return new UsersSongsRecyclerViewAdaptor.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(UsersSongsRecyclerViewAdaptor.MyViewHolder holder, int position) {
        final User user= userList.get(position);
        holder.username.setText(user.getName());
        holder.tvEmail.setText("@"+user.getEmail().split("@")[0]);
        holder.tvStatus.setText(user.getStatus());
        holder.tvLikes.setText(user.getLikes());
        holder.tvSongs.setText(user.getSongsSize());

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void clear(){
        userList.clear();
        notifyDataSetChanged();
    }
}

