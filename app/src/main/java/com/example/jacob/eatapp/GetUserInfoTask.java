package com.example.jacob.eatapp;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by jacob on 2016-05-29.
 */
public class GetUserInfoTask extends AsyncTask<Void, String, String> {

    public AsyncInterface delegate = null;
    public Context mContext;
    public String result = ""; // return result when finished
    public Integer user_pk;

    public GetUserInfoTask(AsyncInterface asyncInterface, Integer userPK) {
        delegate = asyncInterface;
        user_pk = userPK;
    }


    @Override
    protected String doInBackground(Void... params) {
        String result = "";
        try {
            SessionUser sessionUser = SessionUser.getInstance();
            String baseURL = AppManager.getInstance().getBaseUrl();
            URL url;
            if (user_pk == 0) {
                url = new URL(baseURL + "/u/" + sessionUser.getEmail() + "/");
            }
            else {
                url = new URL(baseURL + "/u_pk/" + user_pk + "/");
            }
            String basicAuth = "Bearer " + new String(sessionUser.getAccess_token());
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
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

            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return result;
    }

    protected void onPostExecute(final String result) {
        if (result.length() > 0) {
            System.out.println("ONPOSTEXECUTE HÄMTA ANV INFO");
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
