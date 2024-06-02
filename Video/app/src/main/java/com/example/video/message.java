package com.example.video;

public class message {
    private String user;
    private String comment;

    message(String u, String c){
        this.user = u;
        this.comment = c;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
