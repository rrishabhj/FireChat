package com.google.firebase.udacity.friendlychat;

import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.udacity.friendlychat.Utils.PrefUtil;
import com.google.firebase.udacity.friendlychat.adaptor.SongsPublicRecyclerViewAdapor;
import com.google.firebase.udacity.friendlychat.adaptor.SongsRecyclerViewAdaptor;
import com.google.firebase.udacity.friendlychat.model.Song;

import java.util.ArrayList;
import java.util.List;

public class SongsProfile extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";
    private Toolbar toolbar;
    TextView profileName;
    TextView email,status;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUsersDatabaseReference;
    private ValueEventListener statusChangeListner;
    private Dialog dialog;
    private EditText etStatus;
    private RecyclerView songsRecyclerView;
    private String userEmail;
    private SongsPublicRecyclerViewAdapor mAdapter;
    private List<Song> songList = new ArrayList<>();
    private LinearLayoutManager mLayoutManager;
    private DatabaseReference mSongsDatabaseReference;
    private ChildEventListener mChildEventListener;
    private FloatingActionButton SongsFab;
    private FloatingActionButton songsFab;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs_profile);

        // Initialize Firebase components
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        songsRecyclerView = (RecyclerView) findViewById(R.id.rv_songs);
        songsFab = (FloatingActionButton) findViewById(R.id.fab_add_songs);

//         data email when opening my and others profile
        userEmail = getIntent().getStringExtra("user_id");
        if (userEmail==null){
            userEmail = PrefUtil.getEmail(SongsProfile.this).split("@")[0];
            songsFab.setVisibility(View.VISIBLE);
        }

//        userEmail = "jindalrish";

        mAdapter = new SongsPublicRecyclerViewAdapor(SongsProfile.this, songList, !userEmail.equals(PrefUtil.getEmail(SongsProfile.this).split("@")[0]));
        mLayoutManager = new LinearLayoutManager(SongsProfile.this);
        songsRecyclerView.setLayoutManager(mLayoutManager);
        songsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        songsRecyclerView.setAdapter(mAdapter);


        // Initialize Firebase components
        mSongsDatabaseReference = mFirebaseDatabase.getReference().child("songs").child(userEmail);

        profileName = (TextView) findViewById(R.id.appbar_name);
        profileName.setText(userEmail);

        songsFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SongsProfile.this,MySongsActivity.class));
                finish();
            }
        });

    }

    private void attachDatabaseReadListener() {


        if (mSongsDatabaseReference !=null) {

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
//        mAdapter.clear();
        detachDatabaseReadListener();
    }


//    public interface ClickListener {
//        void onDownload( int position);
//
//        void onUpload(int position);
//
//        void onPlay(int position);
//    }

}
