package com.example.firebasesocial.models;

public class ModelChat {

    String message,receiver,sender,timestrap;
    boolean isSeen;

    ModelChat(){

    }

    public ModelChat(String message, String receiver, String sender, String timestrap, boolean isSeen) {
        this.message = message;
        this.receiver = receiver;
        this.sender = sender;
        this.timestrap = timestrap;
        this.isSeen = isSeen;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTimestrap() {
        return timestrap;
    }

    public void setTimestrap(String timestrap) {
        this.timestrap = timestrap;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }


}
