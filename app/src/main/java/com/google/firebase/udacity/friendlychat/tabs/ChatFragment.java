package com.google.firebase.udacity.friendlychat.tabs;

import android.content.Intent;
import android.database.Cursor;
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
import com.google.firebase.udacity.friendlychat.MainActivity;
import com.google.firebase.udacity.friendlychat.R;
import com.google.firebase.udacity.friendlychat.SongActivity;
import com.google.firebase.udacity.friendlychat.Utils.PrefUtil;
import com.google.firebase.udacity.friendlychat.Utils.Utilities;
import com.google.firebase.udacity.friendlychat.adaptor.TabsPagerAdapter;
import com.google.firebase.udacity.friendlychat.adaptor.UserRecyclerViewAdaptor;
import com.google.firebase.udacity.friendlychat.adaptor.UserRecyclerViewListener;
import com.google.firebase.udacity.friendlychat.model.Song;
import com.google.firebase.udacity.friendlychat.model.User;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getApplicationContext;

public class ChatFragment extends Fragment {

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
	private UserRecyclerViewAdaptor mAdapter;
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

		View rootView = inflater.inflate(R.layout.fragment_chat, container, false);

		// init views
		recyclerView = (RecyclerView) rootView.findViewById(R.id.rv_message);
		mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
		syncSongs = (Button)rootView.findViewById(R.id.btn_sync_Songs);

		// Initialize Firebase components
		mFirebaseDatabase = FirebaseDatabase.getInstance();
		mFirebaseStorage = FirebaseStorage.getInstance();
		mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
		mFirebaseAuth = FirebaseAuth.getInstance();

		mUsersDatabaseReference = mFirebaseDatabase.getReference().child("users");
		mSongsDatabaseReference = mFirebaseDatabase.getReference().child("songs").child(PrefUtil.getEmail(getContext()).split("@")[0]);
//		mChatPhotosStorageReference = mFirebaseStorage.getReference().child("chat_photos");



		//        PrefUtil.clearSharedPreferences(this);
		// Initialize progress bar
		mProgressBar.setVisibility(ProgressBar.INVISIBLE);

		//         Initialize users ListView and its adapter
		//        List<User> friendlyUsers = new ArrayList<>();
		//        mUserAdaptor = new UserAdaptor(this, R.layout.item_user, friendlyUsers);
		//        mMessageListView.setAdapter(mUserAdaptor);


		// sync songs with firebase
		syncSongs.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				songsList = getMp3Songs();
				if (songsList!=null){

					for (Song song: songsList){

						mSongsDatabaseReference.child(song.getMediaId()).setValue(song);
					}
				}
			}
		});

		//Init RecyclerView
		mAdapter = new UserRecyclerViewAdaptor(userList);
		RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
		recyclerView.setLayoutManager(mLayoutManager);
		recyclerView.setItemAnimator(new DefaultItemAnimator());
		recyclerView.setAdapter(mAdapter);


		// onTouchCLick Listener
		recyclerView.addOnItemTouchListener(new UserRecyclerViewListener(getContext(), recyclerView, new ChatFragment.ClickListener() {
			@Override
			public void onClick(View view, int position) {

				Intent chatActivity = new Intent(getContext(),ChatActiivty.class);

				final String db_Email= userList.get(position).getEmail().split("@")[0];
				final String db_name= userList.get(position).getName();
				chatActivity.putExtra(USER_ID,db_Email);
				chatActivity.putExtra(USER_NAME,db_name);
				startActivity(chatActivity);
				Toast.makeText(getContext(),"Success"+userList.get(position).getU_id(),Toast.LENGTH_LONG).show();
			}

			@Override
			public void onLongClick(View view, int position) {

				Intent intent = new Intent(getContext(), SongActivity.class);
				final String dbEmail= userList.get(position).getEmail().split("@")[0];
				intent.putExtra(USER_ID, dbEmail);
				startActivity(intent);

			}
		}));

		mAuthStateListener = new FirebaseAuth.AuthStateListener() {
			@Override
			public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
				FirebaseUser user = firebaseAuth.getCurrentUser();
				if (user != null) {
					// User is signed in
					onSignedInInitialize(user.getDisplayName());
				} else {
					if (PrefUtil.getLoginStatus(getContext())) {
						// User is signed out
						onSignedOutCleanup();
					}

					startActivityForResult(
							AuthUI.getInstance()
									.createSignInIntentBuilder()
									.setIsSmartLockEnabled(false)
									.setProviders(
											AuthUI.EMAIL_PROVIDER,
											AuthUI.GOOGLE_PROVIDER)
									.build(),
							RC_SIGN_IN);
				}
			}
		};

		//        // Create Remote Config Setting to enable developer mode.
		//        // Fetching configs from the server is normally limited to 5 requests per hour.
		//        // Enabling developer mode allows many more requests to be made per hour, so developers
		//        // can test different config values during development.
		//        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
		//                .setDeveloperModeEnabled(BuildConfig.DEBUG)
		//                .build();
		//        mFirebaseRemoteConfig.setConfigSettings(configSettings);
		//
		//        // Define default config values. Defaults are used when fetched config values are not
		//        // available. Eg: if an error occurred fetching values from the server.
		//        Map<String, Object> defaultConfigMap = new HashMap<>();
		//        defaultConfigMap.put(FRIENDLY_MSG_LENGTH_KEY, DEFAULT_MSG_LENGTH_LIMIT);
		//        mFirebaseRemoteConfig.setDefaults(defaultConfigMap);
		//        fetchConfig();




		return rootView;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == RC_SIGN_IN) {
			if (resultCode == RESULT_OK) {

				// Sign-in succeeded, set up the UI
				Toast.makeText(getContext(), "Signed in!", Toast.LENGTH_SHORT).show();

				FirebaseUser user = mFirebaseAuth.getCurrentUser();
				final String name= user.getDisplayName();
				final String email= user.getEmail();
				final String u_id= user.getUid();

				// save in firebase db
				final String key = mUsersDatabaseReference.push().getKey();
				final User user1=new User(email,name,u_id,key,isOnline,"false","Hey I'm Using Firechat");


				db_Email = email.split("@")[0];


				mUsersDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
					@Override
					public void onDataChange(DataSnapshot dataSnapshot) {
						if (dataSnapshot.hasChild(db_Email)) {
							Toast.makeText(getContext(),"Signing in",Toast.LENGTH_LONG).show();
						}else{
							mUsersDatabaseReference.child(db_Email).setValue(user1);
						}
					}

					@Override
					public void onCancelled(DatabaseError databaseError) {

					}
				});



				//save in sharedpref
				PrefUtil.saveLoginDetails(getContext(),key,email,name);

				//isreceipt is used as status
				PrefUtil.setStatus(getContext(),user1.getIsReceipt());

			} else if (resultCode == RESULT_CANCELED) {
				// Sign in was canceled by the user, finish the activity
				Toast.makeText(getContext(), "Sign in canceled", Toast.LENGTH_SHORT).show();
				getActivity().finish();
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		mFirebaseAuth.addAuthStateListener(mAuthStateListener);
		isOnline = "true";

		//        if (PrefUtil.getLoginStatus(MainActivity.this)) {
		//            mUsersDatabaseReference.child(PrefUtil.getEmail(MainActivity.this).split("@")[0] + "/isOnline").setValue("true");
		//        }

		//        mUsersDatabaseReference.child(PrefUtil.getUserId(MainActivity.this)).child("isOnline").runTransaction(new Transaction.Handler() {
		//            @Override
		//            public Transaction.Result doTransaction(MutableData mutableData) {
		//
		//                mutableData.setValue("true");
		//                return Transaction.success(mutableData);
		//            }
		//
		//            @Override
		//            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
		//
		//            }
		//        });

		//        writeOnlineStatus(PrefUtil.getEmail(MainActivity.this),PrefUtil.getUsername(MainActivity.this),"afasf",PrefUtil.getUserId(MainActivity.this), isOnline , "false" , "false");
	}



	@Override
	public void onPause() {
		super.onPause();
		isOnline="false";


		//        writeOnlineStatus(PrefUtil.getEmail(MainActivity.this),PrefUtil.getUsername(MainActivity.this),"afasf",PrefUtil.getUserId(MainActivity.this), isOnline , "false" , "false");
		if (mAuthStateListener != null) {
			mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
		}
		mAdapter.clear();
		//        mMessageAdapter.clear();
		detachDatabaseReadListener();
	}

	@Override
	public void onStop() {
		super.onStop();
		//        if (PrefUtil.getLoginStatus(MainActivity.this)) {
		//            mUsersDatabaseReference.child(PrefUtil.getEmail(MainActivity.this).split("@")[0] + "/isOnline").setValue("false");
		//        }
	}

	private void onSignedInInitialize(String username) {
		mUsername = username;
		attachDatabaseReadListener();
	}

	private void onSignedOutCleanup() {

		isOnline="false";
		//        writeOnlineStatus(PrefUtil.getEmail(MainActivity.this),PrefUtil.getUsername(MainActivity.this),"afasf",PrefUtil.getUserId(MainActivity.this), isOnline , "false" , "false");
		mUsername = ANONYMOUS;



		mAdapter.clear();
		//        mMessageAdapter.clear();
		PrefUtil.clearSharedPreferences(getContext());
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

	public ArrayList<Song> getMp3Songs() {

		Uri allsongsuri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
		ArrayList<Song> songsList = new ArrayList<>();

		String[] projection = { MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.SIZE };

		cursor = getContext().getContentResolver().query(allsongsuri, projection, selection, null, null);

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

					String size = String.valueOf(cursor.getInt(cursor
							.getColumnIndex(MediaStore.Audio.Media.SIZE)));

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


	public interface ClickListener {
		void onClick(View view, int position);

		void onLongClick(View view, int position);
	}


}
