package com.example.kidseat.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.IgnoreExtraProperties;


@IgnoreExtraProperties
public class Event {

    private String name;
    private String date;
    private String time;
    private String address;
    private String image;

    public Event() { }

    public Event(String name, String meal_type, String address, String date, String time, String description, String image, Timestamp created_at) {
        this.name = name;
        this.address = address;
        this.date = date;
        this.time = time;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getAddress() {
        return address;
    }

    public String getImage() {return image;}

}