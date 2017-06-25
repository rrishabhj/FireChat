package com.google.firebase.udacity.friendlychat.adaptor;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.udacity.friendlychat.R;
import com.google.firebase.udacity.friendlychat.model.FriendlyMessage;
import com.google.firebase.udacity.friendlychat.model.User;

import java.util.List;
import java.util.List;

import static java.security.AccessController.getContext;

/**
 * Created by DMI on 20-06-2017.
 */

public class UserRecyclerViewAdaptor extends RecyclerView.Adapter<UserRecyclerViewAdaptor .MyViewHolder> {

    private List<User> userList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView username;

        public MyViewHolder(View view) {
            super(view);
            username = (TextView) view.findViewById(R.id.userTextView);
        }
    }


    public UserRecyclerViewAdaptor(List<User> userList) {
        this.userList = userList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        User user= userList.get(position);
        holder.username.setText(user.getName());
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
