package com.example.jacob.eatapp;

import android.support.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by jacob on 2016-05-28.
 * En klass för att hålla koll på alla användare som har laddats
 * in i appen. Denna klass är en singleton, dvs att det bara finns en instans
 * så man kan alltid vara säker på att hämta rätt data.
 */
public class UserManager {

    private static UserManager mInstance = null;
    private ArrayList<User> users = new ArrayList<>();

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    public static UserManager getInstance() {
        if(mInstance == null)
        {
            mInstance = new UserManager();
        }
        return mInstance;

    }

    public User parseFromStringToUser(JSONObject obj) throws JSONException {
        User u = new User();
        u.setId(obj.getInt("pk"));
        u.setEmail(obj.getString("email"));
        u.setFirst_name(obj.getString("first_name"));
        u.setProfile_pic_url(obj.getString("profile_picture"));
        u.setDate_of_birth(obj.getString("date_of_birth"));
        return u;
    }

    @Nullable
    public User getUser(Integer pk) {
        for (User u:users) {
            if(u.getId() == pk) {
                return u;
            }
        }
        return null;
    }

    public void addUser(User u) {
        this.users.add(u);
    }

    public Boolean doesUserExist(Integer pk) {
        for (User u:users) {
            if (u.getId() == pk) {
                return true;
            }
        }
        return false;
    }
}
