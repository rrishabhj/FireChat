package com.google.firebase.udacity.friendlychat;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.udacity.friendlychat.Utils.PrefUtil;
import com.google.firebase.udacity.friendlychat.adaptor.SongsRecyclerViewAdaptor;
import com.google.firebase.udacity.friendlychat.model.Song;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

public class MySongsActivity extends AppCompatActivity {
    private String userEmail;

    private RecyclerView songsRecyclerView;
    private List<Song> songList = new ArrayList<>();
    private SongsRecyclerViewAdaptor mAdapter;
    private LinearLayoutManager mLayoutManager;
    private ChildEventListener mChildEventListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mSongsDatabaseReference;
    private Cursor cursor;
    private Toolbar toolbar;
    private boolean isSelectAll;
    private List<Song> currentSelectedItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        songsRecyclerView = (RecyclerView) findViewById(R.id.rv_songs);



//        // data from intent
//        userEmail = getIntent().getStringExtra("user_id");

        // Initialize Firebase components
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mSongsDatabaseReference = mFirebaseDatabase.getReference().child("songs").child(PrefUtil.getEmail(MySongsActivity.this).split("@")[0]);


        mAdapter = new SongsRecyclerViewAdaptor(songList, new SongsRecyclerViewAdaptor.OnItemCheckListener() {
            @Override
            public void onItemCheck(Song item) {
                currentSelectedItems.add(item);
            }

            @Override
            public void onItemUncheck(Song item) {
                currentSelectedItems.remove(item);
            }
        });

        mLayoutManager = new LinearLayoutManager(MySongsActivity.this);
        songsRecyclerView.setLayoutManager(mLayoutManager);
        songsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        songsRecyclerView.setAdapter(mAdapter);

        if (getMp3Songs()!=null) {
            songList.addAll(getMp3Songs());
            mAdapter.notifyDataSetChanged();
        }
    }


    public ArrayList<Song> getMp3Songs() {

        Uri allsongsuri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        ArrayList<Song> songsList = new ArrayList<>();

        String[] projection = { MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.SIZE };

        cursor = getContentResolver().query(allsongsuri, projection, selection, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {

                String song_name;
                do {
                    song_name = cursor
                            .getString(cursor
                                    .getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                    String song_id = String.valueOf(cursor.getInt(cursor
                            .getColumnIndex(MediaStore.Audio.Media._ID)));

                    String fullpath = cursor.getString(cursor
                            .getColumnIndex(MediaStore.Audio.Media.DATA));
//					fullsongpath.add(fullpath);

//                    Bitmap cover  = getSongCoverImage(Uri.fromFile(new File(fullpath)));

                    String size = String.valueOf(cursor.getInt(cursor
                            .getColumnIndex(MediaStore.Audio.Media.SIZE))/(1024*1024));

//					String album_name = cursor.getString(cursor
//							.getColumnIndex(MediaStore.Audio.Media.ALBUM));
//					int album_id = cursor.getInt(cursor
//							.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));

//                    artist_name = cursor.getString(cursor
//                            .getColumnIndex(MediaStore.Audio.Media.ARTIST));
//                    int artist_id = cursor.getInt(cursor
//                            .getColumnIndex(MediaStore.Audio.Media.ARTIST_ID));

                    Song mySong = new Song(song_name,song_id,fullpath,song_name,size);


                    // sending song object one by one
                    songsList.add(mySong);



                } while (cursor.moveToNext());

                Log.d("Mainactivity",songsList.toString());

                return songsList;

            }
            cursor.close();
        }
        return null;
    }


    // return the bitmap of the cover image if present else get default image from the uri of the song
    public Bitmap getSongCoverImage(Uri uri){

        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        byte[] rawArt;
        BitmapFactory.Options bfo=new BitmapFactory.Options();

        mmr.setDataSource(getApplicationContext(), uri);
        rawArt = mmr.getEmbeddedPicture();

        return rawArt != null ? BitmapFactory.decodeByteArray(rawArt, 0, rawArt.length, bfo): BitmapFactory.decodeResource(getResources() , R.drawable.ic_default_cover_song) ;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(  R.menu.songs_list_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.select_all_menu:
                selectAllSongs();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void selectAllSongs() {

        if (mAdapter != null) {
            if (!isSelectAll) {
                mAdapter.checkAllItems(true);
                isSelectAll = true;
            } else {
                mAdapter.checkAllItems(false);
                isSelectAll = false;
            }
        }
    }

    public void uploadSongs(View view){

        if (currentSelectedItems!=null) {
            for (Song song : currentSelectedItems) {
                mSongsDatabaseReference.child(song.getMediaId()).setValue(song);
                Toast.makeText(MySongsActivity.this,song.getMediaName(),Toast.LENGTH_SHORT).show();
            }
        }
        finish();
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


//    @Override
//    protected void onResume() {
//        super.onResume();
//        attachDatabaseReadListener();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        mAdapter.clear();
//        detachDatabaseReadListener();
//    }

}
