package com.google.firebase.udacity.friendlychat.service;

import android.app.Service;
import android.app.job.JobService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.udacity.friendlychat.MySongsActivity;
import com.google.firebase.udacity.friendlychat.Utils.PrefUtil;
import com.google.firebase.udacity.friendlychat.model.LocationData;

/**
 * Created by DMI on 27-07-2017.
 */

public class MyLocationService extends com.firebase.jobdispatcher.JobService {


    private static final String TAG = "BOOMBOOMTESTGPS";
    private GoogleApiClient mGoogleApiClient;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mLocationDatabaseReference;


    private class GoogleLocationListner implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

        private LocationRequest mLocationRequest;

        @Override
        public void onConnected(@Nullable Bundle bundle) {

            mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(1000);
            if (ActivityCompat.checkSelfPermission(getBaseContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getBaseContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        }

        @Override
        public void onConnectionSuspended(int i) {
            Log.i(TAG, "Connection suspended");
            mGoogleApiClient.connect();
        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "onLocationChanged: " + location);

            if (mLocationDatabaseReference != null) {
                LocationData locationData = new LocationData(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()), String.valueOf(location.getTime()));
                mLocationDatabaseReference.push().setValue(locationData);
            }
        }

    }

    GoogleLocationListner mGoogleLocationListner = new GoogleLocationListner();

    @Override
    public boolean onStartJob(JobParameters job) {

        Log.d("MyJobService","my job");

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
    }


    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");
        initializeLocationManager();

        FirebaseApp.initializeApp(getBaseContext());
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mLocationDatabaseReference = mFirebaseDatabase.getReference().child("location").child(PrefUtil.getEmail(getBaseContext()).split("@")[0]);
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        buildGoogleApiClient();

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(mGoogleLocationListner)
                .addOnConnectionFailedListener(mGoogleLocationListner)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();
    }
}