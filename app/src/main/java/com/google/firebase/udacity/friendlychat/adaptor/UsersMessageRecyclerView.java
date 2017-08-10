package com.google.firebase.udacity.friendlychat.adaptor;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.udacity.friendlychat.R;
import com.google.firebase.udacity.friendlychat.model.User;
import com.google.firebase.udacity.friendlychat.model.UsersMessages;

import java.util.List;

/**
 * Created by DMI on 08-08-2017.
 */

public class UsersMessageRecyclerView extends RecyclerView.Adapter<UsersMessageRecyclerView .MyViewHolder> {

    private List<UsersMessages> userList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView username, tvEmail;

        public MyViewHolder(View view) {
            super(view);
            username = (TextView) view.findViewById(R.id.userTextView);
            tvEmail = (TextView) view.findViewById(R.id.tv_email);
        }
    }


    public UsersMessageRecyclerView(List<UsersMessages> userList) {

        this.userList = userList;
    }

    @Override
    public UsersMessageRecyclerView.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat, parent, false);

        return new UsersMessageRecyclerView.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(UsersMessageRecyclerView.MyViewHolder holder, int position) {
        UsersMessages user = userList.get(position);

        holder.username.setText(user.getUsername());

        // is receipt is being used as status
        holder.tvEmail.setText(user.getLastMessage());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void clear() {
        userList.clear();
        notifyDataSetChanged();
    }
}