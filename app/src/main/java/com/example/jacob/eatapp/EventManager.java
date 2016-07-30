package com.example.jacob.eatapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by jacob on 2016-05-09.
 */
public class EventManager {
    /*
        Singletoninstans som håller reda på alla event som laddats från servern
     */
    private static EventManager mInstance = null;
    public ArrayList<EventItem> events;

    // GPSTracker och Location
    public GPSTracker gpsTracker;
    public LocationManager locManager;

    public static EventManager getInstance() {
        if (mInstance == null) {
            mInstance = new EventManager();
        }
        return mInstance;

    }

    public ArrayList<EventItem> getEvents() {
        if (events == null) {
            events = new ArrayList<EventItem>();
        }
        return events;
    }

    public void setEvents(ArrayList<EventItem> events) {
        this.events = events;
    }

    public void addEvent(EventItem eventItem) {
        if (events == null) {
            events = new ArrayList<EventItem>();
        }
        events.add(eventItem);
    }

    public void removeEvent(EventItem eventItem) {
        events.remove(eventItem);
    }

    public Boolean eventExist(Integer pk) {
        if (events != null) {
            for (EventItem event : events) {
                if (event.getId() == pk) {
                    return true;
                }
            }
        }
        return false;
    }

    public EventItem parseJsonEvent(JSONObject object) {

        EventItem ei = new EventItem();
        try {
            Boolean exist = EventManager.getInstance().eventExist(object.getInt("pk"));
            if(exist) {
                ei = EventManager.getInstance().getEventWithPk(object.getInt("pk"));
            }
            else {
                ei = new EventItem();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            String title = object.getString("title");
            Integer event_creator = object.getInt("user");
            Integer event_pk = object.getInt("pk");
            String desc = object.getString("description");
            Integer numOfPeople = object.getInt("numOfPeople");
            String date_start = object.getString("date_start");
            Double price = object.getDouble("price");

            // Sätter värden på eventitem
            ei.setDate_start(date_start);
            ei.setId(event_pk);
            ei.setUser_pk(event_creator);
            ei.setDescription(desc);
            ei.setNumOfPeople(numOfPeople);
            ei.setTitle(title);
            ei.setPrice(object.getDouble("price"));

            // Startar bearbeta deltagare på eventen
            JSONArray participants = object.getJSONArray("participant");
            ArrayList<User> p_array = new ArrayList<>();
            for (int i = 0; i < participants.length(); i++) {
                /* Loopar igenom participants arrayn och lägger till dem
                lokalt i User klassen, samt sätter participants på EventItem till
                en ArrayList med user instanser som skapas här.
                 */
                Integer pk = participants.getJSONObject(i).getInt("pk");
                String email = participants.getJSONObject(i).getString("email");
                String date_of_birth = participants.getJSONObject(i).getString("date_of_birth");
                String first_name = participants.getJSONObject(i).getString("first_name");
                String profile_picture = participants.getJSONObject(i).getString("profile_picture");

                // Hämtar userManagern
                UserManager userManager = UserManager.getInstance();
                User user = null;
                if (userManager.doesUserExist(pk)) {
                    // om användaren finns så hämta användaren
                    user = userManager.getUser(pk);
                } else {
                    // annars skapa ny användare
                    user = new User();
                    userManager.addUser(user);
                }
                user.setId(pk);
                user.setEmail(email);
                user.setDate_of_birth(date_of_birth);
                user.setFirst_name(first_name);
                user.setProfile_pic_url(profile_picture);
                p_array.add(user);
            }

            // Latitude longitude formation
            try {
                JSONObject location = object.getJSONObject("location");
                Double distance = new Double(0.0);
                if (object.has("distance")) {
                    distance = object.getDouble("distance");
                }
                Double lon = location.getDouble("longitude");
                Double latitude = location.getDouble("latitude");
                ei.setDistance(distance);
            } catch (NullPointerException e) {
                Log.e("Location", "Creating event doesn't return location");
            }

            ei.setParticipants(p_array);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ei;
    }

    private EventItem getEventWithPk(int pk) {
        for (EventItem e: events) {
            if (e.getId() == pk) {
                return e;
            }
        }
        return new EventItem();
    }

    public EventItem createEvent(View v) {

        Button dateStart = (Button) v.findViewById(R.id.choosedate);
        Button timeStart = (Button) v.findViewById(R.id.choosetime);
        //TextView dateEnd = (TextView) findViewById(R.id.eventDateEnd);
        //TextView timeEnd = (TextView) findViewById(R.id.eventTimeEnd);
        EditText title = (EditText) v.findViewById(R.id.editTextEventTitle);
        EditText desc = (EditText) v.findViewById(R.id.editTextEventDescription);
        EditText nop = (EditText) v.findViewById(R.id.editTextEventNumOfPeople);
        EditText price = (EditText) v.findViewById(R.id.editTextEventPrice);

        // Skapar event instans
        EventItem eventItem = new EventItem();

        // Initierar variabler
        String djangoDateStart = "";
        String d_start = "";
        String t = "";
        Integer n = 0;
        String de = "";
        String time_start = "";
        Double price_var = 0.0;
        try {
            Location loc = AppManager.getInstance().getLoc();
            Double lat = loc.getLatitude();
            Double lon = loc.getLongitude();

            d_start = dateStart.getText().toString();
            n = Integer.valueOf(nop.getText().toString());
            t = title.getText().toString();
            de = desc.getText().toString();
            time_start = timeStart.getText().toString();
            price_var = Double.valueOf(price.getText().toString());

            // Datum & Tidsformatering "2016-05-26T13:36:50.140030Z"
            String da = d_start.replace("/", "-");
            djangoDateStart = da + "T" + time_start;

            eventItem.setTitle(t);
            eventItem.setDate_start(djangoDateStart);
            eventItem.setDescription(de);
            eventItem.setLat(lat);
            eventItem.setLon(lon);
            eventItem.setNumOfPeople(n);
            eventItem.setUser_pk(SessionUser.getInstance().getId());
            eventItem.setPrice(price_var);
            System.out.println(eventItem.getTitle() + " EVENTMANAGER CREATE EVENT");

        } catch (NullPointerException e) {
            Log.e("NullPointer", "Some of the create event fields were not filled out");
        }

        // Skickar eventinstans till AsyncTask
        return eventItem;
    }

}
