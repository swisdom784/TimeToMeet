package com.example.video;

public class message {
    private String writer;
    private String comment;

    message(String c, String u){
        this.writer = u;
        this.comment = c;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String user) {
        this.writer = user;
    }
}
