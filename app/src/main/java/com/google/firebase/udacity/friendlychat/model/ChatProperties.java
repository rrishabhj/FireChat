package com.google.firebase.udacity.friendlychat.model;

/**
 * Created by DMI on 08-08-2017.
 */

public class ChatProperties {
    String username;
    String email;
    String lastMessage;
    String lastMessageDate;


    public ChatProperties(){

    }

    public ChatProperties(String username, String email, String lastMessage, String lastMessageDate) {
        this.username = username;
        this.email = email;
        this.lastMessage = lastMessage;
        this.lastMessageDate = lastMessageDate;
    }


    public String getLastMessageDate() {
        return lastMessageDate;
    }

    public void setLastMessageDate(String lastMessageDate) {
        this.lastMessageDate = lastMessageDate;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }



    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
