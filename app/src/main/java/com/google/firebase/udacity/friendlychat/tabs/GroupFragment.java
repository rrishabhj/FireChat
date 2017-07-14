package com.google.firebase.udacity.friendlychat.tabs;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.udacity.friendlychat.R;
import com.google.firebase.udacity.friendlychat.Utils.PrefUtil;
import com.google.firebase.udacity.friendlychat.adaptor.MessageRecyclerViewAdaptor;
import com.google.firebase.udacity.friendlychat.model.FriendlyMessage;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.google.firebase.udacity.friendlychat.tabs.ChatFragment.ANONYMOUS;
import static com.google.firebase.udacity.friendlychat.tabs.ChatFragment.DEFAULT_MSG_LENGTH_LIMIT;
import static com.google.firebase.udacity.friendlychat.tabs.ChatFragment.RC_SIGN_IN;

public class GroupFragment extends Fragment {

	private static final int RC_PHOTO_PICKER = 2;

	private ChildEventListener mChildEventListener;
	private FirebaseStorage mFirebaseStorage;
	private FirebaseDatabase mFirebaseDatabase;
	private DatabaseReference mMessagesDatabaseReference;
	private StorageReference mChatPhotosStorageReference;
	private ProgressBar mProgressBar;
	private ImageButton mPhotoPickerButton;
	private EditText mMessageEditText;
	private Button mSendButton;
	private RecyclerView recyclerView;
	private MessageRecyclerViewAdaptor mAdapter;
	private List<FriendlyMessage> messageList = new ArrayList<>();
	private String mUsername;
	private LinearLayoutManager mLayoutManager;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_group, container, false);

		// Initialize Firebase components
		mFirebaseDatabase = FirebaseDatabase.getInstance();
		mFirebaseStorage = FirebaseStorage.getInstance();

		mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("groupmessages");
		mChatPhotosStorageReference = mFirebaseStorage.getReference().child("chat_photos");
//
		// Initialize references to views
		mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);

		recyclerView = (RecyclerView) rootView.findViewById(R.id.rv_message);
//
		mPhotoPickerButton = (ImageButton) rootView.findViewById(R.id.photoPickerButton);
		mMessageEditText = (EditText) rootView.findViewById(R.id.messageEditText);
		mSendButton = (Button) rootView.findViewById(R.id.sendButton);

		// init rv
		mAdapter = new MessageRecyclerViewAdaptor(messageList, getContext(),true);
		mLayoutManager = new LinearLayoutManager(getContext());
		recyclerView.setLayoutManager(mLayoutManager);
		recyclerView.setItemAnimator(new DefaultItemAnimator());
		recyclerView.setAdapter(mAdapter);


		mUsername = PrefUtil.getUsername(getContext());
//
//
//
//
		// Initialize progress bar
		mProgressBar.setVisibility(ProgressBar.INVISIBLE);

		// ImagePickerButton shows an image picker to upload a image for a message
		mPhotoPickerButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
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
					mSendButton.setEnabled(true);
				} else {
					mSendButton.setEnabled(false);
				}
			}

			@Override
			public void afterTextChanged(Editable editable) {
			}
		});
		mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});

		// Send button sends a message and clears the EditText
		mSendButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (messageList.size()!=0) {
					mLayoutManager.scrollToPosition(messageList.size() - 1);
					recyclerView.smoothScrollToPosition(messageList.size());
				}

				FriendlyMessage friendlyMessage = new FriendlyMessage(mMessageEditText.getText().toString(),
						mUsername, null);
				mMessagesDatabaseReference.push().setValue(friendlyMessage);

				// Clear input box
				mMessageEditText.setText("");
			}
		});
		return rootView;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == RC_SIGN_IN) {
			if (resultCode == RESULT_OK) {
				// Sign-in succeeded, set up the UI
				Toast.makeText(getContext(), "Signed in!", Toast.LENGTH_SHORT).show();
			} else if (resultCode == RESULT_CANCELED) {
				// Sign in was canceled by the user, finish the activity
				Toast.makeText(getContext(), "Sign in canceled", Toast.LENGTH_SHORT).show();
			}
		} else if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
			Uri selectedImageUri = data.getData();

			// Get a reference to store file at chat_photos/<FILENAME>
			StorageReference photoRef = mChatPhotosStorageReference.child(selectedImageUri.getLastPathSegment());

			// Upload file to Firebase Storage
			photoRef.putFile(selectedImageUri)
					.addOnSuccessListener(getActivity(), new OnSuccessListener<UploadTask.TaskSnapshot>() {
						public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
							// When the image has successfully uploaded, we get its download URL
							Uri downloadUrl = taskSnapshot.getDownloadUrl();

							// Set the download URL to the message box, so that the user can send it to the database
							FriendlyMessage friendlyMessage = new FriendlyMessage(null, mUsername, downloadUrl.toString());
							mMessagesDatabaseReference.push().setValue(friendlyMessage);
						}
					});
		}
	}

	@Override public void onResume() {
		super.onResume();
		attachDatabaseReadListener();
	}

	@Override
	public void onPause() {
		super.onPause();

		mAdapter.clear();
		detachDatabaseReadListener();
	}

	private void attachDatabaseReadListener() {
		if (mChildEventListener == null) {
			mChildEventListener = new ChildEventListener() {
				@Override
				public void onChildAdded(DataSnapshot dataSnapshot, String s) {
					FriendlyMessage friendlyMessage = dataSnapshot.getValue(FriendlyMessage.class);

					friendlyMessage.setText(PrefUtil.getUsername(getContext())+"@"+friendlyMessage.getText());
					messageList.add(friendlyMessage);

					if (messageList.size()!=0) {
						mLayoutManager.scrollToPosition(messageList.size() - 1);
						recyclerView.smoothScrollToPosition(messageList.size());
					}
				}

				public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
				public void onChildRemoved(DataSnapshot dataSnapshot) {}
				public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
				public void onCancelled(DatabaseError databaseError) {}
			};
			mMessagesDatabaseReference.addChildEventListener(mChildEventListener);
		}
	}

	private void detachDatabaseReadListener() {
		if (mChildEventListener != null) {
			mMessagesDatabaseReference.removeEventListener(mChildEventListener);
			mChildEventListener = null;
		}
	}

}