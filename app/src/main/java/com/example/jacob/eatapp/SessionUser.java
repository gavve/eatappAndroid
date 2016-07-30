package com.example.jacob.eatapp;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by jacob on 2016-05-13.
 */
public class SessionUser extends UserBaseClass {

    private static SessionUser instance = new SessionUser();
    private String access_token;
    private String refresh_token;
    private Double lon;
    private Double lat;

    protected SessionUser() {
        // Exists only to defeat instantiation.
    }

    public static SessionUser getInstance() {
        if(instance == null) {
            instance = new SessionUser();
        }
        return instance;
    }

    // Getters & Setters
    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    @Override
    public Double getLon() {
        return lon;
    }

    @Override
    public void setLon(Double lon) {
        this.lon = lon;
    }

    @Override
    public Double getLat() {
        return lat;
    }

    @Override
    public void setLat(Double lat) {
        this.lat = lat;
    }
}
