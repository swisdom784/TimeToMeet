package com.example.video;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
@IgnoreExtraProperties
public class room {

    private String name;
    private String owner;
    private String password;
    private Map<String,Object> guest = new HashMap<>();
    private Map<String,Integer> day = new HashMap<>();
    private Map<String,Integer> time = new HashMap<>();
    private List<Integer> sum = new ArrayList<>();
    private List<HashMap<String,String>> messageList = new ArrayList<>();

    public List<HashMap<String, String>> getMessageList() {
        return messageList;
    }

    public void setMessageList(List<HashMap<String, String>> messageList) {
        this.messageList = messageList;
    }

    public List<Integer> getSum() {
        return sum;
    }

    public void setSum(List<Integer> sum) {
        this.sum = sum;
    }

    public Map<String, Object> getGuest() {return guest;}

    public void setGuest(Map<String, Object> guest) {this.guest = guest;}
    public String getname(){return name;}
    public void setname(String name){this.name = name;}
    public void setOwner(String owner){this.owner = owner;}
    public String getowner(){return owner;}

    public String getPassword(){return password;}
    public void setPassword(String password){this.password = password;}
    public Map<String, Integer> getDay() {
        return day;
    }

    public void setDay(Map<String, Integer> day) {
        this.day = day;
    }

    public Map<String, Integer> getTime() {
        return time;
    }

    public void setTime(Map<String, Integer> time) {
        this.time = time;
    }

    public room(){

    }
}
