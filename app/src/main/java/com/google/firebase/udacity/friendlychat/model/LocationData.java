package com.google.firebase.udacity.friendlychat.model;

/**
 * Created by DMI on 27-07-2017.
 */

public class LocationData {

    String latitude;
    String longitude;
    String date;

    public LocationData(){

    }
    public LocationData(String latitude, String longitude, String date) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
