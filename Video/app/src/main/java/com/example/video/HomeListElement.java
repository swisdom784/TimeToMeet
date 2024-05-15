package com.example.video;

public class HomeListElement {
    private String RoomNumber;
    private String RoomPeople;
    public HomeListElement(String RoomNumber, String RoomPeople){
        this.RoomNumber = RoomNumber;
        this.RoomPeople = RoomPeople;
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
}
