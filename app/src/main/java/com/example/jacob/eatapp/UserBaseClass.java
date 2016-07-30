package com.example.jacob.eatapp;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Html;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by jacob on 2016-05-13.
 */
public abstract class UserBaseClass {

    public String email;
    public int id;
    public Integer expires_in;
    public ArrayList<Integer> events;
    public String date_of_birth;
    public Double lon;
    public Double lat;
    public String first_name;
    public String path_to_profile_pic;
    public String profile_pic_url;

    public Bitmap getProfile_pic() {
        if (profile_pic != null) {
            return profile_pic;
        }
        return null;
    }

    public void setProfile_pic(Bitmap profile_pic) {
        this.profile_pic = profile_pic;
    }

    public Bitmap profile_pic = null;

    public UserBaseClass() {
        // Exists only to defeat instantiation.
        this.profile_pic = null;
    }

    // Getters & Setters for User


    public String getProfile_pic_url() {
        return profile_pic_url;
    }

    public void setProfile_pic_url(String profile_pic_url) {
        this.profile_pic_url = profile_pic_url;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setExpires_in(Integer time) { this.expires_in = time; }

    public Integer getExpires_in() { return this.expires_in; }

    public void setEvents(ArrayList<Integer> events) { this.events = events; }

    public ArrayList<Integer> getEvents() { return this.events; }

    public void setDate_of_birth(String date_of_birth) {
        this.date_of_birth = date_of_birth;
    }

    public String getDate_of_birth() { return date_of_birth; }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) { this.lon = lon; }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) { this.lat = lat; }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getFirst_name() {
        return first_name;
    }

    public int getAge () {
        // Räkna ut åldern från dagens datum
        GregorianCalendar cal = new GregorianCalendar();
        int y, m, d, age;
        // Räkna ut ålder
        String[] dates = this.getDate_of_birth().split("-");
        Integer year = Integer.valueOf(dates[0]);
        Integer month = Integer.valueOf(dates[1]);
        Integer day = Integer.valueOf(dates[2]);
        y = cal.get(Calendar.YEAR);
        m = cal.get(Calendar.MONTH);
        d = cal.get(Calendar.DAY_OF_MONTH);
        cal.set(year, month, day);
        age = y - cal.get(Calendar.YEAR);
        if ((m < cal.get(Calendar.MONTH))
                || ((m == cal.get(Calendar.MONTH)) && (d < cal
                .get(Calendar.DAY_OF_MONTH)))) {
            --age;
        }
        if(age < 0)
            throw new IllegalArgumentException("Age < 0");
        return age;
    }

    public void savePictureToInternalStorage(Bitmap bitmapImage, Context context) throws IOException {
        ContextWrapper cw = new ContextWrapper(context);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        if (this.getProfile_pic_url() != null) {
            // Get filename
            String[] url_path = this.getProfile_pic_url().split("/");
            String filename = url_path[url_path.length-1];
            // Create imageDir
            File mypath = new File(directory, filename);

            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(mypath);
                // Use the compress method on the BitMap object to write image to the OutputStream
                bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                fos.close();
            }
        }
        path_to_profile_pic = directory.getAbsolutePath();
    }

    public void setImageFromStorage()
    {
        Bitmap b;
        try {
            // Get filename
            String[] url_path = this.getProfile_pic_url().split("/");
            File f=new File(path_to_profile_pic);
            b = BitmapFactory.decodeStream(new FileInputStream(f));
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            b = null;
        }
        profile_pic = b;
    }
}
