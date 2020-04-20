package com.example.kidseat.models;


import com.google.firebase.Timestamp;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.Date;

@IgnoreExtraProperties
public class Event {

    private String name;
    private String date;
    private String time;
    private String address;
    private String meal_type;
    private String description;
    private String image;
    private Timestamp created_at;
//    private TimestampValue mTimestamp;


    public Event() { }

    public Event(String name, String meal_type, String address, String date, String time, String description, String image, Timestamp created_at) {
        this.name = name;
        this.meal_type = meal_type;
        this.address = address;
        this.date = date;
        this.time = time;
        this.description = description;
        this.image = image;
        this.created_at = created_at;

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

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMealType() {return meal_type;}

    public String getDescription() {return description;}

    public String getImage() {return image;}

    public void setImage(String image) {this.image = image;}

    public Timestamp getTimestamp() { return created_at; }



}