package com.example.jacob.eatapp;

import android.app.FragmentManager;
import android.content.Context;
import android.location.Location;

/**
 * Created by jacob on 2016-05-29.
 */
public class AppManager {

    private static AppManager mInstance = null;
    private String baseURL = "http://192.168.0.102:8000";

    public Boolean getSaveLogin() {
        return saveLogin;
    }

    public void setSaveLogin(Boolean saveLogin) {
        this.saveLogin = saveLogin;
    }

    private Boolean saveLogin = null;

    public GPSTracker getGpsTracker(Context context) {
        if (gpsTracker == null) {
            gpsTracker = new GPSTracker(context);
        }
        return gpsTracker;
    }

    public void setGpsTracker(GPSTracker gpsTracker) {
        this.gpsTracker = gpsTracker;
    }

    public Location getLoc() {
        return loc;
    }

    public void setLoc(Location loc) {
        this.loc = loc;
    }

    public GPSTracker gpsTracker;
    public Location loc;

    public static AppManager getInstance() {
        // Konstruerar Singleton
        if (mInstance == null) {
            mInstance = new AppManager();
        }
        return mInstance;
    }

    public void showAllEvents(FragmentManager fragmentManager, Boolean popBack) {
        ListEvents userEvents = new ListEvents();
        fragmentManager.beginTransaction().replace(R.id.mainFrameContainer, userEvents).commit();
        if (popBack) {
            fragmentManager.popBackStack();
        }
    }

    public String getBaseUrl() {
        return baseURL;
    }
}
