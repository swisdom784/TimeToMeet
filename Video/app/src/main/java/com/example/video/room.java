package com.example.video;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
@IgnoreExtraProperties
public class room {

    private String name;
    private String owner;
    private String password;
    private Map<String,String> guest = new HashMap<>();
    private Map<String,Integer> day = new HashMap<>();
    private Map<String,Integer> time = new HashMap<>();

    public room(){

    }
    public String getname(){return name;}
    public void setname(String name){this.name = name;}
    public void setOwner(String owner){this.owner = owner;}
    public String getowner(){return owner;}

    public String getPassword(){return password;}
    public void setPassword(String password){this.password = password;}
    public Map<String,String> getGuest(){return guest;}
    public void setGuest(Map<String,String> guest){this.guest = guest;}
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


    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name",name);
        result.put("owner",owner);
        result.put("guest",guest);
        return result;
    }
}
