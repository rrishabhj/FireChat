package com.google.firebase.udacity.friendlychat.model;

/**
 * Created by DMI on 20-06-2017.
 */

public class User {
    String email;
    String name;
    String u_id;
    String key;
    int isOnline;
    int isTyping;
    int isReceipt;


    public User(){

    }

    public User(String email, String name, String u_id, String key, int isOnline, int isTyping, int isReceipt) {
        this.email = email;
        this.name = name;
        this.u_id = u_id;
        this.key = key;
        this.isOnline = isOnline;
        this.isTyping = isTyping;
        this.isReceipt = isReceipt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getU_id() {
        return u_id;
    }

    public void setU_id(String u_id) {
        this.u_id = u_id;
    }

    public int getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(int isOnline) {
        this.isOnline = isOnline;
    }

    public int getIsTyping() {
        return isTyping;
    }

    public void setIsTyping(int isTyping) {
        this.isTyping = isTyping;
    }

    public int getIsReceipt() {
        return isReceipt;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setIsReceipt(int isReceipt) {
        this.isReceipt = isReceipt;
    }
}
