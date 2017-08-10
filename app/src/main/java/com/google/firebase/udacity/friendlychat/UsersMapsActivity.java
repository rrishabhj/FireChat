package com.google.firebase.udacity.friendlychat;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.udacity.friendlychat.Utils.DirectionsJSONParser;
import com.google.firebase.udacity.friendlychat.Utils.PrefUtil;
import com.google.firebase.udacity.friendlychat.model.LocationData;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UsersMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    ArrayList markerPoints= new ArrayList();
    private PolylineOptions lineOptions = new PolylineOptions();
    
    private ArrayList<LatLng> coor = new ArrayList<>();
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mLocationDatabaseReference;
    private ChildEventListener mChildEventListener;
    private SupportMapFragment mapFragment;
    private long count=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Initialize Firebase components
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        // Initialize Firebase components
        mLocationDatabaseReference = mFirebaseDatabase.getReference().child("location").child(PrefUtil.getEmail(UsersMapsActivity.this).split("@")[0]);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

//        mapFragment.getMapAsync(UsersMapsActivity.this);

    }

    @Override
    protected void onResume() {
        super.onResume();

        attachDatabaseReadListener();
    }

    private void attachDatabaseReadListener() {
        if (mChildEventListener == null) {
			mChildEventListener = new ChildEventListener() {
				@Override
				public void onChildAdded(DataSnapshot dataSnapshot, String s) {



                    LocationData locationData = dataSnapshot.getValue(LocationData.class);
					//                    mMessageAdapter.add(friendlyUser);

                    LatLng latLng = new LatLng(Double.valueOf(locationData.getLatitude()),Double.valueOf(locationData.getLongitude()));
                    coor.add(latLng);


                    count++;


                    if(count >= dataSnapshot.getChildrenCount()){
                        //stop progress bar here
                        mapFragment.getMapAsync(UsersMapsActivity.this);
                    }
				}

				public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
				public void onChildRemoved(DataSnapshot dataSnapshot) {}
				public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
				public void onCancelled(DatabaseError databaseError) {

				}
			};
            mLocationDatabaseReference.addChildEventListener(mChildEventListener);
		}
	}

    @Override
    protected void onPause() {
        super.onPause();
        detachDatabaseReadListener();
    }

    private void detachDatabaseReadListener() {
            if (mLocationDatabaseReference != null && mChildEventListener!=null) {
                mLocationDatabaseReference.removeEventListener(mChildEventListener);
                mChildEventListener = null;
            }
        }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

//        LatLng latLng1 = new LatLng(28.5486110, 77.3288530);
//        LatLng latLng2 = new LatLng(28.5440118, 77.3399681);
//        LatLng latLng3 = new LatLng(28.5395632, 77.3333162);
//        LatLng latLng4 = new LatLng(28.5350391, 77.3450321);
//
//        coor.add(latLng1);
//        coor.add(latLng2);
//        coor.add(latLng3);
//        coor.add(latLng4);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coor.get(0), 16));

        // Creating MarkerOptions
        MarkerOptions options = new MarkerOptions();
        for (int index = 0; index < coor.size()-1; index++) {

            options.position(coor.get(index));
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            // Add new marker to the Google Map Android API V2
            mMap.addMarker(options);


            LatLng origin = (LatLng) coor.get(index);
            LatLng dest = (LatLng) coor.get(index + 1);

            // Getting URL to the Google Directions API
            String url = getDirectionsUrl(origin, dest);

            DownloadTask downloadTask = new DownloadTask();

            // Start downloading json data from Google Directions API
            downloadTask.execute(url);
        }
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();


            parserTask.execute(result);

        }
    }


    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList points = null;
            lineOptions = new PolylineOptions();
            MarkerOptions markerOptions = new MarkerOptions();

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList();

                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.RED);
                lineOptions.geodesic(true);

            }

// Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions);
        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=walking";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
}

