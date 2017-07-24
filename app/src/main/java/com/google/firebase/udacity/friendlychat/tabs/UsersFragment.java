package com.google.firebase.udacity.friendlychat.tabs;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.udacity.friendlychat.ChatActiivty;
import com.google.firebase.udacity.friendlychat.R;
import com.google.firebase.udacity.friendlychat.SongsProfile;
import com.google.firebase.udacity.friendlychat.Utils.PrefUtil;
import com.google.firebase.udacity.friendlychat.adaptor.TabsPagerAdapter;
import com.google.firebase.udacity.friendlychat.adaptor.UserRecyclerViewAdaptor;
import com.google.firebase.udacity.friendlychat.adaptor.UserRecyclerViewListener;
import com.google.firebase.udacity.friendlychat.adaptor.UsersSongsRecyclerViewAdaptor;
import com.google.firebase.udacity.friendlychat.model.Song;
import com.google.firebase.udacity.friendlychat.model.User;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by DMI on 24-07-2017.
 */

public class UsersFragment extends Fragment {

    private static final String TAG = "MainActivity";
    private static final int RC_PHOTO_PICKER = 2;
    public static final String ANONYMOUS = "anonymous";
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;
    public static final String FRIENDLY_MSG_LENGTH_KEY = "friendly_msg_length";

    public static final int RC_SIGN_IN = 1;
    private static final int REQUEST = 101;

    private ListView mMessageListView;
    //    private UserAdaptor mUserAdaptor;
    private ProgressBar mProgressBar;
    private ImageButton mPhotoPickerButton;
    private EditText mMessageEditText;
    private Button mSendButton;

    private String mUsername;

    // Firebase instance variables
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUsersDatabaseReference;
    private DatabaseReference mMessagesDatabaseReference;
    private ChildEventListener mChildEventListener;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mChatPhotosStorageReference;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    private List<User> userList = new ArrayList<>();
    private RecyclerView recyclerView;
    private UsersSongsRecyclerViewAdaptor mAdapter;
    public String USER_ID = "user_id";
    public String USER_NAME = "user_name";

    String isOnline="false";
    private String db_Email;
    private TabLayout tabLayout;
    private Toolbar toolbar;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private ViewPager viewPager;
    private TabsPagerAdapter mTabsAdapter;
    private Cursor cursor;
    private Button syncSongs;
    private ArrayList<Song> songsList;
    private DatabaseReference mSongsDatabaseReference;


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_user, container, false);

        // init views
        recyclerView = (RecyclerView) rootView.findViewById(R.id.rv_users);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);


        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUsersDatabaseReference = mFirebaseDatabase.getReference().child("users");
        mSongsDatabaseReference = mFirebaseDatabase.getReference().child("songs").child(PrefUtil.getEmail(getContext()).split("@")[0]);


        mProgressBar.setVisibility(ProgressBar.INVISIBLE);

        //Init RecyclerView
        mAdapter = new UsersSongsRecyclerViewAdaptor(getContext() , userList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);


//        // onTouchCLick Listener
//        recyclerView.addOnItemTouchListener(new UserRecyclerViewListener(getContext(), recyclerView, new ChatFragment.ClickListener() {
//            @Override
//            public void onClick(View view, int position) {
//
//                Intent chatActivity = new Intent(getContext(),ChatActiivty.class);
//
//                final String db_Email= userList.get(position).getEmail().split("@")[0];
//                final String db_name= userList.get(position).getName();
//                chatActivity.putExtra(USER_ID,db_Email);
//                chatActivity.putExtra(USER_NAME,db_name);
//                startActivity(chatActivity);
//                Toast.makeText(getContext(),"Success"+userList.get(position).getU_id(),Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void onLongClick(View view, int position) {
//
//                Intent intent = new Intent(getContext(), SongsProfile.class);
//                final String dbEmail= userList.get(position).getEmail().split("@")[0];
//                intent.putExtra(USER_ID, dbEmail);
//                startActivity(intent);
//
//            }
//        }));

        return rootView;
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
                    mProgressBar.setVisibility(ProgressBar.INVISIBLE);

                    User friendlyUser = dataSnapshot.getValue(User.class);
                    //                    mMessageAdapter.add(friendlyUser);

                    if (!(friendlyUser.getEmail().split("@")[0].equals(PrefUtil.getEmail(getContext()).split("@")[0] ))){
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
