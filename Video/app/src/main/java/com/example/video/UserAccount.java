package com.example.video;

import java.util.List;

public class UserAccount {


    private String idToken;
    private String password;
    private String emailId;
    private String username;
    private List<Integer> roomList;
    public UserAccount(){
    }

    public List<Integer> getRoomList() {return roomList;}

    public void setRoomList(List<Integer> roomList) {this.roomList = roomList;}
    public String getUsername(){return username;}
    public void setUsername(String username){this.username = username;}
    public String getIdToken(){return idToken;}

    public void setIdToken(String idToken){
        this.idToken = idToken;
    }

    public String getEmailId() {return emailId;}

    public void setEmailId(String emailId){ this.emailId = emailId;}

    public String getPassword(){return password;}
    public void setPassword(String password) {this.password = password;}

}
