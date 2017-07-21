package com.google.firebase.udacity.friendlychat.model;

/**
 * Created by DMI on 21-07-2017.
 */

public class Song {

    String mediaName;
    String mediaId;
    String mediaDataLink;

    //media remote link is the filename ocontained in the folder of the user
    String mediaDataRemoteLink;
    String mediaSize;

    public Song(){

    }

    public Song(String mediaName, String mediaId, String mediaDataLink, String mediaDataRemoteLink, String mediaSize) {
        this.mediaName = mediaName;
        this.mediaId = mediaId;
        this.mediaDataLink = mediaDataLink;
        this.mediaDataRemoteLink = mediaDataRemoteLink;
        this.mediaSize = mediaSize;
    }


    public String getMediaName() {
        return mediaName;
    }

    public void setMediaName(String mediaName) {
        this.mediaName = mediaName;
    }

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public String getMediaDataLink() {
        return mediaDataLink;
    }

    public void setMediaDataLink(String mediaDataLink) {
        this.mediaDataLink = mediaDataLink;
    }

    public String getMediaDataRemoteLink() {
        return mediaDataRemoteLink;
    }

    public void setMediaDataRemoteLink(String mediaDataRemoteLink) {
        this.mediaDataRemoteLink = mediaDataRemoteLink;
    }

    public String getMediaSize() {
        return mediaSize;
    }

    public void setMediaSize(String mediaSize) {
        this.mediaSize = mediaSize;
    }
}
