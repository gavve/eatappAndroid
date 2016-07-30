package com.example.jacob.eatapp;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ListFragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListEvents extends ListFragment {

    private View eventListView;
    private View eventLoadingProgress;

    public ListEvents() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Startar listEvents med att hämta redan sparade event
        ArrayList<EventItem> events = EventManager.getInstance().getEvents();
        // Hämtar eventAdapter och skickar in context samt output från asynctask.
        EventAdapter eventAdapter = new EventAdapter(getActivity().getBaseContext(), events);
        setListAdapter(eventAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View listView = inflater.inflate(R.layout.fragment_list_events, container, false);

        // Göm floating action bar
        FloatingActionButton fab = ((MainActivity) getActivity()).getFloatingActionButton();
        fab.show();
        return listView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);

        eventListView = getListView();
        eventLoadingProgress = getView().findViewById(R.id.loading_events_progress);
        // Startar med att visa progressbaren
        showProgress(true);
        RetrieveEvents retrieveEvents = new RetrieveEvents(new AsyncInterface() {
            @Override
            public void processFinish(String output) throws JSONException {
                // Hämtar eventManagern
                EventManager eventManager = EventManager.getInstance();

                // Get objects from GET result
                JSONObject jsonObject = new JSONObject(output);
                JSONArray jsonArray = jsonObject.getJSONArray("results");

                if (eventManager.getEvents().isEmpty()) {
                    eventManager.setEvents(new ArrayList<EventItem>());
                }
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jObject = jsonArray.getJSONObject(i);
                    if (!eventManager.eventExist(jObject.getInt("pk"))) {
                        eventManager.addEvent(eventManager.parseJsonEvent(jObject));
                    }
                }

                // Hämtar eventAdapter och skickar in context samt output från asynctask.
                EventAdapter eventAdapter = new EventAdapter(getActivity().getBaseContext(), eventManager.getEvents());
                setListAdapter(eventAdapter);

                // När all data är hämtad, göm progressbar och visa eventlistan
                showProgress(false);
            }

            @Override
            public void bitMapFinished(Bitmap output) throws IOException {

            }
        });
        retrieveEvents.execute();
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            eventListView.setVisibility(show ? View.GONE : View.VISIBLE);
            eventListView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    eventListView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            eventLoadingProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            eventLoadingProgress.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    eventLoadingProgress.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            eventLoadingProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            eventListView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


}
