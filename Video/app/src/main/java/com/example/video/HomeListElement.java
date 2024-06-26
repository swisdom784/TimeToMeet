package com.example.video;

public class HomeListElement {
    private String RoomNumber;
    private String RoomPeople;
    private int RoomNum;
    private boolean isChecked;

    public int getRoomNum() {
        return RoomNum;
    }

    public void setRoomNum(int roomNum) {
        RoomNum = roomNum;
    }

    public HomeListElement(String RoomNumber, String RoomPeople, int RoomNum){
        this.RoomNumber = RoomNumber;
        this.RoomPeople = RoomPeople;
        this.RoomNum = RoomNum;
        this.isChecked = false;
    }

    public String getRoomNumber() {
        return RoomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        RoomNumber = roomNumber;
    }

    public String getRoomPeople() {
        return RoomPeople;
    }

    public void setRoomPeople(String roomPeople) {
        RoomPeople = roomPeople;
    }

    public boolean isChecked() {return isChecked;}

    public void setChecked(boolean checked) {isChecked = checked;}
}
