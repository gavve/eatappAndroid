package com.example.jacob.eatapp;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by jacob on 2016-05-29.
 */
class CreateEventSync extends AsyncTask<Void, String, String> {
    public AsyncInterface delegate = null;
    public EventItem eventItem;
    public String result = ""; // return result when finished
    public CreateEventSync(AsyncInterface asyncInterface, EventItem event) {
        delegate = asyncInterface;
        eventItem = event;
    }

    String baseURL = AppManager.getInstance().getBaseUrl();

    @Override
    protected String doInBackground(Void... params) {
        System.out.println("Startar CreateeventSync background");
        SessionUser user = SessionUser.getInstance(); // hämtar inloggade användaren
        HttpURLConnection urlConnection = null;

        try {
            // 1. Sätt upp url och anslutningsegenskaper
            URL url = new URL(baseURL+"/create_event/");
            String basicAuth = "Bearer " + new String(user.getAccess_token());
            String contentType = "application/json";
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Authorization", basicAuth);
            urlConnection.addRequestProperty("Content-Type", contentType);
            urlConnection.connect();

            String json = "";
            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();

            // Definierar json objektet som ska skickas till servern
            JSONObject eventJSONObject = new JSONObject();
            JSONObject location = new JSONObject();
            location.accumulate("latitude", eventItem.getLat());
            location.accumulate("longitude", eventItem.getLon());
            eventJSONObject.accumulate("user", String.valueOf(user.getId()));
            eventJSONObject.accumulate("title", eventItem.getTitle().toString());
            eventJSONObject.accumulate("description", eventItem.getDescription().toString());
            eventJSONObject.accumulate("numOfPeople", eventItem.getNumOfPeople());
            eventJSONObject.accumulate("date_start", eventItem.getDate_Start().toString());
            eventJSONObject.accumulate("price", eventItem.getPrice());
            String lat = String.valueOf(location.getDouble("latitude"));
            String lon = String.valueOf(location.getDouble("longitude"));
            String loc = "{'latitude':"+lat+", 'longitude':"+lon+"}";
            eventJSONObject.accumulate("location", location);
            /*
            '{"numOfPeople":"10","title":"sommar","location":{"latitude":51.8096321, "longitude":5.8170026},"user":"2","description":"sommardag","date_start":"2016-5-29T15:36"}'
             */
            // 4. convert JSONObject to JSON to String
            json = eventJSONObject.toString();
            System.out.println(json);
            // 5. Börja skicka data
            DataOutputStream outputStream = new DataOutputStream(urlConnection.getOutputStream ());
            outputStream.write(json.getBytes());
            outputStream.flush();

            int status = urlConnection.getResponseCode();

            switch (status) {
                // HTTP Response status switches
                case 200:
                case 201:
                    InputStream is = urlConnection.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line+"\n");
                    }
                    br.close();
                    System.out.println("SUCCESS 201 in Onbackground");
                    result = sb.toString();
            }
        }

        catch(Exception e) {
            Log.e("ERROR", e.getMessage(), e);
        }
        System.out.println(result);
        return result;
    }

    protected void onPostExecute(final String result) {
        if (result.length() > 0) {
            System.out.println("ONPOSTEXECUTE SKAPA EVENT");
            try {
                delegate.processFinish(result);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}