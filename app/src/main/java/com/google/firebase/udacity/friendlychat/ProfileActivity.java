package com.google.firebase.udacity.friendlychat;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.Profile;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.udacity.friendlychat.Utils.PrefUtil;

/**
 * Created by DMI on 14-07-2017.
 */

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";
    private Toolbar toolbar;
    EditText profileName;
    TextView email,status;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUsersDatabaseReference;
    private ValueEventListener statusChangeListner;
    private Dialog dialog;
    private EditText etStatus;
    private boolean isCurrentUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setTitle("Profile");
        setSupportActionBar(toolbar);

        // Initialize Firebase components
        mFirebaseDatabase = FirebaseDatabase.getInstance();



        // isreceipt is for now used for storing status.
        mUsersDatabaseReference = mFirebaseDatabase.getReference().child("users").child(PrefUtil.getEmail(ProfileActivity.this).split("@")[0]).child("status");

        profileName = (EditText) findViewById(R.id.et_name);
        email = (TextView) findViewById(R.id.tv_email);
        status = (TextView) findViewById(R.id.tv_status);


        profileName.setText(PrefUtil.getUsername(ProfileActivity.this));
        email.setText(PrefUtil.getEmail(ProfileActivity.this));

        status.setText(PrefUtil.getStatus(ProfileActivity.this));

        statusChangeListner = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String str= (String) dataSnapshot.getValue();
                if (str!=null && str.length()>0){

                    status.setText(str);
//                    tvIsTyping.setVisibility(View.VISIBLE);
                }else{
                    status.setText("Error");
//                    tvIsTyping.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };

        mUsersDatabaseReference.addValueEventListener(statusChangeListner);

    }

    public void changeStatus(View view){

        dialog = new Dialog(ProfileActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_update_status);
        dialog.setCanceledOnTouchOutside(true);

        etStatus = (EditText) dialog.findViewById(R.id.etStatus);

        Button btnUpdateStatus= (Button) dialog.findViewById(R.id.btnUpdateStatus);

        btnUpdateStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (etStatus.getText().length() > 0) {
                    mUsersDatabaseReference.setValue(etStatus.getText().toString() + "");
                    PrefUtil.setStatus(ProfileActivity.this,etStatus.getText().toString());
                    dialog.cancel();
                }
            }
        });

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        //p.dimAmount = 0.3f;
        window.setAttributes(wlp);

        dialog.show();




    }

    @Override
    protected void onPause() {
        super.onPause();

        if (statusChangeListner != null ) {
            mUsersDatabaseReference.removeEventListener(statusChangeListner);
            statusChangeListner = null;
        }
    }
}
