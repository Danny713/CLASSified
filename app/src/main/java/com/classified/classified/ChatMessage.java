package com.classified.classified;

import android.util.Log;

import java.util.Date;


public class ChatMessage {

    private String messageText;
    private long messageTime;

    public ChatMessage(String messageText) {
        Log.d("hello", "chatmessage received: " + messageText);
        this.messageText = messageText;

        // Initialize to current time
        messageTime = new Date().getTime();
    }

    public ChatMessage(){

    }

    public String getMessageText() {
        return messageText;
    }

    public long getMessageTime() {
        return messageTime;
    }

}