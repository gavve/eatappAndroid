package com.example.jacob.eatapp;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public Context mContext;
    public AppManager appManager = null;
    public GPSTracker gpsTracker;
    public Location loc;
    public FloatingActionButton fab;

    String SENDER_ID = "eatapp-1327"; // Google Services API-Key


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mContext = getApplicationContext();


        // Hämta aktiva användaren
        SessionUser user = SessionUser.getInstance();

        // Starta appmanager och upprätta gpsTracker
        appManager = AppManager.getInstance();
        gpsTracker = appManager.getGpsTracker(mContext);
        appManager.setLoc(gpsTracker.getLocation());
        loc = appManager.getLoc();

        // Ta hand om floatingactionbutton (skapa nytt event)
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Anropa metoden för att skapa nytt event
                Create_new_event();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /*
            Nedanför börjar hämtningen av användardata
            När användaren fått respons och datan är lagrad startar
            fragment transaction till ListEvents för att ladda eventen för
            den aktiva användaren
         */
        GetUserInfoTask getUserInfoTask = new GetUserInfoTask(new AsyncInterface() {
            @Override
            public void processFinish(String output) throws JSONException {
                // Handle result
                JSONObject obj = new JSONObject(output);

                SessionUser user = SessionUser.getInstance();

                user.setId(obj.getInt("pk"));
                user.setDate_of_birth(obj.getString("date_of_birth"));
                user.setFirst_name(obj.getString("first_name"));
                user.setProfile_pic_url(obj.getString("profile_picture"));

                // Fragment management
                FragmentManager fragmentManager = getFragmentManager();

                // Transaction starts
                android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                ListEvents elf = new ListEvents();
                fragmentTransaction.replace(R.id.mainFrameContainer, elf);
                fragmentTransaction.commit();

                // Sätt namn och profilbild på drawern
                TextView userName = (TextView) findViewById(R.id.header_userName);
                System.out.println("SESSIONUSER USERNAME" + user.getFirst_name());
                userName.setText(user.getFirst_name());
                TextView userEmail = (TextView) findViewById(R.id.header_UserEmail);
                userEmail.setText(user.getEmail());
            }

            @Override
            public void bitMapFinished(Bitmap output) throws IOException {

            }
        }, 0);
        getUserInfoTask.execute();

    }

    private void Create_new_event() {
        // Handle fragment transition and back stacking
        CreateEvent createEvent = new CreateEvent();
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction()
                .addToBackStack(null)
                .replace(R.id.mainFrameContainer, createEvent)
                .commit();
    }

    public FloatingActionButton getFloatingActionButton() {
        // Getter för att hämta från andra fragments för hide/show
        return fab;
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu;
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        /*
            Inte implementerad ännu, användaren har fast radie på 50km
            Settings skall senare vara för användaren att redigera information
         */

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.showEventsItem) {
            // Visa alla events i närheten
            appManager.showAllEvents(getFragmentManager(), false);
        } else if (id == R.id.showMyProfileItem) {
            // Visa min profil
            Bundle args = new Bundle();
            args.putInt(UserDetails.USER_PK, SessionUser.getInstance().getId());
            Log.e("pos", "is valid");
            // Handle fragment transition and back stacking
            UserDetails userDetails = new UserDetails();

            userDetails.setArguments(args);
            FragmentManager fm = getFragmentManager();
            fm.beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.mainFrameContainer, userDetails)
                    .commit();

        } else if (id == R.id.createNewEventItem) {
            // Anropa metoden för att skapa nytt event
            Create_new_event();
        } else if (id == R.id.logout) {
            signOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void signOut() {
        // Metod som anropas när användaren vill logga ut
        Intent in=new Intent(MainActivity.this, LoginActivity.class); // skickas till LoginActivity.
        AppManager.getInstance().setSaveLogin(false);
        startActivity(in);
        finish(); // Avsluta main activity så användaren måste logga in för att komma tillbaka
    }

    public void showTimePickerDialog(View v) {
        // Handle action from "set time for event" button
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }

    public void showDatePickerDialog(View v) {
        // Handle action from "set date for event" button
        DialogFragment newFragment = new DateTimePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

}
