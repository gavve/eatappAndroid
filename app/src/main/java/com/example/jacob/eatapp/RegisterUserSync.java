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
import java.util.Dictionary;
import java.util.HashMap;

/**
 * Created by jacob on 2016-05-29.
 */
class RegisterUserSync extends AsyncTask<Void, String, String> {
    public AsyncInterface delegate = null;
    public HashMap<String, String> userDic = null;
    public String result = ""; // return result when finished

    public RegisterUserSync(AsyncInterface asyncInterface, HashMap<String, String> userDict) {
        delegate = asyncInterface;
        userDic = userDict;
    }

    String baseURL = AppManager.getInstance().getBaseUrl();

    @Override
    protected String doInBackground(Void... params) {
        System.out.println("Startar RegisterUser background");
        HttpURLConnection urlConnection = null;

        try {
            // 1. Sätt upp url och anslutningsegenskaper
            URL url = new URL(baseURL+"/sign_up/");
            String contentType = "application/json";
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.addRequestProperty("Content-Type", contentType);
            urlConnection.connect();

            // 3. bygger jsonObject
            JSONObject jsonObject = new JSONObject();
            String json = "";

            // Definierar json objektet som ska skickas till servern
            JSONObject newUser = new JSONObject();
            newUser.accumulate("email", userDic.get("email"));
            newUser.accumulate("first_name", userDic.get("first_name"));
            newUser.accumulate("date_of_birth", userDic.get("date_of_birth"));
            newUser.accumulate("password", userDic.get("password"));

            // 4. convert JSONObject to JSON to String
            json = newUser.toString();

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
                    result = sb.toString();
            }
        }

        catch(Exception e) {
            Log.e("ERROR", e.getMessage(), e);
        }
        return result;
    }

    protected void onPostExecute(final String result) {
        if (result.length() > 0) {
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