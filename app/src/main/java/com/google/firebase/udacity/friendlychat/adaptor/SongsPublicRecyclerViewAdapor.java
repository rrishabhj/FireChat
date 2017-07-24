package com.google.firebase.udacity.friendlychat.adaptor;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.udacity.friendlychat.R;
import com.google.firebase.udacity.friendlychat.model.Song;

import java.util.List;

/**
 * Created by DMI on 24-07-2017.
 */

public class SongsPublicRecyclerViewAdapor extends RecyclerView.Adapter<SongsPublicRecyclerViewAdapor .MyViewHolder>{
    private boolean isRemoteUser= true;
    private List<Song> songList;
    private boolean isSelectedAll=false;
    Context context;

    @NonNull
    private SongsRecyclerViewAdaptor.OnItemCheckListener onItemCheckListener;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView songName,songSize;
        public LinearLayout itemSong;
        public ImageView playImage, downloadImage;

        public MyViewHolder(View view) {
            super(view);
            songName = (TextView) view.findViewById(R.id.tv_song_name);
            songSize = (TextView) view.findViewById(R.id.tv_song_size);
            itemSong = (LinearLayout) view.findViewById(R.id.ll_item_song);
            playImage = (ImageView) view.findViewById(R.id.img_play);
            downloadImage = (ImageView) view.findViewById(R.id.img_download);
            if (!isRemoteUser){
                downloadImage.setImageResource(R.drawable.ic_upload);
            }

            downloadImage.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {

        }
    }


    public SongsPublicRecyclerViewAdapor(List<Song> songList) {
        this.songList = songList;
    }

    // check if this rv is for user of the phone
    public SongsPublicRecyclerViewAdapor(Context context, List<Song> songList, boolean isRemoteUser) {
        this.songList = songList;
        this.isRemoteUser = isRemoteUser;
        this.context = context;
    }

    public SongsPublicRecyclerViewAdapor(List<Song> songList, @NonNull SongsRecyclerViewAdaptor.OnItemCheckListener onItemCheckListener) {
        this.songList = songList;

        this.onItemCheckListener = onItemCheckListener;
    }

    @Override
    public SongsPublicRecyclerViewAdapor.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_public_song, parent, false);

        return new SongsPublicRecyclerViewAdapor.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final SongsPublicRecyclerViewAdapor.MyViewHolder holder, int position) {
        final Song song= songList.get(position);
        holder.songName.setText(song.getMediaName());

        // is receipt is being used as status
        holder.songSize.setText(song.getMediaSize()+" MB");


    }

    @Override
    public int getItemCount() {
        return songList==null ? 0 : songList.size();
    }

    public void clear(){
        if (songList!=null) {
            songList.clear();
            notifyDataSetChanged();
        }
    }
}

