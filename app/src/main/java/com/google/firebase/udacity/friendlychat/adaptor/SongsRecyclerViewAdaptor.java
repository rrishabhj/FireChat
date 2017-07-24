package com.google.firebase.udacity.friendlychat.adaptor;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
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
    private boolean isSelectedAll=false;

    @NonNull
    private OnItemCheckListener onItemCheckListener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView songName,songSize;
        public LinearLayout itemSong;
        public CheckBox checkBoxSelect;

        public MyViewHolder(View view) {
            super(view);
            songName = (TextView) view.findViewById(R.id.tv_song_name);
            songSize = (TextView) view.findViewById(R.id.tv_song_size);
            itemSong = (LinearLayout) view.findViewById(R.id.ll_item_song);
            checkBoxSelect = (CheckBox) view.findViewById(R.id.checkb_select);
        }
    }


    public SongsRecyclerViewAdaptor(List<Song> songList) {
        this.songList = songList;
    }

    public SongsRecyclerViewAdaptor(List<Song> songList, @NonNull OnItemCheckListener onItemCheckListener) {
        this.songList = songList;

        this.onItemCheckListener = onItemCheckListener;
    }

    @Override
    public SongsRecyclerViewAdaptor.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_song, parent, false);

        return new SongsRecyclerViewAdaptor.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final SongsRecyclerViewAdaptor.MyViewHolder holder, int position) {
        final Song song= songList.get(position);
        holder.songName.setText(song.getMediaName());

        // is receipt is being used as status
        holder.songSize.setText(song.getMediaSize()+" MB");

        // if all items are selected remove checkbox and remove from count

        if (onItemCheckListener!=null) {
            if (!isSelectedAll) {
                holder.checkBoxSelect.setChecked(false);
                onItemCheckListener.onItemUncheck(song);
            } else {
                holder.checkBoxSelect.setChecked(true);
                onItemCheckListener.onItemCheck(song);
            }

            //if item
            holder.checkBoxSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.checkBoxSelect.isChecked()) {
                        onItemCheckListener.onItemCheck(song);
                    } else {
                        onItemCheckListener.onItemUncheck(song);
                    }
                }
            });

        }
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

    public void checkAllItems(boolean isSelected){
        isSelectedAll = isSelected;
        notifyDataSetChanged();
    }


    public interface OnItemCheckListener {
        void onItemCheck(Song item);
        void onItemUncheck(Song item);
    }
}
