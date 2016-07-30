package com.example.jacob.eatapp;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by jacob on 2016-05-08.
 */
public class EventItem implements Serializable {

    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    private String date_start;
    private String date_end;
    private Integer numOfPeople;
    private String title;
    private String description;
    private Double lat;
    private Double lon;
    private String sortOfFood;
    private Integer age;
    private Integer id;
    private Double distance;
    private Integer user_pk;

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    private Double price = 0.0;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User user;
    public ArrayList<User> getParticipants() {
        return participants;
    }

    public void setParticipants(ArrayList<User> participants) {
        this.participants = participants;
    }

    public ArrayList<User> participants;
    public Integer getUser_pk() {
        return user_pk;
    }

    public void setUser_pk(Integer user_pk) {
        this.user_pk = user_pk;
    }



    public EventItem() {
        super();
    }

    // Getters & Setters for the properties
    public void setId(int id) {this.id = id;}
    public int getId() {
        return id;
    }

    public void setLat(Double lat) { this.lat = lat; }
    public void setLon(Double lon) { this.lon = lon; }
    public Double getLat() { return lat; }
    public Double getLon() { return lon; }

    public void setDistance(double distance) { this.distance = distance; }
    public Double getDistance() { return distance; }

    public String getDate_Start() {
        return date_start;
    }

    public void setDate_start(String date_start) {
        this.date_start = date_start;
    }

    public Integer getNumOfPeople() {
        return numOfPeople;
    }

    public void setNumOfPeople(Integer numOfPeople) {
        this.numOfPeople = numOfPeople;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSortOfFood() {
        return sortOfFood;
    }

    public void setSortOfFood(String sortOfFood) {
        this.sortOfFood = sortOfFood;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }


    public String getDate_end() {
        return date_end;
    }

    public void setDate_end(String date_end) {
        this.date_end = date_end;
    }

    public long getTime(String time) throws Exception {
        try {
            return this.format.parse(time).getTime();
        } catch (Exception e) {
            format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        }
        try {
            return this.format.parse(time).getTime();
        } catch (Exception e) {
            format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            //For this you may need to manually adjust time offset
        }
        try {
            return this.format.parse(time).getTime();
        } catch (Exception e) {
            format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
        }
        try {
            return this.format.parse(time).getTime();
        } catch (Exception e) {
            format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss'Z'");
            //For this you may need to manually adjust time offset
        }
        return this.format.parse(time).getTime();
    }

}
