package com.example.jacob.eatapp;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by jacob on 2016-05-29.
 */
class RetrieveEvents extends AsyncTask<Void, String, String> {
    public AsyncInterface delegate = null;

    public RetrieveEvents(AsyncInterface asyncInterface) {
        delegate = asyncInterface;
    }

    InputStream inputStream = null;

    String userFilter = "";
    String userLongitude = "";
    String userLatitude = "";


    protected void onPreExecute() {
    }

    @Override
    protected String doInBackground(Void... params) {
        String result = "";
        // Do some validation here
        SessionUser user = SessionUser.getInstance();
        try {
            // Sätter filter för request
            userFilter = "?pk="+user.getId();
            userLongitude = "&long="+user.getLon().toString();
            userLatitude = "&lat="+user.getLat().toString();
            String baseURL = AppManager.getInstance().getBaseUrl();
            URL url = new URL(baseURL+"/events/"+userFilter+userLongitude+userLatitude);

            String basicAuth = "Bearer " + new String(user.getAccess_token());
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Authorization", basicAuth);
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                bufferedReader.close();
                result = stringBuilder.toString();

            } finally {
                urlConnection.disconnect();
            }
        }
        catch(Exception e) {
            Log.e("ERRORS", e.getMessage(), e);
        }
        return result;
    }

    protected void onPostExecute(final String result) {
        System.out.println("ONPOSTEXECUTE");
        if (result.length() > 0) {
            System.out.println("ONPOSTEXECUTE SKICKA");
            try {
                System.out.println(result);
                delegate.processFinish(result);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
