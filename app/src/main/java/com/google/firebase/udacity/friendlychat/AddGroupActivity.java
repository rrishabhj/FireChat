package com.google.firebase.udacity.friendlychat;

import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.ProgressBar;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.udacity.friendlychat.Utils.PrefUtil;
import com.google.firebase.udacity.friendlychat.adaptor.UserRecyclerViewAdaptor;
import com.google.firebase.udacity.friendlychat.adaptor.UsersSongsRecyclerViewAdaptor;
import com.google.firebase.udacity.friendlychat.model.User;

import java.util.ArrayList;
import java.util.List;

public class AddGroupActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private List<User> userList=new ArrayList<>();
    private RecyclerView recyclerView;
    private UsersSongsRecyclerViewAdaptor mAdapter;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUsersDatabaseReference;
    private ChildEventListener mChildEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.rv_message);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Select Group");

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUsersDatabaseReference = mFirebaseDatabase.getReference().child("users");


        //Init RecyclerView
        mAdapter = new UsersSongsRecyclerViewAdaptor(AddGroupActivity.this , userList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(AddGroupActivity.this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

    }

    @Override
    public void onResume() {
        super.onResume();
        attachDatabaseReadListener();
    }


    @Override
    public void onPause() {
        super.onPause();
        mAdapter.clear();
        detachDatabaseReadListener();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void attachDatabaseReadListener() {

        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    // Initialize progress bar
                    User friendlyUser = dataSnapshot.getValue(User.class);
                    //                    mMessageAdapter.add(friendlyUser);

                    if (!(friendlyUser.getEmail().split("@")[0].equals(PrefUtil.getEmail(AddGroupActivity.this).split("@")[0] ))){
                        userList.add(friendlyUser);
                        mAdapter.notifyDataSetChanged();
                    }
                }

                public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
                public void onChildRemoved(DataSnapshot dataSnapshot) {}
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            mUsersDatabaseReference.addChildEventListener(mChildEventListener);
        }
    }

    private void detachDatabaseReadListener() {
        if (mChildEventListener != null) {
            mUsersDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }
}
