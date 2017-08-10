package com.google.firebase.udacity.friendlychat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.udacity.friendlychat.Utils.PrefUtil;
import com.google.firebase.udacity.friendlychat.adaptor.UserRecyclerViewAdaptor;
import com.google.firebase.udacity.friendlychat.adaptor.UserRecyclerViewListener;
import com.google.firebase.udacity.friendlychat.adaptor.UsersSongsRecyclerViewAdaptor;
import com.google.firebase.udacity.friendlychat.model.User;
import com.google.firebase.udacity.friendlychat.tabs.ChatFragment;

import java.util.ArrayList;
import java.util.List;

import static com.google.firebase.udacity.friendlychat.Utils.PrefUtil.USER_ID;
import static com.google.firebase.udacity.friendlychat.Utils.PrefUtil.USER_NAME;

public class ContactsActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUsersDatabaseReference;
    private RecyclerView recyclerView;
    private ProgressBar mProgressBar;
    private List<User> userList = new ArrayList<>();
    private ChildEventListener mChildEventListener;
    private Toolbar toolbar;
    private UsersSongsRecyclerViewAdaptor mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Select Contact");


        // Initialize Firebase components
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUsersDatabaseReference = mFirebaseDatabase.getReference().child("users");

        // init views
        recyclerView = (RecyclerView) findViewById(R.id.rv_message);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        mProgressBar.setVisibility(ProgressBar.INVISIBLE);


        //Init RecyclerView
        mAdapter = new UsersSongsRecyclerViewAdaptor(ContactsActivity.this , userList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ContactsActivity.this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        // onTouchCLick Listener
        recyclerView.addOnItemTouchListener(new UserRecyclerViewListener(ContactsActivity.this, recyclerView, new ChatFragment.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                Intent chatActivity = new Intent(ContactsActivity.this,ChatActiivty.class);

                final String db_Email= userList.get(position).getEmail().split("@")[0];
                final String db_name= userList.get(position).getName();
                chatActivity.putExtra(USER_ID,db_Email);
                chatActivity.putExtra(USER_NAME,db_name);
                startActivity(chatActivity);
                Toast.makeText(ContactsActivity.this,"Success"+userList.get(position).getU_id(),Toast.LENGTH_LONG).show();
            }

            @Override
            public void onLongClick(View view, int position) {

                Intent intent = new Intent(ContactsActivity.this, SongsProfile.class);
                final String dbEmail= userList.get(position).getEmail().split("@")[0];
                intent.putExtra(USER_ID, dbEmail);
                startActivity(intent);

            }
        }));

        attachDatabaseReadListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        detachDatabaseReadListener();
    }

    private void attachDatabaseReadListener() {

        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    // Initialize progress bar
                    mProgressBar.setVisibility(ProgressBar.INVISIBLE);

                    User friendlyUser = dataSnapshot.getValue(User.class);
                    //                    mMessageAdapter.add(friendlyUser);

                    if (!(friendlyUser.getEmail().split("@")[0].equals(PrefUtil.getEmail(ContactsActivity.this).split("@")[0] ))){
                        userList.add(friendlyUser);
                        mAdapter.notifyDataSetChanged();
                    }
                }

                public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
                public void onChildRemoved(DataSnapshot dataSnapshot) {}
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                public void onCancelled(DatabaseError databaseError) {
                    // Initialize progress bar
                    mProgressBar.setVisibility(ProgressBar.INVISIBLE);

                }
            };
            mUsersDatabaseReference.orderByValue().limitToLast(100).addChildEventListener(mChildEventListener);
            mUsersDatabaseReference.keepSynced(true);
        }
    }

    private void detachDatabaseReadListener() {
        if (mChildEventListener != null) {
            mUsersDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }

}
