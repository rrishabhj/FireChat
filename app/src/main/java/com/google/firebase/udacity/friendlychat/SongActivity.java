package com.google.firebase.udacity.friendlychat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ProgressBar;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.udacity.friendlychat.Utils.PrefUtil;
import com.google.firebase.udacity.friendlychat.adaptor.MessageRecyclerViewAdaptor;
import com.google.firebase.udacity.friendlychat.adaptor.SongsRecyclerViewAdaptor;
import com.google.firebase.udacity.friendlychat.model.FriendlyMessage;
import com.google.firebase.udacity.friendlychat.model.Song;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.google.firebase.udacity.friendlychat.Utils.PrefUtil.USER_ID;

public class SongActivity extends AppCompatActivity {

    private String userEmail;

    private RecyclerView songsRecyclerView;
    private List<Song> songList = new ArrayList<>();
    private SongsRecyclerViewAdaptor mAdapter;
    private LinearLayoutManager mLayoutManager;
    private ChildEventListener mChildEventListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mSongsDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);

        songsRecyclerView = (RecyclerView) findViewById(R.id.rv_songs);

        // data from intent
        userEmail = getIntent().getStringExtra("user_id");


        mAdapter = new SongsRecyclerViewAdaptor(songList);
        mLayoutManager = new LinearLayoutManager(SongActivity.this);
        songsRecyclerView.setLayoutManager(mLayoutManager);
        songsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        songsRecyclerView.setAdapter(mAdapter);


        // Initialize Firebase components
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mSongsDatabaseReference = mFirebaseDatabase.getReference().child("songs").child(userEmail);
    }


    private void attachDatabaseReadListener() {


        if (mSongsDatabaseReference!=null) {

            if (mChildEventListener == null) {
                mChildEventListener = new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        Song song = dataSnapshot.getValue(Song.class);
                        songList.add(song);
                        mAdapter.notifyDataSetChanged();
                    }

                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    }

                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                    }

                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                    }

                    public void onCancelled(DatabaseError databaseError) {

                    }
                };
                mSongsDatabaseReference.addChildEventListener(mChildEventListener);


            } else {
                Log.i("SongActivity", "Sync Failed");
//            mProgressBar.setVisibility(ProgressBar.INVISIBLE);
            }
        }
    }

    private void detachDatabaseReadListener() {
        if (mChildEventListener != null) {
            mSongsDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        attachDatabaseReadListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAdapter.clear();
        detachDatabaseReadListener();
    }


}
