package com.example.jacob.eatapp;


import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class EventDetail extends Fragment {

    public static final String EVENT = "event";
    public EventItem event;
    String baseURL = "http://192.168.0.102:8000";
    public ParticipantsAdapter participantsAdapter;
    public User hostUser;
    private Context mContext;

    public EventDetail() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event_detail, container, false);

        // Göm floating action bar
        FloatingActionButton fab = ((MainActivity) getActivity()).getFloatingActionButton();
        fab.hide();
        mContext = getActivity().getBaseContext();
        // Get arguments for Event from ListEvent
        Bundle args = getArguments();
        event = (EventItem) args.getSerializable(EVENT);

        // Kopplar ihop textviews från xml
        TextView description = (TextView) view.findViewById(R.id.hostDescription);
        TextView numOfPeople = (TextView) view.findViewById(R.id.hostNumOfPeople);
        TextView dateStart = (TextView) view.findViewById(R.id.hostDate);
        TextView location = (TextView) view.findViewById(R.id.hostLocation);
        TextView price = (TextView) view.findViewById(R.id.event_price);

        // Formatera avstånd till event
        Double distance = event.getDistance();
        String distanceString = new String();
        if (distance < 0.5) {
            distanceString = "Very close by";
        }
        else {
            distanceString = String.format("%.2f", distance) + " km away";
        }
        location.setText(distanceString);

        // Formatera datum & tid
        try {
            // Från String till Date
            String d_start = event.getDate_Start();
            Date originalDate = null;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            originalDate = format.parse(d_start);

            // Från Date to String
            Date newDate = null;
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM HH:mm");
            newDate = new Date();
            String datetime = dateFormat.format(originalDate);

            // Sätter dateview text till nytt formaterat datum
            dateStart.setText(datetime);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        description.setText(event.getDescription());
        Integer accepted_participants = event.getParticipants().size();
        numOfPeople.setText(accepted_participants + "/" + event.getNumOfPeople().toString());
        numOfPeople.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_group, 0, 0, 0);
        price.setText("€ " + event.getPrice());

        // Textview data
        final TextView[] userName = {(TextView) view.findViewById(R.id.hostName)};
        final TextView[] userAge = {(TextView) view.findViewById(R.id.hostAge)};
        final ImageView userProfile = (ImageView) view.findViewById(R.id.hostProfilePicture);

        // Sätt värde på textview from jObject
        final UserManager userManager = UserManager.getInstance();
        Boolean foundUser = false;
        for (User u: userManager.getUsers()) {
            /* Om användaren redan finns i userManager så skicka ingen ny request
                utan använd redan lagrad data
             */
            if (u.getId() == event.getUser_pk()) {
                Integer age = u.getAge();
                hostUser = userManager.getUser(u.getId());
                foundUser = true;
                // Textview data
                userName[0] = (TextView) view.findViewById(R.id.hostName);
                userAge[0] = (TextView) view.findViewById(R.id.hostAge);
                userName[0].setText(hostUser.getFirst_name());
                userAge[0].setText(String.format("%d", hostUser.getAge()) + " years old");
                getUserProfilePic(userProfile, getView());
                break;
            }
        }
        if (!foundUser) {
            // Start AsyncTask to retrieve information about user
            GetUserInfoTask getUserInfoTask = new GetUserInfoTask(new AsyncInterface() {
                @Override
                public void processFinish(String output) throws JSONException, IOException {
                    JSONObject obj = new JSONObject(output);
                    UserManager uM = UserManager.getInstance();
                    User u = uM.parseFromStringToUser(obj);
                    uM.addUser(u);
                    hostUser = u;
                    // Textview data
                    userName[0] = (TextView) getView().findViewById(R.id.hostName);
                    userAge[0] = (TextView) getView().findViewById(R.id.hostAge);
                    userName[0].setText(hostUser.getFirst_name());
                    userAge[0].setText(String.format("%d", hostUser.getAge()) + " years old");
                    getUserProfilePic(userProfile, getView());
                }

                @Override
                public void bitMapFinished(Bitmap output) throws IOException {

                }
            },event.getUser_pk());
            getUserInfoTask.execute();

        }


        // Sätt event title som actionbar header title
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(event.getTitle());

        // Sätt elevation på bakgrundsviewn för host
        View hostLayout = view.findViewById(R.id.hostBackgroundLayout);
        if (Build.VERSION.SDK_INT > 21) {
            hostLayout.setElevation(5);
        }

        // Deltagarlistan
        ListView lv = (ListView) view.findViewById(R.id.hostParticipants);
        lv.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        // Update the adapter with Usertitems
        participantsAdapter = new ParticipantsAdapter(getActivity(), event.getParticipants());
        lv.setAdapter(participantsAdapter);

        // Hantera join event knapptryckning
        Button joinEvent = (Button) view.findViewById(R.id.joinEvent);
        joinEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParticipantJoinSync sendData = new ParticipantJoinSync(new AsyncInterface() {
                    @Override
                    public void processFinish(String output) throws JSONException {
                        User u = userManager.getUser(SessionUser.getInstance().getId());
                        if (event.getParticipants().isEmpty()) {
                            event.setParticipants(new ArrayList<User>());
                        }
                        event.getParticipants().add(u);
                    }

                    @Override
                    public void bitMapFinished(Bitmap output) throws IOException {

                    }
                }, event);
                sendData.execute();
            }
        });

        return view;
    } // end onCreateView

    public void getUserProfilePic(final ImageView userProfile, final View v) {
        // kolla om bilden redan finns annars ladda ner den i bakgrunden och sätt till imageView
        if (hostUser.getProfile_pic() != null) {
            userProfile.setImageBitmap(hostUser.getProfile_pic());
        } else {
            DownloadImageTask downloadImageTask = new DownloadImageTask(new AsyncInterface() {

                @Override
                public void processFinish(String output) {
                    // gör ingenting här när det är Bitmap som ska bearbetas
                }

                @Override
                public void bitMapFinished(Bitmap output) throws IOException {
                    hostUser.savePictureToInternalStorage(output, v.getContext());
                    hostUser.setImageFromStorage();
                    hostUser.setProfile_pic(output);
                    userProfile.setImageBitmap(output);
                }
            });
            downloadImageTask.execute(hostUser.getProfile_pic_url());
        }
    }
} // end class
