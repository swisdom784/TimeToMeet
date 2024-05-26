package com.example.video;

public class UserListElement {
    public UserListElement(String s){
        this.peopleName = s;
    }
    public String getPeopleName() {
        return peopleName;
    }

    public void setPeopleName(String peopleName) {
        this.peopleName = peopleName;
    }

    private String peopleName;


}
