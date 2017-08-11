package com.google.firebase.udacity.friendlychat;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.storage.UploadTask;
import com.google.firebase.udacity.friendlychat.Utils.PrefUtil;
import com.google.firebase.udacity.friendlychat.adaptor.MessageRecyclerViewAdaptor;
import com.google.firebase.udacity.friendlychat.adaptor.UserRecyclerViewAdaptor;
import com.google.firebase.udacity.friendlychat.model.ChatProperties;
import com.google.firebase.udacity.friendlychat.model.FriendlyMessage;
import com.google.firebase.udacity.friendlychat.model.User;
import com.google.firebase.udacity.friendlychat.service.MyLocationService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.google.firebase.udacity.friendlychat.Utils.Utilities.getStoragePermission;

public class ChatActiivty extends AppCompatActivity {
    public static final int RC_SIGN_IN = 1;
    private static final int RC_PHOTO_PICKER = 2;
    private static final int REQUEST = 101;
    public static final String FRIENDLY_MSG_LENGTH_KEY = "friendly_msg_length";
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;
    private static final String TAG = "ChatActivity";


    private ProgressBar mProgressBar;
    private ImageButton mPhotoPickerButton;
    private EditText mMessageEditText;
    private Button mSendButton;
    private TextView tvIsOnline;
    private TextView tvIsTyping;
    private TextView tvSeen;


    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseStorage mFirebaseStorage;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private DatabaseReference mMessagesDatabaseReference;
//    private DatabaseReference mMessagesReceiptDatabaseReference;
    private DatabaseReference mMessagesMessageDatabaseReference;
    private StorageReference mChatPhotosStorageReference;
    private RecyclerView recyclerView;
    private ChildEventListener mChildEventListener;
    private ChildEventListener mChildChildEventListener;

    private List<FriendlyMessage> messageList = new ArrayList<>();
    private MessageRecyclerViewAdaptor mAdapter;
    private String senderUserId;
    private DatabaseReference mUsersDatabaseReference;
    private DatabaseReference mTypingUsersDatabaseReference;
    private DatabaseReference mTypingUsersDatabaseListnerReference;
    private SimpleDateFormat dateString;
    private ValueEventListener isTypingPostListener;
//    private ValueEventListener isSeeenPostListener;
    private TabLayout tabLayout;
    private Toolbar toolbar;
    private CircleImageView appbarDP;
    private TextView appbarName;
    private ActionBar actionBar;
    private DatabaseReference mUsersDatabaseListnerReference;
    private LinearLayoutManager mLayoutManager;
    private DatabaseReference mChatpropDatabseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_actiivty);


        //init  ActionBar
        initAppBar();

        dateString = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");

        // Initialize Firebase components
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        // Initialize references to views
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mPhotoPickerButton = (ImageButton) findViewById(R.id.photoPickerButton);
        mMessageEditText = (EditText) findViewById(R.id.messageEditText);
        mSendButton = (Button) findViewById(R.id.sendButton);
        recyclerView = (RecyclerView) findViewById(R.id.rv_message);


        // get senders user_id = email
        senderUserId = getIntent().getStringExtra("user_id");

        if (senderUserId==null){
            return;
        }

//        mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("messages").child(PrefUtil.getUserId(ChatActiivty.this)).child(senderUserId).child("chat");


        mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("messages").child(PrefUtil.getEmail(ChatActiivty.this).split("@")[0]).child(senderUserId).child("chat");
        mChatpropDatabseReference = mFirebaseDatabase.getReference().child("messages").child(PrefUtil.getEmail(ChatActiivty.this).split("@")[0]).child(senderUserId);
        mMessagesMessageDatabaseReference = mFirebaseDatabase.getReference().child("messages").child(senderUserId).child(PrefUtil.getEmail(ChatActiivty.this).split("@")[0]).child("chat");

//        mMessagesReceiptDatabaseReference = mFirebaseDatabase.getReference().child("messages").child(senderUserId).child(PrefUtil.getEmail(ChatActiivty.this).split("@")[0]);

        mChatPhotosStorageReference = mFirebaseStorage.getReference().child("chat_photos");

        mUsersDatabaseReference = mFirebaseDatabase.getReference().child("users").child(PrefUtil.getEmail(ChatActiivty.this).split("@")[0]).child("isOnline");
        mUsersDatabaseListnerReference = mFirebaseDatabase.getReference().child("users").child(senderUserId).child("isOnline");

        mTypingUsersDatabaseReference = mFirebaseDatabase.getReference().child("users").child(PrefUtil.getEmail(ChatActiivty.this).split("@")[0]).child("isTyping");
        mTypingUsersDatabaseListnerReference = mFirebaseDatabase.getReference().child("users").child(senderUserId).child("isTyping");

        // init rv
        mAdapter = new MessageRecyclerViewAdaptor(messageList,ChatActiivty.this);
        mLayoutManager = new LinearLayoutManager(ChatActiivty.this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);



        // don't show progressbar in chat
        mProgressBar.setVisibility(View.GONE);



        // isOnline event listner
        ValueEventListener isOnlinePostListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String str= (String) dataSnapshot.getValue();
                if (str.equalsIgnoreCase("true")){
                    tvIsOnline.setVisibility(View.VISIBLE);
                }else{
                    tvIsOnline.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };

        mUsersDatabaseListnerReference.addValueEventListener(isOnlinePostListener);

        // isTyping event listner
        isTypingPostListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String str= (String) dataSnapshot.getValue();
                if (str.equalsIgnoreCase("true")){
                    tvIsTyping.setVisibility(View.VISIBLE);
                }else{
                    tvIsTyping.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };

        mTypingUsersDatabaseListnerReference.addValueEventListener(isTypingPostListener);


        //todo improve logic this is not working
        // isSeen event listner
//        isSeeenPostListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                String str= (String) dataSnapshot.getValue();
//                if(str!=null) {
//                    if (str.equalsIgnoreCase("true")) {
//                        tvSeen.setVisibility(View.VISIBLE);
//                    } else {
//                        tvSeen.setVisibility(View.GONE);
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                // Getting Post failed, log a message
//                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
//                // ...
//            }
//        };

//        mMessagesReceiptDatabaseReference.child("isReceipt").addValueEventListener(isSeeenPostListener);




        // ImagePickerButton shows an image picker to upload a image for a message
        mPhotoPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getStoragePermission(ChatActiivty.this);
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
            }
        });


        // Enable Send button when there's text to send
        mMessageEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mTypingUsersDatabaseReference.setValue("true");
                    mSendButton.setEnabled(true);

                } else {
                    mSendButton.setEnabled(false);
                }

            }


            @Override
            public void afterTextChanged(Editable editable) {

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 100ms
                        mTypingUsersDatabaseReference.setValue("false");
                    }
                }, 500);

            }
        });

        mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});

        // Send button sends a message and clears the EditText
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String chatDate = String.valueOf(System.currentTimeMillis());
                FriendlyMessage friendlyMessage = new FriendlyMessage(mMessageEditText.getText().toString(), chatDate, null);
                mMessagesDatabaseReference.push().setValue(friendlyMessage);
//                mMessagesReceiptDatabaseReference.child("isReceipt").setValue("false");

                String name = getIntent().getStringExtra("user_name");
                ChatProperties chatProperties = new ChatProperties(name, senderUserId,mMessageEditText.getText().toString(),chatDate);
                mChatpropDatabseReference.child("prop").setValue(chatProperties);

                mLayoutManager.scrollToPosition(messageList.size() - 1);

                recyclerView.smoothScrollToPosition(messageList.size());

                // Clear input box
                mMessageEditText.setText("");
            }
        });

    }

    private void initAppBar() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        tvIsOnline = (TextView) findViewById(R.id.appbar_online);
        tvIsTyping = (TextView) findViewById(R.id.appbar_istyping);
        tvSeen = (TextView) findViewById(R.id.tv_seen);
        appbarName = (TextView) findViewById(R.id.appbar_name);

        appbarDP = (CircleImageView)findViewById(R.id.appbar_dp);


        String name = getIntent().getStringExtra("user_name");

        appbarName.setText(name);
        //toolbar.setTitle("InstaGrabber");

    }

    @Override
    protected void onResume() {
        super.onResume();
        mUsersDatabaseReference.setValue("true");
        attachDatabaseReadListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAdapter.clear();
        mUsersDatabaseReference.setValue("false");
//        mMessageAdapter.clear();
        detachDatabaseReadListener();
    }



    private void attachDatabaseReadListener() {

        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    // Initialize progress bar
                    mProgressBar.setVisibility(ProgressBar.INVISIBLE);


                    FriendlyMessage message = dataSnapshot.getValue(FriendlyMessage.class);
//                    mMessageAdapter.add(friendlyUser);
                    message.setText("sender@"+message.getText());
                    messageList.add(message);
                    Collections.sort(messageList);

                    if (messageList.size()!=0) {
                        mLayoutManager.scrollToPosition(messageList.size() - 1);
                        recyclerView.smoothScrollToPosition(messageList.size());
                    }

                    mAdapter.notifyDataSetChanged();

                }

                public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
                public void onChildRemoved(DataSnapshot dataSnapshot) {}
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                public void onCancelled(DatabaseError databaseError) {
                    // Initialize progress bar
                    mProgressBar.setVisibility(ProgressBar.INVISIBLE);

                }
            };
            mMessagesDatabaseReference.addChildEventListener(mChildEventListener);
            mMessagesDatabaseReference.keepSynced(true);



        }else {
            mProgressBar.setVisibility(ProgressBar.INVISIBLE);
        }


        if (mChildChildEventListener== null) {
            mChildChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    // Initialize progress bar
                    mProgressBar.setVisibility(ProgressBar.INVISIBLE);


                    FriendlyMessage message = dataSnapshot.getValue(FriendlyMessage.class);
//                    mMessageAdapter.add(friendlyUser);
                    message.setText("receiver@"+message.getText());
                    messageList.add(message);
//                    mMessagesReceiptDatabaseReference.child("isReceipt").setValue("true");
                    Collections.sort(messageList);

                    if (messageList.size()!=0) {
                        mLayoutManager.scrollToPosition(messageList.size() - 1);
                        recyclerView.smoothScrollToPosition(messageList.size());
                    }

                    mAdapter.notifyDataSetChanged();
                }

                public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
                public void onChildRemoved(DataSnapshot dataSnapshot) {}
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                public void onCancelled(DatabaseError databaseError) {
                    // Initialize progress bar
                    mProgressBar.setVisibility(ProgressBar.INVISIBLE);

                }
            };
            mMessagesMessageDatabaseReference.addChildEventListener(mChildChildEventListener);
            mMessagesMessageDatabaseReference.keepSynced(true);


        }else {
            mProgressBar.setVisibility(ProgressBar.INVISIBLE);
        }


    }


    private void detachDatabaseReadListener() {
        if (mChildEventListener != null ) {
            mMessagesMessageDatabaseReference.removeEventListener(mChildChildEventListener);
            mChildChildEventListener = null;
        }
        if (mChildChildEventListener != null ) {
            mMessagesMessageDatabaseReference.removeEventListener(mChildChildEventListener);
            mChildChildEventListener = null;
        }
        if (isTypingPostListener != null ) {
            mTypingUsersDatabaseListnerReference.removeEventListener(isTypingPostListener);
            isTypingPostListener = null;
        }

//        if (isSeeenPostListener != null ) {
//            mMessagesReceiptDatabaseReference.removeEventListener(isSeeenPostListener);
//            isSeeenPostListener = null;
//        }

    }

    // Fetch the config to determine the allowed length of messages.
    public void fetchConfig() {
        long cacheExpiration = 3600; // 1 hour in seconds
        // If developer mode is enabled reduce cacheExpiration to 0 so that each fetch goes to the
        // server. This should not be used in release builds.
        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }
        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Make the fetched config available
                        // via FirebaseRemoteConfig get<type> calls, e.g., getLong, getString.
                        mFirebaseRemoteConfig.activateFetched();

                        // Update the EditText length limit with
                        // the newly retrieved values from Remote Config.
                        applyRetrievedLengthLimit();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // An error occurred when fetching the config.
                        Log.w(TAG, "Error fetching config", e);

                        // Update the EditText length limit with
                        // the newly retrieved values from Remote Config.
                        applyRetrievedLengthLimit();
                    }
                });
    }

    /**
     * Apply retrieved length limit to edit text field. This result may be fresh from the server or it may be from
     * cached values.
     */
    private void applyRetrievedLengthLimit() {
        Long friendly_msg_length = mFirebaseRemoteConfig.getLong(FRIENDLY_MSG_LENGTH_KEY);
        mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(friendly_msg_length.intValue())});
        Log.d(TAG, FRIENDLY_MSG_LENGTH_KEY + " = " + friendly_msg_length);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
                Uri selectedImageUri = data.getData();

                // Get a reference to store file at chat_photos/<FILENAME>
                StorageReference photoRef = mChatPhotosStorageReference.child(selectedImageUri.getLastPathSegment());


                // Upload file to Firebase Storage
                photoRef.putFile(selectedImageUri)
                        .addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // When the image has successfully uploaded, we get its download URL
                                Uri downloadUrl = taskSnapshot.getDownloadUrl();

                                // Set the download URL to the message box, so that the user can send it to the database
                                FriendlyMessage friendlyMessage = new FriendlyMessage(null, PrefUtil.getUsername(ChatActiivty.this), downloadUrl.toString());
                                mMessagesDatabaseReference.push().setValue(friendlyMessage);
                            }
                        });
            }
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.chat_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.oprn_profile_menu:

                Driver driver = new GooglePlayDriver(ChatActiivty.this);
                FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

                try {
                    Job myJob = dispatcher.newJobBuilder()
                            .setService(MyLocationService.class) // the JobService that will be called
                            .setTag("my-unique-tag")        // uniquely identifies the job
                            .setRecurring(true)
                            .setLifetime(Lifetime.FOREVER)
                            // don't overwrite an existing job with the same tag
                            .setReplaceCurrent(false)
                            .setTrigger(Trigger.executionWindow(0, 120))
                            .build();

                    dispatcher.mustSchedule(myJob);

                }catch (Exception e){
                    Log.d("MainActivity",e.toString());
                }
                return true;
            case R.id.show_loc_menu:

                startActivity(new Intent(ChatActiivty.this, UsersMapsActivity.class));


                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
