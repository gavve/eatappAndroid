package com.example.jacob.eatapp;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class CreateEvent extends Fragment implements View.OnClickListener {

    EventItem eventItem;
    View v;
    SessionUser user = SessionUser.getInstance();

    public CreateEvent() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_create_event, container, false);

        // Sätt users firstname som actionbar header title
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Create EatUp!");
        // Göm floating action bar
        FloatingActionButton fab = ((MainActivity) getActivity()).getFloatingActionButton();
        fab.hide();

        // Hämtar publish knappen
        Button publish = (Button) v.findViewById(R.id.publishEventButton);
        publish.setOnClickListener(this);

        // Inflate the layout for this fragment
        return v;
    }

    public void publishEvent() {
        // Hämta eventmanagern
        EventManager eventManager = EventManager.getInstance();
        EventItem tempEvent = eventManager.createEvent(v); // skickas in i createEventSync
        System.out.println(tempEvent.getDescription());

        // Kör asynctask och vänta på respons
        CreateEventSync createEventSync = new CreateEventSync(new AsyncInterface() {
            @Override
            public void processFinish(String output) throws JSONException {
                // Hämtar eventManagern
                EventManager eventManager = EventManager.getInstance();
                System.out.println(output);
                // Get objects from GET result
                JSONObject jsonObject = new JSONObject(output);
                EventItem eventItem = eventManager.parseJsonEvent(jsonObject);
                eventManager.addEvent(eventItem);

                // Hämtar eventAdapter och skickar in context samt output från asynctask.
                AppManager.getInstance().showAllEvents(getFragmentManager(), true);

            }

            @Override
            public void bitMapFinished(Bitmap output) throws IOException {

            }

        }, tempEvent);
        createEventSync.execute();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.publishEventButton:
                publishEvent();
                break;
        }
    }
}
