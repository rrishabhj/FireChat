package com.google.firebase.udacity.friendlychat.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by DMI on 20-06-2017.
 */
public class User {
    String email;
    String name;
    String u_id;
    String key;
    String isOnline;
    String isTyping;
    String isReceipt;



    public User(){

    }

    public User(String email, String name, String u_id, String key, String isOnline, String isTyping, String isReceipt) {
        this.email = email;
        this.name = name;
        this.u_id = u_id;
        this.key = key;
        this.isOnline = isOnline;
        this.isTyping = isTyping;
        this.isReceipt = isReceipt;
    }

    public Map<String, String> toMap() {
        HashMap<String, String> result = new HashMap<>();
        result.put("email", email);
        result.put("name", name);
        result.put("u_id", u_id);
        result.put("key", key);
        result.put("isOnline", isOnline);
        result.put("isTyping", isTyping);
        result.put("isReceipt", isReceipt);

        return result;
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


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(String isOnline) {
        this.isOnline = isOnline;
    }

    public String getIsTyping() {
        return isTyping;
    }

    public void setIsTyping(String isTyping) {
        this.isTyping = isTyping;
    }

    public String getIsReceipt() {
        return isReceipt;
    }

    public void setIsReceipt(String isReceipt) {
        this.isReceipt = isReceipt;
    }
}
