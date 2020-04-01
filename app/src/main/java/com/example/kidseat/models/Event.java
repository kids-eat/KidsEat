package com.example.kidseat.models;


import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.model.value.TimestampValue;

import java.util.Date;

@IgnoreExtraProperties
public class Event {

    private String name;
    private String meal_type;
    private String date;
    private String time;
    private String address;
    private String image;
    private String description;
    private TimestampValue created_at;
//    private TimestampValue mTimestamp;


    public Event() { }

    public Event(String name, String meal_type, String address, String date, String time, String description, String image, TimestampValue created_at) {
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

    public String getMealType() {return meal_type;}

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getDescription() {return description;}

    public String getImage() {return image;}

    public TimestampValue getTimestamp() { return created_at; }



}