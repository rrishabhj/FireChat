package com.google.firebase.udacity.friendlychat.model;

/**
 * Created by DMI on 08-08-2017.
 */

public class UsersMessages {

    String username;
    String email;
    String lastMessage;
    String lastMessageDate;

    public UsersMessages(){

    }

    public UsersMessages(String username, String email, String lastMessage, String lastMessageDate) {
        this.username = username;
        this.email = email;
        this.lastMessage = lastMessage;
        this.lastMessageDate = lastMessageDate;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getLastMessageDate() {
        return lastMessageDate;
    }

    public void setLastMessageDate(String lastMessageDate) {
        this.lastMessageDate = lastMessageDate;
    }
}

