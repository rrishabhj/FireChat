package com.google.firebase.udacity.friendlychat.adaptor;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.udacity.friendlychat.R;
import com.google.firebase.udacity.friendlychat.model.Song;
import com.google.firebase.udacity.friendlychat.model.User;

import java.util.List;

/**
 * Created by DMI on 21-07-2017.
 */

public class SongsRecyclerViewAdaptor  extends RecyclerView.Adapter<SongsRecyclerViewAdaptor .MyViewHolder> {

    private List<Song> songList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView songName,songSize;

        public MyViewHolder(View view) {
            super(view);
            songName = (TextView) view.findViewById(R.id.tv_song_name);
            songSize = (TextView) view.findViewById(R.id.tv_song_size);
        }
    }


    public SongsRecyclerViewAdaptor(List<Song> songList) {
        this.songList = songList;
    }

    @Override
    public SongsRecyclerViewAdaptor.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_song, parent, false);

        return new SongsRecyclerViewAdaptor.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SongsRecyclerViewAdaptor.MyViewHolder holder, int position) {
        Song song= songList.get(position);
        holder.songName.setText(song.getMediaName());

        // is receipt is being used as status
        holder.songSize.setText(song.getMediaSize());
    }

    @Override
    public int getItemCount() {
        return songList==null ? 0 : songList.size();
    }

    public void clear(){
        songList.clear();
        notifyDataSetChanged();
    }
}
